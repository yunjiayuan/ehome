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
     * 新增喂鸟历史记录
     * @param birdFeedingRecord
     * @return
     */
    @Insert("insert into birdFeedingRecord(userId,visitId,time) " +
            "values (#{userId},#{visitId},#{time})")
    @Options(useGeneratedKeys = true)
    int addJourna(BirdFeedingRecord birdFeedingRecord);

    /***
     * 新增喂鸟互动次数
     * @param birdInteraction
     * @return
     */
    @Insert("insert into birdInteraction(userId,visitId,feedBirdTotalCount) " +
            "values (#{userId},#{visitId},#{feedBirdTotalCount})")
    @Options(useGeneratedKeys = true)
    int addInteraction(BirdInteraction birdInteraction);

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
     * 新增砸蛋记录
     * @param birdEggSmash
     * @return
     */
    @Insert("insert into BirdEggSmash(myId,userId,eggType,time) " +
            "values (#{myId},#{userId},#{eggType},#{time})")
    @Options(useGeneratedKeys = true)
    int addEgg(BirdEggSmash birdEggSmash);

    /***
     * 新增中奖记录
     * @param theWinners
     * @return
     */
    @Insert("insert into BirdTheWinners(eggType,userId,grade,time,issue,cost) " +
            "values (#{eggType},#{userId},#{grade},#{time},#{issue},#{cost})")
    @Options(useGeneratedKeys = true)
    int addWinners(BirdTheWinners theWinners);

    /***
     * 更新喂鸟者数据
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
     * 更新被喂者数据
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
     * 更新产蛋时间产蛋状态
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
     * 更新产蛋状态产蛋数
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
    int updateUsc(BirdFeedingData birdFeedingData);

    /***
     * 更新互动次数
     * @param birdInteraction
     * @return
     */
    @Update("<script>" +
            "update BirdInteraction set" +
            " feedBirdTotalCount=#{feedBirdTotalCount}" +
            " where 1=1" +
            " and userId=#{userId}" +
            " and visitId=#{visitId}" +
            "</script>")
    int updateMyb(BirdInteraction birdInteraction);

    /***
     * 更新对方家蛋状态
     * @param birdFeedingData
     * @return
     */
    @Update("<script>" +
            "update BirdFeedingData set" +
            " startLayingTime=#{startLayingTime}," +
            " eggState=#{eggState}," +
            " birdBeFeedTotalCount=#{birdBeFeedTotalCount}," +
            " beenFeedBirdTotalCount=#{beenFeedBirdTotalCount}" +
            " where userId=#{userId}" +
            "</script>")
    int updateUserEgg(BirdFeedingData birdFeedingData);

    /***
     * 删除喂鸟记录
     * @param id
     * @return
     */
    @Delete("<script>" +
            "delete from BirdFeedingRecord" +
            " where id =#{id}" +
            "</script>")
    int del(@Param("id") long id);

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

    /***
     * 根据userId查询喂鸟记录
     * @param userId
     */
    @Select("select * from BirdFeedingData where userId=#{userId}")
    BirdFeedingData findUserById(@Param("userId") long userId);

    /***
     * 根据用户Id查询双方的互动记录
     * @param userId
     */
    @Select("select * from BirdInteraction where userId=#{userId} and visitId=#{visitId}")
    BirdInteraction findInterac(@Param("userId") long userId, @Param("visitId") long visitId);

    /***
     * 分页查询喂鸟记录 默认按时间降序排序
     * @param userId
     * @param state
     * @return
     */
    @Select("<script>" +
            "select * from BirdFeedingRecord" +
            " where 1=1" +
            "<if test=\"state == 0\">" +
            " and visitId=#{userId}" +
            "</if>" +
            "<if test=\"state == 1\">" +
            " and userId=#{userId}" +
            "</if>" +
            " order by time desc" +
            "</script>")
    List<BirdFeedingRecord> findList(@Param("userId") long userId, @Param("state") int state);

    /***
     * 分页查询砸蛋记录 默认按时间降序排序
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from BirdEggSmash" +
            " where myId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<BirdEggSmash> findEggList(@Param("userId") long userId);

    /***
     * 分页查询互动次数
     * @param userId
     * @param state
     * @return
     */
    @Select("<script>" +
            "select * from BirdInteraction" +
            " where 1=1" +
            "<if test=\"state == 0\">" +
            " and visitId=#{userId}" +
            " and userId in" +
            "<foreach collection='users' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</if>" +
            "<if test=\"state == 1\">" +
            " and userId=#{userId}" +
            " and visitId in" +
            "<foreach collection='users' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</if>" +
            "</script>")
    List<BirdInteraction> findUserList(@Param("userId") long userId, @Param("users") String[] users, @Param("state") int state);

    /***
     * 查询最新一期奖品
     * @param eggType 蛋类型 0不限 1金蛋2 银蛋
     * mybatis查询的时候，需要用到运算符 小于号：< ，在mybatis配置文件里面，这种会被认为是标签，所以解析错误.
     * 解决方法,用<![CDATA[ ]]>将有<,的代码包起来。
     * @return
     */
    @Select("<script>" +
            "select * from BirdPrize" +
            " where <![CDATA[ eggType=#{eggType}" +
            " and #{time} >= startTime and #{time} <= endTime" +
            " order by grade asc ]]>" +
            "</script>")
    List<BirdPrize> findNewList(@Param("eggType") int eggType, @Param("time") int time);

    /***
     * 查询指定一期奖品
     * @param eggType 蛋类型 0不限 1金蛋2 银蛋
     * @param issue 期号
     * @return
     */
    @Select("<script>" +
            "select * from BirdPrize" +
            " where eggType=#{eggType}" +
            " and issue=#{issue}" +
            " order by grade asc" +
            "</script>")
    List<BirdPrize> findAppointList(@Param("eggType") int eggType, @Param("issue") int issue);

    /***
     * 分页查询自己奖品
     * @param userId  用户ID
     * @param eggType 蛋类型 0不限 1金蛋2 银蛋
     * @return
     */
    @Select("<script>" +
            "select * from BirdTheWinners" +
            " where 1=1" +
            "<if test=\"eggType > 0\">" +
            " and eggType=#{eggType}" +
            "</if>" +
            " order by time desc" +
            "</script>")
    List<BirdTheWinners> findWinnersList(@Param("userId") long userId, @Param("eggType") int eggType);

    /***
     * 分页查询指定一期奖品
     * @param issue  期号
     * @param eggType 蛋类型 0不限 1金蛋2 银蛋
     * @return
     */
    @Select("<script>" +
            "select * from BirdTheWinners" +
            " where 1=1" +
            " and eggType=#{eggType}" +
            " and issue=#{issue}" +
            " and (grade = 5 or grade = 6) " +
            " order by grade,time desc" +
            "</script>")
    List<BirdTheWinners> findPrizeList(@Param("eggType") int eggType, @Param("issue") int issue);

}
