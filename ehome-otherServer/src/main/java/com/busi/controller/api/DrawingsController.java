package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.DrawingsService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import com.busi.utils.UserMembershipUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/***
 * 抽签相关接口
 * author：zhaojiajie
 * create time：2020-09-15 13:30:41
 */
@RestController
public class DrawingsController extends BaseController implements DrawingsApiController {


    @Autowired
    DrawingsService grabGiftsService;

    @Autowired
    UserMembershipUtils userMembershipUtils;

//    @Autowired
//    CrawlingSignUtils crawlingSign;

    /***
     * 新增抽签数据
     * @param drawings
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addDrawings(@Valid @RequestBody Drawings drawings, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //自定义新增
        grabGiftsService.addDrawings(drawings);
        //遍历网页新增
//            for (int i = 26; i <= 100; i++) {
//                Drawings drawings2 = new Drawings();
//                drawings.setSignNum(i);//阿拉伯签号
//                drawings2 = crawlingSign.run(drawings);
//                grabGiftsService.addDrawings(drawings2);
//            }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", drawings.getSignNum());
    }

    /***
     * 抽签
     * @param grabMedium
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData getDrawings(@Valid @RequestBody DrawingRecords grabMedium, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断是否是会员：会员不限次数
        UserMembership memberMap = userMembershipUtils.getUserMemberInfo(CommonUtils.getMyId());
        if (memberMap.getMemberShipStatus() <= 0) {
            //判断当前用户当天是否还有次数 以每天凌晨0点为准 每天每人只能抽三次
            int num = grabGiftsService.findNum(CommonUtils.getMyId());
            if (num >= 3) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "今天次数用尽，明天再来吧", new JSONObject());
            }
        }
        //开始抽
        Random rand = new Random();
        long id = rand.nextInt(100) + 1;
        Drawings gifts = grabGiftsService.findGifts(id);
        grabMedium.setTime(new Date());
        grabMedium.setDrawingId(gifts.getId());
        grabMedium.setSignature(gifts.getAllusionName());
        grabMedium.setUserId(CommonUtils.getMyId());
        grabGiftsService.add(grabMedium);
        Map<String, Object> map = new HashMap<>();
        map.put("id", gifts.getId());//签子ID
//        int last = gifts.getSign().indexOf("签");
//        map.put("number", gifts.getSign().substring(0, last));//中文签号
        map.put("number", gifts.getAllusionName());//典故名称
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查询剩余次数
     * @return
     */
    @Override
    public ReturnData findDrawingsNum() {
        //查询当前用户当天剩余次数 以每天凌晨0点为准 每天每人只能抢三次
        int num = grabGiftsService.findNum(CommonUtils.getMyId());
        if (num >= 3) {
            num = 0;
        } else {
            num = 3 - num;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("num", num);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查询详情
     * @return
     */
    @Override
    public ReturnData findDrawings(@PathVariable long id) {
        Drawings gifts = grabGiftsService.findGifts(id);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", gifts);
    }

    /****
     * 查询抽签记录
     * @param userId
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findDrawingsList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<DrawingRecords> pageBean;
        pageBean = grabGiftsService.findOweList(userId, page, count);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
