package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 我的家门口超市实体
 * @author: ZhaoJiaJie
 * @create: 2020-07-15 18:36:26
 */
@Setter
@Getter
public class ShopFloorMyDoorway {

    private long id;                    //主键

    private long userId;                //用户

    private double lat;                    //纬度

    private double lon;                    //经度


}
