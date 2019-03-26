package com.busi.utils;
/** 
 * 状态码对照表
 *
 * @author SunTianJie 
 *
 * @version create time：2015-9-23 上午9:47:21 
 * 
 */
public enum StatusCode {

	/*-----------系统状态码------------*/
	CODE_SUCCESS(0,"success"),
	CODE_PARAMETER_ERROR(101,"参数有误"),
	CODE_PASSWORD_ERROR(102,"密码错误"),
	CODE_ACCOUNT_NOT_EXIST(103,"用户不存在"),
	CODE_ACCOUNT_NOT_ACTIVATED(104,"用户账户未激活"),
	CODE_ACCOUNT_IS_CLOSE(105,"该账号已停用"),
	CODE_IP_ERROR(106,"用户IP非法或IP存在异常"),
	CODE_PASSWORD_NOT_EXIST(107,"服务器中无该用户密码信息（服务端异常）"),
	CODE_ACCOUNT_STATUS_ERROR(108,"服务器账号状态异常"),
	CODE_SERVER_ERROR(109,"服务端异常"),
	CODE_USER_NOTLOGIN(110,"用户登录过期，客户端需要自动登录"),
	CODE_USER_LOGINQUIT_ERROR(111,"用户退出失败"),
	CODE_PASSWORD_ERROR_TOO_MUCH(112,"登录密码错误次数过多，系统已自动封号一天，如有疑问请联系官方客服"),
	CODE_SERVER_CODE_ERROR(113,"验证码有误"),
	CODE_HOUSENUMBER_IS_EXIST_CODE_ERROR(114,"该门牌号已经绑定其他第三方平台账号"),
	CODE_OTHERACCOUNT_IS_EXIST_CODE_ERROR(115,"该第三方平台账号已绑定其他门票号"),
	CODE_OTHERACCOUNT_KEY_ERROR(116,"第三方平台账号与当前账号解绑失败，平台KEY不匹配"),
	CODE_NETWORK_TIMEOUT_ERROR(117,"网络请求超时，请稍后重试!"),
	CODE_REMOTE_LOGIN(118,"您的账号已在其他设备上登录，请您重新登录!"),
	CODE_TOKEN_ERROR(119,"您的token不正确，请重新登录"),
	CODE_REQUEST_ERROR_COUNT(120,"您的设备进行非法请求的次数过多，系统已自动禁止该设备使用一天，如有疑问请联系官方客服"),
	CODE_ACCOUNT_NOT_ACTIVE(121,"该账号未激活，暂时不能访问其他数据接口，自动跳转到登录界面"),
	CODE_ACCOUNT_DEACTIVATED(122,"该账号已被停用，暂时不能访问其他数据接口，自动跳转到登录界面"),
	/*-----------系统状态码------------*/
	
	/*-----------短信平台态码------------*/
	CODE_SMS_IPOVER_ERROR(131,"该IP段发送短息验证码次数过多"),
	CODE_SMS_USERBOUND_ERROR(132,"该用户已绑定手机"),
	CODE_SMS_PHONEBOUND_ERROR(133,"该手机号已经被占用"),
	CODE_SMS_PHONENUM_ERROR(134,"该用户并未绑定手机或绑定的手机与将要验证的手机不符"),
	CODE_SMS_PHONEOVER_ERROR(135,"该手机发送短息验证码次数过多"),
	CODE_SMS_USEROVER_ERROR(136,"该账户发送短息验证码次数过多"),
	CODE_SMS_PHONE_NOT_EXIST_ERROR(137,"该用户的手机号未注册过"),
	/*-----------短信平台状态码------------*/
	
	
	/*-----------支付系统态码------------*/
	CODE_PAYKEY_LOSE_EFFECT_ERROR(151,"私钥过期或者失效"),
	CODE_PAYKEY_ERROR(152,"私钥错误,不匹配"),
	CODE_PAYPASSWORD_ERROR(153,"支付密码有误"),
	CODE_PURSE_NOT_ENOUGH_ERROR(154,"账户余额不足"),
	CODE_PAYPASSWORD_IS_EXIST_ERROR(155,"账户支付密码已设置过，非法操作"),
	CODE_PAY_OBJECT_NOT_EXIST_ERROR(156,"支付对象不存在，无法进行支付"),
	CODE_PAY_ERROR(157,"支付失败"),
	CODE_PAYPASSWORD_IS_NOT_EXIST_ERROR(158,"当前账户尚未设置过支付密码，无法进行相关支付操作"),
	CODE_PAYPASSWORD_ERROR_TOO_MUCH(159,"支付密码错误次数过多"),
	/*-----------支付系统态码------------*/

