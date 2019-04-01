package com.busi.service;

import com.busi.dao.GoodNumberDao;
import com.busi.entity.GoodNumber;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 预售靓号service
 * author：suntj
 * create time：2019-3-28 18:39:46
 */
@Service
public class GoodNumberService {

    @Autowired
    private GoodNumberDao goodNumberDao;

    /***
     * 新增
     * @param goodNumber
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(GoodNumber goodNumber) {
        return goodNumberDao.add(goodNumber);
    }

    /***
     * 更新靓号状态
     * @param goodNumber
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateStatus(GoodNumber goodNumber) {
        return goodNumberDao.update(goodNumber);
    }

    /**
     * 精确查找预售账号（根据省简称ID+门牌号查询）
     * @param proId        省简称ID
     * @param house_number 门牌号ID
     * @return
     */
    public GoodNumber findGoodNumberInfo(int proId,long house_number) {
        return goodNumberDao.findGoodNumberInfo(proId,house_number);
    }

    /***
     * 模糊查找预售账号（根据省简称ID+门牌号查询）
     * @param proId        省简称ID 默认-1
     * @param house_number 门牌号ID
     * @param page
     * @param count
     * @return
     */
    public PageBean<GoodNumber> findGoodNumberListByNumber(int proId,long house_number,int page,int count) {

        List<GoodNumber> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = goodNumberDao.findGoodNumberListByNumber(proId, house_number);

        return PageUtils.getPageBean(p, list);
    }

    /**
     * 条件查询预售靓号列表
     * @param proId       省简称ID 默认-1不限
     * @param theme       主题ID 默认-1不限
     * @param label       数字规则ID 默认-1不限
     * @param numberDigit 靓号位数ID 默认-1不限 (例如7表示7位)
     * @param orderType   省简称ID 默认 0不限 1按价格倒序 2按价格升序
     * @param page
     * @param count
     * @return
     */
    public PageBean<GoodNumber> findList(int proId,int theme, String label, int numberDigit,int orderType,int page,int count) {

        List<GoodNumber> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = goodNumberDao.findList(proId, theme, label,numberDigit,orderType);

        return PageUtils.getPageBean(p, list);
    }

}
