package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.UserHeadNotes;
import com.busi.entity.UserInfo;
import com.busi.mq.MqProducer;
import com.busi.service.UserHeadNotesService;
import com.busi.service.UserInfoService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 用户信息相关接口（内部调用）
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
@RestController
public class UserInfoLController extends BaseController implements UserInfoLocalController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqProducer mqProducer;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserHeadNotesService userHeadNotesService;

    /***
     * 查询用户信息
     * @param userId
     * @return
     */
    @Override
    public UserInfo getUserInfo(@PathVariable(value = "userId") long userId) {
        Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userId);
        UserInfo userInfo = null;
        if (userMap == null || userMap.size() <= 0) {
            //缓存中没有用户对象信息 查询数据库
            UserInfo u = userInfoService.findUserById(userId);
            if (u == null) {//数据库也没有
                return null;
            }
            userMap = CommonUtils.objectToMap(u);
            redisUtils.hmset(Constants.REDIS_KEY_USER + userId, userMap, Constants.USER_TIME_OUT);
        }
        userInfo = (UserInfo) CommonUtils.mapToObject(userMap, UserInfo.class);
        return userInfo;
    }

    /***
     * 查询用户信息
     * @param houseNumber 0_1001518
     * @return
     */
    @Override
    public UserInfo getUserInfoByHouseNumber(@PathVariable(value = "houseNumber") String houseNumber) {
        Object obj = redisUtils.hget(Constants.REDIS_KEY_HOUSENUMBER, houseNumber);
        UserInfo userInfo = null;
        if (obj == null || CommonUtils.checkFull(String.valueOf(obj.toString()))) {
            userInfo = userInfoService.findUserByHouseNumber(Integer.parseInt(houseNumber.split("_")[0]), houseNumber.split("_")[1]);
        }else{
            long userId = Long.parseLong(obj.toString());
            Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userId);
            if (userMap != null && userMap.size() > 0) {
                userInfo = (UserInfo) CommonUtils.mapToObject(userMap, UserInfo.class);
            } else {
                userInfo = userInfoService.findUserByHouseNumber(Integer.parseInt(houseNumber.split("_")[0]), houseNumber.split("_")[1]);
            }
        }
        return userInfo;
    }

    /***
     * 更新用户新人标识
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData updateIsNew(@RequestBody UserInfo userInfo) {

        int count = userInfoService.updateIsNewUser(userInfo);
        if (count <= 0) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "更新用户信息失败", new JSONObject());
        }
        //更新缓存数据
        Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userInfo.getUserId());
        if (userMap != null && userMap.size() > 0) {//缓存中存在 才更新 不存在不更新
            //更新缓存 自己修改自己的用户信息 不考虑并发问题
            redisUtils.hset(Constants.REDIS_KEY_USER + userInfo.getUserId(), "isNewUser", userInfo.getIsNewUser(), Constants.USER_TIME_OUT);
            redisUtils.expire(Constants.REDIS_KEY_USER + userInfo.getUserId(), Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新用户V认证标识
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData updateUserCe(@RequestBody UserInfo userInfo) {
        int count = userInfoService.updateUserCe(userInfo);
        if (count <= 0) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "更新用户V认证标识失败", new JSONObject());
        }
        //更新缓存数据
        Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userInfo.getUserId());
        if (userMap != null && userMap.size() > 0) {//缓存中存在 才更新 不存在不更新
            //更新缓存 自己修改自己的用户信息 不考虑并发问题
            redisUtils.hset(Constants.REDIS_KEY_USER + userInfo.getUserId(), "user_ce", userInfo.getIsNewUser(), Constants.USER_TIME_OUT);
            redisUtils.expire(Constants.REDIS_KEY_USER + userInfo.getUserId(), Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新生活圈首次视频发布状态
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData updateHomeBlogStatus(@RequestBody UserInfo userInfo) {

        int count = userInfoService.updateHomeBlogStatus(userInfo);
        if (count <= 0) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "更新生活圈首次视频发布状态失败", new JSONObject());
        }
        //更新缓存数据
        Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userInfo.getUserId());
        if (userMap != null && userMap.size() > 0) {//缓存中存在 才更新 不存在不更新
            //更新缓存 自己修改自己的用户信息 不考虑并发问题
            redisUtils.hset(Constants.REDIS_KEY_USER + userInfo.getUserId(), "homeBlogStatus", userInfo.getHomeBlogStatus(), Constants.USER_TIME_OUT);
            redisUtils.expire(Constants.REDIS_KEY_USER + userInfo.getUserId(), Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新用户手机号绑定状态
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData updateBindPhone(@RequestBody UserInfo userInfo) {
        userInfoService.updateBindPhone(userInfo);
        Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userInfo.getUserId());
        if (userMap != null&&userMap.size() > 0) {//缓存中存在 则更新缓存
            //修改缓存数据
            if(!CommonUtils.checkFull(userInfo.getPhone())){
                redisUtils.hset(Constants.REDIS_KEY_PHONENUMBER,userInfo.getPhone(),userInfo.getUserId());
            }else{
                Object phone = userMap.get("phone");
                if(phone!=null){
                    redisUtils.hdel(Constants.REDIS_KEY_PHONENUMBER,phone.toString());
                }
            }
            userMap.put("phone",userInfo.getPhone());
            redisUtils.hmset(Constants.REDIS_KEY_USER+userInfo.getUserId(),userMap,Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新用户第三方平台账号绑定状态
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData updateBindOther(@RequestBody UserInfo userInfo) {
        userInfoService.updateBindOther(userInfo);
        Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userInfo.getUserId());
        if (userMap != null&&userMap.size() > 0) {//缓存中存在 则更新缓存
            //修改缓存数据
            if(!CommonUtils.checkFull(userInfo.getOtherPlatformKey())){
                redisUtils.hset(Constants.REDIS_KEY_OTHERNUMBER,userInfo.getOtherPlatformType() + "_" + userInfo.getOtherPlatformKey(),userInfo.getUserId());
            }else{
                UserInfo u = (UserInfo) CommonUtils.mapToObject(userMap,UserInfo.class);
                if(u!=null){
                    redisUtils.hdel(Constants.REDIS_KEY_OTHERNUMBER, u.getOtherPlatformType() + "_" + u.getOtherPlatformKey());
                }
            }
            userMap.put("otherPlatformKey",userInfo.getOtherPlatformKey());
            userMap.put("otherPlatformAccount",userInfo.getOtherPlatformAccount());
            userMap.put("otherPlatformType",userInfo.getOtherPlatformType());
            redisUtils.hmset(Constants.REDIS_KEY_USER+userInfo.getUserId(),userMap,Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更换欢迎视频接口(仅用于发布生活圈视频时更新机器人欢迎视频功能 刷假数据)
     * @param userHeadNotes
     * @return
     */
    public ReturnData updateWelcomeVideoByHomeBlog(@RequestBody UserHeadNotes userHeadNotes) {
        UserHeadNotes uhn = userHeadNotesService.findUserHeadNotesById(userHeadNotes.getUserId());
        if(uhn!=null){//更新
            uhn.setWelcomeVideoPath(userHeadNotes.getWelcomeVideoPath());
            uhn.setWelcomeVideoCoverPath(userHeadNotes.getWelcomeVideoCoverPath());
            userHeadNotesService.updateWelcomeVideo(uhn);
        }else{//新增
            int number = new Random().nextInt(3) + 1;
            int hy = new Random().nextInt(11) + 1;
            if(hy>=10){
                userHeadNotes.setGardenCover("/image/roomCover/pub_hy_image_0"+hy+".jpg");
            }else{
                userHeadNotes.setGardenCover("/image/roomCover/pub_hy_image_00"+hy+".jpg");
            }
            userHeadNotes.setLivingRoomCover("/image/roomCover/pub_kt_image_00"+number+".jpg");
            userHeadNotes.setHomeStoreCover("/image/roomCover/pub_jd_image_00"+number+".jpg");
            userHeadNotes.setStorageRoomCover("/image/roomCover/pub_ccs_image_00"+number+".jpg");
            userHeadNotesService.add(userHeadNotes);
        }
        //将缓存中数据 清除
        redisUtils.expire(Constants.REDIS_KEY_USER_HEADNOTES+userHeadNotes.getUserId(),0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 新增靓号接口(本地调用)
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData addGoodNumberToUser(@RequestBody UserInfo userInfo) {
        //开始注册
        UserInfo newUserInfo = new UserInfo();
        newUserInfo.setName(userInfo.getName());
        newUserInfo.setPassword(userInfo.getPassword());
        newUserInfo.setIm_password(CommonUtils.strToMD5(userInfo.getPassword(),32));//环信密码为两遍MD5
        newUserInfo.setSex(userInfo.getSex());
        newUserInfo.setBirthday(userInfo.getBirthday());
        newUserInfo.setCountry(userInfo.getCountry());
        newUserInfo.setProvince(userInfo.getProvince());
        newUserInfo.setCity(userInfo.getCity());
        newUserInfo.setDistrict(userInfo.getDistrict());
        newUserInfo.setTime(new Date());
        newUserInfo.setAccessRights(1);
        newUserInfo.setIsGoodNumber(1);
        newUserInfo.setProType(userInfo.getProType());
        newUserInfo.setHouseNumber(userInfo.getHouseNumber());
        //生成默认头像
        Random random = new Random();
        newUserInfo.setHead("image/head/defaultHead/defaultHead_"+random.nextInt(20)+"_225x225.jpg");
        //写入数据库
        userInfoService.add(newUserInfo);
        //同步环信 由于环信服务端接口限流每秒30次 所以此操作改到客户端完成 拼接注册环信需要的参数 返回给客户端 环信账号改成用户ID
        Map<String,String> im_map = new HashMap<>();
        im_map.put("proType",newUserInfo.getProType()+"");
        im_map.put("houseNumber",newUserInfo.getHouseNumber()+"");
        im_map.put("myId",newUserInfo.getUserId()+"");
        im_map.put("password",newUserInfo.getIm_password());//环信密码
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",im_map);
    }
    /***
     * 更新用户找人倾诉状态
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData updateTalkToSomeoneStatus(@RequestBody UserInfo userInfo) {
        int count = userInfoService.updateTalkToSomeoneStatus(userInfo);
        if (count <= 0) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "更新用户找人倾诉状态失败", new JSONObject());
        }
        //更新缓存数据
        Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userInfo.getUserId());
        if (userMap != null && userMap.size() > 0) {//缓存中存在 才更新 不存在不更新
            //更新缓存 自己修改自己的用户信息 不考虑并发问题
            redisUtils.hset(Constants.REDIS_KEY_USER + userInfo.getUserId(), "talkToSomeoneStatus", userInfo.getTalkToSomeoneStatus(), Constants.USER_TIME_OUT);
            redisUtils.expire(Constants.REDIS_KEY_USER + userInfo.getUserId(), Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
    /***
     * 更新用户聊天互动状态
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData updateChatnteractionStatus(@RequestBody UserInfo userInfo) {
        int count = userInfoService.updateChatnteractionStatus(userInfo);
        if (count <= 0) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "更新用户聊天互动状态失败", new JSONObject());
        }
        //更新缓存数据
        Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userInfo.getUserId());
        if (userMap != null && userMap.size() > 0) {//缓存中存在 才更新 不存在不更新
            //更新缓存 自己修改自己的用户信息 不考虑并发问题
            redisUtils.hset(Constants.REDIS_KEY_USER + userInfo.getUserId(), "chatnteractionStatus", userInfo.getChatnteractionStatus(), Constants.USER_TIME_OUT);
            redisUtils.expire(Constants.REDIS_KEY_USER + userInfo.getUserId(), Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

}
