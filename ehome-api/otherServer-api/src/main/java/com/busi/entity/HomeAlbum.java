package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: 存储室相册
 * @author: ZHaoJiaJie
 * @create: 2018-10-18 10:58:47
 */
@Setter
@Getter
public class HomeAlbum {

    private long id;        //主键

    private String name;    //相册名称

    private long userId;    //创建者

    private int roomType;    //房间类型0花园,1客厅,2家店,3存储室

    private long photoSize;    //相册图片数量

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;    //相册创建时间

    private String shootTime;    //拍摄时间

    private String albumType;    //相册类型 【暂不用】

    private String albumDescribe;    //相册描述

    private long albumPurview;        //相册密码ID

    private int albumSeat;            //相册坐标位子  1-4

    private int albumState;            //相册状态	0正常 1删除

    private String imgCover;    //封面

    //与数据库无关
    private String delUrls;         //将要删除的地址组合 “,”分隔

    private String password;            //密码

    private String oldPassword;        //原密码

}
