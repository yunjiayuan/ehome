package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 小时工
 * @author: ZHaoJiaJie
 * @create: 2019-1-7 15:23:55
 */
@Setter
@Getter
public class HourlyWorker {

    private long id;                    // ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;                // 小时工ID

    private int businessStatus;        // 营业状态:0正常 1暂停

    private String housekeeping;        // 家政公司

    private int deleteType;                 // 删除标志:0未删除,1用户删除,2管理员删除

    private int auditType;            // 审核标志:0审核中,1通过,2未通过

    private int arriveTime;                // 平均到达时长

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    private String healthyImgUrl;            //健康证

    private String coverCover;        //头像

    private String videoUrl;        //视频地址

    private String videoCoverUrl;     //视频封面地址

    private String content;                //简介

    private long totalSales;        // 总的服务次数

    private long totalScore;        // 总评分

    private double lat;                    //纬度

    private double lon;                    //经度

    private String address;            // 详细地址

    private String workerType;        //工作类型【格式(逗号分隔)：打扫卫生,擦桌子】

    private String name;                //姓名

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date birthday;         //生日

    private int sex;        // 性别:1男,2女

    //与数据库无关字段
    private String delImgUrls;//将要删除的图片地址组合 “,”分隔

    private int distance;        //距离

    private int proTypeId;    //	省简称ID

    private long houseNumber;    // 门牌号
}
