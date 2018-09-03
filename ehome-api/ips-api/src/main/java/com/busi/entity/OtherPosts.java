package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 其他公告实体类
 * @description:
 * @author: ZHaoJiaJie
 * @create: 2018-08-07 15:00
 */
@Setter
@Getter
public class OtherPosts {

    private long  id ;  //主键

    @Min(value= 1 ,message= "userId参数有误")
    private long userId ;   //用户ID

    @Length(max = 30, message = "标题最多可输入30字")
    private String title;//标题 （评分标准：字数<=5为5分  >=10为10分  >=15为20分(推荐)  >=20为30分 ）

    @Length(max = 140, message = "内容最多可输入140字")
    private String content;//内容 （评分标准：字数<=20为5分  >=50为10分  >=80为20分(推荐)  >=100为30分 ）

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date refreshTime ;   //刷新时间

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date addTime ;   //添加时间

//    @Max(value = 3, message = "审核参数有误")
//    @Min(value= 1 ,message= "审核参数有误")
    private int auditType ;  //审核标志:1审核中,2通过,3未通过

//    @Max(value = 3, message = "删除参数有误")
//    @Min(value= 1 ,message= "删除参数有误")
    private int deleteType ;   //删除标志:1未删除,2用户删除,3管理员删除

    private long seeNumber ;  //浏览次数

    private int frontPlaceType;			//是否置顶：1未置顶，2已置顶

    private int fraction;//公告分数

}
