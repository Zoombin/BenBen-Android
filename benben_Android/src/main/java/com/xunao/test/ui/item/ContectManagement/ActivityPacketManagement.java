package com.xunao.test.ui.item.ContectManagement;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.Contacts;
import com.xunao.test.bean.ContactsGroup;
import com.xunao.test.bean.ContactsObject;
import com.xunao.test.bean.SuccessMsg;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.dialog.InputDialog;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.ToastUtils;
import com.xunao.test.view.DragListView;

public class ActivityPacketManagement extends BaseActivity implements
		OnClickListener {

	private DragListView listview;
	private ArrayList<ContactsGroup> mContactsGroups;
	private MyAdapter myAdapter;

	private String pecketName;

	private InputDialog inputDialog;
	private int curPosition;
	private String pecketId;
    private int start=0;
    private int end=0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
				mContext) {
			@Override
			public void onLoading(ImageTask imageTask,
					CubeImageView cubeImageView) {
				Boolean ispost = (Boolean) cubeImageView
						.getTag(R.string.ispost);
				if (cubeImageView != null) {
					if (ispost != null && ispost) {
						cubeImageView.setImageResource(R.drawable.default_face);
					} else {
						cubeImageView.setImageResource(R.drawable.loading);
					}
				}

			}

			@Override
			public void onLoadFinish(ImageTask imageTask,
					CubeImageView cubeImageView, BitmapDrawable drawable) {
				if (cubeImageView != null) {
					if (imageTask.getIdentityUrl().equalsIgnoreCase(
							(String) cubeImageView.getTag())) {

						cubeImageView.setVisibility(View.VISIBLE);
						cubeImageView.setImageDrawable(drawable);

					}

				}
			}

			@Override
			public void onLoadError(ImageTask imageTask,
					CubeImageView imageView, int errorCode) {
				if (imageView != null) {
					Boolean ispost = (Boolean) imageView
							.getTag(R.string.ispost);
					if (ispost != null && ispost) {
						imageView.setImageResource(R.drawable.default_face);
					} else {
						imageView.setImageResource(R.drawable.error);
					}
				}
			}
		});
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_packet_management);

		initTitle_Right_Left_bar("分组管理", "", "",
				R.drawable.icon_com_title_left, R.drawable.icon_com_title_add);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		listview = (DragListView) findViewById(R.id.listview);
        listview.setDropListener(mDropListener);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
	}

	protected void refresh() {
		try {
//			mContactsGroups = (ArrayList<ContactsGroup>) dbUtil
//					.findAll(ContactsGroup.class);
            mContactsGroups = new ArrayList<>();
            List<ContactsGroup> list =  dbUtil
                    .findAll(Selector.from(ContactsGroup.class).orderBy("sort"));
            for(int i=0;i<list.size();i++){
                mContactsGroups.add(list.get(i));
            }

			ContactsGroup temp = null;
			ArrayList<Contacts> getmContacts = null;
			for (ContactsGroup cg : mContactsGroups) {

				if (cg.getName().equals("未分组")) {
					temp = cg;
				}
				getmContacts = cg.getmContacts();
				getmContacts.clear();
				List<Contacts> findAll = dbUtil.findAll(Selector
						.from(Contacts.class)
						.where("group_id", "=", cg.getId())
						.orderBy("pinyin", false));
				if (findAll != null) {
					getmContacts.addAll(findAll);
				}
			}
			mContactsGroups.remove(temp);
			mContactsGroups.add(temp);

			mApplication.contactsObjectManagement = new ContactsObject();
			mApplication.contactsObjectManagement
					.setmContactsGroups(mContactsGroups);
			List<Contacts> findAll = dbUtil.findAll(Selector
					.from(Contacts.class).where("group_id", "!=", "10000")
					.orderBy("pinyin", false));
			mApplication.contactsObjectManagement
					.setmContactss((ArrayList<Contacts>) findAll);
			ArrayList<ContactsGroup> getmContactsGroups = mApplication.contactsObjectManagement
					.getmContactsGroups();
			mContactsGroups = new ArrayList<ContactsGroup>();
			for (int i = 0; i < getmContactsGroups.size(); i++) {
				ContactsGroup contactsGroup = getmContactsGroups.get(i);
				if (contactsGroup.getId() != 10000) {
					mContactsGroups.add(contactsGroup);
				}
			}
			if (mContactsGroups != null && mContactsGroups.size() > 0) {
				myAdapter = new MyAdapter();
				listview.setAdapter(myAdapter);
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);

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
			SuccessMsg msg = new SuccessMsg();
			msg.parseJSON(jsonObject);

			String group_id = jsonObject.optString("group_id");

			ContactsGroup contactsGroup = new ContactsGroup();
			contactsGroup.setId(Integer.parseInt(group_id));
			contactsGroup.setName(pecketName);
			contactsGroup.setProportion("0/0");
            contactsGroup.setSort(mContactsGroups.size());

			mContactsGroups.add(contactsGroup);
			mApplication.contactsObject.getmContactsGroups().add(contactsGroup);
			mApplication.mContactsGroupMap.put(contactsGroup.getId() + "",
					contactsGroup);
			groupOrderBy();
			myAdapter.notifyDataSetChanged();
			// 保存到本地
			dbUtil.save(contactsGroup);
			sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
			inputDialog.dismiss();

		} catch (NetRequestException e) {
			e.getError().print(mContext);
			e.printStackTrace();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void groupOrderBy() {
		int size = mContactsGroups.size();
		// 对组群排序,未分组放在后面
		ContactsGroup unGroup = null;
		for (int i = 0; i < size; i++) {
			if (mContactsGroups.get(i).getName().equalsIgnoreCase("未分组")) {
				unGroup = mContactsGroups.remove(i);
				break;
			}
		}
		if (unGroup != null) {
			mContactsGroups.add(unGroup);
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub
		ToastUtils.Errortoast(mContext, strMsg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 头部左侧点击
		case R.id.com_title_bar_left_bt:
		case R.id.com_title_bar_left_tv:
			onBackPressed();
			break;

		// 头部右侧点击
		case R.id.com_title_bar_right_bt:
		case R.id.com_title_bar_right_tv:
			// 添加分组
			inputDialog = new InputDialog(mContext, R.style.MyDialogStyle);
			inputDialog.setContent("添加分组", "请输入新的分组名", "确认", "取消");
			inputDialog.setCancleListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					inputDialog.dismiss();
				}
			});
			inputDialog.setOKListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					pecketName = inputDialog.getInputText();
					
					if(!CommonUtils.StringIsSurpass2(pecketName, 2, 8)){
						ToastUtils.Errortoast(mContext, "名称限制在1-8个字之间");
						return;
					}
//					if(pecketName.length() > 8){
//					}

					if ("未分组".equals(pecketName)) {
						ToastUtils.Infotoast(mContext, "分组名已存在");
					} else {
						inputDialog.dismiss();
						if (CommonUtils.isNetworkAvailable(mContext))
							InteNetUtils.getInstance(mContext).AddPacket(
									pecketName, mRequestCallBack);
					}

				}
			});
			inputDialog.show();
			break;

		default:
			break;
		}

	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// // TODO Auto-generated method stub
	// super.onActivityResult(requestCode, resultCode, data);
	// if (resultCode == AndroidConfig.PacketManagementResultCode) {
	// ArrayList<ContactsGroup> getmContactsGroups = mApplication.contactsObject
	// .getmContactsGroups();
	// mContactsGroups = new ArrayList<ContactsGroup>();
	// for (int i = 0; i < getmContactsGroups.size(); i++) {
	// ContactsGroup contactsGroup = getmContactsGroups.get(i);
	// if (!contactsGroup.getId().equals("10000")) {
	// mContactsGroups.add(contactsGroup);
	// }
	// }
	// myAdapter.notifyDataSetChanged();
	// }
	// }

	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}


    //交换listview的数据
    private DragListView.DropListener mDropListener =
            new DragListView.DropListener() {
                public void drop(int from, int to) {
                    if(to==mContactsGroups.size()-1){
                        listview.setLock(true);
                    }else {
                        ContactsGroup item = mContactsGroups.get(from);
                        mContactsGroups.remove(item);//.remove(from);
                        mContactsGroups.add(to, item);
                        myAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void start(int from) {
                    start = from;
                    if(start==mContactsGroups.size()-1){
                        listview.setLock(true);
                    }
                }

                @Override
                public void stop(int to) {
                    listview.setLock(false);
                    if(start!=mContactsGroups.size()-1) {

                        if (to == mContactsGroups.size() - 1) {
                            to--;
                        }
                        end = to;
                        String sort = "";
                        if (start != end) {
                            if (start > end) {
                                int pos = start;
                                start = end;
                                end = pos;
                            }
                            for (int i = start; i <= end; i++) {
                                sort += mContactsGroups.get(i).getId() + ";" + (i + 1) + "|";
                            }
                            if (sort.length() > 1) {
                                sort = sort.substring(0, sort.length() - 1);
                            }
                            ActivityPacketManagement.this.showLoding("加载中...");
                            InteNetUtils.getInstance(mContext).sortGroup(
                                    user.getToken(), sort, new RequestCallBack<String>() {

                                        @Override
                                        public void onFailure(HttpException arg0, String arg1) {
                                            ActivityPacketManagement.this.dissLoding();
                                            ToastUtils.Errortoast(mContext, "操作失败！");
                                            refresh();
                                        }

                                        @Override
                                        public void onSuccess(ResponseInfo<String> arg0) {
                                            ActivityPacketManagement.this.dissLoding();
                                            for (int i = start; i <= end; i++) {
                                                mContactsGroups.get(i).setSort(i + 1);
                                                try {
                                                    dbUtil.saveOrUpdate(mContactsGroups.get(i));
                                                } catch (DbException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
                                            myAdapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                }
            };

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mContactsGroups.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mContactsGroups.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			final ContactsGroup contactsGroup = mContactsGroups.get(position);
			final itemHolder itHolder;
			if (convertView == null) {
				itHolder = new itemHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_activirty_packet_management, null);
				itHolder.item_layout = (LinearLayout) convertView
						.findViewById(R.id.item_layout);
				itHolder.changeNameBut = (ImageView) convertView
						.findViewById(R.id.changeNameBut);
				itHolder.item_delete = (LinearLayout) convertView
						.findViewById(R.id.item_delete);
				itHolder.group_name = (TextView) convertView
						.findViewById(R.id.group_name);
				itHolder.addFriend = (LinearLayout) convertView
						.findViewById(R.id.addFriend);
                itHolder.iv_move = (ImageView) convertView.findViewById(R.id.iv_move);
				convertView.setTag(itHolder);
			} else {
				itHolder = (itemHolder) convertView.getTag();
			}

            if(mContactsGroups.size()==1){
                itHolder.addFriend.setVisibility(View.GONE);
            }else {
                itHolder.addFriend.setVisibility(View.VISIBLE);
            }

            if(position == mContactsGroups.size()-1){
                itHolder.iv_move.setVisibility(View.INVISIBLE);
            }else{
                itHolder.iv_move.setVisibility(View.VISIBLE);
            }

			itHolder.item_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
                    listview.stopDrag();
					startAnimActivityForResult(ActivityPacketDelete.class,
							"contactsGroup", contactsGroup, "contactsObject",
							mApplication.contactsObject,
							AndroidConfig.PacketManagementRequestCode);
				}
			});

			itHolder.item_delete
					.setOnLongClickListener(new OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {
                            listview.stopDrag();
							if (!(mContactsGroups.get(position).getName()
									.equals("未分组"))) {
								curPosition = position;
								pecketId = mContactsGroups.get(position)
										.getId() + "";
								inputDialog = new InputDialog(mContext,
										R.style.MyDialogStyle);
								inputDialog.setEditContent(mContactsGroups.get(
										position).getName());
								inputDialog.setContent("修改分组名", "请输入新的分组名",
										"确认", "取消");
								inputDialog.setEditContent(mContactsGroups.get(
										position).getName());
								inputDialog
										.setCancleListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												inputDialog.dismiss();
											}
										});
								inputDialog
										.setOKListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												pecketName = inputDialog
														.getInputText();
												
//												if(pecketName.length() > 8){
//													ToastUtils.Errortoast(mContext, "分组在8个字以内");
//													return;
//												}
												if(!CommonUtils.StringIsSurpass2(pecketName, 2, 8)){
													ToastUtils.Errortoast(mContext, "名称限制在1-8个字之间");
													return;
												}
												if (CommonUtils
														.isNetworkAvailable(mContext)) {
													InteNetUtils.getInstance(
															mContext)
															.EditPacket(
																	pecketName,
																	pecketId,
																	changeName);

												}
											}
										});
								inputDialog.show();
							}
							return false;
						}

					});

			itHolder.addFriend.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
                    listview.stopDrag();
					startAnimActivity2Obj(ActivityPacketAddFriend.class,
							"contactsGroup", contactsGroup);
				}
			});

			itHolder.addFriend
					.setOnLongClickListener(new OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {
                            listview.stopDrag();
							if (!(mContactsGroups.get(position).getName()
									.equals("未分组"))) {
								curPosition = position;
								pecketId = mContactsGroups.get(position)
										.getId() + "";
								inputDialog = new InputDialog(mContext,
										R.style.MyDialogStyle);
								inputDialog.setEditContent(mContactsGroups.get(
										position).getName());
								inputDialog.setContent("修改分组名", "请输入新的分组名",
										"确认", "取消");
								inputDialog.setEditContent(mContactsGroups.get(
										position).getName());
								inputDialog
										.setCancleListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												inputDialog.dismiss();
											}
										});
								inputDialog
										.setOKListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												pecketName = inputDialog
														.getInputText();
												
												if(pecketName.length() > 8){
													ToastUtils.Errortoast(mContext, "分组在8个字以内");
													return;
												}
												if (CommonUtils
														.isNetworkAvailable(mContext)) {
													InteNetUtils.getInstance(
															mContext)
															.EditPacket(
																	pecketName,
																	pecketId,
																	changeName);

												}
											}
										});
								inputDialog.show();
							}
							return true;
						}

					});

			itHolder.item_layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
                    listview.stopDrag();
					mApplication.contactsGroup = mContactsGroups.get(position);
					startAnimActivityForResult(ActivityPacketInfo.class,
							AndroidConfig.PacketManagementRequestCodeInfo);
				}
			});

			itHolder.item_layout
					.setOnLongClickListener(new OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {
                            listview.stopDrag();
							if (!(mContactsGroups.get(position).getName()
									.equals("未分组"))) {
								curPosition = position;
								pecketId = mContactsGroups.get(position)
										.getId() + "";
								inputDialog = new InputDialog(mContext,
										R.style.MyDialogStyle);
								inputDialog.setEditContent(mContactsGroups.get(
										position).getName());
								inputDialog.setContent("修改分组名", "请输入新的分组名",
										"确认", "取消");
								inputDialog.setEditContent(mContactsGroups.get(
										position).getName());
								inputDialog
										.setCancleListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												inputDialog.dismiss();
											}
										});
								inputDialog
										.setOKListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												pecketName = inputDialog
														.getInputText();
												
												if(!CommonUtils.StringIsSurpass2(pecketName, 2, 8)){
													ToastUtils.Errortoast(mContext, "名称限制在1-8个字之间");
													return;
												}
												if (CommonUtils
														.isNetworkAvailable(mContext)) {
													InteNetUtils.getInstance(
															mContext)
															.EditPacket(
																	pecketName,
																	pecketId,
																	changeName);

												}
											}
										});
								inputDialog.show();
							}
							return false;
						}

					});

			if (!mContactsGroups.get(position).getName().equals("未分组")) {
				itHolder.changeNameBut.setVisibility(View.VISIBLE);
				itHolder.changeNameBut
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
                                listview.stopDrag();
								// 添加分组
								curPosition = position;
								pecketId = mContactsGroups.get(position)
										.getId() + "";
								inputDialog = new InputDialog(mContext,
										R.style.MyDialogStyle);
								inputDialog.setEditContent(mContactsGroups.get(
										position).getName());
								inputDialog.setContent("修改分组名", "请输入新的分组名",
										"确认", "取消");
								inputDialog.setEditContent(mContactsGroups.get(
										position).getName());
								inputDialog
										.setCancleListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												inputDialog.dismiss();
											}
										});
								inputDialog
										.setOKListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												pecketName = inputDialog
														.getInputText();
												
												if(!CommonUtils.StringIsSurpass2(pecketName, 2, 8)){
													ToastUtils.Errortoast(mContext, "名称限制在1-8个字之间");
													return;
												}
												if (CommonUtils
														.isNetworkAvailable(mContext)) {
													InteNetUtils.getInstance(
															mContext)
															.EditPacket(
																	pecketName,
																	pecketId,
																	changeName);

												}
											}
										});
								inputDialog.show();
							}
						});
			} else {
				itHolder.changeNameBut.setVisibility(View.GONE);
			}

			// int benbenNum = 0;
			// for (Contacts c : contactsGroup.getmContacts()) {
			// if (!c.getIs_benben().equals("0")) {
			// benbenNum++;
			// }
			// }
			itHolder.group_name.setText(mContactsGroups.get(position).getName() + "  ("
					+ mContactsGroups.get(position).getmContacts().size() + ")");

			// if ("未分组".equals(contactsGroup.getName())) {
			// itHolder.item_layout.setVisibility(View.GONE);
			// } else {
			// itHolder.item_layout.setVisibility(View.VISIBLE);
			// }

			return convertView;
		}

	}

	class itemHolder {
		ImageView changeNameBut;
		LinearLayout item_layout;
		LinearLayout item_delete;
		LinearLayout addFriend;
		TextView group_name;
        ImageView iv_move;
	}

	/**
	 * 修改分组名
	 */
	public RequestCallBack<String> changeName = new RequestCallBack<String>() {

		public void onStart() {
			// ToastUtils.Infotoast(mContext, "onstart");
		};

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(arg0.result);
				SuccessMsg msg = new SuccessMsg();
				msg.checkJson(jsonObject);

				mContactsGroups.get(curPosition).setName(pecketName);
				mApplication.contactsObject.setmContactsGroups(mContactsGroups);

				// 更新数据库
				dbUtil.saveOrUpdate(mContactsGroups.get(curPosition));
				curPosition = 0;
				myAdapter.notifyDataSetChanged();
				sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
				inputDialog.dismiss();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NetRequestException e) {
				e.getError().print(mContext);
				e.printStackTrace();
			}

		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			ToastUtils.Errortoast(mContext, arg1);
			inputDialog.dismiss();
		}
	};

	public void onBackPressed() {
		// 给上个页面设置返回数据
		setResult(AndroidConfig.ContactsFragmentResultCode);
		AnimFinsh();
	};

}
