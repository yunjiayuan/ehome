package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * @program: ehome
 * @description: 家店商品实体
 * @author: ZHaoJiaJie
 * @create: 2019-07-24 16:02
 */
@Setter
@Getter
public class HomeShopGoods {

    private long id;                    //主键

    private long userId;                //用户

    private long shopId;                //店铺ID

    private String imgUrl;            //图片

    private String videoUrl;        //视频地址

    private String videoCoverUrl;     //视频封面地址

    private int goodsType;                //商品类型:0全新  1二手

    @Length(min = 1, max = 30, message = "goodsTitle参数超出合法范围")
    private String goodsTitle;                //标题

    private int levelOne;           //商品1级分类

    private int levelTwo;           //商品2级分类

    private int levelThree;         //商品3级分类

    private int levelFour;          //商品4级分类

    private int levelFive;          //商品5级分类

    private String usedSort;        //商品分类名称

    private long usedSortId;        //商品分类Id

    private long brandId;                //品牌ID

    private String brand;                //品牌名称

//    private String netContent;                //净含量
//
//    private String producer;                //产地

    private String specs;                //规格

    private double price;                //价格

    private int stock;                //库存

//    private int freight;                //运费

    private String details;                //商品详情描述

    private long detailsId;                //商品详情描述Id

    private String barCode;                //商品条形码

    private String code;                //商家编码

    private String sort;                //店铺中所属分类名称

    private long sortId;                //店铺中所属分类id

    private int province;                //发货地省份  默认为0

    private int city;                    //发货地城市  默认为0

    private int district;                //发货地区域  默认为0

    private int pinkageType;                //是否包邮:0是  1否

    private String expressMode;            //快递方式 格式[快递,邮费;快递,邮费]：圆通快递,10;申通快递,15;韵达快递,12;中通快递,16;

    private int invoice;                //发票:0有  1无

    private int guarantee;                //保修:0有  1无

    private int refunds;                //退货承诺:0有  1无

    private int returnPolicy;                //退货保障:0有  1无

    private int stockCount;                //库存计数:0拍下减库存  1付款减库存

    private String startTime;                //开始时间

    private int spike;                //秒杀商品:0电脑用户  1手机用户

    private int galleryFeatured;                //橱窗推荐:0是  1否

    private long seeNumber;                //浏览数

    private long monthSales;                //月销量

    private long commentNum;                //评价数

    private int deleteType;                //删除标志：0正常，1用户删除，2管理人员删除

    private int auditType;                // 审核标志:0审核中,1通过,2未通过

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseTime;            //发布时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refreshTime;            //刷新时间

    private int sellType;                //商品买卖状态 : 0已上架，1已下架，2已卖出

//    private double lat;                    //纬度
//
//    private double lon;                    //经度

    private int frontPlaceType;            //是否置顶：0未置顶，1已置顶

    private String specialProperty;       //特殊商品属性(多个属性之间";"分隔)  【暂不用】

    private String propertyName;        //商品属性名称：分场景
                                        // 传参时：多个属性之间"_"分隔 格式：属性ID,属性名称,属性值 如： 1,颜色,红色
                                        // 写表时：多个属性之间","分隔 格式：#属性#,#属性# 如：#红色#,#4寸#

    // 与数据库无关字段
    private String name;                //用户名

    private String head;                //头像

    private int proTypeId;                //省简称ID

    private long houseNumber;            //门牌号

    private int sellingNumber;            //在卖宝贝数量

    private String delImgUrls;//将要删除的图片地址组合 “,”分隔
}
