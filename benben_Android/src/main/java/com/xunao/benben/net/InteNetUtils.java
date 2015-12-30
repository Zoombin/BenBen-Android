package com.xunao.benben.net;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.baidu.platform.comapi.map.A;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.XunaoLog;

public class InteNetUtils {

	private static InteNetUtils netUtils;
	private HttpUtils httpUtils;
	private static Context context;

	private InteNetUtils() {
		httpUtils = new HttpUtils();
		// 链接超时设置
		httpUtils.configSoTimeout(30000);
		httpUtils.configTimeout(30000);

	};

	public static InteNetUtils getInstance(Context context) {
		if (netUtils == null) {
			netUtils = new InteNetUtils();
		}
		InteNetUtils.context = context;
		return netUtils;
	}

	public static InteNetUtils getInstanceNo(Context context) {
		if (netUtils == null) {
			netUtils = new InteNetUtils();
			InteNetUtils.context = context;
		}
		return netUtils;
	}

	private void comParams(RequestParams params, HashMap<String, String> hashMap) {

		Set<Entry<String, String>> entrySet = hashMap.entrySet();

		if (CrashApplication.getInstance().user != null
				&& CrashApplication.getInstance().user.getToken() != null) {
			params.addBodyParameter("token",
					CrashApplication.getInstance().user.getToken());
		}

		for (Entry<String, String> entry : entrySet) {
			params.addBodyParameter(entry.getKey(), entry.getValue());
		}
	}

	private void ComPost(String url, HashMap<String, String> hashMap,
			RequestCallBack<String> callBack) {
		RequestParams params = new RequestParams();
		if (hashMap != null) {
			comParams(params, hashMap);

			Set<Entry<String, String>> entrySet = hashMap.entrySet();

			String log = "?";
			for (Entry<String, String> e : entrySet) {
				log += "&" + e.getKey() + "=" + e.getValue();
			}

			XunaoLog.yLog().i(url.substring(0, url.length() - 1) + log);
			httpUtils.send(HttpMethod.POST, url, params, callBack);
		}

	}

	// 添加图片上传
	private void addImageUpload(RequestParams params, String filekey, File file) {

		InputStream compressImage = CommonUtils.compressImage(file, 1000, 0);

		if (compressImage != null) {
			File temp = CommonUtils.getImagePath(context,
					CommonUtils.md5(file.getPath() + ".jpg"));
			try {
				FileOutputStream fop = new FileOutputStream(temp);

				int available = compressImage.available();
				byte[] buffer = new byte[available];
				compressImage.read(buffer);
				fop.write(buffer);

				if (temp != null) {
					// params.addBodyParameter(filekey, compressImage,
					// compressImage.available());
					params.addBodyParameter(filekey, temp, "image/jpeg");
					// params.addBodyParameter(filekey, compressImage,
					// compressImage.available(), file.getName());
					// temp.delete();
					// temp = null;
					compressImage.close();
					compressImage = null;
					fop.close();
					fop = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 可上传图片的方法
	private void ComPost(final String url,
			final HashMap<String, String> hashMap,
			final RequestCallBack<String> callBack, final String filekey,
			final File file) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				RequestParams params = new RequestParams();
				addImageUpload(params, filekey, file);
				if (hashMap != null) {
					comParams(params, hashMap);
				}
				httpUtils.send(HttpMethod.POST, url, params, callBack);
			}
		}).start();
	}

	// 可上传两张图片的方法
	private void ComPost(final String url,
			final HashMap<String, String> hashMap,
			final RequestCallBack<String> callBack, final String filekey,
			final File file, final String fileKey2, final File file2) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				RequestParams params = new RequestParams();

				for (int i = 0; i < 2; i++) {
					if (i == 0) {
						addImageUpload(params, filekey, file);
					} else {
						addImageUpload(params, fileKey2, file2);
					}
				}

