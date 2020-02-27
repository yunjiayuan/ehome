package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * @program: ehome
 * @description: 楼店商品实体
 * @author: ZHaoJiaJie
 * @create: 2019-11-19 13:13
 */
@Setter
@Getter
public class ShopFloorGoods {

    private long id;                    //主键

    private long userId;                //用户

    private String imgUrl;            //图片

    private String videoUrl;        //视频地址

    private String videoCoverUrl;     //视频封面地址

    private String goodsCoverUrl;     //商品封面地址

    @Length(min = 1, max = 30, message = "goodsTitle参数超出合法范围")
    private String goodsTitle;                //标题

    @Length(max = 140, message = "basicDescribe参数超出合法范围")
    private String basicDescribe;                //基本描述

    private int levelOne;           //商品1级分类

    private int levelTwo;           //商品2级分类

    private int levelThree;           //商品3级分类

    private int extendSort;           //商品扩展分类

    private String usedSort;        //商品分类名称

    private String specs;                //规格

    private double price;                //价格

    private int discount;                //折扣

    private double discountPrice;        //折扣价

    private int stock;                   //库存

    private long sales;                     //销量

    private String details;                //商品详情描述

    private long detailsId;                //商品详情描述Id

    private int deleteType;                //删除标志：0正常，1用户删除，2管理人员删除

    private int auditType;                // 审核标志:0审核中,1通过,2未通过

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseTime;            //发布时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refreshTime;            //刷新时间

    private int sellType;                //商品买卖状态 : 0已上架，1已下架，2已卖出

    private long commentNumber;       //评论数

    // 与数据库无关字段
    private String name;                //用户名

    private String head;                //头像

    private int proTypeId;                //省简称ID

    private long houseNumber;            //门牌号

//    private int sellingNumber;            //在卖宝贝数量

    private String delImgUrls;//将要删除的图片地址组合 “,”分隔
}
