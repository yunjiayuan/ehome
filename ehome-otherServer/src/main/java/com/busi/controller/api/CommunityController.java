package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.CommunityMessageService;
import com.busi.service.CommunityResidentTagService;
import com.busi.service.CommunityService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * @program: ehome
 * @description: 居委会相关
 * @author: ZHaoJiaJie
 * @create: 2020-03-17 17:07:43
 */
@RestController
public class CommunityController extends BaseController implements CommunityApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    CommunityService communityService;

    @Autowired
    CommunityResidentTagService residentTagService;

    @Autowired
    private CommunityMessageService communityMessageService;

    /***
     * 查询是否已加入居委会
     * @param userId
     * @return
     */
    @Override
    public ReturnData findJoinCommunity(@PathVariable long userId) {
        List list = null;
        CommunityResident resident = null;
        list = communityService.findJoin(userId);
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        resident = (CommunityResident) list.get(0);
        if (resident == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", resident);
    }

    /***
     * 刷新居委会时间
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeCommunityTime(@Valid @RequestBody CommunityResident homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        homeHospital.setRefreshTime(new Date());
        communityService.changeCommunityTime(homeHospital);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_COMMUNITY + homeHospital.getId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增居委会
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addCommunity(@Valid @RequestBody Community homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        homeHospital.setTime(new Date());
        communityService.addCommunity(homeHospital);

        //新增居民
        CommunityResident resident = new CommunityResident();
        resident.setTime(new Date());
        resident.setRefreshTime(new Date());
        resident.setCommunityId(homeHospital.getId());
        resident.setUserId(homeHospital.getUserId());
        resident.setIdentity(2);
        communityService.addResident(resident);

        //新增默认居委会标签
        String[] string = {"普通居民", "小区长", "楼栋长", "单元长/联户长", "消防员", "社区民警", "建档立卡贫困户", "低保户", "特困户", "五保户", "兜底户", "残疾户"};
        for (int i = 0; i < string.length; i++) {
            CommunityResidentTag tag = new CommunityResidentTag();
            tag.setTagName(string[i]);
            tag.setCommunityId(homeHospital.getId());
            residentTagService.add(tag);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", homeHospital.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新居委会
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeCommunity(@Valid @RequestBody Community homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        communityService.changeCommunity(homeHospital);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_COMMUNITY + homeHospital.getId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询居委会详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findCommunity(@PathVariable long id) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_COMMUNITY + id);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            Community sa = communityService.findCommunity(id);
            if (sa == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询居委会不存在!", new JSONObject());
            }
            //放入缓存
            kitchenMap = CommonUtils.objectToMap(sa);
            redisUtils.hmset(Constants.REDIS_KEY_KITCHEN + id, kitchenMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
    }

    /***
     * 查询居委会列表
     * @param userId    用户ID(默认0，大于0时查询此用户加入的所有居委会)
     * @param lon     经度
     * @param lat  纬度
     * @param string    模糊搜索 (居委会名字)
     * @param province     省
     * @param city      市
     * @param district    区
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findCommunityList(@PathVariable long userId, @PathVariable double lon, @PathVariable double lat, @PathVariable String string, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        List list = null;
        String ids = "";
        if (userId > 0) {
            list = communityService.findJoin(userId);
            if (list == null || list.size() <= 0) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
            }
            for (int i = 0; i < list.size(); i++) {
                CommunityResident resident = (CommunityResident) list.get(i);
                if (resident != null) {
                    if (i == 0) {
                        ids = resident.getCommunityId() + "";//居委会ID
                    } else {
                        ids += "," + resident.getCommunityId();
                    }
                }
            }
            List list2 = null;
            if (!CommonUtils.checkFull(ids)) {
                list2 = communityService.findCommunityList2(ids);
                if (list2 == null || list2.size() <= 0) {
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
                }
                for (int i = 0; i < list2.size(); i++) {
                    Community community = (Community) list2.get(i);
                    for (int j = 0; j < list.size(); j++) {
                        CommunityResident resident = (CommunityResident) list.get(j);
                        if (community.getId() == resident.getCommunityId()) {
                            community.setIdentity(resident.getIdentity());
                            list.remove(j);
                        }
                    }
                }
            }
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list2);
        }
        PageBean<Community> pageBean = null;
        pageBean = communityService.findCommunityList(lon, lat, string, province, city, district, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        list = pageBean.getList();
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
//        if (!CommonUtils.checkFull(string)) {
//            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
//        }
        for (int i = 0; i < list.size(); i++) {
            Community ik = (Community) list.get(i);
            if (CommonUtils.checkFull(string)) {
                double userlon = ik.getLon();
                double userlat = ik.getLat();

                int distance = (int) Math.round(CommonUtils.getShortestDistance(userlon, userlat, lon, lat));

                ik.setDistance(distance);//距离/m
            }
            if (i == 0) {
                ids = ik.getId() + "";//居委会ID
            } else {
                ids += "," + ik.getId();
            }
            ik.setIdentity(-1);
        }
        List list2 = null;
        if (!CommonUtils.checkFull(ids)) {
            list2 = communityService.findIsList2(ids, CommonUtils.getMyId());//查询是否有我加入的居委会
            if (list2 != null && list2.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    Community community = (Community) list.get(i);
                    for (int j = 0; j < list2.size(); j++) {
                        CommunityResident resident = (CommunityResident) list2.get(j);
                        if (resident.getUserId() == CommonUtils.getMyId() && community.getId() == resident.getCommunityId()) {
                            community.setIdentity(resident.getIdentity());
                            list2.remove(j);
                        }
                    }
                }
            }
        }
        if (CommonUtils.checkFull(string)) {
            Collections.sort(list, new Comparator<Community>() {
                /*
                 * int compare(Person o1, Person o2) 返回一个基本类型的整型，
                 * 返回负数表示：o1 小于o2，
                 * 返回0 表示：o1和p2相等，
                 * 返回正数表示：o1大于o2
                 */
                @Override
                public int compare(Community o1, Community o2) {
                    // 按照距离进行正序排列
                    if (o1.getDistance() > o2.getDistance()) {
                        return 1;
                    }
                    if (o1.getDistance() == o2.getDistance()) {
                        return 0;
                    }
                    return -1;
                }
            });
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 加入居委会
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addResident(@Valid @RequestBody CommunityResident homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        CommunityResident resident1 = null;
        if (homeHospital.getType() == 1) { //判断邀请者权限
            resident1 = communityService.findResident(homeHospital.getCommunityId(), homeHospital.getMasterId());
            if (resident1 == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "没有权限", new JSONArray());
            }
            //判断是否已经加入了
            CommunityResident sa = communityService.findResident(homeHospital.getCommunityId(), homeHospital.getUserId());
            if (sa != null) {
                //更新居民标签
                if (!CommonUtils.checkFull(homeHospital.getTags())) {
                    if (resident1.getIdentity() < 1) {
                        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "没有权限", new JSONArray());
                    }
                    sa.setTags(homeHospital.getTags());
                    communityService.changeResidentTag(sa);
                }
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
            }
        }
        homeHospital.setTime(new Date());
        homeHospital.setRefreshTime(new Date());
        communityService.addResident(homeHospital);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
    }

    /***
     * 更新居民权限
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeResident(@Valid @RequestBody CommunityResident homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断权限
        CommunityResident sa = communityService.findResident(homeHospital.getCommunityId(), CommonUtils.getMyId());
        if (sa == null || sa.getIdentity() < 1) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "没有权限", new JSONArray());
        }
        if (homeHospital.getIdentity() == 2 && sa.getIdentity() == 2) {
            sa.setIdentity(1);
            communityService.changeResident(sa);
        }
        communityService.changeResident(homeHospital);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新居民标签
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeResidentTag(@Valid @RequestBody CommunityResident homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断权限
        CommunityResident sa = communityService.findResident(homeHospital.getCommunityId(), CommonUtils.getMyId());
        if (sa == null || sa.getIdentity() < 1) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "没有权限", new JSONArray());
        }
        communityService.changeResidentTag(homeHospital);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除居民
     * @param type 0删除居民  1删除管理员
     * @return:
     */
    @Override
    public ReturnData delResident(@PathVariable int type, @PathVariable String ids, @PathVariable long communityId) {
        Community sa = communityService.findCommunity(communityId);
        if (sa == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询居委会不存在!", new JSONObject());
        }
        List resident = communityService.findIsList3(ids);
        if (resident == null || resident.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        for (int i = 0; i < resident.size(); i++) {
            CommunityResident communityResident = (CommunityResident) resident.get(i);
            if (communityResident.getUserId() == CommonUtils.getMyId()) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
        }
        if (type == 1) {
            if (sa.getUserId() != CommonUtils.getMyId()) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "没有权限!", new JSONObject());
            }
        }
        communityService.delResident(type, ids.split(","));
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询居民详情
     * @param communityId
     * @param homeNumber
     * @return
     */
    @Override
    public ReturnData findResiden(@PathVariable long communityId, @PathVariable String homeNumber) {
        CommunityResident sa = communityService.findResident(communityId, CommonUtils.getMyId());
        if (sa == null || sa.getIdentity() < 1) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "没有权限", new JSONArray());
        }
        UserInfo userInfo = null;
        userInfo = userInfoUtils.getUserInfo(homeNumber);
        if (userInfo == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        CommunityResident resident = communityService.findResident(communityId, userInfo.getUserId());
        if (resident == null) {
            resident = new CommunityResident();
            resident.setUserId(userInfo.getUserId());
        }
        resident.setName(userInfo.getName());
        resident.setHead(userInfo.getHead());
        resident.setProTypeId(userInfo.getProType());
        resident.setHouseNumber(userInfo.getHouseNumber());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", resident);
    }

    /***
     * 查询居民列表
     * @param type    0所有人  1管理员
     * @param communityId    居委会ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findResidentList(@PathVariable int type, @PathVariable long communityId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<CommunityResident> pageBean = null;
        pageBean = communityService.findResidentList(type, communityId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = pageBean.getList();
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        for (int i = 0; i < list.size(); i++) {
            CommunityResident sa = (CommunityResident) list.get(i);
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
     * 添加留言板
     * @param shopFloorComment
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addMessageBoard(@Valid @RequestBody CommunityMessageBoard shopFloorComment, BindingResult bindingResult) {
        //处理特殊字符
        String content = shopFloorComment.getContent();
        if (!CommonUtils.checkFull(content)) {
            String filteringContent = CommonUtils.filteringContent(content);
            if (CommonUtils.checkFull(filteringContent)) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "评论内容不能为空并且不能包含非法字符！", new JSONArray());
            }
            shopFloorComment.setContent(filteringContent);
        }
        if (shopFloorComment.getType() == 0) { //留言类别   0居委会
            Community posts = null;
            posts = communityService.findCommunity(shopFloorComment.getCommunityId());
            if (posts == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            //更新评论数
            posts.setCommentNumber(posts.getCommentNumber() + 1);
            communityService.updateBlogCounts(posts);
            long myId = shopFloorComment.getUserId();
            long userId = shopFloorComment.getReplayId();
            int ate = shopFloorComment.getReplyType();
            //新增消息
            if (ate == 0) {//评论时给所有管理员新增消息
                List wardenList = communityService.findWardenList(shopFloorComment.getCommunityId());
                if (wardenList != null && wardenList.size() > 0) {
                    for (int i = 0; i < wardenList.size(); i++) {
                        CommunityResident resident = (CommunityResident) wardenList.get(i);
                        CommunityMessage comment = new CommunityMessage();
                        comment.setNewsState(1);
                        comment.setTime(new Date());
                        comment.setNewsType(ate);
                        comment.setType(shopFloorComment.getType());
                        comment.setUserId(myId);
                        comment.setReplayId(resident.getUserId());
                        comment.setContent(shopFloorComment.getContent());
                        comment.setCommentId(shopFloorComment.getId());
                        comment.setCommunityId(shopFloorComment.getCommunityId());
                        communityMessageService.addMessage(comment);
                    }
                }
            } else {
                CommunityMessage comment = new CommunityMessage();
                comment.setNewsState(1);
                comment.setTime(new Date());
                comment.setNewsType(ate);
                comment.setType(shopFloorComment.getType());
                comment.setUserId(myId);
                comment.setReplayId(userId);
                comment.setContent(shopFloorComment.getContent());
                comment.setCommentId(shopFloorComment.getId());
                comment.setCommunityId(shopFloorComment.getCommunityId());
                communityMessageService.addMessage(comment);
            }
        } else if (shopFloorComment.getType() == 1) {//留言类别  1物业

        }
        shopFloorComment.setTime(new Date());
        communityService.addComment(shopFloorComment);
        if (shopFloorComment.getReplyType() == 0) {//新增评论
            //放入缓存(七天失效)
            redisUtils.addListLeft(Constants.REDIS_KEY_COMMUNITY_COMMENT + shopFloorComment.getCommunityId() + "_" + shopFloorComment.getType(), shopFloorComment, Constants.USER_TIME_OUT);
        } else {//新增回复
            List list = null;
            //先添加到缓存集合(七天失效)
            redisUtils.addListLeft(Constants.REDIS_KEY_COMMUNITY_REPLY + shopFloorComment.getFatherId(), shopFloorComment, Constants.USER_TIME_OUT);
            //再保证5条数据
            list = redisUtils.getList(Constants.REDIS_KEY_COMMUNITY_REPLY + shopFloorComment.getFatherId(), 0, -1);
            //清除缓存中的回复信息
            redisUtils.expire(Constants.REDIS_KEY_COMMUNITY_REPLY + shopFloorComment.getFatherId(), 0);
            if (list != null && list.size() > 5) {//限制五条回复
                //缓存中获取最新五条回复
                CommunityMessageBoard message = null;
                List<CommunityMessageBoard> messageList = new ArrayList<>();
                for (int j = 0; j < list.size(); j++) {
                    if (j < 5) {
                        message = (CommunityMessageBoard) list.get(j);
                        if (message != null) {
                            messageList.add(message);
                        }
                    }
                }
                if (messageList.size() == 5) {
                    redisUtils.pushList(Constants.REDIS_KEY_COMMUNITY_REPLY + shopFloorComment.getFatherId(), messageList, 0);
                }
            }
            //更新回复数
            CommunityMessageBoard num = communityService.findById(shopFloorComment.getFatherId());
            if (num != null) {
                num.setReplyNumber(num.getReplyNumber() + 1);
                communityService.updateCommentNum(num);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除留言板
     * @param id 评论ID
     * @param communityId 居委会或物业ID
     * @return
     */
    @Override
    public ReturnData delMessageBoard(@PathVariable long id, @PathVariable long communityId) {
        List list = null;
        List list2 = null;
        List list3 = null;
        List messList = null;
        CommunityMessageBoard comment = communityService.findById(id);
        if (comment == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "评论不存在", new JSONArray());
        }
        //判断操作人权限
        long userId = comment.getUserId();//评论者ID
        long myId = CommonUtils.getMyId();//登陆者ID
        if (myId != userId) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "参数有误，当前用户[" + myId + "]无权限删除[" + communityId + "]的评论", new JSONObject());
        }
        comment.setReplyStatus(1);//1删除
        communityService.update(comment);
        //同时删除此评论下回复
        String ids = "";
        messList = communityService.findMessList(id);
        if (messList != null && messList.size() > 0) {
            for (int i = 0; i < messList.size(); i++) {
                CommunityMessageBoard message = null;
                message = (CommunityMessageBoard) messList.get(i);
                if (message != null) {
                    ids += message.getId() + ",";
                }
            }
            //更新回复删除状态
            communityService.updateReplyState(ids.split(","));
        }
        int num = messList.size();
        if (comment.getType() == 0) { //留言类别   0居委会
            //查询该居委会信息
            Community posts = null;
            posts = communityService.findCommunity(communityId);
            if (posts == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "不存在", new JSONObject());
            }
            //更新评论数
            posts.setCommentNumber(posts.getCommentNumber() - num - 1);
            communityService.updateBlogCounts(posts);
        }
        if (comment.getType() == 1) {//留言类别  1物业

        }
        if (comment.getReplyType() == 0) {
            //获取缓存中评论列表
            list = redisUtils.getList(Constants.REDIS_KEY_COMMUNITY_COMMENT + communityId + "_" + comment.getType(), 0, -1);
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    CommunityMessageBoard comment2 = (CommunityMessageBoard) list.get(i);
                    if (comment2.getId() == id) {
                        //更新评论缓存
                        redisUtils.removeList(Constants.REDIS_KEY_COMMUNITY_COMMENT + communityId + "_" + comment.getType(), 1, comment2);
                        //更新此评论下的回复缓存
                        redisUtils.expire(Constants.REDIS_KEY_COMMUNITY_REPLY + comment.getFatherId(), 0);
                        break;
                    }
                }
            }
        } else {
            List<CommunityMessageBoard> messageList = new ArrayList<>();
            //获取缓存中回复列表
            list3 = redisUtils.getList(Constants.REDIS_KEY_COMMUNITY_REPLY + comment.getFatherId(), 0, -1);
            if (list3 != null && list3.size() > 0) {
                for (int i = 0; i < list3.size(); i++) {
                    CommunityMessageBoard comment1 = (CommunityMessageBoard) list3.get(i);
                    if (comment1 != null) {
                        if (comment1.getId() == id) {
                            //清除缓存中的回复信息
                            redisUtils.expire(Constants.REDIS_KEY_COMMUNITY_REPLY + comment.getFatherId(), 0);
                            //数据库获取最新五条回复
                            list2 = communityService.findMessList(comment.getFatherId());
                            if (list2 != null && list2.size() > 0) {
                                CommunityMessageBoard message = null;
                                for (int j = 0; j < list2.size(); j++) {
                                    if (j < 5) {
                                        message = (CommunityMessageBoard) list2.get(j);
                                        if (message != null) {
                                            messageList.add(message);
                                        }
                                    }
                                }
                                redisUtils.pushList(Constants.REDIS_KEY_COMMUNITY_REPLY + comment.getFatherId(), messageList, 0);
                            }
                            break;
                        }
                    }
                }
            }
            //更新回复数
            comment.setReplyNumber(comment.getReplyNumber() - 1);
            communityService.updateCommentNum(comment);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询留言板记录
     * @param communityId   type=0时为居委会ID  type=1时为物业ID
     * @param type          类型： 0居委会  1物业
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findMessageBoardList(@PathVariable int type, @PathVariable long communityId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //获取缓存中评论列表
        List list = null;
        List list2 = null;
        List commentList = null;
        List commentList2 = null;
        List<CommunityMessageBoard> messageArrayList = new ArrayList<>();
        PageBean<CommunityMessageBoard> pageBean = null;
        long countTotal = redisUtils.getListSize(Constants.REDIS_KEY_COMMUNITY_COMMENT + communityId + "_" + type);
        int pageCount = page * count;
        if (pageCount > countTotal) {
            pageCount = -1;
        } else {
            pageCount = pageCount - 1;
        }
        commentList = redisUtils.getList(Constants.REDIS_KEY_COMMUNITY_COMMENT + communityId + "_" + type, (page - 1) * count, pageCount);
        //获取数据库中评论列表
        if (commentList == null || commentList.size() < count) {
            pageBean = communityService.findList(type, communityId, page, count);
            commentList2 = pageBean.getList();
            if (commentList2 != null && commentList2.size() > 0) {
                for (int i = 0; i < commentList2.size(); i++) {
                    CommunityMessageBoard comment = null;
                    comment = (CommunityMessageBoard) commentList2.get(i);
                    if (comment != null) {
                        for (int j = 0; j < commentList.size(); j++) {
                            CommunityMessageBoard comment2 = null;
                            comment2 = (CommunityMessageBoard) commentList.get(j);
                            if (comment2 != null) {
                                if (comment.getId() == comment2.getId()) {
                                    redisUtils.removeList(Constants.REDIS_KEY_COMMUNITY_COMMENT + communityId + "_" + type, 1, comment2);
                                }
                            }
                        }
                    }
                }
                //更新缓存
                redisUtils.pushList(Constants.REDIS_KEY_COMMUNITY_COMMENT + communityId + "_" + type, commentList2, Constants.USER_TIME_OUT);
                //获取最新缓存
                commentList = redisUtils.getList(Constants.REDIS_KEY_COMMUNITY_COMMENT + communityId + "_" + type, (page - 1) * count, page * count);
            }
        }
        if (commentList == null) {
            commentList = new ArrayList();
        }
        for (int j = 0; j < commentList.size(); j++) {//评论
            UserInfo userInfo = null;
            CommunityMessageBoard comment = null;
            comment = (CommunityMessageBoard) commentList.get(j);
            if (comment != null) {
                userInfo = userInfoUtils.getUserInfo(comment.getUserId());
                if (userInfo != null) {
                    comment.setUserHead(userInfo.getHead());
                    comment.setUserName(userInfo.getName());
                    comment.setHouseNumber(userInfo.getHouseNumber());
                    comment.setProTypeId(userInfo.getProType());
                }
                //获取缓存中回复列表
                list = redisUtils.getList(Constants.REDIS_KEY_COMMUNITY_REPLY + comment.getId(), 0, -1);
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {//回复
                        CommunityMessageBoard message = null;
                        message = (CommunityMessageBoard) list.get(i);
                        if (message != null) {
                            userInfo = userInfoUtils.getUserInfo(message.getReplayId());
                            if (userInfo != null) {
                                message.setReplayName(userInfo.getName());
                            }
                            userInfo = userInfoUtils.getUserInfo(message.getUserId());
                            if (userInfo != null) {
                                message.setUserName(userInfo.getName());
                            }
                        }
                    }
                    comment.setMessageList(list);
                } else {
                    //查询数据库 （获取最新五条回复）
                    list2 = communityService.findMessList(comment.getId());
                    if (list2 != null && list2.size() > 0) {
                        CommunityMessageBoard message = null;
                        for (int l = 0; l < list2.size(); l++) {
                            if (l < 5) {
                                message = (CommunityMessageBoard) list2.get(l);
                                if (message != null) {
                                    userInfo = userInfoUtils.getUserInfo(message.getReplayId());
                                    if (userInfo != null) {
                                        message.setReplayName(userInfo.getName());
                                    }
                                    userInfo = userInfoUtils.getUserInfo(message.getUserId());
                                    if (userInfo != null) {
                                        message.setUserName(userInfo.getName());
                                    }
                                    messageArrayList.add(message);
                                }
                            }
                        }
                        comment.setMessageList(messageArrayList);
                        //更新缓存
                        redisUtils.pushList(Constants.REDIS_KEY_COMMUNITY_REPLY + comment.getId(), messageArrayList, 0);
                    }
                }
            }
        }
        pageBean = new PageBean<>();
        pageBean.setSize(commentList.size());
        pageBean.setPageNum(page);
        pageBean.setPageSize(count);
        pageBean.setList(commentList);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 查询留言板指定评论下的回复记录接口
     * @param contentId     评论ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findMessageBoardReplyList(@PathVariable long contentId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        List list = null;
        PageBean<CommunityMessageBoard> pageBean = null;
        pageBean = communityService.findReplyList(contentId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        long num = 0;
        UserInfo userInfo = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {//回复
                CommunityMessageBoard message = null;
                message = (CommunityMessageBoard) list.get(i);
                if (message != null) {
                    userInfo = userInfoUtils.getUserInfo(message.getReplayId());
                    if (userInfo != null) {
                        message.setReplayName(userInfo.getName());
                    }
                    userInfo = userInfoUtils.getUserInfo(message.getUserId());
                    if (userInfo != null) {
                        message.setUserHead(userInfo.getHead());
                        message.setUserName(userInfo.getName());
                        message.setProTypeId(userInfo.getProType());
                        message.setHouseNumber(userInfo.getHouseNumber());
                    }
                }
            }
            num = communityService.getReplayCount(contentId);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("num", num);
        map.put("list", list);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 新增居委会人员设置
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addSetUp(@Valid @RequestBody CommunitySetUp homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        communityService.addSetUp(homeHospital);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新居委会人员设置
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeSetUp(@Valid @RequestBody CommunitySetUp homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        communityService.changeSetUp(homeHospital);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @param ids
     * @Description: 删除居委会人员设置
     * @return:
     */
    @Override
    public ReturnData delSetUp(@PathVariable String ids) {
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "删除参数ids不能为空", new JSONObject());
        }
        communityService.delSetUp(ids.split(","));
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询居委会人员设置列表（按职务正序）
     * @param communityId    居委会ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findSetUpList(@PathVariable long communityId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<CommunitySetUp> pageBean = null;
        pageBean = communityService.findSetUpList(communityId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
