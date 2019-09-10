package com.busi.entity;

import lombok.Getter;
import lombok.Setter;


/**
 * @program: ehome
 * @description: 厨房包间or大厅设置实体
 * @author: ZHaoJiaJie
 * @create: 2019-07-30 11:15
 */
@Setter
@Getter
public class KitchenPrivateRoom {

    private long id;                    // ID

    private long userId;                // 商家ID

    private long kitchenId;             // 厨房ID

    private String imgUrl;             // 图片 (最多九张，逗号分隔)

    private String elegantName;           // 雅称

    private int leastNumber;             // 最少人数

    private int mostNumber;             // 最多人数

    private int bookedType;             // 包间0  散桌1

    private String videoUrl;        //视频地址

    private String videoCoverUrl;     //视频封面地址


    //与数据库无关字段
    private int reserveState;             // 状态  0空闲  1已预订

    private String delImgUrls;//将要删除的图片地址组合 “,”分隔
}
