package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.CommunityNewsService;
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
 * @program: ehome
 * @description: 资讯
 * @author: ZHaoJiaJie
 * @create: 2020-03-20 11:46:04
 */
@RestController
public class CommunityNewsController extends BaseController implements CommunityNewsApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    CommunityNewsService todayNewsService;

    @Autowired
    CommunityService communityService;

    @Autowired
    PropertyService propertyService;


    /***
     * 新增
     * @param todayNews
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addNews(@Valid @RequestBody CommunityNews todayNews, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        int newsFormat = todayNews.getNewsFormat();
        todayNews.setNewsState(0);
        todayNews.setAddTime(new Date());
        todayNews.setRefreshTime(new Date());
        if (newsFormat == 1 || newsFormat == 2) {//发布类型  0纯文  1一图  2多图  3视频
            if (!CommonUtils.checkFull(todayNews.getImgUrls())) {
                String[] arrey = todayNews.getImgUrls().split(",");
                if (arrey.length > 2) {
                    todayNews.setNewsFormat(2);
                } else {
                    todayNews.setNewsFormat(1);
                }
            }
        } else if (newsFormat == 3) {
            if (!CommonUtils.checkFull(todayNews.getVideoUrl())) {
                todayNews.setNewsFormat(3);
            }
        } else {
            todayNews.setNewsFormat(0);
        }
        todayNewsService.add(todayNews);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新
     * @Param: todayNews
     * @return:
     */
    @Override
    public ReturnData editNews(@Valid @RequestBody CommunityNews todayNews, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        todayNews.setRefreshTime(new Date());
        todayNewsService.editNews(todayNews);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_COMMUNITY_NEWS + todayNews.getId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除
     * @return:
     */
    @Override
    public ReturnData delNews(@PathVariable long id) {
        todayNewsService.del(id);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_COMMUNITY_NEWS + id, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询新闻列表
     * @param noticeType 通告： 0资讯 1点对点通知通告（普通居民） 2内部人员通知
     * @param communityId newsType=0时为居委会ID  newsType=1时为物业ID
     * @param newsType 社区： 0居委会  1物业
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findNewsList(@PathVariable int noticeType, @PathVariable long communityId, @PathVariable int newsType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (newsType < 0 || newsType > 3) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数newsType有误", new JSONObject());
        }
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<CommunityNews> pageBean = null;
        PageBean<CommunityNews> pageBean2 = null;
        pageBean2 = new PageBean<>();
        pageBean2.setSize(0);
        pageBean2.setPageNum(page);
        pageBean2.setPageSize(count);
        pageBean2.setList(null);
        pageBean = todayNewsService.findListByAdmin(communityId, newsType, noticeType, page, count);
        CommunityResident sa = null;
        PropertyResident resident = null;
        long userId = CommonUtils.getMyId();
        String[] tagArray = null;
        if (noticeType == 1) {// 1点对点通知通告（普通居民）
            if (newsType == 0 && noticeType == 1) {//居委会
                sa = communityService.findResident(communityId, userId);
                if (sa == null) {
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean2);
                }
            }
        }
        if (noticeType == 2) { //内部人员通知
            if (newsType == 0) {
                sa = communityService.findResident(communityId, userId);
                if (sa == null) {
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean2);
                }
                if (sa.getIdentity() > 0) {
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
                } else {
                    List list = pageBean.getList();
                    if (list == null || list.size() <= 0) {
                        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
                    }
                    String tag = sa.getTags();
                    if (!CommonUtils.checkFull(tag)) {
                        String[] array = tag.split(",");
                        for (int i = 0; i < array.length; i++) {
                            tagArray = new String[array.length];
                            tagArray[i] = "#" + array[i] + "#";
                        }
                    }
                    List list1 = new ArrayList();
                    for (int j = 0; j < list.size(); j++) {
                        CommunityNews news = (CommunityNews) list.get(j);
                        if (CommonUtils.checkFull(news.getIdentity()) && CommonUtils.checkFull(news.getLookUserIds())) {//判断是否设置查看权限
                            list1.add(news);
                            break;
                        }
                        if (!CommonUtils.checkFull(news.getIdentity())) {
                            String[] identity = news.getIdentity().split(",");
                            if (!CommonUtils.checkFull(sa.getTags())) {
                                for (int i = 0; i < tagArray.length; i++) {
                                    String num = tagArray[i];
                                    for (int a = 0; a < identity.length; a++) {
                                        String num2 = identity[a];
                                        if (num.equals(num2)) {
                                            list1.add(news);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        for (int b = 0; b < list1.size(); b++) {
                            if (list1.get(b) == news) {
                                break;
                            }
                        }
                        if (!CommonUtils.checkFull(news.getLookUserIds())) {//判断是否设置查看权限
                            String[] num = news.getLookUserIds().split(",");
                            String num1 = "#" + sa.getUserId() + "#";
                            for (int i = 0; i < num.length; i++) {
                                if (num[i].equals(num1)) {
                                    list1.add(news);
                                    break;
                                }
                            }
                        }
                    }
                    //删除ArrayList中重复元素并保持顺序
//                    Set set = new HashSet();
//                    List newList = new ArrayList();
//                    for (Iterator iter = list1.iterator(); iter.hasNext(); ) {
//                        Object integer = iter.next();
//                        if (set.add(integer)) {
//                            newList.add(integer);
//                        }
//                    }
//                    list.clear();
//                    list.add(newList);
                    pageBean = new PageBean<>();
                    pageBean.setSize(list1.size());
                    pageBean.setPageNum(page);
                    pageBean.setPageSize(count);
                    pageBean.setList(list1);
                }
            }
            if (newsType == 1) {
                resident = propertyService.findResident(communityId, userId);
                if (resident == null) {
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean2);
                }
                if (resident.getIdentity() > 0) {
                    pageBean = todayNewsService.findListByAdmin(communityId, newsType, noticeType, page, count);
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 根据ID查询
     * @param infoId 资讯ID
     * @return
     */
    @Override
    public ReturnData findPress(@PathVariable long infoId) {
        if (infoId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "infoId参数有误", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_COMMUNITY_NEWS + infoId);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            CommunityNews sa = todayNewsService.findInfo(infoId);
            if (sa == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询资讯不存在!", new JSONObject());
            }
            //放入缓存
            kitchenMap = CommonUtils.objectToMap(sa);
            redisUtils.hmset(Constants.REDIS_KEY_COMMUNITY_NEWS + infoId, kitchenMap, Constants.USER_TIME_OUT);
        }
        if (kitchenMap != null && kitchenMap.size() > 0) {
            CommunityNews communityNews = (CommunityNews) CommonUtils.mapToObject(kitchenMap, CommunityNews.class);
            if (communityNews.getNoticeType() < 2) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
            }
            //居委会通告
            if (CommonUtils.checkFull(communityNews.getIdentity()) && CommonUtils.checkFull(communityNews.getLookUserIds())) {//判断是否设置查看权限
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
            }
            //新增通告浏览记录
            CommunityLook communityLook = new CommunityLook();
            communityLook.setCommunityId(communityNews.getCommunityId());
            communityLook.setInfoId(infoId);
            communityLook.setTime(new Date());
            communityLook.setTitle(communityNews.getTitle());
            communityLook.setUserId(CommonUtils.getMyId());
            //判断是否是管理员
            CommunityResident sa = communityService.findResident(communityNews.getCommunityId(), CommonUtils.getMyId());
            if (sa == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "没有权限", new JSONArray());
            }
            if (sa.getIdentity() > 0) {
                todayNewsService.addLook(communityLook);
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
            } else {
                if (!CommonUtils.checkFull(sa.getTags()) && !CommonUtils.checkFull(communityNews.getIdentity())) {//判断是否设置查看权限
                    String[] num = communityNews.getIdentity().split(",");
                    String[] num1 = sa.getTags().split(",");
                    for (int i = 0; i < num.length; i++) {
                        for (int j = 0; j < num1.length; j++) {
                            if (num[i].equals("#" + num1[j] + "#")) {
                                todayNewsService.addLook(communityLook);
                                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
                            }
                        }
                    }
                }
                if (!CommonUtils.checkFull(communityNews.getLookUserIds())) {//判断是否设置查看权限
                    String[] num = communityNews.getLookUserIds().split(",");
                    String num1 = "#" + sa.getUserId() + "#";
                    for (int i = 0; i < num.length; i++) {
                        if (num[i].equals(num1)) {
                            todayNewsService.addLook(communityLook);
                            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
                        }
                    }
                }
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "没有权限", new JSONArray());
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONArray());
    }

    /**
     * @param ids
     * @Description: 删除浏览记录
     * @return:
     */
    @Override
    public ReturnData delLook(@PathVariable String ids) {
        //验证参数
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "ids参数有误", new JSONObject());
        }
        //查询数据库
        int look = todayNewsService.delLook(ids.split(","));
        if (look <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "浏览记录[" + ids + "]不存在", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询浏览记录接口
     * @param id  通告ID
     * @param page
     * @param count
     * @return
     */
    @Override
    public ReturnData findLook(@PathVariable long id, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (id < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //开始查询
        PageBean<CommunityLook> pageBean;
        pageBean = todayNewsService.findLook(id, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        CommunityLook t = null;
        UserInfo userCache = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                t = (CommunityLook) list.get(i);
                if (t != null) {
                    userCache = userInfoUtils.getUserInfo(t.getUserId());
                    if (userCache != null) {
                        t.setName(userCache.getName());
                        t.setHead(userCache.getHead());
                        t.setProTypeId(userCache.getProType());
                        t.setHouseNumber(userCache.getHouseNumber());
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, list);
    }
}
