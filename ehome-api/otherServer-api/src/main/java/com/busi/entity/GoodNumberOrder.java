package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 靓号订单接口（与数据库无关实体）
 * author：SunTianJie
 * create time：2019/4/1 16:56
 */
@Setter
@Getter
public class GoodNumberOrder {

    private String orderNumber;//订单编号（程序生成）

    @Min(value= 1 ,message= "payState参数有误，超出指定范围")
    private long userId;//用户ID

    private double money;//花费金额

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;//支付时间

    @Max(value = 1, message = "payState参数有误，超出指定范围")
    @Min(value= 0 ,message= "payState参数有误，超出指定范围")
    private int payState;//支付状态  0未支付   1已支付

    @Max(value = 33, message = "proId参数有误，超出指定范围")
    @Min(value= 0 ,message= "proId参数有误，超出指定范围")
    private int proId;//省简称id

    @Max(value = 12, message = "house_number参数有误，超出指定范围")
    @Min(value= 1 ,message= "house_number参数有误，超出指定范围")
    private long house_number;//门牌号

    @Length(min = 32, max = 32, message = "密码格式有误")
    private String password;//32位-遍md5加密

    @Pattern(regexp="[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!\\-]{2,10}",message = "名字格式有误，长度为2-10，并且不能包含非法字符")
    private String name; // 姓名 中文 英文 数字 简单字符组合

    @Max(value = 2, message = "sex参数有误，超出指定范围")
    @Min(value= 0 ,message= "sex参数有误，超出指定范围")
    private int sex; // 性别

    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date birthday; // 生日

    @Min(value= 0 ,message= "sex参数有误，超出指定范围")
    private int country; // 国家

    @Min(value= 0 ,message= "province参数有误，超出指定范围")
    private int province; // 省

    @Min(value= 0 ,message= "city参数有误，超出指定范围")
    private int city; // 城市

    @Min(value= 0 ,message= "district参数有误，超出指定范围")
    private int district; // 地区或县

}
