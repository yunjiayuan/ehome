package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 寻人、寻物、失物招领实体
 * @author: ZHaoJiaJie
 * @create: 2018-08-10 11:08
 */
@Setter
@Getter
public class SearchGoods {

    private long id; // 主键

    @Min(value = 1, message = "userId参数有误")
    private long userId; // 用户ID

    private int searchType; // 查找类别:1寻人,2寻物,3失物招领

    @Length(max = 30, message = "标题最多可输入30字")
    private String goodsName; // 物品名称,寻人人名,招领物品的名称

    private String missingPlace; // 失踪地点，坐标对应中文地址

    private String missingDate; // 失踪日期

    private String missingTime; // 失踪时间

    @Length(max = 30, message = "标题最多可输入30字")
    private String title; // 标题

    @Length(max = 140, message = "内容最多可输入140字")
    private String content; // 内容

    @Length(max = 10, message = "联系人最多可输入10字")
    private String contactsName; // 联系人姓名

    @Length(max = 13, message = "电话最多可输入13字")
    private String contactsPhone; // 联系人电话

    @Max(value = 2, message = "性别参数有误，未找到指定的性别选项")
    @Min(value = 1, message = "性别参数有误，未找到指定的性别选项")
    private int missingSex; // 失踪人性别:1男,2女

    private int age;      //年龄

    private String imgUrl;//图片地址

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refreshTime; // 刷新时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime; // 添加时间

    private int auditType; // 审核标志:1审核中,2通过,3未通过

    private int deleteType; // 删除标志:1未删除,2用户删除,3管理员删除

    private long seeNumber; // 浏览次数

    private int afficheStatus; // 公告状态 :0未解决,1已解决

    private int province; // 省

    private int city; // 城市

    private int district; // 地区或县

    private double longitude;  //东经

    private double latitude;  // 北纬

    private int fraction;//公告分数
}
