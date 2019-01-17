package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 小时工收藏
 * @author: ZHaoJiaJie
 * @create: 2019-1-7 15:23:55
 */
@Setter
@Getter
public class HourlyWorkerCollection {

    private long id;        //主键ID

    @Min(value = 1, message = "myId参数有误")
    private long myId;        //用户ID

    private long workerId;                //小时工ID

    private String workerName;                //小时工名字

    private String workerCover;        //封面

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;        //收藏时间

    //与数据库无关字段
    private int distance;            //距离

    private int age;         //年龄

    private int sex;        // 性别:1男,2女

    private long score;        // 评分：1一星 2二星 3三星 4 四星 5 五星

    private String workerType;        //工作类型【格式(逗号分隔)：打扫卫生,擦桌子】

    private int arriveTime;                // 平均到达时长

}