				if (hashMap != null) {
					comParams(params, hashMap);
				}
				httpUtils.send(HttpMethod.POST, url, params, callBack);
			}
		}).start();
	}

    // 可上传多张图片的方法,最多6张
    private void ComPost(final String url,
                         final HashMap<String, String> hashMap,
                         final RequestCallBack<String> callBack, final List<File> files) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                String[] strings={"img1","img2","img3","img4","img5","img6"};
                for (int i = 0; i < files.size(); i++) {
                    addImageUpload(params, strings[i], files.get(i));
                }

                if (hashMap != null) {
                    comParams(params, hashMap);
                }
                httpUtils.send(HttpMethod.POST, url, params, callBack);
            }
        }).start();
    }

	// 获取手机验证码
	public void autoLogin(String token, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("token", token);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.AutoLogin, hashMap,
				callBack);
	}

	// 获取手机验证码
	public void getPhoneCode(String phoneNum, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("phone", phoneNum);
		hashMap.put("is_reset", "2");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.PhoneCode, hashMap,
				callBack);
	}

	// 获取手机验证码
	public void getForgetPhoneCode(String phoneNum,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("phone", phoneNum);
		hashMap.put("is_reset", "1");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.PhoneCode, hashMap,
				callBack);
	}

	// 忘记密码
	public void updateForgetPhoneCode(String phoneNum, String password,
			String repassword, String code, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("phone", phoneNum);
		hashMap.put("password", password);
		hashMap.put("repassword", repassword);
		hashMap.put("code", code);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.PhoneForget, hashMap,
				callBack);
	}

	// 会员注册
	public void Register(String nick_name, String phone, String age,
			String sex, String password, String repassword, String phone_model,
			String code, RequestCallBack<String> callBack) {

		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("nick_name", nick_name);
		hashMap.put("phone", phone);
		hashMap.put("age", age);
		hashMap.put("sex", sex);
		hashMap.put("password", password);
		hashMap.put("repassword", repassword);
		hashMap.put("phone_model", phone_model);
		hashMap.put("code", code);

		ComPost(AndroidConfig.NETHOST + AndroidConfig.UserRegister, hashMap,
				callBack);
	}

	// 会员登录
	public void Login(String user_name, String user_password,
			String phone_model, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("phone", user_name);
		hashMap.put("password", user_password);
		hashMap.put("phone_model", phone_model);

		ComPost(AndroidConfig.NETHOST + AndroidConfig.UserLogin, hashMap,
				callBack);
	}

	// 修改头像
	public void updateFace(File file, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.UpdateFace, hashMap,
				callBack, "poster", file);
	}

	// 修改密码
	public void updatePassword(String oldPassword, String newPassword,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("oldpassword", oldPassword);
		hashMap.put("password", newPassword);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.UpdatePwd, hashMap,
				callBack);
	}

	// 修改通讯录名称
	public void updateContactsName(String contactsId, String newName,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("id", contactsId);
		hashMap.put("name", newName);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.UpdateContactsName,
				hashMap, callBack);
	}

	// 修改年龄
	public void updateBirthday(String newAge, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("age", newAge);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.UpdateUser, hashMap,
				callBack);
	}

	// 通讯录匹配 分组名间以逗号,分隔,没有分组则空
	public void PhoneMatch(String group, String phone,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("group", group);
		hashMap.put("phone", phone);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.PhoneMatch, hashMap,
				callBack);
	}

	// 同步通讯录
	public void contactsSynchro(String phone, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		// hashMap.put("group", group);
		hashMap.put("phone", phone);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.ContactsSynchro, hashMap,
				callBack);
	}

    public void Updatematch(String phone, RequestCallBack<String> callBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        // hashMap.put("group", group);
        hashMap.put("phone", phone);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Updatematch, hashMap,
                callBack);
    }


	// 获取通讯录
	public void GetContact(RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.GetContact, hashMap,
				callBack);
	}

	// 添加分组
	public void AddPacket(String packetName, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("group", packetName);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.AddPacket, hashMap,
				callBack);
	}

	// 编辑分组
	public void EditPacket(String packetName, String packetId,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("group", packetName);
		hashMap.put("group_id", packetId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EditPacket, hashMap,
				callBack);
	}

	// 删除分组
	public void DeletePacket(String group_id, String target,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("group_id", group_id);
		hashMap.put("target", target);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.DeletePacket, hashMap,
				callBack);
	}

    // 新增联系人
    public void Addcontact(String name,String phone,String group_id, RequestCallBack<String> callBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("name", name);
        hashMap.put("phone", phone);
        hashMap.put("group_id", group_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Addcontact, hashMap,
                callBack);
    }

	// 朋友圈数据
	public void getFriendData(String lastCreateTime,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("last_time", lastCreateTime);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.GetFriend, hashMap,
				callBack);

	}

	// 我的动态
	public void getMyDynamicData(String lastCreateTime,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("last_time", lastCreateTime);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.GetMyDynamic, hashMap,
				callBack);

	}

	// 微创作数据
	public void getSmallMakeData(String lastCreateTime,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("last_time", lastCreateTime);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.GetSmallMake, hashMap,
				callBack);
	}

	// 我关注的微创作数据
	public void getMyselfSmallMakeData(String lastCreateTime,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("is_like", "1");
		hashMap.put("last_time", lastCreateTime);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.GetSmallMake, hashMap,
				callBack);
	}

	// 我的微创作数据
	public void getMySmallMakeData(String lastCreateTime,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("last_time", lastCreateTime);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.GetMySmallMake, hashMap,
				callBack);
	}

	// 编辑分组成员 多用户之间用逗号分开 注 这里传递的是整个组的联系人
	public void EditPacketInfo(String group_id, String user_id,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("group_id", group_id);
		hashMap.put("user_id", user_id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EditPacketInfo, hashMap,
				callBack);
	}

	public void EditPacketInfoGroup(String group_id, String user_id,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("group_id", group_id);
		hashMap.put("user_id", user_id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EditPacketInfoGroup,
				hashMap, callBack);
	}

	// 获得我的分组
	public void getTalkGroup(RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.GetTalkGroup, hashMap,
				callBack);
	}

	// // 检测更新
	public void checkVersion(RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.CHECKVERSION, hashMap,
				callBack);
	}

	// 投诉建议
	public void doComplain(String content, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("info", content);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.DoComplain, hashMap,
				callBack);
	}

	// 我要买举报
	public void doBuyComplain(String buyId, String content,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("reason", content);
		hashMap.put("userid", buyId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.DoBuyComplain, hashMap,
				callBack);
	}

	public void doPublic(final String friendUnionId, final String content, final String phone,final String legphone,
			final String type, final String[] images, final RequestCallBack<String> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("key", "android");
                hashMap.put("content", content);
                hashMap.put("league_id", friendUnionId);
                hashMap.put("phone", phone);
                hashMap.put("legphone", legphone);
                hashMap.put("type", type);
                RequestParams params = new RequestParams();
                int length = images.length;
                for (int i = 1; i <= length; i++) {
                    addImageUpload(params, "img" + i, new File(
                            images[i - 1]));
                }
                Set<Entry<String, String>> entrySet = hashMap.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    params.addBodyParameter(entry.getKey(), entry.getValue());
                }
                httpUtils.send(HttpMethod.POST, AndroidConfig.NETHOST
                        + AndroidConfig.sendPublic, params, callBack);
            }
        }).start();


	}

    public void doPublicVoice(final String friendUnionId, final String content, final String phone,final String legphone,
                         final String type, final String recordFile,final int recordLength, final RequestCallBack<String> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("key", "android");
                hashMap.put("content", content);
                hashMap.put("league_id", friendUnionId);
                hashMap.put("phone", phone);
                hashMap.put("legphone", legphone);
                hashMap.put("type", type);
                RequestParams params = new RequestParams();
                if(!recordFile.equals("")) {
                    params.addBodyParameter("audio", new File(recordFile),
                            "audio/AMR");
                    hashMap.put("audiotime", recordLength+"");
                }

                Set<Entry<String, String>> entrySet = hashMap.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    params.addBodyParameter(entry.getKey(), entry.getValue());
                }
                httpUtils.send(HttpMethod.POST, AndroidConfig.NETHOST
                        + AndroidConfig.sendPublic, params, callBack);
            }
        }).start();


    }

	// 获取地址
	public void getAddress(String bid, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("bid", bid);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.GetAddress, hashMap,
				callBack);
	}

	// 获取行业
	public void getIndustry(long time, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("time", time+"");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.GetIndustry, hashMap,
				callBack);
	}

	// 创建组群 带了图片
	public void addGroup(File poster, String name, String[] address,
			String info, String notice, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("name", name);
		hashMap.put("description", info);
		hashMap.put("notice", notice);
		if (address != null && address.length >= 3) {
			hashMap.put("province", address[0]);
			hashMap.put("city", address[1]);
			hashMap.put("region", address[2]);
		}

		if (poster != null) {
			ComPost(AndroidConfig.NETHOST + AndroidConfig.AddGroup, hashMap,
					callBack, "poster", poster);
		} else {
			ComPost(AndroidConfig.NETHOST + AndroidConfig.AddGroup, hashMap,
					callBack);
		}
	}

	// 证企通讯录
	public void getEnterprises(RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.GetEnterpriseList,
				hashMap, callBack);
	}

	// 添加证企通讯录
	public void addEnterprises(String name, int type, String phone,
			String intro, String[] addressId, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("name", name);
		hashMap.put("type", type + "");
		hashMap.put("short_phone", phone);
		hashMap.put("description", intro);
		hashMap.put("province", addressId[0]);
		hashMap.put("city", addressId[1]);
		hashMap.put("area", addressId[2]);

		ComPost(AndroidConfig.NETHOST + AndroidConfig.AddEnterpriseList,
				hashMap, callBack);
	}

	// 修改证企通讯录
	public void updateEnterprises(String id, String name, int type,
			String phone, String intro, String[] addressId,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterpriseid", id);
		hashMap.put("name", name);
		hashMap.put("type", type + "");
		hashMap.put("short_phone", phone);
		hashMap.put("description", intro);

		if (addressId != null) {
			hashMap.put("description", intro);
			hashMap.put("province", addressId[0]);
			hashMap.put("city", addressId[1]);
			hashMap.put("area", addressId[2]);
		}

		ComPost(AndroidConfig.NETHOST + AndroidConfig.UpdateEnterpriseList,
				hashMap, callBack);
	}

	// 证企通讯录成员
	public void enterpriseMember(String id, String keyWord,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterpriseid", id);
		hashMap.put("keyword", keyWord);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterpriseMember,
				hashMap, callBack);
	}

	// 证企通讯录成员
	public void enterpriseAllMember(String id, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterpriseid", id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterpriseAllMember,
				hashMap, callBack);
	}

	// 证企通讯录详细
	public void enterprisesDetail(String id, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterpriseid", id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterpriseDetail,
				hashMap, callBack);
	}

	// 退出政企通讯录
	public void enterprisesExit(String id, String enterpriseId,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("member_id", id);
		hashMap.put("enterpriseid", enterpriseId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterpriseExit, hashMap,
				callBack);
	}

	// 查询政企通讯录成员
	public void searchEnterprisesMember(String enterpriseId, String keyWord,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("keyword", keyWord);
		hashMap.put("enterpriseid", enterpriseId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.SearchEnterpriseMember,
				hashMap, callBack);
	}

	// 号码直通车
	public void getStoreList(int pagerNum, String searchKey, double latitude,
			double longitude, String province, String city, String area, String street,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("keyword", searchKey);
		hashMap.put("page", pagerNum + "");
		hashMap.put("latitude", latitude + "");
		hashMap.put("longitude", longitude + "");
		hashMap.put("province", province);
		hashMap.put("city", city);
		hashMap.put("area", area);
		hashMap.put("street", street);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.GetStoreList, hashMap,
				callBack);
	}

	// 我的号码直通车
	public void getMyStore(RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.MyStoreList, hashMap,
				callBack);
	}

	// 朋友圈点赞
	public void clickGood(String friendId, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("friendid", friendId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.clickGood, hashMap,
				callBack);
	}

	// 朋友圈取消点赞
	public void cancelClickGood(String friendId,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("friendid", friendId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.cancelClickGood, hashMap,
				callBack);
	}

	// 微创作点赞
	public void clickSmallGood(String creationid,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("creationid", creationid);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.clickSmallGood, hashMap,
				callBack);
	}

	// 朋友圈取消点赞
	public void cancelSmallClickGood(String creationid,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("creationid", creationid);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.cancelSmallClickGood,
				hashMap, callBack);
	}

	// 朋友评论
	public void publicComment(String friendId, String content,String replier,
			RequestCallBack<String> callBack) {

		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("friendid", friendId);
		hashMap.put("content", content);
        hashMap.put("replier", replier);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.comment, hashMap,
				callBack);
	}

	// 微创作评论
	public void publicSmallComment(String creationid, String content,
			RequestCallBack<String> callBack) {

		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("creationid", creationid);
		hashMap.put("content", content);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.smallcomment, hashMap,
				callBack);
	}

	public void publicFrient(final String type, final String content,
			final String[] images, final RequestCallBack<String> callBack) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				int length = images.length;
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("key", "android");
				hashMap.put("type", type);
				hashMap.put("description", content);

				RequestParams params = new RequestParams();

				for (int i = 1; i <= length; i++) {
					// params.addBodyParameter("img" + i, new File(images[i -
					// 1]),
					// "image/jpeg");
					addImageUpload(params, "img" + i, new File(images[i - 1]));

				}

				Set<Entry<String, String>> entrySet = hashMap.entrySet();

				for (Entry<String, String> entry : entrySet) {
					params.addBodyParameter(entry.getKey(), entry.getValue());
				}

				httpUtils.send(HttpMethod.POST, AndroidConfig.NETHOST
						+ AndroidConfig.publicF, params, callBack);
			}
		}).start();

	}

	// 发送微创作
	public void publicSmallMake(final int type, final String content,
			final String friendUnion, final String[] images,
			final String recordFile, final String[] addressId,
			final RequestCallBack<String> callBack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HashMap<String, String> hashMap = new HashMap<String, String>();
				if (addressId != null) {
					hashMap.put("province", addressId[0]);
					hashMap.put("city", addressId[1]);
					hashMap.put("area", addressId[2]);
				}

				hashMap.put("key", "android");
				hashMap.put("type", type + "");
				hashMap.put("description", content);
				if (!TextUtils.isEmpty(friendUnion)) {
					hashMap.put("league_id", friendUnion);
				}
				RequestParams params = new RequestParams();

				switch (type) {
				case 0:// 图文
					int length = images.length;
					for (int i = 1; i <= length; i++) {
						// params.addBodyParameter("img" + i, new File(images[i
						// - 1]),
						// "image/jpeg");
						addImageUpload(params, "img" + i, new File(
								images[i - 1]));
					}
					break;
				case 1:// 音频
					params.addBodyParameter("audio", new File(recordFile),
							"audio/AMR");

					break;
				}

				Set<Entry<String, String>> entrySet = hashMap.entrySet();

				for (Entry<String, String> entry : entrySet) {
					params.addBodyParameter(entry.getKey(), entry.getValue());
				}

				httpUtils.send(HttpMethod.POST, AndroidConfig.NETHOST
						+ AndroidConfig.publicS, params, callBack);
			}
		}).start();
	}

	// 查询组群
	public void findTalkGroup(String keyword, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("keyword", keyword);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.FindTalkGroup, hashMap,
				callBack);
	}

	// 加入群组
	public void joinTalkGroup(String huanxin_groupid, String huanxin_username,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("huanxin_groupid", huanxin_groupid);
		hashMap.put("huanxin_username", huanxin_username);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.JoinGroup, hashMap,
				callBack);
	}

    // 群组拒绝
    public void rejectGroup(String huanxin_groupid, String hxusername,String token,
                             RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("huanxin_groupid", huanxin_groupid);
        hashMap.put("hxusername", hxusername);
        hashMap.put("token", token);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.rejectGroup, hashMap,
                requestCallBack);
    }

	// 退出群组
	public void quitTalkGroup(String group_id, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("group_id", group_id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.QuitGroup, hashMap,
				callBack);
	}

    // 屏蔽群消息
    public void setFreeMode(String group_id,int freemode,String token, RequestCallBack<String> callBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("groupid", group_id);
        hashMap.put("freemode", freemode+"");
        hashMap.put("token", token);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.setFreeMode, hashMap,
                callBack);
    }

	// 退出群组(群主T人)
	public void quitTalkGroup(String group_id, String member_id,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("group_id", group_id);
		hashMap.put("member_id", member_id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.QuitGroup, hashMap,
				callBack);
	}

	// 我的好友联盟
	public void getMyFriendUnion(RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.MySelfFriendUnion,
				hashMap, callBack);
	}

	// 创建我的好友联盟
	public void createdFriendUnion(String name, String info,
			String[] addressId, File file, String type,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("name", name);
		hashMap.put("description", info);
		hashMap.put("province", addressId[0]);
		hashMap.put("city", addressId[1]);
		hashMap.put("area", addressId[2]);
		hashMap.put("type", type);

		ComPost(AndroidConfig.NETHOST + AndroidConfig.CreatedFriendUnion,
				hashMap, callBack, "poster", file);

	}

	public void updateFriendUnion(String id, String name, String info,
			String[] addressId, String announcement, File file,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("name", name);
		hashMap.put("description", info);
		hashMap.put("leagueid", id);
		hashMap.put("announcement", announcement);
		if (addressId != null) {
			hashMap.put("province", addressId[0]);
			hashMap.put("city", addressId[1]);
			hashMap.put("area", addressId[2]);
		}

		if (file == null) {
			ComPost(AndroidConfig.NETHOST + AndroidConfig.UpdateFriendUnion,
					hashMap, callBack);
		} else {
			ComPost(AndroidConfig.NETHOST + AndroidConfig.UpdateFriendUnion,
					hashMap, callBack, "poster", file);
		}
	}

	// 邀请好友加入好友联盟
	public void inviteFriendToUnion(String id, String memberId,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("leagueid", id);
		hashMap.put("benben_id", memberId);

		ComPost(AndroidConfig.NETHOST + AndroidConfig.InviteFriendUnion,
				hashMap, callBack);
	}

	// 退出好友联盟
	public void quitFriendUnion(String id, String memberId,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("leagueid", id);
		hashMap.put("benben_id", memberId);

		ComPost(AndroidConfig.NETHOST + AndroidConfig.QuitFriendUnion, hashMap,
				callBack);
	}

	// 号码直通车详情
	public void getStoreListDetail(String id, String latitude,
			String longitude, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("id", id);
		hashMap.put("latitude", CommonUtils.isEmpty(latitude) ? "" : latitude);
		hashMap.put("longitude", CommonUtils.isEmpty(longitude) ? ""
				: longitude);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.GetStoreListDetail,
				hashMap, callBack);
	}

    // 号码直通车详情
    public void getOwnerDetail(String id, String latitude,
                                   String longitude, RequestCallBack<String> callBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("id", id);
        hashMap.put("latitude", CommonUtils.isEmpty(latitude) ? "" : latitude);
        hashMap.put("longitude", CommonUtils.isEmpty(longitude) ? ""
                : longitude);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.GetOwnerDetail,
                hashMap, callBack);
    }

	public void attention(String memberId, RequestCallBack<String> callBack) {

		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("memberid", memberId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.Attention, hashMap,
				callBack);
	}

	public void getMyAttention(RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.getAttention, hashMap,
				callBack);
	}

	public void cnacleAttention(String creationAuthId,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("memberid", "" + creationAuthId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.cancleAttention, hashMap,
				requestCallBack);
	}

	// 收藏号码直通车
	public void collectStore(String id, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("id", id);

		ComPost(AndroidConfig.NETHOST + AndroidConfig.CollectStore, hashMap,
				callBack);
	}

	// 取消收藏号码直通车
	public void CancelCollectStore(String id, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("id", id);

		ComPost(AndroidConfig.NETHOST + AndroidConfig.CancelCollectStore,
				hashMap, callBack);
	}

