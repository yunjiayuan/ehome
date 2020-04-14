package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @program: ehome
 * @description: 家医馆咨询记录
 * @author: ZHaoJiaJie
 * @create: 2020-01-07 11:13
 */
@Setter
@Getter
public class HomeHospitalRecord {

    private long id;                    // ID

    @Min(value = 1, message = "doctorId参数有误")
    private long doctorId;                // 医师ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;                // 用户ID

    @Min(value = 0, message = "deleteType参数有误，数值超出指定范围")
    @Max(value = 1, message = "deleteType参数有误，数值超出指定范围")
    private int deleteType;                 // 删除标志:0未删除,1已删除

    @Min(value = 0, message = "state参数有误，数值超出指定范围")
    @Max(value = 1, message = "state参数有误，数值超出指定范围")
    private int state;                 // 建议状态:0未给建议,1已建议

    private String prescribed;        //医嘱（建议）

    private String content;                //内容(主诉)

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refreshTime;            //刷新时间

    private String orderNumber;//订单编号（程序生成）

    private double money;//咨询金额

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;//支付时间

    private int payState;//支付状态  0未支付   1已支付

    private int type;           // 咨询类型：0语音  1视频  2图文

    private int consultationStatus;     // 咨询状态：0未咨询  1已咨询

    private int title;            // 职称

    private int duration;   //咨询时长：0：5分钟  1：15分钟 2：30分钟  3：60分钟

    private int actualDuration;   //实际咨询时长

    //与数据库无关字段
    private int age;         //年龄

    private int sex;        // 性别:1男,2女

    private String name;                //用户名	查询后从内存获取最新

    private String head;                    //头像	查询后从内存获取最新

    private int proTypeId;                //	省简称ID

    private long houseNumber;        // 门牌号
}
