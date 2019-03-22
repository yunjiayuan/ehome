package com.busi.dao;

import com.busi.entity.Footprint;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 脚印DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface FootprintDao {

    /***
     * 查询离开时间为空的人
     * @return
     */
    @Select("<script>" +
            "select * from Footprint" +
            " where 1=1" +
            " and awayTime is null" +
            "</script>")
    List<Footprint> find();

    /***
     * 更新离开时间
     * @param footprint
     * @return
     */
    @Update("update Footprint set awayTime=#{awayTime} where myId=#{myId} and userId=#{userId} and awayTime is null")
    int update(Footprint footprint);

}
