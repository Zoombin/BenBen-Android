package com.xunao.benben.config;

import android.content.Intent;

public class AndroidConfig {

	public static final String SHARENAME = "BenBen";

	public static final String NETHOST ="http://112.124.101.177:81/index.php/v2";
    public static final String NETHOST2 ="http://112.124.101.177:81/index.php/v2";
    public static final String NETHOST3 ="http://112.124.101.177:81/index.php/v1";

//    public static final String NETHOST = "http://112.124.101.177/index.php/v2";
//	public static final String NETHOST2 ="http://112.124.101.177/index.php/v2";
//    public static final String NETHOST3 ="http://112.124.101.177/index.php/v1";

    // 商户PID
    public static final String PARTNER = "2088121516774084";
    // 商户收款账号
    public static final String SELLER = "18958171188@189.cn";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBALSBpjsJM9ns2oln" +
            "UK29bqZc5kM07NU5dgu7kcSkBku3wwVYrC/Yjcwcps4DTnHQtpbJJy90G2X3CvUX" +
            "JLchB3TSl1uGKxrYgU0T4mpRsTkXWuAW776N1FsTjCVHQSUVmkO44IFxf0gzJekJ" +
            "NMnsFEpcpqnx12NTkplZMrFNYdZLAgMBAAECgYEAsqcfP478ItSp9xSqZUr4GPAZ" +
            "dqsLPH6Ct1oOC/HLyWU3QDNjOHe84Cf7cCsVmNBZ5yNwBSeFdoXr8mfVjurB1NQK" +
            "7dNXk8uxF412JeTQDr1ejiBrfg1qsKiz8Px3f9m7gVXgDxS+CYRDn9XdDNdwT8oM" +
            "T2TYliO2NGQ+zvEaQAECQQDiC6lBQiNHGXcanSgsNocQuuCkVQwHyJskKo3bqnog" +
            "+CASo4dLPyrwKX2N6QcpMDKe8hQxPbpyzn5U8KbnII1LAkEAzG0gJxVEuKoAah/M" +
            "dZcI3SXTP5s1ZKtk7Rb53Zl1+FByIuLIbvHReb8GN/40eOSyGIbkXc5YMKl9CyDg" +
            "J3M7AQJBALqrmJ2cqZdojzQ6PlesvANz4Fm1JhczcfL+9WFHOk4JuirKQstAIgxs" +
            "pxPPMauw8szR6xzy9gsjPa6Vga9y8VsCQQCyg8EGIC6Iy/vcsLQNI71b0UIuU01H" +
            "Adz6pYvBFdfM5gMlr8C0EXuJw2Sc1OHhiGR1wqX9vMmKhRdq9mSITroBAkEAq5Qh" +
            "jZWutGw7zdrTdc+q9/nRG2DgFmpcntcXRmr21Wk4Wiqn5zzLXhhZSAaOULOtowQ5" +
            "6YacA4Se5ujdF47gUg==";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "";
	 
	// 检查更新

