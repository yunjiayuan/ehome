package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/***
 * 用户头像相册  主界面各房间封面
 * author：SunTianJie
 * create time：2018/6/25 9:40
 */
@Setter
@Getter
public class UserHeadNotes {

  private long id;//主键ID

  private long userId;//用户ID

  private String gardenCover;//花园封面

  private String livingRoomCover;//客厅封面

  private String homeStoreCover;//家店封面

  private String storageRoomCover;//储存室封面

  private String welcomeVideoPath;//欢迎视频地址

  private String welcomeVideoCoverPath;//欢迎视频封面地址

}
