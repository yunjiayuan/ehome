package com.busi.service;

import com.busi.dao.PartnerBuyDao;
import com.busi.entity.PageBean;
import com.busi.entity.PartnerBuyGoods;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 合伙购相关Service
 * author：ZhaoJiaJie
 * create time：2020-04-17 18:35:18
 */
@Service
public class PartnerBuyService {

    @Autowired
    private PartnerBuyDao goodsCenterDao;

    /***
     * 新增
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(PartnerBuyGoods homeShopGoods) {
        return goodsCenterDao.add(homeShopGoods);
    }

    /***
     * 更新
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(PartnerBuyGoods homeShopGoods) {
        return goodsCenterDao.update(homeShopGoods);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public PartnerBuyGoods findUserById(long id) {
        return goodsCenterDao.findUserById(id);
    }

    /***
     * 查询列表
     * @param sort  查询条件:0全部，1我发起的，2我参与的
     * @param userId  查询者
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<PartnerBuyGoods> findDishesSortList(int sort, long userId, int page, int count) {
        List<PartnerBuyGoods> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = goodsCenterDao.findDishesSortList(sort, "#" + userId + "#", userId);
        return PageUtils.getPageBean(p, list);
    }

}
