package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 聊天广场马甲功能实体
 * author：SunTianJie
 * create time：2018/10/10 15:54
 */
@Getter
@Setter
public class ChatSquare {

    private long id;//主键

    private long userId; // UseId

    @Pattern(regexp="[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!\\-]{2,10}",message = "名字格式有误，长度为2-10，并且不能包含非法字符")
    private String name; // 姓名

    @Max(value = 2, message = "请选择性别")
    @Min(value= 1 ,message= "请选择性别")
    private int sex; // 性别

    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    @NotNull(message= "生日不能为空")
    private Date birthday; // 生日

    @Min(value= 0 ,message= "country参数有误，超出指定范围")
    private int country; // 国家

    @Min(value= 0 ,message= "province参数有误，超出指定范围")
    private int province; // 省

    @Min(value= 0 ,message= "city参数有误，超出指定范围")
    private int city; // 城市

    @Min(value= 0 ,message= "district参数有误，超出指定范围")
    private int district; // 地区或县

    @Max(value = 8, message = "学历参数有误，未找到指定的学历选项")
    @Min(value= 0 ,message= "学历参数有误，未找到指定的学历选项")
    private int studyRank; //学历

    @Max(value = 4, message = "职业参数有误，未找到指定的婚姻选项")
    @Min(value= 0 ,message= "婚姻参数有误，未找到指定的婚姻选项")
    private int maritalStatus; //婚姻

    @Length(min = 0, max = 140, message = "座右铭最多只能输入140个字")
    private String gxqm;//个性签名

}
