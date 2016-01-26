package com.xunao.benben.ui.item;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.LodingDialog;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.LocalContact;
import com.xunao.benben.utils.LocalPhone;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.RegexUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltf on 2015/11/15.
 */
public class ActivityContactsUpdate extends BaseActivity implements View.OnClickListener {
    private List<Contacts> myContacts;
    private List<Contacts> newContactList;
    private List<LocalContact> localContactList = new ArrayList<>();
    private ListView ll_match;
    private myAdapter myAdapter;
    private TextView tv_add;
    private TextView tv_match;
    private LinearLayout no_data;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contacts_update);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("联系人合并", "", "", R.drawable.icon_com_title_left,
                0);
        ll_match = (ListView) findViewById(R.id.ll_match);
        myAdapter = new myAdapter();
        ll_match.setAdapter(myAdapter);
        tv_match = (TextView) findViewById(R.id.tv_match);
        tv_match.setOnClickListener(this);
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_add.setOnClickListener(this);
        no_data = (LinearLayout) findViewById(R.id.no_data);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        lodingDialog = new LodingDialog(mContext);
        lodingDialog.setContent("正在匹配...");
        lodingDialog.show();
        new Thread() {
            public void run() {
                localContactList = new ArrayList<>();
                newContactList = new ArrayList<>();
                try {
                    myContacts = dbUtil.findAll(Selector
                            .from(Contacts.class)
                            .where("group_id", "!=", 10000).orderBy("pinyin", false));
                    if(myContacts!=null){
                        for(int i=0;i<myContacts.size();i++) {
                            Contacts contacts = myContacts.get(i);
                            List<PhoneInfo> phoneInfos = dbUtil.findAll(Selector
                                    .from(PhoneInfo.class)
                                    .where("contacts_id", "=", contacts.getId()));
                            List<PhoneInfo> newPhoneInfos = dbUtil.findAll(Selector
                                    .from(PhoneInfo.class)
                                    .where("contacts_id", "=", contacts.getId()));
                            if (phoneInfos != null) {
                                for(int j=0;j<phoneInfos.size();j++){
                                    if(phoneInfos.get(j).getPhone().equals("")){
                                        newPhoneInfos.remove(phoneInfos.get(j));
                                    }
                                }
                                if(newPhoneInfos.size()>0) {
                                    //去重合并name相同的联系人信息
                                    if (newContactList.size() == 0) {
                                        contacts.setPhones((java.util.ArrayList<PhoneInfo>) newPhoneInfos);
                                        newContactList.add(contacts);
                                    } else {
                                        Contacts newContacts = newContactList.get(newContactList.size() - 1);
                                        if (newContacts.getName().equals(contacts.getName())) {
                                            newContacts.getPhones().addAll((java.util.ArrayList<PhoneInfo>) newPhoneInfos);
                                        } else {
                                            contacts.setPhones((java.util.ArrayList<PhoneInfo>) newPhoneInfos);
                                            newContactList.add(contacts);
                                        }
                                    }
                                }
                            }
                        }
                        for(int i=0;i<newContactList.size();i++) {
                            Contacts contacts = newContactList.get(i);
                            ContentResolver cr = getContentResolver();
                            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                                    ContactsContract.Contacts.DISPLAY_NAME + " = '"+ contacts.getName() + "'", null, null);
                            if(cursor==null || cursor.getCount()==0){
                                addContact(contacts.getName(), contacts.getPhones());
                            }else{
                                List<LocalPhone> localPhones = new ArrayList<>();
                                List<String> phoneList = new ArrayList<>();

                                while (cursor.moveToNext()) {
//                            int nameIndex = cursor
//                                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                                    int contactIdIndex = cursor
                                            .getColumnIndex(ContactsContract.Contacts._ID);
                                    String contactId = cursor.getString(contactIdIndex);

                                    String rawContactsId = "";
                                    // 读取rawContactsId
                                    Cursor rawContactsIdCur = cr.query(ContactsContract.RawContacts.CONTENT_URI,
                                            null,
                                            ContactsContract.RawContacts.CONTACT_ID +" = ?",
                                            new String[]{contactId}, null);
                                    // 该查询结果一般只返回一条记录，所以我们直接让游标指向第一条记录
                                    if (rawContactsIdCur.moveToFirst())
                                    {
                                        // 读取第一条记录的RawContacts._ID列的值
                                        rawContactsId =
                                                rawContactsIdCur.getString(rawContactsIdCur.getColumnIndex(
                                                        ContactsContract.RawContacts._ID));
                                    }
                                    rawContactsIdCur.close();

//                            String name = cursor.getString(nameIndex);
                            /*
                            * 查找该联系人的phone信息
                            */
                                    Cursor phones = cr.query(
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                            null,
                                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                    + "=" + contactId, null,  null);
                                    int phoneIndex = 0;
                                    if (phones.getCount() > 0) {
                                        phoneIndex = phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                                        String phoneNum = "";
                                        while (phones.moveToNext()) {
                                            String phoneNumber = phones.getString(phoneIndex);

                                            phoneNumber = phoneNumber.replaceAll("\\+86", "")
                                                    .replaceAll(" ", "").replaceAll("-", "");
                                            if (RegexUtils.checkDigit(phoneNumber)) {
                                                if (!CommonUtils.isEmpty(phoneNumber)) {
                                                    phoneList.add(phoneNumber);
                                                    phoneNum = phoneNumber + "#" + phoneNum;
                                                }
                                                phoneNum = phoneNum.replaceAll("-", "");
                                            }
                                        }
                                        if (phoneNum.length() > 1) {
                                            phoneNum = phoneNum.substring(0, phoneNum.length() - 1);
                                            phoneNum = phoneNum.replaceAll(" ", "");
                                        }

                                        LocalPhone localPhone = new LocalPhone();
                                        localPhone.setPid(rawContactsId);
                                        localPhone.setPhone(phoneNum);
                                        localPhone.setType("本地");
                                        localPhones.add(localPhone);
                                    }
                                    phones.close();
                                }
                                boolean addMatch = false;
                                String benbenPhones="";
                                for (int j=0;j<contacts.getPhones().size();j++){
                                    String phone = contacts.getPhones().get(j).getPhone();
                                    benbenPhones += phone+"#";
                                    if(!phoneList.contains(phone)){
                                        addMatch=true;
                                    }
                                }
//                                if(addMatch){
//                                    LocalContact localContact=new LocalContact();
//                                    localContact.setName(contacts.getName());
//                                    localContact.setLocalPhones(localPhones);
//                                    localContactList.add(localContact);
//                                }
//                                boolean hasPhone = false;
//
//
//
//                                for (int j=0;j<contacts.getPhones().size();j++){
//                                    String phone = contacts.getPhones().get(j).getPhone();
//                                    benbenPhones += phone+"#";
//                                }
//                                if(benbenPhones.length()>1) {
//                                    benbenPhones = benbenPhones.substring(0, benbenPhones.length() - 1);
//                                }
//                                for(int j=0;j<localPhones.size();j++){
//                                    if(benbenPhones.equals(localPhones.get(j).getPhone())){
//                                        hasPhone=true;
//                                        break;
//                                    }
//                                }
                                if(addMatch){
                                    if(benbenPhones.length()>1) {
                                        benbenPhones = benbenPhones.substring(0, benbenPhones.length() - 1);
                                    }
                                    LocalPhone localPhone = new LocalPhone();
                                    localPhone.setPhone(benbenPhones);
                                    localPhone.setType("奔犇");
                                    localPhones.add(localPhone);
                                    LocalContact localContact=new LocalContact();
                                    localContact.setName(contacts.getName());
                                    localContact.setLocalPhones(localPhones);
                                    localContactList.add(localContact);
                                }

                            }
                            if(cursor!=null){
                                cursor.close();
                            }
                        }
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }


                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lodingDialog.dismiss();
                        if(localContactList!=null && localContactList.size()>0){
                            ll_match.setVisibility(View.VISIBLE);
                            no_data.setVisibility(View.GONE);
                        }else{
                            ll_match.setVisibility(View.GONE);
                            no_data.setVisibility(View.VISIBLE);
                        }
                        myAdapter.notifyDataSetChanged();
                    }
                });
            };
        }.start();


    }

    /**
     * 单条联系人添加通讯录
     *
     * @throws OperationApplicationException
     * @throws RemoteException
     */
    public void addContact(String name, List<PhoneInfo> phoneInfo) {

        ContentValues values = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(
                ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        // 向data表插入数据
        if (name != "") {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
                    values);
        }
        // 向data表插入电话号码
        for(int i=0;i<phoneInfo.size();i++){
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneInfo.get(i).getPhone());
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
                    values);
        }
    }

    /**
     * 批量添加通讯录
     *
     * @throws OperationApplicationException
     * @throws RemoteException
     */
    public  void BatchAddContact(List<Contacts> list)
            throws RemoteException, OperationApplicationException {

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = 0;
        for (Contacts contact : list) {
            rawContactInsertIndex = ops.size(); // 有了它才能给真正的实现批量添加

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .withYieldAllowed(true).build());

            // 添加姓名
            ops.add(ContentProviderOperation
                    .newInsert(
                            android.provider.ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName())
                    .withYieldAllowed(true).build());
            // 添加号码


            ops.add(ContentProviderOperation
                    .newInsert(
                            android.provider.ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getName())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "").withYieldAllowed(true).build());
        }
        if (ops != null) {
            // 真正添加
            ContentProviderResult[] results = getContentResolver()
                    .applyBatch(ContactsContract.AUTHORITY, ops);

        }
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimFinsh();
            }
        });
    }

    @Override
    protected void onHttpStart() {

    }

    @Override
    protected void onLoading(long count, long current, boolean isUploading) {

    }

    @Override
    protected void onSuccess(JSONObject jsonObject) {

    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_add:
                if(localContactList!=null && localContactList.size()>0){
                    showLoding("");
                    for(int i=0;i<localContactList.size();i++){
                        addNewContact(localContactList.get(i).getName(), localContactList.get(i).getLocalPhones());
//                        localContactList.remove(i);
                    }
                    localContactList.clear();
                    ll_match.setVisibility(View.GONE);
                    no_data.setVisibility(View.VISIBLE);
                    myAdapter.notifyDataSetChanged();
                    dissLoding();
                }else{
                    ToastUtils.Infotoast(mContext,"当前无联系人更新");
                }

                break;
            case R.id.tv_match:
                if(localContactList!=null && localContactList.size()>0){
                    showLoding("");
                    for(int i=0;i<localContactList.size();i++){
                        updateNewContact(localContactList.get(i).getName(), localContactList.get(i).getLocalPhones());
                    }
                    localContactList.clear();
                    ll_match.setVisibility(View.GONE);
                    no_data.setVisibility(View.VISIBLE);
                    myAdapter.notifyDataSetChanged();
                    dissLoding();
                }else{
                    ToastUtils.Infotoast(mContext,"当前无联系人更新");
                }
                break;
        }
    }

    class myAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return localContactList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return localContactList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder localViewHolder = new ViewHolder();
