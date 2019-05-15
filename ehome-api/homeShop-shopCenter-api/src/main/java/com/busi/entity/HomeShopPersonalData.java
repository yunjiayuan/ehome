package com.busi.entity;

import com.busi.validator.IdCardConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * @program: ehome
 * @description: 个人信息
 * @author: ZHaoJiaJie
 * @create: 2019-5-10 14:23:26
 */
@Setter
@Getter
public class HomeShopPersonalData {

    private long id;                    //主键

    private long userId;                //用户

    @Pattern(regexp="[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!\\-]{2,30}",message = "名字格式有误，长度为2-10，并且不能包含非法字符")
    private String realName;//真实姓名

    @IdCardConstraint(message = "身份证格式有误")
    private String idCard;//身份证号码

    private String idCardExpireTime;    //身份证到期时间

    private int province;                //省份  默认为0

    private int city;                    //城市  默认为0

    private int district;                //区域  默认为0

    @Length(max = 46, message = "详细地址不能超过46字")
    private String address;            // 详细地址

    @Pattern(regexp="^\\s*$|^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$",message = "手机号格式有误，请输入正确的手机号")
    private String phone; // 联系方式  手机号

    @NotNull
    private String holdIDCardUrl;     //手持身份证地址

    @NotNull
    private String halfBodyUrl;     //半身照地址

    @NotNull
    private String positiveIDCardUrl;     //身份证正面地址

    @NotNull
    private String backIDCardUrl;     //身份证反面地址

    @NotNull
    private String localNewsUrl;     //当地报纸地址

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            //新增时间

    private int acState;       // 认证状态:0未认证,1审核中,2未通过,3已认证

    // 与数据库无关字段
//    private String code;        //短信验证码

    private String delImgUrls;  //将要删除的图片地址组合 “,”分隔

}
