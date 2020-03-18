package com.busi.entity;

import com.busi.validator.IdCardConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 房屋信息
 * author suntj
 * Create time 2020-02-15 16:02:25
 */
@Setter
@Getter
public class CommunityHouse {

    private long id;                //主键ID

    @Min(value = 1, message = "communityId参数有误")

    private long communityId;    //居委会ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;            //用户ID

    @NotEmpty(message = "小区名称不能为空")
    private String villageName;       // 小区名称

    @Digits(integer = 3, fraction = 6, message = "lat参数格式有误")
    private double lat;                    //纬度

    @Digits(integer = 3, fraction = 6, message = "lon参数格式有误")
    private double lon;                    //经度

    private String address;            // 详细地址

    private String houseNumber;          //楼栋编号

    private int houseCompany;         //单位：0号楼、1栋、2幢、3座 4无

    private String unitNumber;             //单元编号

    private int unitCompany;           //单位：0单元、1无单元

    private String roomNumber;             //室

    private int roomState;             //房屋状态：0自住 1出租

    @IdCardConstraint(message = "身份证格式有误")
    private String idCard;//业主身份证

//    @Pattern(regexp = "[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!\\-]{2,30}", message = "名字格式有误，长度为2-10，并且不能包含非法字符")
    private String realName;//业主姓名

    @Pattern(regexp = "^\\s*$|^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$", message = "手机号格式有误，请输入正确的手机号")
    private String phone;//业主电话

    private int review;   // 0审核中 1已审核 2审核不通过

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;            // 新增时间
}
