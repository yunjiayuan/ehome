package com.busi.utils;

/**
 * 常量类
 * author：SunTianJie
 * create time：2018/6/5 13:36
 */
public class Constants {
    /**超时时间常量配置 开始**/
    public static final String MSG_ID ="msg_id_";
//    public static final int MSG_TIME_OUT_SECOND_30 =30;//30秒有效期
    public static final int TIME_OUT_MINUTE_5 =60*5;//5分钟有效期
    public static final int MSG_TIME_OUT_MINUTE_10 =60*10;//10分钟有效期
    public static final int TIME_OUT_MINUTE_15 =60*15;//15分钟有效期
    public static final int TIME_OUT_MINUTE_45 =60*45;//45分钟有效期
    public static final int MSG_TIME_OUT_HOUR_1 =60*60;//1小时有效期
    public static final int TIME_OUT_MINUTE_60_24_1 =60*60*24;//1天有效期
//    public static final int MSG_TIME_OUT_DAY_7 =60*60*24*7;//七天有效期
    public static final String MSG_REGISTER_MQ ="msg_register_mq";//注册队列名称
    public static final int USER_TIME_OUT =60*60*24*7;//对象在缓存中的生命周期 7天有效期
    public static final int TIME_OUT_MINUTE_60_24_30 =60*60*24*30;//30天有效期
    /**超时时间常量配置 结束**/

    /** 省市区参数 开始**/
    public static final int DSY_PROVINCE = 34;
    public static final String DSY_CITY = "1,17,9,14,21,14,9,18,11,18,13,17,14,9,13,11,14,12,5,8,17,11,10,1,21,1,7,18,17,11,1,9,3,3";
    public static final String DSY_DISTRICT = "16,12,7,5,5,8,8,10,4,6,8,7,4,5,3,8,7,4,13,7,10,9,5,12,12,6,11,5,7,8,3,2,7,8,8,9,7,8,7,4,6,3,32,5,12,6,5,7,5,6,8,8,7,3,10,10,4,5,9,4,24,3,12,4,7,4,17,5,11,4,6,10,12,4,7,6,6,8,10,4,16,12,8,10,14,11,9,8,10,10,10,4,11,10,11,12,10,9,8,12,16,7,18,25,16,11,19,11,11,7,23,14,19,17,9,5,1,10,10,15,13,10,6,9,12,10,6,12,10,10,5,6,9,3,18,8,6,9,9,10,4,13,8,10,17,3,8,10,6,5,8,1,1,8,2,1,13,1,6,9,7,13,9,9,11,12,12,5,12,5,8,6,11,9,4,9,5,6,10,9,4,6,5,7,8,7,8,7,13,8,11,5,6,8,11,9,7,6,11,18,13,4,12,9,5,12,2,10,3,7,6,7,10,6,7,7,6,7,7,4,13,7,6,3,7,9,12,8,9,13,8,3,11,12,6,5,3,4,6,3,6,4,6,5,5,4,7,6,7,11,5,9,10,12,2,8,12,12,4,6,4,12,12,6,8,11,10,5,11,17,13,6,10,14,3,13,10,12,11,7,4,11,13,14,13,12,19,13,4,19,7,6,18,5,7,11,17,6,9,9,5,5,5,8,10,4,6,7,18,7,11,8,7,10,18,12,9,1,9,3,7,3,8,12,4,4,2,1,3,8,1,10,7,7,5,10,12,5,3,13,14,5,8,4,9,1,8,3,9,11,10,13,5,7,9,9,11,6,9,11,4,6,40,12,38,7,29,37,4,2,29,13,5,2,1,4,5,9";
    public static final int[] PRO_INFO_ARRAY = {0,11,12,24,18,27,21,19,4,15,8,16,17,7,9,13,6,28,29,25,14,5,23,2,20,1,26,30,22,10,3,33,32,31};//内存中用户省简称转换ID对照表
    /**省市区参数 结束**/

