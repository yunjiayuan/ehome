package com.busi.controller.api;

import com.busi.entity.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 居委会相关接口
 * author：ZJJ
 * create time：2020-03-17 15:53:35
 */
public interface CommunityApiController {

    /***
     * 查询是否已加入居委会
     * @param userId
     * @return
     */
    @GetMapping("findJoinCommunity/{userId}")
    ReturnData findJoinCommunity(@PathVariable long userId);

    /***
     * 新增居委会
     * @param homeHospital
     * @return
     */
    @PostMapping("addCommunity")
    ReturnData addCommunity(@Valid @RequestBody Community homeHospital, BindingResult bindingResult);

    /***
     * 更新居委会
     * @param homeHospital
     * @return
     */
    @PutMapping("changeCommunity")
    ReturnData changeCommunity(@Valid @RequestBody Community homeHospital, BindingResult bindingResult);

    /***
     * 查询居委会详情
     * @param id
     * @return
     */
    @GetMapping("findCommunity/{id}")
    ReturnData findCommunity(@PathVariable long id);

    /***
     * 查询居委会列表
     * @param lon     经度
     * @param lat     纬度
     * @param string    模糊搜索
     * @param province     省
     * @param city      市
     * @param district    区
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findCommunityList/{lon}/{lat}/{string}/{province}/{city}/{district}/{page}/{count}")
    ReturnData findCommunityList(@PathVariable double lon, @PathVariable double lat, @PathVariable String string, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int page, @PathVariable int count);

    /***
     * 新增居民
     * @param homeHospital
     * @return
     */
    @PostMapping("addResident")
    ReturnData addResident(@Valid @RequestBody CommunityResident homeHospital, BindingResult bindingResult);

    /***
     * 更新居民
     * @param homeHospital
     * @return
     */
    @PutMapping("changeResident")
    ReturnData changeResident(@Valid @RequestBody CommunityResident homeHospital, BindingResult bindingResult);

    /**
     * @Description: 删除居民
     * @return:
     */
    @DeleteMapping("delResident/{ids}")
    ReturnData delResident(@PathVariable String ids);

    /***
     * 查询居民列表
     * @param communityId    居委会ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findResidentList/{communityId}/{page}/{count}")
    ReturnData findResidentList(@PathVariable int communityId, @PathVariable int page, @PathVariable int count);

    /***
     * 新增房屋
     * @param homeHospital
     * @return
     */
    @PostMapping("addHouse")
    ReturnData addHouse(@Valid @RequestBody CommunityHouse homeHospital, BindingResult bindingResult);

    /***
     * 更新房屋
     * @param homeHospital
     * @return
     */
    @PutMapping("changeHouse")
    ReturnData changeHouse(@Valid @RequestBody CommunityHouse homeHospital, BindingResult bindingResult);

    /***
     * 查询房屋详情
     * @param id
     * @return
     */
    @GetMapping("findHouse/{id}")
    ReturnData findHouse(@PathVariable long id);

    /**
     * @Description: 删除房屋
     * @return:
     */
    @DeleteMapping("delHouse/{ids}")
    ReturnData delHouse(@PathVariable String ids);

    /***
     * 查询房屋列表
     * @param communityId    居委会ID
     * @param userId    房主ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findHouseList/{communityId}/{userId}/{page}/{count}")
    ReturnData findHouseList(@PathVariable int communityId, @PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /***
     * 添加留言板
     * @param shopFloorComment
     * @return
     */
    @PostMapping("addMessageBoard")
    ReturnData addMessageBoard(@Valid @RequestBody CommunityMessageBoard shopFloorComment, BindingResult bindingResult);

    /***
     * 删除留言板
     * @param id 评论ID
     * @param communityId 居委会ID
     * @return
     */
    @DeleteMapping("delMessageBoard/{id}/{communityId}")
    ReturnData delMessageBoard(@PathVariable long id, @PathVariable long communityId);

    /***
     * 查询留言板记录
     * @param communityId     居委会ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findMessageBoardList/{communityId}/{page}/{count}")
    ReturnData findMessageBoardList(@PathVariable long communityId, @PathVariable int page, @PathVariable int count);

    /***
     * 查询留言板指定评论下的回复记录接口
     * @param contentId     评论ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findMessageBoardReplyList/{contentId}/{page}/{count}")
    ReturnData findMessageBoardReplyList(@PathVariable long contentId, @PathVariable int page, @PathVariable int count);

    /***
     * 添加事件报备
     * @param shopFloorComment
     * @return
     */
    @PostMapping("addEventReporting")
    ReturnData addEventReporting(@Valid @RequestBody CommunityEventReporting shopFloorComment, BindingResult bindingResult);

    /***
     * 更新事件报备
     * @param homeHospital
     * @return
     */
    @PutMapping("changeEventReporting")
    ReturnData changeEventReporting(@Valid @RequestBody CommunityEventReporting homeHospital, BindingResult bindingResult);

    /***
     * 查询事件报备详情
     * @param id
     * @return
     */
    @GetMapping("findEventReporting/{id}")
    ReturnData findEventReporting(@PathVariable long id);

    /***
     * 查询事件报备列表
     * @param roomId     房屋ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findEventReportingList/{roomId}/{page}/{count}")
    ReturnData findEventReportingList(@PathVariable long roomId, @PathVariable int page, @PathVariable int count);

    /***
     * 新增居委会人员设置
     * @param homeHospital
     * @return
     */
    @PostMapping("addSetUp")
    ReturnData addSetUp(@Valid @RequestBody CommunitySetUp homeHospital, BindingResult bindingResult);

    /***
     * 更新居委会人员设置
     * @param homeHospital
     * @return
     */
    @PutMapping("changeSetUp")
    ReturnData changeSetUp(@Valid @RequestBody CommunitySetUp homeHospital, BindingResult bindingResult);

    /**
     * @Description: 删除居委会人员设置
     * @return:
     */
    @DeleteMapping("delSetUp/{ids}")
    ReturnData delSetUp(@PathVariable String ids);

    /***
     * 查询居委会人员设置列表（按职务正序）
     * @param communityId    居委会ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findSetUpList/{communityId}/{page}/{count}")
    ReturnData findSetUpList(@PathVariable int communityId, @PathVariable int page, @PathVariable int count);
}
