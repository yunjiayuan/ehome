package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 医生、律师咨询费订单
 * @author: ZHaoJiaJie
 * @create: 2020-03-12 14:44:09
 */
@Setter
@Getter
public class ConsultationOrders {
    private String orderNumber;//订单编号（程序生成）

    private long userId;//用户ID

    private long peopleId;    // 医师或律师ID

    private int occupation;          // 职业：0医生  1律师

    private String content;    //内容(主诉)

    private double money;//咨询金额

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;//支付时间

    private int payState;//支付状态  0未支付   1已支付

    private int duration;   //咨询时长
}
