package com.busi.dao;

import com.busi.entity.Administrators;
import com.busi.entity.AdministratorsAuthority;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 管理员
 * @author: ZHaoJiaJie
 * @create: 2020-09-27 16:58:08
 */
@Mapper
@Repository
public interface AdministratorsDao {

    /***
     * 新增管理员
     * @param selectionVote
     * @return
     */
    @Insert("insert into Administrators(userId,levels) " +
            "values (#{userId},#{levels})")
    @Options(useGeneratedKeys = true)
    int addAdministrator(Administrators selectionVote);

    /***
     * 根据userId查询管理员
     * @param userId
     * @return
     */
    @Select("select * from Administrators where userId = #{userId}")
    Administrators findByUserId(@Param("userId") long userId);

    /***
     * 查询管理员
     * @param levels
     * @return
     */
    @Select("select * from AdministratorsAuthority where levels = #{levels}")
    AdministratorsAuthority findUserId(@Param("levels") int levels);

    /***
     * 查询管理员列表
     * @param levels    等级
     * @return
     */
    @Select("<script>" +
            "select * from Administrators" +
            " where 1=1" +
            "<if test=\"levels >= 0\">" +
            " and levels = #{levels}" +
            "</if>" +
            " ORDER BY levels desc" +
            "</script>")
    List<Administrators> findAdministratorlist(@Param("levels") int levels);

    /***
     * 删除管理员
     * @param userId
     * @return
     */
    @Delete("<script>" +
            "delete from Administrators" +
            " where userId = #{userId}" +
            "</script>")
    int delAdministrator(@Param("userId") long userId);
}
