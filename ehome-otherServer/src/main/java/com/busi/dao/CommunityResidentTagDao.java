package com.busi.dao;

import com.busi.entity.CommunityResidentTag;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 居委会标签Dao
 * author：ZJJ
 * create time：2020-03-30 16:14:20
 */
@Mapper
@Repository
public interface CommunityResidentTagDao {

    /***
     * 新增
     * @param residentTag
     * @return
     */
    @Insert("insert into CommunityResidentTag(communityId,tagName) " +
            "values (#{communityId},#{tagName})")
    @Options(useGeneratedKeys = true)
    int add(CommunityResidentTag residentTag);

    /***
     * 更新
     * @param residentTag
     * @return
     */
    @Update("<script>" +
            "update CommunityResidentTag set" +
            " tagName=#{tagName}" +
            " where id=#{id}" +
            "</script>")
    int update(CommunityResidentTag residentTag);

    /***
     * 查询标签列表
     */
    @Select("select * from CommunityResidentTag and communityId=#{id} ORDER BY id ASC")
    List<CommunityResidentTag> findList(@Param("id") long id);

    /***
     * 删除居委会标签
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from CommunityResidentTag" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int delTags(@Param("ids") String[] ids);
}
