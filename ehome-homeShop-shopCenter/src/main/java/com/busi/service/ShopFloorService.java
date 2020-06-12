package com.busi.service;

import com.busi.dao.ShopFloorDao;
import com.busi.entity.PageBean;
import com.busi.entity.ShopFloor;
import com.busi.entity.ShopFloorStatistics;
import com.busi.entity.YongHuiGoodsSort;
import com.busi.utils.CommonUtils;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @program: ehome
 * @description: 楼店
 * @author: ZHaoJiaJie
 * @create: 2019-11-12 16:47
 */
@Service
public class ShopFloorService {

    @Autowired
    private ShopFloorDao shopCenterDao;

    /***
     * 新建楼店
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addHomeShop(ShopFloor homeShopCenter) {
        return shopCenterDao.addHomeShop(homeShopCenter);
    }

    /***
     * 更新楼店
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateHomeShop(ShopFloor homeShopCenter) {
        return shopCenterDao.updateHomeShop(homeShopCenter);
    }

    /***
     * 新建楼店统计
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addStatistics(ShopFloorStatistics homeShopCenter) {
        return shopCenterDao.addStatistics(homeShopCenter);
    }

    /***
     * 更新楼店统计
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int upStatistics(ShopFloorStatistics homeShopCenter) {
        return shopCenterDao.upStatistics(homeShopCenter);
    }

    /***
     * 更新保证金支付状态
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updatePayStates(ShopFloor homeShopCenter) {
        return shopCenterDao.updatePayStates(homeShopCenter);
    }


    /***
     * 更新店铺营业状态
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBusiness(ShopFloor homeShopCenter) {
        return shopCenterDao.updateBusiness(homeShopCenter);
    }

    /***
     * 更新店铺配货状态
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int upDistributionStatus(ShopFloor homeShopCenter) {
        return shopCenterDao.upDistributionStatus(homeShopCenter);
    }

    /***
     * 根据ID查询店铺
     * @param id
     * @return
     */
    public ShopFloor findId(long id) {
        return shopCenterDao.findId(id);
    }

    /***
     * 根据用户ID查询店铺状态
     * @param userId
     * @return
     */
    public ShopFloor findByUserId(long userId, String villageOnly) {
        return shopCenterDao.findByUserId(userId, villageOnly);
    }

    /***
     * 查询所有店铺
     * @return
     */
    public List<ShopFloor> findByIds(String[] villageOnly) {
        List<ShopFloor> list = null;
        list = shopCenterDao.findByIds(villageOnly);
        return list;
    }

    /***
     * 查询黑店列表
     * @param province     省 (经纬度>0时默认-1)
     * @param city      市 (经纬度>0时默认-1)
     * @param district    区 (经纬度>0时默认-1)
     * @param lat      纬度(省市区>0时默认-1)
     * @param lon      经度(省市区>0时默认-1)
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<ShopFloor> findNearbySFList(int province, int city, int district, double lat, double lon, int page, int count) {
        List<ShopFloor> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (lat > 0 && lon > 0) {
            list = shopCenterDao.findNearbySFList3(lat, lon);
        } else {
            list = shopCenterDao.findNearbySFList(null, province, city, district, -1);
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询黑店列表
     * @param shopState     店铺状态   -1不限 0未营业  1已营业
     * @param shopName     店铺名称 (默认null)
     * @param province     省
     * @param city      市
     * @param district    区
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<ShopFloor> findNearbySFList2(String date, int province, int city, int district, int shopState, String shopName, int page, int count) {
        List<ShopFloor> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (!CommonUtils.checkFull(shopName)) {
            list = shopCenterDao.findNearbySFList2(shopName);
        } else {
            if (CommonUtils.checkFull(date)) {
                date = null;
            }
            list = shopCenterDao.findNearbySFList(date, province, city, district, shopState);
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询黑店统计列表
     * @param province     省
     * @param city      市
     * @return
     */
    public ShopFloorStatistics findStatistics(int province, int city) {
        return shopCenterDao.findStatistics(province, city);
    }

    public ShopFloorStatistics findStatistics2(int province, int city) {
        return shopCenterDao.findStatistics2(province, city);
    }

    /***
     * 查询用户楼店
     * @param userId   用户
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<ShopFloor> findUserSFlist(long userId, int page, int count) {
        List<ShopFloor> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = shopCenterDao.findUserSFlist(userId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询楼店
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<ShopFloorStatistics> findRegionSFlist(int shopState, int page, int count) {
        List<ShopFloorStatistics> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = shopCenterDao.findRegionSFlist(shopState);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 新增永辉分类
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addYHSort(YongHuiGoodsSort homeShopCenter) {
        return shopCenterDao.addYHSort(homeShopCenter);
    }

    /***
     * 更新永辉分类
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeYHSort(YongHuiGoodsSort homeShopCenter) {
        return shopCenterDao.changeYHSort(homeShopCenter);
    }

    /***
     * 批量删除永辉分类
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delYHSort(String[] ids) {
        return shopCenterDao.delYHSort(ids);
    }

    /***
     * 查询商品分类
     * @param levelOne 商品1级分类   -2为不限
     * @param levelTwo 商品2级分类   -2为不限
     * @param levelTwo 商品3级分类   -2为不限
     * @param letter 商品分类首字母
     * @return
     */
    public List<YongHuiGoodsSort> findYHSort(int levelOne, int levelTwo, int levelThree, String letter) {
        List<YongHuiGoodsSort> list = null;
        list = shopCenterDao.findYHSort(levelOne, levelTwo, levelThree, letter);
        return list;
    }

    /***
     * 查询黑店数量（返回结构：总数、未配货的 、已配货的）
     * @param province     省
     * @param city      市
     * @param district    区
     * @return
     */
    public List<ShopFloor> findNum(int province, int city, int district) {
        List<ShopFloor> list;
        list = shopCenterDao.findNearbySFList(null, province, city, district, -1);
        return list;
    }
}
