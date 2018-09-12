package com.busi.controller.api;

import com.busi.entity.Footprint;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 脚印接口
 * author：SunTianJie
 * create time：2018/9/12 19:02
 */
public interface FootprintApiController {

    /***
     * 更新离开时间
     * @param footprint
     * @return
     */
    @PutMapping("updateAwayTime")
    ReturnData updateAwayTime(@Valid @RequestBody Footprint footprint, BindingResult bindingResult);

    /***
     * 查询脚印记录和在家的人
     * @param userId       当前登录者ID
     * @param findType     历史脚印查询类型 当isOnlineType=1时有效 0查询自己被访问过的脚印记录  1查询自己访问过的脚印记录
     * @param isOnlineType 查询类型  0表示查询当时正在家的人  1表示查询历史脚印记录
     * @param page
     * @param count
     * @return
     */
    @GetMapping("findFootprintList/{userId}/{isOnlineType}/{findType}/{page}/{count}")
    ReturnData findFootprintList(@PathVariable long userId, @PathVariable int isOnlineType, @PathVariable int findType,
                                @PathVariable int page, @PathVariable int count);
}
