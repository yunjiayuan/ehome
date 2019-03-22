package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 自频道会员
 * @author: ZHaoJiaJie
 * @create: 2019-03-22 13:21
 */
@Setter
@Getter
public class SelfChannelVip {

    private long id;

    private long userId;   //用户

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;  //开通时间

    private Date expiretTime;  //到期时间

    @Max(value = 1, message = "memberShipStatus参数有误，参数值超出合法范围")
    @Min(value= 0 ,message= "memberShipStatus参数有误，参数值超出合法范围")
    private int memberShipStatus;//用户当前会员状态  0自频道会员   1过期会员

}
