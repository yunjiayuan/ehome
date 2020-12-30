package com.busi.entity;

import com.busi.validator.IdCardConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Date;

/***
 * 用户实体类
 * author：SunTianJie
 * create time：2018/6/25 9:40
 */
@Setter
@Getter
public class UserInfo {

	private long userId; // UseId

	private int proType; // 省简称

	private long houseNumber; // 门牌号

    @Email(message = "邮箱格式不正确")
	private String email;    //邮箱

	@Pattern(regexp="^\\s*$|^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$",message = "手机号格式有误，请输入正确的手机号")
	private String phone; // 手机号

	@Length(min = 32, max = 32, message = "登录密码格式有误")
	private String password; // 密码

    @Length(min = 32, max = 32, message = "环信密码格式有误")
    private String im_password; // 环信密码

	@IdCardConstraint(message = "身份证格式有误")
	private String idCard; // 身份证号

	@Pattern(regexp="[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!\\-]{2,10}",message = "名字格式有误，长度为2-10，并且不能包含非法字符")
	private String name; // 姓名 中文 英文 数字 简单字符组合

	@Max(value = 2, message = "sex参数有误，超出指定范围")
	@Min(value= 0 ,message= "sex参数有误，超出指定范围")
	private int sex; // 性别

	@JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
	private Date birthday; // 生日

	private int country; // 国家

	private int province; // 省

	private int city; // 城市

	private int district; // 地区或县

	@Max(value = 8, message = "学历参数有误，未找到指定的学历选项")
	@Min(value= 0 ,message= "学历参数有误，未找到指定的学历选项")
	private int studyRank;  	//学历 "0":"无","1":"中专","2":"专科","3":"本科","4":"双学士","5":"硕士","6":"博士","7":"博士后","8":"其他"

	@Max(value = 43, message = "职业参数有误，未找到指定的职业选项")
	@Min(value= 0 ,message= "职业参数有误，未找到指定的职业选项")
    private int job;           //职业 "0":"无","1":"在校学生","2":"计算机/互联网/IT","3":"电子/半导体/仪表仪器","4":"通讯技术","5":"销售","6":"市场拓展","7":"公关/商务","8":"采购/贸易","9":"客户服务/技术支持","10":"人力资源/行政/后勤", "11":"高级管理","12":"生产/加工/制造","13":"质检/安检","14":"工程机械","15":"技工","16":"财会/审计/统计","17":"金融/证券/投资/保险","18":"房地产/装修/物业","19":"仓储/物流","20":"交通/运输","21":"普通劳动力/家政服务","22":"普通服务行业","23":"航空服务业","24":"教育/培训","25":"咨询/顾问","26":"学术/科研","27":"法律","28":"设计/创意","29":"文学/传媒/影视","30":"餐饮/旅游", "31":"化工","32":"能源/地址勘察","33":"医疗/护理","34":"保健/美容","35":"生物/制药/医疗机械","36":"体育工作者","37":"翻译","38":"公务员/国家干部","39":"私营业主","40":"农/林/牧/渔业","41":"警察/其他","42":"自由职业者","43":"其他"

	@Max(value = 4, message = "职业参数有误，未找到指定的婚姻选项")
	@Min(value= 0 ,message= "婚姻参数有误，未找到指定的婚姻选项")
    private int maritalStatus;  //婚姻 "0":"无","1":"已婚","2":"未婚","3":"离异","4":"丧偶"

	@Max(value = 56, message = "nation参数有误，超出指定范围")
	@Min(value= 0 ,message= "nation参数有误，超出指定范围")
    private int nation;         //民族 "0":"请选择 1 汉族2 蒙古族3 回族4 藏族5 维吾尔族6 苗族7 彝族8 壮族9 布依族10 朝鲜族11 满族12 侗族13 瑶族14 白族15 土家族16 哈尼族17 哈萨克族18 傣族19 黎族20 傈僳族21 佤族22 畲族23 高山族24 拉祜族25 水族26 东乡族27 纳西族28 景颇族29 柯尔克孜族30 土族31 达斡尔族32 仫佬族33 羌族34 布朗族35 撒拉族36 毛南族37 仡佬族38 锡伯族39 阿昌族40 普米族41 塔吉克族42 怒族43 乌孜别克族44 俄罗斯族45 鄂温克族46 德昂族47 保安族48 裕固族49 京族50 塔塔尔族51 独龙族52 鄂伦春族53 赫哲族54 门巴族55 珞巴族56 基诺族

