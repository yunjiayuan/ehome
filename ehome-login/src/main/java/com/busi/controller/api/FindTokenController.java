package com.busi.controller.api;

import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取家主页信息接口
 * author：SunTianJie
 * create time：2018/7/16 13:51
 */
@RestController
public class FindTokenController extends BaseController implements FindTokenApiController {

    @Autowired
    RedisUtils redisUtils;

    /***
     * 获取七牛上传token
     * @return
     */
    @Override
    public ReturnData findToken() {
        //获取七牛云存储token
        Map<String,Object> map = new HashMap<>();
        Object obj = redisUtils.getKey(Constants.REDIS_KEY_QINIU_TOKEN);
        String qiuniu_token = "";
        if(obj==null||CommonUtils.checkFull(obj.toString())){//缓存中不存在 重新生成
            qiuniu_token = CommonUtils.getQiniuToken();
            //更新缓存
            redisUtils.set(Constants.REDIS_KEY_QINIU_TOKEN,qiuniu_token,Constants.USER_TIME_OUT);//7天有效期
        }else{
            qiuniu_token = obj.toString();
        }
        map.put("qiniu_token",qiuniu_token);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }
}
