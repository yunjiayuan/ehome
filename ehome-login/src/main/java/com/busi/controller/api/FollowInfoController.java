package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.FollowInfoService;
import com.busi.service.UserInfoService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 关注相关接口（全局关注接口 不仅仅适用于生活圈）
 * author：SunTianJie
 * create time：2018/10/23 10:35
 */
@RestController
public class FollowInfoController extends BaseController implements FollowInfoApiController {

    @Autowired
    private FollowInfoService followInfoService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserInfoService userInfoService;

    /***
     * 加关注
     * @param followInfo
     * @return
     */
    @Override
    public ReturnData addFollow(@Valid @RequestBody FollowInfo followInfo, BindingResult bindingResult) {
        //验证参数
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证发布人权限
        if(CommonUtils.getMyId()!=followInfo.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限以用户["+followInfo.getUserId()+"]的身份关注别人",new JSONObject());
        }
        if(followInfo.getUserId()==followInfo.getFollowUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，自己不能关注自己",new JSONObject());
        }
        //验证是否达到上限2000
        int count = 0;
        Object follow = redisUtils.getKey(Constants.REDIS_KEY_FOLLOW_LIST+followInfo.getUserId());
        if(follow!=null&&!CommonUtils.checkFull(follow.toString())){
            String[] array = follow.toString().split(",");
            if(array!=null){
                count = array.length;
            }
        }else{
            count = followInfoService.findFollowCounts(followInfo.getUserId());
        }
        if(count>=Constants.FOLLOW_COUNT){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"您已达到加关注的上限，建议取消无关的关注人，再进行新关注",new JSONObject());
        }
        followInfo.setTime(new Date());
        followInfoService.addFollow(followInfo);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_FOLLOW_LIST+followInfo.getUserId(),0);
        redisUtils.expire(Constants.REDIS_KEY_FOLLOW_LIST+followInfo.getFollowUserId(),0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 取消关注
     * @param userId       生活圈发布者用户ID
     * @param followUserId 将要被删除的生活圈
     * @return
     */
    @Override
    public ReturnData delFollow(@PathVariable long userId,@PathVariable long followUserId) {
        //验证参数
        if(userId<0||followUserId<0||userId==followUserId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误",new JSONObject());
        }
        followInfoService.delFollow(userId,followUserId);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_FOLLOW_LIST+userId,0);
        redisUtils.expire(Constants.REDIS_KEY_FOLLOW_LIST+followUserId,0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 查询关注和粉丝列表
     * @param userId     将要查询的用户ID
     * @param searchType 0 表示查询我关注的人列表  1表示关注我的用户列表
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findFollowList(@PathVariable long userId,@PathVariable int searchType,
                                   @PathVariable  int page,@PathVariable  int count) {
        //验证参数
        if(searchType<0||searchType>1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"searchType参数有误",new JSONObject());
        }
        if(page<1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"page参数有误",new JSONObject());
        }
        if(count<0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"count参数有误",new JSONObject());
        }
        PageBean<FollowInfo> pageBean = null;
        pageBean = followInfoService.findFollowList(userId,searchType,page,count);
        if(pageBean==null){
            pageBean = new PageBean();
            pageBean.setList(new ArrayList<>());
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",pageBean);
        }
        List<FollowInfo> list  = pageBean.getList();
        for(int i=0;i<list.size();i++){
            FollowInfo followInfo = list.get(i);
            if(followInfo==null){
                continue;
            }
            long newUserId = 0;
            if(searchType==0){
                newUserId = followInfo.getFollowUserId();
            }else{
                newUserId = followInfo.getUserId();
            }
            //设置用户信息
            Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + newUserId);
            UserInfo userInfo = null;
            if (userMap == null || userMap.size() <= 0) {
                userInfo = userInfoService.findUserById(newUserId);
            }else{
                userInfo = (UserInfo) CommonUtils.mapToObject(userMap,UserInfo.class);
            }
            if(userInfo!=null){
                followInfo.setName(userInfo.getName());
                followInfo.setHead(userInfo.getHead());
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
