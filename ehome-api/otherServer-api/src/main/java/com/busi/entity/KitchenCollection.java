package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 厨房收藏
 * @author: ZHaoJiaJie
 * @create: 2019-03-01 10:26
 */
@Setter
@Getter
public class KitchenCollection {

    private long id;		//主键ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;		//收藏用户ID

    private long kitchend;				//厨房ID

    private String kitchenName;				//厨房名称

    private String goodFood;			//拿手菜

    private String cuisine;			//菜系

    private String kitchenCover;		//封面

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;		//收藏时间

    private String address; 			// 详细地址   与数据库无关字段

    private int distance;			//距离  	与数据库无关字段

}
