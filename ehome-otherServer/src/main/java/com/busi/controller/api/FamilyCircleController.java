package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.FamilyCircleService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import com.busi.utils.UserInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.smartcardio.CommandAPDU;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @program: ehome
 * @description: 家人圈相关接口
 * @author: ZHaoJiaJie
 * @create: 2019-04-18 13:45
 */
@RestController
public class FamilyCircleController extends BaseController implements FamilyCircleApiController {

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    FamilyCircleService familyCircleService;

    /***
     * 新增家族评论
     * @param familyComments
     * @return
     */
    @Override
    public ReturnData addFComment(@Valid @RequestBody FamilyComments familyComments, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        familyComments.setTime(new Date());
        familyCircleService.addFComment(familyComments);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除家族评论
     * @return:
     */
    @Override
    public ReturnData delFComment(@PathVariable long userId, @PathVariable String ids) {
        familyCircleService.delFComment(userId, ids.split(","));
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询家族用户评论
     * @param userId 用户
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findFCommentList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<FamilyComments> pageBean = null;
        pageBean = familyCircleService.findFCommentList(userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                FamilyComments ik = (FamilyComments) list.get(i);

                UserInfo sendInfoCache = null;
                sendInfoCache = userInfoUtils.getUserInfo(ik.getMyId());
                if (sendInfoCache != null) {
                    ik.setName(sendInfoCache.getName());
                    ik.setHead(sendInfoCache.getHead());
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 新增家族问候
     * @param familyGreeting
     * @return
     */
    @Override
    public ReturnData addGreeting(@Valid @RequestBody FamilyGreeting familyGreeting, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        FamilyGreeting greeting = familyCircleService.findUser(familyGreeting.getVisitUserId(), familyGreeting.getUserId());
        if (greeting != null) {
            return returnData(StatusCode.CODE_TADAY_GREET_ERROR.CODE_VALUE, "今日已对其问候过", new JSONObject());
        }
        if (familyGreeting.getVisitUserId() == CommonUtils.getMyId()) {
            familyGreeting.setTime(new Date());
            familyCircleService.addGreeting(familyGreeting);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询家族问候
     * @param userId 用户
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findGreetingList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        List list = null;
        PageBean<FamilyGreeting> pageBean = null;
        if (page < 0 || count <= 0) {// 查询今天全部的
            list = familyCircleService.findGreetingList(userId);
        } else {//分页查询
            pageBean = familyCircleService.findGreetingList2(userId, page, count);
            list = pageBean.getList();
        }
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                FamilyGreeting ik = (FamilyGreeting) list.get(i);

                UserInfo sendInfoCache = null;
                sendInfoCache = userInfoUtils.getUserInfo(ik.getVisitUserId());
                if (sendInfoCache != null) {
                    ik.setName(sendInfoCache.getName());
                    ik.setHead(sendInfoCache.getHead());
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 新增今日记事
     * @param familyTodayPlan
     * @return
     */
    @Override
    public ReturnData addInfor(@Valid @RequestBody FamilyTodayPlan familyTodayPlan, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        familyTodayPlan.setTime(new Date());
        familyCircleService.addInfor(familyTodayPlan);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除今日记事
     * @return:
     */
    @Override
    public ReturnData delInfor(@PathVariable long userId, @PathVariable String ids) {
        familyCircleService.delInfor(userId, ids.split(","));
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询今日记事
     * @param userId 用户
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findInforList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<FamilyTodayPlan> pageBean = null;
        pageBean = familyCircleService.findInforList(userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                FamilyTodayPlan plan = (FamilyTodayPlan) list.get(i);

                UserInfo sendInfoCache = null;
                sendInfoCache = userInfoUtils.getUserInfo(plan.getUserId());
                if (sendInfoCache != null) {
                    plan.setName(sendInfoCache.getName());
                    plan.setHead(sendInfoCache.getHead());
                    plan.setProTypeId(sendInfoCache.getProType());
                    plan.setHouseNumber(sendInfoCache.getHouseNumber());
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }
}
