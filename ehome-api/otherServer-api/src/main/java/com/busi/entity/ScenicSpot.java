package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 景区实体
 * @author: ZhaoJiaJie
 * @create: 2020-07-29 10:41:32
 */
@Setter
@Getter
public class ScenicSpot {

    private long id;                    // 景区ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;                // 商家ID

    private int businessStatus;        // 营业状态:0正常 1打烊中

    private int deleteType;            // 删除标志:0未删除,1用户删除,2管理员删除

    private int auditType;            // 审核标志:0审核中,1通过,2未通过

    @Length(max = 14, message = "景区名称不能超过30字")
    private String scenicSpotName;                //景区名称

    private String openTime;                // 景区开放时间

    private String closeTime;                // 景区关闭时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 入驻时间

    private String licence;        //景区证照

    private String picture;        //景区图片

    private String videoUrl;        //视频地址

    private String videoCoverUrl;     //视频封面地址

    @Length(max = 140, message = "景区贴士不能超过140字")
    private String tips;                //景区贴士

    @Length(max = 2000, message = "景区简介不能超过2000字")
    private String content;                //景区简介

    private long totalEvaluate;        // 总评价

    private long totalScore;        // 总评分

    @Min(value = 0, message = "province参数有误，超出指定范围")
    private int province; // 省

    @Min(value = 0, message = "city参数有误，超出指定范围")
    private int city; // 城市

    @Min(value = 0, message = "district参数有误，超出指定范围")
    private int district; // 地区或县

    @Digits(integer = 3, fraction = 6, message = "lat参数格式有误")
    private double lat;                    //纬度

    @Digits(integer = 3, fraction = 6, message = "lon参数格式有误")
    private double lon;                    //经度

    private String type;            // 景区类型

    private int levels;            // 景区级别

//    @Pattern(regexp = "^\\s*$|^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$", message = "手机号格式有误，请输入正确的手机号")
    private String phone;//景区电话

    private double cost;                    //门票最低价格

    private int free;            // 是否免费开放：0收费  1免费

    //与数据库无关字段
    private String delImgUrls;//将要删除的图片地址组合 “,”分隔

    private int distance;            //距离

    private String name;                //用户名

    private String head;                    //头像

    private int proTypeId;                //	省简称ID

    private long houseNumber;        // 门牌号

}
