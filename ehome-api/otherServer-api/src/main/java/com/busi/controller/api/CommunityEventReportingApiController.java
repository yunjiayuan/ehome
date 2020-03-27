package com.busi.controller.api;

import com.busi.entity.CommunityEventReporting;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 社区新冠状病毒事件报备相关接口
 * author：suntj
 * create time：2020-03-17 15:53:35
 */
public interface CommunityEventReportingApiController {

    /***
     * 新增新冠状病毒报备
     * @param communityEventReporting
     * @return
     */
    @PostMapping("addCommunityEventReporting")
    ReturnData addCommunityEventReporting(@Valid @RequestBody CommunityEventReporting communityEventReporting, BindingResult bindingResult);

    /***
     * 更新新冠状病毒报备
     * @param communityEventReporting
     * @return
     */
    @PutMapping("changeCommunityEventReporting")
    ReturnData changeCommunityEventReporting(@Valid @RequestBody CommunityEventReporting communityEventReporting, BindingResult bindingResult);

    /***
     * 审核新冠状病毒报备
     * @param communityEventReporting
     * @return
     */
    @PutMapping("toExamineCommunityEventReporting")
    ReturnData toExamineCommunityEventReporting(@Valid @RequestBody CommunityEventReporting communityEventReporting, BindingResult bindingResult);

    /***
     * 根据ID查询新冠状病毒报备详情
     * @param id
     * @return
     */
    @GetMapping("findCommunityEventReportin/{id}")
    ReturnData findCommunityEventReportin(@PathVariable long id);

    /**
     * @Description: 删除新冠状病毒报备
     * @return:
     */
    @DeleteMapping("delCommunityEventReportin/{ids}")
    ReturnData delCommunityEventReportin(@PathVariable String ids);

    /***
     * 查询新冠状病毒报备列表
     * @param communityId    居委会ID
     * @param userId   业主的用户ID
     * @param communityHouseId   房屋ID 大于0时 为查询指定用户的报备信息
     * @param type     -1表示查询所有 0表示查询未审核 1表示查询已审核 2表示查询审核失败
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findCommunityEventReportinList/{communityId}/{userId}/{communityHouseId}/{type}/{page}/{count}")
    ReturnData findHouseList(@PathVariable long communityId, @PathVariable long userId,@PathVariable long communityHouseId,@PathVariable int type, @PathVariable int page, @PathVariable int count);

}
