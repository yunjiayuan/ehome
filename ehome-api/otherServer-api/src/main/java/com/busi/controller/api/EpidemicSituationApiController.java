package com.busi.controller.api;


import com.busi.entity.CampaignAwardActivity;
import com.busi.entity.CampaignAwardVote;
import com.busi.entity.EpidemicSituationAbout;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 疫情相关接口
 * author：zhaojiajie
 * create time：2020-02-15 10:40:23
 */
public interface EpidemicSituationApiController {

    /***
     * 查询疫情
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findEpidemicSituation/{page}/{count}")
    ReturnData findEpidemicSituation(@PathVariable int page, @PathVariable int count);

    /***
     * 新增我和疫情
     * @param epidemicSituationAbout
     * @return
     */
    @PostMapping("addESabout")
    ReturnData addESabout(@Valid @RequestBody EpidemicSituationAbout epidemicSituationAbout, BindingResult bindingResult);

    /***
     * 编辑我和疫情
     * @param epidemicSituationAbout
     * @return
     */
    @PutMapping("changeESabout")
    ReturnData changeESabout(@Valid @RequestBody EpidemicSituationAbout epidemicSituationAbout, BindingResult bindingResult);

    /***
     * 查询我和疫情信息
     * @param userId
     * @return
     */
    @GetMapping("findESabout/{userId}")
    ReturnData findESabout(@PathVariable long userId);

    /***
     * 参加活动
     * @param selectionActivities
     * @param bindingResult
     * @return
     */
    @PostMapping("joinCampaignAward")
    ReturnData joinCampaignAward(@Valid @RequestBody CampaignAwardActivity selectionActivities, BindingResult bindingResult);

    /**
     * @Description: 更新活动信息
     * @Param: selectionActivities
     * @return:
     */
    @PutMapping("editCampaignAward")
    ReturnData editCampaignAward(@Valid @RequestBody CampaignAwardActivity selectionActivities, BindingResult bindingResult);

    /**
     * @Description: 删除
     * @return:
     */
    @DeleteMapping("delCampaignAward/{id}")
    ReturnData delCampaignAward(@PathVariable long id);

    /***
     * 查询活动的详细信息
     * @param id
     * @return
     */
    @GetMapping("findCampaignAward/{id}")
    ReturnData findCampaignAward(@PathVariable long id);

    /***
     * 分页查询活动列表
     * @param findType   查询类型： 0表示默认全部，1表示查询有视频的  2按编号查询
     * @param orderVoteCountType  排序规则 0按票数从高到低 1按票数从低到高
     * @param province  省ID -1不限
     * @param city   市ID -1不限
     * @param district  区ID -1不限
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findCampaignAwardList/{findType}/{orderVoteCountType}/{province}/{city}/{district}/{page}/{count}")
    ReturnData findCampaignAwardList(@PathVariable int findType, @PathVariable int orderVoteCountType,
                                     @PathVariable int province, @PathVariable int city, @PathVariable int district,
                                     @PathVariable int page, @PathVariable int count);

    /***
     * 投票
     * @param selectionVote
     * @param bindingResult
     * @return
     */
    @PostMapping("voteCampaignAward")
    ReturnData voteCampaignAward(@Valid @RequestBody CampaignAwardVote selectionVote, BindingResult bindingResult);

}
