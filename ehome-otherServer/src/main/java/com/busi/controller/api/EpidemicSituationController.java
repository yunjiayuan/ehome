package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.EpidemicSituationService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/***
 * 疫情相关接口
 * author：zhaojiajie
 * create time：2020-02-15 10:40:23
 */
@RestController
public class EpidemicSituationController extends BaseController implements EpidemicSituationApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    EpidemicSituationService epidemicSituationService;

    /***
     * 查询最新疫情信息(天气平台)
     * @return
     */
    @Override
    public ReturnData findNew() {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_EPIDEMICSITUATION);
        if (map == null || map.size() <= 0) {
            EpidemicSituation eSabout = epidemicSituationService.findNew();
            if (eSabout == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            map = CommonUtils.objectToMap(eSabout);
            redisUtils.hmset(Constants.REDIS_KEY_EPIDEMICSITUATION, map, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查询疫情
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findEpidemicSituation(@PathVariable int page, @PathVariable int count) {
        //开始查询
        PageBean<EpidemicSituation> pageBean = null;
        pageBean = epidemicSituationService.findList(page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 查询疫情(天气平台)
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findEStianQi(@PathVariable int page, @PathVariable int count) {
        //开始查询
        PageBean<EpidemicSituationTianqi> pageBean = null;
        pageBean = epidemicSituationService.findTQlist(page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 查询最新疫情信息(天气平台)
     * @return
     */
    @Override
    public ReturnData findNewEStianQi() {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_EPIDEMICSITUATION);
        if (map == null || map.size() <= 0) {
            EpidemicSituationTianqi eSabout = epidemicSituationService.findNewEStianQi();
            if (eSabout == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            map = CommonUtils.objectToMap(eSabout);
            redisUtils.hmset(Constants.REDIS_KEY_EPIDEMICSITUATION, map, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 新增我和疫情
     * @param epidemicSituationAbout
     * @return
     */
    @Override
    public ReturnData addESabout(@Valid @RequestBody EpidemicSituationAbout epidemicSituationAbout, BindingResult bindingResult) {

        EpidemicSituationAbout situationAbout = epidemicSituationService.findESabout(epidemicSituationAbout.getUserId());
        if (situationAbout != null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }

        epidemicSituationAbout.setAddTime(new Date());
        epidemicSituationService.addESabout(epidemicSituationAbout);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 编辑我和疫情
     * @param epidemicSituationAbout
     * @return
     */
    @Override
    public ReturnData changeESabout(@Valid @RequestBody EpidemicSituationAbout epidemicSituationAbout, BindingResult bindingResult) {
        EpidemicSituationAbout situationAbout = epidemicSituationService.findESabout(epidemicSituationAbout.getUserId());
        if (situationAbout != null) {
            epidemicSituationService.changeESabout(epidemicSituationAbout);
        } else {
            epidemicSituationAbout.setAddTime(new Date());
            epidemicSituationService.addESabout(epidemicSituationAbout);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询我和疫情信息
     * @param userId
     * @return
     */
    @Override
    public ReturnData findESabout(@PathVariable long userId) {
        EpidemicSituationAbout situationAbout = epidemicSituationService.findESabout(userId);
        if (situationAbout == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", situationAbout);
    }

    /***
     * 新增评选作品
     * @param selectionActivities
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData joinCampaignAward(@Valid @RequestBody CampaignAwardActivity selectionActivities, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        selectionActivities.setTime(new Date());
        if (selectionActivities.getUserId() == 56555) {//添加机器人数据 suntj 20200220
            Random ra = new Random();
            selectionActivities.setUserId(ra.nextInt(40000) + 13870);//随机13870-53870
            selectionActivities.setVotesCounts(ra.nextInt(1000) + 200);
            double rs2 = ra.nextInt(3);
            double moneyNew = 0;
            if (rs2 == 2) {
                if (ra.nextInt(3) == 1) {
                    moneyNew = 50;
                }
            } else if (rs2 == 1) {
                moneyNew = 20;
            } else {
                moneyNew = 10;
            }
            selectionActivities.setDraftMoney(moneyNew);
        }
        epidemicSituationService.addSelection(selectionActivities);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新评选作品信息
     * @Param: selectionActivities
     * @return:
     */
    @Override
    public ReturnData editCampaignAward(@Valid @RequestBody CampaignAwardActivity selectionActivities, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != selectionActivities.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + selectionActivities.getUserId() + "]的活动信息", new JSONObject());
        }
        if (!CommonUtils.checkFull(selectionActivities.getDelUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(selectionActivities.getUserId(), selectionActivities.getDelUrls());
        }
        epidemicSituationService.updateSelection(selectionActivities);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除评选作品
     * @return:
     */
    @Override
    public ReturnData delCampaignAward(@PathVariable long id) {
        CampaignAwardActivity io = epidemicSituationService.findById(id);
        if (io != null) {
            io.setStatus(1);
            epidemicSituationService.updateDel(io);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 审核活动作品
     * @param id  作品ID
     * @param examineType  1已审核无稿费 2已审核有稿费
     * @param draftMoney  稿费
     * @return
     */
    @Override
    public ReturnData examineWorks(@PathVariable long id, @PathVariable int examineType, @PathVariable double draftMoney) {
        long myId = CommonUtils.getMyId();
        if (myId != 10076 && myId != 12770 && myId != 9389 && myId != 9999 && myId != 13005 && myId != 12774 && myId != 13031 && myId != 12769 && myId != 12796 && myId != 10053) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限进行此操作，请联系管理员申请权限!", new JSONObject());
        }
        CampaignAwardActivity sa = epidemicSituationService.findById(id);
        if (sa == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询作品不存在!", new JSONObject());
        }
        if (examineType == 2) {
            sa.setDraftMoney(draftMoney);
        }
        sa.setAuditor(myId);
        sa.setExamineType(examineType);
        epidemicSituationService.updateExamine(sa);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 抽取稿费
     * @param id
     * @return
     */
    @Override
    public ReturnData extractDraftMoney(@PathVariable long id, @PathVariable long userId) {
        CampaignAwardActivity sa = epidemicSituationService.findId(id, userId);
        if (sa == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询作品不满足抽取条件!", new JSONObject());
        }
        sa.setExamineType(3);
        epidemicSituationService.updateExamine(sa);
        //更新钱包
        mqUtils.addRewardLog(userId, 7, 0, sa.getDraftMoney(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询审核作品列表
     * @param findType   查询类型： 0待审核（时间倒叙&票数最高），1已审核的  2我审核的
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findExamineList(@PathVariable int findType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<CampaignAwardActivity> pageBean = null;
        pageBean = epidemicSituationService.findExamineList(CommonUtils.getMyId(), findType, page, count);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }

    /***
     * 查询评选作品的详细信息
     * @param id
     * @return
     */
    @Override
    public ReturnData findCampaignAward(@PathVariable long id) {
        CampaignAwardActivity sa = epidemicSituationService.findById(id);
        if (sa == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询作品不存在!", new JSONObject());
        }
        UserInfo userInfo = null;
        userInfo = userInfoUtils.getUserInfo(sa.getUserId());
        if (userInfo != null) {
            sa.setName(userInfo.getName());
            sa.setHead(userInfo.getHead());
            sa.setProTypeId(userInfo.getProType());
            sa.setHouseNumber(userInfo.getHouseNumber());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", sa);
    }

    /***
     * 分页查询评选作品列表
     * @param findType   查询类型： 0综合排序，1票数最高 2时间最新
     * @param userId   用戶ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findCampaignAwardList(@PathVariable int findType, @PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<CampaignAwardActivity> pageBean = null;
        pageBean = epidemicSituationService.findsSelectionActivitiesList(userId, findType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = pageBean.getList();
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        for (int i = 0; i < list.size(); i++) {
            CampaignAwardActivity sa = (CampaignAwardActivity) list.get(i);
            if (sa != null) {
                UserInfo userInfo = null;
                userInfo = userInfoUtils.getUserInfo(sa.getUserId());
                if (userInfo != null) {
                    sa.setName(userInfo.getName());
                    sa.setHead(userInfo.getHead());
                    sa.setProTypeId(userInfo.getProType());
                    sa.setHouseNumber(userInfo.getHouseNumber());
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 评选作品投票
     * @param selectionVote
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData voteCampaignAward(@Valid @RequestBody CampaignAwardVote selectionVote, BindingResult bindingResult) {
        //验证是不是自己
        if (CommonUtils.getMyId() == selectionVote.getUserId()) {
            return returnData(StatusCode.CODE_NOT_AUTHORITY_VOTE.CODE_VALUE, "投票失败，不能给自己投票!", new JSONObject());
        }
        //判断当前用户是否给该用户投过票 以每天凌晨0点为准 每天每人只能给同一个参选作品投一次票
        CampaignAwardVote vote = epidemicSituationService.findTicket(CommonUtils.getMyId(), selectionVote.getCampaignAwardId());
        if (vote != null) {
            return returnData(StatusCode.CODE_ALREADY_VOTE.CODE_VALUE, "今天已经对该作品进行过投票", new JSONObject());
        }
        selectionVote.setMyId(CommonUtils.getMyId());
        selectionVote.setTime(new Date());
        epidemicSituationService.addVote(selectionVote);
        CampaignAwardActivity activities = epidemicSituationService.findById(selectionVote.getCampaignAwardId());
        if (activities == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "该作品还未参加该活动!", new JSONObject());
        }
        activities.setVotesCounts(activities.getVotesCounts() + 1);
        epidemicSituationService.updateNumber(activities);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增轨迹
     * @param selectionActivities
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addTrajectory(@Valid @RequestBody MyTrajectory selectionActivities, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        selectionActivities.setTime(new Date());
        epidemicSituationService.addTrajectory(selectionActivities);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新轨迹
     * @Param: selectionActivities
     * @return:
     */
    @Override
    public ReturnData editTrajectory(@Valid @RequestBody MyTrajectory selectionActivities, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        epidemicSituationService.editTrajectory(selectionActivities);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除轨迹
     * @return:
     */
    @Override
    public ReturnData delTrajectory(@PathVariable String ids) {
        //查询数据库
        epidemicSituationService.delTrajectory(ids.split(","), CommonUtils.getMyId());

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询轨迹
     * @param id
     * @return
     */
    @Override
    public ReturnData findTrajectory(@PathVariable long id) {
        MyTrajectory situationAbout = epidemicSituationService.findTrajectory(id);
        if (situationAbout == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", situationAbout);
    }

    /***
     * 分页查询轨迹列表
     * @param userId   用戶ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findTrajectoryList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<MyTrajectory> pageBean = null;
        pageBean = epidemicSituationService.findTrajectoryList(userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
