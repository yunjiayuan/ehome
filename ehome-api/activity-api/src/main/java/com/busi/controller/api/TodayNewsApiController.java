package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.TodayNews;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 资讯相关接口
 * author：zhaojiajie
 * create time：2018-9-27 11:40:30
 */
public interface TodayNewsApiController {

    /***
     * 新增
     * @param todayNews
     * @param bindingResult
     * @return
     */
    @PostMapping("addNews")
    ReturnData addNews(@Valid @RequestBody TodayNews todayNews, BindingResult bindingResult);

    /**
     * @Description: 更新
     * @Param: todayNews
     * @return:
     */
    @PutMapping("editNews")
    ReturnData editNews(@Valid @RequestBody TodayNews todayNews, BindingResult bindingResult);

    /**
     * @Description: 删除
     * @return:
     */
    @DeleteMapping("delNews/{id}")
    ReturnData delNews(@PathVariable long id);

    /***
     * 查询新闻列表
     * @param newsType 发布新闻类型0今日人物  1今日企业  2今日新闻
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findNewsList/{newsType}/{page}/{count}")
    ReturnData findNewsList(@PathVariable int newsType, @PathVariable int page, @PathVariable int count);

    /***
     * 根据ID查询
     * @param infoId 资讯ID
     * @return
     */
    @GetMapping("findPress/{infoId}")
    ReturnData findPress(@PathVariable long infoId);
}
