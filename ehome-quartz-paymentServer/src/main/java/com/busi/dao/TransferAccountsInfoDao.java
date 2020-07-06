package com.busi.dao;

import com.busi.entity.TransferAccountsInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 转账信息相关DAO
 * author：SunTianJie
 * create time：2020-7-1 15:16:53
 */
@Mapper
@Repository
public interface TransferAccountsInfoDao {

    /***
     * 接收转账后更新转账订单状态
     * @param transferAccountsInfo
     * @return
     */
    @Update("update transferAccountsInfo set transferAccountsStatus = #{transferAccountsStatus},receiveTime = #{receiveTime} where receiveUserId = #{receiveUserId} and id = #{id}")
    int updateTransferAccountsInfoStatus(TransferAccountsInfo transferAccountsInfo);

    /***
     * 查询接收转账时间为空的
     * @return
     */
    @Select("select * from TransferAccountsInfo where transferAccountsStatus=0 and receiveTime is null")
    List<TransferAccountsInfo> findEmpty();
}
