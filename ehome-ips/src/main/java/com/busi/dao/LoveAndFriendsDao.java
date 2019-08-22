package com.busi.dao;

import com.busi.entity.LoveAndFriends;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import javax.ws.rs.GET;
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
            "update LoveAndFriends set" +
            "<if test=\"title != null and title != ''\">" +
            " title=#{title}," +
            "</if>" +
            "<if test=\"sex == 1 or sex == 2\">" +
            " sex=#{sex}," +
            "</if>" +
            "<if test=\"content != null and content != ''\">" +
            " content=#{content}," +
            "</if>" +
            "<if test=\"imgUrl != null and imgUrl != ''\">" +
            " imgUrl=#{imgUrl}," +
            "</if>" +
            "<if test=\"age >= 1 \">" +
            " age=#{age}," +
            "</if>" +
            "<if test=\"stature >= 1 \">" +
            " stature=#{stature}," +
            "</if>" +
            "<if test=\"education >= 1 \">" +
            " education=#{education}," +
            "</if>" +
            "<if test=\"marriage >= 1 \">" +
            " marriage=#{marriage}," +
            "</if>" +
            "<if test=\"income >= 1 \">" +
            " income=#{income}," +
            "</if>" +
            " locationProvince=#{locationProvince}," +
            " locationCity=#{locationCity}," +
            " locationDistrict=#{locationDistrict}," +
            " fraction=#{fraction}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int update(LoveAndFriends loveAndFriends);

    /***
     * 更新婚恋交友删除状态
     * @param loveAndFriends
     * @return
     */
    @Update("<script>" +
            "update loveAndFriends set" +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDel(LoveAndFriends loveAndFriends);

    /***
     * 刷新公告时间
     * @param loveAndFriends
     * @return
     */
    @Update("<script>" +
            "update loveAndFriends set" +
            " refreshTime=#{refreshTime}" +
            " where id=#{id} and userId=#{userId} " +
            " and auditType = 2 and deleteType = 1" +
            "</script>")
    int updateTime(LoveAndFriends loveAndFriends);

    /***
     * 更新浏览量
     * @param loveAndFriends
     * @return
     */
    @Update("<script>" +
            "update loveAndFriends set" +
            " seeNumber=#{seeNumber}" +
            " where id=#{id} and userId=#{userId} " +
            " and auditType = 2 and deleteType = 1" +
            "</script>")
    int updateSee(LoveAndFriends loveAndFriends);

    /***
     * 置顶公告
     * @param loveAndFriends
     * @return
     */
    @Update("<script>" +
            "update loveAndFriends set" +
            " frontPlaceType=#{frontPlaceType}" +
            " where id=#{id} and userId=#{userId} " +
            " and auditType = 2 and deleteType = 1" +
            "</script>")
    int setTop(LoveAndFriends loveAndFriends);

    /***
     * 统计当月置顶次数
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(*) from loveAndFriends" +
            " where DATE_FORMAT( refreshTime, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' )" +
            " and frontPlaceType > 0" +
            " and userId=#{userId}" +
            " and auditType = 2 and deleteType = 1" +
            "</script>")
    int statistics(@Param("userId") long userId);

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
     * @param screen  暂定按性别查询:0不限，1男，2女
     * @param sort   默认0智能排序，1时间倒序
     * @return
     */
//    @Select("select * from loveAndFriends where auditType = 2 and deleteType = 1 order by refreshTime")
    @Select("<script>" +
            "select * from loveAndFriends" +
            " where 1=1" +
            "<if test=\"sort == 0 \">" +
            " and sex!=#{sex}" +
            " and age!=#{age}" +
            " and income>=#{income}" +
            "</if>" +
            "<if test=\"sort == 1 and screen!=0\">" +
            " and sex=#{screen}" +
            "</if>" +
            "<if test=\"sort == 1 and screen == 0\">" +
            " and deleteType = 1" +
            "</if>" +
            " and auditType = 2" +
            " and deleteType = 1" +
            " order by refreshTime desc" +
            "</script>")
    List<LoveAndFriends> findList(@Param("screen") int screen, @Param("sort") int sort, @Param("sex") int sex, @Param("age") int age, @Param("income") int income);

    /***
     * 分页条件查询 按userId查询
     * @param userId   用户ID
     * @return
     */
    @Select("<script>" +
            "select * from loveAndFriends" +
            " where userId=#{userId}" +
            " and auditType = 2" +
            " and deleteType = 1" +
            " order by refreshTime desc" +
            "</script>")
    List<LoveAndFriends> findUList(@Param("userId") long userId);

    /***
     * 分页条件查询 按userId查询
     * @param userId   用户ID
     * @return
     */
    @Select("<script>" +
            "select * from loveAndFriends" +
            " where auditType = 2" +
            "<if test=\"userId != 0\">" +
            " and userId = #{userId}" +
            "</if>" +
            " and deleteType = 1" +
            " order by refreshTime desc" +
            "</script>")
    List<LoveAndFriends> findHList(@Param("userId") long userId);

}
