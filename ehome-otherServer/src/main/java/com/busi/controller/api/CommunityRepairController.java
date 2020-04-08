package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.CommunityRepairService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import com.busi.utils.UserInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @program: ehome
 * @description: 社区报修
 * @author: ZHaoJiaJie
 * @create: 2020-04-08 16:32:58
 */
@RestController
public class CommunityRepairController extends BaseController implements CommunityRepairApiController {

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    CommunityRepairService communityService;

    /***
     * 新增报修
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addRepair(@Valid @RequestBody CommunityRepair homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        communityService.addSetUp(homeHospital);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除报修
     * @return:
     * @param ids
     */
    @Override
    public ReturnData delRepair(@PathVariable String ids) {
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "删除参数ids不能为空", new JSONObject());
        }
        communityService.delSetUp(ids.split(","));
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询报修列表
     * @param type    type=0居委会  type=1物业
     * @param communityId   type=0时居委会ID  type=1时物业ID
     * @param userId   查询者
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findRepairList(@PathVariable int type, @PathVariable long communityId, @PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<CommunityRepair> pageBean = null;
        pageBean = communityService.findSetUpList(type, communityId, userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = pageBean.getList();
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        for (int i = 0; i < list.size(); i++) {
            CommunityRepair sa = (CommunityRepair) list.get(i);
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
}
