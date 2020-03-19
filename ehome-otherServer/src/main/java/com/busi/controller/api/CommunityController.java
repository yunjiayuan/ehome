package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
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

    /***
     * 查询是否已加入居委会
     * @param userId
     * @return
     */
    @Override
    public ReturnData findJoinCommunity(@PathVariable long userId) {
        CommunityResident situationAbout = communityService.findJoin(userId);
        if (situationAbout == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", situationAbout);
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
        resident.setCommunityId(homeHospital.getId());
        resident.setUserId(homeHospital.getUserId());
        resident.setIdentity(2);
        communityService.addResident(resident);

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
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询居委会详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findCommunity(@PathVariable long id) {
        Community sa = communityService.findCommunity(id);
        if (sa == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询居委会不存在!", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", sa);
    }

    /***
     * 查询列表
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
    public ReturnData findCommunityList(@PathVariable double lon, @PathVariable double lat, @PathVariable String string, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<Community> pageBean = null;
        pageBean = communityService.findCommunityList(lon, lat, string, province, city, district, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = pageBean.getList();
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        for (int i = 0; i < list.size(); i++) {
            Community ik = (Community) list.get(i);
            double userlon = ik.getLon();
            double userlat = ik.getLat();

            int distance = (int) Math.round(CommonUtils.getShortestDistance(userlon, userlat, lon, lat));

            ik.setDistance(distance);//距离/m
        }
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
        if (homeHospital.getType() == 0) {//主动加入
            homeHospital.setTime(new Date());
            communityService.addResident(homeHospital);
        } else {
            //判断邀请者权限
            CommunityResident sa = communityService.findResident(homeHospital.getCommunityId(), homeHospital.getMasterId());
            if (sa == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
            }
            //判断是否已经加入了
            List list = communityService.findIsList(homeHospital.getCommunityId(), homeHospital.getUserIds());
            if (list != null && list.size() > 0) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "已经邀请过了", new JSONArray());
            }
            String[] userId = homeHospital.getUserIds().split(",");
            for (int i = 0; i < userId.length; i++) {
                CommunityResident resident = new CommunityResident();
                resident.setIdentity(0);
                resident.setType(1);
                resident.setTime(new Date());
                resident.setCommunityId(homeHospital.getId());
                resident.setMasterId(homeHospital.getMasterId());
                resident.setUserId(Long.parseLong(userId[i]));
                communityService.addResident(resident);
            }
        }
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
        if (sa == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "没有权限", new JSONArray());
        }
        if (sa.getIdentity() > 1) {
            communityService.changeResident(homeHospital);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @param ids
     * @param communityId
     * @Description: 删除居民
     * @return:
     */
    @Override
    public ReturnData delResident(@PathVariable String ids, @PathVariable long communityId) {
        Community sa = communityService.findCommunity(communityId);
        if (sa == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询居委会不存在!", new JSONObject());
        }
        if (sa.getUserId() != CommonUtils.getMyId()) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "没有权限!", new JSONObject());
        }
        communityService.delResident(ids.split(","));
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询居民列表
     * @param communityId    居委会ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findResidentList(@PathVariable long communityId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<CommunityResident> pageBean = null;
        pageBean = communityService.findResidentList(communityId, page, count);
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
        //查询居委会信息
        Community posts = null;
        posts = communityService.findCommunity(shopFloorComment.getCommunityId());
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //处理特殊字符
        String content = shopFloorComment.getContent();
        if (!CommonUtils.checkFull(content)) {
            String filteringContent = CommonUtils.filteringContent(content);
            if (CommonUtils.checkFull(filteringContent)) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "评论内容不能为空并且不能包含非法字符！", new JSONArray());
            }
            shopFloorComment.setContent(filteringContent);
        }
        shopFloorComment.setTime(new Date());
        communityService.addComment(shopFloorComment);
        if (shopFloorComment.getReplyType() == 0) {//新增评论
            //放入缓存(七天失效)
            redisUtils.addListLeft(Constants.REDIS_KEY_COMMUNITY_COMMENT + shopFloorComment.getCommunityId(), shopFloorComment, Constants.USER_TIME_OUT);
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
                shopFloorComment.setReplyNumber(num.getReplyNumber() + 1);
                communityService.updateCommentNum(shopFloorComment);
            }
        }
        //更新评论数
        posts.setCommentNumber(posts.getCommentNumber() + 1);
        communityService.updateBlogCounts(posts);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除留言板
     * @param id 评论ID
     * @param communityId 居委会ID
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
        //查询该居委会信息
        Community posts = null;
        posts = communityService.findCommunity(communityId);
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "居委会不存在", new JSONObject());
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
        //更新商品评论数
        int num = messList.size();
        posts.setCommentNumber(posts.getCommentNumber() - num);
        communityService.updateBlogCounts(posts);
        if (comment.getReplyType() == 0) {
            //获取缓存中评论列表
            list = redisUtils.getList(Constants.REDIS_KEY_COMMUNITY_COMMENT + communityId, 0, -1);
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    CommunityMessageBoard comment2 = (CommunityMessageBoard) list.get(i);
                    if (comment2.getId() == id) {
                        //更新评论缓存
                        redisUtils.removeList(Constants.REDIS_KEY_COMMUNITY_COMMENT + communityId, 1, comment2);
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
        //更新评论数
        posts.setCommentNumber(posts.getCommentNumber() - 1);
        communityService.updateBlogCounts(posts);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询留言板记录
     * @param communityId     居委会ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findMessageBoardList(@PathVariable long communityId, @PathVariable int page, @PathVariable int count) {
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
        long countTotal = redisUtils.getListSize(Constants.REDIS_KEY_COMMUNITY_COMMENT + communityId);
        int pageCount = page * count;
        if (pageCount > countTotal) {
            pageCount = -1;
        } else {
            pageCount = pageCount - 1;
        }
        commentList = redisUtils.getList(Constants.REDIS_KEY_COMMUNITY_COMMENT + communityId, (page - 1) * count, pageCount);
        //获取数据库中评论列表
        if (commentList == null || commentList.size() < count) {
            pageBean = communityService.findList(communityId, page, count);
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
                                    redisUtils.removeList(Constants.REDIS_KEY_COMMUNITY_COMMENT + communityId, 1, comment2);
                                }
                            }
                        }
                    }
                }
                //更新缓存
                redisUtils.pushList(Constants.REDIS_KEY_COMMUNITY_COMMENT + communityId, commentList2, Constants.USER_TIME_OUT);
                //获取最新缓存
                commentList = redisUtils.getList(Constants.REDIS_KEY_COMMUNITY_COMMENT + communityId, (page - 1) * count, page * count);
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
        if(CommonUtils.checkFull(ids)){
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