    /**门牌号靓号规则 开始**/
    public static final String ABAB ="(\\d)((?!\\1)\\d)\\1\\2$";
    public static final String AABB = "(\\d)\\1((?!\\1)\\d)\\2$";
    public static final String ABCABC = "^(\\d\\d\\d)\\1+$";
    public static final String ABABAB = "^(\\d)((?!\\1)\\d)\\1\\2\\1\\2";
    public static final String AABBCC = "(\\d)\\1((?!\\1)\\d)\\2((?!\\1)\\d)\\3";
    public static final String AAABBB = "^(?:(\\d)\\1\\1)+$";
    public static final String ABC = "(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){2}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){2})\\d";
    public static final String ABCD = "(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){3}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){3})\\d";
    public static final String ABCDE = "(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){4}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){4})\\d";
    public static final String ABCDEF = "(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){5}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){5})\\d";
    public static final String ABCDEFG = "(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){6}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){6})\\d";
    public static final String ABCDEFGH = "(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){7}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){7})\\d";
    public static final String ABCDEFGHI = "(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){8}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){8})\\d";
    public static final String AAA = "(.)\\1{2}";
    public static final String AAAA = "(.)\\1{3}";
    public static final String AAAAA = "(.)\\1{4}";
    public static final String AAAAAA = "(.)\\1{5}";
    public static final String AAAAAAA = "(.)\\1{6}";
    public static final String AAAAAAAA = "(.)\\1{7}";
    public static final String AAAAAAAAA = "(.)\\1{8}";
    public static final String AAAAAAAAAA = "(.)\\1{9}";
    public static final String AAAAAAAAAAA = "(.)\\1{10}";
    public static final String LOVE = "^[0-9]*(521|520|1314|9421|921|1711)";
    public static final String PHONE = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
    public static final String BRITHDAY = "^(19|20)\\d{2}(1[0-2]|0?[1-9])(0?[1-9]|[1-2][0-9]|3[0-1])$";
    public static final String[] PRETTY_NUMBER_ARRAY ={AAAAAAAAAAA,AAAAAAAAAA,AAAAAAAAA,AAAAAAAA,AAAAAAA,AAAAAA,AAAAA,AAAA,AAA,ABCDEFGHI,ABCDEFGH,ABCDEFG,ABCDEF,ABCDE,ABCD,ABC,AAABBB,AABBCC,ABABAB,ABCABC,AABB,ABAB,LOVE,PHONE,BRITHDAY};
    public static final int[] PRETTY_NUMBER_PRICE_ARRAY ={0,0,0,0,1800,1600,1200,1000,50,0,0,1800,1600,1200,1000,50,1200,1200,1200,1200,50,50,100,0,30};//单位元  七位数靓号价格数组 0表示占位无意义 其他位数需要新增定价数组
    /**门牌号靓号规则 结束**/

    /** 请求接口白名单 开始**/
    public static final String REQUEST_WHITE_LIST ="login-api/createCode,login-api/login,login-api/checkCode,login-api/registerByHouseNumber," +
            "login-api/registerByPhone,login-api/findVersion,paymentServer-api/checkAlipaySign,paymentServer-api/checkWeixinSign," +
            "paymentServer-api/checkUnionPaySign,otherServer-api/checkPhoneCode,login-api/checkAccount,otherServer-api/checkQuestion," +
            "otherServer-api/checkEmailCode,login-api/resetPassWord,login-api/SendPhoneMessage,login-api/SendEmailMessage," +
            "otherServer-api/findUserAccountSecurity,otherServer-api/findQuestion,login-api/privacyPolicy,/login-api/findAdvertPic";//白名单接口 服务端将不会验证token权限
    /** 请求接口白名单 结束**/

