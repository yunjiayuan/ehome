package com.busi.dao;

import com.busi.entity.InteractiveGameLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 互动游戏胜负记录相关DAO
 * author：SunTianJie
 * create time：2018-8-16 11:38:27
 */
@Mapper
@Repository
public interface InteractiveGameLogDao {

    /***
     * 新增
     * @param interactiveGameLog
     * @return
     */
    @Insert("insert into InteractiveGameLog (myId,userId,homePoint,gameResults,gameType,time) values (#{myId},#{userId},#{homePoint},#{gameResults},#{gameType},#{time})")
    @Options(useGeneratedKeys = true)
    int addInteractiveGameLog(InteractiveGameLog interactiveGameLog);

}
