package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.BigVAccountsExamineService;
import com.busi.service.UserAccountSecurityService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

/***
 * V认证相关接口
 * author：zhaojiajie
 * create time：2020-07-07 15:18:53
 */
@RestController
public class BigVAccountsExamineController extends BaseController implements BigVAccountsExamineApiController {

    @Autowired
    BigVAccountsExamineService bigVAccountsExamineService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserAccountSecurityService userAccountSecurityService;

    @Autowired
    UserInfoUtils userInfoUtils;

    /***
     * 新增
     * @param homeAlbum
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addBigVExamine(@Valid @RequestBody BigVAccountsExamine homeAlbum, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断该用户是否实名
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + CommonUtils.getMyId());
        if (map == null || map.size() <= 0) {
            UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(CommonUtils.getMyId());
            if (userAccountSecurity == null) {
                return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
            } else {
                userAccountSecurity.setRedisStatus(1);//数据库中已有记录
            }
            //放到缓存中
            map = CommonUtils.objectToMap(userAccountSecurity);
            redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + CommonUtils.getMyId(), map, Constants.USER_TIME_OUT);
        }
        UserAccountSecurity userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
        if (userAccountSecurity == null || CommonUtils.checkFull(userAccountSecurity.getRealName()) || CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
        }
        BigVAccountsExamine modelPwd = bigVAccountsExamineService.findById(CommonUtils.getMyId());
        homeAlbum.setTime(new Date());
        if (modelPwd != null) {//有记录的话覆盖
            homeAlbum.setId(modelPwd.getId());
            bigVAccountsExamineService.changeAppealState(homeAlbum);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        bigVAccountsExamineService.add(homeAlbum);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询认证列表
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findBigVExamineList(@PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        long myId = CommonUtils.getMyId();
        if (myId != 10076 && myId != 12770 && myId != 9389 && myId != 9999 && myId != 13005 && myId != 12774 && myId != 13031 && myId != 12769 && myId != 12796 && myId != 10053) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限进行此操作，请联系管理员申请权限!", new JSONObject());
        }
        //开始查询
        PageBean<BigVAccountsExamine> pageBean = null;
        pageBean = bigVAccountsExamineService.findChildAppealList(page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = pageBean.getList();
        for (int i = 0; i < list.size(); i++) {
            BigVAccountsExamine pwdAppeal = (BigVAccountsExamine) list.get(i);
            if (pwdAppeal == null) {
                continue;
            }
            UserInfo sendInfoCache = null;
            sendInfoCache = userInfoUtils.getUserInfo(pwdAppeal.getUserId());
            if (sendInfoCache != null) {
                pwdAppeal.setName(sendInfoCache.getName());
                pwdAppeal.setHead(sendInfoCache.getHead());
                pwdAppeal.setProTypeId(sendInfoCache.getProType());
                pwdAppeal.setHouseNumber(sendInfoCache.getHouseNumber());
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 更新认证状态
     * @param homeAlbum
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeBigVExamineState(@Valid @RequestBody BigVAccountsExamine homeAlbum, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        long myId = CommonUtils.getMyId();
        if (myId != 10076 && myId != 12770 && myId != 9389 && myId != 9999 && myId != 13005 && myId != 12774 && myId != 13031 && myId != 12769 && myId != 12796 && myId != 10053) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限进行此操作，请联系管理员申请权限!", new JSONObject());
        }
        //判断该用户是否实名
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + homeAlbum.getUserId());
        if (map == null || map.size() <= 0) {
            UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(homeAlbum.getUserId());
            if (userAccountSecurity == null) {
                homeAlbum.setState(1);//未通过
                bigVAccountsExamineService.changeAppealState(homeAlbum);
                return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
            } else {
                userAccountSecurity.setRedisStatus(1);//数据库中已有记录
            }
            //放到缓存中
            map = CommonUtils.objectToMap(userAccountSecurity);
            redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + homeAlbum.getUserId(), map, Constants.USER_TIME_OUT);
        }
        UserAccountSecurity userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
        if (userAccountSecurity == null || CommonUtils.checkFull(userAccountSecurity.getRealName()) || CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
            homeAlbum.setState(1);//未通过
            bigVAccountsExamineService.changeAppealState(homeAlbum);
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
        }
        bigVAccountsExamineService.changeAppealState(homeAlbum);

        //更新用户V认证标识
        if (homeAlbum.getState() == 2) {
            userInfoUtils.updateUserCe(homeAlbum.getUserId(), 1);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询认证
     * @param userId
     * @return
     */
    @Override
    public ReturnData findBigVExamine(@PathVariable long userId) {
        BigVAccountsExamine bigVAccountsExamine = bigVAccountsExamineService.findById(userId);
        if (bigVAccountsExamine == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", bigVAccountsExamine);
    }
}
