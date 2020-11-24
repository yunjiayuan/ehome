package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.TalkToSomeoneService;
import com.busi.utils.CommonUtils;
import com.busi.utils.MqUtils;
import com.busi.utils.StatusCode;
import com.busi.utils.UserInfoUtils;
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
        TalkToSomeone reserveData = homeHospitalService.findSomeone(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("data", reserveData);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
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
        homeHospital.setTime(new Date());
        homeHospitalService.update(homeHospital);

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
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", homeHospital.getNo());
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
        if (homeHospital.getStatus() == 1 && homeHospital.getPayState() == 1) {
            //转入被倾诉者钱包
            mqUtils.sendPurseMQ(homeHospital.getUserId(), 45, 0, homeHospital.getMoney());
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
     * @param type   类型：0全部 1未倾诉 2已倾诉
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
        pageBean = homeHospitalService.findSomeoneHistoryList(type, page, count);
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
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }
}
