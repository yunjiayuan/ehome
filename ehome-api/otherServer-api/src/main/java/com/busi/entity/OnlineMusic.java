package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @program: 在线音乐
 * @author: ZHaoJiaJie
 * @create: 2018-10-31 12:21:44
 */
@Setter
@Getter
public class OnlineMusic {

    private long id;        //主键ID

    private String singer;      //歌手

    private int songType;        //歌曲类型：0.热歌榜 1.流行 2.纯音乐 3.摇滚 4.神曲 5.DJ 6.电音趴 7.说唱 8.国风 9.欧美

    private String songName;       //歌名

    private String lengthTime;      //时长

    private String coverUrl;       //封面

    private String musicUrl;      //歌曲地址

    private int grade;        //等级

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;     //添加时间

}
