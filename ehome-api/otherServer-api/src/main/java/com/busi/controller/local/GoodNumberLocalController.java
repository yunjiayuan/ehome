package com.busi.controller.local;

import com.busi.entity.GoodNumber;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.*;

/**
 * 预售靓号相关业务接口(内部调用)
 * author：suntj
 * create time：2019-3-28 18:39:46
 */
public interface GoodNumberLocalController {

    /***
     * 自动生成指定区间内的靓号
     * @param beginNumber 起始门牌号（包含）
     * @param endNumber   结束门牌号（包含）
     * @return
     */
    @GetMapping("addGoodNumber/{beginNumber}/{endNumber}")
    ReturnData findGoodNumberListByNumber(@PathVariable long beginNumber,@PathVariable long endNumber);


    /***
     * 更新靓号状态
     * @param goodNumber
     * @return
     */
    @PutMapping("updateGoodNumber")
    ReturnData updateGoodNumber(@RequestBody GoodNumber goodNumber);


}
