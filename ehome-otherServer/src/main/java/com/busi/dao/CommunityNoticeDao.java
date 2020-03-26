package com.busi.dao;

import com.busi.entity.CommunityNotice;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 公告
 * @author: ZHaoJiaJie
 * @create: 2020-03-23 16:58:32
 */
@Mapper
@Repository
public interface CommunityNoticeDao {
    /***
     * 新增
     * @param todayNotice
     * @return
     */
    @Insert("insert into CommunityNotice(communityId,userId, type, content, addTime, refreshTime) " +
            "values (#{communityId},#{userId},#{type},#{content},#{addTime},#{refreshTime})")
    @Options(useGeneratedKeys = true)
    int add(CommunityNotice todayNotice);

    /***
     * 更新
     * @param todayNotice
     * @return
     */
    @Update("<script>" +
            "update CommunityNotice set" +
            "<if test=\"content != null and content != ''\">" +
            " content=#{content}," +
            "</if>" +
            " refreshTime=#{refreshTime}" +
            " where id=#{id}" +
            "</script>")
    int editNotice(CommunityNotice todayNotice);

    /***
     * 删除
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from CommunityNotice" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int del(@Param("ids") String[] ids);

    /***
     * 分页查询
     * @param type
     * @return
     */
    @Select("<script>" +
            "select * from CommunityNotice" +
            " where type=#{type} and communityId=#{communityId}" +
            " order by refreshTime desc" +
            "</script>")
    List<CommunityNotice> findList(@Param("communityId") long communityId, @Param("type") int type);


}
