package com.busi.dao;

import com.busi.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 抽签Dao
 * @author: ZHaoJiaJie
 * @create: 2020-11-02 15:43:39
 */
@Mapper
@Repository
public interface ZhouGongDreamDao {

    /***
     * 统计该用户当天次数
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from ZhouGongDreamRecords" +
            " where 1=1 " +
            " and userId = #{userId}" +
            " and TO_DAYS(time)=TO_DAYS(NOW())" +
            "</script>")
    int findNum(@Param("userId") long userId);

    /***
     * 新增记录
     * @param prizesLuckyDraw
     * @return
     */
    @Insert("insert into ZhouGongDreamRecords(userId, time, dreamId, title) " +
            "values (#{userId},#{time},#{dreamId},#{title})")
    @Options(useGeneratedKeys = true)
    int add(ZhouGongDreamRecords prizesLuckyDraw);

    /***
     * 查询记录
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from ZhouGongDreamRecords" +
            " where userId=#{userId} " +
            " order by time desc" +
            "</script>")
    List<ZhouGongDreamRecords> findOweList(@Param("userId") long userId);

    /***
     * 条件查询
     * @param title 关键字
     * @param biglx 一级分类 ：人物、动物、植物、物品、活动、生活、自然、鬼神、建筑、其它
     * @param smalllx 二级分类 null查所有
     * @return
     */
    @Select("<script>" +
            "<if test=\"title != null and title != '' \">" +
            "select * from ZhouGongDream" +
            " where title LIKE CONCAT('%',#{title},'%')" +
            "</if>" +
            "<if test=\"title == null or title == '' \">" +
            "select * from ZhouGongDream" +
            " where biglx = #{biglx}" +
            "<if test=\"smalllx != null and smalllx != '' \">" +
            " and smalllx = #{smalllx}" +
            "</if>" +
            "</if>" +
            "</script>")
    List<ZhouGongDream> findDreamsSortList(@Param("title") String title, @Param("biglx") String biglx, @Param("smalllx") String smalllx);

    /***
     * 查询
     */
    @Select("select * from ZhouGongDream where id = #{id}")
    ZhouGongDream findGifts(long id);

    /***
     * 查询
     */
    @Select("select * from ZhouGongDreamSort where biglx = #{biglx}")
    ZhouGongDreamSort findDreamsTwoSort(String biglx);

    /***
     * 更新内容
     */
    @Update("<script>" +
            "update ZhouGongDream set" +
            " message=#{message}" +
            " where id=#{id}" +
            "</script>")
    int update(ZhouGongDream kitchen);

    /***
     * 查询全部
     */
    @Select("<script>" +
            "select * from ZhouGongDream" +
            "</script>")
    List<ZhouGongDream> findList();
}
