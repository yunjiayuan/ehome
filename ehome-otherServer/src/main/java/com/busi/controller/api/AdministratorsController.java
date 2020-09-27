package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.AdministratorsService;
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
 * 管理员相关接口
 * author：ZJJ
 * create time：2020-09-27 14:06:30
 */
@RestController
public class AdministratorsController extends BaseController implements AdministratorsApiController {

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    RedisUtils redisUtils;


    @Autowired
    AdministratorsService administratorsService;

    /***
     * 新增管理员
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addAdministrator(@Valid @RequestBody Administrators homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断是否有权限
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_USER_ADMINISTRATORS + CommonUtils.getMyId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Administrators administrator = administratorsService.findByUserId(CommonUtils.getMyId());
            if (administrator != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(administrator);
                redisUtils.hmset(Constants.REDIS_KEY_USER_ADMINISTRATORS + administrator.getUserId(), kitchenMap, 0);
            }
        }
        Administrators administrators = (Administrators) CommonUtils.mapToObject(kitchenMap, Administrators.class);
        if (administrators == null || administrators.getLevels() <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "没有权限", new JSONObject());
        }
        Map<String, Object> kitchenMap2 = redisUtils.hmget(Constants.REDIS_KEY_USER_ADMINISTRATORS + homeHospital.getUserId());
        if (kitchenMap2 == null || kitchenMap2.size() <= 0) {
            Administrators administrator2 = administratorsService.findByUserId(homeHospital.getUserId());
            if (administrator2 != null) {
                //放入缓存
                kitchenMap2 = CommonUtils.objectToMap(administrator2);
                redisUtils.hmset(Constants.REDIS_KEY_USER_ADMINISTRATORS + administrator2.getUserId(), kitchenMap2, 0);
            }
        }
        Administrators kitchen = (Administrators) CommonUtils.mapToObject(kitchenMap2, Administrators.class);
        if (kitchen != null) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "对方已经是管理员了", new JSONObject());
        }
        if (administrators.getLevels() == 1) {//高级管理员对普通管理员有新增、删除的权限
            if (kitchen.getLevels() >= 1) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "没有权限", new JSONObject());
            }
        }
        if (administrators.getLevels() == 2) {//最高管理员对高级管理员、普通管理员有新增、删除的权限
            if (kitchen.getLevels() >= 2) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "没有权限", new JSONObject());
            }
        }
        //新增管理员
        administratorsService.addAdministrator(homeHospital);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除管理员
     * @param userId 管理员ID
     * @return:
     */
    @Override
    public ReturnData delAdministrator(@PathVariable long userId) {
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_USER_ADMINISTRATORS + CommonUtils.getMyId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Administrators administrator = administratorsService.findByUserId(CommonUtils.getMyId());
            if (administrator != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(administrator);
                redisUtils.hmset(Constants.REDIS_KEY_USER_ADMINISTRATORS + administrator.getUserId(), kitchenMap, 0);
            }
        }
        Administrators administrators = (Administrators) CommonUtils.mapToObject(kitchenMap, Administrators.class);
        if (administrators == null || administrators.getLevels() <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "没有权限", new JSONObject());
        }
        Map<String, Object> kitchenMap2 = redisUtils.hmget(Constants.REDIS_KEY_USER_ADMINISTRATORS + userId);
        if (kitchenMap2 == null || kitchenMap2.size() <= 0) {
            Administrators administrator2 = administratorsService.findByUserId(userId);
            if (administrator2 != null) {
                //放入缓存
                kitchenMap2 = CommonUtils.objectToMap(administrator2);
                redisUtils.hmset(Constants.REDIS_KEY_USER_ADMINISTRATORS + administrator2.getUserId(), kitchenMap2, 0);
            }
        }
        Administrators kitchen = (Administrators) CommonUtils.mapToObject(kitchenMap2, Administrators.class);
        if (kitchen == null) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "对方不存在", new JSONObject());
        }
        if (administrators.getLevels() == 1) {//高级管理员对普通管理员有新增、删除的权限
            if (kitchen.getLevels() >= 1) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "没有权限", new JSONObject());
            }
        }
        if (administrators.getLevels() == 2) {//最高管理员对高级管理员、普通管理员有新增、删除的权限
            if (kitchen.getLevels() >= 2) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "没有权限", new JSONObject());
            }
        }
        //删除管理员
        administratorsService.delAdministrator(userId);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_USER_ADMINISTRATORS + userId, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询管理员列表
     * @param levels   级别：-1全部  0普通管理员 1高级管理员 2最高管理员
     * @return
     */
    @Override
    public ReturnData findAdministratorlist(@PathVariable int levels) {
        //开始查询
        List list = null;
        list = administratorsService.findAdministratorlist(levels);
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        for (int i = 0; i < list.size(); i++) {
            Administrators sa = (Administrators) list.get(i);
            if (sa != null) {
                UserInfo userInfo = null;
                userInfo = userInfoUtils.getUserInfo(sa.getUserId());
                if (userInfo != null) {
                    sa.setName(userInfo.getName());
                    sa.setHead(userInfo.getHead());
                    sa.setProTypeId(userInfo.getProType());
                    sa.setHouseNumber(userInfo.getHouseNumber());
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 查询当前用户管理员权限(返回""代表没有权限)
     * @return
     */
    @Override
    public ReturnData findAdministrator() {
        int levels = -1;   //级别：-1普通用户 0普通管理员 1高级管理员 2最高管理员
        String authorityId = "";  // 对应权限id,逗号分隔
        Object obj = redisUtils.hget(Constants.REDIS_KEY_USER_ADMINISTRATORS, "levels_" + CommonUtils.getMyId());
        if (obj != null) {
            levels = Integer.parseInt(String.valueOf(obj));
        } else {
            Administrators administrator = administratorsService.findByUserId(CommonUtils.getMyId());
            if (administrator != null) {
                levels = administrator.getLevels();
                redisUtils.hset(Constants.REDIS_KEY_USER_ADMINISTRATORS, "levels_" + CommonUtils.getMyId(), administrator.getLevels());
            }
        }
//        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_USER_ADMINISTRATORS + CommonUtils.getMyId());
//        if (kitchenMap == null || kitchenMap.size() <= 0) {
//            Administrators administrator = administratorsService.findByUserId(CommonUtils.getMyId());
//            if (administrator != null) {
//                //放入缓存
//                kitchenMap = CommonUtils.objectToMap(administrator);
//                redisUtils.hmset(Constants.REDIS_KEY_USER_ADMINISTRATORS + administrator.getUserId(), kitchenMap, 0);
//            }
//        }
//        Administrators administrators = (Administrators) CommonUtils.mapToObject(kitchenMap, Administrators.class);
//        if (administrators != null && administrators.getLevels() >= 0) {
//            levels = administrators.getLevels();
//            AdministratorsAuthority authority = administratorsService.findUserId(administrators.getLevels());
//            if (authority != null) {
//                authorityId = authority.getAuthorityId();
//            }
//        }
        if (levels >= 0) {
            AdministratorsAuthority authority = administratorsService.findUserId(levels);
            if (authority != null) {
                authorityId = authority.getAuthorityId();
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("levels", levels);
        map.put("authorityId", authorityId);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
