package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: 通告浏览记录实体
 * @author: ZHaoJiaJie
 * @create: 2018-8-24 15:05:25
 */
@Setter
@Getter
public class CommunityLook {
    private long id;        //主键ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;        //浏览用户ID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;        //浏览时间

    private long infoId;    //通告ID

    @Length(max = 30, message = "标题最多可输入30字")
    private String title;        //标题

    private long communityId;    //居委会ID

    //与数据库无关字段
    private String name;                //用户名

    private String head;                 //头像

    private int proTypeId;              //省简称ID

    private long houseNumber;        // 门牌号
}
