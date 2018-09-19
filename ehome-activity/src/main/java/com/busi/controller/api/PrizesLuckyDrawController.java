package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.PrizesLuckyDrawService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import com.busi.utils.UserInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: ehome
 * @description: 赢大奖相关接口实现
 * @author: ZHaoJiaJie
 * @create: 2018-09-17 13:37
 */
@RestController
public class PrizesLuckyDrawController extends BaseController implements PrizesLuckyDrawApiController {

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    PrizesLuckyDrawService prizesLuckyDrawService;

    /***
     * 参加活动
     * @param issue 期数
     * @return
     */
    @Override
    public ReturnData participateIn(@PathVariable int issue) {
        //验证参数
        if (issue <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "issue参数有误", new JSONObject());
        }
        //判断该用户是否绑定手机(暂留)

        PrizesLuckyDraw prizesLuckyDraw = null;
        PrizesLuckyDraw luckyDraw = new PrizesLuckyDraw();
        prizesLuckyDraw = prizesLuckyDrawService.findIn(CommonUtils.getMyId(), issue);
        if (prizesLuckyDraw != null) {
            return returnData(StatusCode.CODE_ALREADY_JOIN.CODE_VALUE, "用户：[ " + CommonUtils.getMyId() + "]已参与第[" + issue + "]期活动！", new JSONObject());
        }
        //开始抽奖
        int awardsId = 0;//奖品ID：0背包  1便携音箱  2豆浆机  3精美餐具  4动漫模型  5酒红石榴石手链  6女士太阳镜  7剃须刀  8头戴式耳机  9榨汁机
        String[] cost = {"背包", "便携音箱", "豆浆机", "精美餐具", "动漫模型", "酒红石榴石手链", "女士太阳镜", "剃须刀", "头戴式耳机", "榨汁机"};
        Random rand = new Random();
        awardsId = rand.nextInt(100);
        if (awardsId < 10) {
            luckyDraw.setGrade(2);
            luckyDraw.setPrize(awardsId);
            luckyDraw.setCost(cost[awardsId]);
            luckyDraw.setWinningState(1);//中奖状态 0没中 1中奖未领奖  2 已领奖
        } else {
            luckyDraw.setGrade(0);
            luckyDraw.setPrize(0);
            luckyDraw.setCost("谢谢参与");
            luckyDraw.setWinningState(0);//中奖状态 0没中 1中奖未领奖  2 已领奖
        }
        luckyDraw.setUserId(CommonUtils.getMyId());
        luckyDraw.setIssue(issue);
        luckyDraw.setTime(new Date());
        prizesLuckyDrawService.addLucky(luckyDraw);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 领奖
     * @param infoId  领奖ID
     * @return
     */
    @Override
    public ReturnData takeThePrize(@PathVariable long infoId, @PathVariable String address, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable String contactsName, @PathVariable String contactsPhone, @PathVariable String postalcode) {

        PrizesLuckyDraw draw = null;
        draw = prizesLuckyDrawService.findWinning(CommonUtils.getMyId(), infoId);
        if (draw != null) {
            PrizesReceipt prize = new PrizesReceipt();
            if (draw.getGrade() == 1) {//一等奖
                PrizesEvent e = null;
                e = prizesLuckyDrawService.findEvent(draw.getIssue());
                if (e != null) {
                    prize.setPrice(e.getPrice());
                    prize.setIssue(e.getIssue());
                    prize.setImgUrl(e.getImgUrl());
                    prize.setCostName(e.getName());
                    prize.setDescribe(e.getDescribe());

                }
            } else {//二等奖
                PrizesMemorial e = null;
                e = prizesLuckyDrawService.findMemorial(draw.getIssue(), draw.getCost());
                if (e != null) {
                    prize.setImgUrl(e.getImgUrl());
                    prize.setPrice(e.getPrice());
                    prize.setIssue(e.getIssue());
                    prize.setCostName(e.getName());
                    prize.setDescribe(e.getDescribe());
                }
            }
            prize.setAddress(address);
            prize.setAddTime(new Date());
            prize.setCity(city);
            prize.setDistrict(district);
            prize.setProvince(province);
            prize.setContactsName(contactsName);
            prize.setContactsPhone(contactsPhone);
            prize.setPostalcode(postalcode);
            prize.setUserId(draw.getUserId());
            prizesLuckyDrawService.addReceipt(prize);
            draw.setWinningState(2);
            prizesLuckyDrawService.updateDraw(draw);

        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询中奖人员列表
     * @param issue  期数
     * @param grade  奖品等级：1一等奖 2纪念奖
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findWinningList(@PathVariable int issue, @PathVariable int grade, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        List list = null;
        List listp = null;
        PrizesLuckyDraw t = null;
        PageBean<PrizesLuckyDraw> pageBean;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
        list = prizesLuckyDrawService.findGradeEvent(issue, grade);
        pageBean = prizesLuckyDrawService.findGradeList(issue, grade, page, count);
        listp = pageBean.getList();
        if (list == null || list.size() <= 0 || listp == null || listp.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        if (listp != null && listp.size() > 0) {
            for (int i = 0; i < listp.size(); i++) {
                t = (PrizesLuckyDraw) listp.get(i);
                if (t != null) {
                    UserInfo userInfo = null;
                    userInfo = userInfoUtils.getUserInfo(t.getUserId());
                    if (userInfo != null) {
                        t.setName(userInfo.getName());
                        t.setProTypeId(userInfo.getProType());
                        t.setHead(userInfo.getHead());
                        t.setHouseNumber(userInfo.getHouseNumber());
                    }
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            PrizesEvent prizes = (PrizesEvent) list.get(i);
            if (prizes != null) {
                Date end = null;
                Date start = null;
                try {
                    end = format.parse(prizes.getEndTime());
                    start = format.parse(prizes.getStartTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String startDate = format1.format(start);
                String endDate = format1.format(end);
                prizes.setEndTime(endDate);
                prizes.setStartTime(startDate);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("prizeData", list);
        map.put("data", listp);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查询自己奖品
     * @param openTime 开奖时间
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findOwnList(@PathVariable String openTime, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        int issue = 0;
        List list = null;
        List eventList = null;
        PrizesEvent eventPrizes = null;
        PrizesLuckyDraw draw = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        long nowTime = new Date().getTime();// 当前时间
        long countTime = 24 * 60 * 60 * 1000 * 2;// 48小时
        PageBean<PrizesLuckyDraw> pageBean;
        pageBean = prizesLuckyDrawService.findOweList(CommonUtils.getMyId(), page, count);
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            eventList = prizesLuckyDrawService.findOpenTime(openTime);
            if (eventPrizes != null && eventList.size() > 0) {
                eventPrizes = (PrizesEvent) eventList.get(0);
                issue = eventPrizes.getIssue();//最新一期期号
                for (int i = 0; i < list.size(); i++) {
                    draw = (PrizesLuckyDraw) list.get(i);
                    if (draw.getIssue() == issue) {
                        list.remove(i);
                        for (int j = 0; j < list.size(); j++) {
                            draw = (PrizesLuckyDraw) list.get(j);
                            if (draw.getWinningState() == 1) {
                                eventPrizes = (PrizesEvent) prizesLuckyDrawService.findAppointEvent(draw.getIssue());
                                if (eventPrizes != null) {
                                    Date date = null;
                                    try {
                                        date = format.parse(eventPrizes.getEndTime());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    long openTime1 = date.getTime();//开奖时间
                                    if (openTime1 <= nowTime - countTime) {//是否领奖过期
                                        draw.setWinningState(3);
                                        prizesLuckyDrawService.updateDraw(draw);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 查询最新一期奖品
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findNewList(@PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //判断该用户是否绑定手机(暂留)

        int binding = 1;//是否绑定手机：1是  0 否
        int is = 1;//0已参加 1未参加
        List list = null;
        PrizesLuckyDraw draw = null;
        PageBean<PrizesEvent> pageBean;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
        int time = Integer.valueOf(format.format(new Date()));//当前系统时间(Date转int)
        pageBean = prizesLuckyDrawService.findNew(time, page, count);
        list = pageBean.getList();
        if (list.size() > 0 && list != null) {
            for (int i = 0; i < list.size(); i++) {
                PrizesEvent prizes = (PrizesEvent) list.get(i);
                if (prizes != null) {
                    Date end = null;
                    Date start = null;
                    try {
                        start = format.parse(prizes.getStartTime());
                        end = format.parse(prizes.getEndTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String startDate = format1.format(start);
                    String endDate = format1.format(end);
                    prizes.setEndTime(endDate);
                    prizes.setStartTime(startDate);
                }
            }
            //查询是否已参加
            PrizesEvent prizes1 = (PrizesEvent) list.get(0);
            if (prizes1 != null) {
                draw = prizesLuckyDrawService.findIn(CommonUtils.getMyId(), prizes1.getIssue());
                if (draw != null) {
                    is = 0;
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("data", list);
        map.put("is", is);
        map.put("binding", binding);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查询最新一期纪念奖奖品
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findMemorialList(@PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        List list = null;
        PageBean<PrizesEvent> pageBean;
        PageBean<PrizesMemorial> pagePrizes = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        int time = Integer.valueOf(format.format(new Date()));//当前系统时间(Date转int)
        pageBean = prizesLuckyDrawService.findNew(time, page, count);
        list = pageBean.getList();
        if (list.size() > 0 && list != null) {
            PrizesEvent prizes = (PrizesEvent) list.get(0);
            if (prizes != null) {
                pagePrizes = prizesLuckyDrawService.findIssueMemorial(prizes.getIssue(), page, count);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pagePrizes);
    }
}