	/*-----------业务状态码------------*/
	//好友相关
	CODE_FRIENDS_IS_EXIST(201,"双方已有好友关系"),
	CODE_FRIENDS_NOT_EXIST(202,"双方无好友关系"),
	CODE_FRIENDS_APPLY_NOT_EXIST(203,"双方之前并未发送过好友申请或申请已过期"),
	CODE_FRIENDS_GROUP_NOT_EXIST(204,"分组不存在或者对该分组无权限操作"),
	CODE_FRIENDS_GROUP_FULL(205,"分组个数已到达上线"),
	CODE_MY_FRIENDS_COUNT_FULL(206,"自己的好友个数已到达上线"),
	CODE_USER_FRIENDS_COUNT_FULL(207,"对方用户的好友个数已到达上线"),

	//固定群/临时群/家族群
	CODE_GROUP_FULL(211,"创建群已达到上限10个"),
	CODE_GROUP_NOT_ACCESS(212,"群操作权限不足"),
	CODE_GROUP_IS_MEMBERS (213,"已是本群成员"),
	CODE_GROUP_NOT_MEMBERS(214,"不是本群成员"),
	CODE_GROUP_BASE64ENCODER_ERROR(215,"群头像上传失败"),
	CODE_GROUP_NOT(216,"未找到该群"),
	CODE_GROUP_NOT_ADMIN(217,"管理员退出群,未指派新管理者"),
	

	//仓库及装修
	CODE_DEPOT_NOT_FOUND(231,"仓库内没有这个物品"),
	CODE_DEPOT_NOT_AWARDYOU(232,"用户无权修改"),
	CODE_DEPOT_DEL(233,"物品已经被删除"),
	CODE_DEPOT_ENDED(234,"物品的已经到期"),
	CODE_DEPOT_NOT_YOURSELF(235,"不能赠送给自己"),
	CODE_DEPOT_ERROR(236,"撤销场景所有装修物品失败"),
	CODE_ITEM_NOT_FOUND(237,"商城内没有这个物品"),
	CODE_ITEM_NOT_MONEY(238,"余额不足"),
	CODE_ITEM_NOT_WALLET(239,"未开通钱包无法支付"),
	CODE_GIFT_NOT_FIND(241,"礼物信息不存在"),


	//家门口帮助
	CODE_HELP_NOT_HELP(251,"没有该帮助信息"),
	CODE_HELP_NOT_AWARDYOU(252,"用户无权修改"),
	CODE_HELP_NOT_FOUND(253,"帮助信息不存在"),
	CODE_HELP_NOT_PAY(254,"该求助尚未被购买过,无权限查看求助详情"),
	CODE_HELP_NOT_AWARDYOU_DEL(255,"无法删除求助信息,出售点子被购买过后不能删除,悬赏求助未解决无法删除"),
	CODE_HELP_NOT_PEEK_PAY(256,"未购买偷看资格,无权限查看求助详情"),

	//家博博文
	CODE_BLOG_NOT_TAG(261,"没有该标签"),
	CODE_BLOG_LABEL_TAG(262,"标签超过上限"),
	CODE_BLOG_MEMBER_TAG(276,"该标签内成员超过上限"),
	CODE_BLOG_NOT_ACCESS(263,"用户操作权限不足"),
	CODE_BLOG_NOT_FOUND(264,"博文不存在"),
	CODE_BLOG_HAS_LIKE(265,"博文点过赞"),
	CODE_BLOG_HAS_SHARE(266,"博文转发内容相同"),
	CODE_BLOG_USER_NOTLOGIN(267,"用户无权修改"),
	CODE_BLOG_USER_HAS_ATTENT(268,"已经关注过了"),
	CODE_BLOG_FRAME_FULL(269,"家博头像相册上限"),
	CODE_BLOG_NOT_FRAME(271,"家博镜框图不存在"),

	CODE_BLOG_NOT_ALBUM(272,"家博发布浏览的家园相册不存在"),
	CODE_BLOG_ALBUM_PASSWORD_ERROR(273,"家博发布浏览的家园相册密码错误"),
	CODE_BLOG_BASE64ENCODER_ERROR(274,"上传失败"),
	CODE_BLOG_NOT_PUBLIC_ERROR(275,"博文不是公开类型不能转发"),
	CODE_BLOG_ORIG_NOT_FOUND(286,"原博文不存在"),

	//文件上传
	CODE_FILE_MAXSIZE_ERROR(281,"上传文件大小限制(B)"),

