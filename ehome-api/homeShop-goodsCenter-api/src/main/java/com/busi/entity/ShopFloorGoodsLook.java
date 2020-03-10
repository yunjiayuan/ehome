package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 搂店商品浏览
 * @author: ZHaoJiaJie
 * @create: 2020-03-02 21:55:27
 */
@Setter
@Getter
public class ShopFloorGoodsLook {

    private long id;        //主键ID

    @Min(value = 1, message = "userId参数有误")
    private long userId;        //用户ID

    private long goodsId;       //商品ID

    private String goodsName;   //商品名称

    private double price;       //价格

    private String imgUrl;      //图片

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;        //浏览时间
}