	// 敏感词
//	public static final String minganci = "阿伯|阿爹|阿公|阿姑|阿舅|阿娘|八爸|八表弟|八表哥|八表姐|八表妹|八伯|八弟|八爹|八哥|八姑|八姑夫|八姑妈|八姑丈|八姐|八舅|八舅公|八舅妈|八妹|八女婿|八嫂|八婶|八叔|八叔公|八叔婆|八外公|八外婆|八媳妇|八爷|八爷爷|八姨|爸|爸爸|表伯|表弟|表哥|表姑|表姐|表妹|表叔|伯|伯伯|大爸|大表弟|大表哥|大表姐|大表妹|大伯|大弟|大爹|大哥|大姑|大姑夫|大姑妈|大姑丈|大姐|大舅|大舅公|大舅妈|大妈|大妹|大女婿|大嫂|大婶|大叔|大叔公|大叔婆|大外公|大外婆|大媳妇|大爷|大爷爷|大姨|弟|弟弟|弟妹|弟媳|爹|爹爹|儿子|二表弟|二表哥|二表姐|二表妹|二伯|二弟|二爹|二哥|二姑|二姑夫|二姑妈|二姑丈|二姐|二舅|二舅公|二舅妈|二妹|二女婿|二嫂|二婶|二叔|二叔公|二叔婆|二外公|二外婆|二媳妇|二爷|二爷爷|二姨|父亲|哥|哥哥|公|公公|姑|姑夫|姑公|姑妈|姑婆|姑爷|姑丈|姐|姐姐|舅|舅父|舅公|舅舅|舅妈|舅姆|舅婆|舅嫂|老爸|老伯|老公|老舅|老妈|老婆老婶|老叔|老姨|六爸|六表弟|六表哥|六表姐|六表妹|六伯|六弟|六爹|六哥|六姑|六姑夫|六姑妈|六姑丈|六姐|六舅|六舅公|六舅妈|六妹|六女婿|六嫂|六婶|六叔|六叔公|六叔婆|六外公|六外婆|六媳妇|六爷|六爷爷|六姨|妈|妈妈|妹|妹妹|女儿|女婿|婆婆|七爸|七表弟|七表哥|七表姐|七表妹|七伯|七弟|七爹|七哥|七姑|七姑夫|七姑妈|七姑丈|七姐|七舅|七舅公|七舅妈|七妹|七女婿|七嫂|七婶|七叔|七叔公|七叔婆|七外公|七外婆|七媳妇|七爷|七爷爷|七姨|亲家公|亲家母|三爸|三表弟|三表哥|三表姐|三表妹|三伯|三弟|三爹|三哥|三姑|三姑夫|三姑妈|三姑丈|三姐|三舅|三舅公|三舅妈|三妹|三女婿|三嫂|三婶|三叔|三叔公|三叔婆|三外公|三外婆|三媳妇|三爷|三爷爷|三姨|嫂|嫂嫂|婶娘|婶婶|叔|叔公|叔婆|叔叔|四爸|四表弟|四表哥|四表姐|四表妹|四伯|四弟|四爹|四哥|四姑|四姑夫|四姑妈|四姑丈|四姐|四舅|四舅公|四舅妈|四妹|四女婿|四嫂|四婶|四叔|四叔公|四叔婆|四外公|四外婆|四媳妇|四爷|四爷爷|四姨|太公|太婆|太爷爷|太祖公|太祖婆|太祖爷爷|堂伯|堂弟|堂哥|堂姑|堂姐|堂妹|堂叔|外伯公|外伯婆|外公|外姑公|外姑奶|外姑婆|外婆|外叔公|外叔婆|外孙|外孙女|五爸|五表弟|五表哥|五表姐|五表妹|五伯|五弟|五爹|五哥|五姑|五姑夫|五姑妈|五姑丈|五姐|五舅|五舅公|五舅妈|五妹|五女婿|五嫂|五婶|五叔|五叔公|五叔婆|五外公|五外婆|五媳妇|五爷|五爷爷|五姨|媳妇|小爸|小表弟|小表哥|小表姐|小表妹|小伯|小弟|小爹|小哥|小姑|小姑夫|小姑妈|小姑丈|小姐|小舅|小舅公|小舅妈|小妹|小女婿|小嫂|小婶|小叔|小叔公|小叔婆|小外公|小外婆|小媳妇|小爷|小爷爷|小姨|爷|爷爷|姨|姨公|姨娘|姨婆|姨丈|岳父|岳母|丈母|丈人|侄子|祖公|阿姨|CEO|CMO|CTO|安全员|保管员|采购员|厂长|出纳|调度员|董事长|翻译|分析师|副厂长|副董事长|副课长|副主任|副组长|工程师|顾问|管理员|核算员|护士|健身教练|经纪人|经理|警卫|客户代表|课长|美工|秘书|培训师|培训专员|前台|设计师|审计员|收银员|司机|统计|销售代表|协理|质检员|主编|主管|主任|专家|专员|专员|咨询师|总裁|总监|总经理|总助|组长|班长|班主任|保安|保管员|报务员|编辑|编剧|播音员|博士|博士后|船长|大副|大管轮|导演|电机员|二副|二管轮|翻译|飞行员|辅导员|副教授|副校长|副研|副院长|干部|干事|公证员|馆员|管理员|管理员|护士|会长|会计|会计师|机械员|记者|技师|技术员|讲师|教练|教师|教授|教员|经济师|老师|领航员|律师|轮机长|美术师|美术员|门卫|农艺师|三副|三管轮|设计师|审计|审计师|实验师|实验员|书记|水电工|硕士|通信员|统计|统计师|校长|校对|校医|学士|研究员|演员|演奏员|药师|医师|引航员|院长|指挥|主任|助教|助理|助理编辑|组长|作曲|总书记|书记|主席|副主席|常委|委员|纪委|总理|副总理|副院长|院长|检察长|干部|干事|部长|副部长|省长|副省长|市长|副市长|局长|副局长|司长|副司长|厅长|副厅长|处长|副处长|县长|副县长|巡视员|调研员|科长|副科长|乡长|副乡长|主任|副主任|科员|副科|办事员|镇长|副镇长|村长|副村长|军委主席|军委副主席|军委委员|总参谋长|参谋长|总参|司令|副司令|军长|副军长|师长|副师长|旅长|副旅长|团长|副团长|团副|营长|副营长|营副|连长|副连长|连副|排长|副排长|排副|上将|中将|少将|大校|上校|中校|少校|上尉|中尉|少尉|士官|列兵";
	public static final String minganci = "阿伯|阿爹|阿公|阿姑|阿舅|阿娘|八爸|八表弟|八表哥|八表姐|八表妹|八伯|八弟|八爹|八哥|八姑|八姑夫|八姑妈|八姑丈|八姐|八舅|八舅公|八舅妈|八妹|八女婿|八嫂|八婶|八叔|八叔公|八叔婆|八外公|八外婆|八媳妇|八爷|八爷爷|八姨|爸|爸爸|表伯|表弟|表哥|表姑|表姐|表妹|表叔|伯|伯伯|大爸|大表弟|大表哥|大表姐|大表妹|大伯|大弟|大爹|大哥|大姑|大姑夫|大姑妈|大姑丈|大姐|大舅|大舅公|大舅妈|大妈|大妹|大女婿|大嫂|大婶|大叔|大叔公|大叔婆|大外公|大外婆|大媳妇|大爷|大爷爷|大姨|弟|弟弟|弟妹|弟媳|爹|爹爹|儿子|二表弟|二表哥|二表姐|二表妹|二伯|二弟|二爹|二哥|二姑|二姑夫|二姑妈|二姑丈|二姐|二舅|二舅公|二舅妈|二妹|二女婿|二嫂|二婶|二叔|二叔公|二叔婆|二外公|二外婆|二媳妇|二爷|二爷爷|二姨|父亲|哥|哥哥|公|公公|姑|姑夫|姑公|姑妈|姑婆|姑爷|姑丈|姐|姐姐|舅|舅父|舅公|舅舅|舅妈|舅姆|舅婆|舅嫂|老爸|老伯|老公|老舅|老妈|老婆老婶|老叔|老姨|六爸|六表弟|六表哥|六表姐|六表妹|六伯|六弟|六爹|六哥|六姑|六姑夫|六姑妈|六姑丈|六姐|六舅|六舅公|六舅妈|六妹|六女婿|六嫂|六婶|六叔|六叔公|六叔婆|六外公|六外婆|六媳妇|六爷|六爷爷|六姨|妈|妈妈|妹|妹妹|女儿|女婿|婆婆|七爸|七表弟|七表哥|七表姐|七表妹|七伯|七弟|七爹|七哥|七姑|七姑夫|七姑妈|七姑丈|七姐|七舅|七舅公|七舅妈|七妹|七女婿|七嫂|七婶|七叔|七叔公|七叔婆|七外公|七外婆|七媳妇|七爷|七爷爷|七姨|亲家公|亲家母|三爸|三表弟|三表哥|三表姐|三表妹|三伯|三弟|三爹|三哥|三姑|三姑夫|三姑妈|三姑丈|三姐|三舅|三舅公|三舅妈|三妹|三女婿|三嫂|三婶|三叔|三叔公|三叔婆|三外公|三外婆|三媳妇|三爷|三爷爷|三姨|嫂|嫂嫂|婶娘|婶婶|叔|叔公|叔婆|叔叔|四爸|四表弟|四表哥|四表姐|四表妹|四伯|四弟|四爹|四哥|四姑|四姑夫|四姑妈|四姑丈|四姐|四舅|四舅公|四舅妈|四妹|四女婿|四嫂|四婶|四叔|四叔公|四叔婆|四外公|四外婆|四媳妇|四爷|四爷爷|四姨|太公|太婆|太爷爷|太祖公|太祖婆|太祖爷爷|堂伯|堂弟|堂哥|堂姑|堂姐|堂妹|堂叔|外伯公|外伯婆|外公|外姑公|外姑奶|外姑婆|外婆|外叔公|外叔婆|外孙|外孙女|五爸|五表弟|五表哥|五表姐|五表妹|五伯|五弟|五爹|五哥|五姑|五姑夫|五姑妈|五姑丈|五姐|五舅|五舅公|五舅妈|五妹|五女婿|五嫂|五婶|五叔|五叔公|五叔婆|五外公|五外婆|五媳妇|五爷|五爷爷|五姨|媳妇|小爸|小表弟|小表哥|小表姐|小表妹|小伯|小弟|小爹|小哥|小姑|小姑夫|小姑妈|小姑丈|小姐|小舅|小舅公|小舅妈|小妹|小女婿|小嫂|小婶|小叔|小叔公|小叔婆|小外公|小外婆|小媳妇|小爷|小爷爷|小姨|爷|爷爷|姨|姨公|姨娘|姨婆|姨丈|岳父|岳母|丈母|丈人|侄子|祖公|阿姨|CEO|CMO|CTO|安全员|保管员|采购员|厂长|出纳|调度员|董事长|翻译|分析师|副厂长|副董事长|副课长|副主任|副组长|工程师|顾问|管理员|核算员|健身教练|经纪人|经理|警卫|客户代表|课长|美工|秘书|培训师|培训专员|前台|设计师|审计员|收银员|司机|销售代表|协理|质检员|主编|主管|专家|专员|咨询师|总裁|总监|总经理|总助|组长|班长|班主任|保安|报务员|编辑|编剧|播音员|博士|博士后|船长|大副|大管轮|导演|电机员|二副|二管轮|飞行员|辅导员|副教授|副校长|副研|干部|干事|公证员|馆员|护士|会长|会计|会计师|机械员|记者|技师|技术员|讲师|教练|教师|教授|教员|经济师|老师|领航员|律师|轮机长|美术师|美术员|门卫|农艺师|三副|三管轮|审计|审计师|实验师|实验员|水电工|硕士|通信员|统计|统计师|校长|校对|校医|学士|研究员|演员|演奏员|药师|医师|引航员|院长|指挥|助教|助理|助理编辑|作曲|总书记|书记|主席|副主席|常委|委员|纪委|总理|副总理|副院长|检察长|部长|副部长|省长|副省长|市长|副市长|局长|副局长|司长|副司长|厅长|副厅长|处长|副处长|县长|副县长|巡视员|调研员|科长|副科长|乡长|副乡长|主任|科员|副科|办事员|镇长|副镇长|村长|副村长|军委主席|军委副主席|军委委员|总参谋长|参谋长|总参|司令|副司令|军长|副军长|师长|副师长|旅长|副旅长|团长|副团长|团副|营长|副营长|营副|连长|副连长|连副|排长|副排长|排副|上将|中将|少将|大校|上校|中校|少校|上尉|中尉|少尉|士官|列兵";
	
