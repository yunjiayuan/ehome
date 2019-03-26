package com.busi.dao;

import com.busi.entity.CloudVideo;
import com.busi.entity.CloudVideoActivities;
import com.busi.entity.CloudVideoVote;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 云视频
 * @author: ZHaoJiaJie
 * @create: 2019-03-21 09:51
 */
@Mapper
@Repository
public interface CloudVideoDao {

    /***
     * 新增视频
     * @param cloudVideo
     * @return
     */
    @Insert("insert into CloudVideo(videoUrl,userId,videoCover,time,duration) " +
            "values (#{videoUrl},#{userId},#{videoCover},#{time},#{duration})")
    @Options(useGeneratedKeys = true)
    int addCloudVideo(CloudVideo cloudVideo);

    /***
     * 查询视频
     * @param id
     * @return
     */
    @Select("select * from CloudVideo where id = #{id}")
    CloudVideo findId(@Param("id") long id);

    /***
     * 删除视频
     * @param id
     * @return
     */
    @Delete("<script>" +
            "delete from CloudVideo" +
            " where id =#{id}" +
            "</script>")
    int delCloudVideo(@Param("id") long id);

    /***
     * 删除活动
     * @param id
     * @return
     */
    @Delete("<script>" +
            "delete from CloudVideoActivities" +
            " where id =#{id}" +
            "</script>")
    int del(@Param("id") long id);

    /***
     * 分页查询用户的云视频列表
     * @return
     */
    @Select("<script>" +
            "select * from CloudVideo" +
            " where 1=1" +
            " and userId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<CloudVideo> findCloudVideoList(@Param("userId") long userId);

    /***
     * 分页查询用户已参加的活动
     * @return
     */
    @Select("<script>" +
            "select * from CloudVideoActivities" +
            " where 1=1" +
            " and userId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<CloudVideoActivities> findCloudVideoList2(@Param("userId") long userId);

    /***
     * 查询是否已经参加
     * @param selectionType
     * @return
     */
    @Select("select * from CloudVideoActivities where userId = #{userId} and selectionType = #{selectionType} ")
    CloudVideoActivities findDetails(@Param("userId") long userId, @Param("selectionType") long selectionType);

    /***
     * 新增活动信息
     * @param cloudVideoActivities
     * @return
     */
    @Insert("insert into CloudVideoActivities(userId,selectionType,province,city,district,singer,songName,duration,sex,birthday,videoUrl" +
            ",videoCover,time,activityState) " +
            "values (#{userId},#{selectionType},#{province},#{city},#{district},#{singer},#{songName},#{duration},#{sex},#{birthday},#{videoUrl}" +
            ",#{videoCover},#{time},#{activityState})")
    @Options(useGeneratedKeys = true)
    int addSelection(CloudVideoActivities cloudVideoActivities);

    /***
     * 分页查询参加活动的人员
     * @param selectionType  活动分类 0云视频  (后续添加)
     * @return
     */
    @Select("<script>" +
            "select * from CloudVideoActivities" +
            " where 1=1" +
            " and selectionType = #{selectionType}" +
            " order by votesCounts desc" +
            "</script>")
    List<CloudVideoActivities> findPersonnelList(@Param("selectionType") long selectionType);

    /***
     * 新增投票
     * @param cloudVideoVote
     * @return
     */
    @Insert("insert into CloudVideoVote(myId,userId,selectionType,time) " +
            "values (#{myId},#{userId},#{selectionType},#{time})")
    @Options(useGeneratedKeys = true)
    int addVote(CloudVideoVote cloudVideoVote);

    /***
     * 更新投票数
     * @param cloudVideoActivities
     * @return
     */
    @Update("<script>" +
            "update CloudVideoActivities set" +
            " votesCounts=#{votesCounts}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateNumber(CloudVideoActivities cloudVideoActivities);

    /***
     * 分页查询投票历史
     * @param userId  用户ID
     * @param selectionType  活动分类 0云视频  (后续添加)
     * @return
     */
    @Select("<script>" +
            "select * from CloudVideoVote" +
            " where 1=1" +
            " and userId=#{userId}" +
            " and selectionType = #{selectionType}" +
            " order by time desc" +
            "</script>")
    List<CloudVideoVote> findVoteList(@Param("userId") long userId, @Param("selectionType") long selectionType);

    /***
     * 查询今天是否给该用户投过票
     * @param selectionType
     * @return
     */
    @Select("select * from CloudVideoVote where userId=#{userId} " +
            " and myId = #{myId} " +
            " and selectionType = #{selectionType}" +
            " and TO_DAYS(time)=TO_DAYS(NOW())"
    )
    CloudVideoVote findTicket(@Param("myId") long myId, @Param("userId") long userId, @Param("selectionType") int selectionType);
}
