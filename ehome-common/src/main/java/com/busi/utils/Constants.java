package com.busi.utils;

/**
 * 常量类
 * author：SunTianJie
 * create time：2018/6/5 13:36
 */
public class Constants {
    /**超时时间常量配置 开始**/
    public static final String MSG_ID ="msg_id_";
//    public static final int MSG_TIME_OUT_SECOND_30 =30;/30秒有效期
    public static final int MSG_TIME_OUT_MINUTE_10 =60*10;//10分钟有效期
    public static final int TIME_OUT_MINUTE_5 =60*5;//5分钟有效期
    public static final int TIME_OUT_MINUTE_60_24_1 =60*60*24;//1天有效期
//    public static final int MSG_TIME_OUT_HOUR_1 =60*60;//1小时有效期
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
    public static final String ABAB ="(\\d\\d)\\1+$";
    public static final String AABB = "^(?:(\\d)\\1)+$";
    public static final String ABCABC = "^(\\d\\d\\d)\\1+$";
    public static final String ABABAB = "(\\d\\d)\\1+$";
    public static final String AABBCC = "^(?:(\\d)\\1)+$";
    public static final String AAABBB = "^(?:(\\d)\\1\\1)+$";
    public static final String ABC = "(.*?(123)|(234)|(345)|(567)|(678)|(789).*)";
    public static final String ABCD = "(.*?(1234)|(2345)|(3456)|(5678)|(6789).*)";
    public static final String ABCDE = "(.*?(12345)|(23456)|(34567)|(56789).*)";
    public static final String ABCDEF = "(.*?(123456)|(234567)|(345678).*)";
    public static final String ABCDEFG = "(.*?(1234567)|(2345678)|(3456789).*)";
    public static final String ABCDEFGH = "(.*?(12345678)|(23456789).*)";
    public static final String ABCDEFGHI = "(.*?(123456789).*)";
    public static final String AAA = "^(?:(\\d)\\1\\1).*$";
    public static final String AAAA = "^(?:(\\d)\\1\\1\\1).*$";
    public static final String AAAAA = "^(?:(\\d)\\1\\1\\1\\1).*$";
    public static final String AAAAAA = "^(?:(\\d)\\1\\1\\1\\1\\1).*$";
    public static final String AAAAAAA = "^(?:(\\d)\\1\\1\\1\\1\\1\\1).*$";
    public static final String AAAAAAAA = "^(?:(\\d)\\1\\1\\1\\1\\1\\1\\1).*$";
    public static final String AAAAAAAAA = "^(?:(\\d)\\1\\1\\1\\1\\1\\1\\1\\1).*$";
    public static final String AAAAAAAAAA = "^(?:(\\d)\\1\\1\\1\\1\\1\\1\\1\\1\\1).*$";
    public static final String AAAAAAAAAAA = "^(?:(\\d)\\1\\1\\1\\1\\1\\1\\1\\1\\1\\1).*$";
    public static final String LOVE = "^[0-9]*(521|520|1314|9421|921|1711)";
    public static final String PHONE = "0?(13|14|15|17|18|19)[0-9]{9}";
    public static final String BRITHDAY = "^(19|20)\\\\d{2}(1[0-2]|0?[1-9])(0?[1-9]|[1-2][0-9]|3[0-1])$";
    public static final String[] PRETTY_NUMBER_ARRAY ={ABAB,AABB,ABCABC,ABABAB,AABBCC,AAABBB,AAA,AAAA,AAAAA,AAAAAA,AAAAAAA,AAAAAAAA,AAAAAAAAA,AAAAAAAAAA,AAAAAAAAAAA,ABC,ABCD,ABCDE,ABCDEF,ABCDEFG,ABCDEFGH,ABCDEFGHI,LOVE,PHONE,BRITHDAY};
    /**门牌号靓号规则 结束**/

