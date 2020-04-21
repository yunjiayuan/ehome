package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.CollectService;
import com.busi.service.SearchGoodsService;
import com.busi.utils.*;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * @program: 寻人寻物失物招领
 * @author: ZHaoJiaJie
 * @create: 2018-08-10 15:01
 */

@RestController
public class SearchGoodsController extends BaseController implements SearchGoodsApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqUtils mqUtils;

    @Autowired
    SearchGoodsService searchGoodsService;

    @Autowired
    CollectService collectService;

    /***
     * 新增
     * @param searchGoods
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addMatter(@Valid @RequestBody SearchGoods searchGoods, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //处理特殊字符
        String title = searchGoods.getTitle();
        String content = searchGoods.getContent();
        if (!CommonUtils.checkFull(content) || !CommonUtils.checkFull(title)) {
            String filteringTitle = CommonUtils.filteringContent(title);
            String filteringContent = CommonUtils.filteringContent(content);
            if (CommonUtils.checkFull(filteringTitle) || CommonUtils.checkFull(filteringContent)) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "内容不能为空并且不能包含非法字符！", new JSONArray());
            }
            searchGoods.setContent(filteringTitle);
            searchGoods.setContent(filteringContent);
        }
        //验证地区
        if (!CommonUtils.checkProvince_city_district(0, searchGoods.getProvince(), searchGoods.getCity(), searchGoods.getDistrict())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "省、市、区参数不匹配", new JSONObject());
        }
        //计算公告分数
        int num3 = 0;//图片
        int fraction = 0;//公告分数
        int num = CommonUtils.getStringLengsByByte(searchGoods.getTitle());//标题
        int num2 = CommonUtils.getStringLengsByByte(searchGoods.getContent());//内容
        if (!CommonUtils.checkFull(searchGoods.getImgUrl())) {
            String[] imgArray = searchGoods.getImgUrl().split(",");
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

        searchGoods.setAuditType(2);
        searchGoods.setDeleteType(1);
        searchGoods.setAfficheStatus(0);
        searchGoods.setFraction(fraction);
        searchGoods.setAddTime(new Date());
        searchGoods.setRefreshTime(new Date());
        searchGoodsService.add(searchGoods);

        //新增home
        if (searchGoods.getFraction() >= 70) {
            IPS_Home ipsHome = new IPS_Home();
            ipsHome.setInfoId(searchGoods.getId());
            ipsHome.setTitle(searchGoods.getTitle());
            ipsHome.setUserId(searchGoods.getUserId());
            ipsHome.setContent(searchGoods.getContent());
            ipsHome.setMediumImgUrl(searchGoods.getImgUrl());
            ipsHome.setReleaseTime(searchGoods.getAddTime());
            ipsHome.setRefreshTime(searchGoods.getRefreshTime());
            ipsHome.setAuditType(2);
            ipsHome.setDeleteType(1);
            ipsHome.setAfficheType(searchGoods.getSearchType() + 2);
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
        mqUtils.sendTaskMQ(searchGoods.getUserId(), 1, 3);
        //新增足迹
        mqUtils.sendFootmarkMQ(searchGoods.getUserId(), searchGoods.getTitle(), searchGoods.getImgUrl(), null, null, searchGoods.getId() + "," + (searchGoods.getSearchType() + 2), 1);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", searchGoods.getId());
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_SEARCHGOODS + searchGoods.getId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 删除
     * @param id
     * @param userId 将要删除的userId
     * @return
     */
    @Override
    public ReturnData delMatter(@PathVariable long id, @PathVariable long userId) {
        //验证参数
        if (userId <= 0 || id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证删除权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限删除用户[" + userId + "]的公告信息", new JSONObject());
        }
        // 查询数据库
        SearchGoods posts = searchGoodsService.findUserById(id);
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //更新home
        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_IPS_HOMELIST, 0, 101);
        for (int i = 0; i < list.size(); i++) {
            IPS_Home home = (IPS_Home) list.get(i);
            if (home.getAfficheType() == (posts.getSearchType() + 2) && home.getInfoId() == posts.getId()) {
                redisUtils.removeList(Constants.REDIS_KEY_IPS_HOMELIST, 1, home);
            }
        }
        posts.setDeleteType(2);
        searchGoodsService.updateDel(posts);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_SEARCHGOODS + id, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新
     * @param searchGoods
     * @return
     */
    @Override
    public ReturnData updateMatter(@Valid @RequestBody SearchGoods searchGoods, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //处理特殊字符
        String title = searchGoods.getTitle();
        String content = searchGoods.getContent();
        if (!CommonUtils.checkFull(content) || !CommonUtils.checkFull(title)) {
            String filteringTitle = CommonUtils.filteringContent(title);
            String filteringContent = CommonUtils.filteringContent(content);
            if (CommonUtils.checkFull(filteringTitle) || CommonUtils.checkFull(filteringContent)) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "内容不能为空并且不能包含非法字符！", new JSONArray());
            }
            searchGoods.setContent(filteringTitle);
            searchGoods.setContent(filteringContent);
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != searchGoods.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + searchGoods.getUserId() + "]的公告信息", new JSONObject());
        }
        // 查询数据库
        SearchGoods posts = searchGoodsService.findUserById(searchGoods.getId());
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_IPS_HOMELIST, 0, 101);
        for (int i = 0; i < list.size(); i++) {
            IPS_Home home = (IPS_Home) list.get(i);
            if (home.getAfficheType() == (posts.getSearchType() + 2) && home.getInfoId() == searchGoods.getId()) {
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
        int num = CommonUtils.getStringLengsByByte(searchGoods.getTitle());//标题
        int num2 = CommonUtils.getStringLengsByByte(searchGoods.getContent());//内容
        if (!CommonUtils.checkFull(searchGoods.getImgUrl())) {
            String[] imgArray = searchGoods.getImgUrl().split(",");
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
        if (fraction > 70) {
            IPS_Home ipsHome = new IPS_Home();
            ipsHome.setInfoId(searchGoods.getId());
            ipsHome.setTitle(searchGoods.getTitle());
            ipsHome.setUserId(searchGoods.getUserId());
            ipsHome.setContent(searchGoods.getContent());
            ipsHome.setReleaseTime(posts.getAddTime());
            ipsHome.setMediumImgUrl(searchGoods.getImgUrl());
            ipsHome.setRefreshTime(new Date());
            ipsHome.setAuditType(2);
            ipsHome.setDeleteType(1);
            ipsHome.setAfficheType(searchGoods.getSearchType() + 2);
            ipsHome.setFraction(fraction);

            //放入缓存
            redisUtils.addListLeft(Constants.REDIS_KEY_IPS_HOMELIST, ipsHome, 0);
        }
        searchGoods.setFraction(fraction);
        searchGoods.setRefreshTime(new Date());
        searchGoodsService.update(searchGoods);

        if (!CommonUtils.checkFull(searchGoods.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(searchGoods.getUserId(), searchGoods.getDelImgUrls());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", searchGoods.getId());
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_SEARCHGOODS + searchGoods.getId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * 查询
     *
     * @param id
     * @return
     */
    @Override
    public ReturnData getMatter(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        int num = 0;
        SearchGoods posts = null;
        Map<String, Object> otherPostsMap = redisUtils.hmget(Constants.REDIS_KEY_IPS_SEARCHGOODS + id);
        if (otherPostsMap == null || otherPostsMap.size() <= 0) {
            posts = searchGoodsService.findUserById(id);
            if (posts == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            //新增浏览记录
            mqUtils.sendLookMQ(CommonUtils.getMyId(), id, posts.getTitle(), posts.getSearchType() + 2);
            //放入缓存
            otherPostsMap = CommonUtils.objectToMap(posts);
            redisUtils.hmset(Constants.REDIS_KEY_IPS_SEARCHGOODS + id, otherPostsMap, Constants.USER_TIME_OUT);
        } else {
            //新增浏览记录
            num = (int) otherPostsMap.get("searchType");
            mqUtils.sendLookMQ(CommonUtils.getMyId(), id, otherPostsMap.get("title").toString(), num + 2);
        }
        int collection = 0;
        Collect collect1 = null;
        collect1 = collectService.findUserId(id, CommonUtils.getMyId(), posts.getSearchType() + 2);
        if (collect1 != null) {
            collection = 1;
        }
        otherPostsMap.put("collection", collection);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", otherPostsMap);
    }

    /***
     * 分页查询
     * @param province  省
     * @param city  市
     * @param district  区
     * @param beginAge  开始年龄
     * @param endAge  结束年龄
     * @param missingSex  失踪人性别:1男,2女
     * @param searchType  查找类别:0不限 ,1寻人,2寻物,3失物招领
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findMatterList(@PathVariable long userId, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int beginAge, @PathVariable int endAge, @PathVariable int missingSex, @PathVariable int searchType, @PathVariable int page, @PathVariable int count) {

        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (missingSex < 0 || missingSex > 2) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "missingSex参数有误", new JSONObject());
        }
        if (searchType < 0 || searchType > 3) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "searchType参数有误", new JSONObject());
        }
        if (beginAge < 0 || beginAge > endAge) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "beginAge参数有误", new JSONObject());
        }
        if (endAge < 0 || endAge < beginAge) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "endAge参数有误", new JSONObject());
        }
        //开始查询
        PageBean<SearchGoods> pageBean;
        pageBean = searchGoodsService.findList(userId, province, city, district, beginAge, endAge, missingSex, searchType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List<SearchGoods> searchGoods = new ArrayList<>();
        searchGoods = pageBean.getList();
        Collections.sort(searchGoods, new Comparator<SearchGoods>() {
            @Override
            public int compare(SearchGoods o1, SearchGoods o2) {
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
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }

    /**
     * 更新公告状态
     *
     * @param id            主键ID
     * @param userId        用户ID
     * @param afficheStatus 0未解决  1已解决
     * @return
     */
    @Override
    public ReturnData updateState(@PathVariable long id, @PathVariable long userId, @PathVariable int afficheStatus) {
        //验证参数
        if (id <= 0 || userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + userId + "]的公告信息", new JSONObject());
        }
        SearchGoods posts = searchGoodsService.findUserById(id);
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        posts.setAfficheStatus(afficheStatus);
        searchGoodsService.updateStatus(posts);

        //更新home
        IPS_Home ipsHome = new IPS_Home();
        ipsHome.setInfoId(posts.getId());
        ipsHome.setTitle(posts.getTitle());
        ipsHome.setUserId(posts.getUserId());
        ipsHome.setContent(posts.getContent());
        ipsHome.setReleaseTime(posts.getAddTime());
        ipsHome.setMediumImgUrl(posts.getImgUrl());
        ipsHome.setRefreshTime(posts.getRefreshTime());
        ipsHome.setAuditType(2);
        ipsHome.setDeleteType(1);
        ipsHome.setAfficheType(posts.getSearchType() + 2);
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
        redisUtils.expire(Constants.REDIS_KEY_IPS_SEARCHGOODS + id, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
