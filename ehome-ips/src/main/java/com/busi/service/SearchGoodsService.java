package com.busi.service;

import com.busi.dao.SearchGoodsDao;
import com.busi.entity.SearchGoods;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: 寻人寻物失物招领Service
 * @author: ZHaoJiaJie
 * @create: 2018-08-10 14:36
 */
@Service
public class SearchGoodsService {

    @Autowired
    private SearchGoodsDao searchGoodsDao;

    /***
     * 新增
     * @param searchGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(SearchGoods searchGoods) {
        return searchGoodsDao.add(searchGoods);
    }

    /***
     * 删除
     * @param id
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(long id, long userId) {
        return searchGoodsDao.del(id, userId);
    }

    /***
     * 更新
     * @param searchGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(SearchGoods searchGoods) {
        return searchGoodsDao.update(searchGoods);
    }

    /***
     * 更新删除状态
     * @param searchGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(SearchGoods searchGoods) {
        return searchGoodsDao.updateDel(searchGoods);
    }

    /***
     * 更新公告状态
     * @param searchGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateStatus(SearchGoods searchGoods) {
        return searchGoodsDao.updateStatus(searchGoods);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public SearchGoods findUserById(long id) {
        return searchGoodsDao.findUserById(id);
    }

    /***
     * 分页查询
     * @param province  省
     * @param city  市
     * @param district  区
     * @param beginAge  开始年龄
     * @param endAge  结束年龄
     * @param missingSex  失踪人性别:1男,2女
     * @param searchType  查找类别:0不限 ,1寻人,2寻物,3失物招领
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<SearchGoods> findList(long userId, int province, int city, int district, int beginAge, int endAge, int missingSex, int searchType, int page, int count) {

        List<SearchGoods> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = searchGoodsDao.findList(userId,province, city, district, beginAge, endAge, missingSex, searchType);

        return PageUtils.getPageBean(p, list);
    }
}
