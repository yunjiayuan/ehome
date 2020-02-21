package com.busi.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * 战役评奖活动
 * author ZJJ
 * Create time 2020-02-16 12:15:41
 */
@Setter
@Getter
public class CampaignAwardActivity {
    private long id;//主键ID

    @Min(value = 1, message = "userId参数有误，超出指定范围")
    private long userId;//发布者用户ID

    @Length(max = 30, message = "标题内容不能超过30字")
    private String title;//标题（最多30个字）

    @Length(max = 10000, message = "内容不能超过1万字")
    private String content;//内容（最多1万字）

    @Length(max = 500, message = "图片地址格式有误")
    private String imgUrl;//图片

    @Length(max = 100, message = "视频地址格式有误")
    private String videoUrl;//视频地址

    @Length(max = 100, message = "视频封面地址格式有误")
    private String videoCoverUrl;//视频封面地址

    private long votesCounts;//票数

    private int status;//状态  0正常 1删除

    private long auditor;//审核人

    private int examineType;//0 待审核 1已审核无稿费 2已审核未抽取稿费 3已审核已抽取稿费

    private double draftMoney; //稿费

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;//发布时间

    //与数据库无关字段
    private String delUrls;//将要删除的地址组合 “,”分隔

    private String name; //用户名

    private String head; //头像

    private int proTypeId;//省简称ID

    private long houseNumber;//门牌号

}
