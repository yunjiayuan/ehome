package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.CommunityService;
import com.busi.service.PropertyService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * 物业相关接口
 * author ZJJ
 * Create time 2020-04-07 16:46:08
 */
@RestController
public class PropertyController extends BaseController implements PropertyApiController {
    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    PropertyService communityService;

    @Autowired
    CommunityService findCommunity;

    /***
     * 查询是否已加入物业
     * @param userId
     * @return
     */
    @Override
    public ReturnData findJoinProperty(@PathVariable long userId) {
        List list = null;
        PropertyResident resident = null;
        list = communityService.findJoin(userId);
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        resident = (PropertyResident) list.get(0);
        if (resident == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_PROPERTY + resident.getPropertyId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Property sa = communityService.findProperty(resident.getPropertyId());
            if (sa == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询物业不存在!", new JSONObject());
            }
            //放入缓存
            kitchenMap = CommonUtils.objectToMap(sa);
            redisUtils.hmset(Constants.REDIS_KEY_PROPERTY + resident.getPropertyId(), kitchenMap, Constants.USER_TIME_OUT);
        }
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        Property property = (Property) CommonUtils.mapToObject(kitchenMap, Property.class);
        if (property != null) {
            resident.setCommunityId(property.getCommunityId());//返回所属居委会ID
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", resident);
    }

    /***
     * 新增物业
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addProperty(@Valid @RequestBody Property homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        homeHospital.setTime(new Date());
        communityService.addProperty(homeHospital);

        //新增居民
        PropertyResident resident = new PropertyResident();
        resident.setTime(new Date());
        resident.setRefreshTime(new Date());
        resident.setPropertyId(homeHospital.getId());
        resident.setUserId(homeHospital.getUserId());
        resident.setIdentity(2);
        communityService.addResident(resident);

        //新增默认物业标签
//        String[] string = {/*"普通居民",*/ "小区长", "楼栋长", "单元长/联户长", "消防员", "社区民警", "建档立卡贫困户", "低保户", "特困户", "五保户", "兜底户", "残疾户"};
//        for (int i = 0; i < string.length; i++) {
//            PropertyResidentTag tag = new PropertyResidentTag();
//            tag.setTagName(string[i]);
//            tag.setPropertyId(homeHospital.getId());
//            residentTagService.add(tag);
//        }
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", homeHospital.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新物业
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeProperty(@Valid @RequestBody Property homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        communityService.changeProperty(homeHospital);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_PROPERTY + homeHospital.getId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 设置所属居委会
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData subordinateProperty(@Valid @RequestBody Property homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        communityService.subordinateProperty(homeHospital);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_PROPERTY + homeHospital.getId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询所属居委会
     * @param id
     * @return
     */
    @Override
    public ReturnData findSubordinate(@PathVariable long id) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_PROPERTY + id);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Property sa = communityService.findProperty(id);
            if (sa == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询物业不存在!", new JSONObject());
            }
            //放入缓存
            kitchenMap = CommonUtils.objectToMap(sa);
            redisUtils.hmset(Constants.REDIS_KEY_PROPERTY + id, kitchenMap, Constants.USER_TIME_OUT);
        }
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        Property property = (Property) CommonUtils.mapToObject(kitchenMap, Property.class);
        if (property == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_COMMUNITY + property.getCommunityId());
        if (map == null || map.size() <= 0) {
            Community sa = findCommunity.findCommunity(property.getCommunityId());
            if (sa == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            //放入缓存
            map = CommonUtils.objectToMap(sa);
            redisUtils.hmset(Constants.REDIS_KEY_COMMUNITY + property.getCommunityId(), map, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新物业刷新时间
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changePropertyTime(@Valid @RequestBody PropertyResident homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        homeHospital.setRefreshTime(new Date());
        communityService.changeCommunityTime(homeHospital);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询物业详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findProperty(@PathVariable long id) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_PROPERTY + id);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Property sa = communityService.findProperty(id);
            if (sa == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询物业不存在!", new JSONObject());
            }
            //放入缓存
            kitchenMap = CommonUtils.objectToMap(sa);
            redisUtils.hmset(Constants.REDIS_KEY_PROPERTY + id, kitchenMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
    }

    /***
     * 查询物业列表
     * @param userId    用户ID(默认0，大于0时查询此用户加入的所有物业)
     * @param lon     经度
     * @param lat  纬度
     * @param string    模糊搜索 (物业名字)
     * @param province     省
     * @param city      市
     * @param district    区
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findPropertyList(@PathVariable long userId, @PathVariable double lon, @PathVariable double lat, @PathVariable String string, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        List list = null;
        String ids = "";
        if (userId > 0) {
            list = communityService.findJoin(userId);
            if (list == null || list.size() <= 0) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
            }
            for (int i = 0; i < list.size(); i++) {
                PropertyResident resident = (PropertyResident) list.get(i);
                if (resident != null) {
                    if (i == 0) {
                        ids = resident.getPropertyId() + "";//物业ID
                    } else {
                        ids += "," + resident.getPropertyId();
                    }
                }
            }
            List list2 = null;
            if (!CommonUtils.checkFull(ids)) {
                list2 = communityService.findPropertyList2(ids);
                if (list2 == null || list2.size() <= 0) {
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
                }
                for (int i = 0; i < list2.size(); i++) {
                    Property community = (Property) list2.get(i);
                    for (int j = 0; j < list.size(); j++) {
                        PropertyResident resident = (PropertyResident) list.get(j);
                        if (community.getId() == resident.getPropertyId()) {
                            community.setIdentity(resident.getIdentity());
                            community.setTags(resident.getTags());
                            list.remove(j);
                        }
                    }
                }
            }
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list2);
        }
        PageBean<Property> pageBean = null;
        pageBean = communityService.findPropertyList(lon, lat, string, province, city, district, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        list = pageBean.getList();
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        for (int i = 0; i < list.size(); i++) {
            Property ik = (Property) list.get(i);
            if (CommonUtils.checkFull(string)) {
                double userlon = ik.getLon();
                double userlat = ik.getLat();

                int distance = (int) Math.round(CommonUtils.getShortestDistance(userlon, userlat, lon, lat));

                ik.setDistance(distance);//距离/m
            }
            if (i == 0) {
                ids = ik.getId() + "";//物业ID
            } else {
                ids += "," + ik.getId();
            }
            ik.setIdentity(-1);
        }
        List list2 = null;
        if (!CommonUtils.checkFull(ids)) {
            list2 = communityService.findIsList2(ids, CommonUtils.getMyId());//查询是否有我加入的物业
            if (list2 != null && list2.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    Property community = (Property) list.get(i);
                    for (int j = 0; j < list2.size(); j++) {
                        PropertyResident resident = (PropertyResident) list2.get(j);
                        if (resident.getUserId() == CommonUtils.getMyId() && community.getId() == resident.getPropertyId()) {
                            community.setIdentity(resident.getIdentity());
                            community.setTags(resident.getTags());
                            list2.remove(j);
                        }
                    }
                }
            }
        }
        if (CommonUtils.checkFull(string)) {
            Collections.sort(list, new Comparator<Property>() {
                /*
                 * int compare(Person o1, Person o2) 返回一个基本类型的整型，
                 * 返回负数表示：o1 小于o2，
                 * 返回0 表示：o1和p2相等，
                 * 返回正数表示：o1大于o2
                 */
                @Override
                public int compare(Property o1, Property o2) {
                    // 按照距离进行正序排列
                    if (o1.getDistance() > o2.getDistance()) {
                        return 1;
                    }
                    if (o1.getDistance() == o2.getDistance()) {
                        return 0;
                    }
                    return -1;
                }
            });
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 新增居民
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addPResident(@Valid @RequestBody PropertyResident homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        PropertyResident resident1 = null;
        if (homeHospital.getType() == 1) { //判断邀请者权限
            resident1 = communityService.findResident(homeHospital.getPropertyId(), homeHospital.getMasterId());
            if (resident1 == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "没有权限", new JSONArray());
            }
            //判断是否已经加入了
            PropertyResident sa = communityService.findResident(homeHospital.getPropertyId(), homeHospital.getUserId());
            if (sa != null) {
                //更新居民标签
                if (!CommonUtils.checkFull(homeHospital.getTags())) {
                    if (resident1.getIdentity() < 1) {
                        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "没有权限", new JSONArray());
                    }
                    sa.setTags(homeHospital.getTags());
                    communityService.changeResidentTag(sa);
                }
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
            }
        }
        homeHospital.setTime(new Date());
        homeHospital.setRefreshTime(new Date());
        communityService.addResident(homeHospital);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
    }

    /***
     * 更新居民权限
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changePResident(@Valid @RequestBody PropertyResident homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断设置者权限
        PropertyResident sa = communityService.findResident(homeHospital.getCommunityId(), CommonUtils.getMyId());
        if (sa == null || sa.getIdentity() < 1) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "没有权限", new JSONArray());
        }
        //判断被设置者是否是本居民
        PropertyResident resident = communityService.findResident(homeHospital.getCommunityId(), homeHospital.getUserId());
        if (resident == null) {
            PropertyResident resident1 = new PropertyResident();
            resident1.setType(1);
            resident1.setTime(new Date());
            resident1.setRefreshTime(new Date());
            resident1.setMasterId(CommonUtils.getMyId());
            resident1.setUserId(homeHospital.getUserId());
            resident1.setIdentity(homeHospital.getIdentity());
            resident1.setCommunityId(homeHospital.getCommunityId());
            communityService.addResident(resident1);
        }
        if (homeHospital.getIdentity() == 1 && resident != null) {
            communityService.changeResident(resident);
        }
        if (homeHospital.getIdentity() == 2 && sa.getIdentity() == 2) {
            if (resident != null) {
                resident.setIdentity(2);
                communityService.changeResident(resident);
            }
            sa.setIdentity(1);
            communityService.changeResident(sa);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除居民
     * @param type 0删除居民  1删除管理员
     * @param ids
     * @param propertyId
     * @return:
     */
    @Override
    public ReturnData delPResident(@PathVariable int type, @PathVariable String ids, @PathVariable long propertyId) {
        Property sa = communityService.findProperty(propertyId);
        if (sa == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询物业不存在!", new JSONObject());
        }
        List resident = communityService.findIsList3(ids);
        if (resident == null || resident.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        for (int i = 0; i < resident.size(); i++) {
            PropertyResident communityResident = (PropertyResident) resident.get(i);
            if (communityResident.getUserId() == CommonUtils.getMyId()) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
        }
        if (type == 1) {
            if (sa.getUserId() != CommonUtils.getMyId()) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "没有权限!", new JSONObject());
            }
        }
        communityService.delResident(type, ids.split(","));
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询居民详情
     * @param propertyId
     * @param homeNumber
     * @return
     */
    @Override
    public ReturnData findPResiden(@PathVariable long propertyId, @PathVariable String homeNumber) {
        PropertyResident sa = communityService.findResident(propertyId, CommonUtils.getMyId());
        if (sa == null || sa.getIdentity() < 1) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "没有权限", new JSONArray());
        }
        UserInfo userInfo = null;
        userInfo = userInfoUtils.getUserInfo(homeNumber);
        if (userInfo == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        PropertyResident resident = communityService.findResident(propertyId, userInfo.getUserId());
        if (resident == null) {
            resident = new PropertyResident();
            resident.setUserId(userInfo.getUserId());
        }
        resident.setName(userInfo.getName());
        resident.setHead(userInfo.getHead());
        resident.setProTypeId(userInfo.getProType());
        resident.setHouseNumber(userInfo.getHouseNumber());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", resident);
    }

    /***
     * 查询居民列表
     * @param type    0所有人  1管理员
     * @param propertyId    物业ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findPResidentList(@PathVariable int type, @PathVariable long propertyId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<PropertyResident> pageBean = null;
        pageBean = communityService.findResidentList(type, propertyId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = pageBean.getList();
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        for (int i = 0; i < list.size(); i++) {
            PropertyResident sa = (PropertyResident) list.get(i);
            if (sa != null) {
                UserInfo userInfo = null;
                userInfo = userInfoUtils.getUserInfo(sa.getUserId());
                if (userInfo != null) {
                    sa.setName(userInfo.getName());
                    sa.setHead(userInfo.getHead());
                    sa.setProTypeId(userInfo.getProType());
                    sa.setHouseNumber(userInfo.getHouseNumber());
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 新增物业人员设置
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addPropertySetUp(@Valid @RequestBody PropertySetUp homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        communityService.addSetUp(homeHospital);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新物业人员设置
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changePropertySetUp(@Valid @RequestBody PropertySetUp homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        communityService.changeSetUp(homeHospital);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @param ids
     * @Description: 删除物业人员设置
     * @return:
     */
    @Override
    public ReturnData delPropertySetUp(@PathVariable String ids) {
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "删除参数ids不能为空", new JSONObject());
        }
        communityService.delSetUp(ids.split(","));
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询物业人员设置列表（按职务正序）
     * @param propertyId    物业ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findPropertySetUpList(@PathVariable long propertyId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<PropertySetUp> pageBean = null;
        pageBean = communityService.findSetUpList(propertyId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

}
