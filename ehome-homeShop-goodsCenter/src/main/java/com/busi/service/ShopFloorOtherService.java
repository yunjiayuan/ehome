package com.busi.service;

import com.busi.dao.ShopFloorOtherDao;
import com.busi.entity.PageBean;
import com.busi.entity.ShopFloorGoodsCollection;
import com.busi.entity.ShopFloorGoodsLook;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: 浏览&收藏记录
 * @author: ZHaoJiaJie
 * @create: 2020-03-10 16:27:31
 */
@Service
public class ShopFloorOtherService {

    @Autowired
    private ShopFloorOtherDao shopFloorOtherDao;

    /***
     * 新增
     * @param look
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int addLook( ShopFloorGoodsLook look){
        return shopFloorOtherDao.addLook(look);
    }

    /***
     * 删除
     * @param ids
     * @param userId
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int delLook(String[] ids ,long userId){
        return shopFloorOtherDao.delLook(ids,userId);
    }

    /***
     * 分页查询
     * @param myId 用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<ShopFloorGoodsLook> findLookList(long myId, int page, int count) {

        List<ShopFloorGoodsLook> list;
        Page p = PageHelper.startPage(page,count);//为此行代码下面的第一行sql查询结果进行分页
        list = shopFloorOtherDao.findLookList(myId);

        return PageUtils.getPageBean(p,list);
    }
    /***
     * 新增
     * @param collect
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addCollection(ShopFloorGoodsCollection collect) {
        return shopFloorOtherDao.addCollection(collect);
    }

    /***
     * 删除
     * @param ids
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delCollection(String[] ids, long userId) {
        return shopFloorOtherDao.delCollection(ids, userId);
    }

    /***
     * 根据用户&商品ID查询
     * @param id
     * @param userId
     * @return
     */
    public ShopFloorGoodsCollection findUserId(long id, long userId) {
        return shopFloorOtherDao.findUserId(id, userId);
    }

    /***
     * 分页查询
     * @param myId 用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<ShopFloorGoodsCollection> findCollectionList(long myId, int page, int count) {

        List<ShopFloorGoodsCollection> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = shopFloorOtherDao.findCollectionList(myId);

        return PageUtils.getPageBean(p, list);
    }
}
