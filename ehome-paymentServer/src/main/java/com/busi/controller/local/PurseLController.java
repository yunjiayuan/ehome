package com.busi.controller.local;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Date;
import java.util.Map;

/**
 * 钱包相关接口(通过fegin本地内部调用)
 * author：SunTianJie
 * create time：2018-8-16 09:46:30
 */
@RestController
public class PurseLController extends BaseController implements PurseLocalController{

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PurseInfoService purseInfoService;

    /***
     * 更新用户账户信息接口(包含新增）
     * @param purse
     * @return
     */
    @Override
    public ReturnData updatePurseInfo(@Valid @RequestBody Purse purse) {
//        //验证参数格式
//        if(bindingResult.hasErrors()){
//            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
//        }
//        //验证修改人权限
//        if(CommonUtils.getMyId()!=purse.getUserId()){
//            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+purse.getUserId()+"]的钱包信息",new JSONObject());
//        }
        //判断缓存中和数据库中是否存在 以判断是新增还是更新
        Map<String,Object> purseMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PURSEINFO+purse.getUserId() );
        if(purseMap==null||purseMap.size()<=0){
            Purse p = purseInfoService.findPurseInfo(purse.getUserId());
            if(p==null){//数据库中不存在
                purse.setTime(new Date());
                purseInfoService.addPurseInfo(purse);
                //使缓存中的钱包信息失效  查询时会重新加载
                redisUtils.expire(Constants.REDIS_KEY_PAYMENT_PURSEINFO+purse.getUserId(),0);
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
            }
        }else{//缓存中存在 判断是否为空对象
            if(Integer.parseInt(purseMap.get("redisStatus").toString())==0){//redisStatus==0 说明数据中无此记录
                purse.setTime(new Date());
                purseInfoService.addPurseInfo(purse);
                //使缓存中的钱包信息失效  查询时会重新加载
                redisUtils.expire(Constants.REDIS_KEY_PAYMENT_PURSEINFO+purse.getUserId(),0);
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
            }
        }
        //缓存中存在 则更新
//        purse.setTime(new Date());
        purse.setHomePoint(Long.parseLong(purseMap.get("homePoint").toString())+purse.getHomePoint());
        purse.setHomeCoin(Long.parseLong(purseMap.get("homeCoin").toString())+purse.getHomeCoin());
        purse.setSpareMoney(Double.parseDouble(purseMap.get("spareMoney").toString())+purse.getSpareMoney());
        purseInfoService.updatePurseInfo(purse);
        //使缓存中的用户钱包信息失效  查询时会重新加载
        redisUtils.expire(Constants.REDIS_KEY_PAYMENT_PURSEINFO+purse.getUserId(),0);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

}