	public static final String newmatchlog = "/contact/newmatchlog/";
	// 查找用户 搜索
	public static final String buySearchFriend = "/user/search/";
    // 好友推荐
    public static final String recommendFriends = "/user/recommendFriends/";
    // 添加推荐好友
    public static final String addRecommendFriend = "/user/addRecommendFriend/";

	public static final String CHECKVERSION = "/version/getVersion";
	// 自动登录
	public static final String AutoLogin = "/user/autologin/";

	// 拨打电话提交
	public static final String CallPhone = "/user/memberdialog/";
	// 发短信提交
	public static final String SendMsg = "/user/invitelog/";
	// 登录展示页面
	public static final String DownloadImg = "/splash/getSplash/";
    // 获取提醒
    public static final String Remind = "/splash/Remind/";

	// 手机验证码发送
	public static final String PhoneCode = "/user/sendcode";

	public static final String PhoneForget = "/user/fogpwd/";

	// 注册会员信息
	public static final String UserRegister = "/user/register";
	// 会员登录
	public static final String UserLogin = "/user/login";

	// 会员信息修改
	public static final String UpdateUser = "/user/update/";

	// 更换头像
	public static final String UpdateFace = "/user/updateavatar/";

	// 修改密码
	public static final String UpdatePwd = "/user/changepwd/";

