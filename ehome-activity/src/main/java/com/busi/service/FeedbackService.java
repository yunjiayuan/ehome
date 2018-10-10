package com.busi.service;

import com.busi.dao.FeedbackDao;
import com.busi.entity.Feedback;
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
 * @description: 意见反馈
 * @author: ZHaoJiaJie
 * @create: 2018-10-09 17:09
 */
@Service
public class FeedbackService {

    @Autowired
    private FeedbackDao feedbackDao;

    /***
     * 新增
     * @param feedback
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(Feedback feedback) {
        return feedbackDao.add(feedback);
    }

    /***
     * 查询记录
     * @param opinionType  意见类型  0：反馈  1：投诉
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @param sort // 类别    opinionType=0时为反馈类别：0.聊天相关1.访问串门相关2.房间功能相关3.头像设置及私房照上传4.个人资料编辑5.活动参加及投票6.发布公告相关7.发布家博相关8.求助发布相关9.钱包充值相关10.家币家点相关11.红包发送相关12.二手物品相关13.发布视频照片相关14.添加好友及通讯录15.串门送礼物相关16.串门喂鹦鹉砸蛋相关17.每日任务领取家点18.红包分享得红包相关19.家门口功能相关20.设置相关21.其他
     * 						  opinionType=1时为投诉类别：0、聊天互动类1、公共信息类2、家博信息类3、个人资料类4、活动、求助类5、其他类
     * @return
     */
    public PageBean<Feedback> findList(long userId, int opinionType, int sort, int page, int count) {

        List<Feedback> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = feedbackDao.findList(userId, opinionType, sort);

        return PageUtils.getPageBean(p, list);
    }

}
