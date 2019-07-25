package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @program: ehome
 * @description: 厨房评价
 * @author: ZHaoJiaJie
 * @create: 2019-03-01 10:18
 */
@Setter
@Getter
public class KitchenEvaluate {

    private long id;                            //主建ID

    private long userId;                        //用户ID

    private String content;                        //内容

    private long orderId;                        //	订单Id

    private long kitchenId;                        //厨房ID

    private String imgUrls;                //图片

    private String kitchenCover;            //厨房封面

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;                            //评价时间

    private int state;                        // 0正常 1删除

    private int score;                        // 评分：1一星 2二星 3三星 4 四星 5 五星

    private int anonymousType;  //是否匿名发布 0表示正常发布不匿名  1表示匿名发布  别人能看到评论  无法查看名字和门牌号

    private int bookedState;            //   0厨房  1订座

    //与数据库无关字段
    private String dishesIds;		//菜品IDS   逗号间隔

    private long houseNumber;        // 门牌号

    private int proTypeId;                //	省简称ID

    private int isLike;                        //是否点赞  0未点赞 1点过赞

    private String head;                    //头像	查询后从内存获取最新

    private String name;                //用户名	查询后从内存获取最新

}
