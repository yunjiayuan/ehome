package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 云视频
 * @author: ZHaoJiaJie
 * @create: 2019-03-20 15:29
 */
@Setter
@Getter
public class CloudVideo {

    private long id;//主键ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;//用户ID

    private String videoUrl;//视频地址

    private String videoCover;//视频封面

    private int duration;//时长

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;//时间

}
