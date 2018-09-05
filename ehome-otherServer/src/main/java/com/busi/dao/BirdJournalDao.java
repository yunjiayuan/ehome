package com.busi.dao;

import com.busi.entity.BirdFeedingData;
import com.busi.entity.BirdFeedingRecord;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 喂鸟Dao
 * author：zhaojiajie
 * create time：2018-9-4 14:09:37
 */
@Mapper
@Repository
public interface BirdJournalDao {

    /***
     * 新增喂鸟记录
     * @param birdFeedingRecord
     * @return
     */
    @Insert("insert into birdFeedingRecord(myId,title,infoId,afficheType,time) " +
            "values (#{myId},#{title},#{infoId},#{afficheType},#{time})")
    @Options(useGeneratedKeys = true)
    int add(BirdFeedingRecord birdFeedingRecord);

    /***
     * 新增喂鸟详细记录
     * @param birdFeedingData
     * @return
     */
    @Insert("insert into birdFeedingData(userId,feedBirdTotalCount,beenFeedBirdTotalCount,lastFeedBirdDate,curFeedBirdTimes,feedBirdIds,birdBeFeedTotalCount,beenLastFeedBirdDate,layingTotalCount,startLayingTime,eggState) " +
            "values (#{userId},#{feedBirdTotalCount},#{beenFeedBirdTotalCount},#{lastFeedBirdDate},#{curFeedBirdTimes},#{feedBirdIds},#{birdBeFeedTotalCount},#{beenLastFeedBirdDate},#{layingTotalCount},#{startLayingTime},#{eggState})")
    @Options(useGeneratedKeys = true)
    int addData(BirdFeedingData birdFeedingData);

    /***
     * 更新
     * @param birdFeedingData
     * @return
     */
    @Update("<script>" +
            "update birdFeedingData set" +
            " curFeedBirdTimes=#{curFeedBirdTimes}," +
            " feedBirdTotalCount=#{feedBirdTotalCount}," +
            " lastFeedBirdDate=#{lastFeedBirdDate}," +
            " feedBirdIds=#{feedBirdIds}" +
            " where 1=1" +
            " and userId=#{userId}" +
            "</script>")
    int updateMya(BirdFeedingData birdFeedingData);

    /***
     * 更新
     * @param birdFeedingData
     * @return
     */
    @Update("<script>" +
            "update birdFeedingData set" +
            " birdBeFeedTotalCount=#{birdBeFeedTotalCount}," +
            " beenLastFeedBirdDate=#{beenLastFeedBirdDate}," +
            " beenFeedBirdTotalCount=#{beenFeedBirdTotalCount}" +
            " where 1=1" +
            " and userId=#{userId}" +
            "</script>")
    int updateUsa(BirdFeedingData birdFeedingData);

    /***
     * 更新
     * @param birdFeedingData
     * @return
     */
    @Update("<script>" +
            "update birdFeedingData set" +
            " startLayingTime=#{startLayingTime}," +
            " eggState=#{eggState}" +
            " where 1=1" +
            " and userId=#{userId}" +
            "</script>")
    int updateUsb(BirdFeedingData birdFeedingData);

    /***
     * 删除
     * @param ids
     * @param myId
     * @return
     */
//    @Delete("delete from collect where id in (#{ids}) and myId=#{myId}")
    @Delete("<script>" +
            "delete from collect" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and myId=#{myId}" +
            "</script>")
    int del(@Param("ids") String[] ids, @Param("myId") long myId);

    /***
     * 根据userId查询喂鸟记录
     * @param userId
     */
    @Select("select * from BirdFeedingData where userId=#{userId}")
    BirdFeedingData findUserById(@Param("userId") long userId);

    /***
     * 分页查询 默认按时间降序排序
     * @param myId
     * @return
     */
    @Select("<script>" +
            "select * from BirdFeedingRecord" +
            " where 1=1" +
            "<if test=\"myId > 0\">" +
            " and myId=#{myId}" +
            "</if>" +
            " order by time desc" +
            "</script>")
    List<BirdFeedingRecord> findList(@Param("myId") long myId);
}
