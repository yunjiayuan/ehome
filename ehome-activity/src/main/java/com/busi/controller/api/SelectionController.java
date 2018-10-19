package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.SelectionService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 评选活动相关接口
 * @author: ZHaoJiaJie
 * @create: 2018-10-15 17:54
 */
@RestController
public class SelectionController extends BaseController implements SelectionApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    SelectionService selectionService;

    @Autowired
    UserAccountSecurityUtils userAccountSecurityUtils;

    /***
     * 新增
     * @param selectionActivities
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData joinActivity(@Valid @RequestBody SelectionActivities selectionActivities, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断该用户是否实名
        UserAccountSecurity userAccountSecurity = null;
        userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(CommonUtils.getMyId());
        if (userAccountSecurity != null) {
            if (CommonUtils.checkFull(userAccountSecurity.getRealName()) || CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
                return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名!", new JSONObject());
            }
        } else {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名!", new JSONObject());
        }
        // 检测之前是否已经参加
        SelectionActivities activities = selectionService.findDetails(selectionActivities.getUserId(), selectionActivities.getSelectionType());
        if (activities != null) {
            return returnData(StatusCode.CODE_ALREADY_JOIN.CODE_VALUE, "您已经参加该活动!", new JSONObject());
        }
        if (selectionActivities.getSelectionType() == 2) {// 2 校花
            selectionActivities.setS_city(-1);
            selectionActivities.setS_district(-1);
        }
        // 默认拿第一张图片设为封面
        if (selectionActivities.getImgUrl() != null) {
            String picArray[] = selectionActivities.getImgUrl().split(",");
            selectionActivities.setActivityCover(picArray[0]);
        }
//        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date birthday = null;
//        try {
//            birthday = dateformat.parse(dateformat.format(selectionActivities.getS_birthday()));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        selectionActivities.setS_birthday(selectionActivities.getS_birthday() + " 00:00:00");
        selectionActivities.setTime(new Date());
        selectionService.addSelection(selectionActivities);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新
     * @Param: selectionActivities
     * @return:
     */
    @Override
    public ReturnData editActivity(@Valid @RequestBody SelectionActivities selectionActivities, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != selectionActivities.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + selectionActivities.getUserId() + "]的活动信息", new JSONObject());
        }
        SelectionActivities activities = selectionService.findDetails(selectionActivities.getUserId(), selectionActivities.getSelectionType());
        if (activities == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "数据错误，活动信息不存在", new JSONObject());
        }
        if (!CommonUtils.checkFull(selectionActivities.getDelUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(selectionActivities.getUserId(), selectionActivities.getDelUrls());
        }
        selectionService.updateSelection(selectionActivities);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询参加活动的人员列表
     * @param searchType  排序 0按条件查询 1按编号查询 2按名字查询
     * @param selectionType  评选类型 1城市小姐  2校花  3城市之星  4青年创业
     * @param findType   查询类型： 0表示默认，1表示查询有视频的
     * @param infoId  被查询参加活动人员的活动ID
     * @param orderVoteCountType  排序规则 0按票数从高到低 1按票数从低到高
     * @param s_name  名字
     * @param s_province  省ID -1不限
     * @param s_city   市ID -1不限
     * @param s_district  区ID -1不限
     * @param s_job   selectionType为1时=职业 "0":"请选择","1":"在校学生","2":"计算机/互联网/IT","3":"电子/半导体/仪表仪器","4":"通讯技术","5":"销售","6":"市场拓展","7":"公关/商务","8":"采购/贸易","9":"客户服务/技术支持","10":"人力资源/行政/后勤","11":"高级管理","12":"生产/加工/制造","13":"质检/安检","14":"工程机械","15":"技工","16":"财会/审计/统计","17":"金融/证券/投资/保险","18":"房地产/装修/物业","19":"仓储/物流","20":"交通/运输","21":"普通劳动力/家政服务","22":"普通服务行业","23":"航空服务业","24":"教育/培训","25":"咨询/顾问","26":"学术/科研","27":"法律","28":"设计/创意","29":"文学/传媒/影视","30":"餐饮/旅游","31":"化工","32":"能源/地址勘察","33":"医疗/护理","34":"保健/美容","35":"生物/制药/医疗机械","36":"体育工作者","37":"翻译","38":"公务员/国家干部","39":"私营业主","40":"农/林/牧/渔业","41":"警察/其他","42":"自由职业者","43":"其他"
     * 	  			  selectionType为2时=学校名称：0，1，2，3
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findMyRecord(@PathVariable int searchType, @PathVariable int selectionType, @PathVariable int findType, @PathVariable long infoId, @PathVariable int orderVoteCountType,
                                   @PathVariable String s_name, @PathVariable int s_province, @PathVariable int s_city, @PathVariable int s_district, @PathVariable int s_job,
                                   @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<SelectionActivities> pageBean = null;
        pageBean = selectionService.findsSelectionActivitiesList(searchType, selectionType, findType,
                infoId, orderVoteCountType, s_name, s_province, s_city,
                s_district, s_job, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /**
     * @Description: 更新封面&活动图片&视频地址
     * @Param: selectionActivities
     * @return:
     */
    @Override
    public ReturnData setCover(@Valid @RequestBody SelectionActivities selectionActivities, BindingResult bindingResult) {
        // 检测是否已经参加
        SelectionActivities activities = selectionService.findDetails(selectionActivities.getUserId(), selectionActivities.getSelectionType());
        if (activities == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您还未参加该活动!", new JSONObject());
        }
        if (!CommonUtils.checkFull(selectionActivities.getDelUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(activities.getUserId(), selectionActivities.getDelUrls());
        }
        selectionService.setCover(selectionActivities);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除活动图片
     * @return:
     */
//    @Override
//    public ReturnData delPic(@PathVariable String delImgUrl, @PathVariable long id) {
//        SelectionActivities activities = selectionService.findById(id, CommonUtils.getMyId());
//        if (!CommonUtils.checkFull(delImgUrl)) {
//            //调用MQ同步 图片到图片删除记录表
//            mqUtils.sendDeleteImageMQ(CommonUtils.getMyId(), delImgUrl);
//        }
//        selectionService.update(activities);
//        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
//    }

    /**
     * 查询用户是否参加过活动
     *
     * @param selectionType 评选类型 1城市小姐  2校花  3城市之星   4青年创业
     * @return
     */
    @Override
    public ReturnData isJoin(@PathVariable int selectionType) {
        //判断该用户是否实名
        int autonym = 1;//0已实名，1未实名
        UserAccountSecurity userAccountSecurity = null;
        userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(CommonUtils.getMyId());
        if (userAccountSecurity != null) {
            if (!CommonUtils.checkFull(userAccountSecurity.getRealName()) || !CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
                autonym = 0;
            }
        }
        // 检测之前是否已经参加
        long id = 0;
        int isJoin = 0;// 0未参加 1已参加
        SelectionActivities activities = selectionService.findDetails(CommonUtils.getMyId(), selectionType);
        if (activities != null) {
            id = activities.getId();
            isJoin = 1;
        }
        // 获取用户性别 客 户端判断是否具备参加资格
        UserInfo userInfo = null;
        userInfo = userInfoUtils.getUserInfo(CommonUtils.getMyId());
        if (userInfo == null) {
            return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE, "用户不存在", new JSONObject());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("isJoin", isJoin);
        map.put("autonym", autonym);
        map.put("sex", userInfo.getSex());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * 查询参加活动人员的详细信息
     *
     * @param id
     * @return
     */
    @Override
    public ReturnData findJoin(@PathVariable long id) {
        SelectionActivities sa = selectionService.findById(id);
        //判断该用户是否实名
        int realNameStatus = 0;// 0实名未认证 1已认证
        UserAccountSecurity userAccountSecurity = null;
        userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(CommonUtils.getMyId());
        if (userAccountSecurity != null) {
            if (!CommonUtils.checkFull(userAccountSecurity.getRealName()) || !CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
                realNameStatus = 1;
            }
        }
        UserInfo userInfo = null;
        userInfo = userInfoUtils.getUserInfo(CommonUtils.getMyId());
        if (userInfo != null) {
            sa.setName(userInfo.getName());
            sa.setHead(userInfo.getHead());
            sa.setProTypeId(userInfo.getProType());
            sa.setHouseNumber(userInfo.getHouseNumber());
            sa.setRealNameStatus(realNameStatus);
        }

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", sa);
    }

    /***
     * 投票
     * @param selectionVote
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData vote(@Valid @RequestBody SelectionVote selectionVote, BindingResult bindingResult) {
        //验证是不是自己
        if (CommonUtils.getMyId() == selectionVote.getUserId()) {
            return returnData(StatusCode.CODE_NOT_AUTHORITY_VOTE.CODE_VALUE, "投票失败，不能给自己投票!", new JSONObject());
        }
        //判断当前用户是否给该用户投过票 以每天凌晨点为准 每天每人只能给同一个人投一次票
        SelectionVote vote = selectionService.findTicket(CommonUtils.getMyId(), selectionVote.getUserId(), selectionVote.getSelectionType());
        if (vote != null) {
            return returnData(StatusCode.CODE_ALREADY_VOTE.CODE_VALUE, "今天已经对该用户进行过投票", new JSONObject());
        }
        selectionVote.setMyId(CommonUtils.getMyId());
        selectionVote.setTime(new Date());
        selectionService.addVote(selectionVote);
        SelectionActivities activities = selectionService.findDetails(selectionVote.getUserId(), selectionVote.getSelectionType());
        if (activities == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "该用户还未参加该活动!", new JSONObject());
        }
        activities.setVotesCounts(activities.getVotesCounts() + 1);
        selectionService.updateNumber(activities);
        //新增任务
        mqUtils.sendTaskMQ(CommonUtils.getMyId(), 1, 5);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询被投票历史记录
     * @param userId  被查询用户ID
     * @param selectionType  评选类型 1城市小姐  2校花  3城市之星   4青年创业
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findVoteHistory(@PathVariable long userId, @PathVariable int selectionType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        List list = null;
        PageBean<SelectionVote> pageBean = null;
        pageBean = selectionService.findVoteList(userId, selectionType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                UserInfo userInfo = null;
                SelectionVote vote = null;
                vote = (SelectionVote) list.get(i);
                if (vote != null) {
                    userInfo = userInfoUtils.getUserInfo(vote.getMyId());
                    if (userInfo != null) {
                        vote.setUserName(userInfo.getName());
                        vote.setUserHead(userInfo.getHead());
                        vote.setProTypeId(userInfo.getProType());
                        vote.setHouseNumber(userInfo.getHouseNumber());
                        vote.setProvince(userInfo.getProvince());
                        vote.setCity(userInfo.getCity());
                        vote.setDistrict(userInfo.getDistrict());
                        vote.setSex(userInfo.getSex());
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
