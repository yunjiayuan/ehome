package com.busi.dao;

import com.busi.entity.ChatSquare;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 聊天广场马甲DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface ChatSquareDao {

    /***
     * 新增马甲
     * @param chatSquare
     * @return
     */
    @Insert("insert into ChatSquare (userId,name,sex,birthday,country,province,city,district,studyRank,maritalStatus,gxqm) " +
            "values (#{userId},#{name},#{sex},#{birthday},#{country},#{province},#{city},#{district},#{studyRank},#{maritalStatus},#{gxqm})")
    @Options(useGeneratedKeys = true)
    int add(ChatSquare chatSquare);

    /***
     * 更新用户马甲信息
     * @param chatSquare
     * @return
     */
//    @Update(("update userInfo set name=#{name} where userId=#{userId}"))
    @Update("<script>" +
            "update chatSquare set"+
            "<if test=\"name != null and name != ''\">"+
            " name=#{name}," +
            "</if>" +
            "<if test=\"sex != 0 \">"+
            " sex=#{sex}," +
            "</if>" +
            "<if test=\"birthday != null\">"+
            " birthday=#{birthday}," +
            "</if>" +
            "<if test=\"country != -1 \">"+
            " country=#{country}," +
            "</if>" +
            "<if test=\"province != -1 \">"+
            " province=#{province}," +
            "</if>" +
            "<if test=\"city != -1 \">"+
            " city=#{city}," +
            "</if>" +
            "<if test=\"district != -1 \">"+
            " district=#{district}," +
            "</if>" +
            "<if test=\"studyRank != 0 \">"+
            " studyRank=#{studyRank}," +
            "</if>" +
            "<if test=\"maritalStatus != 0 \">"+
            " maritalStatus=#{maritalStatus}," +
            "</if>" +
            " gxqm=#{gxqm}" +
            " where userId=#{userId}"+
            "</script>")
    int update(ChatSquare chatSquare);

    /***
     * 根据userId查询用户马甲信息
     * @param userId
     */
    @Select("select * from chatSquare where userId = #{userId}")
    ChatSquare findChatSquareByUserId(@Param("userId") long userId);

    /***
     * 删除马甲信息
     * @param userId
     */
    @Delete("delete from chatSquare where userId = #{userId}")
    int delChatSquareByUserId(@Param("userId") long userId);

    /***
     * 查询指定用户集合的马甲信息列表
     * @param users
     * @return
     */
    @Select("<script>" +
            "select * from chatSquare" +
            " where 1=1" +
            " and userId in" +
            "<foreach collection='users' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<ChatSquare> findChatSquareUserInfo(@Param("users") String[] users);
}
