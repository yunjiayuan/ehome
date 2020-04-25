package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.CollectService;
import com.busi.service.RentAhouseService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 租房买房相关接口
 * author ZhaoJiaJie
 * Create time 2020-04-20 21:11:53
 */
@RestController
public class RentAhouseController extends BaseController implements RentAhouseApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    CollectService collectService;

    @Autowired
    RentAhouseService communityService;

    /***
     * 新增房源
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addRentAhouse(@Valid @RequestBody RentAhouse homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        homeHospital.setAddTime(new Date());
        homeHospital.setRefreshTime(new Date());
        communityService.addCommunity(homeHospital);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", homeHospital.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新房源
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeRentAhouse(@Valid @RequestBody RentAhouse homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        homeHospital.setRefreshTime(new Date());
        communityService.changeCommunity(homeHospital);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_RENTAHOUSE + homeHospital.getId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除房源
     * @param ids 房源ID
     * @return:
     */
    @Override
    public ReturnData delRentAhouse(@PathVariable String ids) {
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "删除参数ids不能为空", new JSONObject());
        }
        communityService.delResident(ids.split(","), CommonUtils.getMyId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询房源详情
     * @param id 房源ID
     * @return
     */
    @Override
    public ReturnData findRentAhouse(@PathVariable long id) {
        //查询缓存 缓存中不存在 查询数据库
        int roomState = 0;   //房屋状态：0出售 1出租
        RentAhouse sa = null;
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_RENTAHOUSE + id);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            sa = communityService.findRentAhouse(id);
            if (sa == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询房源不存在!", new JSONObject());
            }
            if (sa.getRoomState() == 0) {
                roomState = 9;
            }
            if (sa.getRoomState() == 1) {
                roomState = 10;
            }
            //返回收藏状态
            int collection = 0;
            Collect collect1 = null;
            collect1 = collectService.findUserId(id, CommonUtils.getMyId(), roomState);
            if (collect1 != null) {
                collection = 1;
            }
            //放入缓存
            kitchenMap = CommonUtils.objectToMap(sa);
            kitchenMap.put("collection", collection);
            redisUtils.hmset(Constants.REDIS_KEY_RENTAHOUSE + id, kitchenMap, Constants.USER_TIME_OUT);
        }
        int type = (int) kitchenMap.get("roomState");
        if (type == 0) {
            roomState = 9;
        }
        if (type == 1) {
            roomState = 10;
        }
        //新增浏览记录
        mqUtils.sendLookMQ(CommonUtils.getMyId(), id, (String) kitchenMap.get("title"), roomState);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
    }

    /***
     * 条件查询房源
     * @param userId    用户ID
     * @param sellState  -1不限 roomState=0时：0出售中  1已售出  roomState=1时：0出租中  1已出租
     * @param roomState  0出售  1出租
     * @param sort  排序条件:-1不限 0最新发布，1价格最低，2价格最高
     * @param nearby  附近 -1不限  0附近
     * @param residence     房型：-1不限 0一室 1二室 2三室 3四室 4五室 5五室以上
     * @param roomType     房屋类型 roomState=0时：-1不限 0新房 1二手房   roomState=1时：-1不限 0合租 1整租
     * @param lon     经度  nearby=0时有效
     * @param lat     纬度  nearby=0时有效
     * @param province     省
     * @param city      市
     * @param district    区
     * @param minPrice  最小价格
     * @param maxPrice  最大价格
     * @param minArea  最小面积
     * @param maxArea  最大面积
     * @param orientation  朝向：-1不限 0南北、1东北、2东南、3西南、4西北、5东西、6南、7东、8西、9北
     * @param renovation   房屋装修：-1不限 0精装 1普装 2毛坯
     * @param floor   房屋楼层：-1不限 0底层 1低楼层 2中楼层 3高楼层 4顶层
     * @param bedroomType   卧室类型：-1不限 0主卧 1次卧 2其他
     * @param houseType  房源类型: -1不限 0业主直租 1中介
     * @param paymentMethod  支付方式: -1不限  0押一付一 1押一付三 2季付 3半年付 4年付
     * @param lookHomeTime  看房时间 ： -1不限 0随时看房 1 周末看房  2下班后看房  3电话预约
     * @param string    模糊搜索
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findRentAhouseList(@PathVariable long userId, @PathVariable int sellState, @PathVariable int roomState, @PathVariable int sort, @PathVariable int nearby, @PathVariable int residence, @PathVariable int roomType, @PathVariable double lon, @PathVariable double lat, @PathVariable int province, @PathVariable int city,
                                         @PathVariable int district, @PathVariable int minPrice, @PathVariable int maxPrice, @PathVariable int minArea, @PathVariable int maxArea, @PathVariable int orientation, @PathVariable int renovation, @PathVariable int floor, @PathVariable int bedroomType,
                                         @PathVariable int houseType, @PathVariable int paymentMethod, @PathVariable int lookHomeTime, @PathVariable String string, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<RentAhouse> pageBean = null;
        if (residence > -1) {
            residence = residence + 1;
        }
        pageBean = communityService.findRentAhouseList(userId, sellState, roomState, sort, nearby, residence, roomType, lon,
                lat, province, city, district, minPrice, maxPrice, minArea, maxArea, orientation,
                renovation, floor, bedroomType, houseType, paymentMethod, lookHomeTime, string, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
