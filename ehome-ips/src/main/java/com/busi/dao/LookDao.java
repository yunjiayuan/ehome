package com.busi.dao;

import com.busi.entity.Look;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 浏览记录Dao
 * author：zhaojiajie
 * create time：2018-8-24 15:36:17
 */
@Mapper
@Repository
public interface LookDao {

    /***
     * 新增浏览记录
     * @param look
     * @return
     */
    @Insert("insert into look(myId,title,infoId,afficheType,time) " +
            "values (#{myId},#{title},#{infoId},#{afficheType},#{time})")
    @Options(useGeneratedKeys = true)
    int add(Look look);

    /***
     * 删除
     * @param ids
     * @param myId
     * @return
     */
    @Delete(("delete from look where id in (#{ids}) and myId=#{myId}"))
    int del(@Param("ids") String ids , @Param("myId") long myId);

    /***
     * 分页查询 默认按时间降序排序
     * @param myId
     * @return
     */
    @Select("<script>" +
            "select * from look" +
            " where 1=1" +
            "<if test=\"myId > 0\">"+
            " and myId=#{myId}" +
            "</if>" +
            " order by time desc" +
            "</script>")
    List<Look> findList(@Param("myId") long myId);
}