	// 轻松一刻
	public static final String GetHappy = "/happy/newHappynext/";

	// 轻松一刻点赞
	public static final String SetHappyGoodOrBad = "/happy/goodOrBad/";

	// 开关直通车
	public static final String StoreClose = "/store/close/";

	// 自动登录
	public static final String UserLoginAuto = "/user/autologin";

	// 通讯录匹配
	public static final String PhoneMatch = "/contact/match";

	// 通讯录同步
	public static final String ContactsSynchro = "/contact/newmatch";
    public static final String Updatematch = "/contact/Updatematch";


	// 修改通讯录成员名称
	public static final String UpdateContactsName = "/contact/editname/";

	// 添加分组
	public static final String AddPacket = "/contact/addgroup";

	// 编辑分组 修改分组名
	public static final String EditPacket = "/contact/editgroup/";

	// 删除分组
	public static final String DeletePacket = "/contact/deletegroup/";

    // 新增联系人
    public static final String Addcontact = "/contact/Addcontact";

	// 获取朋友圈
	public static final String GetFriend = "/friend/list/";
	// 删除朋友圈
	public static final String DeleteFriend = "/friend/deleteItem/";

	// 获取我的动态
	public static final String GetMyDynamic = "/friend/mylist/";

	// 我的获取微创作
	public static final String GetMySmallMake = "/creation/mylist/";
	// 删除微创作
	public static final String DeleteMySmallMake = "/creation/deleteItem/";

	// 获取微创作
	public static final String GetSmallMake = "/creation/list/";

	// 获取通讯录
	public static final String GetContact = "/contact/contactinfo";

	// 编辑分组成员
	public static final String EditPacketInfo = "/contact/editmember/";

	// 获得群组信息
	public static final String GetTalkGroup = "/group/mygroup";
    // 群组转让
    public static final String groupTransfer = "/group/grouptransfer";
    // 群组转让详情
    public static final String getTransferinfo = "/group/gettransferinfo";

    // 接受群组转让
    public static final String acceptTransfer = "/group/accepttransfer";
    // 拒绝群组转让
    public static final String refuseTransfer = "/group/refusetransfer";


	// 投诉建议
	public static final String DoComplain = "/complain/postComplain/";
	// 我要买申诉
	public static final String DoBuyComplain = "/buy/report";

	public static final String sendPublic = "/news/broadcasting/";

	// 获取地区
	public static final String GetAddress = "/area/getArea/";

	// 获取行业
	public static final String GetIndustry = "/industry/getIndustry/";

	// 创建组群
	public static final String AddGroup = "/group/add";

	// 证企通讯录
	public static final String GetEnterpriseList = "/enterprise/myenterprisein/";

