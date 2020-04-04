package com.busi.servive;

import com.busi.dao.GrabGiftsDrawDao;
import com.busi.entity.GrabGifts;
import com.busi.entity.GrabMedium;
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
 * @description: 抢礼物Service
 * @author: ZHaoJiaJie
 * @create: 2020-04-03 13:03:04
 */
@Service
public class GrabGiftsService {

    @Autowired
    private GrabGiftsDrawDao giftsDrawDao;

    /***
     * 新增记录
     * @param prizesLuckyDraw
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(GrabMedium prizesLuckyDraw) {
        return giftsDrawDao.add(prizesLuckyDraw);
    }

    /***
     * 更新奖品数量
     * @param prizesLuckyDraw
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(GrabGifts prizesLuckyDraw) {
        return giftsDrawDao.update(prizesLuckyDraw);
    }

    /***
     * 查询奖品
     */
    public GrabGifts findGifts() {
        return giftsDrawDao.findGifts();
    }
}
