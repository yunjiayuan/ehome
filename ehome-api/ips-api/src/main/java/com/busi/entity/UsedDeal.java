package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 二手实体
 * @author: ZHaoJiaJie
 * @create: 2018-09-18 15:51
 */
@Setter
@Getter
public class UsedDeal {

    private int usedSort1;                //一级分类:起始值为0,默认-1为不限 :二手手机 、数码、汽车...

    private int usedSort2;                //二级分类:起始值为0,默认-1为不限 : 苹果,三星,联想....

    private int usedSort3;                //三级分类:起始值为0,默认-1为不限 :iPhone6s.iPhone5s....

    private int basicParame1;            //基本参数一:起始值为0   默认-1为不填

    private int basicParame2;            //基本参数二:起始值为0   默认-1为不填

    private int basicParame3;            //基本参数三:起始值为0   默认-1为不填

    private int basicParame4;            //基本参数四:起始值为0   默认-1为不填

    private long id;                    //主键

    @Min(value = 1, message = "userId参数有误")
    private long userId;                //用户

    @Length(max = 30, message = "标题最多可输入30字")
    private String title;                //标题

    @Length(max = 140, message = "内容最多可输入140字")
    private String content;                //宝贝描述

    private long seeNumber;                //浏览数

    private int deleteType;                //删除标志：1未删除，2用户删除，3管理人员删除

    private int auditType;                // 审核标志:1审核中,2通过,3未通过

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseTime;            //发布时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refreshTime;            //刷新时间

    private String imgUrl;            //图片

    private String problemType;            //存在的问题 无问题传空  有问题 格式： 拆机维修,屏幕异常

    private String otherProblem;        //自定义其他问题  无问题传空  有问题 格式： 喇叭,机身发热

    private double sellingPrice;        //卖出价格

    private double buyingPrice;            //买入价格

    private int pinkageType;            //是否包邮：1否，2是

    private int negotiable;                //是否面议：1否，2是

    private int toPay;                        //是否到付：1否，2是

    private int merchantType;        //是否是商家：1否，2是

    private String expressMode;            //快递方式 格式[快递,邮费;快递,邮费]：圆通快递,10;申通快递,15;韵达快递,12;中通快递,16;

    private int province;                //发布省份  默认为0

    private int city;                    //发布城市  默认为0

    private int district;                //发布区域  默认为0

    private int sellType;                //商品买卖状态 : 1已上架，2已下架，3已卖出

    private double lat;                    //纬度

    private double lon;                    //经度

    // 与数据库无关字段
    private String name;                //用户名

    private String head;                //头像

    private int proTypeId;                //省简称ID

    private long houseNumber;            //门牌号

    private int sellingNumber;            //在卖宝贝数量

    private int fraction;//公告分数

    private int frontPlaceType;			//是否置顶：1未置顶，2已置顶

    //与数据库无关
    private String delImgUrls;//将要删除的图片地址组合 “,”分隔

}
