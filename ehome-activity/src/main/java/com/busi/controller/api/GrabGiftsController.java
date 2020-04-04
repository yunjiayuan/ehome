package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.GrabGiftsService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import com.busi.utils.UserAccountSecurityUtils;
import com.busi.utils.UserInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/***
 * 抢礼物相关接口
 * author：zhaojiajie
 * create time：2020-04-03 10:22:21
 */
@RestController
public class GrabGiftsController extends BaseController implements GrabGiftsApiController {

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    GrabGiftsService grabGiftsService;

    @Autowired
    UserAccountSecurityUtils userAccountSecurityUtils;

    /***
     * 抢礼物
     * @param grabMedium
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData grabMedium(@Valid @RequestBody GrabMedium grabMedium, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断该用户是否绑定手机
//        UserAccountSecurity userAccountSecurity = null;
//        userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(CommonUtils.getMyId());
//        if (userAccountSecurity != null) {
//            if (CommonUtils.checkFull(userAccountSecurity.getPhone())) {
//                return returnData(StatusCode.CODE_NOT_BIND_PHONE_ERROR.CODE_VALUE, "该用户未绑定手机号!", new JSONObject());
//            }
//        } else {
//            return returnData(StatusCode.CODE_NOT_BIND_PHONE_ERROR.CODE_VALUE, "该用户未绑定手机号!", new JSONObject());
//        }
        //判断当前用户当天是否还有次数 以每天凌晨0点为准 每天每人只能抢三次
        int num = grabGiftsService.findNum(CommonUtils.getMyId());
        if (num >= 3) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "今天次数用尽，明天再来吧", new JSONObject());
        }
        //开始抢
        grabMedium.setWinningState(0);
        grabMedium.setCost("谢谢参与");
        grabMedium.setTime(new Date());
        grabMedium.setUserId(CommonUtils.getMyId());
        grabGiftsService.add(grabMedium);
        Map<String, Object> map = new HashMap<>();
        map.put("winningState", grabMedium.getWinningState());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查询中奖人员列表
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findWinList(@PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<GrabMedium> pageBean;
        pageBean = grabGiftsService.findList(page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        List list = pageBean.getList();
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 6); // 控制时
//        calendar.set(Calendar.MINUTE, 0);       // 控制分
//        calendar.set(Calendar.SECOND, 0);       // 控制秒
//        long time1 = calendar.getTimeInMillis(); // 此处为00：00：00
//
//        Calendar calendar2 = Calendar.getInstance();
//        calendar2.set(Calendar.HOUR_OF_DAY, 14); // 控制时
//        calendar2.set(Calendar.MINUTE, 29);       // 控制分
//        calendar2.set(Calendar.SECOND, 59);       // 控制秒
//        long time2 = calendar2.getTimeInMillis(); // 此处为11：29：59
        for (int i = 0; i < list.size(); i++) {
            GrabMedium medium = (GrabMedium) list.get(i);
//            Date randomDate = randomDate(time1, time2);
//            medium.setTime(randomDate);
//            medium.setCost("Apple iPhone 11 Pro Max");
//            medium.setPrice(12699);
            UserInfo userInfo = null;
            userInfo = userInfoUtils.getUserInfo(medium.getUserId());
            if (userInfo != null) {
                medium.setName(userInfo.getName());
                medium.setProTypeId(userInfo.getProType());
                medium.setHead(userInfo.getHead());
                medium.setHouseNumber(userInfo.getHouseNumber());
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 查询自己的记录
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findMyList(@PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<GrabMedium> pageBean;
        pageBean = grabGiftsService.findOweList(CommonUtils.getMyId(), page, count);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 查询奖品
     * @return
     */
    @Override
    public ReturnData findGifts() {
        Random rand = new Random();
        String[] music = {"audio/musicServer/qiang/Music (1).mp3", "audio/musicServer/qiang/Music (2).mp3", "audio/musicServer/qiang/Music (3).mp3", "audio/musicServer/qiang/Music (4).mp3", "audio/musicServer/qiang/Music (5).mp3", "audio/musicServer/qiang/Music (6).mp3", "audio/musicServer/qiang/Music (7).mp3", "audio/musicServer/qiang/Music (8).mp3", "audio/musicServer/qiang/Music (9).mp3", "audio/musicServer/qiang/Music (10).mp3", "audio/musicServer/qiang/Music (11).mp3", "audio/musicServer/qiang/Music (12).mp3", "audio/musicServer/qiang/Music (13).mp3", "audio/musicServer/qiang/Music (14).mp3", "audio/musicServer/qiang/Music (15).mp3", "audio/musicServer/qiang/Music (16).mp3", "audio/musicServer/qiang/Music (17).mp3", "audio/musicServer/qiang/Music (18).mp3", "audio/musicServer/qiang/Music (19).mp3", "audio/musicServer/qiang/Music (20).mp3", "audio/musicServer/qiang/Music (21).mp3", "audio/musicServer/qiang/Music (22).mp3", "audio/musicServer/qiang/Music (23).mp3", "audio/musicServer/qiang/Music (24).mp3", "audio/musicServer/qiang/Music (25).mp3", "audio/musicServer/qiang/Music (26).mp3", "audio/musicServer/qiang/Music (27).mp3", "audio/musicServer/qiang/Music (28).mp3", "audio/musicServer/qiang/Music (29).mp3", "audio/musicServer/qiang/Music (30).mp3", "audio/musicServer/qiang/Music (31).mp3", "audio/musicServer/qiang/Music (32).mp3", "audio/musicServer/qiang/Music (33).mp3", "audio/musicServer/qiang/Music (34).mp3", "audio/musicServer/qiang/Music (35).mp3", " audio/musicServer/qiang/Music (36).mp3", "audio/musicServer/qiang/Music (37).mp3", "audio/musicServer/qiang/Music (38).mp3", "audio/musicServer/qiang/Music (39).mp3", "audio/musicServer/qiang/Music (40).mp3", "audio/musicServer/qiang/Music (41).mp3", "audio/musicServer/qiang/Music (42).mp3", "audio/musicServer/qiang/Music (43).mp3"};
        GrabGifts gifts = grabGiftsService.findGifts();
        if (gifts != null) {
//            gifts.setNumber(rand.nextInt(19) + 1);
            gifts.setMusic(music[rand.nextInt(music.length) + 0]);
            //查询当前用户当天剩余次数 以每天凌晨0点为准 每天每人只能抢三次
            int num = grabGiftsService.findNum(CommonUtils.getMyId());
            if (num >= 3) {
                gifts.setNum(0);
            } else {
                gifts.setNum(3 - num);
            }
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", gifts);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 生成随机时间
     * @param beginAnyTime
     * @param endAnyTime
     * @return
     */
    private static Date randomDate(long beginAnyTime, long endAnyTime) {
        try {
            if (beginAnyTime >= endAnyTime) {
                return null;
            }
            long date = random(beginAnyTime, endAnyTime);
            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long random(long begin, long end) {
        long rtn = begin + (long) (Math.random() * (end - begin));
        //如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
        if (rtn == begin || rtn == end) {
            return random(begin, end);
        }
        return rtn;
    }
}
