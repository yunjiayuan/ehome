package com.busi.dao;

import com.busi.entity.Collect;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 收藏Dao
 * author：zhaojiajie
 * create time：2018-8-24 15:36:17
 */
@Mapper
@Repository
public interface CollectDao {

    /***
     * 新增收藏
     * @param collect
     * @return
     */
    @Insert("insert into collect(myId,title,infoId,afficheType,time) " +
            "values (#{myId},#{title},#{infoId},#{afficheType},#{time})")
    @Options(useGeneratedKeys = true)
    int add(Collect collect);

    /***
     * 删除
     * @param ids
     * @param myId
     * @return
     */
    @Delete(("delete from collect where id in (#{ids}) and myId=#{myId}"))
    int del(@Param("ids") String ids , @Param("myId") long myId);

    /***
     * 根据Id统计被收藏次数
     * @param infoId
     * @param afficheType
     */
    @Select("select COUNT(id) from collect where infoId=#{infoId} and afficheType=#{afficheType}")
    int findUserById(@Param("infoId") long infoId,@Param("afficheType") int afficheType);

    /***
     * 分页查询 默认按时间降序排序
     * @param myId
     * @return
     */
    @Select("<script>" +
            "select * from collect" +
            " where 1=1" +
            "<if test=\"myId > 0\">"+
            " and myId=#{myId}" +
            "</if>" +
            " order by time desc" +
            "</script>")
    List<Collect> findList(@Param("myId") long myId);
}
