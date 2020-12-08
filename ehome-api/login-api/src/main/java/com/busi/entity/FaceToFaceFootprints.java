package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;

/***
 * 面对面足迹实体类
 * author：ZhaoJiaJie
 * create time：2020-12-07 17:29:59
 */
@Setter
@Getter
public class FaceToFaceFootprints {

    private long userId;                // 用户ID

    private String roomName;                //房间名称

    @Digits(integer = 3, fraction = 6, message = "lat参数格式有误")
    private double lat;                    //纬度

    @Digits(integer = 3, fraction = 6, message = "lon参数格式有误")
    private double lon;                    //经度

    private String name;                //用户名

    private String head;                    //头像

    private int proTypeId;                //	省简称ID

    private long houseNumber;        // 门牌号
}