	// 添加政企通讯录
	public static final String AddEnterpriseList = "/enterprise/add/";

	// 修改政企通讯录
	public static final String UpdateEnterpriseList = "/enterprise/edit/";

	// 政企通讯录详细
	public static final String EnterpriseDetail = "/enterprise/detail/";

	// /政企通讯录成员
	public static final String EnterpriseMember = "/enterprise/member";

	// /政企通讯录成员
	public static final String EnterpriseAllMember = "/enterprise/allmember";

	// 邀请加入政企通讯录
	public static final String EnterpriseInvite = "/enterprise/joinmember";

	public static final String EnterpriseVInvite = "/enterprise/newjoin/";

	// 主动加入政企
	public static final String EnterpriseAdd = "/enterprise/joinvirtual";

	// 政企通讯录分组管理
	public static final String EnterpriseGroup = "/enterprise/egroup/";

	// 政企通讯录新建分组
	public static final String EnterpriseGroupAdd = "/enterprise/addgroup/";

	// 政企通讯录修改分组
	public static final String EnterpriseGroupUpdate = "/enterprise/editgroup/";
	// 根据环信名获取资料
	public static final String getHXinfo = "/user/hxmemberinfo/";
	// public static final String getHXinfo = "/v1/user/hxcontactinfo";

	public static final String EnterpriseGroupMemberUpdate = "/enterprise/editmember/";

	public static final String EnterpriseGroupDel = "/enterprise/deletegroup/";

	// 政企通讯录短号、备注名修改
	public static final String EnterpriseShortPhoneUpdate = "/enterprise/editphone/";

	public static final String EnterpriseEditRemarkName = "/enterprise/editRemarkName/";

	// 政企通讯录查找
	public static final String EnterpriseSearch = "/enterprise/search/";

	// 通过环信名得到联系人信息
	public static final String ContactInfoFromHX = "/user/hxcontactinfo/";
	// 通过环信名得到联系人信息
	public static final String ContactInfoFromHXg = "/user/hxgcontactinfo/";
	// 通过群组groupID获取群组所有人信息
	public static final String TalkContactInfoFromHX = "/group/memberh/";

	public static final String ContactInfoFromQR = "/user/qrcontactinfo/";
	public static final String hxgcontactinfo = "/user/hxgcontactinfo/";

	// 政企通讯录添加常用联系人
	public static final String addCommon = "/enterprise/common/";

	// 政企通讯录删除常用联系人
	public static final String delCommon = "/enterprise/cancelcommon/";

	public static final String friendUnionMember = "/league/allmember/";

	// 退出政企通讯录
	public static final String EnterpriseExit = "/enterprise/quit/";

	// 退出政企通讯录
	public static final String SearchEnterpriseMember = "/enterprise/membersearch/";

	// 号码直通车
	public static final String GetStoreList = "/store/search/";

	// 我的号码直通车
	public static final String MyStoreList = "/store/mydetail/";

	// 我的号码直通车资料完善
	public static final String PerfectMyStore = "/store/info/";

	public static final String AddContactsBySelf = "/contact/addcontact";

	// 修改我的号码直通车
	public static final String UpdateMyStoreList = "/store/add/";

	public static final String updateFrienduUnionRemark = "/league/editremark/";

	public static final String editEnterpriseGroupMember = "/enterprise/editmember/";

	// 朋友圈点赞
	public static final String clickGood = "/friend/laud/";
	// 朋友圈取消点赞
	public static final String cancelClickGood = "/friend/cancellaud";
	// 微创作点赞
	public static final String clickSmallGood = "/creation/laud/";
	// 微创作取消点赞
	public static final String cancelSmallClickGood = "/creation/cancellaud/";
	// 朋友圈评论
	public static final String comment = "/friend/comment";
	// 朋友圈发布
	public static final String publicF = "/friend/create";
	// 微创作发布
	public static final String publicS = "/creation/create";

	// 微创作评论
	public static final String smallcomment = "/creation/comment";

	// 号码直通车详情
	public static final String GetStoreListDetail = "/store/detail/";
    // 号码直通车详情
    public static final String GetOwnerDetail = "/store/ownerdetail/";

	// 关注作者
	public static final String Attention = "/creation/attention/";
	// 获取关注
	public static final String getAttention = "/creation/myattention/";
	// 取消关注
	public static final String cancleAttention = "/creation/cancelattention/";

	// 我要买列表
	public static final String getBuyInfo = "/buy/list/";

	public static final String getMyBuyInfo = "/buy/mylist/";

	// 我要买 报价
	public static final String submitPrice = "/buy/quoted/";
	// 我要买 搜索
	public static final String buySearch = "/buy/search/";

