package com.busi.dao;

import com.busi.entity.CommunityRepair;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 报修
 * @author: ZHaoJiaJie
 * @create: 2020-04-08 17:09:30
 */
@Mapper
@Repository
public interface CommunityRepairDao {

    /***
     * 删除
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from CommunityRepair" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int delSetUp(@Param("ids") String[] ids);

    /***
     * 新增
     * @param communityHouse
     * @return
     */
    @Insert("insert into CommunityRepair(communityId,post,head,name) " +
            "values (#{communityId},#{post},#{head},#{name})")
    @Options(useGeneratedKeys = true)
    int addSetUp(CommunityRepair communityHouse);

    /***
     * 查询报修列表
     * @param type    type=0居委会  type=1物业
     * @param communityId   type=0时居委会ID  type=1时物业ID
     * @param userId   查询者
     * @return
     */
    @Select("<script>" +
            "select * from CommunityRepair" +
            " where 1=1" +
            "<if test=\"userId > 0\">" +
            " and userId = #{userId}" +
            "</if>" +
            " and type = #{type}" +
            " and communityId = #{communityId}" +
            " ORDER BY time desc" +
            "</script>")
    List<CommunityRepair> findSetUpList(@Param("type") int type, @Param("communityId") long communityId, @Param("userId") long userId);
}
