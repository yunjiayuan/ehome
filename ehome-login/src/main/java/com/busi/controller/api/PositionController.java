package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.PositionInfo;
import com.busi.entity.ReturnData;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用户位置信息接口
 * author：SunTianJie
 * create time：2018/7/12 16:40
 */
@RestController
public class PositionController extends BaseController implements PositionApiController {

    @Autowired
    RedisUtils redisUtils;

    /***
     * 更新位置信息
     * @param positionInfo
     * @return
     */
    @Override
    public ReturnData updatePosition(@Valid @RequestBody PositionInfo positionInfo, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //更新时间
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        positionInfo.setTime(dateFormat.format(date));
        //用于 附近的人 使用
        redisUtils.addPosition(Constants.REDIS_KEY_USER_POSITION_LIST,positionInfo.getUserId()+"",positionInfo.getLat(),positionInfo.getLon());
        //单独记录位置信息 并设置过期时间 查询根据该对象是否存在来确定上面的位置信息是否过期
        redisUtils.hmset(Constants.REDIS_KEY_USER_POSITION+positionInfo.getUserId(),CommonUtils.objectToMap(positionInfo),Constants.USER_TIME_OUT);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,new JSONObject());
    }

    /***
     * 删除位置信息
     * @return
     */
    @Override
    public ReturnData delPosition() {
        long myId = CommonUtils.getMyId();
        redisUtils.delPosition(Constants.REDIS_KEY_USER_POSITION_LIST,myId+"");
        redisUtils.expire(Constants.REDIS_KEY_USER_POSITION+myId,0);//0s后过期
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,new JSONObject());
    }
}
