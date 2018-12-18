package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 招聘实体
 * @author: ZHaoJiaJie
 * @create: 2018-12-14 10:24:03
 */
@Setter
@Getter
public class WorkRecruit {

    private long id;        //主键

    @Min(value = 1, message = "userId参数有误")
    private long userId;        //用户ID

    private long companyId;        //企业ID

    private String positionName;        //职位名称

    private int positionType1;        //一级职位类型

    private int positionType2;        //二级职位类型

    private String total;    //招聘人数

    private int educational;        //学历要求

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;    //添加时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refreshTime;    //刷新时间

    private int state;        // 删除状态:0正常1已删除

    private int recruitmentStatus;        // 招聘状态:0已上架1已下架

    private int workingLife;    //工作年限

    private int startSalary;        //薪资水平:起

    private int endSalary;        //薪资水平:终

    private String address;        //详细地址

    private int jobProvince;        // 工作区域：省

    private int jobCity;                // 工作区域：城市

    private int jobDistrict;            // 工作区域：地区或县

    private long browseAmount;            // 浏览量

    private long deliveryNumber;            // 投递数

    private String welfare;            // 公司福利：逗号分隔！例：五险一金，周末双休，交通补贴

    private String requirements;    //任职要求

    private String contactsPhone;    // 联系人电话

    private String contactPeople;    //联系人

    private String mailbox;    //电子邮箱

    private String corporateName;    //公司名称

    //与数据库无关字段
    private String head;        //头像	查询后从内存获取最新

    private String name;        //年龄

    private long houseNumber;        //门牌号	查询后从内存获取最新

    private int proTypeId;                //	省简称ID

}
