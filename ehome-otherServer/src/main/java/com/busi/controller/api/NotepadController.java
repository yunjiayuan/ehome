package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.NotepadService;
import com.busi.utils.CommonUtils;
import com.busi.utils.FootmarkUtils;
import com.busi.utils.MqUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: ehome
 * @description: 记事本
 * @author: ZHaoJiaJie
 * @create: 2018-10-11 12:45
 */
@RestController
public class NotepadController extends BaseController implements NotepadApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    NotepadService notepadService;

    @Autowired
    FootmarkUtils footmarkUtils;

    /**
     * @Description: 新增记事
     * @Param: notepad
     * @return:
     */
    @Override
    public ReturnData addNotepad(@Valid @RequestBody Notepad notepad, BindingResult bindingResult) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取时分秒
        Calendar now = Calendar.getInstance();
        int shi = now.get(Calendar.HOUR_OF_DAY);//时
        int fen = now.get(Calendar.MINUTE);//分
        int miao = now.get(Calendar.SECOND);//秒
        String newDate = "" + notepad.getThisDateId();
        String regex = "(\\d{4})(\\d{2})(\\d{2})";
        newDate = newDate.replaceAll(regex, "$1-$2-$3");
        newDate = newDate + " " + shi + ":" + fen + ":" + miao;
        Date thenTime = null;
        try {
            thenTime = dateformat.parse(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int num = 0;
        int type = -1;//类型区分：6记事 7日程
        Notepad note = null;
        if (notepad.getAddType() == 1) {//记事
//            note = notepadService.findDayInfo(notepad.getUserId(), notepad.getThisDateId());
//            if (note != null) {
//                return returnData(StatusCode.CODE_NOTEPAD_REPEAT_ERROR.CODE_VALUE, "新增记事失败，今天已有记事！", new JSONObject());
//            }
            //判断该用户记事数量   每天最多10条
            num = notepadService.findNum(notepad.getUserId(), notepad.getAddType());
            if (num >= 10) {
                return returnData(StatusCode.CODE_NOTEPAD_REPEAT_ERROR.CODE_VALUE, "新增记事数量超过上限,新增失败", new JSONObject());
            }
            notepad.setTime(thenTime);
            type = 6;
        } else {
            //判断该用户日程数量   每天最多10条
            num = notepadService.findNum(notepad.getUserId(), notepad.getAddType());
            if (num >= 10) {
                return returnData(StatusCode.CODE_NOTEPAD_SCHEDULE_ERROR.CODE_VALUE, "新增日程数量超过上限,新增失败", new JSONObject());
            }
            Date time = null;
            if (notepad.getAlarmTime() == null) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "alarmTime参数有误", new JSONObject());
            }
            try {
                time = dateformat.parse(dateformat.format(notepad.getAlarmTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            notepad.setTime(new Date());
            notepad.setAlarmTime(time);
            type = 7;
        }
        notepad.setThenTime(thenTime);
        notepadService.add(notepad);
        //新增任务
        mqUtils.sendTaskMQ(notepad.getUserId(), 1, 9);
        //新增足迹
        String videoUrl = null;        //视频地址
        String videoCover = null;        //视频封面
        if (!CommonUtils.checkFull(notepad.getVideoUrl())) {
            videoCover = notepad.getVideoCover();
            videoUrl = notepad.getVideoUrl();
        } else {
            videoCover = notepad.getImgUrls();
        }
        String users = "";        //用户ID组合：逗号分隔
        if (!CommonUtils.checkFull(notepad.getUsers())) {
            users = notepad.getId() + "_" + notepad.getUsers();//主键加用户ID组合
        } else {
            users += notepad.getId();
        }
        //新增足迹
        mqUtils.sendFootmarkMQ(notepad.getUserId(), notepad.getContent(), videoCover, videoUrl, null, users, type);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新记事
     * @Param: notepad
     * @return:
     */
    @Override
    public ReturnData editNotepad(@Valid @RequestBody Notepad notepad, BindingResult bindingResult) {
        //验证权限
        if (CommonUtils.getMyId() != notepad.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限编辑用户[" + notepad.getUserId() + "]的记事本信息", new JSONObject());
        }
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (notepad.getAddType() == 0) {
            Date time = null;
            try {
                time = dateformat.parse(dateformat.format(notepad.getAlarmTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            notepad.setAlarmTime(time);
        } else {
            //更新足迹记事
            Footmark footmark = new Footmark();
            footmark.setImgUrl(notepad.getImgUrls());
            footmark.setInfoId(notepad.getId() + "");
            footmark.setTitle(notepad.getContent());
            footmark.setVideoUrl(notepad.getVideoUrl());
            footmark.setUserId(notepad.getUserId());
            footmarkUtils.updateFootmark(footmark);
        }
        notepadService.update(notepad);
        if (!CommonUtils.checkFull(notepad.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(notepad.getUserId(), notepad.getDelImgUrls());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除
     * @return:
     */
    @Override
    public ReturnData delNotepad(@PathVariable long userId, @PathVariable long id) {
        //验证参数
        if (userId <= 0 || id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证删除权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限删除用户[" + userId + "]的记事本", new JSONObject());
        }
        //查询数据库
        Notepad notepad = notepadService.findById(id);
        if (notepad == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //删除足迹记事
        Footmark footmark = new Footmark();
        footmark.setInfoId(notepad.getId() + "");
        footmark.setUserId(notepad.getUserId());
        footmark.setFootmarkStatus(1);
        footmarkUtils.delFootmarkPad(footmark);

        notepadService.del(id);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 按月查询带标记的日期
     * @param startTime
     * @return
     */
    @Override
    public ReturnData findRecord(@PathVariable int findType, @PathVariable long startTime) {
        String time = "";
        long endTime = 0;
        List list = null;
        if (findType == 0) {//查询带标记的日期  findType=0时 格式：201802  findType=1时 格式：2018
            endTime = ((startTime + 1) * 100);
            startTime = startTime * 100;
        } else {
            endTime = ((startTime + 1) * 10000);
            startTime = startTime * 10000;
        }
        list = notepadService.findIdentify(CommonUtils.getMyId(), startTime, endTime);
        if (list.size() > 0 && list != null) {
            int len = list.size();
            Notepad notepad = null;
            for (int i = 0; i < len; i++) {
                notepad = (Notepad) list.get(i);
                time += notepad.getThisDateId() + (i == len - 1 ? "" : ",");
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("time", time);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * 获取我某天的记事
     *
     * @param thisDateId
     * @return
     */
    @Override
    public ReturnData findThisDateRecord(@PathVariable long thisDateId, @PathVariable int options) {
        List list = null;
        JSONArray jsonArray = new JSONArray();
        list = notepadService.findThisDateRecord(CommonUtils.getMyId(), thisDateId, options);
        if (list != null && list.size() > 0) {
            Notepad np = null;
            for (int i = 0; i < list.size(); i++) {
                np = (Notepad) list.get(i);
                Map<String, Object> obb = new HashMap<>();
                obb.put("id", np.getId());
                obb.put("addType", np.getAddType());
                obb.put("content", np.getContent());
                if (options == 1) {
                    obb.put("alarmTime", np.getAlarmTime());
                    obb.put("remindType", np.getRemindType());
                }
                jsonArray.add(obb);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", jsonArray);
    }

    /***
     * 分页查询我的记事
     * @param userId  用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findMyRecord(@PathVariable long userId, @PathVariable int options, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (userId < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //开始查询
        PageBean<Notepad> pageBean;
        pageBean = notepadService.findList(userId, options, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /**
     * 获取指定年份黄历记载
     *
     * @param thisYearId
     * @return
     */
    @Override
    public ReturnData findAlmanac(@PathVariable long thisYearId) {
        List list = notepadService.findAlmanac(thisYearId);
        if (list == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /**
     * 查询黄历详情
     *
     * @param calendar;     //查询黄历详情：格式20180101
     * @param calendarType; //黄历查询方式：0查所有   1只查宜忌
     * @return
     */
    @Override
    public ReturnData findCalendar(@PathVariable int calendar, @PathVariable int calendarType) {
        NotepadLunar almanac = notepadService.findDetails(calendar);
        if (almanac != null) {
            if (calendarType == 1) {
                almanac.setChong("");
                almanac.setLDay("");
                almanac.setLMonth("");
                almanac.setLYear("");
                almanac.setMoonName("");
                almanac.setPengZu("");
                almanac.setTaiShen("");
                almanac.setTianGanDiZhiDay("");
                almanac.setTianGanDiZhiMonth("");
                almanac.setTianGanDiZhiYear("");
                almanac.setWuXingJiaZi("");
                almanac.setWuXingNaDay("");
                almanac.setWuXingNaMonth("");
                almanac.setWuXingNaYear("");
                almanac.setXingEast("");
            }
            if (CommonUtils.checkFull(almanac.getDressing())) {
                Random r = new Random();
                String[] strings = "宜/缓".split("/");
                String[] colour = "红，橙，黄，绿，青，蓝，紫，灰，粉，黑，白，棕".split("，");
                int nextInt = r.nextInt(2) + 0;
                almanac.setFriends("见友人恋人" + strings[nextInt]);
                nextInt = r.nextInt(2) + 0;
                almanac.setPartner("见生意伙伴" + strings[nextInt]);
                nextInt = r.nextInt(2) + 0;
                almanac.setParty("聚会聚餐" + strings[nextInt]);
                nextInt = r.nextInt(2) + 0;
                almanac.setTravelFar(strings[nextInt]);
//                nextInt = r.nextInt(2) + 0;
                int nextInt2 = r.nextInt(12) + 0;
                if (nextInt2 == 11) {//随机返回一至两个穿衣颜色
//                    almanac.setDressing(strings[nextInt] + colour[nextInt2] + "色");//暂不拼宜/缓
                    almanac.setDressing(colour[nextInt2] + "色");
                } else {
//                    almanac.setDressing(strings[nextInt] + colour[nextInt2] + "色、" + colour[nextInt2 + 1] + "色");
                    almanac.setDressing(colour[nextInt2] + "色 " + colour[nextInt2 + 1] + "色");
                }
                notepadService.updateNotepadLunar(almanac);
            }
        } else {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", almanac);
    }

    /**
     * 获取指定年份法定节假日加班日安排时间
     *
     * @param thisYearId
     * @return
     */
    @Override
    public ReturnData findHolidayOvertime(@PathVariable long thisYearId) {
        NotepadFestival calendarsHoliday = notepadService.findCalendarsHoliday(thisYearId);
        if (calendarsHoliday == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", calendarsHoliday);
    }

    /**
     * 根据ID查看详情
     *
     * @param infoId
     * @return
     */
    @Override
    public ReturnData findByIdInfo(@PathVariable long infoId) {
        Notepad np = null;
        np = notepadService.findById(infoId);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", np);
    }

    /**
     * 获取某天信息
     *
     * @param thisDateId; //时间戳 查询当天记录信息  2016628
     * @param options;    //类型ID 0日程1记事
     * @return
     */
    @Override
    public ReturnData findByDayInfo(@PathVariable long userId, @PathVariable long thisDateId, @PathVariable int options) {
        List list = null;
        list = notepadService.findThisDateId(CommonUtils.getMyId(), thisDateId, options);
        if (list == null && list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }
}
