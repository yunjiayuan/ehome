package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/***
 * 消息通知设置相关接口
 * author：zhaojiajie
 * create time：2018-9-13 11:37:55
 */
public interface NoticeApiController {

    /***
     * 设置消息通知
     * @param category 0消息通知 1通知消息显示内容  2群消息设置  3全天免扰  4自定义时间免扰   5震动  6声音
     * @param newNotice 新消息通知 0启用  1关闭
     * @param showContents 通知显示消息内容    0显示  1不显示
     * @param allDayExempts  全天免扰 0关闭  1开启
     * @param exemptingStartTime  自定义免扰开始时间
     * @param exemptingEndTime  自定义免扰结束时间
     * @param shock  震动	0关闭  1开启
     * @param voice  声音 0启用  1关闭
     * @param setup  群消息设置  ：0接受消息并提醒 1接受消息但不提醒     2屏蔽群消息
     * @param groupId 群ID
     * @return
     */
    @GetMapping("setUp/{category}/{newNotice}/{showContents}/{allDayExempts}/{exemptingStartTime}/{exemptingEndTime}/{shock}/{voice}/{setup}/{groupId}")
    ReturnData setUp(@PathVariable int category, @PathVariable int newNotice, @PathVariable int showContents, @PathVariable int allDayExempts, @PathVariable String exemptingStartTime, @PathVariable String exemptingEndTime, @PathVariable int shock, @PathVariable int voice, @PathVariable int setup, @PathVariable long groupId);

    /***
     * 查询消息设置详情
     * @param findType 查询类型：0消息通知  1群消息通知
     * @param groupId 群ID
     * @return
     */
    @GetMapping("findSetUp/{findType}/{groupId}")
    ReturnData findSetUp(@PathVariable int findType, @PathVariable long groupId);

    /***
     * 分页查询群消息通知设置
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findSetUpList/{page}/{count}")
    ReturnData findSetUpList(@PathVariable int page, @PathVariable int count);

    /***
     * 置空自定义免扰时间
     * @return
     */
    @GetMapping("empty")
    ReturnData empty();

}
