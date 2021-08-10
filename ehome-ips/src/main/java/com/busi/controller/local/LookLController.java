package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Look;
import com.busi.entity.LoveAndFriends;
import com.busi.entity.ReturnData;
import com.busi.entity.SearchGoods;
import com.busi.service.LookService;
import com.busi.service.LoveAndFriendsService;
import com.busi.service.SearchGoodsService;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @program: 浏览记录
 * @author: ZHaoJiaJie
 * @create: 2018-08-24 16:50
 */
@RestController
public class LookLController extends BaseController implements LookLocalController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    LookService lookService;

    @Autowired
    SearchGoodsService searchGoodsService;

    @Autowired
    LoveAndFriendsService loveAndFriendsService;

    /***
     * 新增
     * @param look
     * @return
     */
    @Override
    public ReturnData addLook(@RequestBody Look look) {

        look.setTime(new Date());
        int num = look.getAfficheType();
        if (num == 1) {//婚恋交友
            //查询数据库
            LoveAndFriends andFriends = loveAndFriendsService.findUserById(look.getInfoId());
            if (andFriends == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            andFriends.setSeeNumber(andFriends.getSeeNumber() + 1);
            loveAndFriendsService.updateSee(andFriends);
            //清除缓存中的信息
            redisUtils.expire(Constants.REDIS_KEY_IPS_LOVEANDFRIEND + andFriends.getId(), 0);
        }
        if (num == 3 || num == 4 || num == 5) {//寻人寻物失物招领
            //查询数据库
            SearchGoods andFriends = searchGoodsService.findUserById(look.getInfoId());
            if (andFriends == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            andFriends.setSeeNumber(andFriends.getSeeNumber() + 1);
            searchGoodsService.updateSee(andFriends);
            //清除缓存中的信息
            redisUtils.expire(Constants.REDIS_KEY_IPS_SEARCHGOODS + andFriends.getId(), 0);
        }
        lookService.add(look);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
