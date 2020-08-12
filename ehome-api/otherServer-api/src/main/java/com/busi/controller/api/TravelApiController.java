package com.busi.controller.api;

import com.busi.entity.ScenicSpotCollection;
import com.busi.entity.ScenicSpot;
import com.busi.entity.ScenicSpotTickets;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @program: ehome
 * @description: 旅游相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-07-29 13:03:46
 */
public interface TravelApiController {

    /***
     * 新增景区
     * @param scenicSpot
     * @return
     */
    @PostMapping("addScenicSpot")
    ReturnData addScenicSpot(@Valid @RequestBody ScenicSpot scenicSpot, BindingResult bindingResult);

    /***
     * 更新景区
     * @param scenicSpot
     * @return
     */
    @PutMapping("changeScenicSpot")
    ReturnData changeScenicSpot(@Valid @RequestBody ScenicSpot scenicSpot, BindingResult bindingResult);

    /**
     * @Description: 删除景区
     * @return:
     */
    @DeleteMapping("delScenicSpot/{userId}/{id}")
    ReturnData delScenicSpot(@PathVariable long userId, @PathVariable long id);

    /***
     * 更新景区营业状态
     * @param scenicSpot
     * @return
     */
    @PutMapping("updScenicSpotStatus")
    ReturnData updScenicSpotStatus(@Valid @RequestBody ScenicSpot scenicSpot, BindingResult bindingResult);

    /***
     * 查询景区信息
     * @param userId
     * @return
     */
    @GetMapping("findScenicSpot/{userId}")
    ReturnData findScenicSpot(@PathVariable long userId);

    /***
     * 条件查询景区
     * @param watchVideos 筛选视频：0否 1是
     * @param name    模糊搜索
     * @param province     省
     * @param city      市
     * @param district    区
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findScenicSpotList/{watchVideos}/{name}/{province}/{city}/{district}/{lat}/{lon}/{page}/{count}")
    ReturnData findScenicSpotList(@PathVariable int watchVideos, @PathVariable String name, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count);

    /***
     * 新增门票
     * @param tickets
     * @return
     */
    @PostMapping("addTickets")
    ReturnData addTickets(@Valid @RequestBody ScenicSpotTickets tickets, BindingResult bindingResult);

    /***
     * 更新门票
     * @param tickets
     * @return
     */
    @PutMapping("updateTickets")
    ReturnData updateTickets(@Valid @RequestBody ScenicSpotTickets tickets, BindingResult bindingResult);

    /**
     * @Description: 删除门票
     * @return:
     */
    @DeleteMapping("delTickets/{ids}")
    ReturnData delTickets(@PathVariable String ids);

    /***
     * 查询门票详情
     * @param id
     * @return
     */
    @GetMapping("findTickets/{id}")
    ReturnData findTickets(@PathVariable long id);

    /***
     * 分页查询门票列表
     * @param id   景区ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findTicketsList/{id}/{page}/{count}")
    ReturnData findTicketsList(@PathVariable long id, @PathVariable int page, @PathVariable int count);

    /***
     * 新增收藏
     * @param hourlyWorkerCollection
     * @return
     */
    @PostMapping("addScenicSpotCollect")
    ReturnData addScenicSpotCollect(@Valid @RequestBody ScenicSpotCollection hourlyWorkerCollection, BindingResult bindingResult);

    /***
     * 分页查询收藏列表
     * @param userId   用户ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findScenicSpotCollectList/{userId}/{page}/{count}")
    ReturnData findScenicSpotCollectList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /**
     * @Description: 删除收藏
     * @return:
     */
    @DeleteMapping("delScenicSpotCollect/{ids}")
    ReturnData delScenicSpotCollect(@PathVariable String ids);

    /***
     * 关联景区、酒店、订座
     * @param type 0景区ID、1酒店ID、2订座ID
     * @param id 景区ID、酒店ID、订座ID
     * @return
     */
    @GetMapping("relationSet/{id}/{type}")
    ReturnData relationSet(@PathVariable int type, @PathVariable long id);

}
