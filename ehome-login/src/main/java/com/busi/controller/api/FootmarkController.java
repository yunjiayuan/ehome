package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.FootmarkService;
import com.busi.service.UserInfoService;
import com.busi.service.UserRelationShipService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 足迹相关接口实现
 * @author: ZHaoJiaJie
 * @create: 2018-09-29 16:36
 */
@RestController
public class FootmarkController extends BaseController implements FootmarkApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    FootmarkService footmarkService;

    @Autowired
    UserRelationShipService userRelationShipService;

    /***
     * 查询足迹列表
     * @param userId  用户ID
     * @param footmarkType  足迹类型 -1查询除记事以外的  0.默认全部 1.公告 2.生活圈 3.图片 4.音频 5.视频  6记事  7日程
     * @param startTime  开始时间
     * @param endTime   结束时间
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findFootmarkList(@PathVariable long userId, @PathVariable int footmarkType, @PathVariable String startTime, @PathVariable String endTime, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        //验证查询权限
        PageBean<Footmark> pageBean = null;
        if (CommonUtils.getMyId() == userId) {//查自己
            pageBean = footmarkService.findList(userId, footmarkType, startTime, endTime, page, count);
        } else {//查别人
            //判断是否有权限查看别人的足迹
            Footmarkauthority posts = footmarkService.findUserId(CommonUtils.getMyId());
            if (posts == null) {
                pageBean = footmarkService.findList(userId, footmarkType, startTime, endTime, page, count);
            } else {
                //对方设置过权限
                int authority = posts.getAuthority();
                if (authority == 2) {//仅自己可见
                    return returnData(StatusCode.CODE_FOOTMARK_NOT_AUTHORITY.CODE_VALUE, "用户无访问权限!", new JSONObject());
                } else if (authority == 1) {//仅好友可见
                    //判断自己是否与该用户是好友关系
                    boolean flag = userRelationShipService.checkFriend(CommonUtils.getMyId(), userId);
                    if (flag) {//是好友
                        pageBean = footmarkService.findList(userId, footmarkType, startTime, endTime, page, count);
                    } else {//无好友关系
                        return returnData(StatusCode.CODE_FOOTMARK_NOT_AUTHORITY.CODE_VALUE, "用户无访问权限!", new JSONObject());
                    }
                } else {//公开
                    pageBean = footmarkService.findList(userId, footmarkType, startTime, endTime, page, count);
                }
            }
        }
        if (pageBean != null) {
            List list = null;
            String newUsers = "";//用户id + 名字 + 头像   逗号分隔
            list = pageBean.getList();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    Footmark ik = (Footmark) list.get(i);
                    if (ik != null && !CommonUtils.checkFull(ik.getUsers())) {
                        String[] strings = ik.getUsers().split(",");
                        for (int j = 0; j < strings.length; j++) {
                            long newUserId = Long.parseLong(strings[j]);
                            Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + newUserId);
                            UserInfo userInfo = null;
                            if (userMap == null || userMap.size() <= 0) {
                                //缓存中没有用户对象信息 查询数据库
                                UserInfo u = userInfoService.findUserById(newUserId);
                                if (u != null) {
                                    userMap = CommonUtils.objectToMap(u);
                                    redisUtils.hmset(Constants.REDIS_KEY_USER + newUserId, userMap, Constants.USER_TIME_OUT);
                                }
                            }
                            userInfo = (UserInfo) CommonUtils.mapToObject(userMap, UserInfo.class);
                            if (userInfo != null) {
                                if (j == strings.length - 1) {
                                    newUsers += newUserId + "," + userInfo.getName() + "," + userInfo.getHead();
                                    ik.setUsers(newUsers);
                                } else {
                                    newUsers += newUserId + "," + userInfo.getName() + "," + userInfo.getHead() + ";";
                                }
                            }
                        }
                    }
                }
            }
            pageBean = new PageBean<>();
            pageBean.setSize(list.size());
            pageBean.setPageNum(page);
            pageBean.setPageSize(count);
            pageBean.setList(list);
        }
        //新增任务
        mqUtils.sendTaskMQ(userId, 0, 3);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /**
     * @Description: 删除
     * @return:
     */
    @Override
    public ReturnData delFootmark(@PathVariable long userId, @PathVariable long id) {
        //验证参数
        if (userId <= 0 || id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证删除权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限删除用户[" + userId + "]的婚恋交友信息", new JSONObject());
        }
        //查询数据库
        Footmark footmark = footmarkService.findUserById(id);
        if (footmark == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        footmark.setFootmarkStatus(1);
        footmarkService.updateDel(footmark);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 设置权限
     * @Param: footmarkauthority
     * @return:
     */
    @Override
    public ReturnData setAffiche(@Valid @RequestBody Footmarkauthority footmarkauthority, BindingResult bindingResult) {
        Footmarkauthority posts = footmarkService.findUserId(CommonUtils.getMyId());
        if (posts == null) {
            footmarkService.addAuthority(footmarkauthority);
        } else {
            posts.setAuthority(footmarkauthority.getAuthority());
            footmarkService.updateAuthority(posts);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询权限详情
     * @return
     */
    @Override
    public ReturnData findAuthority() {
        //查询数据库
        Map<String, Object> map = new HashMap<>();
        Footmarkauthority posts = footmarkService.findUserId(CommonUtils.getMyId());
        if (posts == null) {
            map.put("authority", 0);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
        }
        map.put("authority", posts.getAuthority());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查找房间
     * @param roomName  房间名称
     * @param lat  纬度
     * @param lon   经度
     * @return
     */
    @Override
    public ReturnData findFootRoom(@PathVariable String roomName, @PathVariable double lat, @PathVariable double lon) {
        FaceToFaceFootprints face = new FaceToFaceFootprints();
        face.setUserId(CommonUtils.getMyId());
        face.setRoomName(roomName);
        face.setLat(lat);
        face.setLon(lon);
        Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + CommonUtils.getMyId());
        UserInfo userInfo = null;
        if (userMap == null || userMap.size() <= 0) {
            //缓存中没有用户对象信息 查询数据库
            UserInfo u = userInfoService.findUserById(CommonUtils.getMyId());
            if (u != null) {
                userMap = CommonUtils.objectToMap(u);
                redisUtils.hmset(Constants.REDIS_KEY_USER + CommonUtils.getMyId(), userMap, Constants.USER_TIME_OUT);
            }
        }
        userInfo = (UserInfo) CommonUtils.mapToObject(userMap, UserInfo.class);
        if (userInfo != null) {
            face.setName(userInfo.getName());
            face.setHead(userInfo.getHead());
            face.setProTypeId(userInfo.getProType());
            face.setHouseNumber(userInfo.getHouseNumber());
        }
        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_FACETOFACE_FOOTPRINTS + roomName, 0, -1);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                FaceToFaceFootprints footprints = (FaceToFaceFootprints) list.get(i);
                //判断是否已在房间并且位置相近
                if (footprints.getUserId() == CommonUtils.getMyId()) {
                    int distance = (int) Math.round(CommonUtils.getShortestDistance(footprints.getLon(), footprints.getLat(), lon, lat));
                    if (distance > 100) {
                        list.remove(footprints);
                        list.add(face);
                        //清除缓存中信息
                        redisUtils.expire(Constants.REDIS_KEY_FACETOFACE_FOOTPRINTS + roomName, 0);
                        //放入缓存
                        redisUtils.pushList(Constants.REDIS_KEY_FACETOFACE_FOOTPRINTS + roomName, list);
                        break;
                    }
                }
            }
            for (int i = 0; i < list.size(); i++) {
                FaceToFaceFootprints footprints = (FaceToFaceFootprints) list.get(i);
                int distance = (int) Math.round(CommonUtils.getShortestDistance(footprints.getLon(), footprints.getLat(), lon, lat));
                if (distance > 1000) {//只查找1公里范围内的房间人员
                    list.remove(footprints);
                    continue;
                }
            }
        } else {
            list.add(face);
            //放入缓存
            redisUtils.pushList(Constants.REDIS_KEY_FACETOFACE_FOOTPRINTS + roomName, list);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, list);
    }

}
