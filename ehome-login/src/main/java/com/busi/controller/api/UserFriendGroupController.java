package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.entity.UserFriendGroup;
import com.busi.service.UserFriendGroupService;
import com.busi.utils.*;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.List;

/**
 * 好友分组相关接口
 * author：SunTianJie
 * create time：2018/7/17 14:26
 */
@RestController
public class UserFriendGroupController extends BaseController implements UserFriendGroupApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserFriendGroupService userFriendGroupService;

    /***
     * 新增好友分组接口
     * @param userFriendGroup
     * @return
     */
    @Override
    public ReturnData addFriendGroup(@Valid @RequestBody UserFriendGroup userFriendGroup, BindingResult bindingResult) {
        //验证参数格式是否正确
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        userFriendGroupService.add(userFriendGroup);
        //清除缓存中的分组信息
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDGROUP+CommonUtils.getMyId(),0);
        //清除缓存中的分组信息
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDLIST+CommonUtils.getMyId(),0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 删除好友分组
     * @param groupId 将要删除的分组ID
     * @return
     */
    @Override
    public ReturnData delFriendGroup(@PathVariable long groupId) {
        if(groupId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"分组ID参数有误",new JSONObject());
        }
        userFriendGroupService.del(CommonUtils.getMyId(),groupId);
        //清除缓存中的分组信息
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDGROUP+CommonUtils.getMyId(),0);
        //清除缓存中的分组信息
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDLIST+CommonUtils.getMyId(),0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 获取好友分组列表接口
     * @param page
     * @param count
     * @return
     */
    @Override
    public ReturnData findFriendGroupList(@PathVariable int page,@PathVariable int count) {
        //验证参数
        if(page<0||count<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"分页参数有误",new JSONObject());
        }
        //从缓存中获取好友列表
        PageBean<UserFriendGroup> pageBean = null;
        List<Object> list = redisUtils.getList(Constants.REDIS_KEY_USERFRIENDGROUP+CommonUtils.getMyId(),0,-1);
        if(list==null||list.size()<=0){
            //缓存中不存在 查询数据库 并同步到缓存中
            pageBean  = userFriendGroupService.findList(CommonUtils.getMyId(),page, count);
            redisUtils.pushList(Constants.REDIS_KEY_USERFRIENDGROUP+CommonUtils.getMyId(),pageBean.getList(),Constants.USER_TIME_OUT);
        }
        if(pageBean==null){
            Page p = new Page();
            p.setTotal(list.size());
            p.setPageNum(page);
            p.setPages(1);
            p.setPageSize(count);
            p.setPageSize(list.size());
            pageBean=PageUtils.getPageBean(p,list);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,pageBean);
    }

    /***
     * 修改分组名接口
     * @param userFriendGroup
     * @return
     */
    @Override
    public ReturnData updateGroupName(@Valid @RequestBody UserFriendGroup userFriendGroup, BindingResult bindingResult) {
        //验证参数格式是否正确
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        userFriendGroupService.updateGroupName(userFriendGroup);
        //清除缓存中的分组信息
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDGROUP+CommonUtils.getMyId(),0);
        //清除缓存中的分组信息
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDLIST+CommonUtils.getMyId(),0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
