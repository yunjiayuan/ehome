package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.LoveAndFriendsService;
import com.busi.service.OtherPostsService;
import com.busi.service.SearchGoodsService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @program: 公告首页
 * @author: ZHaoJiaJie
 * @create: 2018-08-10 10:01
 */
@RestController
public class IPS_HomeController extends BaseController implements IPS_HomeApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    OtherPostsService otherPostsService;

    @Autowired
    LoveAndFriendsService loveAndFriendsService;

    @Autowired
    SearchGoodsService searchGoodsService;

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
        int page = 1;
        int count = 50;
        List homeList = null;
        OtherPosts posts = null;
        SearchGoods searchGoods = null;
        LoveAndFriends loveAndFriends = null;
        List<IPS_Home> ips = new ArrayList<>();
        homeList = redisUtils.getList(Constants.REDIS_KEY_IPS_HOMELIST, 0, 100);
        if (userId != 0) {
            //开始查询
            PageBean<OtherPosts> otherPage = otherPostsService.findList(userId, page, count);
            PageBean<SearchGoods> goodsPage = searchGoodsService.findList(userId, page, count);
            PageBean<LoveAndFriends> lovePage = loveAndFriendsService.findUList(userId, page, count);
            List loveList = lovePage.getList();
            List otherList = otherPage.getList();
            List goodsList = goodsPage.getList();
            if (otherList != null && otherList.size() > 0) {
                for (int j = 0; j < otherList.size(); j++) {
                    if (j < 4) {
                        posts = (OtherPosts) otherList.get(j);
                        if (posts != null) {
                            IPS_Home ipsHome = new IPS_Home();
                            ipsHome.setInfoId(posts.getId());
                            ipsHome.setTitle(posts.getTitle());
                            ipsHome.setUserId(posts.getUserId());
                            ipsHome.setContent(posts.getContent());
                            ipsHome.setReleaseTime(posts.getAddTime());
                            ipsHome.setRefreshTime(posts.getRefreshTime());
                            ipsHome.setAuditType(2);
                            ipsHome.setDeleteType(1);
                            ipsHome.setAfficheType(6);
                            ipsHome.setFraction(posts.getFraction());
                            ips.add(ipsHome);
                        }
                    }
                }
            }
            if (goodsList != null && goodsList.size() > 0) {
                for (int j = 0; j < goodsList.size(); j++) {
                    if (j < 4) {
                        searchGoods = (SearchGoods) goodsList.get(j);
                        if (searchGoods != null) {
                            IPS_Home ipsHome = new IPS_Home();
                            ipsHome.setInfoId(searchGoods.getId());
                            ipsHome.setTitle(searchGoods.getTitle());
                            ipsHome.setUserId(searchGoods.getUserId());
                            ipsHome.setContent(searchGoods.getContent());
                            ipsHome.setReleaseTime(searchGoods.getAddTime());
                            ipsHome.setMediumImgUrl(searchGoods.getImgUrl());
                            ipsHome.setRefreshTime(searchGoods.getRefreshTime());
                            ipsHome.setAuditType(2);
                            ipsHome.setDeleteType(1);
                            ipsHome.setAfficheType(searchGoods.getSearchType() + 2);
                            ipsHome.setFraction(searchGoods.getFraction());
                            ips.add(ipsHome);
                        }
                    }
                }
            }
            if (loveList != null && loveList.size() > 0) {
                for (int j = 0; j < loveList.size(); j++) {
                    if (j < 4) {
                        loveAndFriends = (LoveAndFriends) loveList.get(j);
                        if (loveAndFriends != null) {
                            IPS_Home ipsHome = new IPS_Home();
                            ipsHome.setInfoId(loveAndFriends.getId());
                            ipsHome.setTitle(loveAndFriends.getTitle());
                            ipsHome.setUserId(loveAndFriends.getUserId());
                            ipsHome.setContent(loveAndFriends.getContent());
                            ipsHome.setMediumImgUrl(loveAndFriends.getImgUrl());
                            ipsHome.setRefreshTime(loveAndFriends.getRefreshTime());
                            ipsHome.setReleaseTime(loveAndFriends.getReleaseTime());
                            ipsHome.setAuditType(2);
                            ipsHome.setDeleteType(1);
                            ipsHome.setAfficheType(1);
                            ipsHome.setFraction(loveAndFriends.getFraction());
                            ips.add(ipsHome);
                        }
                    }
                }
            }
            Collections.sort(ips, new Comparator<IPS_Home>() {
                @Override
                public int compare(IPS_Home o1, IPS_Home o2) {
                    // 按照刷新时间进行降序排列
                    if (o1.getRefreshTime().getTime() > o2.getRefreshTime().getTime()) {
                        return -1;
                    }
                    if (o1.getRefreshTime().getTime() == o2.getRefreshTime().getTime()) {
                        return 0;
                    }
                    return 1;
                }
            });
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, ips);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, homeList);
    }

    /**
     * 刷新公告时间
     *
     * @param infoId      公告ID
     * @param userId      用户ID
     * @param afficheType 公告类别标志：1婚恋交友,2二手手机,3寻人,4寻物,5失物招领,6其他 7发简历找工作 8发布招聘（注：后续添加）
     * @return
     */
    @Override
    public ReturnData refreshTime(@PathVariable long infoId, @PathVariable long userId, @PathVariable int afficheType) {
        //验证参数
        if (infoId <= 0 || userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "ID参数有误", new JSONObject());
        }
        //验证操作人员权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限刷新用户[" + userId + "]的公告信息", new JSONObject());
        }
        List list = null;
        OtherPosts posts = null;
        SearchGoods searchGoods = null;
        IPS_Home ipsHome = new IPS_Home();
        LoveAndFriends loveAndFriends = null;
        list = redisUtils.getList(Constants.REDIS_KEY_IPS_HOMELIST, 0, 101);
        if (afficheType == 1) { //婚恋交友
            loveAndFriends = loveAndFriendsService.findByIdUser(userId);
            if (loveAndFriends == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            loveAndFriends.setRefreshTime(new Date());
            loveAndFriendsService.updateTime(loveAndFriends);

            //更新home
            ipsHome.setAuditType(2);
            ipsHome.setDeleteType(1);
            ipsHome.setInfoId(infoId);
            ipsHome.setUserId(userId);
            ipsHome.setAfficheType(1);
            ipsHome.setTitle(loveAndFriends.getTitle());
            ipsHome.setContent(loveAndFriends.getContent());
            ipsHome.setReleaseTime(loveAndFriends.getReleaseTime());
            ipsHome.setRefreshTime(loveAndFriends.getRefreshTime());
            ipsHome.setFraction(loveAndFriends.getFraction());
            ipsHome.setMediumImgUrl(loveAndFriends.getImgUrl());
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    IPS_Home home = (IPS_Home) list.get(i);
                    if (home.getAfficheType() == 1 && home.getInfoId() == loveAndFriends.getId()) {
                        redisUtils.removeList(Constants.REDIS_KEY_IPS_HOMELIST, 1, home);
                    }
                }
            }
            //清除缓存中的信息
            redisUtils.expire(Constants.REDIS_KEY_IPS_LOVEANDFRIEND + infoId, 0);
        }
        if (afficheType == 2) {//二手手机
            //后续添加
        }
        if (afficheType == 3 || afficheType == 4 || afficheType == 5) {//寻人,寻物，失物招领
            searchGoods = searchGoodsService.findUserById(infoId);
            if (searchGoods == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            searchGoods.setRefreshTime(new Date());
            searchGoodsService.updateTime(searchGoods);

            //更新home
            ipsHome.setAuditType(2);
            ipsHome.setDeleteType(1);
            ipsHome.setInfoId(infoId);
            ipsHome.setUserId(userId);
            ipsHome.setTitle(searchGoods.getTitle());
            ipsHome.setContent(searchGoods.getContent());
            ipsHome.setReleaseTime(searchGoods.getAddTime());
            ipsHome.setRefreshTime(searchGoods.getRefreshTime());
            ipsHome.setFraction(searchGoods.getFraction());
            ipsHome.setMediumImgUrl(searchGoods.getImgUrl());
            ipsHome.setAfficheType(searchGoods.getSearchType() + 2);
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    IPS_Home home = (IPS_Home) list.get(i);
                    if ((home.getAfficheType() == 3 || home.getAfficheType() == 4 || home.getAfficheType() == 5) && home.getInfoId() == searchGoods.getId()) {
                        redisUtils.removeList(Constants.REDIS_KEY_IPS_HOMELIST, 1, home);
                    }
                }
            }
            //清除缓存中的信息
            redisUtils.expire(Constants.REDIS_KEY_IPS_SEARCHGOODS + infoId, 0);
        }
        if (afficheType == 6) { //其他
            posts = otherPostsService.findUserById(infoId);
            if (posts == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            posts.setRefreshTime(new Date());
            otherPostsService.updateTime(posts);

            //更新home
            ipsHome.setAuditType(2);
            ipsHome.setDeleteType(1);
            ipsHome.setInfoId(infoId);
            ipsHome.setUserId(userId);
            ipsHome.setAfficheType(6);
            ipsHome.setTitle(posts.getTitle());
            ipsHome.setContent(posts.getContent());
            ipsHome.setReleaseTime(posts.getAddTime());
            ipsHome.setRefreshTime(posts.getRefreshTime());
            ipsHome.setFraction(posts.getFraction());
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    IPS_Home home = (IPS_Home) list.get(i);
                    if (home.getAfficheType() == 6 && home.getInfoId() == posts.getId()) {
                        redisUtils.removeList(Constants.REDIS_KEY_IPS_HOMELIST, 1, home);
                    }
                }
            }
            //清除缓存中的信息
            redisUtils.expire(Constants.REDIS_KEY_IPS_OTHERPOSTS + infoId, 0);
        }
        //放入缓存
        redisUtils.addList(Constants.REDIS_KEY_IPS_HOMELIST, ipsHome, 0);
        if (list.size() == 101) {
            //清除缓存中的信息
            redisUtils.expire(Constants.REDIS_KEY_IPS_HOMELIST, 0);
            redisUtils.pushList(Constants.REDIS_KEY_IPS_HOMELIST, list, 0);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
