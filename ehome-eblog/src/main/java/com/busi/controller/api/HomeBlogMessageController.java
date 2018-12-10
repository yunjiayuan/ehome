package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.HomeBlogCommentService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import com.busi.utils.UserInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 生活圈消息
 * @author: ZHaoJiaJie
 * @create: 2018-11-08 10:46
 */
@RestController
public class HomeBlogMessageController extends BaseController implements HomeBlogMessageApiController {

    @Autowired
    private UserInfoUtils userInfoUtils;

    @Autowired
    private HomeBlogCommentService homeBlogCommentService;

    /***
     * 查询生活圈消息接口
     * @param userId     用户ID
     * @param type       查询类型  0所有 1未读 2已读
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findMessageList(@PathVariable int type, @PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<HomeBlogMessage> pageBean;
        pageBean = homeBlogCommentService.findMessageList(type, userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        String ids = "";
        String blIds = "";
        List list = null;
        List list2 = null;
        UserInfo userInfo = null;
        HomeBlogMessage mess = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                mess = (HomeBlogMessage) list.get(i);
                if (mess != null) {
                    ids += mess.getId() + ",";
                    blIds += mess.getBlog() + ",";
                }
            }
            list2 = homeBlogCommentService.findIdList(blIds.split(","));
            if (list2 != null && list2.size() > 0) {
                HomeBlog homeBlog = null;
                for (int j = 0; j < list2.size(); j++) {
                    homeBlog = (HomeBlog) list2.get(j);
                    if (homeBlog != null) {
                        for (int i = 0; i < list.size(); i++) {
                            mess = (HomeBlogMessage) list.get(i);
                            if (mess != null) {
                                if (mess.getBlog() == homeBlog.getId()) {
                                    userInfo = userInfoUtils.getUserInfo(mess.getUserId());
                                    if (userInfo != null) {
                                        mess.setUserHead(userInfo.getHead());
                                        mess.setUserName(userInfo.getName());
                                    }
                                    mess.setBlogTitle(homeBlog.getTitle());
                                    mess.setBlogType(homeBlog.getSendType());
                                    if (mess.getReplayId() > 0) {
                                        userInfo = userInfoUtils.getUserInfo(mess.getReplayId());
                                        mess.setReplayName(userInfo.getName());
                                    }
                                    if (!CommonUtils.checkFull(homeBlog.getImgUrl())) {
                                        mess.setBlogFirstImg(homeBlog.getImgUrl().split(",")[0]);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (type != 2) {
                //更新消息状态
                homeBlogCommentService.updateState(userId, ids.split(","));
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 获取未读消息数量
     * @param userId     查询用户ID
     * @return
     */
    @Override
    public ReturnData getMessageCount(@PathVariable long userId) {
        //消息
        long num = homeBlogCommentService.getCount(userId);

        Map<String, Long> map = new HashMap<>();
        map.put("num", num);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}