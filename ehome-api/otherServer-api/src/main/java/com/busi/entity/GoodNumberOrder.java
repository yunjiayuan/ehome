package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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

}
