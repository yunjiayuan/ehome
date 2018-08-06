package com.busi.dao;

import com.busi.entity.UserHeadAlbum;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 用户个人资料界面的九张头像相册 DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface UserHeadAlbumDao {

    /***
     * 新增
     * @param userHeadAlbum
     * @return
     */
    @Insert("insert into userHeadAlbum(userId,imageUrl) values (#{userId},#{imageUrl})")
    @Options(useGeneratedKeys = true)
    int add(UserHeadAlbum userHeadAlbum);

    /***
     * 根据用户ID查询userHeadAlbum信息
     * @param userId
     */
    @Select("select * from userHeadAlbum where userId = #{userId}")
    UserHeadAlbum findUserHeadAlbumById(@Param("userId") long userId);

    /***
     * 更新
     * @param userHeadAlbum
     * @return
     */
    @Update(("update userHeadAlbum set " +
            "imageUrl=#{imageUrl}" +
            " where userId=#{userId}"))
    int updateUserHeadAlbum(UserHeadAlbum userHeadAlbum);

}
