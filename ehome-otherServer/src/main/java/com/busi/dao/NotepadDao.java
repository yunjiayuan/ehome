package com.busi.dao;

import com.busi.entity.Notepad;
import com.busi.entity.NotepadFestival;
import com.busi.entity.NotepadLunar;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 记事本Dao
 * author：zhaojiajie
 * create time：2018-10-12 11:57:04
 */
@Mapper
@Repository
public interface NotepadDao {

    /***
     * 新增
     * @param notepad
     * @return
     */
    @Insert("insert into notepad(userId,addType,content,thenTime,thisDateId,imgUrls,alarmTime,remindType,time,videoCover,videoUrl,users) " +
            "values (#{userId},#{addType},#{content},#{thenTime},#{thisDateId},#{imgUrls},#{alarmTime},#{remindType},#{time},#{videoCover},#{videoUrl},#{users})")
    @Options(useGeneratedKeys = true)
    int add(Notepad notepad);

    /***
     * 更新
     * @param notepad
     * @return
     */
    @Update("<script>" +
            "update notepad set" +
            "<if test=\"content != null and content != ''\">" +
            " content=#{content}," +
            "</if>" +
            "<if test=\"addType ==1\">" +
            " imgUrls=#{imgUrls}," +
            " videoCover=#{videoCover}," +
            " videoUrl=#{videoUrl}," +
            "</if>" +
            "<if test=\"addType !=1\">" +
            " alarmTime=#{alarmTime}," +
            " remindType=#{remindType}," +
            "</if>" +
            " userId=#{userId}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int update(Notepad notepad);

    /***
     * 查询用户记事
     * @param userId
     */
    @Select("select * from notepad where userId=#{userId} and thisDateId=#{thisDateId} and addType=1")
    Notepad findDayInfo(@Param("userId") long userId, @Param("thisDateId") long thisDateId);

    /***
     * 统计该用户当天日程数量
     * @param userId
     * @param type 0日程 1记事
     * @return
     */
    @Select("<script>" +
            "select count(id) from notepad" +
            " where userId=#{userId}" +
            " and addType = #{type}" +
            " and TO_DAYS(time)=TO_DAYS(NOW())" +
            "</script>")
    int findNum(@Param("userId") long userId, @Param("type") long type);

    /***
     * 删除
     * @param id
     * @return
     */
    @Delete(("delete from notepad where id=#{id}"))
    int del(@Param("id") long id);

    /***
     * 根据ID查询
     * @param id
     */
    @Select("select * from Notepad where id = #{id}")
    Notepad findById(@Param("id") long id);

    /***
     * 按月查询带标记的日期
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from Notepad" +
            " where 1=1" +
            " and userId=#{userId}" +
            " and thisDateId BETWEEN #{startTime} and #{endTime}" +
            "</script>")
    List<Notepad> findIdentify(@Param("userId") long userId, @Param("startTime") long startTime, @Param("endTime") long endTime);

    /***
     * 获取我某天的记事
     * @param options 查询类型ID:默认0全部  1日程
     * @return
     */
    @Select("<script>" +
            "select * from Notepad" +
            " where 1=1" +
            " and userId=#{userId}" +
            " and thisDateId = #{thisDateId}" +
            "<if test=\"options == 1\">" +
            " and addType = 0" +
            "</if>" +
            " order by time desc" +
            "</script>")
    List<Notepad> findThisDateRecord(@Param("userId") long userId, @Param("thisDateId") long thisDateId, @Param("options") int options);

    /***
     * 分页查询我的记事
     * @param options 查询类型ID 0日程1记事
     * @return
     */
    @Select("<script>" +
            "select * from Notepad" +
            " where 1=1" +
            " and userId=#{userId}" +
            " and addType = #{options}" +
            " order by time desc" +
            "</script>")
    List<Notepad> findList(@Param("userId") long userId, @Param("options") int options);

    /***
     * 获取指定年份黄历记载
     * @return
     */
    @Select("<script>" +
            "select * from NotepadLunar" +
            " where 1=1" +
            " <![CDATA[ and gregorianDatetime >= DATE_FORMAT(#{num},\"%Y-%m-%d %T\") and gregorianDatetime <= DATE_FORMAT(#{num2},\"%Y-%m-%d %T\") ]]>" +
            "</script>")
    List<NotepadLunar> findAlmanac(@Param("num") String num, @Param("num2") String num2);

    /**
     * 查询黄历详情
     *
     * @param calendar; //查询黄历详情：格式20180101
     * @return
     */
    @Select("<script>" +
            "select * from NotepadLunar" +
            " where 1=1" +
            " and gregorianDatetime = DATE_FORMAT(#{calendar},\"%Y-%m-%d %T\")" +
            "</script>")
    NotepadLunar findDetails(@Param("calendar") int calendar);

    /***
     * 更新黄历
     * @param notepad
     * @return
     */
    @Update("<script>" +
            "update NotepadLunar set" +
            " friends=#{friends}," +
            " partner=#{partner}," +
            " party=#{party}," +
            " travelFar=#{travelFar}," +
            " dressing=#{dressing}" +
            " where gregorianDatetime = #{gregorianDatetime}" +
            "</script>")
    int updateNotepadLunar(NotepadLunar notepad);

    /***
     * 获取指定年份法定节假日加班日安排时间
     * @param thisYearId
     */
    @Select("select * from NotepadFestival where thisYearId = #{thisYearId}")
    NotepadFestival findCalendarsHoliday(@Param("thisYearId") long thisYearId);

    /***
     * 获取我某天的记事
     * @param options 查询类型ID:默认0日程1记事
     * @return
     */
    @Select("<script>" +
            "select * from Notepad" +
            " where 1=1" +
            " and userId=#{userId}" +
            " and thisDateId = #{thisDateId}" +
            " and addType = #{options}" +
            "</script>")
    List<Notepad> findThisDateId(@Param("userId") long userId, @Param("thisDateId") long thisDateId, @Param("options") int options);

}
