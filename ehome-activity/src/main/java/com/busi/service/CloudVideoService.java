package com.busi.service;

import com.busi.dao.CloudVideoDao;
import com.busi.entity.*;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 云视频
 * @author: ZHaoJiaJie
 * @create: 2019-03-20 18:07
 */
@Service
public class CloudVideoService {

    @Autowired
    private CloudVideoDao cloudVideoDao;

    /***
     * 新增视频
     * @param cloudVideo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addCloudVideo(CloudVideo cloudVideo) {
        return cloudVideoDao.addCloudVideo(cloudVideo);
    }

    /***
     * 查询视频
     * @param id
     * @return
     */
    public CloudVideo findId(long id) {
        return cloudVideoDao.findId(id);
    }

    /***
     * 删除视频
     * @param id
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delCloudVideo(long id) {
        return cloudVideoDao.delCloudVideo(id);
    }

    /***
     * 删除活动
     * @param id
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(long id) {
        return cloudVideoDao.del(id);
    }

    /***
     * 分页查询用户的云视频列表
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<CloudVideo> findCloudVideoList(long userId, int page, int count) {
        List<CloudVideo> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = cloudVideoDao.findCloudVideoList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 分页查询用户已参加的活动
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<CloudVideoActivities> findCloudVideoList2(long userId, int page, int count) {
        List<CloudVideoActivities> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = cloudVideoDao.findCloudVideoList2(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询是否已经参加
     * @param selectionType
     * @return
     */
    public CloudVideoActivities findDetails(long userId, int selectionType) {
        return cloudVideoDao.findDetails(userId, selectionType);
    }

    /***
     * 新增活动信息
     * @param activities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addSelection(CloudVideoActivities activities) {
        return cloudVideoDao.addSelection(activities);
    }

    /***
     * 分页查询参加活动的人员
     * @param selectionType  活动分类 0云视频  (后续添加)
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<CloudVideoActivities> findPersonnelList(int selectionType, int page, int count) {

        List<CloudVideoActivities> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = cloudVideoDao.findPersonnelList(selectionType);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 新增投票
     * @param cloudVideoVote
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addVote(CloudVideoVote cloudVideoVote) {
        return cloudVideoDao.addVote(cloudVideoVote);
    }

    /***
     * 更新投票数
     * @param cloudVideoActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateNumber(CloudVideoActivities cloudVideoActivities) {
        return cloudVideoDao.updateNumber(cloudVideoActivities);
    }

    /***
     * 分页查询投票历史
     * @param userId  用户ID
     * @param selectionType  活动分类 0云视频  (后续添加)
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<CloudVideoVote> findVoteList(long userId, int selectionType, int page, int count) {

        List<CloudVideoVote> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = cloudVideoDao.findVoteList(userId, selectionType);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询是否给该用户投过票
     * @param selectionType
     * @return
     */
    public CloudVideoVote findTicket(long myId, long userId, int selectionType) {
        return cloudVideoDao.findTicket(myId, userId, selectionType);
    }
}
