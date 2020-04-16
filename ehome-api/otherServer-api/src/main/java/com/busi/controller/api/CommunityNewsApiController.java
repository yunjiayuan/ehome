package com.busi.controller.api;

import com.busi.entity.CommunityNews;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 资讯相关接口
 * author：zhaojiajie
 * create time：2020-03-20 11:42:36
 */
public interface CommunityNewsApiController {

    /***
     * 新增
     * @param todayNews
     * @param bindingResult
     * @return
     */
    @PostMapping("addNews")
    ReturnData addNews(@Valid @RequestBody CommunityNews todayNews, BindingResult bindingResult);

    /**
     * @Description: 更新
     * @Param: todayNews
     * @return:
     */
    @PutMapping("editNews")
    ReturnData editNews(@Valid @RequestBody CommunityNews todayNews, BindingResult bindingResult);

    /**
     * @Description: 删除
     * @return:
     */
    @DeleteMapping("delNews/{id}")
    ReturnData delNews(@PathVariable long id);

    /***
     * 查询新闻列表
     * @param communityId newsType=0时为居委会ID  newsType=1时为物业ID
     * @param newsType 社区： 0居委会  1物业
     * @param noticeType 通告： 0资讯 1点对点通知通告（普通居民） 2内部人员通知
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findNewsList/{communityId}/{newsType}/{page}/{count}")
    ReturnData findNewsList(@PathVariable int noticeType, @PathVariable long communityId, @PathVariable int newsType, @PathVariable int page, @PathVariable int count);

    /***
     * 根据ID查询
     * @param infoId 资讯ID
     * @return
     */
    @GetMapping("findPress/{infoId}")
    ReturnData findPress(@PathVariable long infoId);

    /**
     * @Description: 删除浏览记录
     * @return:
     */
    @DeleteMapping("delLook/{ids}")
    ReturnData delLook(@PathVariable String ids);

    /***
     * 分页查询浏览记录接口
     * @param page
     * @param count
     * @return
     */
    @GetMapping("findLook/{id}/{page}/{count}")
    ReturnData findLook(@PathVariable long id, @PathVariable int page, @PathVariable int count);
}
