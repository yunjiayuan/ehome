package com.busi.controller.api;

import com.busi.entity.GoodNumberOrder;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 预售靓号相关业务接口
 * author：suntj
 * create time：2019-3-28 18:39:46
 */
public interface GoodNumberApiController {

//    /***
//     * 精确查找预售账号（根据省简称ID+门牌号查询）
//     * @param proId        省简称ID
//     * @param house_number 门牌号ID
//     * @return
//     */
//    @GetMapping("findGoodNumberInfo/{proId}/{house_number}")
//    ReturnData findGoodNumberInfo(@PathVariable int proId,@PathVariable long house_number);

    /***
     * 模糊查找预售账号（根据省简称ID+门牌号查询）
     * @param proId        省简称ID
     * @param house_number 门牌号ID
     * @return
     */
    @GetMapping("findGoodNumberListByNumber/{proId}/{house_number}/{page}/{count}")
    ReturnData findGoodNumberListByNumber(@PathVariable int proId,@PathVariable long house_number, @PathVariable int page, @PathVariable int count);


    /***
     * 条件查询预售靓号列表
     * @param proId       省简称ID 默认-1不限
     * @param theme       主题ID 默认-1不限
     * @param label       数字规则ID 默认null不限
     * @param numberDigit 靓号位数ID 默认-1不限 (例如7表示7位)
     * @param orderType   省简称ID 默认 0不限 1按价格倒序 2按价格升序
     * @param page
     * @param count
     * @return
     */
    @GetMapping("findGoodNumberList/{proId}/{theme}/{label}/{numberDigit}/{orderType}/{page}/{count}")
    ReturnData findGoodNumberList(@PathVariable int proId,@PathVariable int theme,@PathVariable String label,@PathVariable int numberDigit,@PathVariable int orderType, @PathVariable int page, @PathVariable int count);


    /***
     *靓号下单接口
     * @param goodNumberOrder
     * @return
     */
    @PostMapping("addGoodNumberOrder")
    ReturnData addGoodNumberOrder(@RequestBody GoodNumberOrder goodNumberOrder, BindingResult bindingResult);
}
