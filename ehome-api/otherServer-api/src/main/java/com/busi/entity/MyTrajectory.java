package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * 我的轨迹（疫情）
 * author ZJJ
 * Create time 2020-02-16 12:15:41
 */
@Setter
@Getter
public class MyTrajectory {

    private long id;                       //主键ID

    @Min(value = 1, message = "userId参数有误，超出指定范围")
    private long userId;                   //用户ID

    private String departTime;             //出发时间

    private String placeOfDeparture;       //出发地

    private double setOutLat;              //出发地纬度

    private double setOutLon;              //出发地经度

    private String arriveTime;             //到达时间

    private String destination;            //到达地

    private double arriveLat;              //到达地纬度

    private double arriveLon;              //到达地经度

    private int vehicle;                   //交通工具：0火车 1飞机 2地铁 3客车 4公交 5出租车 6轮船 7其他

    private String trainNumber;            // 车次/航班号

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;                     //新增时间
}