    /** 短信平台配置 开始**/
    public static final String SENDURLPATH = "http://smsapi.c123.cn/OpenPlatform/OpenApi?";
    public static final String ACTION = "sendOnce";
    public static final String AC = "1001@501173720001";//用户账号
    public static final String AUTHKEY = "B01D84CD215ACD2D1BAA68BA83F98326";//认证密钥
    public static final String CGID = "52";//通道组编号
    public static final String CSID = "101";//签名编号
    public static final int ACCOUNT_DAY_TOTAL = 100;//同一账号每天最多发送短信次数
    public static final int ACCOUNT_HOUR_TOTAL = 30;//同一账号每小时最多发送短信次数
    public static final int CLIENT_DAY_TOTAL = 200;//同一客户端设备每天最多发送短信次数
    public static final int CLIENT_HOUR_TOTAL = 60;//同一客户端设备每小时最多发送短信次数
    /** 短信平台配置 结束**/

    /** 附近的人配置 开始**/
    public static final int RADIUS = 30000;//附近的人半径范围 单位M
    public static final int LIMIT = 200;//附近的人查询的人数
    /** 附近的人配置 结束**/

    /** 七牛云存储配置 开始**/
    public static final String QINIU_ACCESSKEY = "yQ6ITsQeI8O8e4089UPjcuspGpfBhzYGXg9hl7t9";
    public static final String QINIU_SECRETKEY = "jubopEJl_xqoca6qllBCnBlF3ViFCWyTe0bkeGK9";
    public static final String QINIU_BUCKET = "ehome";//空间名
    /** 七牛云存储配置 结束**/

    /** 实名制认证配置 开始**/
    public static final String CHECK_REALNAME_URL="https://v.apistore.cn/api/a1";//实名认证地址
    public static final String CHECK_BANKCARD_URL="https://v.apistore.cn/api/v4/verifybankcard4";//银行卡四元素认证地址
    public static final String REALNAME_KEY="381b395c0c9dfa8e06335ef948ffcba3";//实名认证key
    public static final String BANKCARD_KEY="e56266e81c79dd76edb09e8c111d3ced";////银行卡四元素认证key
    /** 实名制认证配置 结束**/

    /** 图灵机器人配置 开始**/
    public static final String TULING_URL="http://openapi.tuling123.com/openapi/api/v2";//请求地址
    public static final String TULING_KEY_NV="0461974cb80b461699dd6ff57244011d";//女机器人key  注意一个机器人对应一个key
    public static final String TULING_KEY_NAN="87f74b1c9b6e4542a480a1c7507fa080";//男机器人key  注意一个机器人对应一个key
    /** 图灵机器人配置 结束**/

    /** 百度UNIT机器人配置 开始**/
    public static final String UNIT_URL="https://aip.baidubce.com/rpc/2.0/unit/service/chat";//沙盒测试请求地址  调用次数无限制 每秒最多处理请求数3
    public static final String UNIT_API_KEY="lUPsvMBxBEbXCEWKsapcpeKj";//api key
    public static final String UNIT_SECRET_KEY="XwTVm7yGG2Gezh3Eo2gNP1Z5ort44u86";//secret key
    /** 百度UNIT机器人配置 结束**/

    /** 随便走走 各地串串配置 开始**/
    public static final int WALK_LIMIT_COUNT_USER = 30;//普通用户每天30次
    public static final int WALK_LIMIT_COUNT_MEMBER = 100;//普通会员用户每天30次
    public static final int WALK_LIMIT_COUNT_SENIOR_MEMBER = 10000;//高级会员 元老级会员 创世元老级会员用户每天10000次
    /** 随便走走 各地串串配置 结束**/

    /** 喂鸟相关配置 开始**/
    public  static final int FEEDBIRDTOTALCOUNT = 10;		//普通用户每天最多喂鸟10次
    public  static final int FEEDBIRDFULL = 10;		    //喂饱次数(产生一个金蛋)
    public  static final long EGGCOUNTDOWN= 1000 * 60 * 60 * 10;//产蛋时间 10个小时
    public  static int birdCount = 13870;//喂鸟定时任务（userId）
    /** 喂鸟相关配置 结束**/

    /** 公告置顶配置 开始**/
    public static final int SET_TOP_COUNT_USER = 0;//普通用户无资格
    public static final int SET_TOP_COUNT_MEMBER = 3;//普通会员用户每月3次
    public static final int SET_TOP_COUNT_SENIOR_MEMBER = 10;//高级会员 元老级会员 创世元老级会员用户每月10次
    /** 公告置顶配置 结束**/

