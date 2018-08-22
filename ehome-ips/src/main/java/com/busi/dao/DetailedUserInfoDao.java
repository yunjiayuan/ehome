package com.busi.dao;

import com.busi.entity.DetailedUserInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 用户详细资料DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface DetailedUserInfoDao {

    /***
     * 新增用户详细资料
     * @param detailedUserInfo
     * @return
     */
    @Insert("insert into DetailedUserInfo (userId,appearanceTag,somatotypes,height,feature,haircolor,introduction,temper,lifeStyle,appointment,matrimony,mateAge,mateSex,mateHeight,mateMaritalStatus,mateStudyRank,mateRegion,mateCharacter,bloodType,religion,workHabits,drinking,bigCost,romance,majors,lifeSkills,time) values (#{userId},#{appearanceTag},#{somatotypes},#{height},#{feature},#{haircolor},#{introduction},#{temper},#{lifeStyle},#{appointment},#{matrimony},#{mateAge},#{mateSex},#{mateHeight},#{mateMaritalStatus},#{mateStudyRank},#{mateRegion},#{mateCharacter},#{bloodType},#{religion},#{workHabits},#{drinking},#{bigCost},#{romance},#{majors},#{lifeSkills},#{time})")
    @Options(useGeneratedKeys = true)
    int add(DetailedUserInfo detailedUserInfo);

    /***
     * 根据userId查询用户详细信息
     * @param userId
     */
    @Select("select * from DetailedUserInfo where userId = #{userId}")
    DetailedUserInfo findDetailedUserById(@Param("userId") long userId);

    /***
     * 更新用户信息
     * @param detailedUserInfo
     * @return
     */
//    @Update(("update userInfo set name=#{name} where userId=#{userId}"))
    @Update("<script>" +
            "update detailedUserInfo set"+
            "<if test=\"appearanceTag != 0 \">"+
            " appearanceTag=#{appearanceTag}," +
            "</if>" +
            "<if test=\"somatotypes != 0 \">"+
            " somatotypes=#{somatotypes}," +
            "</if>" +
            "<if test=\"height != 0 \">"+
            " height=#{height}," +
            "</if>" +
            "<if test=\"haircolor != 0 \">"+
            " haircolor=#{haircolor}," +
            "</if>" +
            "<if test=\"temper != 0 \">"+
            " temper=#{temper}," +
            "</if>" +
            "<if test=\"lifeStyle != 0 \">"+
            " lifeStyle=#{lifeStyle}," +
            "</if>" +
            "<if test=\"appointment != 0 \">"+
            " appointment=#{appointment}," +
            "</if>" +
            "<if test=\"matrimony != 0 \">"+
            " matrimony=#{matrimony}," +
            "</if>" +
            "<if test=\"mateAge != 0 \">"+
            " mateAge=#{mateAge}," +
            "</if>" +
            "<if test=\"mateSex != 0 \">"+
            " mateSex=#{mateSex}," +
            "</if>" +
            "<if test=\"mateHeight != 0 \">"+
            " mateHeight=#{mateHeight}," +
            "</if>" +
            "<if test=\"mateMaritalStatus != 0 \">"+
            " mateMaritalStatus=#{mateMaritalStatus}," +
            "</if>" +
            "<if test=\"mateStudyRank != 0 \">"+
            " mateStudyRank=#{mateStudyRank}," +
            "</if>" +
            "<if test=\"mateCharacter != 0 \">"+
            " mateCharacter=#{mateCharacter}," +
            "</if>" +
            "<if test=\"bloodType != 0 \">"+
            " bloodType=#{bloodType}," +
            "</if>" +
            "<if test=\"religion != 0 \">"+
            " religion=#{religion}," +
            "</if>" +
            "<if test=\"mateHeight != 0 \">"+
            " mateHeight=#{mateHeight}," +
            "</if>" +
            "<if test=\"workHabits != 0 \">"+
            " workHabits=#{workHabits}," +
            "</if>" +
            "<if test=\"drinking != 0 \">"+
            " drinking=#{drinking}," +
            "</if>" +
            "<if test=\"bigCost != 0 \">"+
            " bigCost=#{bigCost}," +
            "</if>" +
            "<if test=\"romance != 0 \">"+
            " romance=#{romance}," +
            "</if>" +
            "<if test=\"majors != 0 \">"+
            " majors=#{majors}," +
            "</if>" +
            "<if test=\"feature != 0 \">"+
            " feature=#{feature}," +
            "</if>" +
            "<if test=\"lifeSkills != 0 \">"+
            " lifeSkills=#{lifeSkills}," +
            "</if>" +
            "<if test=\"introduction != null and introduction != ''\">"+
            " introduction=#{introduction}," +
            "</if>" +
            "<if test=\"mateRegion != null and mateRegion != ''\">"+
            " mateRegion=#{mateRegion}," +
            "</if>" +
            "<if test=\"time != null \">"+
            " time=#{time}" +
            "</if>" +
            " where userId=#{userId}"+
            "</script>")
    int update(DetailedUserInfo detailedUserInfo);
}
