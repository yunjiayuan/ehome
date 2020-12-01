package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 家门口商家收藏
 * @author: ZHaoJiaJie
 * @create: 2020-11-10 16:50:55
 */
@Setter
@Getter
public class DoorwayBusinessCollection {

    private long id;        //主键ID

    private long businessId;        //商家ID

    @Min(value = 1, message = "myId参数有误")
    private long myId;        //用户ID

    private long userId;                //商家创建者ID

    private String name;                //商家名字

    private String picture;        //图片

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;        //收藏时间

    private int type;            // 类型

    private int openType;                // 营业类型:0全天 1时间段

    private String openTime;                // 营业时间 openType=1时有效

    private String closeTime;                // 打烊时间 openType=1时有效
}