    /** 简历下载配置 开始**/
    public static final int DOWRESUME_COUNT = 10;		//普通用户每天可下载简历个数
    public static final int DOWRESUME_COUNTTOTAL = 100;		//普通用户可下载简历总数
    /** 简历下载配置 结束**/

    /** 涂鸦次数限制配置 开始**/
    public static final int GRAFFITI_COUNT_USER = 10;//普通用户每天10次
    public static final int GRAFFITI_COUNT_MEMBER = 20;//普通会员用户每天20次
    public static final int GRAFFITI_COUNT_SENIOR_MEMBER = 100;//高级会员 元老级会员 创世元老级会员用户每天100次
    /** 涂鸦次数限制配置 结束**/

    /** 好友上线个数配置 开始**/
    public static final int USER_FRIEND_COUNT = 500;//普通用户
    public static final int USER_FRIEND_COUNT_MEMBER = 800;//普通会员
    public static final int USER_FRIEND_COUNT_SENIOR_MEMBER = 1000;//高级会员 元老级会员 创世元老级会员
    /** 好友上线个数配置 结束**/

    /** 关注上线个数配置 开始**/
    public static final int FOLLOW_COUNT = 2000;
    /** 关注上线个数配置 结束**/

    /** 相册相关配置 开始**/
    public static final int UPLOADALBUMCOUNT = 50;		//普通用户“存储室”中最多可创建50个相册
    public static final int UPLOADIMGCOUNT = 500;		//普通用户“存储室”中最多上传图片的总张数为：500
    /** 相册相关配置 结束**/

    /**全国114家快递方式对照数组**/
    public static final String[] expressModeArray = {"sf,顺丰速运","yt,圆通速递","sto,申通快递","zt,中通快递","yd,韵达快递","ems,EMS","post,中国邮政","zjs,宅急送","qf,全峰快递","sut,速通物流","tt,天天快递","ht,百世快递","jd,京东快递","jj,佳吉快运","jy,佳怡物流","kj,快捷快递","kk,京广速递","lb,龙邦快运","lht,联昊通速递","qrt,全日通快递","sad,赛澳递","se,速尔快递","wx,万象物流","xb,新邦物流","yc,远成物流","ys,优速快递","af,亚风快运","city100,城市100","qy,全一快递","rfd,如风达","fedex,中国联邦快递","dhl,dhl中国","zy,增益速递","dp,德邦","ane,安能物流","rrs,日日顺物流","sn,苏宁快递","hmj,黄马甲物流","xy,心怡物流","fedexInter,联邦国际","tdhy,天地华宇","hw,汇文配送","dtd,门对门","ql,青旅物流","ezs,速达快递","zzjh,郑州建华","gcex,飞洋快递","aae,aae全球速递","aol,aol澳通速递","jde,骏达快递","ky,跨越速运","sh,盛辉物流","wt,运通速运","xd,迅达速递","yad,源安达快递","yhc,1号仓","cnp,中邮快递","hre,高铁速递","gt,国通快递","pj,品骏（唯品会）","ups,中国UPS","cb,晟邦物流","bt,奔腾物流","dd,大达物流","dt,大田物流","xf,信丰物流","bdt,八达通","ax,安迅物流","zs,准实快运","ccd,次晨达物流","cg,程光快递","ch,春辉物流","cky,出口易跨境物流","cl,City-link","coe,东方快递","ct,诚通物流","cx,传喜物流","yx,宇鑫物流","dby,迪比翼快递","ddw,大道物流","df,德方物流","efs,EFSPOST","ewe,EWE全球快递","fb,飞豹快运","fd,Fardar","fy,飞鹰物流","gd,冠达快递","ge,环球速运","gk,港快速递","hl,恒路物流","hly,好来运快递","hq,华企快运","yw,燕文物流","ydt,易达通快递","ymx,亚马逊物流","ykm,易客满","yhx,一号线国际速递","ucs,UCS合众速递","tnt,TNT","jg,景光物流","jhe,佳惠尔快递物流","ld,林道国际","ljs,立即送","max,澳洲迈速","ml,明亮物流","oto,中欧快运","pad,平安达腾飞快递","ry,日昱物流","sa,圣安物流","sj,速捷快递","sjfd,顺捷丰达速运","sjwl,穗佳物流","suf,速方国际物流"};

