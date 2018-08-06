package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.entity.LoveAndFriends;
import com.busi.service.LoveAndFriendsService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Date;
import java.util.Map;

/**
 * @program: 婚恋交友
 * @author: ZHaoJiaJie
 * @create: 2018-08-02 13:39
 */
@RestController
public class LoveAndFriendsController extends BaseController implements LoveAndFriendsApiController{

    @Autowired
    LoveAndFriendsService loveAndFriendsService;

    @Autowired
    RedisUtils redisUtils;

    /***
     * 新增
     * @param loveAndFriends
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addLove(@Valid @RequestBody LoveAndFriends loveAndFriends, BindingResult bindingResult) {
        //验证参数格式是否正确
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        loveAndFriends.setRefreshTime(new Date());
        loveAndFriends.setReleaseTime(new Date());
        loveAndFriendsService.add(loveAndFriends);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 删除
     * @param userId 将要删除的userId
     * @return
     */
    @Override
    public ReturnData delLove(@PathVariable long userId) {
        //验证参数
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"将要删除的用户ID userId",new JSONObject());
        }
        long myId = CommonUtils.getMyId();
        if(myId != userId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"将要删除的用户ID userId有误，只能自己删除自己的",new JSONObject());
        }
        loveAndFriendsService.del(userId);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDGROUP+CommonUtils.getMyId(),1);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 更新
     * @param loveAndFriends
     * @return
     */
    @Override
    public ReturnData updateLove(@Valid @RequestBody LoveAndFriends loveAndFriends, BindingResult bindingResult) {
        //验证参数格式是否正确
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        loveAndFriends.setRefreshTime(new Date());
        loveAndFriendsService.update(loveAndFriends);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDGROUP+CommonUtils.getMyId(),1);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /**
     * 查询
     * @param userId
     * @return
     */
    @Override
    public ReturnData getLove(@PathVariable  long userId) {
        //验证参数
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"将要删除的用户ID userId",new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        Map<String,Object> testMap =  redisUtils.hmget("test_"+userId);
        if(testMap==null||testMap.size()<=0){
            LoveAndFriends loveAndFriends = loveAndFriendsService.findUserById(userId);
            if(loveAndFriends==null){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
            }
            //放入缓存
            redisUtils.hmset("loveAndFriends"+userId,CommonUtils.objectToMap(loveAndFriends),Constants.USER_TIME_OUT);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",loveAndFriends);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",testMap);
    }

    /***
     * 条件查询接口
     * @param sex  性别:0不限，1男，2女
     * @param income  收入:0不限，1（<3000），2（3000-5000），3（5000-7000），4（7000-9000），5（9000-12000），6（12000-15000），7（15000-20000），8（>20000）
     * @param page   页码 第几页 起始值1
     * @param count  每页条数
     * @return
     */
    @Override
    public ReturnData findListLove(@PathVariable int sex,@PathVariable int income,@PathVariable int page,@PathVariable int count) {
        //验证参数
        if(page<0||count<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"分页参数有误",new JSONObject());
        }
        //开始查询
        PageBean<LoveAndFriends> pageBean;
        pageBean  = loveAndFriendsService.findList(sex,income,page, count);;
        if(pageBean==null){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,pageBean);
    }

}
