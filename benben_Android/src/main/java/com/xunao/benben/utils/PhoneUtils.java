package com.xunao.benben.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.R.anim;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.LatelyLinkeMan;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.net.InteNetUtils;

public class PhoneUtils {

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** 头像ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;

	/** 联系人的ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;

	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };

	/**
	 * 获取所有的 联系人分组信息
	 * 
	 * @return
	 */
	public static List<ContactsGroup> getAllGroupInfo(
			ContentResolver contentResolver) {

		List<ContactsGroup> groupList = new ArrayList<ContactsGroup>();

		Cursor cursor = null;

		try {
			cursor = contentResolver.query(Groups.CONTENT_URI, null, null,
					null, null);

			while (cursor.moveToNext()) {

				ContactsGroup ge = new ContactsGroup();

				int groupId = cursor.getInt(cursor.getColumnIndex(Groups._ID)); // 组id
				String groupName = cursor.getString(cursor
						.getColumnIndex(Groups.TITLE)); // 组名

				ge.setId(groupId);
				ge.setName(groupName);

				// Log.i("MainActivity", "group id:" + groupId + ">>groupName:"
				// + groupName);
				//
				// System.out.println("group id:" + groupId + ">>groupName:"
				// + groupName);

				groupList.add(ge);
				ge = null;
			}

			return groupList;

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * 获取某个分组下的 所有联系人信息 思路：通过组的id 去查询 RAW_CONTACT_ID, 通过RAW_CONTACT_ID去查询联系人
	 * 要查询得到 data表的Data.RAW_CONTACT_ID字段
	 * 
	 * @param groupId
	 * @return
	 */
	public static List<Contacts> getAllContactsByGroupId(int groupId,
			ContentResolver contentResolver) {

		String[] RAW_PROJECTION = new String[] { ContactsContract.Data.RAW_CONTACT_ID, };

		String RAW_CONTACTS_WHERE = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID
				+ "=?"
				+ " and "
				+ ContactsContract.Data.MIMETYPE
				+ "="
				+ "'"
				+ ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE
				+ "'";

		// 通过分组的id 查询得到RAW_CONTACT_ID
		Cursor cursor = contentResolver.query(
				ContactsContract.Data.CONTENT_URI, RAW_PROJECTION,
				RAW_CONTACTS_WHERE, new String[] { groupId + "" }, "data1 asc");

		List<Contacts> contactList = new ArrayList<Contacts>();

		while (cursor.moveToNext()) {
			// RAW_CONTACT_ID
			int col = cursor.getColumnIndex("raw_contact_id");
			int raw_contact_id = cursor.getInt(col);

			// Log.i("getAllContactsByGroupId", "raw_contact_id:" +
			// raw_contact_id);

			Contacts ce = new Contacts();

			// ce.setContactId(raw_contact_id);

			Uri dataUri = Uri.parse("content://com.android.contacts/data");
			Cursor dataCursor = contentResolver.query(dataUri, null,
					"raw_contact_id=?", new String[] { raw_contact_id + "" },
					null);

			while (dataCursor.moveToNext()) {

				int phoneCount = dataCursor
						.getInt(dataCursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

				String ddd = "";
				ArrayList<PhoneInfo> phones = new ArrayList<PhoneInfo>();
				PhoneInfo phone;

				if (phoneCount > 0) {

					// String contactId =
					// dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Contacts._ID));
					// 获得联系人的电话号码列表
					Cursor phoneCursor = contentResolver.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ "=" + raw_contact_id, null, null);
					if (phoneCursor.moveToFirst()) {
						do {
							// 遍历所有的联系人下面所有的电话号码
							String phoneNumber = phoneCursor
									.getString(phoneCursor
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							ddd += "||" + phoneNumber;
							phone = new PhoneInfo();
							phone.setPhone(phoneNumber);
							phones.add(phone);

						} while (phoneCursor.moveToNext());
						phoneCursor.close();
					}
				}

				String data1 = dataCursor.getString(dataCursor
						.getColumnIndex("data1"));
				String mime = dataCursor.getString(dataCursor
						.getColumnIndex("mimetype"));

				if ("vnd.android.cursor.item/phone_v2".equals(mime)) {
					// ce.setTelNumber(data1);
					// ce.setTelNumber(ddd.equals("")?data1:ddd);
					if (ddd.equals("")) {
						PhoneInfo phoneInfo = new PhoneInfo();
						phoneInfo.setPhone(data1);
						ArrayList<PhoneInfo> phs = new ArrayList<PhoneInfo>();
						phs.add(phoneInfo);
						ce.setPhones(phs);
					} else {
						ce.setPhones(phones);
					}
				} else if ("vnd.android.cursor.item/name".equals(mime)) {
					// ce.setContactName(data1);
					ce.setName(data1);
				}
				// ce.setPhones(phones);
				ce.setGroup_id(groupId + "");
			}

			dataCursor.close();
			contactList.add(ce);
			ce = null;
		}

		cursor.close();

		return contactList;
	}

	// 得到手机SIM卡联系人人信息
	public static List<Contacts> getSIMContacts(
			ContentResolver contentResolver, int groupId) {
		List<Contacts> contactList = new ArrayList<Contacts>();
		ContentResolver resolver = contentResolver;
		// 获取Sims卡联系人
		Uri uri = Uri.parse("content://icc/adn");
		// Uri uri = Uri.parse("content://sim/adn");
		// Uri uri = Uri.parse("content://contacts/phones");

		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// Sim卡中没有联系人头像
				Contacts contactEntity = new Contacts();
				contactEntity.setName(contactName);

				PhoneInfo phoneInfo = new PhoneInfo();
				phoneInfo.setPhone(phoneNumber);
				ArrayList<PhoneInfo> phoneInfos = new ArrayList<PhoneInfo>();
				phoneInfos.add(phoneInfo);

				contactEntity.setPhones(phoneInfos);
				contactEntity.setGroup_id(groupId + "");
				// contactEntity.setTelNumber(phoneNumber);
				contactList.add(contactEntity);
			}

			phoneCursor.close();
		}
		return contactList;
	}

	public static String getOnlyContacts(ContentResolver contentResolver) {
		try {
			String result = "";

			Cursor cursor = contentResolver.query(
					ContactsContract.Contacts.CONTENT_URI, null, null, null,
					null);
			if (cursor.getCount()==0) {
				return "";
			}

			while (cursor.moveToNext()) {
				int nameIndex = cursor
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				int contactIdIndex = cursor
						.getColumnIndex(ContactsContract.Contacts._ID);
				String contactId = cursor.getString(contactIdIndex);
				String name = cursor.getString(nameIndex);
				if (!TextUtils.isEmpty(name)) {
					/*
					 * 查找该联系人的phone信息
					 */
					Cursor phones = contentResolver.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ "=" + contactId, null, null);
					name = PhoneUtils.filterEmoji(name.replaceAll("\\+86", ""));
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
									phoneNum = phoneNumber + "#" + phoneNum;
								}
								phoneNum = phoneNum.replaceAll("-", "");
							}
						}
						if (!CommonUtils.isEmpty(phoneNum)) {
							result += phoneNum + "::" + name + "|";
						}
					}
					phones.close();
				}

			}

			if (result.length() > 1) {
				result = result.substring(0, result.length() - 1);
			}
			result = result.replaceAll(" ", "");
			cursor.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 发送短信
	 *
	 */
	public static void sendSMS(String mobile, String content, Context context) {
		// Uri smsToUri = Uri.parse("smsto:10000");
		// Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		// context.startActivity(intent);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.putExtra("address", mobile);
		intent.putExtra("sms_body", content);
		intent.setType("vnd.android-dir/mms-sms");
		context.startActivity(intent);

	}

	/**
	 * 群发
	 * 
	 * @param mobile
	 * @param context
	 */
	public static void sendSMSAll(String mobile, String content, Context context) {
		// Uri smsToUri = Uri.parse("smsto:10000");
		// Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		// context.startActivity(intent);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setType("vnd.android-dir/mms-sms");
		intent.setData(Uri.parse("smsto:" + mobile));
		intent.putExtra("sms_body", content);
		intent.putExtra("address", mobile);
		context.startActivity(intent);

		InteNetUtils.getInstance(context).sendMsg(mobile,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						XunaoLog.yLog().i(arg1);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						XunaoLog.yLog().i(arg0.result);
					}
				});

	}

	/**
	 * 拨打电话
	 */
	public static void makeCall(int cid,String name, String phoneNum, Context context) {

		// 调用系统的拨号服务实现电话拨打功能

		phoneNum = phoneNum.trim();// 删除字符串首部和尾部的空格

		if (phoneNum != null && !phoneNum.equals("")) {
			// 调用系统的拨号服务实现电话拨打功能
			// 封装一个拨打电话的intent，并且将电话号码包装成一个Uri对象传入
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ phoneNum));
			context.startActivity(intent);// 内部类

			InteNetUtils.getInstance(context).callPhone(phoneNum,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							XunaoLog.yLog().i(arg1);
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							XunaoLog.yLog().i(arg0.result);
						}
					});

			LatelyLinkeMan linkeMan = new LatelyLinkeMan(cid,name, phoneNum,
					System.currentTimeMillis());

			try {
				CrashApplication.getInstance().getDb().saveOrUpdate(linkeMan);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	
	private static boolean isNotEmojiCharacter(char codePoint)  
	{  
	    return (codePoint == 0x0) ||  
	        (codePoint == 0x9) ||  
	        (codePoint == 0xA) ||  
	        (codePoint == 0xD) ||  
	        ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||  
	        ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||  
	        ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));  
	}  
	
	/** 
	 * 过滤emoji 或者 其他非文字类型的字符 
	 * @param source 
	 * @return 
	 */  
	public static String filterEmoji(String source)  
	{  
	    int len = source.length();  
	    StringBuilder buf = new StringBuilder(len);  
	    for (int i = 0; i < len; i++)  
	    {  
	        char codePoint = source.charAt(i);  
	        if (isNotEmojiCharacter(codePoint))  
	        {  
	            buf.append(codePoint);  
	        }  
	    }  
	    return buf.toString();  
	} 
}
