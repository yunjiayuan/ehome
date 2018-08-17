package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Purse;
import com.busi.entity.ReturnData;
import com.busi.service.PurseInfoService;
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
import java.util.Map;

/**
 * 钱包相关接口
 * author：SunTianJie
 * create time：2018-8-16 09:46:30
 */
@RestController
public class PurseController extends BaseController implements PurseApiController{

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PurseInfoService purseInfoService;

    /***
     * 查询用户钱包信息
     * @param userId 将要查询的用户ID
     * @return
     */
    @Override
    public ReturnData findPurseInfo(@PathVariable long userId) {
        //验证参数
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
        }
        //验证身份
        if(CommonUtils.getMyId()!=userId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误,无权限进行此操作",new JSONObject());
        }
        Map<String,Object> purseMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PURSEINFO+userId );
        if(purseMap==null||purseMap.size()<=0){
            Purse purse = null;
            //缓存中没有用户对象信息 查询数据库
            purse = purseInfoService.findPurseInfo(userId);
            if(purse==null){
                purse = new Purse();
                purse.setUserId(userId);
            }else{
                purse.setRedisStatus(1);//数据库中已有对应记录
            }
            //更新缓存
            purseMap = CommonUtils.objectToMap(purse);
            redisUtils.hmset(Constants.REDIS_KEY_PAYMENT_PURSEINFO+userId,purseMap,Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",purseMap);
    }
}
