package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.TalkToSomeoneService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 找人倾诉相关接口
 * @author: ZHaoJiaJie
 * @create: 2020-11-23 15:30:21
 */
@RestController
public class TalkToSomeoneController extends BaseController implements TalkToSomeoneApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    TalkToSomeoneService homeHospitalService;

    /***
     * 查询详情
     * @param userId
     * @return
     */
    @Override
    public ReturnData findSomeone(@PathVariable long userId) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_TALKTO_SOMEONE + userId);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            TalkToSomeone reserveData = homeHospitalService.findSomeone(userId);
            if (reserveData != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(reserveData);
                redisUtils.hmset(Constants.REDIS_KEY_TALKTO_SOMEONE + reserveData.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        TalkToSomeone ik = (TalkToSomeone) CommonUtils.mapToObject(kitchenMap, TalkToSomeone.class);
        if (ik == null) {
            ik = new TalkToSomeone();
            ik.setUserId(userId);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", ik);
    }

    /***
     * 新增倾诉信息
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addSomeone(@Valid @RequestBody TalkToSomeone homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        homeHospital.setTime(new Date());
        homeHospitalService.add(homeHospital);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新倾诉信息
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeSomeone(@Valid @RequestBody TalkToSomeone homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //查询数据库
        TalkToSomeone reserveData = homeHospitalService.findSomeone(homeHospital.getUserId());
        if (reserveData == null) {
            homeHospital.setTime(new Date());
            homeHospitalService.add(homeHospital);
            //更新用户找人倾诉状态
            userInfoUtils.updateTalkToSomeoneStatus(homeHospital.getUserId(), homeHospital.getState());
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        if (!CommonUtils.checkFull(homeHospital.getRemarks())) {
            homeHospitalService.update(homeHospital);
        } else {//更新状态
            homeHospitalService.update2(homeHospital);
            //更新用户找人倾诉状态
            userInfoUtils.updateTalkToSomeoneStatus(homeHospital.getUserId(), homeHospital.getState());
        }
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_TALKTO_SOMEONE + homeHospital.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 倾诉
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData talkToSomeone(@Valid @RequestBody TalkToSomeoneOrder homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        Date date = new Date();
        homeHospital.setAddTime(date);
        long time = date.getTime();
        String noTime = String.valueOf(time);
        String random = CommonUtils.getRandom(6, 1);
        String noRandom = CommonUtils.strToMD5(noTime + homeHospital.getMyId() + random, 16);
        homeHospital.setNo(noRandom);//订单编号【MD5】
        homeHospitalService.talkToSomeone(homeHospital);
        //放入缓存
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", homeHospital.getNo());
        Map<String, Object> ordersMap = CommonUtils.objectToMap(homeHospital);
        redisUtils.hmset(Constants.REDIS_KEY_TALKTOSOMEONE + homeHospital.getMyId() + "_" + homeHospital.getNo(), ordersMap, Constants.USER_TIME_OUT);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新倾诉状态
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeSomeoneState(@Valid @RequestBody TalkToSomeoneOrder homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        homeHospitalService.changeSomeoneState(homeHospital);
        //判断是否已倾诉并且已支付
        if (homeHospital.getStatus() == 1) {
            TalkToSomeoneOrder talk = homeHospitalService.findSomeone2(homeHospital.getId());
            if (talk != null && talk.getPayState() == 1) {
                //转入被倾诉者钱包
                mqUtils.sendPurseMQ(homeHospital.getUserId(), 45, 0, talk.getMoney());
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询推荐列表
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findSomeoneList(@PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<TalkToSomeone> pageBean;
        pageBean = homeHospitalService.findSomeoneList(page, count);
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                TalkToSomeone ik = (TalkToSomeone) list.get(i);
                UserInfo sendInfoCache = null;
                sendInfoCache = userInfoUtils.getUserInfo(ik.getUserId());
                if (sendInfoCache != null) {
                    ik.setName(sendInfoCache.getName());
                    ik.setHead(sendInfoCache.getHead());
                    ik.setProTypeId(sendInfoCache.getProType());
                    ik.setHouseNumber(sendInfoCache.getHouseNumber());
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 查询记录列表
     * @param type   类型：-1全部 0未倾诉 1已倾诉
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findSomeoneHistoryList(@PathVariable int type, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<TalkToSomeoneOrder> pageBean;
        pageBean = homeHospitalService.findSomeoneHistoryList(CommonUtils.getMyId(), type, page, count);
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                TalkToSomeoneOrder ik = (TalkToSomeoneOrder) list.get(i);
                UserInfo sendInfoCache = null;
                sendInfoCache = userInfoUtils.getUserInfo(ik.getUserId());
                if (sendInfoCache != null) {
                    ik.setName(sendInfoCache.getName());
                    ik.setHead(sendInfoCache.getHead());
                    ik.setProTypeId(sendInfoCache.getProType());
                    ik.setHouseNumber(sendInfoCache.getHouseNumber());
                    ik.setSex(sendInfoCache.getSex());
                    ik.setBirthday(sendInfoCache.getBirthday());
                    ik.setProvince(sendInfoCache.getProvince());
                    ik.setCity(sendInfoCache.getCity());
                    ik.setDistrict(sendInfoCache.getDistrict());
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }
}
