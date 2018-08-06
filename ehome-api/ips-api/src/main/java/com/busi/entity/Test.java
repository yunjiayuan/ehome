package com.busi.entity;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 实体
 * author：zhaojiajie
 * create time：2018-7-24 16:51:00
 */
@Setter
@Getter
public class Test {

  private long id;

  private long userId;

  private Date time;

  @Pattern(regexp="[\\d\\w\\u4e00-\\u9fa5,\\.;\\:\"'?!\\-]{2,10}",message = "名字格式有误，长度为2-10，并且不能包含非法字符")
  private String name;


}
