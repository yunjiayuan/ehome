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
     * 更新居委会刷新时间
     * @param homeHospital
     * @return
     */
    @PutMapping("changeCommunityTime")
    ReturnData changeCommunityTime(@Valid @RequestBody CommunityResident homeHospital, BindingResult bindingResult);

    /***
     * 查询居委会详情
     * @param id
     * @return
     */
    @GetMapping("findCommunity/{id}")
    ReturnData findCommunity(@PathVariable long id);

    /***
     * 查询居委会列表
     * @param userId    用户ID
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
    @GetMapping("findCommunityList/{userId}/{lon}/{lat}/{string}/{province}/{city}/{district}/{page}/{count}")
    ReturnData findCommunityList(@PathVariable long userId, @PathVariable double lon, @PathVariable double lat, @PathVariable String string, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int page, @PathVariable int count);

    /***
     * 新增居民
     * @param homeHospital
     * @return
     */
    @PostMapping("addResident")
    ReturnData addResident(@Valid @RequestBody CommunityResident homeHospital, BindingResult bindingResult);

    /***
     * 更新居民权限
     * @param homeHospital
     * @return
     */
    @PutMapping("changeResident")
    ReturnData changeResident(@Valid @RequestBody CommunityResident homeHospital, BindingResult bindingResult);

    /***
     * 更新居民标签
     * @param homeHospital
     * @return
     */
    @PutMapping("changeResidentTag")
    ReturnData changeResidentTag(@Valid @RequestBody CommunityResident homeHospital, BindingResult bindingResult);

    /***
     * 删除居民
     * @param type 0删除居民  1删除管理员
     * @return:
     */
    @DeleteMapping("delResident/{type}/{ids}/{communityId}")
    ReturnData delResident(@PathVariable int type, @PathVariable String ids, @PathVariable long communityId);


    /***
     * 查询居民详情
     * @param communityId
     * @return
     */
    @GetMapping("findResiden/{communityId}/{homeNumber}")
    ReturnData findResiden(@PathVariable long communityId, @PathVariable String homeNumber);

    /***
     * 查询居民列表
     * @param type    0所有人  1管理员
     * @param communityId    居委会ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findResidentList/{type}/{communityId}/{page}/{count}")
    ReturnData findResidentList(@PathVariable int type, @PathVariable long communityId, @PathVariable int page, @PathVariable int count);

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
    @GetMapping("findMessageBoardList/{type}/{communityId}/{page}/{count}")
    ReturnData findMessageBoardList(@PathVariable int type, @PathVariable long communityId, @PathVariable int page, @PathVariable int count);

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
    ReturnData findSetUpList(@PathVariable long communityId, @PathVariable int page, @PathVariable int count);
}
