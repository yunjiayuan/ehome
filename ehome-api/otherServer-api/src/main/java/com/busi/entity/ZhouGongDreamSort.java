package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 周公解梦一二级关系实体
 * @author: ZhaoJiaJie
 * @create: 2020-10-30 15:06:11
 */
@Setter
@Getter
public class ZhouGongDreamSort {

    private long id;                    // 主键ID

    private String biglx;                    // 大分类

    private String smalllx;                    // 小分类

}
