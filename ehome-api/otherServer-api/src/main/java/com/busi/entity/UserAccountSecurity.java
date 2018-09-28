package com.busi.entity;

import com.busi.validator.IdCardConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * 用户账户安全实体类
 * author：SunTianJie
 * create time：2018/9/17 14:25
 */
@Getter
@Setter
public class UserAccountSecurity {

    private long id;//主键ID

    @Min(value= 1 ,message= "userId参数有误，超出指定范围")
    private long userId;//用户ID

    @IdCardConstraint(message = "身份证格式有误")
    private String idCard;//身份证号码

    @Pattern(regexp="[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!\\-]{2,30}",message = "名字格式有误，长度为2-10，并且不能包含非法字符")
    private String realName;//真实姓名

    @Pattern(regexp="^\\s*$|^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$",message = "手机号格式有误，请输入正确的手机号")
    private String phone;//密保手机(绑定手机，由于手机登录为同一个手机号)

    @Email
    private String email;//密保邮箱

    @Length(max = 255, message = "securityQuestion参数长度不合法")
    private String securityQuestion;//密保问题 格式 第一个问题的ID,对应答案;第二个问题的ID,对应答案;第三个问题的ID,对应答案

    @Max(value = 3, message = "otherPlatformType参数有误，超出指定范围")
    @Min(value= 0 ,message= "otherPlatformType参数有误，超出指定范围")
    private int otherPlatformType;//是否绑定第三方平台账号，0：未绑定, 1：绑定QQ账号，2：绑定微信账号，3：绑定新浪微博账号

    @Length(max = 50, message = "otherPlatformAccount参数长度不合法")
    private String otherPlatformAccount;//第三方平台账号名称

    @Length(max = 50, message = "otherPlatformAccount参数长度不合法")
    private String otherPlatformKey;//第三方平台账号key

    private String deviceLock;//设备锁(预留)

    private int redisStatus;//该对象在缓存中的存在形式  0空对象 无数据库对应数据  1数据已有对应数据  与数据无关字段

    private String code;//短信验证码 或邮箱验证码 与数据库无关字段

}
