package com.busi.dao;

import com.busi.entity.*;
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
     * 新增喂鸟详细记录
     * @param birdFeedingData
     * @return
     */
    @Insert("insert into birdFeedingData(userId,feedBirdTotalCount,beenFeedBirdTotalCount,lastFeedBirdDate,curFeedBirdTimes,feedBirdIds,birdBeFeedTotalCount,beenLastFeedBirdDate,layingTotalCount,startLayingTime,eggState) " +
            "values (#{userId},#{feedBirdTotalCount},#{beenFeedBirdTotalCount},#{lastFeedBirdDate},#{curFeedBirdTimes},#{feedBirdIds},#{birdBeFeedTotalCount},#{beenLastFeedBirdDate},#{layingTotalCount},#{startLayingTime},#{eggState})")
    @Options(useGeneratedKeys = true)
    int addData(BirdFeedingData birdFeedingData);

    /***
     * 条件删除喂鸟数据
     * @return
     */
    @Delete("<script>" +
            "delete from BirdFeedingData" +
            " where 1=1" +
            "<if test=\"birdCount == userIdstart\">" +
            " and userId &lt; (#{userIdstart} + #{count} * 7) and userId >= (#{userIdstart} + #{count} * 6)" +
            "</if>" +
            "<if test=\"birdCount == userIdstart + count\">" +
            " and userId &lt; (#{userIdstart} + #{count} * 8) and userId >= (#{userIdstart} + #{count} * 7)" +
            "</if>" +
            "<if test=\"birdCount == userIdstart + count * 2\">" +
            " and userId &lt; (#{userIdstart} + #{count}) and userId >= #{userIdstart}" +
            "</if>" +
            "<if test=\"birdCount == userIdstart + count * 3\">" +
            " and userId &lt; (#{userIdstart} + #{count} * 2) and userId >= (#{userIdstart} + #{count})" +
            "</if>" +
            "<if test=\"birdCount == userIdstart + count * 4\">" +
            " and userId &lt; (#{userIdstart} + #{count} * 3) and userId >= (#{userIdstart} + #{count} * 2)" +
            "</if>" +
            "<if test=\"birdCount == userIdstart + count * 5\">" +
            " and userId &lt; (#{userIdstart} + #{count} * 4) and userId >= (#{userIdstart} + #{count} * 3)" +
            "</if>" +
            "<if test=\"birdCount == userIdstart + count * 6\">" +
            " and userId &lt; (#{userIdstart} + #{count} * 5) and userId >= (#{userIdstart} + #{count} * 4)" +
            "</if>" +
            "<if test=\"birdCount == userIdstart + count * 7\">" +
            " and userId &lt; (#{userIdstart} + #{count} * 6) and userId >= (#{userIdstart} + #{count} * 5)" +
            "</if>" +
            "</script>")
    int batchDel(@Param("birdCount") int birdCount, @Param("userIdstart") int userIdstart, @Param("count") int count);

}
