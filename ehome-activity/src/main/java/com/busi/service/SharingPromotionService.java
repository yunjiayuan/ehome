package com.busi.service;

import com.busi.dao.SharingPromotionDao;
import com.busi.entity.PageBean;
import com.busi.entity.ShareRedPacketsInfo;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 分享红包
 * @author: ZHaoJiaJie
 * @create: 2018-09-27 16:20
 */
@Service
public class SharingPromotionService {

    @Autowired
    private SharingPromotionDao sharingPromotionDao;

    /***
     * 新增
     * @param shareRedPacketsInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(ShareRedPacketsInfo shareRedPacketsInfo) {
        return sharingPromotionDao.add(shareRedPacketsInfo);
    }

    /***
     * 返回红包总数
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public long findNum(long userId) {
        return sharingPromotionDao.findNum(userId);
    }

    /***
     * 返回红包总金额
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public double findSum(long userId) {
        return sharingPromotionDao.findSum(userId);
    }

    /***
     * 分页查询
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<ShareRedPacketsInfo> findList(int page, int count, long userId) {
        List<ShareRedPacketsInfo> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = sharingPromotionDao.findList(userId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 返回分享者分享人数
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public long findPeople(long userId) {
        return sharingPromotionDao.findPeople(userId);
    }

}
