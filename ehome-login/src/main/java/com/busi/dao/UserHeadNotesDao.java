package com.busi.dao;

import com.busi.entity.UserHeadNotes;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 用户头像相册（主界面各房间封面）DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface UserHeadNotesDao {

    /***
     * 新增
     * @param userHeadNotes
     * @return
     */
    @Insert("insert into userHeadNotes(userId,gardenCover,livingRoomCover,homeStoreCover,storageRoomCover) values (#{userId},#{gardenCover},#{livingRoomCover},#{homeStoreCover},#{storageRoomCover})")
    @Options(useGeneratedKeys = true)
    int add(UserHeadNotes userHeadNotes);

    /***
     * 根据用户ID查询UserHeadNotes信息
     * @param userId
     */
    @Select("select * from UserHeadNotes where userId = #{userId}")
    UserHeadNotes findUserHeadNotesById(@Param("userId") long userId);

    /***
     * 更新用户房间封面
     * @param userHeadNotes
     * @return
     */
//    @Update(("update UserHeadNotes set name=#{name} where userId=#{userId}"))
    @Update("<script>" +
            "update UserHeadNotes set"+
            "<if test=\"gardenCover != null and gardenCover != ''\">"+
            " gardenCover=#{gardenCover}," +
            "</if>" +
            "<if test=\"livingRoomCover != null and livingRoomCover != ''\">"+
            " livingRoomCover=#{livingRoomCover}," +
            "</if>" +
            "<if test=\"homeStoreCover != null and homeStoreCover != ''\">"+
            " homeStoreCover=#{homeStoreCover}," +
            "</if>" +
            "<if test=\"storageRoomCover != null and storageRoomCover != ''\">"+
            " storageRoomCover=#{storageRoomCover}," +
            "</if>" +
            " userId=#{userId}" +
            " where userId=#{userId}"+
            "</script>")
    int updateUserHeadNoteCover(UserHeadNotes userHeadNotes);

    /***
     * 更新用户欢迎视频和封面
     * @param userHeadNotes
     * @return
     */
//    @Update(("update UserHeadNotes set name=#{name} where userId=#{userId}"))
    @Update("<script>" +
            "update UserHeadNotes set"+
            " welcomeVideoPath=#{welcomeVideoPath}," +
            " welcomeVideoCoverPath=#{welcomeVideoCoverPath}" +
            " where userId=#{userId}"+
            "</script>")
    int updateWelcomeVideo(UserHeadNotes userHeadNotes);
}
