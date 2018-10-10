package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.UserInfo;
import com.busi.service.UserInfoService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 搬家接口
 * author：SunTianJie
 * create time：2018/10/10 9:41
 */
@RestController
public class HouseMovingController extends BaseController implements HouseMovingApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoService userInfoService;

    /***
     * 搬家接口
     * @param homeNumber     目标门牌号 格式：0_1003017
     * @param targetPassword 目标密码  (32位md5码)
     * @param password       当前密码 (32位md5码)
     * @param houseMoving    搬家类型：0搬进  1搬离
     * @return
     */
    @Override
    public ReturnData houseMoving(@PathVariable String homeNumber, @PathVariable String targetPassword,
                                  @PathVariable String password, @PathVariable int houseMoving) {
        //验证参数格式
        if (CommonUtils.checkFull(homeNumber) || homeNumber.indexOf("_") == -1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您输入的目标门牌号格式有误", new JSONObject());
        }
        if (CommonUtils.checkFull(targetPassword) || targetPassword.length() != 32) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您输入的目标门牌号的密码格式有误", new JSONObject());
        }
        if (CommonUtils.checkFull(password) || password.length() != 32) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您输入的当前账号的密码格式有误", new JSONObject());
        }
        if (houseMoving < 0 || houseMoving > 1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您选择的搬家类型有误", new JSONObject());
        }
        //添加暴力密码限制
        String errorCount = String.valueOf(redisUtils.hget(Constants.REDIS_KEY_HOUSEMOVING_ERROR_COUNT, CommonUtils.getMyId() + ""));
        if (!CommonUtils.checkFull(errorCount) && Integer.parseInt(errorCount) > 30) {//大于30次 今天该账号禁止访问
            return returnData(StatusCode.CODE_PASSWORD_ERROR_TOO_MUCH.CODE_VALUE, "您输入的目标门牌号密码错误次数过多，系统已禁止使用该功能一天，如有疑问请联系官方客服", new JSONObject());
        }
        //验证当前账号密码是否正确
        UserInfo myUserInfo = null;
        Map<String, Object> myUserMap = redisUtils.hmget(Constants.REDIS_KEY_USER + CommonUtils.getMyId());
        if (myUserMap == null || myUserMap.size() <= 0) {
            //缓存中没有用户对象信息 查询数据库
            myUserInfo = userInfoService.findUserById(CommonUtils.getMyId());
            if (myUserInfo == null) {//数据库也没有
                return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE, "当前账号不存在", new JSONObject());
            }
        } else {
            myUserInfo = (UserInfo) CommonUtils.mapToObject(myUserMap, UserInfo.class);
        }
        if (!myUserInfo.getPassword().equals(password)) {
            if (CommonUtils.checkFull(errorCount)) {//第一次错误
                redisUtils.hset(Constants.REDIS_KEY_HOUSEMOVING_ERROR_COUNT, CommonUtils.getMyId() + "", 1, 24 * 60 * 60);//设置1天后失效
            } else {
                redisUtils.hashIncr(Constants.REDIS_KEY_HOUSEMOVING_ERROR_COUNT, CommonUtils.getMyId() + "", 1);
            }
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您输入的当前账号的密码不正确", new JSONObject());
        }
        //验证目标账号状态和密码是否正确
        UserInfo targetUserInfo = null;
        Object userId = redisUtils.hget(Constants.REDIS_KEY_HOUSENUMBER, homeNumber);
        //判断门牌号与账号之间的对应关系在缓存中是否存在
        if (userId == null || Long.parseLong(userId.toString()) <= 0) {
            //门牌号与账号之间的对应关系再缓存中不存在  查询数据库
            String houseArray[] = homeNumber.split("_");
            targetUserInfo = userInfoService.findUserByHouseNumber(Integer.parseInt(houseArray[0]), houseArray[1]);
            if (targetUserInfo == null) {
                return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE, "目标账号不存在", new JSONObject());
            }
        } else {
            Map<String, Object> targetUserMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userId);
            if (targetUserMap == null || targetUserMap.size() <= 0) {
                //缓存中没有用户对象信息 查询数据库
                targetUserInfo = userInfoService.findUserById(Long.parseLong(userId.toString()));
                if (targetUserInfo == null) {//数据库也没有
                    return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE, "目标账号不存在", new JSONObject());
                }
            } else {
                targetUserInfo = (UserInfo) CommonUtils.mapToObject(targetUserMap, UserInfo.class);
            }
        }
        if(targetUserInfo.getAccountStatus()!=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "目标账号存在异常，无法搬家", new JSONObject());
        }
        if (!targetUserInfo.getPassword().equals(targetPassword)) {
            if (CommonUtils.checkFull(errorCount)) {//第一次错误
                redisUtils.hset(Constants.REDIS_KEY_HOUSEMOVING_ERROR_COUNT, userId.toString(), 1, 24 * 60 * 60);//设置1天后失效
            } else {
                redisUtils.hashIncr(Constants.REDIS_KEY_HOUSEMOVING_ERROR_COUNT, userId.toString(), 1);
            }
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您输入的目标账号的密码不正确", new JSONObject());
        }
        //验证通过 开始搬家
        if (houseMoving == 0) {//搬进
            //将目标账号的门牌号和密码信息换成当前账号的,并停用当前账号
            targetUserInfo.setProType(myUserInfo.getProType());
            targetUserInfo.setHouseNumber(myUserInfo.getHouseNumber());
            targetUserInfo.setPassword(myUserInfo.getPassword());
            myUserInfo.setAccountStatus(2);
        } else {//搬离
            //将当前账号的门牌号和密码信息换成目标账号的,并停用目标账号
            myUserInfo.setProType(targetUserInfo.getProType());
            myUserInfo.setHouseNumber(targetUserInfo.getHouseNumber());
            myUserInfo.setPassword(targetUserInfo.getPassword());
            targetUserInfo.setAccountStatus(2);
        }
        //更新数据库
        userInfoService.updateByHouseMoving(myUserInfo);
        userInfoService.updateByHouseMoving(targetUserInfo);
        //清空缓存 重新登录
        redisUtils.hdel(Constants.REDIS_KEY_PHONENUMBER, targetUserInfo.getPhone());
        redisUtils.hdel(Constants.REDIS_KEY_HOUSENUMBER, targetUserInfo.getProType() + "_" + targetUserInfo.getHouseNumber());
        redisUtils.hdel(Constants.REDIS_KEY_OTHERNUMBER, targetUserInfo.getOtherPlatformType() + "_" + targetUserInfo.getOtherPlatformKey());
        redisUtils.expire(Constants.REDIS_KEY_USER + targetUserInfo.getUserId(), 0);//0s后过期
        redisUtils.hdel(Constants.REDIS_KEY_PHONENUMBER, myUserInfo.getPhone());
        redisUtils.hdel(Constants.REDIS_KEY_HOUSENUMBER, myUserInfo.getProType() + "_" + myUserInfo.getHouseNumber());
        redisUtils.hdel(Constants.REDIS_KEY_OTHERNUMBER, myUserInfo.getOtherPlatformType() + "_" + myUserInfo.getOtherPlatformKey());
        redisUtils.expire(Constants.REDIS_KEY_USER + myUserInfo.getUserId(), 0);//0s后过期
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
