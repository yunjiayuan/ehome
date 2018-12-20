package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.HomeBlogCommentService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * @program: ehome
 * @description: 生活圈评论
 * @author: ZHaoJiaJie
 * @create: 2018-11-05 15:11
 */
@RestController
public class HomeBlogCommentController extends BaseController implements HomeBlogCommentApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    private HomeBlogCommentService homeBlogCommentService;

    @Autowired
    private UserInfoUtils userInfoUtils;

    /***
     * 生活圈添加评论接口
     * @param homeBlogComment
     * @return
     */
    @Override
    public ReturnData addComment(@Valid @RequestBody HomeBlogComment homeBlogComment, BindingResult bindingResult) {
        //查询该条生活圈信息
        Map<String, Object> blogMap = redisUtils.hmget(Constants.REDIS_KEY_EBLOG + homeBlogComment.getMasterId() + "_" + homeBlogComment.getBlogId());
        if (blogMap == null || blogMap.size() <= 0) {
            return returnData(StatusCode.CODE_BLOG_NOT_FOUND.CODE_VALUE, "生活圈不存在", new JSONArray());
        }
        //处理特殊字符
        String content = homeBlogComment.getContent();
        if (!CommonUtils.checkFull(content)) {
            homeBlogComment.setContent(CommonUtils.filteringContent(content));
        }
        homeBlogComment.setTime(new Date());
        homeBlogCommentService.addComment(homeBlogComment);

        long myId = CommonUtils.getMyId();
        long userId = homeBlogComment.getReplayId();
        int ate = homeBlogComment.getReplyType();

        if (homeBlogComment.getUserId() != homeBlogComment.getMasterId()) {
            //新增消息
            mqUtils.addMessage(myId, userId, homeBlogComment.getMasterId(), homeBlogComment.getBlogId(), homeBlogComment.getId(), homeBlogComment.getContent(), ate);
        }
        if (homeBlogComment.getReplyType() == 0) {//新增评论
            //放入缓存(七天失效)
            redisUtils.addListLeft(Constants.REDIS_KEY_EBLOG_COMMENT + homeBlogComment.getBlogId(), homeBlogComment, Constants.USER_TIME_OUT);
        } else {//新增回复
            List list = null;
            //先添加到缓存集合(七天失效)
            redisUtils.addListLeft(Constants.REDIS_KEY_EBLOG_REPLY + homeBlogComment.getFatherId(), homeBlogComment, Constants.USER_TIME_OUT);
            //再保证5条数据
            list = redisUtils.getList(Constants.REDIS_KEY_EBLOG_REPLY + homeBlogComment.getFatherId(), 0, -1);
            //清除缓存中的回复信息
            redisUtils.expire(Constants.REDIS_KEY_EBLOG_REPLY + homeBlogComment.getFatherId(), 0);
            if (list != null && list.size() > 5) {//限制五条回复
                //缓存中获取最新五条回复
                HomeBlogComment message = null;
                List<HomeBlogComment> messageList = new ArrayList<>();
                for (int j = 0; j < list.size(); j++) {
                    if (j < 5) {
                        message = (HomeBlogComment) list.get(j);
                        if (message != null) {
                            messageList.add(message);
                        }
                    }
                }
                if (messageList.size() == 5) {
                    redisUtils.pushList(Constants.REDIS_KEY_EBLOG_REPLY + homeBlogComment.getFatherId(), messageList, 0);
                }
            }
            //更新回复数
            mqUtils.updateCommentCounts(homeBlogComment.getFatherId(), 1);
        }
        //更新评论数
        mqUtils.updateBlogCounts(homeBlogComment.getMasterId(), homeBlogComment.getBlogId(), 1, 1);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除生活圈评论接口
     * @param id ID
     * @param blogId 生活圈ID
     * @return
     */
    @Override
    public ReturnData delComment(@PathVariable long id, @PathVariable long blogId) {
        HomeBlogComment comment = homeBlogCommentService.findById(id);
        if (comment == null) {
            return returnData(StatusCode.CODE_BLOG_USER_NOTLOGIN.CODE_VALUE, "评论不存在", new JSONArray());
        }
        //查询该条生活圈信息
        Map<String, Object> blogMap = redisUtils.hmget(Constants.REDIS_KEY_EBLOG + comment.getMasterId() + "_" + comment.getBlogId());
        if (blogMap == null || blogMap.size() <= 0) {
            return returnData(StatusCode.CODE_BLOG_NOT_FOUND.CODE_VALUE, "生活圈不存在", new JSONArray());
        }
        //判断操作人权限
        long userId = comment.getUserId();//评论者ID
        long myId = CommonUtils.getMyId();//登陆者ID
        long masterId = comment.getMasterId();//博主ID
        if (myId != userId && myId != masterId) {
            return returnData(StatusCode.CODE_BLOG_USER_NOTLOGIN.CODE_VALUE, "参数有误，当前用户[" + myId + "]无权限删除用户[" + masterId + "]的生活圈评论", new JSONObject());
        }
        comment.setReplyStatus(1);//1删除
        homeBlogCommentService.update(comment);
        List list = null;
        if (comment.getReplyType() == 0) {
            list = redisUtils.getList(Constants.REDIS_KEY_EBLOG_COMMENT + blogId, 0, -1);
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    HomeBlogComment comment2 = (HomeBlogComment) list.get(i);
                    if (comment2.getId() == id) {
                        redisUtils.removeList(Constants.REDIS_KEY_EBLOG_COMMENT + blogId, 1, comment2);
                        break;
                    }
                }
            }
            //更新评论数
            mqUtils.updateBlogCounts(comment.getMasterId(), blogId, 1, -1);
        } else {
            List list2 = null;
            List list3 = null;
            List<HomeBlogComment> messageList = new ArrayList<>();
            //获取缓存中回复列表
            list3 = redisUtils.getList(Constants.REDIS_KEY_EBLOG_REPLY + comment.getFatherId(), 0, -1);
            if (list3 != null && list3.size() > 0) {
                for (int i = 0; i < list3.size(); i++) {
                    HomeBlogComment comment1 = (HomeBlogComment) list3.get(i);
                    if (comment1 != null) {
                        if (comment1.getId() == id) {
                            //清除缓存中的回复信息
                            redisUtils.expire(Constants.REDIS_KEY_EBLOG_REPLY + comment.getFatherId(), 0);
                            //数据库获取最新五条回复
                            list2 = homeBlogCommentService.findMessList(comment.getFatherId());
                            if (list2 != null && list2.size() > 0) {
                                HomeBlogComment message = null;
                                for (int j = 0; j < list2.size(); j++) {
                                    if (j < 5) {
                                        message = (HomeBlogComment) list2.get(j);
                                        if (message != null) {
                                            messageList.add(message);
                                        }
                                    }
                                }
                                redisUtils.pushList(Constants.REDIS_KEY_EBLOG_REPLY + comment.getFatherId(), messageList, 0);
                            }
                            break;
                        }
                    }
                }
            }
            //更新回复数
            mqUtils.updateCommentCounts(comment.getFatherId(), -1);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询生活圈评论记录接口
     * @param blogId     生活圈ID
     * @param type       0评论 1转发评论
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findCommentList(@PathVariable int type, @PathVariable long blogId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //获取缓存中评论列表
        List list = null;
        List list2 = null;
        List commentList = null;
        List commentList2 = null;
        List<HomeBlogComment> messageArrayList = new ArrayList<>();
        PageBean<HomeBlogComment> pageBean = null;
        if (type == 1) {//查询转发评论
            pageBean = homeBlogCommentService.findForwardList(blogId, page, count);
            commentList = pageBean.getList();
            if (commentList != null && commentList.size() > 0) {
                for (int j = 0; j < commentList.size(); j++) {//评论
                    UserInfo userInfo = null;
                    HomeBlogComment comment = null;
                    comment = (HomeBlogComment) commentList.get(j);
                    if (comment != null && comment.getReplyType() == 2) {
                        userInfo = userInfoUtils.getUserInfo(comment.getUserId());
                        if (userInfo != null) {
                            comment.setUserHead(userInfo.getHead());
                            comment.setUserName(userInfo.getName());
                            comment.setHouseNumber(userInfo.getHouseNumber());
                            comment.setProTypeId(userInfo.getProType());
                        }
                        //查询数据库 （获取最新五条回复）
                        list = homeBlogCommentService.findMessList(comment.getId());
                        if (list != null && list.size() > 0) {
                            HomeBlogComment message = null;
                            for (int l = 0; l < list.size(); l++) {
                                if (l < 5) {
                                    message = (HomeBlogComment) list.get(l);
                                    if (message != null) {
                                        messageArrayList.add(message);
                                    }
                                }
                            }
                            comment.setMessageList(messageArrayList);
                        }
                    }
                }
            }
        } else {
//        Map<String, Object> map = new HashMap<>();
            long countTotal = redisUtils.getListSize(Constants.REDIS_KEY_EBLOG_COMMENT + blogId);
            int pageCount = page * count;
            if (pageCount > countTotal) {
                pageCount = -1;
            } else {
                pageCount = pageCount - 1;
            }
            commentList = redisUtils.getList(Constants.REDIS_KEY_EBLOG_COMMENT + blogId, (page - 1) * count, pageCount);
            //获取数据库中评论列表
            if (commentList == null || commentList.size() < count) {
                pageBean = homeBlogCommentService.findList(blogId, page, count);
                commentList2 = pageBean.getList();
                if (commentList2 != null && commentList2.size() > 0) {
                    for (int i = 0; i < commentList2.size(); i++) {
                        HomeBlogComment comment = null;
                        comment = (HomeBlogComment) commentList2.get(i);
                        if (comment != null) {
                            for (int j = 0; j < commentList.size(); j++) {
                                HomeBlogComment comment2 = null;
                                comment2 = (HomeBlogComment) commentList.get(j);
                                if (comment2 != null) {
                                    if (comment.getId() == comment2.getId()) {
                                        redisUtils.removeList(Constants.REDIS_KEY_EBLOG_COMMENT + blogId, 1, comment2);
                                    }
                                }
                            }
                        }
                    }
                    //更新缓存
                    redisUtils.pushList(Constants.REDIS_KEY_EBLOG_COMMENT + blogId, commentList2, 0);
                    //获取最新缓存
                    commentList = redisUtils.getList(Constants.REDIS_KEY_EBLOG_COMMENT + blogId, (page - 1) * count, page * count);
                }
//            for (int j = 0; j < commentList.size(); j++) {
//                commentList2 = new ArrayList();
//                if (!commentList2.contains(commentList.get(j))) {
//                    commentList2.add(commentList.get(j));
//                }
//            }
            }
//        if (commentList == null) {
//            pageBean = homeBlogCommentService.findList(blogId, page, count);
//            commentList = pageBean.getList();
//            //更新缓存
//            redisUtils.pushList(Constants.REDIS_KEY_EBLOG_COMMENT + blogId, commentList, 0);
//        } else if (commentList.size() < count) {
//            pageBean = homeBlogCommentService.findList(blogId, page, count - commentList.size());
//            commentList2 = pageBean.getList();
//            //放入缓存中
//            if (commentList2 != null && commentList2.size() > 0) {
//                for (int i = 0; i < commentList2.size(); i++) {
//                    HomeBlogComment comment = null;
//                    comment = (HomeBlogComment) commentList2.get(i);
//                    if (comment != null) {
//                        commentList.add(comment);
//                    }
//                }
//                //更新缓存
//                redisUtils.pushList(Constants.REDIS_KEY_EBLOG_COMMENT + blogId, commentList, 0);
//            }
//        }
            if (commentList == null) {
                commentList = new ArrayList();
            }
            for (int j = 0; j < commentList.size(); j++) {//评论
                UserInfo userInfo = null;
                HomeBlogComment comment = null;
                comment = (HomeBlogComment) commentList.get(j);
                if (comment != null) {
                    userInfo = userInfoUtils.getUserInfo(comment.getUserId());
                    if (userInfo != null) {
                        comment.setUserHead(userInfo.getHead());
                        comment.setUserName(userInfo.getName());
                        comment.setHouseNumber(userInfo.getHouseNumber());
                        comment.setProTypeId(userInfo.getProType());
                    }
                    //获取缓存中回复列表
                    list = redisUtils.getList(Constants.REDIS_KEY_EBLOG_REPLY + comment.getId(), 0, -1);
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {//回复
                            HomeBlogComment message = null;
                            message = (HomeBlogComment) list.get(i);
                            if (message != null) {
                                userInfo = userInfoUtils.getUserInfo(message.getReplayId());
                                if (userInfo != null) {
                                    message.setReplayName(userInfo.getName());
                                }
                                userInfo = userInfoUtils.getUserInfo(message.getUserId());
                                if (userInfo != null) {
                                    message.setUserName(userInfo.getName());
                                }
                            }
                        }
                        comment.setMessageList(list);
                    } else {
                        //查询数据库 （获取最新五条回复）
                        list2 = homeBlogCommentService.findMessList(comment.getId());
                        if (list2 != null && list2.size() > 0) {
                            HomeBlogComment message = null;
                            for (int l = 0; l < list2.size(); l++) {
                                if (l < 5) {
                                    message = (HomeBlogComment) list2.get(l);
                                    if (message != null) {
                                        userInfo = userInfoUtils.getUserInfo(message.getReplayId());
                                        if (userInfo != null) {
                                            message.setReplayName(userInfo.getName());
                                        }
                                        userInfo = userInfoUtils.getUserInfo(message.getUserId());
                                        if (userInfo != null) {
                                            message.setUserName(userInfo.getName());
                                        }
                                        messageArrayList.add(message);
                                    }
                                }
                            }
                            comment.setMessageList(messageArrayList);
                            //更新缓存
                            redisUtils.pushList(Constants.REDIS_KEY_EBLOG_REPLY + comment.getId(), messageArrayList, 0);
                        }
                    }
                }
            }
        }
        pageBean = new PageBean<>();
        pageBean.setSize(commentList.size());
        pageBean.setPageNum(page);
        pageBean.setPageSize(count);
        pageBean.setList(commentList);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 查询生活圈指定评论下的回复记录接口
     * @param contentId     评论ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findReplyList(@PathVariable long contentId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        List list = null;
        PageBean<HomeBlogComment> pageBean = null;
        pageBean = homeBlogCommentService.findReplyList(contentId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        long num = 0;
        UserInfo userInfo = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {//回复
                HomeBlogComment message = null;
                message = (HomeBlogComment) list.get(i);
                if (message != null) {
                    userInfo = userInfoUtils.getUserInfo(message.getReplayId());
                    if (userInfo != null) {
                        message.setReplayName(userInfo.getName());
                    }
                    userInfo = userInfoUtils.getUserInfo(message.getUserId());
                    if (userInfo != null) {
                        message.setUserHead(userInfo.getHead());
                        message.setUserName(userInfo.getName());
                        message.setProTypeId(userInfo.getProType());
                        message.setHouseNumber(userInfo.getHouseNumber());
                    }
                }
            }
            //消息
            num = homeBlogCommentService.getReplayCount(contentId);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("num", num);
        map.put("list", list);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
