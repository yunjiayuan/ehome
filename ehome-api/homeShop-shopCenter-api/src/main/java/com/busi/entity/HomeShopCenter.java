package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * @program: ehome
 * @description: 家店实体
 * @author: ZHaoJiaJie
 * @create: 2019-5-10 14:23:26
 */
@Setter
@Getter
public class HomeShopCenter {

    private long id;                    //主键

    private long userId;                //用户

    @Length(max = 10, message = "店铺名称最多可输入10个字")
    private String shopName;                //店铺名称

    private String shopHead;            //头像

    private String videoUrl;        //视频地址

    private String videoCoverUrl;     //视频封面地址

    @Length(max = 140, message = "店铺简介最多可输入140字")
    private String content;                //店铺简介

    @Max(value = 1, message = "managementType参数有误，超出指定范围")
    @Min(value= 0 ,message= "managementType参数有误，超出指定范围")
    private int managementType;           //经营类型  0个人全职  1个人兼职

    private int deleteType;                //删除标志：0未删除，1用户删除，2管理人员删除

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            //新增时间

    private int province;                //省份  默认为0

    private int city;                    //城市  默认为0

    private int district;                //区域  默认为0

    @Length(max = 46, message = "详细地址不能超过46字")
    private String address;            // 详细地址

    @Pattern(regexp="^\\s*$|^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$",message = "手机号格式有误，请输入正确的手机号")
    private String phone; // 联系方式  手机号

    private String sourceOfSupply;      //主要货源：格式0,1,2  (逗号分隔)

    @Max(value = 1, message = "entityShop参数有误，超出指定范围")
    @Min(value= 0 ,message= "entityShop参数有误，超出指定范围")
    private int entityShop;        //是否有实体店  0否  1是

    @Max(value = 1, message = "warehouse参数有误，超出指定范围")
    @Min(value= 0 ,message= "warehouse参数有误，超出指定范围")
    private int warehouse;        //是否有工厂或仓库   0否  1是

    @Max(value = 3, message = "location参数有误，超出指定范围")
    @Min(value= 0 ,message= "location参数有误，超出指定范围")
    private int location;            //所在地：0中国大陆 1中国香港/澳门  2中国台湾  3海外

//    private int shopType;        //店铺类型  0个人店铺  1企业店铺

    private int shopState;        //店铺状态  0未开店  1已开店

    // 与数据库无关字段
//    private String name;                //用户名
//
//    private String head;                //头像
//
//    private int proTypeId;                //省简称ID
//
//    private long houseNumber;            //门牌号

    private String delImgUrls;//将要删除的图片地址组合 “,”分隔

}
