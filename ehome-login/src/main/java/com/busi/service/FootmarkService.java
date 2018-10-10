package com.busi.service;

import com.busi.dao.FootmarkDao;
import com.busi.entity.Footmark;
import com.busi.entity.Footmarkauthority;
import com.busi.entity.PageBean;
import com.busi.utils.CommonUtils;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @program: ehome
 * @description: 足迹
 * @author: ZHaoJiaJie
 * @create: 2018-10-09 13:42
 */
@Service
public class FootmarkService {

    @Autowired
    private FootmarkDao footmarkDao;

    /***
     * 新增足迹
     * @param footmark
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(Footmark footmark) {
        return footmarkDao.add(footmark);
    }

    /***
     * 新增足迹权限
     * @param footmarkauthority
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addAuthority(Footmarkauthority footmarkauthority) {
        return footmarkDao.addAuthority(footmarkauthority);
    }

    /***
     * 更新权限
     * @param footmarkauthority
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateAuthority(Footmarkauthority footmarkauthority) {
        return footmarkDao.updateAuthority(footmarkauthority);
    }

    /***
     * 更新删除状态
     * @param footmark
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(Footmark footmark) {
        return footmarkDao.updateDel(footmark);
    }

    /***
     * 根据ID查询足迹
     * @param id
     * @return
     */
    public Footmark findUserById(long id) {
        return footmarkDao.findUserById(id);
    }

    /***
     * 根据ID查询足迹权限
     * @param userId
     * @return
     */
    public Footmarkauthority findUserId(long userId) {
        return footmarkDao.findUserId(userId);
    }


    /**
     * 查询足迹列表
     *
     * @param userId
     * @param page
     * @param count
     * @return
     */
    public PageBean<Footmark> findList(long userId, int footmarkType, String startTime, String endTime, int page, int count) {

        List<Footmark> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (CommonUtils.checkFull(startTime)) {
            list = footmarkDao.findList(userId, footmarkType);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date beginDate = null;
            Date endDate = null;
            Date begin = null;
            Date end = null;
            try {
                if (!CommonUtils.checkFull(startTime)) {
                    beginDate = format.parse(startTime);
                    begin = format2.parse(format2.format(beginDate));
                }
                if (!CommonUtils.checkFull(endTime)) {
                    endDate = format.parse(endTime);
                    end = format2.parse(format2.format(endDate));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            list = footmarkDao.findTimeList(userId, footmarkType, begin, end);
        }
        return PageUtils.getPageBean(p, list);
    }

}
