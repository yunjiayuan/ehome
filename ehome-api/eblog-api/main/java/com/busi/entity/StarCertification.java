package com.busi.entity;

import com.busi.validator.IdCardConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * @program: ehome
 * @description: 明星认证
 * @author: ZHaoJiaJie
 * @create: 2020-01-02 13:16
 */
@Setter
@Getter
public class StarCertification {

    private long id;//主键ID

    @Min(value = 1, message = "userId参数有误，超出指定范围")
    private long userId;//用户ID

    @Pattern(regexp = "[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!\\-]{2,10}", message = "名字格式有误，长度为2-10，并且不能包含非法字符")
    private String name;//姓名

    @Length(max = 10, message = "stageName不能超过10字")
    private String stageName;  //艺名/昵称

    @Length(max = 50, message = "职业不能超过50字")
    private String job;//职业 ：明星/艺人、歌手、演员、模特、舞者、主持人、导演/制片人、经纪人/助理、编曲/作词、监制、编剧、剧务、化妆师/摄影师/灯光师、群演、其他

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date debutTime;//出道时间

    @IdCardConstraint(message = "身份证格式有误")
    private String idCard;//身份证

    @Min(value = 1, message = "age参数有误，超出指定范围")
    private int age;//年龄

    @Max(value = 1, message = "sex参数有误，超出指定范围")
    @Min(value = 0, message = "sex参数有误，超出指定范围")
    private int sex;//性别：0男，1女

    private String iDPositive;//手持身份证正面

    private String iDBack;//手持身份证背面

    private int state;//0认证中  1认证通过  2认证失败

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;//创建时间
}
