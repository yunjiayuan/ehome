package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.ChildModelService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import com.busi.utils.UserInfoUtils;
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

/***
 * 儿童/青少年模式相关接口
 * author：zhaojiajie
 * create time：22020-06-16 14:07:52
 */
@RestController
public class ChildModelController extends BaseController implements ChildModelApiController {

    @Autowired
    ChildModelService homeAlbumService;

    @Autowired
    UserInfoUtils userInfoUtils;

    /***
     * 设置密码
     * @param homeAlbum
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addChildPwd(@Valid @RequestBody ChildModelPwd homeAlbum, BindingResult bindingResult) {
        if (!CommonUtils.checkFull(homeAlbum.getPassword())) {
            String status = CommonUtils.getRandom(6, 0);
            String code = CommonUtils.strToMD5(homeAlbum.getPassword() + status, 32);
            homeAlbum.setPassword(code);
            homeAlbum.setStatus(status);
            homeAlbum.setUserId(CommonUtils.getMyId());
            homeAlbumService.addPwd(homeAlbum);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 修改密码
     * @param homeAlbum
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData modifyChildPwd(@Valid @RequestBody ChildModelPwd homeAlbum, BindingResult bindingResult) {
        ChildModelPwd modelPwd = homeAlbumService.findById(CommonUtils.getMyId());
        if (!CommonUtils.checkFull(homeAlbum.getPassword())) {
            if (modelPwd == null) {
                String status = CommonUtils.getRandom(6, 0);
                String code = CommonUtils.strToMD5(homeAlbum.getPassword() + status, 32);
                homeAlbum.setPassword(code);
                homeAlbum.setStatus(status);
                homeAlbum.setUserId(CommonUtils.getMyId());
                homeAlbumService.addPwd(homeAlbum);
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            if (CommonUtils.checkFull(homeAlbum.getOldPassword())) {
                return returnData(StatusCode.CODE_PASSWORD_ERROR.CODE_VALUE, "密码输入错误！", new JSONObject());
            }
            //对密码进行操作
            //比对密码
            String status = modelPwd.getStatus();
            if (modelPwd.getPassword().equals(CommonUtils.strToMD5(homeAlbum.getOldPassword() + status, 32))) {
                //更新密码
                status = CommonUtils.getRandom(6, 0);
                String codes = CommonUtils.strToMD5(homeAlbum.getPassword() + status, 32);
                modelPwd.setPassword(codes);
                modelPwd.setStatus(status);
                homeAlbumService.updatePwd(modelPwd);
            } else {
                return returnData(StatusCode.CODE_PASSWORD_ERROR.CODE_VALUE, "密码输入错误！", new JSONObject());
            }
        } else {//密码为空时验证通过则删除密码/关闭模式
            if (CommonUtils.checkFull(homeAlbum.getOldPassword())) {
                return returnData(StatusCode.CODE_PASSWORD_ERROR.CODE_VALUE, "密码输入错误！", new JSONObject());
            }
            //比对密码
            String status = modelPwd.getStatus();
            if (modelPwd.getPassword().equals(CommonUtils.strToMD5(homeAlbum.getOldPassword() + status, 32))) {
                //删除密码/关闭模式
                homeAlbumService.delPwd(CommonUtils.getMyId());
            } else {
                return returnData(StatusCode.CODE_PASSWORD_ERROR.CODE_VALUE, "密码输入错误！", new JSONObject());
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 验证密码
     * @param password
     * @return
     */
    @Override
    public ReturnData ckChildPwd(@PathVariable String password) {
        //删除前判断是否有密码
        boolean flag = false;
        ChildModelPwd pwd = null;
        //比对密码
        pwd = homeAlbumService.findById(CommonUtils.getMyId());
        if (pwd != null) {
            String status = pwd.getStatus();
            if (CommonUtils.strToMD5(password + status, 32).equals(pwd.getPassword())) {
                flag = true;
            }
        }
        if (!flag) {
            return returnData(StatusCode.CODE_PASSWORD_ERROR.CODE_VALUE, "密码验证错误!", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询是否设置密码
     * @return
     */
    @Override
    public ReturnData getChildPwd() {
        Map<String, Object> map = new HashMap<>();
        ChildModelPwd album = homeAlbumService.findById(CommonUtils.getMyId());
        if (album == null) {
            map.put("state", 0);//未开启
        } else {
            map.put("state", 1);//已开启
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 新增申诉重置密码
     * @param homeAlbum
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addChildPwdAppeal(@Valid @RequestBody ChildModelPwdAppeal homeAlbum, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        List list = homeAlbumService.findByUserId(CommonUtils.getMyId());
        if (list != null && list.size() >= 3) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "申诉失败！每人每月只可进行三次重置申请", new JSONObject());
        }
        //新增
        homeAlbum.setTime(new Date());
        homeAlbum.setUserId(CommonUtils.getMyId());
        homeAlbumService.add(homeAlbum);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询申诉列表
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findChildAppealList(@PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        long myId = CommonUtils.getMyId();
        if (myId != 10076 && myId != 12770 && myId != 9389 && myId != 9999 && myId != 13005 && myId != 12774 && myId != 13031 && myId != 12769 && myId != 12796 && myId != 10053) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限进行此操作，请联系管理员申请权限!", new JSONObject());
        }
        //开始查询
        PageBean<ChildModelPwdAppeal> pageBean = null;
        pageBean = homeAlbumService.findChildAppealList(page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = pageBean.getList();
        for (int i = 0; i < list.size(); i++) {
            ChildModelPwdAppeal pwdAppeal = (ChildModelPwdAppeal) list.get(i);
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
     * 更新申诉状态
     * @param homeAlbum
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeAppealState(@Valid @RequestBody ChildModelPwdAppeal homeAlbum, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        long myId = CommonUtils.getMyId();
        if (myId != 10076 && myId != 12770 && myId != 9389 && myId != 9999 && myId != 13005 && myId != 12774 && myId != 13031 && myId != 12769 && myId != 12796 && myId != 10053) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限进行此操作，请联系管理员申请权限!", new JSONObject());
        }
        homeAlbumService.changeAppealState(homeAlbum);
        if (homeAlbum.getState() == 2) {
            //重置密码/关闭模式
            homeAlbumService.delPwd(homeAlbum.getUserId());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