	// 我要买 发布
	public static final String publicBuyInfo = "/buy/create";
	// 我要买 详情
	public static final String publicBuyInfoContent = "/buy/detail/";
	// 我要买 关闭
	public static final String closeBuy = "/buy/close";
	// 我要买 接受报价
	public static final String acceptBuy = "/buy/accept";
	// 联系人编辑 删除联系人号码
	public static final String deleteNumber = "/contact/delphone/";
	// 联系人编辑 添加联系人号码
	public static final String addNumber = "/contact/addphone/";
	// 联系人编辑 删除联系人
	public static final String deleteContact = "/contact/delcontact/";
    // 删除奔犇号
    public static final String delBenBen = "/contact/delbenben/";
    // 切换奔犇号
    public static final String setActive = "/contact/setactive/";
    // 群组排序
    public static final String sortGroup = "/contact/sortgroup/";
    // 政企排序
    public static final String sortEnterprise = "/enterprise/sortenterprise/";

	// 消息中心
	public static final String messageCenter = "/news/newsList/";
    // 邀请好友
    public static final String applyFriend = "/user/applyfriend/";

	// 添加好友
	public static final String addFriend = "/user/addfriend/";
    // 拒绝好友
    public static final String rejectFriend = "/user/rejectfriend/";
	// 加入政企
	public static final String addCompany = "/news/confirm/";
	// 加入政企
	public static final String readNews = "/news/updateNews/";
	// 获取一条微创作
	public static final String singleSmall = "/creation/listone";
	// 获取群资料
	public static final String singleGroupInfo = "/group/detailWithHXId/";
	// 修改群名片
	public static final String editNickName = "/group/editnickname/";
	// 获取群成员列表
	public static final String getGroupMember = "/group/member/";
    public static final String getinvitemember = "/group/Getinvitemember/";

	// 邀请群成员getInviteFriendUnionMember
	public static final String addGroupMember = "/group/joinmore/";
	// 同意加入好友联盟
	public static final String agreeJoinFriendUN = "/league/agreejoin/";
    // 拒绝加入好友联盟
    public static final String rejectLeague = "/league/rejectLeague/";


	public static final String getEnterpriseInviteMember = "/enterprise/invitemember/";

	public static final String getEnterpriseMemberList = "/enterprise/invitemember/";

	public static final String getInviteFriendUnionMember = "/league/invitemember/";

	public static final String inviteFriendUnionMember = "/league/join/";
	public static final String exitFriendUnionMember = "/league/exit/";

	// 修改群资料
	public static final String updateGroupInfo = "/group/edit";

	// 更换手机号
	public static final String updatePhone = "/user/changephone/";

	// 收藏号码直通车
	public static final String CollectStore = "/store/collect/";

	// 取消收藏号码直通车
	public static final String CancelCollectStore = "/store/cancelcollect/";

	// 创建好友联盟
	public static final String CreatedFriendUnion = "/league/add/";

	// 修改好友联盟
	public static final String UpdateFriendUnion = "/league/edit/";

	public static final String BroadCastingList = "/news/broadcastinglist";

	// 退出好友联盟
	public static final String QuitFriendUnion = "/league/exit/";

	// 邀请好友加入好友联盟
	public static final String InviteFriendUnion = "/league/join/";

	// 我的好友联盟
	public static final String MyFriendUnion = "/league/myleaguein/";
	// 我自己的好友联盟
	public static final String MySelfFriendUnion = "/league/myleaguein";

	// 查询个人信息
	public static final String MemberInfo = "/user/memberinfo/";

	// 关于犇犇
	public static final String Setting = "/setting/Registerprotocol/";

	// 查找群组
	public static final String FindTalkGroup = "/group/search/";

	// 加入群组
	public static final String JoinGroup = "/group/join/";
    // 群组拒绝
    public static final String rejectGroup = "/group/rejectgroup/";

	// 退出群组
	public static final String QuitGroup = "/group/quit/";

    // 屏蔽群组
    public static final String setFreeMode = "/group/setfreemode/";

    // 申请通知详情
    public static final String notification = "/news/notification/";

    // 联系人好友联盟详情
    public static final String leaguedetail = "/league/Leaguedetail/";


    // 联系人好友联盟详情
    public static final String myLeaguein = "/league/myleaguein/";

	// 得到邀请群组新成员列表
	public static final String getMemberListAdd = "/group/invitemember/";

	public static final String BroadCastingDelete = "/news/broadcastingdel";

	// 加入百姓网
	public static final String EnterBx = "/bxapply/join/";
	public static final String EditBx = "/bxapply/edit/";

	public static final String EditRemark = "/league/editremark";

	// 加入百姓网资料查询
	public static final String ApplyBxInfo = "/bxapply/getinfo/";
	public static final String ApplyBxInfoID = "/bxapply/getinfoWithID";

	// 邀请好友加入百姓网
	public static final String InviteFriendToBx = "/bxapply/invite/";
	// 获取非百姓网用户
	public static final String GetNotBx = "/bxapply/getnotbxg/";

	// 百姓网进度查询(单个)
	public static final String BxProgressSingle = "/bxapply/apply/";
	// 百姓网进度查询
	public static final String BxProgress = "/bxapply/applyall/";
	// 百姓网进度查询
	public static final String EditFriendUN = "/league/editNickname/";

