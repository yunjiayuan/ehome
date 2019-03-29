package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 靓号预售实体类(提前录入预售数据)
 * author：SunTianJie
 * create time：2019/3/28 17:59
 */
@Setter
@Getter
public class GoodNumber {

    private long id;

    private int proId;//省简称id

    private long house_number;//门牌号

    private int theme;//主题 0普通靓号 1顶级靓号 2爱情靓号 3生日靓号 4手机靓号

    private int label;//数字类型规律:0表示7A，1表示6A，2表示5A，3表示4A，4表示3A，5表示7顺，6表示6顺，7表示5顺，8表示4顺，9表示3顺，10表示AAABBB，11表示AABBCC，12表示ABABAB，13表示ABCABC，14表示AABB，15表示ABAB

    private int numberDigit;//数字位数(7表示7位)

//    private int price;//初选费

    private double goodNumberPrice;//靓号价格

    private int status;//0表示未用靓号，1表示已用

}
