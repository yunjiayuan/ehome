package com.busi.entity;

import com.busi.validator.IdCardConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 新型冠状病毒事件报备
 * author suntj
 * Create time 2020-03-18 16:02:25
 */
@Setter
@Getter
public class CommunityEventReporting {

    private long id;//主键ID

    private long communityId;    //居委会ID

    @Min(value = 1, message = "userId参数有误，超出指定范围")
    private long userId;//用户ID

    private int eventReportingType;//事件报备类型  0普通人员  1疑似人员  2病毒感染者

    @NotEmpty(message = "小区名称不能为空")
    private String villageName;       // 小区名称

    private String houseNumber;          //楼栋编号

    private int houseCompany;         //单位：0号楼、1栋、2幢、3座 4无

    private String unitNumber;             //单元编号

    private int unitCompany;           //单位：0单元、1无单元

    private String roomNumber;             //室

    private int roomState;             //房屋状态：0自住 1出租

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;//新增时间

    @IdCardConstraint(message = "身份证格式有误")
    private String idCard;//身份证号码

//    @Pattern(regexp = "[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!\\-]{2,30}", message = "名字格式有误，长度为2-10，并且不能包含非法字符")
    private String realName;//真实姓名

    @Pattern(regexp = "^\\s*$|^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$", message = "手机号格式有误，请输入正确的手机号")
    private String phone;//手机

    private int review;            // 0审核中 1已审核 2审核不通过
//
//    private String departTime;             //出发时间和出院时间

    private String placeOfDeparture;       //出发地和所住医院名称  改为：路线数据存储 格式：出发时间,出发地,到达时间,到达地,交通工具类型,具体交通工具;出发时间,出发地,到达时间,到达地,交通工具类型,具体交通工具

//    private String arriveTime;             //到达时间
//
//    private int vehicle;                   //交通工具：0火车 1飞机 2地铁 3客车 4公交 5出租车 6轮船 7自驾 8其他
//
//    private String trainNumber;            // 车次/航班号

    private int contactHistory;        // 是否有湖北武汉接触史:0没有  1有

    private String remarks;        // 备注

    private String message; // 居委会反馈：请及时留意居委会信息反馈

}
