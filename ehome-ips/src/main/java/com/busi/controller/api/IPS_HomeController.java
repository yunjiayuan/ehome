package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @program: home推荐
 * @author: ZHaoJiaJie
 * @create: 2018-08-10 10:01
 */
@RestController
public class IPS_HomeController  extends BaseController implements IPS_HomeApiController {

    @Autowired
    RedisUtils redisUtils;

    /***
     * 分页查询接口
     * @param userId   用户ID
     * @return
     */
    @Override
    public ReturnData findHomeList(@PathVariable long userId) {
        //验证参数
        if (userId < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        //开始查询
        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_IPS_HOMELIST,0,100);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, list);
    }
}
