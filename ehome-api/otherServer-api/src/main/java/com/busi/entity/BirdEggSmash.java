package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 砸蛋记录
 * @author: ZHaoJiaJie
 * @create: 2018-09-06 16:32
 */
@Setter
@Getter
public class BirdEggSmash {

    private long id;	//ID

    @Min(value = 1, message = "myId参数有误")
    private long myId;	//操作者

    @Min(value = 1, message = "userId参数有误")
    private long userId;	//被砸者

    private int eggType;//蛋类型 1金蛋2 银蛋

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;	//砸蛋时间

    //与数据库无关字段
    private String userName;
    private String userHead;

}
