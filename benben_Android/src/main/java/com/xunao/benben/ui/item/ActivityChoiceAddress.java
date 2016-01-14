package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter.LengthFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.AddressInfo;
import com.xunao.benben.bean.AddressInfoList;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

public class ActivityChoiceAddress extends BaseActivity implements
		OnClickListener {

	private ListView listview;

	private ArrayList<AddressInfo> addressInfos;
	private AddressInfoList addressInfoList = new AddressInfoList();

	private MyAdapter myAdatper;

	private String[] leve = { "选择省份", "选择城市", "选择城区或城镇", "选择街道 " };
	private String addressname = "";
	private String[] addressId = new String[4];
	private int index = 0;
	private boolean choseRange = false;
	private int positions = -1;

	private boolean trims = false;
	private boolean level = false;
	private boolean isRandomBack = false;

	private LinkedList<AddressInfo> addressInfos2 = new LinkedList<AddressInfo>();

	private int i;
    private String from="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_choice_address);

		initTitle_Right_Left_bar(leve[0], "", "",
				R.drawable.icon_com_title_left, 0);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		listview = (ListView) findViewById(R.id.listview);

		Intent intent = getIntent();
		String info = intent.getStringExtra("range");
		int pos = intent.getIntExtra("position", -1);
		String trim = intent.getStringExtra("trim");
		if (info != null) {
			if (info.equals("range")) {
				choseRange = true;
			}
		}

		if (pos != -1) {
			positions = pos;
		}

		if (trim != null) {
			trims = true;
		}

		if (intent.getStringExtra("level") != null) {
			if (intent.getStringExtra("level").equals("3")) {
				level = true;
			} else if (intent.getStringExtra("level").equals("0")) {
				initTitle_Right_Left_bar(leve[0], "", "完成",
						R.drawable.icon_com_title_left, 0);
				isRandomBack = true;
			} else {
				initTitle_Right_Left_bar(leve[0], "", "完成",
						R.drawable.icon_com_title_left, 0);
				isRandomBack = true;
				level = true;
			}

		}

        if(intent.hasExtra("from")){
            from = intent.getStringExtra("from");
        }
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		// 第一次传空
		if (CommonUtils.isNetworkAvailable(mContext))
			InteNetUtils.getInstance(mContext).getAddress("", mRequestCallBack);

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setOnLeftClickLinester(this);
		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (isRandomBack) {
					Intent intent = new Intent();
					intent.putExtra("address", addressname);
					intent.putExtra("addressId", addressId);

					setResult(AndroidConfig.ChoiceAddressResultCode, intent);
					AnimFinsh();
				}
			}
		});
	}

	@Override
	protected void onHttpStart() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		try {
			addressInfoList.parseJSON(jsonObject);
			addressInfos = addressInfoList.getAddressInfos();

			if (addressInfos != null && addressInfos.size() > 0) {
				i = Integer.valueOf(addressInfos.get(0).getLevel()) - 1;
				((TextView) findViewById(R.id.com_title_bar_content))
						.setText(leve[i]);
			} else {
				Intent intent = new Intent();
				intent.putExtra("address", addressname);
				intent.putExtra("addressId", addressId);

				if (positions != -1) {
					intent.putExtra("position", positions);
				}

				setResult(AndroidConfig.ChoiceAddressResultCode, intent);
				AnimFinsh();
			}

			myAdatper = new MyAdapter();
			listview.setAdapter(myAdatper);

		} catch (NetRequestException e) {
			e.getError().print(mContext);
			Intent intent = new Intent();
			intent.putExtra("address", addressname);
			intent.putExtra("addressId", addressId);

			if (positions != -1) {
				intent.putExtra("position", positions);
			}

			setResult(AndroidConfig.ChoiceAddressResultCode, intent);
			AnimFinsh();
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用!");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.com_title_bar_left_bt:
		case R.id.com_title_bar_left_tv:
			if (addressInfos2.size() > 0) {
				if (index > 0) {
					index--;
				}
				addressId[index] = "";
				addressname = addressname
						.substring(0, addressname.length() - 1);
				int lastIndexOf = addressname.lastIndexOf(' ');
				if (lastIndexOf != -1) {
					addressname = addressname.substring(0, lastIndexOf);
				} else {
					addressname = "";
				}

				InteNetUtils.getInstance(mContext).getAddress(
						addressInfos2.removeLast().getParent_bid(),
						mRequestCallBack);
			} else {
				AnimFinsh();
			}
			break;

		default:
			break;
		}
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return addressInfos == null ? 0 : addressInfos.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup arg2) {

			final AddressInfo addressinfo = addressInfos.get(position);

			if (converView == null) {
				converView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_choice_address_item, null);
			}

			((TextView) converView.findViewById(R.id.address_name))
					.setText(addressinfo.getArea_name());

			converView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					addressInfos2.add(addressinfo);
                    if(from.equals("train") && i==2){
                        addressname += addressinfo.getArea_name() + " ";
                        addressId[index] = addressinfo.getBid();
                        index++;
                        Intent intent = new Intent();
                        intent.putExtra("address", addressname);
                        intent.putExtra("addressId", addressId);
                        setResult(AndroidConfig.ChoiceAddressResultCode,
                                intent);
                        AnimFinsh();
                    }else {

                        if (choseRange) {
                            addressname = addressinfo.getArea_name();
                            addressId[index] = addressinfo.getBid();
                            index++;
                            if (Integer.valueOf(addressinfo.getLevel()) >= 3) {
                                Intent intent = new Intent();
                                intent.putExtra("address", addressname);
                                intent.putExtra("addressId", addressId);
                                setResult(AndroidConfig.ChoiceAddressResultCode,
                                        intent);
                                AnimFinsh();
                            } else {
                                InteNetUtils.getInstance(mContext).getAddress(
                                        addressinfo.getBid(), mRequestCallBack);
                            }
                        } else if (level) {
                            addressname += addressinfo.getArea_name() + " ";
                            addressId[index] = addressinfo.getBid();
                            index++;
                            if (Integer.valueOf(addressinfo.getLevel()) >= 3) {
                                Intent intent = new Intent();
                                intent.putExtra("address", addressname);
                                intent.putExtra("addressId", addressId);
                                if (positions != -1) {
                                    intent.putExtra("position", positions);
                                }
                                setResult(AndroidConfig.ChoiceAddressResultCode,
                                        intent);
                                AnimFinsh();
                            } else {
                                InteNetUtils.getInstance(mContext).getAddress(
                                        addressinfo.getBid(), mRequestCallBack);
                            }
                        } else {
                            if (trims) {
                                addressname += addressinfo.getArea_name() + " ";
                            } else {
                                addressname += addressinfo.getArea_name() + " ";
                            }
                            addressId[index] = addressinfo.getBid();
                            index++;
                            if (Integer.valueOf(addressinfo.getLevel()) >= 4) {
                                Intent intent = new Intent();
                                intent.putExtra("address", addressname);
                                intent.putExtra("addressId", addressId);

							if (positions != -1) {
								intent.putExtra("position", positions);
							}

                                setResult(AndroidConfig.ChoiceAddressResultCode,
                                        intent);
                                AnimFinsh();
                            } else {
                                InteNetUtils.getInstance(mContext).getAddress(
                                        addressinfo.getBid(), mRequestCallBack);
                            }
                        }
                    }
				}
			});

			return converView;
		}

	}

}
