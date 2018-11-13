package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.HomeBlogCommentService;
import com.busi.service.HomeBlogService;
import com.busi.utils.CommonUtils;
import com.busi.utils.MqUtils;
import com.busi.utils.StatusCode;
import com.busi.utils.UserInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 生活圈评论
 * @author: ZHaoJiaJie
 * @create: 2018-11-05 15:11
 */
@RestController
public class HomeBlogCommentController extends BaseController implements HomeBlogCommentApiController {

    @Autowired
    private MqUtils mqUtils;

    @Autowired
    private HomeBlogCommentService homeBlogCommentService;

    @Autowired
    private UserInfoUtils userInfoUtils;

    @Autowired
    private HomeBlogService homeBlogService;

    /***
     * 生活圈添加评论接口
     * @param homeBlogComment
     * @return
     */
    @Override
    public ReturnData addComment(@Valid @RequestBody HomeBlogComment homeBlogComment, BindingResult bindingResult) {
        HomeBlog homeBlog = homeBlogService.findInfo(homeBlogComment.getBlogId(), homeBlogComment.getMasterId());
        if (homeBlog == null) {
            return returnData(StatusCode.CODE_BLOG_NOT_FOUND.CODE_VALUE, "生活圈不存在", new JSONArray());
        }

        homeBlogComment.setTime(new Date());
        homeBlogCommentService.addComment(homeBlogComment);

        //更新评论数
        mqUtils.updateBlogCounts(homeBlog.getUserId(), homeBlog.getId(), 1, 1);

        long myId = CommonUtils.getMyId();
        long userId = homeBlogComment.getReplayId();
        int ate = homeBlogComment.getReplyType();

        //新增消息
        mqUtils.addMessage(myId, userId, homeBlog.getId(), homeBlogComment.getId(), homeBlogComment.getContent(), ate);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除生活圈评论接口
     * @param id 评论ID
     * @param blogId 生活圈ID
     * @return
     */
    @Override
    public ReturnData delComment(@PathVariable long id, @PathVariable long blogId) {
        HomeBlog homeBlog = homeBlogService.findInfo(blogId, CommonUtils.getMyId());
        if (homeBlog == null) {
            return returnData(StatusCode.CODE_BLOG_NOT_FOUND.CODE_VALUE, "生活圈不存在", new JSONArray());
        }
        HomeBlogComment comment = homeBlogCommentService.findById(id, blogId);
        if (comment == null) {
            return returnData(StatusCode.CODE_BLOG_USER_NOTLOGIN.CODE_VALUE, "评论不存在", new JSONArray());
        }
        if (comment.getUserId() == CommonUtils.getMyId() || comment.getMasterId() == CommonUtils.getMyId()) {
            comment.setReplyStatus(1);//1删除
            homeBlogCommentService.update(comment);
        } else {
            return returnData(StatusCode.CODE_BLOG_USER_NOTLOGIN.CODE_VALUE, "无权限删除", new JSONArray());
        }
        //更新评论数
        mqUtils.updateBlogCounts(homeBlog.getUserId(), homeBlog.getId(), 1, -1);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询生活圈评论记录接口
     * @param blogId     生活圈ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findCommentList(@PathVariable long blogId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //查询评论列表
        Map<String, Object> map = new HashMap<>();
        PageBean<HomeBlogComment> pageBean;
        pageBean = homeBlogCommentService.findList(blogId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        List list2 = null;
        list = pageBean.getList();
        HomeBlogComment comment = null;
        for (int j = 0; j < list.size(); j++) {
            comment = (HomeBlogComment) list.get(j);
            if (comment != null) {
                UserInfo userInfo = null;
                userInfo = userInfoUtils.getUserInfo(comment.getUserId());
                if (userInfo != null) {
                    comment.setUserHead(userInfo.getHead());
                    comment.setUserName(userInfo.getName());
                }
            }
        }
        //查询回复列表
        list2 = homeBlogCommentService.findReplyList(blogId);
        if (list2 != null && list2.size() > 0) {
            for (int j = 0; j < list2.size(); j++) {
                comment = (HomeBlogComment) list2.get(j);
                if (comment != null) {
                    UserInfo userInfo = null;
                    userInfo = userInfoUtils.getUserInfo(comment.getReplayId());
                    if (userInfo != null) {
                        comment.setReplayName(userInfo.getName());
                    }
                    userInfo = userInfoUtils.getUserInfo(comment.getUserId());
                    if (userInfo != null) {
                        comment.setUserName(userInfo.getName());
                    }
                }
            }
            map.put("replyData", list2);
        }
        map.put("commentData", list);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
