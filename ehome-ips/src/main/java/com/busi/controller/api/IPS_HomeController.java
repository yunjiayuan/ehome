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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        List<Object> ips = new ArrayList<>();
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
                    if (j < 5) {
                        posts = (OtherPosts) otherList.get(j);
                        ips.add(posts);
                    }
                }
            }
            if (goodsList != null && goodsList.size() > 0) {
                for (int j = 0; j < goodsList.size(); j++) {
                    if (j < 5) {
                        searchGoods = (SearchGoods) goodsList.get(j);
                        ips.add(searchGoods);
                    }
                }
            }
            if (loveList != null && loveList.size() > 0) {
                for (int j = 0; j < loveList.size(); j++) {
                    if (j < 5) {
                        loveAndFriends = (LoveAndFriends) loveList.get(j);
                        ips.add(loveAndFriends);
                    }
                }
            }
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, ips);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, homeList);
    }

    /**
     * 刷新公告时间
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
        IPS_Home ipsHome = new IPS_Home();
        OtherPosts posts = null;
        SearchGoods searchGoods = null;
        LoveAndFriends loveAndFriends = null;
        if (afficheType == 1) { //婚恋交友
            loveAndFriends = loveAndFriendsService.findByIdUser(userId);
            if (loveAndFriends == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            loveAndFriends.setRefreshTime(new Date());
            loveAndFriendsService.updateTime(loveAndFriends);
            ipsHome.setAfficheType(1);
            ipsHome.setMediumImgUrl(loveAndFriends.getImgUrl());

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
            ipsHome.setMediumImgUrl(searchGoods.getImgUrl());
            ipsHome.setAfficheType(searchGoods.getSearchType() + 2);

            //清除缓存中的信息
            redisUtils.expire(Constants.REDIS_KEY_IPS_SEARCHGOODS + infoId, 0);
        }
        if (afficheType == 6) {//其他
            posts = otherPostsService.findUserById(infoId);
            if (posts == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            posts.setRefreshTime(new Date());
            otherPostsService.updateTime(posts);
            ipsHome.setAfficheType(6);

            //清除缓存中的信息
            redisUtils.expire(Constants.REDIS_KEY_IPS_OTHERPOSTS + infoId, 0);
        }
        //更新home
        ipsHome.setInfoId(posts.getId());
        ipsHome.setTitle(posts.getTitle());
        ipsHome.setUserId(posts.getUserId());
        ipsHome.setContent(posts.getContent());
        ipsHome.setReleaseTime(posts.getAddTime());
        ipsHome.setRefreshTime(posts.getRefreshTime());
        ipsHome.setAuditType(2);
        ipsHome.setDeleteType(1);
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
        redisUtils.addList(Constants.REDIS_KEY_IPS_HOMELIST, ipsHome, 0);
        if (list.size() == 101) {
            //清除缓存中的信息
            redisUtils.expire(Constants.REDIS_KEY_IPS_HOMELIST, 0);
            redisUtils.pushList(Constants.REDIS_KEY_IPS_HOMELIST, list, 0);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
