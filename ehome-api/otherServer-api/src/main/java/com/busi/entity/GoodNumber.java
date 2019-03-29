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

    private int label;//数字类型规律:0表示11A，1表示10A，2表示9A，3表示8A，4表示7A，5表示6A，6表示5A，7表示4A，8表示3A，9表示9顺，10表示8顺，11表示7顺，12表示6顺，13表示5顺，14表示4顺，15表示3顺，16表示AAABBB，17表示AABBCC，18表示ABABAB，19表示ABCABC，20表示AABB，21表示ABAB

    private int numberDigit;//数字位数(7表示7位)

//    private int price;//初选费

    private double goodNumberPrice;//靓号价格

    private int status;//0表示未用靓号，1表示已用

}
