package com.busi.service;

import com.busi.dao.ZhouGongDreamDao;
import com.busi.entity.*;
import com.busi.utils.CommonUtils;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 解梦Service
 * @author: ZHaoJiaJie
 * @create: 2020-11-02 15:38:46
 */
@Service
public class ZhouGongDreamService {

    @Autowired
    private ZhouGongDreamDao giftsDrawDao;

    /***
     * 统计该用户当日次数
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findNum(long userId) {
        return giftsDrawDao.findNum(userId);
    }

    /***
     * 新增记录
     * @param prizesLuckyDraw
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(ZhouGongDreamRecords prizesLuckyDraw) {
        return giftsDrawDao.add(prizesLuckyDraw);
    }

    /***
     * 查询记录
     */
    public PageBean<ZhouGongDreamRecords> findOweList(long userId, int page, int count) {
        List<ZhouGongDreamRecords> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = giftsDrawDao.findOweList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 条件查询
     * @param title 关键字
     * @param biglx 一级分类 ：人物、动物、植物、物品、活动、生活、自然、鬼神、建筑、其它
     * @param smalllx 二级分类 null查所有
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<ZhouGongDream> findDreamsSortList(String title, String biglx, String smalllx, int page, int count) {
        List<ZhouGongDream> list;
        if (CommonUtils.checkFull(title)) {
            title = null;
        }
        if (CommonUtils.checkFull(smalllx)) {
            smalllx = null;
        }
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = giftsDrawDao.findDreamsSortList(title, biglx, smalllx);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询详情
     */
    public ZhouGongDream findGifts(long id) {
        return giftsDrawDao.findGifts(id);
    }

    /***
     * 查询二级
     */
    public ZhouGongDreamSort findDreamsTwoSort(String biglx) {
        return giftsDrawDao.findDreamsTwoSort(biglx);
    }

    /***
     * 查询全部
     */
    public List<ZhouGongDream> findList() {
        List<ZhouGongDream> list = null;
        list = giftsDrawDao.findList();
        return list;
    }

    /***
     * 更新内容
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(ZhouGongDream kitchen) {
        return giftsDrawDao.update(kitchen);
    }
}
