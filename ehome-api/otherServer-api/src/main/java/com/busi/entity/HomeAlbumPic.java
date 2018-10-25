package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: 存储室图片
 * @author: ZHaoJiaJie
 * @create: 2018-10-18 10:58:47
 */
@Setter
@Getter
public class HomeAlbumPic {

    private long id;        //主键

    private long userId;    //用户

    private long albumId;    //相册ID

    private String name;    //图片名称

    private String picDescribe;    //图片描述

    private int roomType;    //相册房间类型0花园,1客厅,2家店,3存储室

    private int picState;    //图片状态	0正常 1删除

    private String imgUrl;    //图片

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;    //上传时间

}
