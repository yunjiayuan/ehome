package com.busi.dao;

import com.busi.entity.OnlineMusic;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 在线音乐Dao
 * author：zhaojiajie
 * create time：2018-10-31 13:24:05
 */
@Mapper
@Repository
public interface OnlineMusicDao {

    /***
     * 新增音乐
     * @param onlineMusic
     * @return
     */
    @Insert("insert into OnlineMusic(singer,songType,songName,lengthTime,coverUrl,musicUrl,grade,addTime) " +
            "values (#{singer},#{songType},#{songName},#{lengthTime},#{coverUrl},#{musicUrl},#{grade},#{addTime})")
    @Options(useGeneratedKeys = true)
    int add(OnlineMusic onlineMusic);

    /***
     * 根据Id查询
     * @param id
     */
    @Select("select * from OnlineMusic where id=#{id}")
    OnlineMusic findMusic(@Param("id") long id);

    /***
     * 查询歌曲列表
     * @param name  歌名或歌手
     * @return
     */
    @Select("<script>" +
            "select * from OnlineMusic" +
            " where 1=1" +
            " and singer LIKE CONCAT('%',#{name},'%')" +
            " or songName LIKE CONCAT('%',#{name},'%')" +
            " order by grade,id desc" +
            "</script>")
    List<OnlineMusic> findPaging(@Param("name") String name);

    /***
     * 查询歌曲列表
     * @param songType 歌曲类型：0.热歌榜 1.流行 2.纯音乐 3.摇滚 4.神曲 5.DJ 6.电音趴 7.说唱 8.国风 9.欧美（PS：仅在name为空时有效）
     * @return
     */
    @Select("<script>" +
            "select * from OnlineMusic" +
            " where 1=1" +
            " and songType = #{songType}" +
            " order by grade,id desc" +
            "</script>")
    List<OnlineMusic> findPaging2(@Param("songType") int songType);
}
