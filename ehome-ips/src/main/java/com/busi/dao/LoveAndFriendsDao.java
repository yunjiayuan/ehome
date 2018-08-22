package com.busi.dao;

import com.busi.entity.LoveAndFriends;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * LoveAndFriendsDao
 * author：zhaojiajie
 * create time：2018-8-1 18:21:44
 */
@Mapper
@Repository
public interface LoveAndFriendsDao {
    /***
     * 新增婚恋交友
     * @param loveAndFriends
     * @return
     */
    @Insert("insert into loveAndFriends(userId,title,content,imgUrl,sex,age,stature,education,marriage,income,locationProvince,locationCity,locationDistrict,refreshTime,releaseTime,auditType,deleteType,fraction) " +
            "values (#{userId},#{title},#{content},#{imgUrl},#{sex},#{age},#{stature},#{education},#{marriage},#{income},#{locationProvince},#{locationCity},#{locationDistrict},#{refreshTime},#{releaseTime},#{auditType},#{deleteType},#{fraction})")
    @Options(useGeneratedKeys = true)
    int add(LoveAndFriends loveAndFriends);

    /***
     * 删除
     * @param userId
     * @return
     */
    @Delete(("delete from loveAndFriends where userId=#{userId}"))
    int del(@Param("userId") long userId);

    /***
     * 更新婚恋交友信息
     * @param loveAndFriends
     * @return
     */
    @Update("<script>" +
            "update loveAndFriends set"+
            "<if test=\"title != null and title != ''\">"+
            " title=#{title}," +
            "</if>" +
            "<if test=\"sex == 1 or sex == 2\">"+
            " sex=#{sex}," +
            "</if>" +
            "<if test=\"content != null and content != ''\">"+
            " content=#{content}," +
            "</if>" +
            "<if test=\"imgUrl != null and imgUrl != ''\">"+
            " imgUrl=#{imgUrl}," +
            "</if>" +
            "<if test=\"age >= 1 \">"+
            " age=#{age}," +
            "</if>" +
            "<if test=\"stature >= 1 \">"+
            " stature=#{stature}," +
            "</if>" +
            "<if test=\"education >= 1 \">"+
            " education=#{education}," +
            "</if>" +
            "<if test=\"marriage >= 1 \">"+
            " marriage=#{marriage}," +
            "</if>" +
            "<if test=\"income >= 1 \">"+
            " income=#{income}," +
            "</if>" +
            " deleteType=#{deleteType}," +
            " locationProvince=#{locationProvince}," +
            " locationCity=#{locationCity}," +
            " locationDistrict=#{locationDistrict}" +
            " where id=#{id} and userId=#{userId}"+
            "</script>")
    int update(LoveAndFriends loveAndFriends);

    /***
     * 更新婚恋交友删除状态
     * @param loveAndFriends
     * @return
     */
    @Update("<script>" +
            "update loveAndFriends set"+
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId}"+
            "</script>")
    int updateDel(LoveAndFriends loveAndFriends);

    /***
     * 根据Id查询用户婚恋交友信息
     * @param id
     */
    @Select("select * from loveAndFriends where id = #{id} and auditType = 2 and deleteType = 1")
    LoveAndFriends findUserById(@Param("id") long id);

    /***
     * 根据Id查询用户婚恋交友信息
     * @param userId
     */
    @Select("select * from loveAndFriends where userId = #{userId} and auditType = 2 and deleteType = 1")
    LoveAndFriends findByIdUser(@Param("userId") long userId);

    /***
     * 分页条件查询 默认按时间降序排序
     * @param screen
     * @param sort
     * @return
     */
//    @Select("select * from loveAndFriends where auditType = 2 and deleteType = 1 order by refreshTime")
    @Select("<script>" +
            "select * from loveAndFriends" +
            " where 1=1" +
            "<if test=\"screen != 0 \">"+
            " and sex=#{screen}" +
            "</if>" +
            " and auditType = 2"+
            " and deleteType = 1"+
            " order by #{sort} desc" +
            "</script>")
    List<LoveAndFriends> findList(@Param("screen") int screen, @Param("sort") String sort);

}
