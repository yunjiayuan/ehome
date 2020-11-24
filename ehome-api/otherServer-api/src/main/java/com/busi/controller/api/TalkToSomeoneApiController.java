package com.busi.controller.api;

import com.busi.entity.TalkToSomeone;
import com.busi.entity.ReturnData;
import com.busi.entity.TalkToSomeoneOrder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 找人倾诉相关接口
 * author：ZJJ
 * create time：2020-11-23 14:53:32
 */
public interface TalkToSomeoneApiController {

    /***
     * 查询详情
     * @param userId
     * @return
     */
    @GetMapping("findSomeone/{userId}")
    ReturnData findSomeone(@PathVariable long userId);

    /***
     * 新增倾诉信息
     * @param homeHospital
     * @return
     */
    @PostMapping("addSomeone")
    ReturnData addSomeone(@Valid @RequestBody TalkToSomeone homeHospital, BindingResult bindingResult);

    /***
     * 更新倾诉信息
     * @param homeHospital
     * @return
     */
    @PutMapping("changeSomeone")
    ReturnData changeSomeone(@Valid @RequestBody TalkToSomeone homeHospital, BindingResult bindingResult);

    /***
     * 倾诉
     * @param homeHospital
     * @return
     */
    @PostMapping("talkToSomeone")
    ReturnData talkToSomeone(@Valid @RequestBody TalkToSomeoneOrder homeHospital, BindingResult bindingResult);

    /***
     * 更新倾诉状态
     * @param homeHospital
     * @return
     */
    @PutMapping("changeSomeoneState")
    ReturnData changeSomeoneState(@Valid @RequestBody TalkToSomeoneOrder homeHospital, BindingResult bindingResult);

    /***
     * 查询推荐列表
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findSomeoneList/{page}/{count}")
    ReturnData findSomeoneList(@PathVariable int page, @PathVariable int count);

    /***
     * 查询记录列表
     * @param type   类型：0全部 1未倾诉 2已倾诉
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findSomeoneHistoryList/{type}/{page}/{count}")
    ReturnData findSomeoneHistoryList(@PathVariable int type, @PathVariable int page, @PathVariable int count);

}
