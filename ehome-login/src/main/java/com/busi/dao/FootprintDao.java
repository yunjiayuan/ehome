package com.busi.dao;

import com.busi.entity.Footprint;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 脚印DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface FootprintDao {

    /***
     * 新增脚印
     * @param footprint
     * @return
     */
    @Insert("insert into Footprint (myId,userId,time,awayTime) values (#{myId},#{userId},#{time},#{awayTime})")
    @Options(useGeneratedKeys = true)
    int add(Footprint footprint);

    /***
     * 更新离开时间
     * @param footprint
     * @return
     */
    @Update("update Footprint set awayTime=#{awayTime} where myId=#{myId} and userId=#{userId} and awayTime is null")
    int update(Footprint footprint);

    /***
     * 查询脚印记录
     * @param userId   当前登录者用户ID
     * @param findType 查询类型  0查询自己被访问过的脚印记录  1查询自己访问过的脚印记录
     * @return
     */
    @Select("<script>" +
            "select * from Footprint" +
            " where 1=1" +
            "<if test=\"findType == 0 \">"+
            " and userId=#{userId}" +
            "</if>" +
            "<if test=\"findType == 1 \">"+
            " and myId=#{userId}" +
            "</if>" +
            " order by time desc" +
            "</script>")
    List<Footprint> findFootList(@Param("userId") long userId,@Param("findType") long findType);

    /***
     * 查询在线的人
     * @param userId   当前登录者用户ID
     * @return
     */
    @Select("<script>" +
            "select * from Footprint" +
            " where 1=1" +
            " and userId=#{userId}" +
            " and awayTime is null"+
            " order by time desc" +
            "</script>")
    List<Footprint> findOnlineList(@Param("userId") long userId);

}