//            if (convertView == null) {
                localViewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_contact_match, null);
                localViewHolder.ll_phone = (LinearLayout) convertView.findViewById(R.id.ll_phone);
                localViewHolder.tv_add = (TextView) convertView.findViewById(R.id.tv_add);
                localViewHolder.tv_match = (TextView) convertView.findViewById(R.id.tv_match);
                convertView.setTag(localViewHolder);
//            } else {
//                localViewHolder = (ViewHolder) convertView.getTag();
//            }

            final List<LocalPhone> phones = localContactList.get(position).getLocalPhones();

            for(int i=0;i<phones.size();i++){
                if(localViewHolder.ll_phone.getChildCount()<phones.size()) {
                    View phoneView = getLayoutInflater().inflate(
                            R.layout.item_contact_match_phone, null);
                    localViewHolder.ll_phone.addView(phoneView);
                    TextView tv_name = (TextView) phoneView.findViewById(R.id.tv_name);
                    TextView tv_phone = (TextView) phoneView.findViewById(R.id.tv_phone);
                    tv_name.setText(localContactList.get(position).getName()+"("+phones.get(i).getType()+")");
                    String[] phone = phones.get(i).getPhone().split("#");
                    String showPhone="";
                    for(int j=0;j<phone.length;j++){
                        showPhone += phone[j]+"\n";
                    }
                    showPhone = showPhone.substring(0,showPhone.length()-1);
                    tv_phone.setText(showPhone);
                }

            }
