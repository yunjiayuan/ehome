package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Collect;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.service.CollectService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: 收藏
 * @author: ZHaoJiaJie
 * @create: 2018-08-27 10:31
 */

@RestController
public class CollectController extends BaseController implements CollectApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    CollectService collectService;

    /***
     * 新增
     * @param collect
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addCollect(@Valid @RequestBody Collect collect, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        collect.setTime(new Date());
        collectService.add(collect);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_COLLECT + collect.getMyId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除
     * @param myId 用户ID
     * @param ids 将要删除的收藏ID
     * @return
     */
    @Override
    public ReturnData delCollect(@PathVariable long myId, @PathVariable String ids) {
        //验证参数
        if (myId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "myId参数有误", new JSONObject());
        }
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "ids参数有误", new JSONObject());
        }
        //验证删除权限
        if (CommonUtils.getMyId() != myId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限删除用户[" + myId + "]的收藏记录", new JSONObject());
        }
        //查询数据库
        int look = collectService.del(ids.split(","), myId);
        if (look <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "公告收藏记录[" + ids + "]不存在", new JSONObject());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_OTHERPOSTS + myId, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * 查询
     *
     * @param infoId
     * @param afficheType
     * @return
     */
    @Override
    public ReturnData getCollect(@PathVariable long infoId, @PathVariable int afficheType) {
        //验证参数
        if (infoId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        // 统计公告收藏数量
        int collect = 0;
        collect = collectService.findUserById(infoId, afficheType);

        Map<String, Integer> numMap = new HashMap<>();
        numMap.put("count", collect);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", numMap);
    }

    /***
     * 分页查询接口
     * @param userId  用户ID
     * @param page   页码 第几页 起始值1
     * @param count  每页条数
     * @return
     */
    @Override
    public ReturnData findCollect(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (userId < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        //开始查询
        PageBean<Collect> pageBean;
        pageBean = collectService.findList(userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }
}
