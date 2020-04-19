package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * @program: ehome
 * @description: 合伙购商品实体
 * @author: ZHaoJiaJie
 * @create: 2020-04-17 00:48:25
 */
@Setter
@Getter
public class PartnerBuyGoods {
    private long id;                    //主键

    private long userId;                //发起人

    private String personnel;           //合伙人信息：#用户ID#,名字,头像;#用户ID#,名字,头像;

    private String imgUrl;            //图片

    private String videoUrl;        //视频地址

    private String videoCoverUrl;     //视频封面地址

    @Length(min = 1, max = 30, message = "goodsTitle参数超出合法范围")
    private String goodsTitle;           //标题

    private int levelOne;           //商品1级分类

    private int levelTwo;           //商品2级分类

    private int levelThree;         //商品3级分类

    private int levelFour;          //商品4级分类

    private int levelFive;          //商品5级分类

    private String usedSort;        //商品分类名称

    private long usedSortId;        //商品分类Id

    private String specs;                //规格

    private double price;                //市场价格

    private double partnerPrice;                //合伙购价格

    private String details;                //商品详情描述

    private int province;                //产地省份  默认为0

    private int city;                    //产地城市  默认为0

    private int district;                //产地区域  默认为0

    private int deleteType;                //删除标志：0正常，1用户删除，2管理人员删除

    private int limitNumber;                // 限制人数

    private int number;                // 已合伙人数

    private int remarks;                // 备注

    private int state;                // 合伙状态：0未成功  1成功

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseTime;            //发布时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date limitTime;            //限制时间

    // 与数据库无关字段
    private String name;                //用户名

    private String head;                //头像

    private int proTypeId;                //省简称ID

    private long houseNumber;            //门牌号

}