	public static final String EditPacketInfoGroup = "/contact/changegroup";

	// 指向delete界面
	public static final int PacketManagementRequestCode = 101;
	public static final int PacketManagementResultCode = 102;

	public static final int ContactsFragmentRequestCode = 201;
	public static final int ContactsFragmentResultCode = 202;

	// 指向分组详细界面
	public static final int PacketManagementRequestCodeInfo = 301;
	public static final int PacketManagmentResultCodeInfo = 302;

	public static final int PacketManagementResultCodeInfo = 302;

	// 从分组详细界面到编辑成员界面
	public static final int Packet_Info_ManagementRequestCode = 401;
	public static final int Packet_Info_ManagementResultCode = 402;

	// 通用刷新
	public static final int writeFriendRequestCode = 501;
	public static final int writeFriendRefreshResultCode = 503;
	public static final int writeFriendResultCode = 502;

	// 选择地址后回调
	public static final int ChoiceAddressResultCode = 802;
	// 选择行业后回调
	public static final int ChoiceIndustryResultCode = 702;

	public static final int DataNUM = 10;
	public static final int zhitongcheDataNUM = 100;
	// 添加组群后回调
	public static final int AddGroupResultCode = 602;

	// 查找群组 到 群组详情
	public static final int findGroup2GroupInfoRequestCode = 901;
	// 查找群组 到 群组详情
	public static final int GroupInfoBackfindGroupRequestCode = 902;

	// 退出回调
	public static final int exitActivity = 100001;

	// 自动登录
	public static final String AUTOLOGIN = "AUTOLOGIN";
	// 手动登录
	public static final String UNAUTOLOGIN = "UNAUTOLOGIN";

	// 通讯录刷新
	public static final String ContactsRefresh = "ContactsRefresh";

	public static final String RefreshTalkGroup = "RefreshTalkGroup";

	public static final String Finsh = "Finsh";
	public static final String refreshContactsGroup = "refreshContactsGroup";

	public static final String refreshNews = "refreshNews";

	public static final String refreshGroupInfo = "refreshGroupInfo";
	public static final String refreshEnterpriseMember = "refreshEnterpriseMember";

	public static final String refreshFriendUnion = "refreshFriendUnion";

	public static final String detFriendMember = "detFriendMember";

//	public static final String smsContext = "我们一起来玩奔犇吧，下载链接:http://www.91benben.com";
    public static final String smsContext = "我们一起来玩奔犇吧，下载链接:http://112.124.101.177/download/";


	public static final String refrashFriendUnionMember = "refrashFriendUnionMember";

	public static final String refreshPackageInfo = "refreshPackageInfo";
	public static final String refrashMyFragment = "refrashMyFragment";

	public static final String refrashGroupMember = "refrashGroupMember";

	public static final String refreshActivityCaptureContactsInfo = "refreshActivityCaptureContactsInfo";

	public static boolean isDebug = true;

	public static final String refreshGroup = "refreshGroup";

	public static final String refreshFUBroadCasting = "refreshFUBroadCasting";

	public static final String getFriendByArea = "/news/getfriendwitharea";

    public static final String getUnionMemberByArea = "/news/getfriendleaguewitharea";



	public static final String refrashBroadCasting = "refrashBroadCasting";

	public static final String GetMyFriendUnionInfo = "/league/myleagueinfo";

	public static final String GetMyInviteEnterpriseMember = "/enterprise/invitelist";

	public static final String DelMyAddEnterpriseMember = "/enterprise/invitedelete";

	public static final String getEnterpriseGroupMember = "/enterprise/egmember";
	public static final String EnterpriseM = "/enterprise/membersearch";

	public static final String refreshEnterpriseDetail = "refreshEnterpriseDetail";
	public static final String refreshNumberTrain = "refreshNumberTrain";

	public static final String CLOSE = "CLOSE";

	public static final String refreshEnterpriseList = "refreshEnterpriseList";

	public static final String refreshBuyInfo = "refreshBuyInfo";

	public static final String ACTIVITYPROGRESSOFBXREFRESH = "ACTIVITYPROGRESSOFBXREFRESH";

	public static final String ADDBENBENFRIEND = "ADDBENBENFRIEND";

    //设置认证
    public static final String Setauth = "/store/Setauth";

    //获取认证信息
    public static final String Getauth = "/store/Getauth";
    //促销管理
    public static final String Promotionmanage = "/promotion/Promotionmanage";
    //添加促销
    public static final String Addpromotion = "/promotion/Addpromotion";
    //编辑促销
    public static final String Editpromotion = "/promotion/Editpromotion";
    //获取促销信息
    public static final String Getpromotion = "/promotion/Getpromotion";
    //促销上下架
    public static final String Togglepromotion = "/promotion/Togglepromotion";
    //删除促销
    public static final String Delpromotion = "/promotion/Delpromotion";
    //活动现场相册
    public static final String Activityalbum = "/activity/Activityalbum";
    //添加相册
    public static final String Addalbum = "/activity/Addalbum";
    //相册详情
    public static final String Albumdetail = "/activity/Albumdetail";
    //修改相册
    public static final String Editalbum = "/activity/Editalbum";
    //删除相册
    public static final String Delalbum = "/activity/Delalbum";
    //相册删除照片
    public static final String Delphoto = "/activity/Delphoto";
    //相册添加照片
    public static final String Addphoto = "/activity/Addphoto";

