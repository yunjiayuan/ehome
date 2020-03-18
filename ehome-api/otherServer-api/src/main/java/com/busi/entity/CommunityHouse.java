package com.busi.entity;

import com.busi.validator.IdCardConstraint;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

/**
 * 房屋信息
 * author ZJJ
 * Create time 2020-02-15 16:02:25
 */
@Setter
@Getter
public class CommunityHouse {

    private long id;                //主键ID

    private long communityId;    //居委会ID

    private long userId;            //用户ID

    private String villageName;       // 小区名称

    private double lat;                    //纬度

    private double lon;                    //经度

    private String address;            // 详细地址

    private int houseNumber;          //楼栋编号

    private int houseCompany;         //单位：0号楼、1栋、2幢、3座

    private int unitNumber;             //单元编号

    private int unitCompany;           //单位：0单元、1无单元

    private int roomNumber;             //室

    private int roomState;             //房屋状态：0自住 1出租

    @IdCardConstraint(message = "身份证格式有误")
    private String idCard;//业主身份证

    @Pattern(regexp = "[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!\\-]{2,30}", message = "名字格式有误，长度为2-10，并且不能包含非法字符")
    private String realName;//业主姓名

    @Pattern(regexp = "^\\s*$|^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$", message = "手机号格式有误，请输入正确的手机号")
    private String phone;//业主电话

    private int review;            // 0审核中 1已审核
}
