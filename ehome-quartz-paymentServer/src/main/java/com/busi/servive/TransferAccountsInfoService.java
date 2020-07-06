package com.busi.servive;

import com.busi.dao.TransferAccountsInfoDao;
import com.busi.entity.TransferAccountsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
     * 接收转账后更新转账订单状态
     * @param transferAccountsInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateTransferAccountsInfo(TransferAccountsInfo transferAccountsInfo) {
        return transferAccountsInfoDao.updateTransferAccountsInfoStatus(transferAccountsInfo);
    }

    /***
     * 查询接收转账时间为空的
     * @return
     */
    public List<TransferAccountsInfo> findEmpty() {
        List<TransferAccountsInfo> list;
        list = transferAccountsInfoDao.findEmpty();
        return list;
    }
}
