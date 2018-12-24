package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.UsedDealService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * @program: ehome
 * @description: 二手相关接口实现
 * @author: ZHaoJiaJie
 * @create: 2018-09-18 16:37
 */
@RestController
public class UsedDealController extends BaseController implements UsedDealApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqUtils mqUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    UsedDealService usedDealService;

    /***
     * 新增二手公告
     * @param usedDeal
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addJunk(@Valid @RequestBody UsedDeal usedDeal, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //处理特殊字符
        String title = usedDeal.getTitle();
        String content = usedDeal.getContent();
        if (!CommonUtils.checkFull(content) || !CommonUtils.checkFull(title)) {
            String filteringTitle = CommonUtils.filteringContent(title);
            String filteringContent = CommonUtils.filteringContent(content);
            if (CommonUtils.checkFull(filteringTitle) || CommonUtils.checkFull(filteringContent)) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "内容不能为空并且不能包含非法字符！", new JSONArray());
            }
            usedDeal.setContent(filteringTitle);
            usedDeal.setContent(filteringContent);
        }
        //验证地区
        if (!CommonUtils.checkProvince_city_district(0, usedDeal.getProvince(), usedDeal.getCity(), usedDeal.getDistrict())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "省、市、区参数不匹配", new JSONObject());
        }
        //计算公告分数
        int num3 = 0;//图片
        int fraction = 0;//公告分数
        int num = CommonUtils.getStringLengsByByte(usedDeal.getTitle());//标题
        int num2 = CommonUtils.getStringLengsByByte(usedDeal.getContent());//内容
        if (!CommonUtils.checkFull(usedDeal.getImgUrl())) {
            String[] imgArray = usedDeal.getImgUrl().split(",");
            if (imgArray != null) {
                num3 = imgArray.length;

                if (num3 == 1) {
                    fraction += 15;
                }
                if (num3 >= 6) {
                    fraction += 40;
                }
                if (num3 > 3 && num3 < 6) {
                    fraction += 30;
                }
            }
        }
        if (num <= 5 * 2) {
            fraction += 5;
        }
        if (num > 5 * 2 && num <= 10 * 2) {
            fraction += 10;
        }
        if (num > 10 * 2 && num <= 20 * 2) {
            fraction += 20;
        }
        if (num > 20 * 2) {
            fraction += 30;
        }

        if (num2 <= 20 * 2) {
            fraction += 5;
        }
        if (num2 > 20 * 2 && num2 <= 50 * 2) {
            fraction += 10;
        }
        if (num2 > 50 * 2 && num2 <= 80 * 2) {
            fraction += 20;
        }
        if (num2 > 80 * 2) {
            fraction += 30;
        }
        usedDeal.setAuditType(2);
        usedDeal.setDeleteType(1);
        usedDeal.setSellType(1);
        usedDeal.setFraction(fraction);
        usedDeal.setReleaseTime(new Date());
        usedDeal.setRefreshTime(new Date());
        if (usedDeal.getNegotiable() != 1) {
            usedDeal.setSellingPrice(0);
        }
        if (usedDeal.getPinkageType() == 1) {
            if (!CommonUtils.checkFull(usedDeal.getExpressMode())) {
                String[] dmbArrey = usedDeal.getExpressMode().split(";");
                if (dmbArrey != null && dmbArrey.length < 6) {
                    usedDeal.setExpressMode(usedDeal.getExpressMode());
                }
            }
        }
        usedDealService.add(usedDeal);
        //新增home
        if (usedDeal.getFraction() > 70) {
            IPS_Home ipsHome = new IPS_Home();
            ipsHome.setInfoId(usedDeal.getId());
            ipsHome.setTitle(usedDeal.getTitle());
            ipsHome.setUserId(usedDeal.getUserId());
            ipsHome.setContent(usedDeal.getContent());
            ipsHome.setMediumImgUrl(usedDeal.getImgUrl());
            ipsHome.setReleaseTime(usedDeal.getReleaseTime());
            ipsHome.setRefreshTime(usedDeal.getRefreshTime());
            ipsHome.setAuditType(2);
            ipsHome.setDeleteType(1);
            ipsHome.setAfficheType(2);
            ipsHome.setFraction(fraction);

            //放入缓存
            redisUtils.addListLeft(Constants.REDIS_KEY_IPS_HOMELIST, ipsHome, 0);

            List list = null;
            list = redisUtils.getList(Constants.REDIS_KEY_IPS_HOMELIST, 0, 101);
            if (list.size() == 101) {
                //清除缓存中的信息
                redisUtils.expire(Constants.REDIS_KEY_IPS_HOMELIST, 0);
                redisUtils.pushList(Constants.REDIS_KEY_IPS_HOMELIST, list, 0);
            }
        }
        //新增任务
        mqUtils.sendTaskMQ(usedDeal.getUserId(), 1, 3);
        //新增足迹
        mqUtils.sendFootmarkMQ(usedDeal.getUserId(), usedDeal.getTitle(), usedDeal.getImgUrl(), null, null, usedDeal.getId() + "," + 2, 1);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", usedDeal.getId());
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_USEDDEAL + usedDeal.getId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * @Description: 删除
     * @return:
     */
    @Override
    public ReturnData delJunk(@PathVariable long id, @PathVariable long userId) {
        //验证参数
        if (userId <= 0 || id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证删除权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限删除用户[" + userId + "]的公告信息", new JSONObject());
        }
        // 查询数据库
        UsedDeal posts = usedDealService.findUserById(id);
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //更新home
        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_IPS_HOMELIST, 0, 101);
        for (int i = 0; i < list.size(); i++) {
            IPS_Home home = (IPS_Home) list.get(i);
            if (home.getAfficheType() == 2 && home.getInfoId() == posts.getId()) {
                redisUtils.removeList(Constants.REDIS_KEY_IPS_HOMELIST, 1, home);
            }
        }
        posts.setDeleteType(2);
        usedDealService.updateDel(posts);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_USEDDEAL + id, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新二手公告
     * @Param: usedDeal
     * @return:
     */
    @Override
    public ReturnData updateJunk(@Valid @RequestBody UsedDeal usedDeal, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //处理特殊字符
        String title = usedDeal.getTitle();
        String content = usedDeal.getContent();
        if (!CommonUtils.checkFull(content) || !CommonUtils.checkFull(title)) {
            String filteringTitle = CommonUtils.filteringContent(title);
            String filteringContent = CommonUtils.filteringContent(content);
            if (CommonUtils.checkFull(filteringTitle) || CommonUtils.checkFull(filteringContent)) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "内容不能为空并且不能包含非法字符！", new JSONArray());
            }
            usedDeal.setContent(filteringTitle);
            usedDeal.setContent(filteringContent);
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != usedDeal.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + usedDeal.getUserId() + "]的公告信息", new JSONObject());
        }
        // 查询数据库
        UsedDeal posts = usedDealService.findUserById(usedDeal.getId());
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //先把缓存中的推荐数据清除
        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_IPS_HOMELIST, 0, 101);
        for (int i = 0; i < list.size(); i++) {
            IPS_Home home = (IPS_Home) list.get(i);
            if (home.getAfficheType() == 2 && home.getInfoId() == usedDeal.getId()) {
                redisUtils.removeList(Constants.REDIS_KEY_IPS_HOMELIST, 1, home);
            }
        }
        if (list.size() == 101) {
            //清除缓存中的信息
            redisUtils.expire(Constants.REDIS_KEY_IPS_HOMELIST, 0);
            redisUtils.pushList(Constants.REDIS_KEY_IPS_HOMELIST, list, 0);
        }
        //计算公告分数
        int num3 = 0;//图片
        int fraction = 0;//公告分数
        int num = CommonUtils.getStringLengsByByte(usedDeal.getTitle());//标题
        int num2 = CommonUtils.getStringLengsByByte(usedDeal.getContent());//内容
        if (!CommonUtils.checkFull(usedDeal.getImgUrl())) {
            String[] imgArray = usedDeal.getImgUrl().split(",");
            if (imgArray != null) {
                num3 = imgArray.length;

                if (num3 == 1) {
                    fraction += 15;
                }
                if (num3 >= 6) {
                    fraction += 40;
                }
                if (num3 > 3 && num3 < 6) {
                    fraction += 30;
                }
            }
        }
        if (num <= 5 * 2) {
            fraction += 5;
        }
        if (num > 5 * 2 && num <= 10 * 2) {
            fraction += 10;
        }
        if (num > 10 * 2 && num <= 20 * 2) {
            fraction += 20;
        }
        if (num > 20 * 2) {
            fraction += 30;
        }

        if (num2 <= 20 * 2) {
            fraction += 5;
        }
        if (num2 > 20 * 2 && num2 <= 50 * 2) {
            fraction += 10;
        }
        if (num2 > 50 * 2 && num2 <= 80 * 2) {
            fraction += 20;
        }
        if (num2 > 80 * 2) {
            fraction += 30;
        }
        //新增home
        if (fraction >= 70) {
            IPS_Home ipsHome = new IPS_Home();
            ipsHome.setInfoId(usedDeal.getId());
            ipsHome.setTitle(usedDeal.getTitle());
            ipsHome.setUserId(usedDeal.getUserId());
            ipsHome.setContent(usedDeal.getContent());
            ipsHome.setReleaseTime(usedDeal.getReleaseTime());
            ipsHome.setMediumImgUrl(usedDeal.getImgUrl());
            ipsHome.setRefreshTime(usedDeal.getRefreshTime());
            ipsHome.setAuditType(2);
            ipsHome.setDeleteType(1);
            ipsHome.setAfficheType(2);
            ipsHome.setFraction(fraction);

            //放入缓存
            redisUtils.addListLeft(Constants.REDIS_KEY_IPS_HOMELIST, ipsHome, 0);
        }
        if (usedDeal.getNegotiable() == 2) {
            usedDeal.setSellingPrice(0);
        }
        if (usedDeal.getPinkageType() == 1) {
            if (!CommonUtils.checkFull(usedDeal.getExpressMode())) {
                String[] dmbArrey = usedDeal.getExpressMode().split(";");
                if (dmbArrey != null && dmbArrey.length < 6) {
                    usedDeal.setExpressMode(usedDeal.getExpressMode());
                }
            }
        }
        usedDeal.setFraction(fraction);
        usedDeal.setRefreshTime(new Date());
        usedDealService.update(usedDeal);

        if (!CommonUtils.checkFull(usedDeal.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(usedDeal.getUserId(), usedDeal.getDelImgUrls());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", usedDeal.getId());
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_USEDDEAL + usedDeal.getId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * 查询详情
     *
     * @param id
     * @return
     */
    @Override
    public ReturnData getJunk(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        int num = 0;
        UsedDeal posts = null;
        Map<String, Object> otherPostsMap = redisUtils.hmget(Constants.REDIS_KEY_IPS_USEDDEAL + id);
        if (otherPostsMap == null || otherPostsMap.size() <= 0) {
            posts = usedDealService.findUserById(id);
            if (posts == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            UserInfo userInfo = null;
            userInfo = userInfoUtils.getUserInfo(posts.getUserId());
            if (userInfo != null) {
                num = usedDealService.findNum(userInfo.getUserId(), 1);//已上架
                posts.setSellingNumber(num);
                posts.setName(userInfo.getName());
                posts.setHead(userInfo.getHead());
                posts.setProTypeId(userInfo.getProType());
                posts.setHouseNumber(userInfo.getHouseNumber());
            }
            //新增浏览记录
            mqUtils.sendLookMQ(CommonUtils.getMyId(), id, posts.getTitle(), 2);
            //放入缓存
            otherPostsMap = CommonUtils.objectToMap(posts);
            redisUtils.hmset(Constants.REDIS_KEY_IPS_USEDDEAL + id, otherPostsMap, Constants.USER_TIME_OUT);
        } else {
            //新增浏览记录
            mqUtils.sendLookMQ(CommonUtils.getMyId(), id, otherPostsMap.get("title").toString(), 2);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", otherPostsMap);
    }

    /***
     * 分页查询
     * @param sort  排序条件:0默认排序，1最新发布，2价格最低，3价格最高，4离我最近
     * @param userId  用户ID
     * @param province  省
     * @param city  市
     * @param district  区
     * @param minPrice  最小价格
     * @param maxPrice  最大价格
     * @param usedSort1  一级分类:起始值为0,默认-1为不限 :二手手机 、数码、汽车...
     * @param usedSort2  二级分类:起始值为0,默认-1为不限 : 苹果,三星,联想....
     * @param usedSort3  三级分类:起始值为0,默认-1为不限 :iPhone6s.iPhone5s....
     * @param lat  纬度
     * @param lon  经度
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findJunkList(@PathVariable int sort, @PathVariable long userId, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int minPrice, @PathVariable int maxPrice, @PathVariable int usedSort1, @PathVariable int usedSort2, @PathVariable int usedSort3, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<UsedDeal> pageBean = null;
        if (sort == 4) {
            pageBean = usedDealService.findAoList(lat, lon, page, count);
        } else {
            pageBean = usedDealService.findList(sort, userId, province, city, district, minPrice, maxPrice, usedSort1, usedSort2, usedSort3, page, count);
        }
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List<UsedDeal> list = new ArrayList<>();
        list = pageBean.getList();
        Collections.sort(list, new Comparator<UsedDeal>() {
            @Override
            public int compare(UsedDeal o1, UsedDeal o2) {
                // 按照置顶等级进行降序排列
                if (o1.getFrontPlaceType() > o2.getFrontPlaceType()) {
                    return -1;
                }
                if (o1.getFrontPlaceType() == o2.getFrontPlaceType()) {
                    return 0;
                }
                return 1;
            }
        });
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                UserInfo userInfo = null;
                UsedDeal t = null;
                t = list.get(i);
                if (t != null) {
                    userInfo = userInfoUtils.getUserInfo(t.getUserId());
                    if (userInfo != null) {
                        t.setName(userInfo.getName());
                        t.setHead(userInfo.getHead());
                        t.setProTypeId(userInfo.getProType());
                        t.setHouseNumber(userInfo.getHouseNumber());
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, list);
    }

    /**
     * 根据买卖状态查询二手公告列表
     *
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param userId
     * @param sellType 商品买卖状态 : 1已上架，2已下架，3已卖出
     * @return
     */
    @Override
    public ReturnData findJunkState(@PathVariable long userId, @PathVariable int sellType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<UsedDeal> pageBean;
        pageBean = usedDealService.findList(userId, sellType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }

    /**
     * @Description: 更新二手公告买卖状态
     * @Param: id  二手ID
     * @Param: sellType  商品买卖状态 : 1已上架，2已下架，3已卖出
     * @return:
     */
    @Override
    public ReturnData updateBusiness(@PathVariable long id, @PathVariable int sellType) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "id参数有误", new JSONObject());
        }
        UsedDeal posts = usedDealService.findUserById(id);
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        posts.setSellType(sellType);
        usedDealService.updateStatus(posts);

        //更新home
        IPS_Home ipsHome = new IPS_Home();
        ipsHome.setInfoId(posts.getId());
        ipsHome.setTitle(posts.getTitle());
        ipsHome.setUserId(posts.getUserId());
        ipsHome.setContent(posts.getContent());
        ipsHome.setReleaseTime(posts.getReleaseTime());
        ipsHome.setMediumImgUrl(posts.getImgUrl());
        ipsHome.setRefreshTime(posts.getRefreshTime());
        ipsHome.setAuditType(2);
        ipsHome.setDeleteType(1);
        ipsHome.setAfficheType(2);
        ipsHome.setFraction(posts.getFraction());

        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_IPS_HOMELIST, 0, 101);
        for (int i = 0; i < list.size(); i++) {
            IPS_Home home = (IPS_Home) list.get(i);
            if (home.getAfficheType() == 1 && home.getInfoId() == posts.getId()) {
                redisUtils.removeList(Constants.REDIS_KEY_IPS_HOMELIST, 1, home);
            }
        }
        //放入缓存
        redisUtils.addListLeft(Constants.REDIS_KEY_IPS_HOMELIST, ipsHome, 0);
        if (list.size() == 101) {
            //清除缓存中的信息
            redisUtils.expire(Constants.REDIS_KEY_IPS_HOMELIST, 0);
            redisUtils.pushList(Constants.REDIS_KEY_IPS_HOMELIST, list, 0);
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_USEDDEAL + id, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @param userId
     * @Description: 统计已上架, 已卖出已下架, 我的订单数量
     * @return:
     */
    @Override
    public ReturnData statisticsJunk(@PathVariable long userId) {
        //验证参数
        if (userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        int sellCont1 = usedDealService.findNum(userId, 1);//已上架
        int sellCont2 = usedDealService.findNum(userId, 2);//已卖出,已下架
        int sellCont3 = usedDealService.findNum(userId, 0);//商家订单总数  暂未实现

        Map<String, Integer> map = new HashMap<>();
        map.put("sellCont1", sellCont1);
        map.put("sellCont2", sellCont2);
        map.put("sellCont3", 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
