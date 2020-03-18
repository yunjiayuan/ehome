package com.busi.controller.api;

import com.busi.entity.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 社区房屋相关接口
 * author：suntj
 * create time：2020-03-17 15:53:35
 */
public interface CommunityHouseApiController {

    /***
     * 新增房屋
     * @param communityHouse
     * @return
     */
    @PostMapping("addCommunityHouse")
    ReturnData addHouse(@Valid @RequestBody CommunityHouse communityHouse, BindingResult bindingResult);

    /***
     * 更新房屋
     * @param communityHouse
     * @return
     */
    @PutMapping("changeCommunityHouse")
    ReturnData changeHouse(@Valid @RequestBody CommunityHouse communityHouse, BindingResult bindingResult);

    /***
     * 查询房屋详情
     * @param id
     * @return
     */
    @GetMapping("findCommunityHouse/{id}")
    ReturnData findHouse(@PathVariable long id);

    /**
     * @Description: 删除房屋
     * @return:
     */
    @DeleteMapping("delCommunityHouse/{ids}")
    ReturnData delHouse(@PathVariable String ids);

    /***
     * 查询房屋列表
     * @param communityId    居委会ID
     * @param userId    房主ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findCommunityHouseList/{communityId}/{userId}/{page}/{count}")
    ReturnData findHouseList(@PathVariable int communityId, @PathVariable long userId, @PathVariable int page, @PathVariable int count);

}