	//喂鹦鹉
	CODE_BIRD_FEED_FULL(291,"本日喂鸟次数用完"),
	CODE_BIRD_FEED_TREE(292,"本日对该鸟已经喂过了"),
	CODE_BIRD_FEED_ENOUGH(293,"本日鸟吃饱了"),
	CODE_BIRD_FEED_AWARDYOU(294,"用户无权修改"),
	CODE_BIRD_FEED_ROB(295,"鸟蛋已被领取"),
	CODE_BIRD_FEED_PRODUCING(296,"产蛋中不嫩喂食"),
	CODE_BIRD_FEED_UNCLAIMED(297,"未领取蛋不能喂食"),
	CODE_BIRD_FEED_BEAR_EGG(298,"鸟蛋还在生产当中"),

	//橄榄枝
	CODE_OLIVE_FULL(301,"今日抛橄榄枝总次数用完"),
	CODE_OLIVE_TREE(302,"今日已向这家抛过橄榄枝"),
	CODE_OLIVE_ENOUGH(303,"今日这位玩家收橄榄枝的次数已满"),
	CODE_OLIVE_AWARDYOU(304,"用户无权修改"),

	//抛砖头
	CODE_BRICK_FULL(311,"本日抛砖头次数用完"),
	CODE_BRICK_TREE(312,"本日对该用户已经抛过了"),
	CODE_BRICK_ENOUGH(313,"今日这位玩家收抛砖的次数已满"),
	CODE_BRICK_AWARDYOU(314,"用户无权修改"),
	
	//家园相册影音剧
	CODE_FOLDER_NOT_FOUND(321,"家园文件夹不存在"),
	CODE_FOLDER_PASSWORD_ERROR(322,"家园文件夹密码错误"),
	CODE_FOLDER_BASE64ENCODER_ERROR(323,"文件上传失败"),
	CODE_FILE_MAX(324,"文件上传达到上限"),
	CODE_PIC_GIF_ERROR(325,"图片类型为GIF不能旋转"),
	CODE_FOLDER_MAX(326,"文件夹创建达到上限"),
	CODE_ROOM_LOCK(327,"房间已上锁"),
	
	//记事本
	CODE_NOTEPAD_BASE64ENCODER_ERROR(331,"上传失败"),
	CODE_NOTEPAD_REPEAT_ERROR(332,"今日已发表过记事"),
	CODE_NOTEPAD_SCHEDULE_ERROR(333,"日程数量已达上限"),
	
	//公告置顶
	CODE_IPS_COLLECTION(336,"你已经收藏过"),
	CODE_SETTOP_UNQUALIFIED(337,"非会员没有置顶资格"),
	CODE_SETTOP_ORDINARY_TOPLIMIT(338,"本月普通会员的置顶次数用尽"),
	CODE_SETTOP_SENIOR_TOPLIMIT(339,"本月高级会员的置顶次数用尽"),

	//公告
	CODE_IPS_AFFICHE_EXISTING(341,"该类公告已存在"),
	CODE_IPS_AFFICHE_NOT_EXIST(342,"公告不存在"),
	CODE_IPS_SHIPPINGADDRESS_TOPLIMIT(343,"收货地址数量达到上限"),
	CODE_EXPRESS_TOPLIMIT(344,"快递方式数量达到上限"),
	CODE_ORDER_TIMEOUT(345,"订单超时"),
	CODE_POSITION_REPEAT(346,"您本周已投递过该公司了，请下周再来吧"),
	CODE_RESUME_TOPLIMIT(347,"简历数量已达上限"),
	CODE_RECRUIT_TOPLIMIT(348,"招聘数量已达上限"),
	CODE_MATCHING_REPEAT(349,"您与要申请的职位不匹配，再看看其他的职位吧"),
	CODE_DOWRESUME_TOPLIMIT(350,"您下载简历次数已达上限"),
	
	//钱包
	CODE_PURSE_NOT_EXIST(351,"用户未开通钱包"),			
	CODE_PURSE_COIN_NOT_ENOUGH(352,"钱包家币不够"),	
	CODE_PURSE_POINT_NOT_ENOUGH(353,"钱包家点不够"),
	CODE_BANKCARD_CHECK_ERROR(354,"验证银行卡失败，银行卡信息不匹配，未通过"),
	CODE_BANKCARD_IS_EXIST_ERROR(355,"该用户已绑定银行卡"),
	CODE_BANKCARD_IS_NOT_EXIST_ERROR(356,"该用户尚未绑定银行卡"),
	CODE_PHONE_CODE_CHECK_ERROR(357,"短信验证码错误或者已过期失效"),
	CODE_TIME_OUT_ERROR(358,"操作超时,请重新找回"),
	CODE_CHECK_ERROR_TOO_MUCH(359,"验证错误次数过多,停止验证操作一天"),
	
	
//	CODE_PURSE_BOUND_ERROR(354,"账户已经绑定过于此相同的银行卡"),
//	CODE_PURSE_BANKPHONE_ERROR(355,"银行卡预留手机号校验失败"),
//	CODE_PURSE_BANKCARD_ERROR(356,"银行卡号校验失败"),
//	CODE_PURSE_BACKCODE_ERROR(357,"信用卡背面三位数字校验失败"),
//	CODE_PURSE_EXPIRYDATE_ERROR(358,"信用卡有效期校验失败"),
//	CODE_PURSE_CARDUSER_ERROR(359,"银行卡持卡用户名称校验失败"),
	
