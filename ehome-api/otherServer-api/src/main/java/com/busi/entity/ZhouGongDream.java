package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 周公解梦实体
 * @author: ZhaoJiaJie
 * @create: 2020-10-30 15:06:11
 */
@Setter
@Getter
public class ZhouGongDream {

    private long id;                    // 主键ID

    private String title;                    // 标题

    private String message;                    // 详情

    private String biglx;                    // 大分类

    private String smalllx;                    // 小分类

    private String zm;                    // 索引标识首字母
}