//
//            TextView friend_union_number = ViewHolderUtil.get(convertView,
//                    R.id.tv_friend_union_number);
//
//            TextView friend_union_description = ViewHolderUtil.get(convertView,
//                    R.id.tv_friend_union_description);
//
//            TextView tv_union_type = ViewHolderUtil.get(convertView,
//                    R.id.tv_union_type);
//
//            RoundedImageView friend_union_poster = ViewHolderUtil.get(
//                    convertView, R.id.friend_union_poster);
            localViewHolder.tv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLoding("");
                    addNewContact(localContactList.get(position).getName(), phones);
                    localContactList.remove(position);
                    if(localContactList!=null && localContactList.size()>0){
                        ll_match.setVisibility(View.VISIBLE);
                        no_data.setVisibility(View.GONE);
                    }else{
                        ll_match.setVisibility(View.GONE);
                        no_data.setVisibility(View.VISIBLE);
                    }
                    myAdapter.notifyDataSetChanged();
                    dissLoding();
                }
            });

            localViewHolder.tv_match.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLoding("");
                    updateNewContact(localContactList.get(position).getName(), phones);
                    localContactList.remove(position);
                    if(localContactList!=null && localContactList.size()>0){
                        ll_match.setVisibility(View.VISIBLE);
                        no_data.setVisibility(View.GONE);
                    }else{
                        ll_match.setVisibility(View.GONE);
                        no_data.setVisibility(View.VISIBLE);
                    }
                    myAdapter.notifyDataSetChanged();
                    dissLoding();
                }
            });


            return convertView;
        }

        class ViewHolder {
            private LinearLayout ll_phone;
            private TextView tv_add;
            private TextView tv_match;
        }
    }

    /**
     * 单条联系人添加通讯录
     *
     * @throws OperationApplicationException
     * @throws RemoteException
     */
    public void addNewContact(String name, List<LocalPhone> phoneInfo) {
        ContentValues values = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(
                ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        // 向data表插入数据
        if (name != "") {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
                    values);
        }
        // 向data表插入电话号码
        for(int i=0;i<phoneInfo.size();i++){
            if(phoneInfo.get(i).getPid()==null || phoneInfo.get(i).getPid().equals("")) {
                String[] phones = phoneInfo.get(i).getPhone().split("#");
                for(int j=0;j<phones.length;j++) {
                    values.clear();
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phones[j]);
                    values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                    getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
                            values);
                }
            }
        }

    }

    /**
     * 修改通讯录
     *
     * @throws OperationApplicationException
     * @throws RemoteException
     */
    public void updateNewContact(String name, List<LocalPhone> phoneInfo) {
        String rawContactId="";
        String localphones = "";
        for(int i=0;i<phoneInfo.size();i++){
            if(phoneInfo.get(i).getPid()!=null && !phoneInfo.get(i).getPid().equals("")) {
                rawContactId = phoneInfo.get(i).getPid();
                localphones += phoneInfo.get(i).getPhone();
                break;
            }
        }
        ContentValues values = new ContentValues();

        // 向data表插入电话号码
        for(int i=0;i<phoneInfo.size();i++){
            if(phoneInfo.get(i).getPid()==null || phoneInfo.get(i).getPid().equals("")) {
                String[] phones = phoneInfo.get(i).getPhone().split("#");
                for(int j=0;j<phones.length;j++){
                    if(!localphones.contains(phones[j])){
                        values.clear();
                        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phones[j]);
                        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                        getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
                                values);
                    }
                }


            }
        }

    }
}
