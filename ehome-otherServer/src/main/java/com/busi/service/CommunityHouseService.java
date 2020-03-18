package com.busi.service;

import com.busi.dao.CommunityHouseDao;
import com.busi.entity.CommunityHouse;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 居委会房屋信息service
 * @author: suntj
 * @create: 2020-03-18 11:32:23
 */
@Service
public class CommunityHouseService {

    @Autowired
    private CommunityHouseDao communityHouseDao;


    /***
     * 删除房屋信息
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delResident(String[] ids,long userId) {
        return communityHouseDao.delResident(ids,userId);
    }

    /***
     * 更新房屋信息
     * @param communityHouse
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeCommunityHouse(CommunityHouse communityHouse) {
        return communityHouseDao.changeCommunityHouse(communityHouse);
    }

    /***
     * 新增房屋信息
     * @param communityHouse
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addCommunityHouse(CommunityHouse communityHouse) {
        return communityHouseDao.addCommunityHouse(communityHouse);
    }

    /***
     * 根据ID查询房屋信息
     * @param id 房屋ID
     * @return
     */
    public CommunityHouse findCommunityHouse(long id) {
        return communityHouseDao.findCommunityHouse(id);
    }

    /***
     * 查询房屋信息列表
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<CommunityHouse> findCommunityHouseList(long communityId,long userId,int page, int count) {
        List<CommunityHouse> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = communityHouseDao.findCommunityHouseList(communityId,userId);
        return PageUtils.getPageBean(p, list);
    }

}
