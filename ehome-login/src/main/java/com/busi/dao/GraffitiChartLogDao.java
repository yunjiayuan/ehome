package com.busi.dao;

import com.busi.entity.GraffitiChartLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 涂鸦记录DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface GraffitiChartLogDao {
    /***
     * 新增
     * @param graffitiChartLog
     * @return
     */
    @Insert("insert into GraffitiChartLog(myId,userId,graffitiHead,graffitiContent,time) values (#{myId},#{userId},#{graffitiHead},#{graffitiContent},#{time})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int add(GraffitiChartLog graffitiChartLog);

    /***
     * 分页查询
     */
    @Select("select * from GraffitiChartLog where myId = #{myId}")
    List<GraffitiChartLog> findList(@Param("myId") long myId);


}
