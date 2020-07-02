package com.busi.dao;

import com.busi.entity.TransferAccountsInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 转账信息相关DAO
 * author：SunTianJie
 * create time：2020-7-1 15:16:53
 */
@Mapper
@Repository
public interface TransferAccountsInfoDao {

    /***
     * 新增
     * @param transferAccountsInfo
     * @return
     */
    @Insert("insert into transferAccountsInfo (id,sendUserId,receiveUserId,transferAccountsMoney,sendMessage,sendTime,receiveTime,delStatus,payStatus,transferAccountsStatus) " +
            "values (#{id},#{sendUserId},#{receiveUserId},#{transferAccountsMoney},#{sendMessage},#{sendTime},#{receiveTime},#{delStatus},#{payStatus},#{transferAccountsStatus})")
//    @Options(useGeneratedKeys = true)
    int addTransferAccountsInfo(TransferAccountsInfo transferAccountsInfo);

    /***
     * 根据转账ID查询红包信息
     * @param userId
     * @param id
     * @return
     */
    @Select("select * from transferAccountsInfo where (sendUserId = #{userId} or receiveUserId = #{userId}) and id = #{id}")
    TransferAccountsInfo findTransferAccountsInfo(@Param("userId") long userId, @Param("id") String id);


    /***
     * 发送转账支付成功后更新红包支付状态
     * @param transferAccountsInfo
     * @return
     */
    @Update("update transferAccountsInfo set payStatus = #{payStatus} where sendUserId = #{sendUserId} and id = #{id}")
    int updateTransferAccountsInfoPayStatus(TransferAccountsInfo transferAccountsInfo);


    /***
     * 接收转账后更新转账订单状态
     * @param transferAccountsInfo
     * @return
     */
    @Update("update transferAccountsInfo set transferAccountsStatus = #{transferAccountsStatus},receiveTime = #{receiveTime} where receiveUserId = #{receiveUserId} and id = #{id}")
    int updateTransferAccountsInfoStatus(TransferAccountsInfo transferAccountsInfo);

    /***
     * 更改转账订单删除状态
     * @param userId
     * @param id
     * @return
     */
    @Update("update transferAccountsInfo set delStatus = #{delStatus} where  (sendUserId = #{userId} or receiveUserId = #{userId}) and id = #{id}")
    int updateTransferAccountsInfoDelStatus(@Param("userId") long userId, @Param("id") String id);

}
