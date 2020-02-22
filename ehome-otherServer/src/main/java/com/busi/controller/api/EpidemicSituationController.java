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
import java.math.BigDecimal;
import java.util.*;

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
        EpidemicSituationAbout about = new EpidemicSituationAbout();
        if (userId >= 13870 && userId <= 53870) {
            //随机返回机器人数据
            // 我在做什么
            String[] whatAmIdoing = {"宅着", "一直在家", "宅在家里", "不让出门", "什么都不能做", "什么也不做", "猪一样活着", "吃了睡睡了吃", "在家办公", "网上办公", "在家养膘", "在家大眼瞪小眼", "在家研究厨艺", "在家孝敬父母", "除了吃睡没别的", "追剧", "颠三倒四的日子", "玩游戏的大好时光", "玩游戏", "除了刷屏还是刷屏", "老婆孩子热炕头", "在家猫着", "喝酒喝酒喝酒", "喝酒", "足不出户", "想看外面的世界", "懒床", "不梳妆的生活", "披头散发的日子", "百无聊赖的生活", "在家为国做贡献", "吃喝拉撒睡", "在家和疫情作斗争", "在家做直播", "做主播的好日子", "老师网上授课", "看电视看电视", "抱着手机过日子", "隔离过程中", "自我隔离中", "十四天隔离中", "接触者隔离中", "在路上", "异地回不了家", "在酒店等待着回家", "我是驰援武汉的医生在一线", "我是驰援武汉的护士在一线", "在武汉", "在抗疫一线", "新闻工作者在一线", "每天都在抢救病人", "抢救病人", "在一线和病毒作斗争", "在坚守岗位", "在抗击疫情的战斗中", "抗疫工作人员在工作", "在小区参与管控工作", "社区抗击疫情工作", "在村口值守", "在村口站岗", "在值班", "疫情相关工作", "超市工作不放假", "运送物资去武汉", "开车送物资", "志愿者在武汉", "志愿者", "紧张抗击疫情工作中", "医务工作者坚守岗位", "等待接受任务", "终止休假在岗", "物业不打烊", "公安干警冲在一线", "军人自有担当", "哪里有灾难哪里就有我们当兵人的身影"};
            // 捐钱
            String[] donateMoney = {"30", "50", "100", "200", "3000", "500", "1000", "2000", "3000", "5000", "10000", "20000", "30000", "50000", "60000", "800000", "100000"};
            // 捐物
            String[] benevolence = {"口罩100只", "200只", "300只", "500只", "1000只", "2000只", "3000只", "5000只", "10000只", "20000只", "牛奶50箱", "牛奶100箱", "牛奶200箱", "牛奶300箱", "牛奶500箱"};
            // 其他
            String[] other = {"没什么不出门", "宅着", "在家就是贡献", "什么也没做", "配合政府", "不添乱"};
            // 为武汉喊句话
            String[] shoutSentence = {"武汉加油", "武汉加油中国加油", "武汉挺住", "武汉不倒", "武汉不哭", "武汉是座英雄的城市", "大武汉加油", "我的大武汉", "我的大武汉挺住", "胜利属于武汉人民", "我的武汉我的家", "受伤的武汉挺住", "可怜的大武汉", "久违的热干面呀", "武汉人民一定会战胜病魔", "全国人民和你在一起", "十四亿同胞是你的坚强后盾", "我们永远在一起", "全国人民不会忘记你", "走，去武汉", "你的同胞们来了", "坚持就是胜利", "坚强的大武汉", "不要被病魔吓倒", "武汉，全国人民都注视着你", "武汉，你不孤单", "武汉的樱花马上就会盛开", "春天就要来了", "希望就在前方", "疫情过后就去武汉", "病魔吓不倒英雄的武汉人民", "我爱你武汉", "我们都爱你武汉", "英雄的武汉", "我们要求看长江大桥", "我们要去黄鹤楼", "我们要吃热干面", "等到黎明的一天", "问候武汉人民", "全国人民问候你武汉"};
            // 最想见的人
            String[] imagine = {"亲人", "恋人", "家人", "朋友", "客户", "谁也不想见", "见不到的人", "想见又见不到的人", "该见谁见谁", "见了谁是谁", "没有想见的人", "不知道", "继续不见人", "不告诉你", "想见的人在心里", "不能说", "想见的人不想见我", "想见你但不知道你是谁", "谁想见我我见谁", "瞎扯", "别问我", "见了谁是谁", "见你", "见千里之外的人"};
            // 最想做的事
            String[] wantToDo = {"能赚钱的事", "会会朋友", "会会客户", "没有最想做的事", "想做但做不到的事", "什么事也不想做", "不告诉你", "想做的事不会告诉别人", "你说", "不能说", "不会说的", "想做的事多了", "什么事都想做", "想做坏事但犯法", "不好不坏的事", "该做的事", "按部就班的事", "没人做的事", "还是最想做的事", "就是最想做的事", "做了才知道", "最想做的事可惜做不了", "都是不能说的事", "无聊", "没事找事"};
            // 最想去的地方
            String[] wantToGo = {"湖北", "武汉", "那也不想去", "想去哪去哪", "管得着吗", "不告诉你", "天涯海角", "旅游胜地", "哪里也想去但没钱", "你给钱呀", "有人报销吗", "缺个伴儿", "太空", "女儿国", "没去过的地方", "国外", "没有", "很多", "说不清楚"};

            //随机经纬度
            Map<String, String> jw = randomLonLat(85, 122, 29, 116);
            //纬度
            double lon = Double.parseDouble(jw.get("W"));
            //经度
            double lat = Double.parseDouble(jw.get("J"));
            //组合实体
            Random ra = new Random();
            about.setUserId(userId);
            about.setLat(lat);
            about.setLon(lon);
//            about.setAddress(getAdd(lon + "", lat + ""));
            about.setWhatAmIdoing(whatAmIdoing[ra.nextInt(whatAmIdoing.length) + 0]);
            about.setDonateMoney(donateMoney[ra.nextInt(donateMoney.length) + 0]);
            about.setBenevolence(benevolence[ra.nextInt(benevolence.length) + 0]);
            about.setOther(other[ra.nextInt(other.length) + 0]);
            about.setShoutSentence(shoutSentence[ra.nextInt(shoutSentence.length) + 0]);
            about.setImagine(imagine[ra.nextInt(imagine.length) + 0]);
            about.setWantToDo(wantToDo[ra.nextInt(wantToDo.length) + 0]);
            about.setWantToGo(wantToGo[ra.nextInt(wantToGo.length) + 0]);
            about.setAddTime(new Date());
        } else {
            about = epidemicSituationService.findESabout(userId);
            if (about == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", about);
    }

    /***
     * 随机中国经纬度
     * @return
     */
    public static Map<String, String> randomLonLat(double MinLon, double MaxLon, double MinLat, double MaxLat) {
        BigDecimal db = new BigDecimal(Math.random() * (MaxLon - MinLon) + MinLon);
        String lon = db.setScale(6, BigDecimal.ROUND_HALF_UP).toString();// 小数后6位
        db = new BigDecimal(Math.random() * (MaxLat - MinLat) + MinLat);
        String lat = db.setScale(6, BigDecimal.ROUND_HALF_UP).toString();
        Map<String, String> map = new HashMap<String, String>();
        map.put("J", lon);
        map.put("W", lat);
        return map;
    }

    /***
     * 经纬度转换成详细地址
     * @return
     */
//    public static String getAdd(String lng, String lat) {
//        String urlString = "http://api.map.baidu.com/geocoder/v2/?ak=pWNVQZQIhhhtdXhgxdBKtoMxhMFNhWPC&callback=renderReverse&location=" + lat + "," + lng;
//        String res = "";
//        BufferedReader in = null;
//        try {
//            URL url = new URL(urlString);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setDoOutput(true);
//            conn.setRequestMethod("POST");
//            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//            String line = null;
//            while ((line = in.readLine()) != null) {
//                res += line + "\n";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                in.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return res;
//    }

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
            selectionActivities.setExamineType(3);
        }
        epidemicSituationService.addSelection(selectionActivities);
        Map<String, Long> map = new HashMap<>();
        map.put("userId", selectionActivities.getUserId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
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

    /***
     * 新增居家轨迹
     * @param homeTrajectory
     * @return
     */
    @Override
    public ReturnData addHtrajectory(@RequestBody @Valid HomeTrajectory homeTrajectory, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        homeTrajectory.setTime(new Date());
        epidemicSituationService.addHtrajectory(homeTrajectory);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除居家轨迹
     * @return:
     */
    @Override
    public ReturnData delHtrajectory(@PathVariable long id) {
        //查询数据库
        epidemicSituationService.delHtrajectory(id);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询居家轨迹
     * @param id
     * @return
     */
    @Override
    public ReturnData findHtrajectory(@PathVariable long id) {
        HomeTrajectory homeTrajectory = epidemicSituationService.findHtrajectory(id);
        if (homeTrajectory == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", homeTrajectory);
    }
}
