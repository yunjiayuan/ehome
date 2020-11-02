package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.ZhouGongDreamService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import com.busi.utils.UserMembershipUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 抽签相关接口
 * author：zhaojiajie
 * create time：2020-11-02 12:03:41
 */
@RestController
public class ZhouGongDreamController extends BaseController implements ZhouGongDreamApiController {

    @Autowired
    ZhouGongDreamService grabGiftsService;

    @Autowired
    UserMembershipUtils userMembershipUtils;

    /***
     * 查询剩余次数
     * @return
     */
    @Override
    public ReturnData findDreamNum() {
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
     * 查询解梦详情
     * @param id  梦ID
     * @return
     */
    @Override
    public ReturnData findDreams(@PathVariable long id) {
        ZhouGongDream gifts = grabGiftsService.findGifts(id);
        ZhouGongDreamRecords grabMedium = new ZhouGongDreamRecords();
        grabMedium.setTime(new Date());
        grabMedium.setDreamId(gifts.getId());
        grabMedium.setTitle(gifts.getTitle());
        grabMedium.setUserId(CommonUtils.getMyId());
        grabGiftsService.add(grabMedium);

        //整理数据
        List list = grabGiftsService.findList();
        for (int i = 0; i < list.size(); i++) {
            ZhouGongDream dream = (ZhouGongDream) list.get(i);
            if (dream != null) {
                String s = dream.getMessage();
                if (!CommonUtils.checkFull(s)) {
                    int index = s.indexOf("</p>");
                    String newS = s.substring(index);
                    dream.setMessage(newS);
                    grabGiftsService.update(dream);
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", gifts);
    }

    /***
     * 查询二级分类
     * @param biglx 一级分类
     * @return
     */
    @Override
    public ReturnData findDreamsTwoSort(@PathVariable String biglx) {
        ZhouGongDreamSort gifts = grabGiftsService.findDreamsTwoSort(biglx);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", gifts);
    }

    /***
     * 条件查询
     * @param title 关键字
     * @param biglx 一级分类 ：人物、动物、植物、物品、活动、生活、自然、鬼神、建筑、其它
     * @param smalllx 二级分类 null查所有
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findDreamsSortList(@PathVariable String title, @PathVariable String biglx, @PathVariable String smalllx, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<ZhouGongDream> pageBean;
        pageBean = grabGiftsService.findDreamsSortList(title, biglx, smalllx, page, count);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 查询解梦记录
     * @param userId
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findDreamsList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<ZhouGongDreamRecords> pageBean;
        pageBean = grabGiftsService.findOweList(userId, page, count);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
