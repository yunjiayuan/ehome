package com.busi.service;

import com.busi.dao.RedPacketsInfoDao;
import com.busi.entity.PageBean;
import com.busi.entity.RedPacketsInfo;
import com.busi.utils.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 红包信息相关Service
 * author：SunTianJie
 * create time：2018-8-16 11:46:00
 */
@Service
public class RedPacketsInfoService{

    @Autowired
    private RedPacketsInfoDao redPacketsInfoDao;

    /***
     * 新增
     * @param redPacketsInfo
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int addRedPacketsInfo( RedPacketsInfo redPacketsInfo){
        return redPacketsInfoDao.addRedPacketsInfo(redPacketsInfo);
    }

    /***
     * 根据红包ID查询红包信息
     * @param userId
     * @param id
     * @return
     */
    public RedPacketsInfo findRedPacketsInfo(long userId,String id){
        return redPacketsInfoDao.findRedPacketsInfo(userId,id);
    }

    /***
     * 根据用户ID查询红包记录列表
     * @param userId
     * @return
     */
    public PageBean<RedPacketsInfo> findRedPacketsInfoList(long findType, long userId, int time,int page, int count){

        List<RedPacketsInfo> list;
        Page p = PageHelper.startPage(page,count);//为此行代码下面的第一行sql查询结果进行分页
        list = redPacketsInfoDao.findRedPacketsInfoList(findType,userId,time);
        return PageUtils.getPageBean(p,list);
    }

    /***
     * 发红包支付成功后更新红包支付状态
     * @param redPacketsInfo
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateRedPacketsPayStatus(RedPacketsInfo redPacketsInfo){
        return  redPacketsInfoDao.updateRedPacketsPayStatus(redPacketsInfo);
    }

    /***
     * 拆红包后更新留言
     * @param redPacketsInfo
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateRedPacketsReceiveMessage(RedPacketsInfo redPacketsInfo){
        return  redPacketsInfoDao.updateRedPacketsReceiveMessage(redPacketsInfo);
    }

    /***
     * 拆红包后更新红包状态
     * @param redPacketsInfo
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateRedPacketsStatus(RedPacketsInfo redPacketsInfo){
        return  redPacketsInfoDao.updateRedPacketsStatus(redPacketsInfo);
    }

    /***
     * 更改红包删除状态
     * @param userId
     * @param id
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateRedPacketsDelStatus(long userId,String id){
        return  redPacketsInfoDao.updateRedPacketsDelStatus(userId, id);
    }

}
