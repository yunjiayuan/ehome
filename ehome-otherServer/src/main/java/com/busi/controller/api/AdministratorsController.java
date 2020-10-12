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
import java.util.Date;
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
        int levels = CommonUtils.getAdministrator(CommonUtils.getMyId(), redisUtils);
        if (levels == -1) {//级别：-1普通用户 0普通管理员 1高级管理员 2最高管理员
            Administrators administrator = administratorsService.findByUserId(CommonUtils.getMyId());
            if (administrator != null) {
                levels = administrator.getLevels();
                redisUtils.hset(Constants.REDIS_KEY_USER_ADMINISTRATORS, "levels_" + CommonUtils.getMyId(), administrator.getLevels());
            }
        }
        if (levels <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的级别无权限进行此操作", new JSONObject());
        }
        int levels2 = CommonUtils.getAdministrator(homeHospital.getUserId(), redisUtils);
        if (levels2 == -1) {//级别：-1普通用户 0普通管理员 1高级管理员 2最高管理员
            Administrators administrator = administratorsService.findByUserId(homeHospital.getUserId());
            if (administrator != null) {
                levels2 = administrator.getLevels();
                redisUtils.hset(Constants.REDIS_KEY_USER_ADMINISTRATORS, "levels_" + homeHospital.getUserId(), administrator.getLevels());
            }
        }
        if (levels2 >= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "对方已经是管理员了", new JSONObject());
        }
        if (levels2 >= levels) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的级别无权限进行此操作", new JSONObject());
        }
//        if (levels == 1) {//高级管理员对普通管理员有新增、删除的权限
//            if (homeHospital.getLevels() >= 1) {
//                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的级别无权限进行此操作", new JSONObject());
//            }
//        }
//        if (levels == 2) {//最高管理员对高级管理员、普通管理员有新增、删除的权限
//            if (homeHospital.getLevels() >= 2) {
//                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的级别无权限进行此操作", new JSONObject());
//            }
//        }
        //新增管理员
        homeHospital.setTime(new Date());
        administratorsService.addAdministrator(homeHospital);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 修改管理员权限
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeAdministrator(@Valid @RequestBody Administrators homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断是否有权限
        //查询缓存 缓存中不存在 查询数据库
        int levels = CommonUtils.getAdministrator(CommonUtils.getMyId(), redisUtils);
        if (levels == -1) {//级别：-1普通用户 0普通管理员 1高级管理员 2最高管理员
            Administrators administrator = administratorsService.findByUserId(CommonUtils.getMyId());
            if (administrator != null) {
                levels = administrator.getLevels();
                redisUtils.hset(Constants.REDIS_KEY_USER_ADMINISTRATORS, "levels_" + CommonUtils.getMyId(), levels);
            }
        }
        if (levels <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的级别无权限进行此操作", new JSONObject());
        }
        int levels2 = CommonUtils.getAdministrator(homeHospital.getUserId(), redisUtils);
        if (levels2 == -1) {//级别：-1普通用户 0普通管理员 1高级管理员 2最高管理员
            Administrators administrator = administratorsService.findByUserId(homeHospital.getUserId());
            if (administrator != null) {
                levels2 = administrator.getLevels();
            }
        }
        if (levels2 == -1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "对方还不是管理员", new JSONObject());
        }
        if (levels2 >= levels) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的级别无权限进行此操作", new JSONObject());
        }
        administratorsService.changeAdministrator(homeHospital);
        //更新缓存
        redisUtils.hset(Constants.REDIS_KEY_USER_ADMINISTRATORS, "levels_" + homeHospital.getUserId(), homeHospital.getLevels());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除管理员
     * @param userId 管理员ID
     * @return:
     */
    @Override
    public ReturnData delAdministrator(@PathVariable long userId) {
        int levels = CommonUtils.getAdministrator(CommonUtils.getMyId(), redisUtils);
        if (levels == -1) {//级别：-1普通用户 0普通管理员 1高级管理员 2最高管理员
            Administrators administrator = administratorsService.findByUserId(CommonUtils.getMyId());
            if (administrator != null) {
                levels = administrator.getLevels();
                redisUtils.hset(Constants.REDIS_KEY_USER_ADMINISTRATORS, "levels_" + CommonUtils.getMyId(), administrator.getLevels());
            }
        }
        if (levels <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "没有权限", new JSONObject());
        }
        int levels2 = CommonUtils.getAdministrator(userId, redisUtils);
        if (levels2 == -1) {//级别：-1普通用户 0普通管理员 1高级管理员 2最高管理员
            Administrators administrator = administratorsService.findByUserId(userId);
            if (administrator != null) {
                levels2 = administrator.getLevels();
                redisUtils.hset(Constants.REDIS_KEY_USER_ADMINISTRATORS, "levels_" + userId, administrator.getLevels());
            }
        }
        if (levels2 == -1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "对方还不是管理员", new JSONObject());
        }
        if (levels2 >= levels) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的级别无权限进行此操作", new JSONObject());
        }
//        if (levels == 1) {//高级管理员对普通管理员有新增、删除的权限
//            if (levels2 >= 1) {
//                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的级别无权限进行此操作", new JSONObject());
//            }
//        }
//        if (levels == 2) {//最高管理员对高级管理员、普通管理员有新增、删除的权限
//            if (levels2 >= 2) {
//                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您的级别无权限进行此操作", new JSONObject());
//            }
//        }
        //删除管理员
        administratorsService.delAdministrator(userId);
        //更新缓存
        redisUtils.hset(Constants.REDIS_KEY_USER_ADMINISTRATORS, "levels_" + userId, -1);
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
        String authorityId = "";  // 对应权限id,逗号分隔
        int levels = CommonUtils.getAdministrator(CommonUtils.getMyId(), redisUtils);
        if (levels == -1) {//级别：-1普通用户 0普通管理员 1高级管理员 2最高管理员
            Administrators administrator = administratorsService.findByUserId(CommonUtils.getMyId());
            if (administrator != null) {
                levels = administrator.getLevels();
                redisUtils.hset(Constants.REDIS_KEY_USER_ADMINISTRATORS, "levels_" + CommonUtils.getMyId(), administrator.getLevels());
            }
        }
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
