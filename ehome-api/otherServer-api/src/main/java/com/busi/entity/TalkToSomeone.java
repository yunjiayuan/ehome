package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 找人倾诉实体
 * @author: ZhaoJiaJie
 * @create: 2020-11-23 14:26:16
 */
@Setter
@Getter
public class TalkToSomeone {

    private long id;                    // 主键ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;                // 用户ID

    private int state;                //开关状态：0未开启 1已开启

    private int money;                //资费 0免费

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;    //时间

    private String remarks;                //备注

    //与数据库无关字段
    private String name;                //用户名

    private String head;                    //头像

    private int proTypeId;                //	省简称ID

    private long houseNumber;        // 门牌号
}