    //团购管理
    public static final String GroupManage = "/groupBuy/groupmanage";
    //获取支付方式类型
    public static final String Paymethods = "/pay/Paymethods";
    //添加团购
    public static final String Addgroupbuy = "/groupBuy/Addgroupbuy";
    //团购详情
    public static final String Getgroupbuy = "/groupBuy/Getgroupbuy";
    //删除团购
    public static final String Delgroupbuy = "/groupBuy/Delgroupbuy";
    //团购上下架
    public static final String Togglegroupbuy = "/groupBuy/Togglegroupbuy";
    //修改团购
    public static final String Editgroupbuy = "/groupBuy/Editgroupbuy";
    //所有门店
    public static final String Alldeparts = "/store/alldeparts";
    //用户团购详情
    public static final String GroupDetail = "/groupBuy/groupdetail";
    //新增收货地址
    public static final String Addaddress = "/address/Addaddress";
    //收货地址信息
    public static final String Addressdetail = "/address/Addressdetail";
    //删除收货地址
    public static final String Deladdress = "/address/Deladdress";
    //编辑收货地址
    public static final String Editaddress = "/address/Editaddress";
    //查询默认收货地址
    public static final String Defaultaddress = "/address/Defaultaddress";
    //下订单
    public static final String Addorder = "/order/Addorder";
    //订单列表
    public static final String Orderlist = "/order/Orderlist";
    //订单取消
    public static final String Delorder = "/order/Delorder";
    //订单信息
    public static final String Orderdetail = "/order/Orderdetail";
    //退款申请
    public static final String Backorder = "/order/Backorder";
    //订单验证
    public static final String Checkorder = "/order/Checkorder";
    //确认收货
    public static final String Sureget = "/order/Sureget";
    //延长收货
    public static final String Extendtime = "/order/Extendtime";
    //确认消费
    public static final String Sureconsume = "/order/Sureconsume";
    //拒绝退款
    public static final String Refuse = "/order/Refuse";
    //我是商家
    public static final String Storderlist = "/order/Storderlist";
    //我是商家订单信息
    public static final String Storderdetail = "/order/Storderdetail";
    //手动发货
    public static final String Manualsend = "/order/Manualsend";
    //用户评论
    public static final String Ordercomment = "/order/Ordercomment";
    //商家评论
    public static final String Stordercomment = "/order/Stordercomment";
    //评论列表
    public static final String OrderCommentList = "/order/OrderCommentList";
    //回复评论
    public static final String Reply = "/order/Reply";
    //同意退款
    public static final String Agreeback = "/order/Agreeback";
	//收藏小助手详情
	public static final String HelpcollectDetails = "/store/Helpcollect";
	//群公告添加
	public static final String Addbulletin = "/group/Addbulletin";
	//查看群公告
	public static final String Getbulletin = "/group/Getbulletin";
	//群组公告弹框
	public static final String PopContent = "/store/popContent";
	//号码直通车转让
	public static final String ApplyStoreTransfer = "/store/applyStoreTransfer";
	//直通车同意转让
	public static final String StoreAgreeTransfer = "/store/agreeTransfer";
	//直通车不同意转让
	public static final String StoreRefuseTransfer = "/store/refuseTransfer";

    //添加收藏
    public static final String AddCollect = "/CollectGoods/addCollect";
    //收藏列表
    public static final String CollectGoodsList = "/CollectGoods/collectGoodsList";
    //删除收藏
    public static final String DelCollect = "/CollectGoods/delCollect";
    //查询某店铺所有评论
    public static final String StoreCommentList = "/StoreComment/StoreCommentList";
    //我的账户
    public static final String MyAccount = "/user/myAccount";
    //我的账单
    public static final String MyPayLog = "/user/myPayLog";
    //政企通讯录电话数统计
    public static final String AddTelNum = "/enterprise/addTelNum";
    //获取所有拍卖场
    public static final String GetAuctionList = "/TopAuction/GetAuctionList";
    //获取拍卖场详情
    public static final String AuctionDetail = "/TopAuction/AuctionDetail";
    //出价
    public static final String GivePrice = "/TopAuction/GivePrice";
    //保证金
    public static final String PayGuarantee = "/TopAuction/PayGuarantee";
    //拍卖纪录
    public static final String GetLog = "/TopAuction/GetLog";
    //拍卖用户token值
    public static final String AuctionSet = "/TopAuction/Set";


    // 首页获取通讯录
    public static final String AddressBook = "/contact/addressBook";

    // 通过id获取联系人详情
    public static final String AddressDetail = "/contact/AddressDetail";
}
