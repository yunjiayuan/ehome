package com.busi.dao;

import com.busi.entity.RedPacketsInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 红包信息相关DAO
 * author：ZHJJ
 * create time：2018-8-16 11:38:27
 */
@Mapper
@Repository
public interface RedPacketsInfoDao {

    /***
     * 红包过期后更新红包状态
     * @param redPacketsInfo
     * @return
     */
    @Update("update redPacketsInfo set redPacketsStatus = #{redPacketsStatus} where sendUserId = #{sendUserId} and id = #{id}")
    int updateEmptyStatus(RedPacketsInfo redPacketsInfo);

    /***
     * 查询接收红包时间为空的
     * @return
     */
    @Select("select * from redPacketsInfo where redPacketsStatus=0 and receiveTime is null")
    List<RedPacketsInfo> findEmpty();
}