    /**家医馆科室对照数组**/
    public static String[] department = {"内科","心脏科","呼吸科","消化内科","血液科","内分泌科","感染科","影像科","免疫科","神经内科","外科","骨科","泌尿外科","心胸外科","血管外科","神经外科","妇科","产科","儿科","口腔科","眼科","耳鼻喉科","心理科","理疗科","皮肤科","男科","整形美容科","营养科","肝瘤及预防科","报告解读科","康复科","中医科"};

    /** 用户奖励提现金额的最低限制 配置 开始**/
    public static final int REWARD_TOTAL_MONEY = 50;//例如 满50元可以提现到钱包
    public static final int REWARD_EBLOG_LIKE_COUNT_10 = 10;//点赞数临界值 超过临界值 自动奖励用户红包
    public static final int REWARD_EBLOG_LIKE_COUNT_100 = 100;//点赞数临界值 超过临界值 自动奖励用户红包
    public static final int REWARD_EBLOG_LIKE_COUNT_10000 = 10000;//点赞数临界值 超过临界值 自动奖励用户红包
    /** 用户奖励提现金额的最低限制 结束**/

    /** redis配置 开始**/
    //redis 0库相关key配置(USER)
    public static final String REDIS_KEY_VERSION = "version_";//版本号key
    public static final String REDIS_KEY_ADVERTPICADDRESS = "advert_pic_address";//版本号key
    public static final String REDIS_KEY_ADMINI_HOMEPAGEINFO_FLAG = "admini_homePageInfo_flag";//屏蔽主界面部分功能按钮 0关闭 1开启
    public static final String REDIS_KEY_ADMINI_VIDEOSHOOT_TYPE = "admini_videoShoot_type";//修改“生活圈拍摄视频时的拍摄类型” 0默认使用七牛拍摄 1使用APP自研拍摄 2使用其他平台拍摄
    public static final String REDIS_KEY_USER = "user_";//用户实体key
    public static final String REDIS_KEY_PHONENUMBER = "phoneNumber";//手机号与用户ID对应关系
    public static final String REDIS_KEY_OTHERNUMBER = "otherNumber";//第三方平台账号与用户ID对应关系
    public static final String REDIS_KEY_HOUSENUMBER = "houseNumber";//门牌号与用户ID对应关系
    public static final String REDIS_KEY_REGISTER_CREATECODE_COUNT = "register_createCode_count";//获取验证码次数统计
    public static final String REDIS_KEY_LOGIN_ERROR_COUNT = "login_error_count";//登录错误记录
    public static final String REDIS_KEY_HOUSEMOVING_ERROR_COUNT = "houseMoving_error_count";//搬家密码错误记录
    public static final String REDIS_KEY_QINIU_TOKEN = "qiniu_token";//七牛token
    public static final String REDIS_KEY_UNIT_TOKEN = "baidu_unit_token";//百度UNIT token
    public static final String REDIS_KEY_USER_DETAILED = "user_detailed_";//用户详细信息对应关系
    public static final String REDIS_KEY_USERFRIENDLIST = "userFriendList_";//用户好友信息对应关系
    public static final String REDIS_KEY_USERFRIENDGROUP = "userFriendGroup_";//用户好友分组对应关系
    public static final String REDIS_KEY_USER_POSITION_LIST = "user_position";//用户位置关系记录
    public static final String REDIS_KEY_USER_POSITION = "user_position_";//用户位置实体对应
    public static final String REDIS_KEY_USER_HEADNOTES = "user_headNotes_";//用户主界面房间封面及欢迎视频对照关系
    public static final String REDIS_KEY_USER_HEADALBUN = "user_headAlbum_";//用户个人资料界面的九张头像相册对照关系
    public static final String REDIS_KEY_USER_WALK_LIMIT = "user_walk_limit";//用户随便走走 各地串串记录对照关系
    public static final String REDIS_KEY_USER_SET_TOP = "user_set_top";//用户置顶公告 对照关系
    public static final String REDIS_KEY_USER_GRAFFITI_LIMIT = "user_graffiti_limit";//涂鸦次数记录对照关系
//    public static final String REDIS_KEY_USER_VISIT_TODAY_COUNT = "user_visit_todayCount";//今日访问
//    public static final String REDIS_KEY_USER_VISIT_TOTAL_COUNT = "user_visit_totalCount";//总访问量
    public static final String REDIS_KEY_USER_VISIT = "user_visit_";//访问量记录关系表
    public static final String REDIS_KEY_BIRD_FEEDING_TODAY = "bird_feeding_today_";//今天喂的鸟
    public static final String REDIS_KEY_BIRD_FEEDING_TOTAL_COUNT = "bird_feeding_totalcount";//今日喂鸟次数
    public static final String REDIS_KEY_SHARING_PROMOTION = "sharing_promotion_";//已领取新人红包用户
    public static final String REDIS_KEY_USER_JURISDICTION = "user_jurisdiction_";//用户权限关系
    public static final String REDIS_KEY_USERMEMBERSHIP = "userMembership_";//userId与用户会员信息对象的关系对照
    public static final String REDIS_KEY_PAYMENT_PURSEINFO = "payment_purse_";//userId与钱包对象的关系对照
    public static final String REDIS_KEY_PAYMENT_PAYKEY = "payment_payKey_";//userId与支付私钥的关系对照
    public static final String REDIS_KEY_PAYMENT_PAYPASSWORD = "payment_payPassword_";//userId与支付密码的关系对照
    public static final String REDIS_KEY_PAYMENT_BANKCARD = "payment_bankCard_";//userId与银行卡的关系对照
    public static final String REDIS_KEY_PAY_ERROR_COUNT = "payment_error_count";//支付错误记录（包括密码有误和支付出现的错误，每天限制100次）
    public static final String REDIS_KEY_PAY_ORDER_EXCHANGE = "payment_order_exchange_";//钱包兑换订单 对应关系  订单ID对应订单实体
    public static final String REDIS_KEY_PAY_ORDER_RECHARGE = "payment_order_recharge_";//钱包充值订单 对应关系  订单ID对应订单实体
    public static final String REDIS_KEY_PAY_ORDER_MEMBER = "payment_order_member_";//购买会员订单 对应关系  订单ID对应订单实体
    public static final String REDIS_KEY_PAY_ORDER_REDPACKETSINFO = "payment_order_redpacketsInfo_";//发送红包订单 对应关系  订单ID对应订单实体
    public static final String REDIS_KEY_FOLLOW_LIST = "follow_list_";//关注信息 当前用户关注的人的ID组合 逗号分隔
    public static final String REDIS_KEY_FOLLOW_COUNTS = "follow_counts_";//粉丝数对照 用户ID对应粉丝数
    public static final String REDIS_KEY_CHAT_SQUARE = "chat_square_";//聊天室在线用户对照 省ID对应在线用户
    public static final String REDIS_KEY_SELFCHANNELVIP = "selfChannelVip_";//userId与自频道会员信息对象的关系对照
    public static final String REDIS_KEY_BONDORDER = "bondOrder_";//userId与楼店保证金订单的关系对照
    public static final String REDIS_KEY_SELFCHANNELVIP_ORDER = "selfChannelVipOrder_";//userId与自频道会员订单对象关系对照
    public static final String REDIS_KEY_GOODNUMBER_ORDER = "goodNumber_order_";//userId与靓号订单对象关系对照
    public static final String REDIS_KEY_GOODNUMBER_ORDER_STATUS = "goodNumber_order_status_";//"省简称ID+门票号(格式0_1001518)"与靓号订单对象关系对照
    public static final String REDIS_KEY_STAR_CERTIFICATION = "star_certification_";//明星认证对象关系对照
    public static final String REDIS_KEY_GOODCATEGORY_LIST = "goodCategory_List";//商品分类
    public static final String REDIS_KEY_GOODSBRANDS_LIST = "goodsBrands_List";//商品品牌

