package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 找人倾诉订单实体
 * @author: ZhaoJiaJie
 * @create: 2020-11-23 14:26:16
 */
@Setter
@Getter
public class TalkToSomeoneOrder {

    private long id;                    // 主键

    private long myId;                // 倾诉者ID

    private long userId;                // 倾诉对象ID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    private String no;//订单编号（程序生成）

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;//支付时间

    private int payState;//支付状态  0未支付   1已支付

    private int status;     // 状态：0未倾诉 1已倾诉

    private double money;      //资费

    //与数据库无关字段
    private String name;                //用户名

    private String head;                    //头像

    private int proTypeId;                //	省简称ID

    private long houseNumber;        // 门牌号
}
