package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.CloudVideoService;
import com.busi.service.SelfChannelVipService;
import com.busi.utils.*;
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
 * @description: 云视频
 * @author: ZHaoJiaJie
 * @create: 2019-03-20 17:24
 */
@RestController
public class CloudVideoController extends BaseController implements CloudVideoApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    CloudVideoService cloudVideoService;

    @Autowired
    SelfChannelVipService selfChannelVipService;

    /***
     * 上传视频
     * @param cloudVideo
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData uploadCloudVideo(@Valid @RequestBody CloudVideo cloudVideo, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证是否是自频道会员
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_SELFCHANNELVIP + cloudVideo.getUserId());
        if (map == null || map.size() <= 0) {
            SelfChannelVip vip = selfChannelVipService.findDetails(cloudVideo.getUserId());
            if (vip == null) {
                return returnData(StatusCode.CODE_SELF_CHANNEL_VIP_NOT_OPENING.CODE_VALUE, "抱歉您还不是自频道会员", new JSONObject());
            }
            //放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(vip);
            redisUtils.hmset(Constants.REDIS_KEY_SELFCHANNELVIP + cloudVideo.getUserId(), ordersMap, Constants.USER_TIME_OUT);
        } else {
            SelfChannelVip vip = (SelfChannelVip) CommonUtils.mapToObject(map, SelfChannelVip.class);
            if (vip != null) {
                if (vip.getExpiretTime().getTime() < new Date().getTime()) {
                    //清除缓存中的信息
                    redisUtils.expire(Constants.REDIS_KEY_SELFCHANNELVIP + cloudVideo.getUserId(), 0);
                    return returnData(StatusCode.CODE_SELF_CHANNEL_VIP_NOT_OPENING.CODE_VALUE, "抱歉您还不是自频道会员", new JSONObject());
                }
            }
        }
        cloudVideo.setTime(new Date());
        cloudVideoService.addCloudVideo(cloudVideo);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除视频
     * @return:
     */
    @Override
    public ReturnData delCloudVideo(@PathVariable long id) {
        CloudVideo cloudVideo = cloudVideoService.findId(id);
        if (cloudVideo != null) {
            if (!CommonUtils.checkFull(cloudVideo.getVideoUrl())) {
                //调用MQ同步 图片到图片删除记录表
                mqUtils.sendDeleteImageMQ(CommonUtils.getMyId(), cloudVideo.getVideoUrl());
            }
            cloudVideoService.delCloudVideo(id);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 退出活动
     * @return:
     */
    @Override
    public ReturnData outCloudVideo(@PathVariable long id) {
        cloudVideoService.del(id);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询用户的云视频列表
     * @param searchType  0默认查询用户己上传的视频  1查询用户已参加的活动
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findCloudVideoList(@PathVariable int searchType, @PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (searchType == 0) {
            PageBean<CloudVideo> pageBean = null;
            pageBean = cloudVideoService.findCloudVideoList(userId, page, count);
            if (pageBean == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
            }
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
        } else {
            PageBean<CloudVideoActivities> pageBean = null;
            pageBean = cloudVideoService.findCloudVideoList2(userId, page, count);
            if (pageBean == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
            }
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
        }
    }

    /***
     * 参加活动
     * @param cloudVideoActivities
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData joinCloudVideo(@Valid @RequestBody CloudVideoActivities cloudVideoActivities, BindingResult bindingResult) {
        // 检测之前是否已经参加
        CloudVideoActivities activities = cloudVideoService.findDetails(cloudVideoActivities.getUserId(), cloudVideoActivities.getSelectionType());
        if (activities != null) {
            return returnData(StatusCode.CODE_SELF_CHANNEL_VIP_NOT_JOIN_ACTIVITIES.CODE_VALUE, "您已经参加该活动!", new JSONObject());
        }
        cloudVideoActivities.setTime(new Date());
        cloudVideoService.addSelection(cloudVideoActivities);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询用户是否参加过活动
     * @param selectionType 活动分类 0云视频  (后续添加)
     * @return
     */
    @Override
    public ReturnData judgeJoin(@PathVariable int selectionType) {
        long id = 0;
        int isJoin = 0;// 0未参加 1已参加
        int isVip = 1;// 0不是会员 1会员
        //验证是否是自频道会员
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_SELFCHANNELVIP + CommonUtils.getMyId());
        if (map == null || map.size() <= 0) {
            SelfChannelVip vip = selfChannelVipService.findDetails(CommonUtils.getMyId());
            if (vip == null) {
                isVip = 0;
            }
            //放入缓存
            Map<String, Object> ordersMap = CommonUtils.objectToMap(vip);
            redisUtils.hmset(Constants.REDIS_KEY_SELFCHANNELVIP + CommonUtils.getMyId(), ordersMap, Constants.USER_TIME_OUT);
        } else {
            SelfChannelVip vip = (SelfChannelVip) CommonUtils.mapToObject(map, SelfChannelVip.class);
            if (vip != null) {
                if (vip.getExpiretTime().getTime() < new Date().getTime()) {
                    isVip = 0;
                    //清除缓存中的信息
                    redisUtils.expire(Constants.REDIS_KEY_SELFCHANNELVIP + vip.getUserId(), 0);
                }
            }
        }
        CloudVideoActivities activities = cloudVideoService.findDetails(CommonUtils.getMyId(), selectionType);
        if (activities != null) {
            id = activities.getId();
            isJoin = 1;
        }
        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", id);
        map2.put("isJoin", isJoin);
        map2.put("isVip", isVip);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map2);
    }

    /***
     * 分页查询参加活动的人员
     * @param selectionType  活动分类 0云视频  (后续添加)
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findPersonnel(@PathVariable int selectionType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<CloudVideoActivities> pageBean = null;
        pageBean = cloudVideoService.findPersonnelList(selectionType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 投票
     * @param cloudVideoVote
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData cloudVideoVote(@Valid @RequestBody CloudVideoVote cloudVideoVote, BindingResult bindingResult) {
        //验证是不是自己
        if (CommonUtils.getMyId() == cloudVideoVote.getUserId()) {
            return returnData(StatusCode.CODE_NOT_AUTHORITY_VOTE.CODE_VALUE, "投票失败，不能给自己投票!", new JSONObject());
        }
        //判断当前用户是否给该用户投过票 以每天凌晨0点为准 每天每人只能给同一个人投一次票
        CloudVideoVote vote = cloudVideoService.findTicket(CommonUtils.getMyId(), cloudVideoVote.getUserId(), cloudVideoVote.getSelectionType());
        if (vote != null) {
            return returnData(StatusCode.CODE_ALREADY_VOTE.CODE_VALUE, "今天已经对该用户进行过投票", new JSONObject());
        }
        CloudVideoActivities activities = cloudVideoService.findDetails(cloudVideoVote.getUserId(), cloudVideoVote.getSelectionType());
        if (activities == null) {
            return returnData(StatusCode.CODE_SELF_CHANNEL_VIP_JOIN_ACTIVITIES.CODE_VALUE, "该用户还未参加该活动!", new JSONObject());
        }
        cloudVideoVote.setTime(new Date());
        cloudVideoService.addVote(cloudVideoVote);
        activities.setVotesCounts(activities.getVotesCounts() + 1);
        cloudVideoService.updateNumber(activities);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询投票历史
     * @param userId  用户ID
     * @param selectionType  活动分类 0云视频  (后续添加)
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findCloudVoteList(@PathVariable long userId, @PathVariable int selectionType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        List list = null;
        PageBean<CloudVideoVote> pageBean = null;
        pageBean = cloudVideoService.findVoteList(userId, selectionType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                UserInfo userInfo = null;
                CloudVideoVote vote = null;
                vote = (CloudVideoVote) list.get(i);
                if (vote != null) {
                    userInfo = userInfoUtils.getUserInfo(vote.getMyId());
                    if (userInfo != null) {
                        vote.setUserName(userInfo.getName());
                        vote.setUserHead(userInfo.getHead());
                        vote.setProTypeId(userInfo.getProType());
                        vote.setHouseNumber(userInfo.getHouseNumber());
                        vote.setProvince(userInfo.getProvince());
                        vote.setCity(userInfo.getCity());
                        vote.setDistrict(userInfo.getDistrict());
                        vote.setSex(userInfo.getSex());
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
