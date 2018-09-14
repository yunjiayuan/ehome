package com.busi.service;

import com.busi.dao.NoticeDao;
import com.busi.entity.Groupsetup;
import com.busi.entity.Notice;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 消息通知设置Service
 * author：zhaojiajie
 * create time：2018-9-13 11:52:51
 */
@Service
public class NoticeService {

    @Autowired
    private NoticeDao noticeDao;

    /***
     * 新增消息通知
     * @param notice
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addNotice(Notice notice) {
        return noticeDao.addNotice(notice);
    }

    /***
     * 新增群消息通知
     * @param groupsetup
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addGroupsetup(Groupsetup groupsetup) {
        return noticeDao.addGroupsetup(groupsetup);
    }

    /***
     * 设置消息通知
     * @param notice
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int setUp0(Notice notice) {
        return noticeDao.setUp0(notice);
    }

    /***
     * 设置消息通知
     * @param notice
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int setUp1(Notice notice) {
        return noticeDao.setUp1(notice);
    }

    /***
     * 设置消息通知
     * @param notice
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int setUp3(Notice notice) {
        return noticeDao.setUp3(notice);
    }

    /***
     * 设置消息通知
     * @param notice
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int setUp4(Notice notice) {
        return noticeDao.setUp4(notice);
    }

    /***
     * 设置消息通知
     * @param notice
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int setUp5(Notice notice) {
        return noticeDao.setUp5(notice);
    }

    /***
     * 设置消息通知
     * @param notice
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int setUp6(Notice notice) {
        return noticeDao.setUp6(notice);
    }

    /***
     * 置空自定义免扰时间
     * @param notice
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int setTime(Notice notice) {
        return noticeDao.setTime(notice);
    }

    /***
     * 设置群消息通知
     * @param groupsetup
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int setUpgroup(Groupsetup groupsetup) {
        return noticeDao.setUpgroup(groupsetup);
    }

    /***
     * 查询消息设置详情
     * @param userId
     * @return
     */
    public Notice findSetUp(long userId) {
        return noticeDao.findSetUp(userId);
    }

    /***
     * 查询群消息设置详情
     * @param groupId
     * @return
     */
    public Groupsetup findsetUpgroup(long groupId) {
        return noticeDao.findsetUpgroup(groupId);
    }

    /***
     * 分页查询群消息设置
     * @param userId 用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<Groupsetup> findSetUpList(long userId, int page, int count) {

        List<Groupsetup> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = noticeDao.findSetUpList(userId);

        return PageUtils.getPageBean(p, list);
    }

}
