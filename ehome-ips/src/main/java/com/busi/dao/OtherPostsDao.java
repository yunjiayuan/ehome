package com.busi.dao;

import com.busi.entity.OtherPosts;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 其他公告Dao
 * author：zhaojiajie
 * create time：2018-8-7 15:23:37
 */
@Mapper
@Repository
public interface OtherPostsDao {
    /***
     * 新增其他公告
     * @param otherPosts
     * @return
     */
    @Insert("insert into otherPosts(userId,title,content,refreshTime,addTime,auditType,deleteType,fraction) " +
            "values (#{userId},#{title},#{content},#{refreshTime},#{addTime},#{auditType},#{deleteType},#{fraction})")
    @Options(useGeneratedKeys = true)
    int add(OtherPosts otherPosts);

    /***
     * 删除
     * @param id
     * @param userId
     * @return
     */
    @Delete(("delete from otherPosts where id=#{id} and userId=#{userId}"))
    int del(@Param("id") long id , @Param("userId") long userId);

    /***
     * 更新其他公告信息
     * @param otherPosts
     * @return
     */
    @Update("<script>" +
            "update otherPosts set"+
            "<if test=\"title != null and title != ''\">"+
            " title=#{title}," +
            "</if>" +
            "<if test=\"content != null and content != ''\">"+
            " content=#{content}," +
            "</if>" +
            " fraction=#{fraction}," +
            " userId=#{userId}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int update(OtherPosts otherPosts);

    /***
     * 更新删除状态
     * @param otherPosts
     * @return
     */
    @Update("<script>" +
            "update otherPosts set"+
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId}"+
            "</script>")
    int updateDel(OtherPosts otherPosts);

    /***
     * 刷新公告时间
     * @param otherPosts
     * @return
     */
    @Update("<script>" +
            "update otherPosts set" +
            " refreshTime=#{refreshTime}" +
            " where id=#{id} and userId=#{userId}" +
            " and auditType = 2 and deleteType = 1" +
            "</script>")
    int updateTime(OtherPosts otherPosts);

    /***
     * 置顶公告
     * @param otherPosts
     * @return
     */
    @Update("<script>" +
            "update otherPosts set" +
            " frontPlaceType=#{frontPlaceType}" +
            " where id=#{id} and userId=#{userId} " +
            " and auditType = 2 and deleteType = 1" +
            "</script>")
    int setTop(OtherPosts otherPosts);

    /***
     * 统计当月置顶次数
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(*) from otherPosts" +
            " where DATE_FORMAT( refreshTime, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' )" +
            " and frontPlaceType > 0" +
            " and userId=#{userId}" +
            " and auditType = 2 and deleteType = 1" +
            "</script>")
    int statistics(@Param("userId") long userId);

    /***
     * 根据Id查询用户其他公告信息
     * @param id
     */
    @Select("select * from otherPosts where id=#{id} and deleteType=1 and auditType=2")
    OtherPosts findUserById(@Param("id") long id);

    /***
     * 分页查询 默认按时间降序排序
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from otherPosts" +
            " where 1=1" +
            "<if test=\"userId > 0\">"+
            " and userId=#{userId}" +
            "</if>" +
            " and auditType = 2"+
            " and deleteType = 1"+
            " order by refreshTime desc" +
            "</script>")
    List<OtherPosts> findList(@Param("userId") long userId);

}
