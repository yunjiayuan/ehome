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
     * 查询疫情信息(天气平台)
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
     * 查询评选作品的详细信息
     * @param id
     * @return
     */
    @Override
    public ReturnData findCampaignAward(@PathVariable long id) {
        CampaignAwardActivity sa = epidemicSituationService.findById(id);
        if (sa == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询活动不存在!", new JSONObject());
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
     * @param findType   查询类型： 0默认全部，1票数最高 2票数最低
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
}
