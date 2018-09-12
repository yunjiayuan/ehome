package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Footprint;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.entity.UserInfo;
import com.busi.service.FootprintService;
import com.busi.service.UserInfoService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 脚印接口
 * author：SunTianJie
 * create time：2018/9/12 19:07
 */
@RestController
public class FootprintController extends BaseController implements FootprintApiController {

    @Autowired
    FootprintService footprintService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqUtils mqUtils;

    /***
     * 更新离开时间
     * @param footprint
     * @return
     */
    @Override
    public ReturnData updateAwayTime(@Valid @RequestBody Footprint footprint, BindingResult bindingResult) {
        //验证参数格式是否正确
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //开始更新
        footprint.setAwayTime(new Date());
        footprintService.update(footprint);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 查询脚印记录和在家的人
     * @param userId       当前登录者ID
     * @param findType     历史脚印查询类型 当isOnlineType=1时有效 0查询自己被访问过的脚印记录  1查询自己访问过的脚印记录
     * @param isOnlineType 查询类型  0表示查询当时正在家的人  1表示查询历史脚印记录
     * @param page
     * @param count
     * @return
     */
    @Override
    public ReturnData findFootprintList(@PathVariable long userId,@PathVariable int isOnlineType,@PathVariable int findType,
                                        @PathVariable int page,@PathVariable int count) {
        //验证参数格式
        if(isOnlineType<0||isOnlineType>1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"isOnlineType参数有误",new JSONObject());
        }
        if(findType<0||findType>1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"findType参数有误",new JSONObject());
        }
        if(page<0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"page参数有误",new JSONObject());
        }
        if(count<1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"count参数有误",new JSONObject());
        }
        //开始查询
        PageBean<Footprint> pageBean;
        pageBean = footprintService.findFootList(userId,findType,isOnlineType,page,count);
        if(pageBean==null){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,new JSONArray());
        }
        List list = pageBean.getList();
        if(list!=null&&list.size()>0){
            for (int i=0;i<list.size();i++){
                Footprint footprint = (Footprint) list.get(i);
                if(footprint!=null){
                    long newUserId = 0;
                    if(isOnlineType==0){
                        newUserId = footprint.getMyId();
                    }else{
                        if(findType==0){
                            newUserId = footprint.getMyId();
                        }else{
                            newUserId = footprint.getUserId();
                        }
                    }
                    Map<String, Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER + newUserId);
                    UserInfo userInfo = null;
                    if (userMap == null || userMap.size() <= 0) {
                        //缓存中没有用户对象信息 查询数据库
                        UserInfo u = userInfoService.findUserById(userId);
                        if (u == null) {//数据库也没有
                            return null;
                        }
                        userMap = CommonUtils.objectToMap(u);
                        redisUtils.hmset(Constants.REDIS_KEY_USER + userId, userMap, Constants.USER_TIME_OUT);
                    }
                    userInfo = (UserInfo) CommonUtils.mapToObject(userMap, UserInfo.class);
                    if(userInfo!=null){
                        footprint.setUserName(userInfo.getName());
                        footprint.setUserHead(userInfo.getHead());
                        footprint.setSex(userInfo.getSex());
                        Date d = userInfo.getBirthday();
                        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                        footprint.setAge(CommonUtils.getAge(format.format(d)));
                    }
                }
            }
        }
        //添加任务
        mqUtils.sendTaskMQ(CommonUtils.getMyId(),0,4);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,pageBean);
    }
}