	//礼物
	CODE_GIFT_NOT_EXIST(361,"用户暂时没有可拾取的礼物"),			
	CODE_GIFT_USER_PICKUP(362,"礼物已经拾取"),
	CODE_GIFT_FULL(363,"今日赠送次数用完"),
	CODE_GIFT_TREE(364,"今日对该用户已经赠送过了"),
	CODE_GIFT_ENOUGH(365,"今日用户拾取礼物上限"),
	
	//评选活动
	CODE_ALREADY_JOIN(381,"该用户已经参加该活动无需再参加"),			
	CODE_ALREADY_VOTE(382,"今天已经对该用户进行过投票"),
	CODE_NOT_AUTHORITY_VOTE(383,"无权限操作"),
	CODE_NOT_REALNAME(384,"未实名"),
	
	//足迹
	CODE_FOOTMARK_NOT_AUTHORITY(391,"用户无访问权限"),
	
	//红包
	CODE_RED_PACKETS_NOT_AWARDYOU(401,"用户无权限操作"),
	CODE_RED_PACKETS_OVERDUE(402,"红包已过期"),
	
	//设置
	CODE_ACCOUNTSECURITY_CHECK_ERROR(421,"验证错误"),
	
	//推广红包 新人红包
	CODE_ALREADY_RECEIVED_ERROR(431,"该用户已领新人红包,不能重复领取"),
	CODE_NOT_BIND_PHONE_ERROR(432,"该用户未绑定手机号"),
	CODE_SHARE_CODE_ERROR(433,"分享码有误,分享码用户不存在"),
	CODE_SHARE_CODE_ERROR2(434,"分享码有误,分享码不能是自己的"),
	
	//随便走走、
	CODE_WALK_FEED_FULL(441,"普通用户随便走走次数用完"),
	CODE_MEMBER_WALK_FEED_FULL(442,"普通会员随便走走次数用完"),
	
	//厨房
	CODE_COLLECTED_KITCHEN_ERROR(451,"用户已收藏过此厨房"),
	
	//小时工
	CODE_COLLECTED_HOURLY_ERROR(461,"用户已收藏过此小时工"),
	CODE_COLLECTED_HOURLY_TOPLIMIT(462,"工种类型已达上限"),
	
	//家族记事 今日问候
	CODE_TADAY_GREET_ERROR(471,"今日已经问候过"),
	
	//家医馆
	CODE_COLLECTED_HOSPITAL_ERROR(481,"用户家医馆已存在"),

	//涂鸦次数graffiti
	CODE_GRAFFITI_FEED_FULL(491,"涂鸦次数已用尽"),

	//涂鸦次数graffiti
	CODE_SELF_CHANNEL_VIP_NOT_OPENING(501,"自频道会员未开通"),
	CODE_SELF_CHANNEL_VIP_OVERDUE(502,"自频道会员已过期"),
	CODE_SELF_CHANNEL_VIP_JOIN_ACTIVITIES(503,"您尚未参加过活动"),
	CODE_SELF_CHANNEL_VIP_NOT_JOIN_ACTIVITIES(504,"您已参加过该活动"),
	CODE_SELF_CHANNEL_VIP_NOT_JOIN_DURATION(505,"您已参加过该活动")

	;
	/*-----------业务状态码------------*/
	public final int CODE_VALUE;
	public final String CODE_DESC;

	StatusCode(int code_value,String code_desc){
		this.CODE_VALUE = code_value;
		this.CODE_DESC = code_desc;
	}
	public static String getMessage(int code) {
		for (StatusCode sc : StatusCode.values()) {
			if (sc.getCODE_VALUE() == code) {
				return sc.getCODE_DESC();
			}
		}
		return "服务端未找到异常所对应的状态码";
	}
	public int getCODE_VALUE() {
		return CODE_VALUE;
	}

	public String getCODE_DESC() {
		return CODE_DESC;
	}
}