    //验证码相关配置
    public static final String REDIS_KEY_REG_TOKEN = "regToken_";//注册临时验证码key
    public static final String REDIS_KEY_PAY_FIND_PAYPASSWORD_CODE = "payment_findPayPassword_code_";//找回支付密码 短信验证码对应的key
    public static final String REDIS_KEY_USER_ACCOUNT_SECURITY_BIND_CODE = "user_account_security_bind_phone_code_";//安全中心绑定手机验证码 短信验证码对应的key
    public static final String REDIS_KEY_USER_ACCOUNT_SECURITY_UNBIND_CODE = "user_account_security_unbind_phone_code_";//安全中心解绑手机验证码 短信验证码对应的key
    public static final String REDIS_KEY_USER_ACCOUNT_SECURITY_BIND_EMAIL_CODE = "user_account_security_bind_email_code_";//安全中心绑定邮箱验证码 验证码对应的key
    public static final String REDIS_KEY_USER_ACCOUNT_SECURITY_UNBIND_EMAIL_CODE = "user_account_security_unbind_email_code_";//安全中心解绑邮箱验证码 验证码对应的key
    public static final String REDIS_KEY_USER_ACCOUNT_SECURITY_CHANGEPASSWORD_EMAIL_CODE = "user_account_security_changepassword_email_code_";//安全中心修改密码邮箱验证码 验证码对应的key
    public static final String REDIS_KEY_USER_ACCOUNT_SECURITY_FINDPASSWORD_EMAIL_CODE = "user_account_security_findPassword_email_code_";//安全中心找回密码邮箱验证码 验证码对应的key
    public static final String REDIS_KEY_USER_ACCOUNT_SECURITY_FINDPASSWORD_CODE = "user_account_security_findPassword_code_";//手机短信找回登录密码验证码 短信验证码对应的key
    public static final String REDIS_KEY_USER_ACCOUNT_SECURITY_CHANGEPASSWORD_CODE = "user_account_security_changePassword_code_";//手机短信修改密码验证码 短信验证码对应的key
    public static final String REDIS_KEY_USER_ACCOUNT_SECURITY_INVITATION_CODE = "user_account_security_invitation_code_";//短信邀请新用户注册 短信验证码对应的key
    public static final String REDIS_KEY_USER_HOMESHOP_USERINFO_CODE = "homeshop_userinfo_code_";//开通家电个人信息验证 短信验证码对应的key
    public static final String REDIS_KEY_USER_CHANGE_PASSWORD_KEY = "user_account_change_password_key_";//修改密码临时key

