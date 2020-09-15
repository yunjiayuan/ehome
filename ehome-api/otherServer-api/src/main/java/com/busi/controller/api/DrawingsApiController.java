package com.busi.controller.api;

import com.busi.entity.DrawingRecords;
import com.busi.entity.Drawings;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/***
 * 抽签相关接口
 * author：zhaojiajie
 * create time：2020-09-15 13:30:41
 */
public interface DrawingsApiController {

    /***
     * 抽签
     * @param grabMedium
     * @return
     */
    @PostMapping("getDrawings")
    ReturnData getDrawings(@Valid @RequestBody DrawingRecords grabMedium, BindingResult bindingResult);

    /***
     * 查询剩余次数
     * @return
     */
    @GetMapping("findDrawingsNum")
    ReturnData findDrawingsNum();

    /***
     * 查询抽签详情
     * @param id  签子ID
     * @return
     */
    @GetMapping("findDrawings/{id}")
    ReturnData findDrawings(@PathVariable long id);

    /***
     * 查询抽签记录
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findDrawingsList/{userId}/{page}/{count}")
    ReturnData findDrawingsList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);

}
