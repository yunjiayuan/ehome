package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: ehome
 * @description: 今日之计
 * @author: ZHaoJiaJie
 * @create: 2019-04-18 11:15
 */
@Setter
@Getter
public class FamilyTodayPlan {

    private long id;            //主键ID

    private long userId;    //用户ID

    private String content;    //内容

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;        //添加时间

    private int state;        //状态 0正常 1删除

    //与数据库无关字段
    private String name;                //用户名

    private String head;                    //头像

    private int proTypeId;                //省简称ID

    private long houseNumber;        // 门牌号
}
