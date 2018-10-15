package com.busi.controller.api;

import com.busi.entity.Notepad;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 记事本相关接口
 * author：zhaojiajie
 * create time：2018-10-11 11:09:44
 */
public interface NotepadApiController {


    /***
     * 新增
     * @param notepad
     * @param bindingResult
     * @return
     */
    @PostMapping("addNotepad")
    ReturnData addNotepad(@Valid @RequestBody Notepad notepad, BindingResult bindingResult);

    /**
     * @Description: 更新记事
     * @Param: notepad
     * @return:
     */
    @PutMapping("editNotepad")
    ReturnData editNotepad(@Valid @RequestBody Notepad notepad, BindingResult bindingResult);

    /**
     * @Description: 删除
     * @return:
     */
    @DeleteMapping("delNotepad/{userId}/{id}")
    ReturnData delNotepad(@PathVariable long userId, @PathVariable long id);

    /**
     * 按月查询带标记的日期
     *
     * @param startTime
     * @return
     */
    @GetMapping("findRecord/{findType}/{startTime}")
    ReturnData findRecord(@PathVariable int findType, @PathVariable long startTime);

    /**
     * 获取我某天的记事
     *
     * @param thisDateId
     * @return
     */
    @GetMapping("findThisDateRecord/{thisDateId}/{options}")
    ReturnData findThisDateRecord(@PathVariable long thisDateId, @PathVariable int options);

    /***
     * 分页查询我的记事
     * @param userId  用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findMyRecord/{userId}/{options}/{page}/{count}")
    ReturnData findMyRecord(@PathVariable long userId, @PathVariable int options, @PathVariable int page, @PathVariable int count);

    /**
     * 获取指定年份黄历记载
     *
     * @param thisYearId
     * @return
     */
    @GetMapping("findAlmanac/{thisYearId}")
    ReturnData findAlmanac(@PathVariable long thisYearId);

    /**
     * 查询黄历详情
     *
     * @param calendar;     //查询黄历详情：格式20180101
     * @param calendarType; //黄历查询方式：0查所有   1只查宜忌
     * @return
     */
    @GetMapping("findCalendar/{calendar}/{calendarType}")
    ReturnData findCalendar(@PathVariable int calendar, @PathVariable int calendarType);

    /**
     * 获取指定年份法定节假日加班日安排时间
     *
     * @param thisYearId
     * @return
     */
    @GetMapping("findHolidayOvertime/{thisYearId}")
    ReturnData findHolidayOvertime(@PathVariable long thisYearId);

    /**
     * 根据ID查看详情
     *
     * @param infoId
     * @return
     */
    @GetMapping("findByIdInfo/{infoId}")
    ReturnData findByIdInfo(@PathVariable long infoId);

    /**
     * 获取某天信息
     *
     * @param thisDateId; //时间戳 查询当天记录信息  2016628
     * @param options;    //类型ID 0日程1记事
     * @return
     */
    @GetMapping("findByDayInfo/{userId}/{thisDateId}/{options}")
    ReturnData findByDayInfo(@PathVariable long userId, @PathVariable long thisDateId, @PathVariable int options);

}
