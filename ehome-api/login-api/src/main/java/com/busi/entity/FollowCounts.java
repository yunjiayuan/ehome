package com.busi.entity;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 粉丝数统计实体
 * author：SunTianJie
 * create time：2018/10/30 17:41
 */
@Setter
@Getter
public class FollowCounts {

    private long id;//主键ID

    @Min(value= 1 ,message= "userId参数有误，超出指定范围")
    private long userId;//主动关注者ID

    @Min(value= 0 ,message= "counts参数有误，超出指定范围")
    @Max(value= 99999999999L ,message= "counts参数有误，超出指定范围")
    private long counts;//被关注者ID

}
