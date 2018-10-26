package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 二手快递
 * @author: ZHaoJiaJie
 * @create: 2018-10-24 13:52:08
 */
@Setter
@Getter
public class UsedDealExpress {

    private long id;                //主键

    private long userId;            //用户

    private int expressSate;        // 状态:0正常1已删除

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    private int postage;                //邮费

    private int expressMode;     //快递方式

}
