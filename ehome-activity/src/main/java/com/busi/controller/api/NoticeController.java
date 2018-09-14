package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Groupsetup;
import com.busi.entity.Notice;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.service.NoticeService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * @program: ehome
 * @description:消息设置相关接口
 * @author: ZHaoJiaJie
 * @create: 2018-09-13 14:22
 */
@RestController
public class NoticeController extends BaseController implements NoticeApiController {

    @Autowired
    NoticeService noticeService;


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
    @Override
    public ReturnData setUp(@PathVariable int category, @PathVariable int newNotice, @PathVariable int showContents, @PathVariable int allDayExempts, @PathVariable String exemptingStartTime, @PathVariable String exemptingEndTime, @PathVariable int shock, @PathVariable int voice, @PathVariable int setup, @PathVariable long groupId) {

        if (category == 2) {
            Groupsetup gs = noticeService.findsetUpgroup(groupId);
            if (gs == null) {
                Groupsetup groupSetup = new Groupsetup();
                groupSetup.setAddTime(new Date());
                groupSetup.setUserId(CommonUtils.getMyId());
                groupSetup.setGroupId(groupId);
                groupSetup.setSetup(setup);
                noticeService.addGroupsetup(groupSetup);
            } else {
                gs.setSetup(setup);
                noticeService.setUpgroup(gs);
            }
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        Notice notice = noticeService.findSetUp(CommonUtils.getMyId());
        if (notice == null) {
            notice = new Notice();
            notice.setUserId(CommonUtils.getMyId());
            notice.setAddTime(new Date());
            if (category == 0) {//0新消息通知
                notice.setNewNotice(newNotice);
            } else if (category == 1) {//1通知消息显示内容
                notice.setShowContents(showContents);
            } else if (category == 3) {// 3全天免扰
                notice.setAllDayExempts(allDayExempts);
            } else if (category == 4) {// 4自定义时间免扰
                notice.setExemptingEndTime(exemptingEndTime);
                notice.setExemptingStartTime(exemptingStartTime);
            } else if (category == 5) {//5震动
                notice.setShock(shock);
            } else {//  6声音
                notice.setVoice(voice);
            }
            noticeService.addNotice(notice);
        } else {//0消息通知 1通知消息显示内容  2群消息设置  3全天免扰  4自定义时间免扰   5震动  6声音
            if (category == 0) {//0新消息通知
                notice.setNewNotice(newNotice);
                noticeService.setUp0(notice);
            }
            if (notice.getNewNotice() == 0) {//新消息通知开启的情况下才可以设置以下项
                if (category == 1) {//1通知消息显示内容
                    notice.setShowContents(showContents);
                    noticeService.setUp1(notice);
                } else if (category == 3) {// 3全天免扰
                    notice.setAllDayExempts(allDayExempts);
                    if (allDayExempts == 1) {
                        //置空自定义免扰时间
                        notice.setExemptingEndTime("");
                        notice.setExemptingStartTime("");
                        //关掉震动及声音
                        notice.setShock(0);
                        notice.setVoice(1);
                        //关闭显示消息内容
                        notice.setShowContents(1);
                    }
                    noticeService.setUp3(notice);
                } else if (category == 4) {// 4自定义时间免扰
                    if (notice.getAllDayExempts() == 0) {//“全天免打扰”关闭状态下才可设置
                        notice.setExemptingEndTime(exemptingEndTime);
                        notice.setExemptingStartTime(exemptingStartTime);
                        noticeService.setUp4(notice);
                    }
                } else if (category == 5) {//5震动
                    notice.setShock(shock);
                    noticeService.setUp5(notice);
                } else {//  6声音
                    notice.setVoice(voice);
                    noticeService.setUp6(notice);
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询消息设置详情
     * @param findType 查询类型：0消息通知  1群消息通知
     * @param groupId 群ID
     * @return
     */
    @Override
    public ReturnData findSetUp(@PathVariable int findType, @PathVariable long groupId) {
        //验证参数
        if (findType < 0 || findType > 1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "findType参数有误", new JSONObject());
        }
        if (findType > 0 && groupId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "groupId参数有误", new JSONObject());
        }
        Map<String, Object> map = null;
        if (findType == 0) {
            Notice notice = noticeService.findSetUp(CommonUtils.getMyId());
            map = CommonUtils.objectToMap(notice);
        } else {
            Groupsetup gs = noticeService.findsetUpgroup(groupId);
            map = CommonUtils.objectToMap(gs);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 分页查询群消息通知设置
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findSetUpList(@PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<Groupsetup> pageBean;
        pageBean = noticeService.findSetUpList(CommonUtils.getMyId(), page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }

    /***
     * 置空自定义免扰时间
     * @return
     */
    @Override
    public ReturnData empty() {
        Notice notice = noticeService.findSetUp(CommonUtils.getMyId());
        if (notice != null) {
            notice.setExemptingEndTime("");
            notice.setExemptingStartTime("");
            noticeService.setTime(notice);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
    }
}
