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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        HomeBlog homeBlog = homeBlogService.findBlogInfo(homeBlogComment.getBlogId(), homeBlogComment.getMasterId());
        if (homeBlog == null) {
            return returnData(StatusCode.CODE_BLOG_NOT_FOUND.CODE_VALUE, "生活圈不存在", new JSONArray());
        }
        //简单过滤目前
        String content = "";
        content = homeBlogComment.getContent().replaceAll("<", "&lt;");
        content = homeBlogComment.getContent().replaceAll(">", "&gt;");

        homeBlogComment.setContent(content);
        homeBlogComment.setTime(new Date());
        homeBlogCommentService.addComment(homeBlogComment);

        //更新评论数
        mqUtils.updateBlogCounts(homeBlog.getUserId(), homeBlog.getId(), 1, 1);

        String messUsers = "";
        HomeBlogMessage msg = new HomeBlogMessage();
        HomeBlog b = new HomeBlog();
        b.setId(homeBlog.getId());
        HomeBlogComment p = new HomeBlogComment();
        p.setId(homeBlogComment.getId());
        //ate  0评论 1回复
        long myId = CommonUtils.getMyId();
        long userId = homeBlogComment.getReplayId();
        int ate = homeBlogComment.getReplyType();
        if (ate == 0 && homeBlog.getUserId() != myId) {
            //消息给博主看的
            msg.setNewsType(0);//0评论 1回复 2赞 3转发 4评论@  5回复@  6转发@ 7博文@
        } else if (ate == 1) {
            //消息给被回复人看的
            if (homeBlogComment.getReplayId() != myId) {
                msg.setNewsType(1);//0评论 1回复 2赞 3转发 4评论@  5回复@  6转发@ 7博文@
            }
            //消息给博主看的
            if (homeBlog.getUserId() != myId && homeBlog.getUserId() != userId) {//被回复者 不能是当前用户和博主
                msg.setNewsType(0);//0评论 1回复 2赞 3转发 4评论@  5回复@  6转发@ 7博文@
            }
        }
        msg.setUserId(myId);
        msg.setReplayId(userId);        //被评论用户ID
//        msg.setBlog(b);
        msg.setCommentId(homeBlogComment.getId());    //消息ID
        msg.setMasterId(homeBlog.getUserId());
        msg.setContent(content);
        msg.setTime(new Date());
        msg.setNewsState(1);    //1未读
        msg.setStatus(0);    //0正常
//        msg.setParentMessage(null);    //消息父ID
        homeBlogCommentService.addMessage(msg);
        //adduser +=","+myId+","+userId;
        //@谁消息
        if (content.indexOf("&") != -1) {
            String regex = "&(\\d+)&";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);
            msg = new HomeBlogMessage();
            while (matcher.find()) {
                //	adduser+=","+matcher.group(1);
                //过滤重复
                if (messUsers.indexOf(matcher.group(1)) == -1) {
                    long _muser = Long.valueOf(matcher.group(1));
                    //自己不能给自己发消息
                    if (myId != _muser) {
                        //消息记录去重
                        messUsers += "," + _muser;
                        msg.setUserId(myId);
                        msg.setNewsType(ate == 0 ? 4 : 5);//0评论 1回复 2赞 3转发 4评论@  5回复@  6转发@ 7博文@
                        msg.setReplayId(_muser);        //被@用户ID
//                        msg.setBlog(b);
                        msg.setCommentId(homeBlogComment.getId());    //消息ID
                        msg.setMasterId(homeBlog.getUserId());
                        msg.setContent(content);    //消息内容待定
                        msg.setTime(new Date());
                        msg.setNewsState(1);    //1未读
                        msg.setStatus(0);    //0正常
//                        if (ate == 0) {
//                            msg.setParentMessage(null);    //消息父ID
//                        } else {
//                            msg.setParentMessage(p);    //消息父ID
//                        }
                        homeBlogCommentService.addMessage(msg);
                    }
                }
            }
        }
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
        HomeBlogComment comment = homeBlogCommentService.findById(id, blogId);
        if (comment == null) {
            return returnData(StatusCode.CODE_BLOG_USER_NOTLOGIN.CODE_VALUE, "评论不存在", new JSONArray());
        }
        // 用户删除自己的评论  和  博主
        if (comment.getUserId() == CommonUtils.getMyId() || comment.getMasterId() == CommonUtils.getMyId()) {
            comment.setReplyStatus(1);//1删除
            homeBlogCommentService.update(comment);
        } else {
            return returnData(StatusCode.CODE_BLOG_USER_NOTLOGIN.CODE_VALUE, "无权限删除", new JSONArray());
        }
        HomeBlog homeBlog = homeBlogService.findBlogInfo(blogId, comment.getMasterId());
        if (homeBlog == null) {
            return returnData(StatusCode.CODE_BLOG_NOT_FOUND.CODE_VALUE, "生活圈不存在", new JSONArray());
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
    public ReturnData findBlogList(@PathVariable long blogId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<HomeBlogComment> pageBean;
        pageBean = homeBlogCommentService.findList(blogId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        HomeBlogComment comment = null;
        String users = "";
        for (int i = 0; i < list.size(); i++) {
            comment = (HomeBlogComment) list.get(i);
            if (comment != null) {
                //获取用户
                users += comment.getUserId() + ",";
                //获取回复用户
                if (comment.getReplyType() == 1) {
                    users += comment.getReplayId() + ",";
                }
                //获取内容中的@谁  格式   #333#
                String content = comment.getContent();
                if (content.indexOf("&") != -1) {
                    String regex = "&(\\d+)&";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(content);
                    while (matcher.find()) {
                        users += matcher.group(1) + ",";
                    }
                }
            }
        }
        String[] kes = users.split(",");

        for (int j = 0; j < list.size(); j++) {
            comment = (HomeBlogComment) list.get(j);
            if (comment != null) {
                for (int l = 0; l < kes.length; l++) {
                    UserInfo userInfo = null;
                    Long t = Long.parseLong(kes[l]);
                    if (t > 0) {
                        userInfo = userInfoUtils.getUserInfo(t);
                        if (userInfo != null) {
                            comment.setUserHead(userInfo.getHead());
                            comment.setUsername(userInfo.getName());
                        }
                        if (comment.getReplyType() == 1) {
                            if (userInfo != null) {
                                comment.setReplayname(userInfo.getName());
                            }
                        }
                        //获取内容中的@谁 加"名称"  格式   #333_阿里巴巴#
                        String content = comment.getContent();
                        if (content.indexOf("&") != -1) {
                            String regex = "&(\\d+)&";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(content);

                            StringBuffer sb = new StringBuffer();
                            while (matcher.find()) {
                                if (userInfo != null) {
                                    matcher.appendReplacement(sb, "&" + matcher.group(1) + "_" + userInfo.getName() + "&");
                                }
                            }
                            matcher.appendTail(sb);
                            comment.setContent(sb.toString());
                        }
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
