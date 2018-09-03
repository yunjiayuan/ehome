package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Look;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.service.LookService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: 浏览记录
 * @author: ZHaoJiaJie
 * @create: 2018-08-24 16:25
 */
@RestController
public class LookController extends BaseController implements LookApiController {

    @Autowired
    LookService lookService;

    /***
     * 删除
     * @param ids  浏览记录ID(，分隔)
     * @param myId
     * @return
     */
    @Override
    public ReturnData delLook(@PathVariable long myId, @PathVariable String ids) {
        //验证参数
        if (myId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "ids参数有误", new JSONObject());
        }
        //验证删除权限
        if (CommonUtils.getMyId() != myId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限删除用户[" + myId + "]的浏览记录", new JSONObject());
        }
        //查询数据库
        int look = lookService.del(ids.split(","), myId);
        if (look <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "公告浏览记录[" + ids + "]不存在", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询接口
     * @param userId  用户ID
     * @param page   页码 第几页 起始值1
     * @param count  每页条数
     * @return
     */
    @Override
    public ReturnData findLook(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (userId < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证查看权限
        if(userId != 0){
            if (CommonUtils.getMyId() != userId) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限浏览用户[" + userId + "]的浏览记录", new JSONObject());
            }
        }
        //开始查询
        PageBean<Look> pageBean;
        pageBean = lookService.findList(userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }
}
