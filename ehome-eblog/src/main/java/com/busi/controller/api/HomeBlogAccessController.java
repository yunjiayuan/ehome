package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.HomeBlogAccessService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import com.busi.utils.UserInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: ehome
 * @description: 生活圈标签
 * @author: ZHaoJiaJie
 * @create: 2018-11-01 16:50
 */
@RestController
public class HomeBlogAccessController extends BaseController implements HomeBlogAccessApiController {

    @Autowired
    private HomeBlogAccessService homeBlogAccessService;

    @Autowired
    private UserInfoUtils userInfoUtils;

    /***
     * 添加标签接口
     * @param homeBlogAccess
     * @return
     */
    @Override
    public ReturnData addLabel(@Valid @RequestBody HomeBlogAccess homeBlogAccess, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断该用户标签数量   最多20条
        int num = homeBlogAccessService.findNum(homeBlogAccess.getUserId());
        if (num >= 20) {
            return returnData(StatusCode.CODE_BLOG_LABEL_TAG.CODE_VALUE, "新增标签数量超过上限,新增失败", new JSONObject());
        }
        if (!CommonUtils.checkFull(homeBlogAccess.getUsers())) {
            String[] ids = homeBlogAccess.getUsers().split(",");
            if (ids.length >= 50) {
                return returnData(StatusCode.CODE_BLOG_MEMBER_TAG.CODE_VALUE, "标签内成员数量超过上限,新增失败", new JSONObject());
            }
        }
        homeBlogAccess.setTime(new Date());
        homeBlogAccessService.add(homeBlogAccess);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新标签
     * @Param: homeBlogAccess
     * @return:
     */
    @Override
    public ReturnData updateLabel(@Valid @RequestBody HomeBlogAccess homeBlogAccess, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        if (!CommonUtils.checkFull(homeBlogAccess.getUsers())) {
            String[] ids = homeBlogAccess.getUsers().split(",");
            if (ids.length >= 50) {
                return returnData(StatusCode.CODE_BLOG_MEMBER_TAG.CODE_VALUE, "标签内成员数量超过上限,新增失败", new JSONObject());
            }
        }
        homeBlogAccessService.update(homeBlogAccess);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除指定标签接口
     * @param userId 用户ID
     * @param tagId 将要被删除的标签
     * @return
     */
    @Override
    public ReturnData delLabel(@PathVariable long userId, @PathVariable long tagId) {
        //验证参数
        if (userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        //验证参数
        if (tagId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "tagId参数有误", new JSONObject());
        }
        //验证删除权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限删除用户[" + userId + "]的标签", new JSONObject());
        }
        int num = homeBlogAccessService.del(tagId, userId);
        if (num <= 0) {
            return returnData(StatusCode.CODE_BLOG_NOT_TAG.CODE_VALUE, "标签不存在", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询指定标签内成员接口
     * @param tagId     被查询标签ID
     * @return
     */
    @Override
    public ReturnData findMemberList(@PathVariable long tagId) {
        //验证参数
        if (tagId < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "tagId参数有误", new JSONObject());
        }
        //开始查询
        HomeBlogAccess blogAccess = homeBlogAccessService.find(tagId);
        if (blogAccess == null) {
            return returnData(StatusCode.CODE_BLOG_NOT_TAG.CODE_VALUE, "标签不存在", new JSONArray());
        }
        List<HomeBlogAccessMembers> list = new ArrayList<>();
        if (!CommonUtils.checkFull(blogAccess.getUsers())) {
            String[] ids = blogAccess.getUsers().split(",");
            for (int i = 0; i < ids.length; i++) {
                UserInfo userInfo = null;
                Long t = Long.parseLong(ids[i]);
                if (t > 0) {
                    userInfo = userInfoUtils.getUserInfo(t);
                    if (userInfo != null) {
                        HomeBlogAccessMembers members = new HomeBlogAccessMembers();
                        members.setUserId(t);
                        members.setName(userInfo.getName());
                        members.setHead(userInfo.getHead());
                        list.add(members);
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 查询标签列表
     * @param userId     用户ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findLabelList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<HomeBlogAccess> pageBean;
        pageBean = homeBlogAccessService.findList(userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