    /** 请求接口白名单 开始**/
    public static final String REQUEST_WHITE_LIST ="login-api/createCode,login-api/login,login-api/checkCode,login-api/registerByHouseNumber," +
            "login-api/registerByPhone,login-api/findVersion,paymentServer-api/checkAlipaySign,paymentServer-api/checkWeixinSign," +
            "paymentServer-api/checkUnionPaySign,otherServer-api/checkPhoneCode,login-api/checkAccount,otherServer-api/checkQuestion," +
            "otherServer-api/checkEmailCode,login-api/resetPassWord,login-api/SendPhoneMessage,login-api/SendEmailMessage,otherServer-api/findUserAccountSecurity,otherServer-api/findQuestion";//白名单接口 服务端将不会验证token权限
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
    public static final int RADIUS = 10000;//附近的人半径范围 单位M
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

    /** 随便走走 各地串串配置 开始**/
    public static final int WALK_LIMIT_COUNT_USER = 30;//普通用户每天30次
    public static final int WALK_LIMIT_COUNT_MEMBER = 100;//普通会员用户每天30次
    public static final int WALK_LIMIT_COUNT_SENIOR_MEMBER = 10000;//高级会员 元老级会员 创世元老级会员用户每天10000次
    /** 随便走走 各地串串配置 结束**/

    /** 喂鸟相关配置 开始**/
    public  static final int FEEDBIRDTOTALCOUNT = 10;		//普通用户每天最多喂鸟10次
    public  static final int FEEDBIRDFULL = 10;		    //喂饱次数(产生一个金蛋)
    public  static final long  EGGCOUNTDOWN= 1000 * 60 * 60 * 10;//产蛋时间 10个小时
    /** 喂鸟相关配置 结束**/

    /** 公告置顶配置 开始**/
    public static final int SET_TOP_COUNT_USER = 0;//普通用户无资格
    public static final int SET_TOP_COUNT_MEMBER = 3;//普通会员用户每月3次
    public static final int SET_TOP_COUNT_SENIOR_MEMBER = 10;//高级会员 元老级会员 创世元老级会员用户每月10次
    /** 公告置顶配置 结束**/

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

    /** 相册相关配置 开始**/
    public static final int UPLOADALBUMCOUNT = 50;		//普通用户“存储室”中最多可创建50个相册
    public static final int UPLOADIMGCOUNT = 500;		//普通用户“存储室”中最多上传图片的总张数为：500
    /** 相册相关配置 结束**/

    /** redis配置 开始**/
    //redis 0库相关key配置(USER)
    public static final String REDIS_KEY_VERSION = "version_";//版本号key
    public static final String REDIS_KEY_USER = "user_";//用户实体key
    public static final String REDIS_KEY_PHONENUMBER = "phoneNumber";//手机号与用户ID对应关系
    public static final String REDIS_KEY_OTHERNUMBER = "otherNumber";//第三方平台账号与用户ID对应关系
    public static final String REDIS_KEY_HOUSENUMBER = "houseNumber";//门牌号与用户ID对应关系
    public static final String REDIS_KEY_REGISTER_CREATECODE_COUNT = "register_createCode_count";//获取验证码次数统计
    public static final String REDIS_KEY_LOGIN_ERROR_COUNT = "login_error_count";//登录错误记录
    public static final String REDIS_KEY_HOUSEMOVING_ERROR_COUNT = "houseMoving_error_count";//搬家密码错误记录
    public static final String REDIS_KEY_QINIU_TOKEN = "qiniu_token";//七牛token
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
    public static final String REDIS_KEY_USER_VISIT_TODAY_COUNT = "user_visit_todayCount";//今日访问
    public static final String REDIS_KEY_USER_VISIT_TOTAL_COUNT = "user_visit_totalCount";//总访问量
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
    public static final String REDIS_KEY_IPS_SEARCHGOODS = "ips_searchGoods_";//userId与寻人寻物失物招领对象的关系对照
    public static final String REDIS_KEY_IPS_COLLECT = "ips_collect_";//userId与公告收藏对象的关系对照

    //redis 2库相关key配置(otherServer)
    public static final String REDIS_KEY_IPS_TASK = "task_";//userId与任务对象的关系对照
    public static final String REDIS_KEY_USER_ACCOUNT_SECURITY = "user_account_security_";//userId与安全中心对象的关系对照

    //redis 3库相关key配置(EBLOG)
    public static final String REDIS_KEY_EBLOG = "eblog_";//userId与生活圈对象的关系对照


    /** redis配置 结束**/

}

