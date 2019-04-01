package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.GoodNumber;
import com.busi.entity.GoodNumberOrder;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.service.GoodNumberService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 预售靓号相关业务接口
 * author：suntj
 * create time：2019-3-28 18:39:46
 */
@RestController
public class GoodNumberController extends BaseController implements GoodNumberApiController {

    @Autowired
    GoodNumberService goodNumberService;

    @Autowired
    RedisUtils redisUtils;

//    /***
//     * 精确查找预售账号（根据省简称ID+门牌号查询）
//     * @param proId        省简称ID
//     * @param house_number 门牌号ID
//     * @return
//     */
//    @Override
//    public ReturnData findGoodNumberInfo(@PathVariable int proId,@PathVariable long house_number) {
//        GoodNumber goodNumber = goodNumberService.findGoodNumberInfo(proId,house_number);
//        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", goodNumber);
//    }

    /***
     * 模糊查找预售账号（根据省简称ID+门牌号查询）
     * @param proId        省简称ID
     * @param house_number 门牌号ID
     * @return
     */
    @Override
    public ReturnData findGoodNumberListByNumber(@PathVariable int proId,@PathVariable long house_number, @PathVariable int page,@PathVariable int count) {
        PageBean<GoodNumber> pageBean;
        pageBean = goodNumberService.findGoodNumberListByNumber(proId,house_number,page,count);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 条件查询预售靓号列表
     * @param proId       省简称ID 默认-1不限
     * @param theme       主题ID 默认-1不限
     * @param label       数字规则ID 默认null不限
     * @param numberDigit 靓号位数ID 默认-1不限 (例如7表示7位)
     * @param orderType   排序规则 默认 0不限 1按价格倒序 2按价格升序
     * @param page
     * @param count
     * @return
     */
    @Override
    public ReturnData findGoodNumberList(@PathVariable int proId,@PathVariable int theme,@PathVariable String label,
                                         @PathVariable int numberDigit,@PathVariable int orderType,
                                         @PathVariable int page,@PathVariable int count) {
        PageBean<GoodNumber> pageBean;
        pageBean =goodNumberService.findList(proId, theme, label, numberDigit, orderType, page, count);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }

    /***
     *靓号下单接口
     * @param goodNumberOrder
     * @return
     */
    @Override
    public ReturnData addGoodNumberOrder(@RequestBody GoodNumberOrder goodNumberOrder, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != goodNumberOrder.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限使用用户[" + goodNumberOrder.getUserId() + "]的购买靓号", new JSONObject());
        }
        GoodNumber goodNumber = goodNumberService.findGoodNumberInfo(goodNumberOrder.getProId(),goodNumberOrder.getHouse_number());
        if(goodNumber==null){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "很抱歉，该账号已经卖出!", new JSONObject());
        }
        Date time = new Date();
        String random = CommonUtils.getRandom(6, 1);
        String noRandom = CommonUtils.strToMD5(time.getTime()+""+ CommonUtils.getMyId() + random, 16);
        goodNumberOrder.setOrderNumber(noRandom);
        goodNumberOrder.setTime(time);
        goodNumberOrder.setMoney(goodNumber.getGoodNumberPrice());
        //放入缓存 5分钟
        redisUtils.hmset(Constants.REDIS_KEY_GOODNUMBER_ORDER + CommonUtils.getMyId() + "_" + goodNumberOrder.getOrderNumber(), CommonUtils.objectToMap(goodNumberOrder), Constants.TIME_OUT_MINUTE_5);
        Map<String, Object> map = new HashMap<>();
        map.put("orderNumber",noRandom);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