//	// 修改我的号码直通车
//	public void updateMyNumberTrain(String name, String shortName,
//			String phone, String numPhone, String industry, String address,
//			String lags, String descriptions, String[] areas, double latitude,
//			double longitude, File file, RequestCallBack<String> callBack) {
//		HashMap<String, String> hashMap = new HashMap<String, String>();
//		hashMap.put("key", "android");
//		hashMap.put("name", name);
//		hashMap.put("short_name", shortName);
//		hashMap.put("phone", numPhone);
//		hashMap.put("telephone", phone);
//		hashMap.put("industry", industry);
//		hashMap.put("address", address);
//		hashMap.put("tag", lags);
//		hashMap.put("description", descriptions);
//
//		if (areas[0] != null)
//			hashMap.put("province", areas[0]);
//		if (areas[1] != null)
//			hashMap.put("city", areas[1]);
//		if (areas[2] != null)
//			hashMap.put("area", areas[2]);
//		if (areas[3] != null)
//			hashMap.put("street", areas[3]);
//
//		hashMap.put("lat", latitude + "");
//		hashMap.put("lng", longitude + "");
//
//		if (file != null) {
//			ComPost(AndroidConfig.NETHOST + AndroidConfig.UpdateMyStoreList,
//					hashMap, callBack, "poster", file);
//		} else {
//			ComPost(AndroidConfig.NETHOST + AndroidConfig.UpdateMyStoreList,
//					hashMap, callBack);
//		}
//
//	}

    // 修改我的号码直通车
    public void updateMyNumberTrain(final String name, final String shortName,
                                    final String phone, final String numPhone, final String industry, final String address,
                                    final String lags, final String descriptions, final String[] areas, final double latitude,
                                    final double longitude, final String[] images,final String poster,final String ids,final String token,final RequestCallBack<String> callBack) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("key", "android");
                hashMap.put("name", name);
                hashMap.put("short_name", shortName);
                hashMap.put("phone", numPhone);
                hashMap.put("telephone", phone);
                hashMap.put("industry", industry);
                hashMap.put("address", address);
                hashMap.put("tag", lags);
                hashMap.put("description", descriptions);

                if (areas[0] != null)
                    hashMap.put("province", areas[0]);
                if (areas[1] != null)
                    hashMap.put("city", areas[1]);
                if (areas[2] != null)
                    hashMap.put("area", areas[2]);
                if (areas[3] != null)
                    hashMap.put("street", areas[3]);

                hashMap.put("lat", latitude + "");
                hashMap.put("lng", longitude + "");
                hashMap.put("poster", poster);
                hashMap.put("ids", ids);
                hashMap.put("token", token);
                RequestParams params = new RequestParams();
                int length = images.length;
                for (int i = 1; i <= length; i++) {
                    addImageUpload(params, "pic" + i, new File(
                            images[i - 1]));
                }
                Set<Entry<String, String>> entrySet = hashMap.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    params.addBodyParameter(entry.getKey(), entry.getValue());
                }
                httpUtils.send(HttpMethod.POST, AndroidConfig.NETHOST
                        + AndroidConfig.UpdateMyStoreList, params, callBack);
            }
        }).start();

    }

	// 查询个人信息
	public void memberInfo(RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.MemberInfo, hashMap,
				callBack);
	}

	// 修改会员
	public void updateMemberInfo(String sex, String nickName, String name,
			String address, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("sex", sex);
		hashMap.put("nick_name", nickName);
		hashMap.put("name", name);
		hashMap.put("address", address);

		ComPost(AndroidConfig.NETHOST + AndroidConfig.UpdateUser, hashMap,
				callBack);
	}

	// 修改所在地区
	public void updateArea(String addressId[], RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("province", addressId[0]);
		hashMap.put("city", addressId[1]);
		hashMap.put("area", addressId[2]);
		hashMap.put("street", addressId[3]);

		ComPost(AndroidConfig.NETHOST + AndroidConfig.UpdateUser, hashMap,
				callBack);
	}

	// 加入百姓网
	public void enterBaixing(String name, String phone, String card, File file,
			File file2, String[] address, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("name", name);
		hashMap.put("phone", phone);
		hashMap.put("id_card", card);
		if (address[0] != null)
			hashMap.put("province", address[0]);
		if (address[1] != null)
			hashMap.put("city", address[1]);
		if (address[2] != null)
			hashMap.put("area", address[2]);
		if (address[3] != null) {
			hashMap.put("street", address[3]);
		}

		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterBx, hashMap,
				callBack, "poster1", file, "poster2", file2);
	}

	// 加入百姓网2
	public void editBaixing(String name, String phone, String card, File file,
			File file2, String[] address, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("name", name);
		hashMap.put("phone", phone);
		if (!CommonUtils.isEmpty(card))
			hashMap.put("id_card", card);

		if (address != null) {
			if (address[0] != null)
				hashMap.put("province", address[0]);
			if (address[1] != null)
				hashMap.put("city", address[1]);
			if (address[2] != null)
				hashMap.put("area", address[2]);
			if (address[3] != null)
				hashMap.put("street", address[3]);
		}

		if (file != null && file2 != null) {
			ComPost(AndroidConfig.NETHOST + AndroidConfig.EditBx, hashMap,
					callBack, "poster1", file, "poster2", file2);
		} else if (file != null && file2 == null) {
			ComPost(AndroidConfig.NETHOST + AndroidConfig.EditBx, hashMap,
					callBack, "poster1", file);
		} else if (file == null && file2 != null) {
			ComPost(AndroidConfig.NETHOST + AndroidConfig.EditBx, hashMap,
					callBack, "poster2", file2);
		} else {
			ComPost(AndroidConfig.NETHOST + AndroidConfig.EditBx, hashMap,
					callBack);
		}

	}

	// 获取非百姓网用户
	public void getNotBaixing(RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.GetNotBx, hashMap,
				callBack);
	}

	// 百姓网申请查询
	public void applyBxInfo(RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.ApplyBxInfo, hashMap,
				callBack);
	}

	// 百姓网申请查询
	public void applyBxInfowithId(String id, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("id", id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.ApplyBxInfoID, hashMap,
				callBack);
	}

	// 百姓网申请进度查询(单个)
	public void applyBxInfoSingle(String phone, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("phone", phone);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.BxProgressSingle,
				hashMap, callBack);
	}

	// 我的号码直通车资料完善
	public void perfectMyStore(String name, String phone, String card,
			File file, File file2, String[] address,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();

		String[] area = { "province", "city", "area" };

		for (int i = 0; i < area.length; i++) {
			hashMap.put(area[i], address[i]);
		}

		hashMap.put("key", "android");
		hashMap.put("name", name);
		hashMap.put("phone", phone);
		hashMap.put("id_card", card);

		ComPost(AndroidConfig.NETHOST + AndroidConfig.PerfectMyStore, hashMap,
				callBack, "poster1", file, "poster2", file2);
	}

	// 邀请好友加入百姓网
	public void inviteFriendToBx(String msg, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("namephone", msg);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.InviteFriendToBx,
				hashMap, callBack);
	}

	// 查询加入百姓网进度
	public void searchApplyBxProgress(String phone,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("phone", phone);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.BxProgress, hashMap,
				callBack);
	}

	// 轻松一刻
	public void getHappy(String id, String newsTime,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("lastid", id);
		//hashMap.put("last_time", newsTime);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.GetHappy, hashMap,
				callBack);
	}

	// 轻松一刻点赞或吐槽
	public void setHappyGoodOrBad(String status, String id,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("status", status);
		hashMap.put("happy_id", id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.SetHappyGoodOrBad,
				hashMap, callBack);
	}

	public void getBuyInfo(String lastTime, String page, String searchKey,
			String[] addressIds, RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("last_time", lastTime);
		hashMap.put("page", page);
		hashMap.put("keyword", searchKey);
		if (addressIds != null) {
			String[] key = { "province", "city", "area" };
			for (int i = 0; i < 3; i++) {
				if (addressIds[i] != null) {
					hashMap.put(key[i], addressIds[i]);
				}
			}
		}

		ComPost(AndroidConfig.NETHOST + AndroidConfig.getBuyInfo, hashMap,
				callBack);
	}

	public void getMyBuyInfo(String lastTime, String searchKey, String type,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("last_time", lastTime);
		hashMap.put("type", type);
		hashMap.put("keyword", searchKey);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.getMyBuyInfo, hashMap,
				callBack);
	}

	public void submitBuyPrice(int buyId, String price, String description,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("buyid", buyId + "");
		hashMap.put("price", price);
		hashMap.put("description", description);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.submitPrice, hashMap,
				requestCallBack);

	}

	public void buySearch(String lastTime, String searchKey,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("last_time", lastTime);
		hashMap.put("keyword", searchKey);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.buySearch, hashMap,
				callBack);
	}

	public void buySearchFriend(String lastTime, String searchKey,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("last_time", lastTime);
		hashMap.put("keyword", searchKey);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.buySearchFriend, hashMap,
				callBack);
	}

    public void recommendFriends(RequestCallBack<String> callBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.recommendFriends, hashMap,
                callBack);
    }

    public void addRecommendFriend(String member_id,RequestCallBack<String> callBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("member_id", member_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.addRecommendFriend, hashMap,
                callBack);
    }





	public void sendBuyInfo(String sTitle, String sInfo, String sNum,
			String sTime, String[] areas,
			RequestCallBack<String> mRequestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("title", sTitle);
		hashMap.put("description", sInfo);
		hashMap.put("amount", sNum);
		hashMap.put("deadline", sTime);

		hashMap.put("province", areas[0]);
		hashMap.put("city", areas[1]);
		hashMap.put("area", areas[2]);
		hashMap.put("street", areas[3]);

		ComPost(AndroidConfig.NETHOST + AndroidConfig.publicBuyInfo, hashMap,
				mRequestCallBack);
	}

	public void getBuyInfoContent(String iD,
			RequestCallBack<String> mRequestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("buyid", iD);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.publicBuyInfoContent,
				hashMap, mRequestCallBack);

	}

	public void closeBuy(String iD, RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("buyid", iD);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.closeBuy, hashMap,
				requestCallBack);
	}

	public void acceptPrice(String id, int store_id, String quoteid,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("buyid", id + "");
		hashMap.put("ntid", store_id + "");
		hashMap.put("quoteid", quoteid);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.acceptBuy, hashMap,
				requestCallBack);

	}

	public void deleteNumber(String id, String token,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("token", token);
		hashMap.put("id", id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.deleteNumber, hashMap,
				requestCallBack);
	}

    public void setActive(String id, String token,int infoid,
                             RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("token", token);
        hashMap.put("id", id);
        hashMap.put("infoid", infoid+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.setActive, hashMap,
                requestCallBack);
    }

    public void deleteBenBen(String benben, String token,int infoid,
                             RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("token", token);
        hashMap.put("benben", benben);
        hashMap.put("infoid", infoid+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.delBenBen, hashMap,
                requestCallBack);
    }

	public void addPhone(String pone, String id,String token,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("phone", pone);
		hashMap.put("id", id);
        hashMap.put("token", token);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.addNumber, hashMap,
				requestCallBack);
	}

	public void deleteContact(String id, RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("id", id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.deleteContact, hashMap,
				requestCallBack);

	}

	public void getUpdateInfo(String lastTime,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("last_time", lastTime);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.messageCenter, hashMap,
				requestCallBack);
	}

	// 更换手机号
	public void updatePhone(String phone, String code,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("phone", phone);
		hashMap.put("code", code);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.updatePhone, hashMap,
				requestCallBack);
	}

	public void addFirend(String huanxin_username,String name,String group_id,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("huanxin_username", huanxin_username);
        hashMap.put("name", name);
        hashMap.put("group_id", group_id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.addFriend, hashMap,
				requestCallBack);
	}

    public void applyFriend(String from_huanxin,String to_huanxin,String name,String group_id,
                          RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("from_huanxin", from_huanxin);
        hashMap.put("to_huanxin", to_huanxin);
        hashMap.put("name", name);
        hashMap.put("group_id", group_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.applyFriend, hashMap,
                requestCallBack);
    }

    public void rejectFriend(String huanxin_username,String token,
                          RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("huanxin_username", huanxin_username);
        hashMap.put("token", token);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.rejectFriend, hashMap,
                requestCallBack);
    }

	public void addCompany(String identity1, String shortPhone,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("news_id", identity1);
		hashMap.put("short_phone", shortPhone);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.addCompany, hashMap,
				requestCallBack);
	}

	public void readNews(String id, RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("news_id", id + "");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.readNews, hashMap,
				requestCallBack);
	}

	public void getSingleSmall(String identity1,
			RequestCallBack<String> mRequestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("creationid", identity1 + "");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.singleSmall, hashMap,
				mRequestCallBack);

	}

	public void getSingleGroupInfo(String id,
			RequestCallBack<String> mRequestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("huanxin_groupid", id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.singleGroupInfo, hashMap,
				mRequestCallBack);
	}

	public void getDetailGroup(String id,
			RequestCallBack<String> mRequestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("groupid", id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.singleGroupInfo, hashMap,
				mRequestCallBack);

	}

	// 修改群组信息
	public void updateGroup(String groupid, File poster, String name,
			String[] address, String info, String notice,
			RequestCallBack<String> callBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("groupid", groupid);
		hashMap.put("name", name);
		hashMap.put("description", info);
		hashMap.put("notice", notice);
		String[] pram = { "province", "city", "region", "street" };

		if (address != null) {
			int len = address.length;
			for (int i = 0; i < len; i++) {
				hashMap.put(pram[i], address[i]);
			}
		}

		if (poster != null) {
			ComPost(AndroidConfig.NETHOST + AndroidConfig.updateGroupInfo,
					hashMap, callBack, "poster", poster);
		} else {
			ComPost(AndroidConfig.NETHOST + AndroidConfig.updateGroupInfo,
					hashMap, callBack);
		}
	}

	public void EditGroupNmae(String name, String id,
			RequestCallBack<String> changeName) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("groupid", id);
		hashMap.put("nickname", name);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.editNickName, hashMap,
				changeName);
	}

	public void getTalkGroupMember(String id,
			RequestCallBack<String> mRequestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("group_id", id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.getGroupMember, hashMap,
				mRequestCallBack);
	}

    public void getinvitemember(String id,
                                   RequestCallBack<String> mRequestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("groupid", id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.getinvitemember, hashMap,
                mRequestCallBack);
    }

	// 政企通讯录邀请好友
	public void enterpriseInviteFriend(String enterpriseId, String memberId,
			String contactId, String shortPhone, String name,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterprise_id", enterpriseId);
		hashMap.put("member_id", memberId);
		hashMap.put("contact_id", contactId);
		hashMap.put("short_phone", shortPhone);
		hashMap.put("name", name);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterpriseVInvite,
				hashMap, requestCallBack);
	}

	// 主动加入政企通讯录
	public void enterpriseAdd(String enterpriseId, String shortPhone,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterprise_id", enterpriseId);
		hashMap.put("short_phone", shortPhone);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterpriseAdd, hashMap,
				requestCallBack);
	}

	// 政企通讯录邀请好友
	public void enterpriseVInviteFriend(String enterpriseId, String name,
			String manual, RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterprise_id", enterpriseId);
		hashMap.put("contact_info", name);

		if (!"".equals(manual)) {
			hashMap.put("manual", manual);
		}

		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterpriseVInvite,
				hashMap, requestCallBack);
	}

	// 政企通讯录邀请好友
	public void enterpriseVInviteFriendshort(String enterpriseId, String name,
			String manual, RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterprise_id", enterpriseId);
		hashMap.put("contact_info", name);
		
		if (!"".equals(manual)) {
			hashMap.put("manual", manual);
		}
		
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterpriseInvite,
				hashMap, requestCallBack);
	}

	// 政企通讯录分组管理
	public void enterpriseGroupList(String enterpriseId,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterpriseid", enterpriseId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterpriseGroup, hashMap,
				requestCallBack);
	}

	// 政企通讯录新建分组
	public void addEnterpriseGroup(String enterpriseId, String groupName,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterpriseid", enterpriseId);
		hashMap.put("name", groupName);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterpriseGroupAdd,
				hashMap, requestCallBack);
	}

	// 政企通讯录编辑分组
	public void updateEnterpriseGroup(String groupId, String groupName,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("groupid", groupId);
		hashMap.put("name", groupName);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterpriseGroupUpdate,
				hashMap, requestCallBack);
	}

	// 根据环信名字获取会员资料
	public void GetContacformNamet(String[] username,
			RequestCallBack<String> requestCallBack) {

		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		int len = username.length;
		StringBuffer userNames = new StringBuffer();
		for (int i = 0; i < len; i++) {
			userNames.append(username[i] + ",");
		}
		userNames.substring(0, userNames.length() - 1);
		hashMap.put("hxname", userNames.toString());
		ComPost(AndroidConfig.NETHOST + AndroidConfig.getHXinfo, hashMap,
				requestCallBack);
	}

	// 政企通讯录移动分组成员
	public void updateEnterpriseGroupMember(String enterpriseId,
			String groupId, String userId,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("group_id", groupId);
		hashMap.put("user_id", userId);
		hashMap.put("enterprise_id", enterpriseId);
		ComPost(AndroidConfig.NETHOST
				+ AndroidConfig.EnterpriseGroupMemberUpdate, hashMap,
				requestCallBack);
	}

	public void delEnterpriseGroupMember(String target, String groupId, String enterprise_id,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("groupid", groupId);
		hashMap.put("target", target);
		hashMap.put("enterprise_id", enterprise_id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterpriseGroupDel,
				hashMap, requestCallBack);
	}

	// 政企通讯录短号、备注名修改
	public void updateEnterpriseShortPhone(String enterpriseId, String rName,
			String phone, RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterprise_id", enterpriseId);
		hashMap.put("remark_name", rName);
		hashMap.put("short_phone", phone);
		ComPost(AndroidConfig.NETHOST
				+ AndroidConfig.EnterpriseShortPhoneUpdate, hashMap,
				requestCallBack);
	}

	public void updateEnterpriseRemarkName(String enterpriseId, String rName,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterprise_id", enterpriseId);
		hashMap.put("remark_name", rName);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterpriseEditRemarkName,
				hashMap, requestCallBack);
	}

	// 政企通讯录查找
	public void searchEnterprise(String keyword,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("keyword", keyword);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterpriseSearch,
				hashMap, requestCallBack);
	}

	public void getContactInfoFromHX(String string,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("hxname", string);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.ContactInfoFromHX,
				hashMap, requestCallBack);
	}

	public void getContactInfoFromHXg(String string,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("hxname", string);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.ContactInfoFromHXg,
				hashMap, requestCallBack);
	}

	public void getTalkGroupMemberInfo(String username,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("huanxin_groupid", username);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.TalkContactInfoFromHX,
				hashMap, requestCallBack);
	}

	public void getContactInfoFromQR(String string, String username,
			RequestCallBack<String> requestCallBack) {

		if (!TextUtils.isEmpty(string)) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("key", "android");
			hashMap.put("qr_name", string);
			ComPost(AndroidConfig.NETHOST + AndroidConfig.ContactInfoFromQR,
					hashMap, requestCallBack);
		} else {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("key", "android");
			hashMap.put("hxname", username);
			ComPost(AndroidConfig.NETHOST + AndroidConfig.hxgcontactinfo,
					hashMap, requestCallBack);

		}

	}

	// 政企通讯录添加常用联系人
	public void addCommon(String enterpriseId, String memberId,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterprise_id", enterpriseId);
		hashMap.put("emember_id", memberId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.addCommon, hashMap,
				requestCallBack);
	}

	// 政企通讯录删除常用联系人
	public void delCommon(String enterpriseId, String memberId,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterprise_id", enterpriseId);
		hashMap.put("emember_id", memberId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.delCommon, hashMap,
				requestCallBack);
	}

	public void getGroupMemberListAdd(String groupId,
			RequestCallBack<String> mRequestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("group_id", groupId);
		hashMap.put("type", "1");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.getMemberListAdd,
				hashMap, mRequestCallBack);
	}

	// 获取好友联盟成员
	public void getFriendUnionMember(String friendUnionId,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("leagueid", friendUnionId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.friendUnionMember,
				hashMap, requestCallBack);
	}

	public void addGroupJoinMore(String ids, String groupId,
			RequestCallBack<String> mRequestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("huanxin_groupid", groupId);
		hashMap.put("member_id", ids);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.addGroupMember, hashMap,
				mRequestCallBack);

	}

	public void getInviteFriendUnionMember(String friendUnionId, String type,
			RequestCallBack<String> mRequestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("leagueid", friendUnionId);
		hashMap.put("type", type);
		ComPost(AndroidConfig.NETHOST
				+ AndroidConfig.getInviteFriendUnionMember, hashMap,
				mRequestCallBack);

	}

	public void inviteFriendUnionMember(String friendUnionId, String benbenId,
			String type, RequestCallBack<String> mRequestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("leagueid", friendUnionId);
		hashMap.put("benben_id", benbenId);
		hashMap.put("type", type);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.inviteFriendUnionMember,
				hashMap, mRequestCallBack);

	}

    public void inviteFriendUnionMemberMySelf(String friendUnionId, String benbenId,
                                        String type,String phone,String hand,String token, RequestCallBack<String> mRequestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("leagueid", friendUnionId);
        hashMap.put("benben_id", benbenId);
        hashMap.put("type", type);
        hashMap.put("phone", phone);
        hashMap.put("hand", hand);
        hashMap.put("token", token);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.inviteFriendUnionMember,
                hashMap, mRequestCallBack);

    }

	public void exitFriendUnionMember(String friendUnionId, String benbenId,
			String type, RequestCallBack<String> mRequestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("leagueid", friendUnionId);
		hashMap.put("member_id", benbenId);
		hashMap.put("type", type);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.exitFriendUnionMember,
				hashMap, mRequestCallBack);

	}

	//
	// // 登陆
	// public void Login(String account, String password,
	// RequestCallBack<String> callBack) {
	//
	// HashMap<String, String> hashMap = new HashMap<String, String>();
	//
	// hashMap.put("username", account);
	// hashMap.put("password", password);
	//
	// ComPost(AndroidConfig.NETHOST + AndroidConfig.LOGIN, hashMap, callBack);
	// }

	public void acceptFriendUN(String newId,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("new_id", newId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.agreeJoinFriendUN,
				hashMap, requestCallBack);
	}

    public void rejectLeague(String legid,String token,
                             RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("legid", legid);
        hashMap.put("token", token);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.rejectLeague, hashMap,
                requestCallBack);
    }

	public void getEnterpriseMemberList(String enterpriseId,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterprise_id", enterpriseId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.getEnterpriseInviteMember,
				hashMap, requestCallBack);
	}

	public void updateFriendUnionRemark(String friendUnionId, String remark,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("leagueid", friendUnionId);
		hashMap.put("remarkname", remark);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.updateFrienduUnionRemark,
				hashMap, requestCallBack);
	}

	public void editEnterpriseGroupMember(String enterpriseId, String groupId,
			String userId, RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterprise_id", enterpriseId);
		hashMap.put("group_id", groupId);
		hashMap.put("user_id", userId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.editEnterpriseGroupMember,
				hashMap, requestCallBack);
	}

	public void callPhone(String phoneNum,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("phone", phoneNum);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.CallPhone, hashMap,
				requestCallBack);
	}

	public void sendMsg(String mobile, RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("phone", mobile);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.SendMsg, hashMap,
				requestCallBack);
	}

	public void downloadSplashImg(int width, int height,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("width", width + "");
		hashMap.put("height", height + "");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.DownloadImg, hashMap,
				requestCallBack);

	}

	public void downloadSplashImg(String path,
			RequestCallBack<File> requestCallBack) {
		httpUtils.download(path, "temp", requestCallBack);

	}

	public void addContactsBySelf(String phone, String name, String groupId,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("phone", phone);
		hashMap.put("name", name);
		hashMap.put("group_id", groupId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.AddContactsBySelf,
				hashMap, requestCallBack);

	}

	public void broadCastinglist(RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.BroadCastingList,
				hashMap, requestCallBack);
	}

	public void deleteBroadCasting(String id, String string,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("id", id);
		hashMap.put("all", string);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.BroadCastingDelete,
				hashMap, requestCallBack);

	}

	public void updateFriendUnionInfo(String remarkName, String name,
			String unionId, String memberId,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("remarkname", remarkName);
		hashMap.put("name", name);
		hashMap.put("leagueid", unionId);
		hashMap.put("memberid", memberId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EditRemark, hashMap,
				requestCallBack);
	}

	public void getMyFriendUnionInfo(RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.GetMyFriendUnionInfo,
				hashMap, requestCallBack);
	}

	public void getFriendByArea(String[] addressId,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		if (addressId != null) {
			hashMap.put("p", addressId[0]);
			if (addressId[1] != null) {
				hashMap.put("c", addressId[1]);
			}

			if (addressId[2] != null) {
				hashMap.put("a", addressId[2]);
			}
		}

		ComPost(AndroidConfig.NETHOST + AndroidConfig.getFriendByArea, hashMap,
				requestCallBack);
	}

    public void getUnionMemberByArea(String[] addressId,
                                RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        if (addressId != null) {
            hashMap.put("p", addressId[0]);
            if (addressId[1] != null) {
                hashMap.put("c", addressId[1]);
            }

            if (addressId[2] != null) {
                hashMap.put("a", addressId[2]);
            }
        }

        ComPost(AndroidConfig.NETHOST + AndroidConfig.getUnionMemberByArea, hashMap,
                requestCallBack);
    }

	public void getMyInviteEnterpriseMember(String enterpriseId,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterprise_id", enterpriseId);
		ComPost(AndroidConfig.NETHOST
				+ AndroidConfig.GetMyInviteEnterpriseMember, hashMap,
				requestCallBack);
	}

	public void delMyAddEnterpriseMember(String enterpriseId, String id,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterprise_id", enterpriseId);
		hashMap.put("id", id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.DelMyAddEnterpriseMember,
				hashMap, requestCallBack);

	}

	public void getEnterpriseGroupMember(String enterpriseId, String groupId,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterpriseid", enterpriseId);
		hashMap.put("groupid", groupId);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.getEnterpriseGroupMember,
				hashMap, requestCallBack);
	}

	public void deleteFriend(int id, RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("id", id + "");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.DeleteFriend, hashMap,
				requestCallBack);
	}

	public void deleteSmallMake(int id, RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("id", id + "");
		ComPost(AndroidConfig.NETHOST + AndroidConfig.DeleteMySmallMake,
				hashMap, requestCallBack);
	}

	public void editFriendUN(String id, String nickName,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("leagueid", id);
		hashMap.put("nickname", nickName);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EditFriendUN, hashMap,
				requestCallBack);
	}

	public void getEnterpriseM(String id,
			RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("enterpriseid", id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.EnterpriseM, hashMap,
				requestCallBack);

	}

	public void storeClose(String id, RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("id", id);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.StoreClose, hashMap,
				requestCallBack);
	}
	
	public void newmatchlog(String phone, RequestCallBack<String> requestCallBack) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "android");
		hashMap.put("phone", phone);
		ComPost(AndroidConfig.NETHOST + AndroidConfig.newmatchlog, hashMap,
				requestCallBack);
	}

    public void sortGroup(String token,String sort,
                          RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("token", token);
        hashMap.put("sort", sort);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.sortGroup, hashMap,
                requestCallBack);
    }

    public void sortEnterprise(String token,String sort,
                          RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("token", token);
        hashMap.put("sort", sort);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.sortEnterprise, hashMap,
                requestCallBack);
    }


    public void newFriendInfo(int memberid,String token,
                          RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("token", token);
        hashMap.put("memberid", memberid+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.notification, hashMap,
                requestCallBack);
    }


    public void newUnionInfo(int legid,String token,
                              RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("token", token);
        hashMap.put("legid", legid+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.notification, hashMap,
                requestCallBack);
    }

    public void myLeaguein(String legid,
                             RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("legid", legid);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.myLeaguein, hashMap,
                requestCallBack);
    }

    public void leagueDetail(String legid,
                           RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("legid", legid);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.leaguedetail, hashMap,
                requestCallBack);
    }




    public void newGroupInfo(String hxusername,String token,
                              RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("token", token);
        hashMap.put("hxusername", hxusername);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.notification, hashMap,
                requestCallBack);
    }

    public void groupTransfer(String groupid,String to,String token,
                             RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("token", token);
        hashMap.put("groupid", groupid);
        hashMap.put("to", to);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.groupTransfer, hashMap,
                requestCallBack);
    }

    public void getTransferinfo(String transfer_id,String token,
                              RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("token", token);
        hashMap.put("transfer_id", transfer_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.getTransferinfo, hashMap,
                requestCallBack);
    }



    public void acceptTransfer(String transfer_id,String token,
                              RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("token", token);
        hashMap.put("transfer_id", transfer_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.acceptTransfer, hashMap,
                requestCallBack);
    }

    public void refuseTransfer(String transfer_id,String token,
                              RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("token", token);
        hashMap.put("transfer_id", transfer_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.refuseTransfer, hashMap,
                requestCallBack);
    }

    public void Remind(long friend_time,long creation_time,long friendrush,long creationrush,String token,
                               RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("friend_time", friend_time+"");
        hashMap.put("creation_time", creation_time+"");
        hashMap.put("friendrush", friendrush+"");
        hashMap.put("creationrush", creationrush+"");
        hashMap.put("token", token);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Remind, hashMap,
                requestCallBack);
    }

    //认证
    public void Setauth(final int type,final String real_name, final String idcard, final File front,final File back,final File licence,final String token,final RequestCallBack<String> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("key", "android");
                hashMap.put("type", type+"");
                hashMap.put("real_name", real_name);
                hashMap.put("idcard", idcard);
                hashMap.put("token", token);
                RequestParams params = new RequestParams();
                if(front!=null) {
                    addImageUpload(params, "front", front);
                }
                if(back!=null) {
                    addImageUpload(params, "back", back);
                }
                if(licence!=null) {
                    addImageUpload(params, "licence", licence);
                }
                Set<Entry<String, String>> entrySet = hashMap.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    params.addBodyParameter(entry.getKey(), entry.getValue());
                }
                httpUtils.send(HttpMethod.POST, AndroidConfig.NETHOST
                        + AndroidConfig.Setauth, params, callBack);
            }
        }).start();


    }

    public void Getauth(String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Getauth, hashMap,
                requestCallBack);
    }

    public void Promotionmanage(String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Promotionmanage, hashMap,
                requestCallBack);
    }


    public void Addpromotion(final String name,final String origion_price, final String promotion_price, final long valid_left,final long valid_right,
                             final String description,final File poster_st,final File poster_nd,final File poster_rd,final String token,final RequestCallBack<String> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("key", "android");
                hashMap.put("name", name);
                hashMap.put("origion_price", origion_price);
                hashMap.put("promotion_price", promotion_price);
                hashMap.put("valid_left", valid_left+"");
                hashMap.put("valid_right", valid_right+"");
                hashMap.put("description", description);
                hashMap.put("token", token);
                RequestParams params = new RequestParams();
                if(poster_st!=null) {
                    addImageUpload(params, "poster_st", poster_st);
                }
                if(poster_nd!=null) {
                    addImageUpload(params, "poster_nd", poster_nd);
                }
                if(poster_rd!=null) {
                    addImageUpload(params, "poster_rd", poster_rd);
                }
                Set<Entry<String, String>> entrySet = hashMap.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    params.addBodyParameter(entry.getKey(), entry.getValue());
                }
                httpUtils.send(HttpMethod.POST, AndroidConfig.NETHOST
                        + AndroidConfig.Addpromotion, params, callBack);
            }
        }).start();

    }

    public void Editpromotion(final int promotionid,final String name,final String origion_price, final String promotion_price, final long valid_left,final long valid_right,
                             final String description,final File poster_st,final File poster_nd,final File poster_rd,final String ids,final String token,final RequestCallBack<String> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("key", "android");
                hashMap.put("promotionid", promotionid+"");
                hashMap.put("name", name);
                hashMap.put("origion_price", origion_price);
                hashMap.put("promotion_price", promotion_price);
                hashMap.put("valid_left", valid_left+"");
                hashMap.put("valid_right", valid_right+"");
                hashMap.put("description", description);
                hashMap.put("ids", ids);
                hashMap.put("token", token);
                RequestParams params = new RequestParams();
                if(poster_st!=null) {
                    addImageUpload(params, "poster_st", poster_st);
                }
                if(poster_nd!=null) {
                    addImageUpload(params, "poster_nd", poster_nd);
                }
                if(poster_rd!=null) {
                    addImageUpload(params, "poster_rd", poster_rd);
                }
                Set<Entry<String, String>> entrySet = hashMap.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    params.addBodyParameter(entry.getKey(), entry.getValue());
                }
                httpUtils.send(HttpMethod.POST, AndroidConfig.NETHOST
                        + AndroidConfig.Editpromotion, params, callBack);
            }
        }).start();


    }

    public void Getpromotion(int promotionid,String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("promotionid", promotionid+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Getpromotion, hashMap,
                requestCallBack);
    }

    public void Togglepromotion(int promotionid,String is_close,String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("promotionid", promotionid+"");
        hashMap.put("is_close", is_close+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Togglepromotion, hashMap,
                requestCallBack);
    }


    public void Delpromotion(int promotionid,String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("promotionid", promotionid+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Delpromotion, hashMap,
                requestCallBack);
    }


    public void Activityalbum(String id,String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("id", id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Activityalbum, hashMap,
                requestCallBack);
    }

    public void Addalbum(final String title, final File poster_cover, final String[] images, final RequestCallBack<String> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("key", "android");
                hashMap.put("title", title);
                RequestParams params = new RequestParams();
                addImageUpload(params, "poster_cover",poster_cover);
                int length = images.length;
                for (int i = 1; i <= length; i++) {
                    addImageUpload(params, "pic" + i, new File(
                            images[i - 1]));
                }
                Set<Entry<String, String>> entrySet = hashMap.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    params.addBodyParameter(entry.getKey(), entry.getValue());
                }
                httpUtils.send(HttpMethod.POST, AndroidConfig.NETHOST
                        + AndroidConfig.Addalbum, params, callBack);
            }
        }).start();


    }

    public void Albumdetail(int activity_id,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("activity_id", activity_id+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Albumdetail, hashMap,
                requestCallBack);
    }


    public void Editalbum(final int activity_id,final String title, final File poster_cover, final RequestCallBack<String> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("key", "android");
                hashMap.put("activity_id", activity_id+"");
                hashMap.put("title", title);
                RequestParams params = new RequestParams();
                if(poster_cover!=null) {
                    addImageUpload(params, "poster_cover", poster_cover);
                }
                Set<Entry<String, String>> entrySet = hashMap.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    params.addBodyParameter(entry.getKey(), entry.getValue());
                }
                httpUtils.send(HttpMethod.POST, AndroidConfig.NETHOST
                        + AndroidConfig.Editalbum, params, callBack);
            }
        }).start();
    }


    public void Delalbum(int activity_id,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("activity_id", activity_id+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Delalbum, hashMap,
                requestCallBack);
    }

    public void Delphoto(int activity_id,String picid,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("activity_id", activity_id+"");
        hashMap.put("picid", picid);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Delphoto, hashMap,
                requestCallBack);
    }

    public void Addphoto(final int activity_id,final String[] images, final RequestCallBack<String> requestCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("key", "android");
                hashMap.put("activity_id", activity_id+"");
                RequestParams params = new RequestParams();
                int length = images.length;
                for (int i = 1; i <= length; i++) {
                    addImageUpload(params, "pic" + i, new File(
                            images[i - 1]));
                }
                Set<Entry<String, String>> entrySet = hashMap.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    params.addBodyParameter(entry.getKey(), entry.getValue());
                }
                httpUtils.send(HttpMethod.POST, AndroidConfig.NETHOST
                        + AndroidConfig.Addphoto, params, requestCallBack);
            }
        }).start();
    }



    public void GroupManage(String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.GroupManage, hashMap,
                requestCallBack);
    }

    public void Paymethods(String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("token", token);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Paymethods, hashMap,
                requestCallBack);
    }

    public void Addgroupbuy(final String name,final String origion_price, final String promotion_price, final long valid_left,final long valid_right,
                             final String description,final File[] files,final String pay_ids,final String shipping_fee,final String mustknow,final String model,final String token,final RequestCallBack<String> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("key", "android");
                hashMap.put("name", name);
                hashMap.put("origion_price", origion_price);
                hashMap.put("promotion_price", promotion_price);
                hashMap.put("valid_left", valid_left+"");
                hashMap.put("valid_right", valid_right+"");
                hashMap.put("description", description);
                hashMap.put("pay_ids", pay_ids);
                hashMap.put("shipping_fee", shipping_fee);
                hashMap.put("mustknow", mustknow);
                hashMap.put("model", model);
                hashMap.put("token", token);
                RequestParams params = new RequestParams();
                for(int i=0;i<files.length;i++){
                    if(files[i]!=null){
                        addImageUpload(params, "pic"+(i+1), files[i]);
                    }
                }
                Set<Entry<String, String>> entrySet = hashMap.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    params.addBodyParameter(entry.getKey(), entry.getValue());
                }
                httpUtils.send(HttpMethod.POST, AndroidConfig.NETHOST
                        + AndroidConfig.Addgroupbuy, params, callBack);
            }
        }).start();

    }

    public void Getgroupbuy(int promotionid,String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("promotionid", promotionid+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Getgroupbuy, hashMap,
                requestCallBack);
    }


    public void Togglegroupbuy(int promotionid,String is_close,String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("promotionid", promotionid+"");
        hashMap.put("is_close", is_close+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Togglegroupbuy, hashMap,
                requestCallBack);
    }


    public void Delgroupbuy(int promotionid,String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("promotionid", promotionid+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Delgroupbuy, hashMap,
                requestCallBack);
    }

    public void Editgroupbuy(final int promotionid,final String origion_price, final String promotion_price, final long valid_left,final long valid_right,
                            final String description,final String ids,final File[] files,final String pay_ids,final String shipping_fee,final String mustknow,final String model,final String token,final RequestCallBack<String> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("key", "android");
                hashMap.put("promotionid", promotionid+"");
                hashMap.put("origion_price", origion_price);
                hashMap.put("promotion_price", promotion_price);
                hashMap.put("valid_left", valid_left+"");
                hashMap.put("valid_right", valid_right+"");
                hashMap.put("description", description);
                hashMap.put("ids", ids);
                hashMap.put("pay_ids", pay_ids);
                hashMap.put("shipping_fee", shipping_fee);
                hashMap.put("mustknow", mustknow);
                hashMap.put("model", model);
                hashMap.put("token", token);
                RequestParams params = new RequestParams();
                for(int i=0;i<files.length;i++){
                    if(files[i]!=null){
                        addImageUpload(params, "pic"+(i+1), files[i]);
                    }
                }
                Set<Entry<String, String>> entrySet = hashMap.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    params.addBodyParameter(entry.getKey(), entry.getValue());
                }
                httpUtils.send(HttpMethod.POST, AndroidConfig.NETHOST
                        + AndroidConfig.Editgroupbuy, params, callBack);
            }
        }).start();

    }

    public void Alldeparts(String trainid,String longitude,String latitude,String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("trainid", trainid);
        hashMap.put("longitude",longitude);
        hashMap.put("latitude", latitude);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Alldeparts, hashMap,
                requestCallBack);
    }

    public void GroupDetail(int promotionid,String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("promotionid", promotionid+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.GroupDetail, hashMap,
                requestCallBack);
    }

    public void Addaddress(String consignee,String[] areas,String address,String zipcode,String mobile,int is_default,String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("consignee", consignee);
        if (areas[0] != null)
            hashMap.put("province", areas[0]);
        if (areas[1] != null)
            hashMap.put("city", areas[1]);
        if (areas[2] != null)
            hashMap.put("district", areas[2]);
        if (areas[3] != null)
            hashMap.put("street", areas[3]);

        hashMap.put("address", address);
//        hashMap.put("zipcode", zipcode);
        hashMap.put("mobile", mobile);
        hashMap.put("is_default", is_default+"");
        hashMap.put("token", token);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Addaddress, hashMap,
                requestCallBack);
    }

    public void Addressdetail(RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Addressdetail, hashMap,
                requestCallBack);
    }

    public void Deladdress(int address_id,String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("address_id", address_id+"");
        hashMap.put("token", token);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Deladdress, hashMap,
                requestCallBack);
    }

    public void Editaddress(int address_id,String consignee,String[] areas,String address,String zipcode,String mobile,int is_default,String token,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("address_id", address_id+"");
        hashMap.put("consignee", consignee);
        if (areas[0] != null)
            hashMap.put("province", areas[0]);
        if (areas[1] != null)
            hashMap.put("city", areas[1]);
        if (areas[2] != null)
            hashMap.put("district", areas[2]);
        if (areas[3] != null)
            hashMap.put("street", areas[3]);

        hashMap.put("address", address);
//        hashMap.put("zipcode", zipcode);
        hashMap.put("mobile", mobile);
        hashMap.put("is_default", is_default+"");
        hashMap.put("token", token);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Editaddress, hashMap,
                requestCallBack);
    }

    public void DefaultEditaddress(int address_id,int is_default,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("address_id", address_id+"");
        hashMap.put("is_default", is_default+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Editaddress, hashMap,
                requestCallBack);
    }

    public void Defaultaddress(RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Defaultaddress, hashMap,
                requestCallBack);
    }

    public void Addorder(int promotion_id,String consignee,String province,String city,String district,String street,
                         String address,String mobile,int pay_id,String pay_name,double goods_amount,String shipping_fee,double order_amount,int goods_number,int extension_code,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("promotion_id", promotion_id+"");
        hashMap.put("consignee", consignee);
        hashMap.put("province", province);
        hashMap.put("city", city);
        hashMap.put("district", district);
        hashMap.put("street", street);
        hashMap.put("address", address);
        hashMap.put("mobile", mobile);
        hashMap.put("pay_id", pay_id+"");
        hashMap.put("pay_name", pay_name);
        hashMap.put("goods_amount", goods_amount+"");
        hashMap.put("shipping_fee", shipping_fee);
        hashMap.put("order_amount", order_amount+"");
        hashMap.put("goods_number", goods_number+"");
        hashMap.put("extension_code", extension_code+"");


        ComPost(AndroidConfig.NETHOST + AndroidConfig.Addorder, hashMap,
                requestCallBack);
    }


    public void Orderlist(int page,String shipping_status,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("page", page+"");
        hashMap.put("shipping_status", shipping_status);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Orderlist, hashMap,
                requestCallBack);
    }

    public void Delorder(String order_id,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("order_id", order_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Delorder, hashMap,
                requestCallBack);
    }

    public void Orderdetail(String order_id,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("order_id", order_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Orderdetail, hashMap,
                requestCallBack);
    }

    public void Backorder(String order_id,String train_id,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("order_id", order_id);
        hashMap.put("train_id", train_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Backorder, hashMap,
                requestCallBack);
    }

    public void Checkorder(String order_sn,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("order_sn", order_sn);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Checkorder, hashMap,
                requestCallBack);
    }

    public void Sureget(String order_id,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("order_id", order_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Sureget, hashMap,
                requestCallBack);
    }

    public void Extendtime(String order_id,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("order_id", order_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Extendtime, hashMap,
                requestCallBack);
    }

    public void Sureconsume(String order_id,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("order_id", order_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Sureconsume, hashMap,
                requestCallBack);
    }

    public void Refuse(String order_id,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("order_id", order_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Refuse, hashMap,
                requestCallBack);
    }

    public void Storderlist(int page,String status,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("page", page+"");
        hashMap.put("status", status);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Storderlist, hashMap,
                requestCallBack);
    }

    public void Storderdetail(String order_id,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("order_id", order_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Storderdetail, hashMap,
                requestCallBack);
    }

    public void Manualsend(String order_id,String sname,String sn,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("order_id", order_id);
        hashMap.put("sname", sname);
        hashMap.put("sn", sn);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Manualsend, hashMap,
                requestCallBack);
    }

    public void Ordercomment(int promotion_id,String order_id,int comment_type,String content,int comment_rank,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("promotion_id", promotion_id+"");
        hashMap.put("order_id", order_id);
        hashMap.put("comment_type", comment_type+"");
        hashMap.put("content", content);
        hashMap.put("comment_rank", comment_rank+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Ordercomment, hashMap,
                requestCallBack);
    }

    public void Stordercomment(int promotion_id,String order_id,int comment_type,String content,int comment_rank,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("promotion_id", promotion_id+"");
        hashMap.put("order_id", order_id);
        hashMap.put("comment_type", comment_type+"");
        hashMap.put("content", content);
        hashMap.put("comment_rank", comment_rank+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Stordercomment, hashMap,
                requestCallBack);
    }

    public void OrderCommentList(String order_id,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("order_id", order_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.OrderCommentList, hashMap,
                requestCallBack);
    }

    public void Reply(String order_id,int comment_id,int comment_type,String content,int promotion_id,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("promotion_id", promotion_id+"");
        hashMap.put("order_id", order_id);
        hashMap.put("comment_type", comment_type+"");
        hashMap.put("content", content);
        hashMap.put("comment_id", comment_id+"");
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Reply, hashMap,
                requestCallBack);
    }

    public void Agreeback(String order_id,RequestCallBack<String> requestCallBack) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "android");
        hashMap.put("order_id", order_id);
        ComPost(AndroidConfig.NETHOST + AndroidConfig.Agreeback, hashMap,
                requestCallBack);
    }
}
