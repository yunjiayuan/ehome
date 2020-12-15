package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: 存储室图片上传记录实体
 * @author: ZhaoJiaJie
 * @create: 2020-12-11 14:42:05
 */
@Setter
@Getter
public class HomeAlbumPicWhole {

    private long id;        //主键

    private long userId;    //用户

    private long num;    //数量

    private int time;    //上传日期  格式20201212
}
