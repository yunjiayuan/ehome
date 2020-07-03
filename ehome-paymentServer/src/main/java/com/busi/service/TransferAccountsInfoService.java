package com.busi.service;

import com.busi.dao.TransferAccountsInfoDao;
import com.busi.entity.TransferAccountsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 转账信息相关Service
 * author：SunTianJie
 * create time：2020-7-1 15:06:10
 */
@Service
public class TransferAccountsInfoService {

    @Autowired
    private TransferAccountsInfoDao transferAccountsInfoDao;

    /***
     * 新增
     * @param transferAccountsInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addTransferAccountsInfo(TransferAccountsInfo transferAccountsInfo) {
        return transferAccountsInfoDao.addTransferAccountsInfo(transferAccountsInfo);
    }

    /***
     * 根据转账订单ID查询转账订单信息
     * @param userId
     * @param id
     * @return
     */
    public TransferAccountsInfo findTransferAccountsInfo(long userId, String id) {
        return transferAccountsInfoDao.findTransferAccountsInfo(userId, id);
    }

    /***
     * 发转账支付成功后更新支付状态
     * @param transferAccountsInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateTransferAccountsInfoPayStatus(TransferAccountsInfo transferAccountsInfo) {
        return transferAccountsInfoDao.updateTransferAccountsInfoPayStatus(transferAccountsInfo);
    }

    /***
     * 接收转账后更新转账订单状态
     * @param transferAccountsInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateTransferAccountsInfo(TransferAccountsInfo transferAccountsInfo) {
        return transferAccountsInfoDao.updateTransferAccountsInfoStatus(transferAccountsInfo);
    }

    /***
     * 更改转账订单删除状态
     * @param userId
     * @param id
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateTransferAccountsInfoDelStatus(long userId, String id) {
        return transferAccountsInfoDao.updateTransferAccountsInfoDelStatus(userId, id);
    }

}