	@Length(min = 0, max = 140, message = "座右铭最多只能输入140个字")
	private String gxqm;           //个性签名

	private String sentiment;     	//心情

	private int birthPlace_province;//家乡省ID 默认-1无设置

	private int birthPlace_city;//家乡市ID 默认-1无设置

	private int birthPlace_district;//家乡区县ID 默认-1无设置

	@Length(max = 50, message = "公司参数有误，名字太长")
	private String company;//公司

	@Length(max = 15, message = "职位参数有误，名字太长")
	private String position;//职位

	private String head; // 头像

	private String graffitiHead; //涂鸦头像

//	private String welcomeVideoPath; //欢迎视频地址 改到 UserHeadNotes中
//
//	private String welcomeVideoCoverPath; //欢迎视频封面地址

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date time; //用户添加时间

	private int isOnline;//0不在家1在家  预留

	private int isGoodNumber;//是否为靓号（0：普通号，1：靓号）后期可根据需求细分普通靓号，高级靓号等

	private int user_ce;//用户认证 ，0表示未经过认证，1表示通过网络大V认证，2表示通过....

	private int accountStatus;//用户账户状态：0：已激活，1：未激活，2：禁用

	private String otherPlatformKey;//第三方平台用户唯一身份标识

	private int otherPlatformType;//第三方平台类型 1：QQ，2：微信，3：苹果账号

	@Length(max = 50, message = "otherPlatformAccount参数长度不合法")
	private String otherPlatformAccount;//第三方平台账号名称

	@Max(value = 1, message = "isNewUser参数有误，超出指定范围")
	@Min(value= 0 ,message= "isNewUser参数有误，超出指定范围")
	private int isNewUser;//是否为领取新人红包的标识  0默认新用户未领取 1已领新人红包(老用户)

	@Max(value = 1, message = "welcomeInfoStatus参数有误，超出指定范围")
	@Min(value= 0 ,message= "welcomeInfoStatus参数有误，超出指定范围")
	private int welcomeInfoStatus;//系统欢迎消息状态 0表示未发送  1表示已发送

	@Max(value = 1, message = "homeBlogStatus参数有误，超出指定范围")
	@Min(value= 0 ,message= "homeBlogStatus参数有误，超出指定范围")
	private int homeBlogStatus;//生活圈首次视频发布状态 0表示未发送  1表示已发送

	@Max(value = 3, message = "访问权限参数有误，超出指定范围")
	@Min(value= 0 ,message= "访问权限参数有误，超出指定范围")
	private int accessRights;//访问权限 1允许任何人  2禁止任何人  3 仅好友访问权限

	@Length(min = 4, max = 4, message = "验证码输入不正确")
	private String code;//门牌号验证码、手机验证码 与数据库无关字段

	private int type;//0表示手机号完善资料 1表示第三方平台完善资料  与数据库无关字段

	private int talkToSomeoneStatus;//倾诉状态 0表示不接受倾诉  1表示接受倾诉

	private int chatnteractionStatus;//聊天互动功能的状态 0表示不接受别人找你互动  1表示接受别人找你互动

	private String token;//用户令牌 与数据库无关字段

	private String clientId;//设备唯一标识 与数据库无关字段

	private int radius;//距离 与数据库无关字段

	private String positionTime;//位置更新时间 格式：yyyy-MM-dd HH:mm:ss 与数据库无关字段

	private long todayVisitCount;//今天访问量 与数据库无关字段

	private long totalVisitCount;//总访问量 与数据库无关字段

	@Length(min = 32, max = 32, message = "新密码格式有误")
	private String newPassword; // 新密码 数据库无关字段 用于修改密码操作使用

	@Length(min = 16, max = 16, message = "修改密码key有误")
	private String key; // 修改密码key 数据库无关字段 用于修改密码操作使用

	private int isVest;//是否设置过马甲  0未设置 1已设置（设置后不能进行串门，只能查看马甲信息） 与数据库无关字段

	private long vestId;//马甲主键ID 与数据库无关字段

	private int isSpokesman;//代言人类型  0不是 1是地区代言人

	private String spokesmanName;//代言人名称 例如：北京海淀代言人

}