    //短信次数验证key
    public static final String REDIS_KEY_ACCOUNT_DAY_TOTAL = "sendMsg_account_day_total";//同一账号每天最多发送短信次数限制KEY
    public static final String REDIS_KEY_ACCOUNT_HOUR_TOTAL = "sendMsg_account_hour_total";//同一账号每小时最多发送短信次数限制KEY
    public static final String REDIS_KEY_CLIENT_DAY_TOTAL = "sendMsg_client_day_total";//同一客户端设备每天最多发送短信次数限制KEY
    public static final String REDIS_KEY_CLIENT_HOUR_TOTAL = "sendMsg_client_hour_total";//同一客户端设备每小时最多发送短信次数限制KEY

    //redis 1库相关key配置(IPS)
    public static final String REDIS_KEY_IPS_HOMELIST = "ips_home_list";//IPS 推荐列表(之前的最新)
    public static final String REDIS_KEY_IPS_LOVEANDFRIEND = "ips_loveAndFriend_";//userId与婚恋交友对象关系对照
    public static final String REDIS_KEY_IPS_OTHERPOSTS = "ips_otherPosts_";//userId与其他公告对象关系对照
    public static final String REDIS_KEY_IPS_USEDDEAL = "ips_usedDeal_";//userId与二手公告对象关系对照
    public static final String REDIS_KEY_IPS_USEDDEALORDERS = "ips_usedDealOrders_";//userId与二手订单对象关系对照
    public static final String REDIS_KEY_IPS_SEARCHGOODS = "ips_searchGoods_";//userId与寻人寻物失物招领对象的关系对照
    public static final String REDIS_KEY_IPS_COLLECT = "ips_collect_";//userId与公告收藏对象的关系对照
    public static final String REDIS_KEY_IPS_WORKRESUME = "ips_workResume_";//userId与简历对象的关系对照
    public static final String REDIS_KEY_IPS_WORKRECRUIT = "ips_workRecruit_";//userId与招聘对象的关系对照
    public static final String REDIS_KEY_IPS_WORKDOWNLOAD = "ips_workDownload_";//企业与被下载简历对象的关系对照
    public static final String REDIS_KEY_KITCHEN = "kitchen_";//厨房关系对照  0厨房  1订座
    public static final String REDIS_KEY_KITCHENDISHESLIST = "kitchenDishesList_";//厨房菜品信息对应关系
    public static final String REDIS_KEY_KITCHENORDERS = "kitchenOrders_";//userId与厨房订单对象关系对照
    public static final String REDIS_KEY_KITCHENBOOKEDORDERS = "kitchenBookedOrders_";//userId与厨房订座订单对象关系对照
    public static final String REDIS_KEY_HOURLYWORKER = "hourlyWorker_";//小时工关系对照
    public static final String REDIS_KEY_HOURLYORDERS = "hourlyOrders_";//userId与小时工订单对象关系对照
    public static final String REDIS_KEY_HOMESHOP = "homeShop_";//userId与家店对象关系对照
    public static final String REDIS_KEY_SHOPFLOOR = "shopFloor_";//userId与楼店对象关系对照
    public static final String REDIS_KEY_SHOPFLOOR_CARTLIST = "shopFloor_cartList_";//楼店购物车列表与userId关系对照
    public static final String REDIS_KEY_SHOPFLOOR_SORTLIST = "shopFloor_sortList_";//楼店分类列表 格式 shopFloor_sortList_一级分类ID_二级楼店分类ID
    public static final String REDIS_KEY_SHOPFLOORORDERS = "shopFloorOrders_";//订单编号与楼店订单对象关系对照
    public static final String REDIS_KEY_SHOPFLOOR_MASTERORDERS = "shopFloor_masterOrders_";//订单编号与楼店补货订单对象关系对照
    public static final String REDIS_KEY_HOMEHOSPITAL = "homeHospital_";//userId与医馆对象关系对照



