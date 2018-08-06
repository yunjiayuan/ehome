package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.entity.UserInfo;
import com.busi.service.UserInfoService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 此处编写本类功能说明
 * author：SunTianJie
 * create time：2018/7/11 13:14
 */
@RestController
public class SearchUserController extends BaseController implements SearchUserApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoService userInfoService;

    /**
     * 精确找人接口
     * @param searchType 查找类型 0门牌号查找(默认) 1手机号查找
     * @param param 当searchType=0时，此处为省简称与门牌号组合，格式：0_1003001
     *               当searchType=1时，此处为手机号，格式为：15901213694
     * @return
     */
    @Override
    public ReturnData accurateSearchUser(@PathVariable int searchType, @PathVariable String param) {
        //验证码参数
        if(searchType<0||searchType>1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"searchType参数有误",new JSONObject());
        }
        if(CommonUtils.checkFull(param)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"param不能为空",new JSONObject());
        }
        //开始查找用户
        long userId = 0L;
        Object obj = null;
        if(searchType==1){//手机号查找
            obj = redisUtils.hget(Constants.REDIS_KEY_PHONENUMBER,param);
        }else{
            obj = redisUtils.hget(Constants.REDIS_KEY_HOUSENUMBER,param);
        }
        if(obj==null||CommonUtils.checkFull(String.valueOf(obj.toString()))){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"查找的用户不存在",new JSONObject());
        }
        userId = Long.parseLong(obj.toString());
        Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userId );
        userMap.put("password","");//过滤登录密码
        userMap.put("im_password","");//过滤环信密码
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",userMap);
    }

    /**
     * 条件找人接口
     * @param name     用户名
     * @param beginAge 起始年龄（包含） 默认0
     * @param endAge   结束年龄（包含） 默认0  endAge>beginAge  0时为上限为不限
     * @param sex      性别 0不限 1男2女
     * @param province 省  -1为不限
     * @param city     市  -1为不限
     * @param district 区  -1为不限
     * @param studyrank 学历  0：不限  1:"中专",2:"专科",3:"本科",4:"双学士",5:"硕士",6:"博士",7:"博士后",8:"其他"
     * @param maritalstatus 婚否  0：不限  1:"已婚",2:"未婚",3:"离异",4:"丧偶"
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @Override
    public ReturnData fuzzySearchUser(@PathVariable String name,@PathVariable int beginAge,@PathVariable int endAge,
                                      @PathVariable int sex,@PathVariable int province,@PathVariable int city,
                                      @PathVariable int district,@PathVariable int studyrank,@PathVariable int maritalstatus,
                                      @PathVariable int page,@PathVariable int count) {

        //验证参数
        //验证地区正确性
        if(province!=-1&&city!=-1&&district!=-1&&!CommonUtils.checkProvince_city_district(0,province,city,district)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"国家、省、市、区参数不匹配",new JSONObject());
        }
        if(studyrank<0||studyrank>8){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"学历studyrank参数有误",new JSONObject());
        }
        if(maritalstatus<0||maritalstatus>4){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"婚否maritalstatus参数有误",new JSONObject());
        }
        if(maritalstatus<0||maritalstatus>4){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"婚否maritalstatus参数有误",new JSONObject());
        }
        if(sex<0||sex>2){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"性别sex参数有误",new JSONObject());
        }
        if(beginAge<0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"起始年龄不能小于0",new JSONObject());
        }
        if(endAge<0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"结束年龄不能小于0",new JSONObject());
        }
        //开始查询
        PageBean<UserInfo> pageBean;
        pageBean  = userInfoService.findList(name,beginAge,endAge,sex,province,city, district,studyrank, maritalstatus,page, count);;
        if(pageBean==null){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,pageBean);
    }

    /***
     * 查找附近的人接口
     * @param sex 性别 0不限 1男2女
     * @param lat 纬度 小数点后8位
     * @param lon 经度 小数点后8位
     * @return
     */
    @Override
    public ReturnData nearbySearchUser(@PathVariable int sex,@PathVariable double lat,@PathVariable double lon) {
        //验证参数
        if(sex<0||sex>2){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"性别参数有误",new JSONObject());
        }
        String str = "^(-)?[0-9]{1,3}+(.[0-9]{1,6})?$";//匹配（正负）整数3位，小数6位的正则表达式
        if(lon<0||lat<0||!String.valueOf(lon).matches(str) || !String.valueOf(lat).matches(str)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"位置坐标参数格式有误",new JSONObject());
        }
        GeoResults<GeoLocation<String>> geoResults =  redisUtils.getPosition(Constants.REDIS_KEY_USER_POSITION_LIST,lat,lon, Constants.RADIUS,0,Constants.LIMIT);
        List<Map<String,Object>> list = new ArrayList<>();
        Iterator iter  = geoResults.getContent().iterator();
        while (iter.hasNext())
        {
            GeoResult<GeoLocation<String>> geoResult = (GeoResult<GeoLocation<String>>) iter.next();
            if(geoResult!=null){
                GeoLocation<String> GeoLocation= geoResult.getContent();
                Distance distance = geoResult.getDistance();
                if(GeoLocation==null||distance==null){
                    continue;
                }
                String userId = GeoLocation.getName();
                if(CommonUtils.checkFull(userId)||userId.equals(CommonUtils.getMyId()+"")){
                    continue;
                }
                Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userId );
                if(userMap==null||userMap.size()<=0){
                   continue;
                }
                //判断性别
                if(sex!=0&&(Integer)userMap.get("sex")!=sex){
                    continue;
                }
                String updateTime = (String)redisUtils.hget(Constants.REDIS_KEY_USER_POSITION+userId,"time");
                if(CommonUtils.checkFull(updateTime)){//不存在 则证明该位置信息已过期 清除之前的记录
                    redisUtils.delPosition(Constants.REDIS_KEY_USER_POSITION_LIST,userId);//清除之前的记录 解决hash无法设置过期时间的问题 但是会影响数据的数量 刷新后正常
                    continue;
                }
                userMap.put("password","");//过滤登录密码
                userMap.put("im_password","");//过滤环信密码
                userMap.put("radius",new BigDecimal(distance.getValue()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());//距离保留两位小数 单位m
                userMap.put("positionTime",updateTime);
                list.add(userMap);
            }
        }
        //补数据处理 如果集合数据太少 则补充系统默认用户数据
//        if(list.size()<10){
//            //补90条
//            //后续开发....
//        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,list);
    }
}
