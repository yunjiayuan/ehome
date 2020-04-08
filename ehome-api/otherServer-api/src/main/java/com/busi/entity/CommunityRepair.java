package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * 社区报修
 * author ZJJ
 * Create time 2020-04-08 16:43:21
 */
@Setter
@Getter
public class CommunityRepair {

    private long id;                //主键ID

    private long communityId;    //type=0时居委会ID  type=1时物业ID

    private int type;         //报修类别   0居委会  1物业

    private long userId;            //报修者ID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;                //报修时间

    @Length(max = 500, message = "内容最多可输入500个字")
    private String content;            //内容

    private String picture;            //图片

    private int state;            //解决状态：0未解决  1已解决

    //与数据库无关字段
    private String name; //用户名

    private String head; //头像

    private int proTypeId;//省简称ID

    private long houseNumber;//门牌号

}
