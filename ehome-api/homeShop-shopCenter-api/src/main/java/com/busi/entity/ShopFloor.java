package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @program: ehome
 * @description: 黑店实体
 * @author: ZHaoJiaJie
 * @create: 2019-11-12 16:14
 */
@Setter
@Getter
public class ShopFloor {

    private long id;                    //主键

    private long userId;                //用户

    @Length(max = 15, message = "店铺名称最多可输入15个字")
    private String shopName;                //店铺名称

    private int identity;            //店主身份:0个人 1居委会

    private long communityId;    //居委会ID

    private String communityName;                // 居委会名称

    private String shopHead;            //店铺封面

    private String videoUrl;        //视频地址

    private String videoCoverUrl;     //视频封面地址

    @Length(max = 140, message = "店铺简介最多可输入140字")
    private String content;                //店铺简介

    private int deleteType;                //删除标志：0未删除，1用户删除，2管理人员删除

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            //新增时间

    private int shopState;        //店铺状态  0未开店  1开店（正常营业） 2暂停营业

    private int payState;        //缴费状态  0未缴费  1已缴费

    private int distributionState;        //是否第一次配货状态  0未配货  1已配货

    private double lat;                    //纬度

    private double lon;                    //经度

    private int province;                //省份  默认为0

    private int city;                    //城市  默认为0

    private int district;                //区域  默认为0

    private String address;                // 详细地址

    private String telephone;                // 联系电话

    @NotNull(message = "小区名称不能为空")
    private String villageName;                // 小区名称

    @NotNull(message = "小区唯一标识不能为空")
    private String villageOnly;                // 小区唯一标识

    // 与数据库无关字段
    private String name;                //用户名

    private String head;                //头像

    private int proTypeId;                //省简称ID

    private long houseNumber;            //门牌号

    private String delImgUrls;      //将要删除的图片地址组合 “,”分隔

    private int distance;            //距离

}
