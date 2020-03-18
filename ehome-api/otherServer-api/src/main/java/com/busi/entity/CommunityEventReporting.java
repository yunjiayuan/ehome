package com.busi.entity;

import com.busi.validator.IdCardConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 事件报备
 * author ZJJ
 * Create time 2020-02-15 16:02:25
 */
@Setter
@Getter
public class CommunityEventReporting {

    private long id;//主键ID

    private long communityId;    //居委会ID

    @Min(value = 1, message = "userId参数有误，超出指定范围")
    private long userId;//用户ID

    private long roomId;//房屋ID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;//新增时间

    @IdCardConstraint(message = "身份证格式有误")
    private String idCard;//身份证号码

    @Pattern(regexp = "[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!\\-]{2,30}", message = "名字格式有误，长度为2-10，并且不能包含非法字符")
    private String realName;//真实姓名

    @Pattern(regexp = "^\\s*$|^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$", message = "手机号格式有误，请输入正确的手机号")
    private String phone;//手机

    private String departTime;             //出发时间

    private String placeOfDeparture;       //出发地

    private String arriveTime;             //到达时间

    private int vehicle;                   //交通工具：0火车 1飞机 2地铁 3客车 4公交 5出租车 6轮船 7自驾 8其他

    private String trainNumber;            // 车次/航班号

    private int review;            // 0审核中 1已审核

    private int type;            // 报备类型：0新型冠状病毒疫情报备 1其他事件报备

    private int contactHistory;        // 是否有湖北武汉接触史:0没有  1有

    private String remarks;        // 备注

}
