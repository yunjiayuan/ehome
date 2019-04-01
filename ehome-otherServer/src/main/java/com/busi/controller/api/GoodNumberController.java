package com.busi.controller.api;

import com.busi.controller.BaseController;
import com.busi.entity.GoodNumber;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.service.GoodNumberService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 预售靓号相关业务接口
 * author：suntj
 * create time：2019-3-28 18:39:46
 */
@RestController
public class GoodNumberController extends BaseController implements GoodNumberApiController {

    @Autowired
    GoodNumberService goodNumberService;

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
}
