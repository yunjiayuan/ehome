package com.busi.controller.api;


import com.busi.entity.CloudVideo;
import com.busi.entity.CloudVideoActivities;
import com.busi.entity.CloudVideoVote;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 云视频相关接口
 * author：zhaojiajie
 * create time：2019-3-20 15:53:38
 */
public interface CloudVideoApiController {

    /***
     * 上传视频
     * @param cloudVideo
     * @param bindingResult
     * @return
     */
    @PostMapping("uploadCloudVideo")
    ReturnData uploadCloudVideo(@Valid @RequestBody CloudVideo cloudVideo, BindingResult bindingResult);

    /**
     * @Description: 删除视频
     * @return:
     */
    @DeleteMapping("delCloudVideo/{id}")
    ReturnData delCloudVideo(@PathVariable long id);

    /**
     * @Description: 退出活动
     * @return:
     */
    @GetMapping("outCloudVideo/{id}")
    ReturnData outCloudVideo(@PathVariable long id);

    /***
     * 分页查询用户的云视频列表
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findCloudVideoList/{searchType}/{userId}/{page}/{count}")
    ReturnData findCloudVideoList(@PathVariable int searchType, @PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /***
     * 参加活动
     * @param cloudVideoActivities
     * @param bindingResult
     * @return
     */
    @PostMapping("joinCloudVideo")
    ReturnData joinCloudVideo(@Valid @RequestBody CloudVideoActivities cloudVideoActivities, BindingResult bindingResult);

    /**
     * 查询用户是否参加过活动
     *
     * @param selectionType 活动分类 0云视频  (后续添加)
     * @return
     */
    @GetMapping("judgeJoin/{selectionType}")
    ReturnData judgeJoin(@PathVariable int selectionType);

    /***
     * 分页查询参加活动的人员
     * @param selectionType  活动分类 0云视频  (后续添加)
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findPersonnel/{selectionType}/{page}/{count}")
    ReturnData findPersonnel(@PathVariable int selectionType, @PathVariable int page, @PathVariable int count);

    /***
     * 投票
     * @param cloudVideoVote
     * @param bindingResult
     * @return
     */
    @PostMapping("cloudVideoVote")
    ReturnData cloudVideoVote(@Valid @RequestBody CloudVideoVote cloudVideoVote, BindingResult bindingResult);

    /***
     * 分页查询投票历史
     * @param userId  用户ID
     * @param selectionType  活动分类 0云视频  (后续添加)
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findCloudVoteList/{userId}/{selectionType}/{page}/{count}")
    ReturnData findCloudVoteList(@PathVariable long userId, @PathVariable int selectionType, @PathVariable int page, @PathVariable int count);

}