    //redis 2库相关key配置(otherServer)
    public static final String REDIS_KEY_IPS_TASK = "task_";//userId与任务对象的关系对照
    public static final String REDIS_KEY_USER_ACCOUNT_SECURITY = "user_account_security_";//userId与安全中心对象的关系对照

    //redis 3库相关key配置(EBLOG)
    public static final String REDIS_KEY_EBLOG = "eblog_";//userId与生活圈对象的关系对照
    public static final String REDIS_KEY_EBLOG_COMMENT = "eblog_Comment_list_";//生活圈评论关系对照
    public static final String REDIS_KEY_EBLOG_REPLY = "eblog_Reply_list_";//生活圈评论回复关系对照
//    public static final String REDIS_KEY_EBLOGLIST = "eblog_list";//生活秀首页列表
    public static final String REDIS_KEY_EBLOGSET = "eblog_set";//生活秀首页列表
    public static final int EBLOG_LIKE_COUNT = 10000;//点赞数临界值 超过临界值 自动进入推荐列表
    public static final int REDIS_KEY_EBLOGLIST_COUNT = 20000;//生活秀首页列表条数临界值
    public static final String EBLOG_LIKE_LIST = "eblog_like_list_";//生活圈ID对应点赞人员记录Set
    public static final String REDIS_KEY_HOMEBLOGTAG = "eblog_tag";//生活圈标签

    /** redis配置 结束**/

}

