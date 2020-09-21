package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.fegin.RewardLogLocalControllerFegin;
import com.busi.fegin.RewardTotalMoneyLogLocalControllerFegin;
import com.busi.service.HomeBlogService;
import com.busi.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;

/**
 * 生活圈相关接口
 * author：SunTianJie
 * create time：2018/10/23 10:35
 */
@Slf4j
@RestController
public class HomeBlogController extends BaseController implements HomeBlogApiController {

    @Autowired
    private HomeBlogService homeBlogService;

    @Autowired
    private UserInfoUtils userInfoUtils;

    @Autowired
    private FollowInfoUtils followInfoUtils;

    @Autowired
    private UserRelationShipUtils userRelationShipUtils;

    @Autowired
    private RewardTotalMoneyLogLocalControllerFegin rewardTotalMoneyLogLocalControllerFegin;

    @Autowired
    private RewardLogLocalControllerFegin rewardLogLocalControllerFegin;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    /***
     * 生活圈发布接口
     * @param homeBlog
     * @return
     */
    @Override
    public ReturnData addBlog(@Valid @RequestBody HomeBlog homeBlog, BindingResult bindingResult) {
        //验证参数
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证发布人权限
        if(CommonUtils.getMyId()!=homeBlog.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限以用户["+homeBlog.getUserId()+"]的身份发布生活圈",new JSONObject());
        }
        //根据发布类型 判断部分参数格式 发布博文类型：0纯文 1图片 2视频 3音频
        if(homeBlog.getSendType()==0&&homeBlog.getBlogType()!=1){
            if(CommonUtils.checkFull(homeBlog.getContent())){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，content不能为空",new JSONObject());
            }
        }
        if(homeBlog.getSendType()==1&&homeBlog.getBlogType()!=1){
            if(CommonUtils.checkFull(homeBlog.getImgUrl())){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，imgUrl不能为空",new JSONObject());
            }
        }
        if(homeBlog.getSendType()==2&&homeBlog.getBlogType()!=1){
            if(CommonUtils.checkFull(homeBlog.getVideoUrl())||CommonUtils.checkFull(homeBlog.getVideoCoverUrl())){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，videoUrl和videoCoverUrl不能为空",new JSONObject());
            }
        }
        if(homeBlog.getSendType()==3&&homeBlog.getBlogType()!=1){
            if(CommonUtils.checkFull(homeBlog.getAudioUrl())){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，audioUrl不能为空",new JSONObject());
            }
        }
        //处理特殊字符
        String title = homeBlog.getTitle();
        if(!CommonUtils.checkFull(title)){
            title = CommonUtils.filteringContent(title);
            homeBlog.setTitle(title);
        }
        String content = homeBlog.getContent();
        if(!CommonUtils.checkFull(content)){
            homeBlog.setContent(CommonUtils.filteringContent(content));
            if(homeBlog.getContent().length()>140){
                homeBlog.setContentTxt(homeBlog.getContent().substring(0,140));
            }else{
                homeBlog.setContentTxt(homeBlog.getContent());
            }
        }
        String reprintContent = homeBlog.getReprintContent();
        if(!CommonUtils.checkFull(reprintContent)){
            reprintContent = CommonUtils.filteringContent(reprintContent);
            homeBlog.setReprintContent(reprintContent);
        }

        //开始新增
        homeBlog.setTime(new Date());
        if(homeBlog.getSendType()==2&&homeBlog.getBlogType()==0&&homeBlog.getClassify()==0){//制作假数据
            if(homeBlog.getUserId()==9999||homeBlog.getUserId()==10076||homeBlog.getUserId()==56555){
                Random ra =new Random();
                Random ra2 =new Random();
                Random ra3 =new Random();

//                homeBlog.setUserId(ra.nextInt(9999)+1);//随机10000以内
                homeBlog.setUserId(ra.nextInt(40000)+13870);//随机13870-53870
                homeBlog.setLikeCount(ra2.nextInt(30000)+10000);
                homeBlog.setLookCount(ra3.nextInt(30000)+30000);
                //设置稿费
                if(homeBlog.getUserId()%2==0){
                    Random random = new Random();
                    double moneyNew = 10;
                    int remunerationStatus = random.nextInt(4)+1;
                    if(remunerationStatus==1){
                        moneyNew = 10;
                    }else if(remunerationStatus==2){//20 50 100元
                        double rs2 = random.nextInt(3) ;
                        if(rs2==2){
                            moneyNew = 20;
                        }else if(rs2==1){
                            moneyNew = 50;
                        }else{
                            moneyNew = 10;
                        }
                    }else if(remunerationStatus==3){
                        moneyNew = 10;
                    }else if(remunerationStatus==4){
                        moneyNew = 20;
                    }
                    homeBlog.setRemunerationStatus(remunerationStatus);
                    homeBlog.setRemunerationMoney(moneyNew);
                    homeBlog.setRemunerationUserId(-1);//-1暂时代表系统审核
                    homeBlog.setRemunerationTime(homeBlog.getTime());
                }
                UserHeadNotes userHeadNotes = new UserHeadNotes();
                userHeadNotes.setWelcomeVideoPath(homeBlog.getVideoUrl());
                userHeadNotes.setWelcomeVideoCoverPath(homeBlog.getVideoCoverUrl());
                userHeadNotes.setUserId(homeBlog.getUserId());
                userInfoUtils.updateWelcomeVideoByHomeBlog(userHeadNotes);
            }
        }
        //对首次视频类型的生活圈进行处理
        double rewardMoney = 10;//奖励金额
        int status = 0;//0表示不显示首次发视频红包 1表示显示首次发视频红包
        boolean flag = false;
        if(homeBlog.getBlogType()==0&&homeBlog.getSendType()==2){
            //机器人不发送红包
            if(homeBlog.getUserId()<=10000||(homeBlog.getUserId()<=53870&&homeBlog.getUserId()>=13870)){
                flag  = true;
            }
            UserInfo userInfo = userInfoUtils.getUserInfo(homeBlog.getUserId());
            if(userInfo==null){
                flag  = true;
            }
            if(userInfo.getHomeBlogStatus()==0){//首发视频
                if(!flag){
                    //修改首发视频奖励规则 当作稿费
                    Random random = new Random();
                    int count = random.nextInt(10)+1;
                    if(count>7){
                        rewardMoney=20;
                    }
                    status = 1;
                    mqUtils.addRewardLog(homeBlog.getUserId(),3,0,rewardMoney,homeBlog.getId());
                    //更新用户状态
                    userInfo.setHomeBlogStatus(1);//改为：已发送
                    userInfoUtils.updateHomeBlogStatus(userInfo);
                    if(rewardMoney==10){
                        homeBlog.setRemunerationStatus(1);
                    }else{
                        homeBlog.setRemunerationStatus(2);
                    }
                    homeBlog.setRemunerationMoney(rewardMoney);
                    homeBlog.setRemunerationUserId(-1);//-1暂时代表系统审核
                    homeBlog.setRemunerationTime(homeBlog.getTime());
                }
            }else{//非首发视频 每次发视频60%-70%概率得10或20  总奖励累积到达80不给稿费
                  /*1、用户发布的视频60%的概率会成为稿费作品。
                    2、同一个用户、同一天发布的视频，最多给2两个视频为稿费作品。
                    3、稿费累积达到80元以后，系统则不再给该用户稿费奖励。
                    4、稿费金额90%为10元，10%为20元。
                  * */
                if(!flag){
                    //判断奖励系统是否累计达到80（70-90）  达到80元则不再给稿费
                    Map<String, Object> rewardTotalMoneyLogMap = redisUtils.hmget(Constants.REDIS_KEY_REWARD_TOTAL_MONEY + homeBlog.getUserId());
                    RewardTotalMoneyLog rewardTotalMoneyLog = null;
                    if (rewardTotalMoneyLogMap == null || rewardTotalMoneyLogMap.size() <= 0) {
                        rewardTotalMoneyLog = rewardTotalMoneyLogLocalControllerFegin.findTotalRewardMoneyInfo(homeBlog.getUserId());
                    }else{
                        rewardTotalMoneyLog = (RewardTotalMoneyLog) CommonUtils.mapToObject(rewardTotalMoneyLogMap,RewardTotalMoneyLog.class);
                    }
                    if(rewardTotalMoneyLog!=null&&rewardTotalMoneyLog.getRewardTotalMoney()<Constants.REWARD_TOTAL_MONEY_LIMIT){
                        Random random = new Random();
                        int count = random.nextInt(100)+1;
                        int grade = 7;
                        List<RewardLog> list = null;
                        if(count<=60){
                            //判断今日是否给过稿费 同一个用户每天最多给1-2个视频稿费
                            list = rewardLogLocalControllerFegin.findRewardLogListByUserId(homeBlog.getUserId());
                            int size = random.nextInt(3);
                            if(list==null||list.size()<size){
                                int r = random.nextInt(100)+1;
                                if(r>90&&rewardTotalMoneyLog.getRewardTotalMoney()<=60){//10%的得20  总金额不能超过80
                                    rewardMoney=20;
                                    grade = 8;
                                }
                                mqUtils.addRewardLog(homeBlog.getUserId(),grade,0,rewardMoney,homeBlog.getId());
                                //更新用户状态
//                        userInfo.setHomeBlogStatus(1);//改为：已发送
//                        userInfoUtils.updateHomeBlogStatus(userInfo);
                                if(rewardMoney==10){
                                    homeBlog.setRemunerationStatus(1);
                                }else{
                                    homeBlog.setRemunerationStatus(2);
                                }
                                homeBlog.setRemunerationMoney(rewardMoney);
                                homeBlog.setRemunerationUserId(-1);//-1暂时代表系统审核
                                homeBlog.setRemunerationTime(homeBlog.getTime());
                                int countss = list.size()+1;
                                log.info("用户 ["+homeBlog.getUserId()+"] 获得视频稿费奖励 ["+homeBlog.getRemunerationMoney()+"元]，今日已获得稿费奖励：["+countss+"]次");
                            }else{
                                log.info("用户 ["+homeBlog.getUserId()+"] 今日已得 ["+list.size()+"] 次视频稿费奖励，本次将不再给其稿费奖励");
                            }
                        }else{
                            log.info("用户 ["+homeBlog.getUserId()+"] 发布的视频，根据概率分配机制，未进入到稿费奖励分派系统");
                        }
                    }else{
                        log.info("用户 ["+homeBlog.getUserId()+"] 获得视频稿费奖励总金额 ["+rewardTotalMoneyLog.getRewardTotalMoney()+"元]，系统将不再自动给稿费奖励");
                    }
                }
            }
        }
        homeBlogService.add(homeBlog);
        if(homeBlog.getSendType()==2&&homeBlog.getBlogType()==0&&homeBlog.getClassify()==0&&homeBlog.getLikeCount()>10000){//制作假数据
//            redisUtils.addListLeft(Constants.REDIS_KEY_EBLOGLIST, homeBlog, 0);
            redisUtils.addSet(Constants.REDIS_KEY_EBLOGSET,homeBlog);
        }
        //添加足迹
//        String title = homeBlog.getTitle();
        String imageUrl = "";
        if(homeBlog.getBlogType()==1){
//            String reprintContent = homeBlog.getReprintContent();
            if(CommonUtils.checkFull(reprintContent)){
                title = "转发生活圈";
            }else{
                title = reprintContent;
            }
        }else{
            if(CommonUtils.checkFull(title)){
                title = homeBlog.getContentTxt();
            }
        }
        if(homeBlog.getSendType()==1){//图片
            imageUrl = homeBlog.getImgUrl();
        }else if(homeBlog.getSendType()==2){//视频
            imageUrl = homeBlog.getVideoCoverUrl();
        }
        mqUtils.sendFootmarkMQ(homeBlog.getUserId(), title,imageUrl , homeBlog.getVideoUrl(), homeBlog.getAudioUrl(), homeBlog.getId()+"", 2);
        //添加任务
        mqUtils.sendTaskMQ(homeBlog.getUserId(), 1, 1);
        //添加转发消息和转发量
        if(homeBlog.getBlogType()==1){
            mqUtils.updateBlogCounts(homeBlog.getOrigUserId(),homeBlog.getOrigBlogId(),2,1);
            if(homeBlog.getOrigUserId()!=homeBlog.getUserId()){
                mqUtils.addMessage(homeBlog.getUserId(),homeBlog.getOrigUserId(),homeBlog.getOrigUserId(),homeBlog.getOrigBlogId(),0,0,homeBlog.getReprintContent(),3);
            }
        }
        Map<String,Object> map = new HashMap();
        map.put("homeBlogStatus",status);//0表示不显示首次发视频红包 1表示显示首次发视频红包
        map.put("rewardMoney",rewardMoney+"");//红包金额
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
//        //对首次视频类型的生活圈进行处理
//        if(homeBlog.getBlogType()==0&&homeBlog.getSendType()==2){
//            //机器人不发送红包
//            if(homeBlog.getUserId()<=10000||(homeBlog.getUserId()<=53870&&homeBlog.getUserId()>=13870)){
//                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
//            }
//            UserInfo userInfo = userInfoUtils.getUserInfo(homeBlog.getUserId());
//            if(userInfo==null){
//                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
//            }
//            if(userInfo.getHomeBlogStatus()!=0){
//                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
//            }
//            //奖励首次发布视频红包 3-5 红包区间3-3.5居多
//            double rewardMoney = 0;//奖励金额
//            double[] moneyArray = new double[10000];//奖池 可自定义奖池大小
//            Random random = new Random();
//            //开始构建奖池
//            for(int i=0;i<10000;i++){
//                moneyArray[i] = (random.nextInt(51) + 300)/100.0;
//            }
//            //奖池中添加3.5-4  千分之的概率
//            for(int i=0;i<10;i++){
//                moneyArray[random.nextInt(10000)] = (random.nextInt(51) + 350)/100.0;
//            }
//            //向奖池中添加大额红包 万分之一概率  后续添加 需要再构建一个小奖池
////            moneyArray[random.nextInt(10000)] = 88;
////            moneyArray[random.nextInt(10000)] = 88.88;
////            moneyArray[random.nextInt(10000)] = 16.88;
////            moneyArray[random.nextInt(10000)] = 66;
////            moneyArray[random.nextInt(10000)] = 66.66;
//            //奖池构建完成 开始随机取值
//            rewardMoney = moneyArray[random.nextInt(10000)];
//            mqUtils.addRewardLog(homeBlog.getUserId(),3,0,rewardMoney,homeBlog.getId());
//            //更新用户状态
//            userInfo.setHomeBlogStatus(1);//改为：已发送
//            userInfoUtils.updateHomeBlogStatus(userInfo);
//            Map<String,Object> map = new HashMap();
//            map.put("homeBlogStatus",1);//0表示不显示首次发视频红包 1表示显示首次发视频红包
//            map.put("rewardMoney",rewardMoney+"");//红包金额
//            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
//        }
//        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 生活圈视频稿费评级
     * @param blogId 生活圈ID
     * @param userId 生活圈主任ID 注意不是当前登录这ID
     * @param grade 1是一级稿费作品 2是二级稿费作品 3是三级稿费作品 4是四级稿费作品
     * @param type  0:默认随机给钱 1:当前等级范围内最低金额 2:自定义金额
     * @param money  当type=2时，此字段有效
     * @return
     */
    @Override
    public ReturnData gradeBlog(@PathVariable long userId,@PathVariable long blogId,@PathVariable int grade,@PathVariable int type,@PathVariable double money) {
        long myId = CommonUtils.getMyId();
        if(myId!=10076&&myId!=12770&&myId!=9389&&myId!=9999&&myId!=13005&&myId!=12774&&myId!=13031&&myId!=12769&&myId!=12796&&myId!=10053){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限进行此操作，请联系管理员申请权限!", new JSONObject());
        }
        double moneyNew = 0;
        Random random = new Random();
        if(type==1){//当前等级范围内最低金额
            if(grade==1){
                moneyNew = 10;
            }else if(grade==2){//20-100元
                moneyNew = 20;
            }else if(grade==3){//100-2000
                moneyNew = 100;//
            }else if(grade==4){//2000-20000
                moneyNew = 2000;
            }
        }else if(type==2){//自定义金额
            moneyNew = money;
        }else{//当前等级范围内随机给钱
            if(grade==1){
                moneyNew = 10;
            }else if(grade==2){//20-100元
                moneyNew = random.nextInt(4)*10 + 20;//临时20-50
            }else if(grade==3){//100-2000
                moneyNew = random.nextInt(5)*100 + 100;//临时100-500
            }else if(grade==4){//2000-20000
                moneyNew = random.nextInt(4)*1000 + 2000;//临时2000-5000
            }
        }
        if(moneyNew>0){
            //更新生活圈
            HomeBlog homeBlog = homeBlogService.findBlogInfo(blogId,userId);
            if(homeBlog.getRemunerationStatus()>0){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "该视频已被审核过!", new JSONObject());
            }
            if(homeBlog.getBlogType()!=1&&homeBlog.getSendType()==2&&homeBlog.getLikeCount()>=Constants.EBLOG_LIKE_COUNT){
                //先将此对象从生活秀列表中清除 方便后续操作
                redisUtils.removeSetByValues(Constants.REDIS_KEY_EBLOGSET,homeBlog);
            }
            homeBlog.setRemunerationStatus(grade);
            homeBlog.setRemunerationMoney(moneyNew);
            homeBlog.setRemunerationUserId(myId);
            homeBlog.setRemunerationTime(new Date());
            homeBlogService.updateGradeBlog(homeBlog);
            //上边将生活秀删除 此处重新添加进去
            redisUtils.addSet(Constants.REDIS_KEY_EBLOGSET,homeBlog);
            //更新奖励系统
            mqUtils.addRewardLog(userId,grade+6,0,moneyNew,blogId);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 根据生活圈ID查询生活圈详情接口
     * @param userId 被查询用户ID
     * @param blogId 被查询生活圈ID
     * @return
     */
    @Override
    public ReturnData findBlogInfo(@PathVariable long userId,@PathVariable long blogId) {
        Map<String,Object> blogInfoMap = redisUtils.hmget(Constants.REDIS_KEY_EBLOG+userId+"_"+blogId);
        if(blogInfoMap==null||blogInfoMap.size()<=0){
            HomeBlog homeBlog = homeBlogService.findBlogInfo(blogId,userId);
            if(homeBlog!=null){
                //放到缓存中
                blogInfoMap = CommonUtils.objectToMap(homeBlog);
                redisUtils.hmset(Constants.REDIS_KEY_EBLOG+userId+"_"+blogId,blogInfoMap,Constants.USER_TIME_OUT);
            }
        }
        HomeBlog homeBlog = (HomeBlog) CommonUtils.mapToObject(blogInfoMap,HomeBlog.class);
        if(homeBlog==null){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //设置用户信息
        UserInfo userInfo = userInfoUtils.getUserInfo(homeBlog.getUserId());
        if(userInfo!=null){
            homeBlog.setUserName(userInfo.getName());
            homeBlog.setUserHead(userInfo.getHead());
            homeBlog.setProTypeId(userInfo.getProType());
            homeBlog.setHouseNumber(userInfo.getHouseNumber());
        }
        //设置是否喜欢过
        boolean isMember = redisUtils.isMember(Constants.EBLOG_LIKE_LIST+blogId,CommonUtils.getMyId());
        if(isMember){
            homeBlog.setIsLike(1);
        }else{
            homeBlog.setIsLike(0);
        }
        //设置是否为 已付稿费作品
        if(homeBlog.getUserId()>=13870&&homeBlog.getUserId()<=53870&&homeBlog.getRemunerationStatus()==0){
            if(homeBlog.getUserId()%2==0){
                Random random = new Random();
                double moneyNew = 10;
                int remunerationStatus = random.nextInt(4)+1;
                if(remunerationStatus==1){
                    moneyNew = 10;
                }else if(remunerationStatus==2){//20 50 100元
                    double rs2 = random.nextInt(3) ;
                    if(rs2==2){
                        moneyNew = 20;
                    }else if(rs2==1){
                        moneyNew = 50;
                    }else{
                        moneyNew = 10;
                    }
                }else if(remunerationStatus==3){
                    moneyNew = 10;
                }else if(remunerationStatus==4){
                    moneyNew = 20;
                }
                homeBlog.setRemunerationStatus(remunerationStatus);
                homeBlog.setRemunerationMoney(moneyNew);
            }
        }
//        HomeBlogLike homeBlogLike = homeBlogLikeService.checkHomeBlogLike(CommonUtils.getMyId(),blogId);
//        if(homeBlogLike!=null){
//            homeBlog.setIsLike(1);
//        }else{
//            homeBlog.setIsLike(0);
//        }
        //添加浏览量
        mqUtils.updateBlogCounts(homeBlog.getUserId(),homeBlog.getId(),3,1);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", homeBlog);
    }

    /***
     * 删除指定生活圈接口
     * @param userId 生活圈发布者用户ID
     * @param blogId 将要被删除的生活圈
     * @return
     */
    @Override
    public ReturnData delBlog(@PathVariable long userId,@PathVariable long blogId) {
        //判断操作人权限
        if(CommonUtils.getMyId()!=userId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限操作用户["+userId+"]的生活圈",new JSONObject());
        }
        //查询该条生活圈信息
        Map<String,Object> blogInfoMap = redisUtils.hmget(Constants.REDIS_KEY_EBLOG+userId+"_"+blogId);
        if(blogInfoMap==null||blogInfoMap.size()<=0){
            HomeBlog homeBlog = homeBlogService.findBlogInfo(blogId,userId);
            if(homeBlog!=null){
                blogInfoMap = CommonUtils.objectToMap(homeBlog);
            }
        }
        HomeBlog hb = (HomeBlog) CommonUtils.mapToObject(blogInfoMap,HomeBlog.class);
        if(hb==null){//不存在直接返回成功
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //开始更新删除状态
        homeBlogService.delBlog(blogId,userId);
        //判断是否为生活秀首页推荐数据
        if(hb.getSendType()==2){
            //删除生活秀首页推荐列表中对应的数据
            redisUtils.removeSetByValues(Constants.REDIS_KEY_EBLOGSET,hb);
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_EBLOG + userId+"_"+blogId, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 条件查询生活圈接口（查询所有类型的生活圈）redisUtils.addSet(Constants.REDIS_KEY_EBLOGSET,homeBlog);
     * @param userId     被查询用户ID 默认0查询所有
     * @param searchType 查询类型 0查看朋友圈 1查看关注 2查看兴趣话题 3查询指定用户
     * @param tags       被查询兴趣标签ID组合，逗号分隔例如：1,2,3 仅当searchType=2 时有效 默认传null
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findBlogList(@PathVariable long userId,@PathVariable int searchType,
                                   @PathVariable String tags,@PathVariable int page,@PathVariable int count) {
        //验证参数
        if(searchType<0||searchType>3){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"searchType参数有误",new JSONObject());
        }
        if(page<1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"page参数有误",new JSONObject());
        }
        if(count<0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"count参数有误",new JSONObject());
        }
        PageBean<HomeBlog> pageBean = null;
        switch (searchType) {
            case 0://0查看朋友的生活圈
                //从缓存中获取好友列表
                List list = null;
//                list = redisUtils.getList(Constants.REDIS_KEY_USERFRIENDLIST+CommonUtils.getMyId(),0,-1);
                list = userRelationShipUtils.getFirendList(CommonUtils.getMyId());
                if(list==null||list.size()<=0){//缓存无好友列表存在 直接返回
                    pageBean = new PageBean<HomeBlog>();
                    pageBean.setList(new ArrayList<>());
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",pageBean);
                }
                String firendUserIds = "";//好友ID组合
                for (int i = 0; i < list.size(); i++) {
                    HashMap map = (HashMap) list.get(i);
                    if(map!=null&&map.size()>0){
                        Object groupName = map.get("groupName");
                        if(groupName==null||"黑名单".equals(groupName.toString())){
                            continue;
                        }
                        ArrayList userList =(ArrayList) map.get("userList");
                        if(userList==null||userList.size()<=0){
                            continue;
                        }
                        for (int j = 0; j <userList.size() ; j++) {
                            UserRelationShip userRelationShip = (UserRelationShip) userList.get(j);
                            if(userRelationShip==null){
                                continue;
                            }
//                            if(j==userList.size()-1){
//                                firendUserIds += userRelationShip.getFriendId()+"";
//                            }else{
//                                firendUserIds += userRelationShip.getFriendId()+",";
//                            }
                            firendUserIds += userRelationShip.getFriendId()+",";
                        }
                    }
                }
                //将自己加入查询列表中
                firendUserIds = firendUserIds+CommonUtils.getMyId();
                pageBean = homeBlogService.findBlogListByFirend(CommonUtils.getMyId(),firendUserIds.split(","),0,0,page,count);
                break;
            case 1://1查看关注人的生活圈
                //获取我关注的人的列表
                String[] followArray = null;
                String followUserIds = followInfoUtils.getFollowInfo(CommonUtils.getMyId());
                //将自己加入查询列表中  关注暂时不查自己的
//                followUserIds = followUserIds+","+CommonUtils.getMyId();
                if(!CommonUtils.checkFull(followUserIds)){
                    followArray = followUserIds.split(",");
                }
                if(followArray==null||followArray.length<=0){//无关注列表存在 直接返回
                    pageBean = new PageBean<HomeBlog>();
                    pageBean.setList(new ArrayList<>());
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",pageBean);
                }
                pageBean = homeBlogService.findBlogListByFirend(CommonUtils.getMyId(),followArray,0,0,page,count);
                break;
            case 2://2查看兴趣话题
                if(CommonUtils.checkFull(tags)){
                    return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"tags参数有误",new JSONObject());
                }
                pageBean = homeBlogService.findBlogListByTags(tags.split(","),0,CommonUtils.getMyId(),page,count);
                break;
            case 3://3查询指定用户
                if(userId<0){
                    return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
                }
                //判断是不是查自己的信息
                int type = 0;
                if(userId!=CommonUtils.getMyId()){
                    type = 1;
                }
                pageBean = homeBlogService.findBlogListByUserId(userId,type,0,page,count);
                break;
        }
        if(pageBean==null){
            pageBean = new PageBean<HomeBlog>();
            pageBean.setList(new ArrayList<>());
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",pageBean);
        }
        List<HomeBlog> list  = pageBean.getList();
        for(int i=0;i<list.size();i++){
            HomeBlog homeBlog = list.get(i);
            if(homeBlog==null){
                continue;
            }
            //设置用户信息
            UserInfo userInfo = userInfoUtils.getUserInfo(homeBlog.getUserId());
            if(userInfo!=null){
                homeBlog.setUserName(userInfo.getName());
                homeBlog.setUserHead(userInfo.getHead());
                homeBlog.setProTypeId(userInfo.getProType());
                homeBlog.setHouseNumber(userInfo.getHouseNumber());
            }
            //设置是否喜欢过状态
            boolean isMember = redisUtils.isMember(Constants.EBLOG_LIKE_LIST+homeBlog.getId(),CommonUtils.getMyId());
            if(isMember){
                homeBlog.setIsLike(1);
            }else{
                homeBlog.setIsLike(0);
            }
            //设置是否为 已付稿费作品
            if(homeBlog.getUserId()>=13870&&homeBlog.getUserId()<=53870&&homeBlog.getRemunerationStatus()==0){
                if(homeBlog.getUserId()%2==0){
                    Random random = new Random();
                    double moneyNew = 10;
                    int remunerationStatus = random.nextInt(4)+1;
                    if(remunerationStatus==1){
                        moneyNew = 10;
                    }else if(remunerationStatus==2){//20 50 100元
                        double rs2 = random.nextInt(3) ;
                        if(rs2==2){
                            moneyNew = 20;
                        }else if(rs2==1){
                            moneyNew = 50;
                        }else{
                            moneyNew = 10;
                        }
                    }else if(remunerationStatus==3){
                        moneyNew = 10;
                    }else if(remunerationStatus==4){
                        moneyNew = 20;
                    }
                    homeBlog.setRemunerationStatus(remunerationStatus);
                    homeBlog.setRemunerationMoney(moneyNew);
                    homeBlogService.updateGradeBlog(homeBlog);
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 条件查询生活秀、今日现场、娱乐圈接口、医生圈、律师圈接口（只查询视频内容）
     * @param userId     被查询用户ID 默认0查询所有
     * @param searchType 查询类型 0查询首页推荐 1查同城 2查看朋友 3查询关注 4查询兴趣标签 5查询指定用户 6查询附近的生活秀（家门口的生活圈）
     *                   7查询今日现场首页 8查询今日现场同城 9查询今日现场关注  10查询娱乐圈首页  11查询娱乐圈关注 12今日现场查询指定用户
     *                   13娱乐圈查询指定用户  14查询待稿费审核视频列表 15查询稿费已审核视频 16查询医生圈主页 17查询医生圈关注 18查询医生圈指定用户
     *                   19查询律师圈主页 20查询律师圈关注 21查询律师圈指定用户
     * @param tags       被查询兴趣标签ID组合，逗号分隔例如：1,2,3  仅当searchType=4 时有效 默认传null
     * @param cityId     城市ID 当searchType=1时有效 默认传0
     * @param lat        纬度 小数点后6位 当searchType=6时有效
     * @param lon        经度 小数点后6位 当searchType=6时有效
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findBlogVideoList(@PathVariable long userId,@PathVariable int searchType,
                                        @PathVariable String tags,@PathVariable int cityId,
                                        @PathVariable double lat,@PathVariable double lon,
                                        @PathVariable int page,@PathVariable int count) {
        //验证参数
        if(searchType<0||searchType>21){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"searchType参数有误",new JSONObject());
        }
        if(page<1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"page参数有误",new JSONObject());
        }
        if(count<0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"count参数有误",new JSONObject());
        }
        PageBean<HomeBlog> pageBean = null;
        Map<String,Distance> distanceMap = new HashMap<>();
//        String str = "^(-)?[0-9]{1,3}+(.[0-9]{1,6})?$";//匹配（正负）整数3位，小数6位的正则表达式
        switch (searchType) {
            case 0://0查询首页推荐
                //首页数据初试算法 时间顺序显示
//                long countTotal = redisUtils.getListSize(Constants.REDIS_KEY_EBLOGLIST);
//                //处理服务器宕机问题
//                if(countTotal<=0){
//                    PageBean<HomeBlog> pb = homeBlogService.findBlogListBylikeCount(Constants.EBLOG_LIKE_COUNT-1,1,Constants.REDIS_KEY_EBLOGLIST_COUNT);
//                    List<HomeBlog> list = pb.getList();
//                    if(list!=null){
//                        //放入缓存中
//                        redisUtils.pushList(Constants.REDIS_KEY_EBLOGLIST, list, 0);
//                    }
//                }
//                int pageCount = page*count;
//                if(pageCount>countTotal){
//                    pageCount = -1;
//                }else{
//                    pageCount = pageCount-1;
//                }
//                List eblogList = redisUtils.getList(Constants.REDIS_KEY_EBLOGLIST, (page-1)*count, pageCount);
                //首页数据初试算法 时间顺序显示结束

                //首页数据改为随机算法
                long countTotal = redisUtils.getSetSize(Constants.REDIS_KEY_EBLOGSET);
                if(countTotal<=0){
                    redisUtils.expire(Constants.REDIS_KEY_EBLOGSET,0);
                    PageBean<HomeBlog> pb = homeBlogService.findBlogListBylikeCount(Constants.EBLOG_LIKE_COUNT-1,1,Constants.REDIS_KEY_EBLOGLIST_COUNT);
                    List<HomeBlog> list = pb.getList();
                    if(list!=null){
                        //放入缓存中
                        redisUtils.addSet(Constants.REDIS_KEY_EBLOGSET, list.toArray());
                    }
                }
                Set<Object> set = redisUtils.distinctRandomMembers(Constants.REDIS_KEY_EBLOGSET,count);
                pageBean = new PageBean<>();
                pageBean.setSize(set.size());
                pageBean.setPageNum(page);
                pageBean.setPageSize(count);
                pageBean.setList(new ArrayList(set));
                break;
            case 1://1查同城
                if(cityId<0){
                    return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"cityId参数有误",new JSONObject());
                }
                //验证参数
//                if(lon<0||lat<0||!String.valueOf(lon).matches(str) || !String.valueOf(lat).matches(str)){
//                    return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"位置坐标参数格式有误",new JSONObject());
//                }
                pageBean = homeBlogService.findBlogListByCityId(userId,cityId,1,page,count);
                break;
            case 2://0查看朋友的生活秀
                //从缓存中获取好友列表
                List list = null;
                list = userRelationShipUtils.getFirendList(CommonUtils.getMyId());
                if(list==null||list.size()<=0){//缓存无好友列表存在 直接返回
                    pageBean = new PageBean<HomeBlog>();
                    pageBean.setList(new ArrayList<>());
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",pageBean);
                }
                String firendUserIds = "";//好友ID组合
                for (int i = 0; i < list.size(); i++) {
                    HashMap map = (HashMap) list.get(i);
                    if(map!=null&&map.size()>0){
                        Object groupName = map.get("groupName");
                        if(groupName==null||"黑名单".equals(groupName.toString())){
                            continue;
                        }
                        ArrayList userList =(ArrayList) map.get("userList");
                        if(userList==null||userList.size()<=0){
                            continue;
                        }
                        for (int j = 0; j <userList.size() ; j++) {
                            UserRelationShip userRelationShip = (UserRelationShip) userList.get(j);
                            if(userRelationShip==null){
                                continue;
                            }
//                            if(j==userList.size()-1){
//                                firendUserIds += userRelationShip.getFriendId()+"";
//                            }else{
//                                firendUserIds += userRelationShip.getFriendId()+",";
//                            }
                            firendUserIds += userRelationShip.getFriendId()+",";
                        }
                    }
                }
                //将自己加入查询列表中
                firendUserIds = firendUserIds+CommonUtils.getMyId();
                pageBean = homeBlogService.findBlogListByFirend(CommonUtils.getMyId(),firendUserIds.split(","),1,0,page,count);
                break;
            case 3://1查看关注人的生活秀
                //获取我关注的人的列表
                String[] followArray = null;
                String followUserIds = followInfoUtils.getFollowInfo(CommonUtils.getMyId());
                //将自己加入查询列表中 关注暂时不查自己的
//                followUserIds = followUserIds+","+CommonUtils.getMyId();
                if(!CommonUtils.checkFull(followUserIds)){
                    followArray = followUserIds.split(",");
                }
                if(followArray==null||followArray.length<=0){//无关注列表存在 直接返回
                    pageBean = new PageBean<HomeBlog>();
                    pageBean.setList(new ArrayList<>());
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",pageBean);
                }
                pageBean = homeBlogService.findBlogListByFirend(CommonUtils.getMyId(),followArray,1,0,page,count);
                break;
            case 4://2查看兴趣话题
                String[] array = null;
                if(!CommonUtils.checkFull(tags)){
                    array = tags.split(",");
                }
                pageBean = homeBlogService.findBlogListByTags(array,1,CommonUtils.getMyId(),page,count);
                break;
            case 5://3查询指定用户
                if(userId<0){
                    return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
                }
                //判断是不是查自己的信息
                int type = 0;
                if(userId!=CommonUtils.getMyId()){
                    type = 1;
                }
                pageBean = homeBlogService.findBlogListByUserId(userId,type,1,page,count);
                break;
            case 6://6查询附近的生活圈（家门口的生活圈）
                //验证参数
//                String str = "^(-)?[0-9]{1,3}+(.[0-9]{1,6})?$";//匹配（正负）整数3位，小数6位的正则表达式
//                if(lon<0||lat<0||!String.valueOf(lon).matches(str) || !String.valueOf(lat).matches(str)){
//                    return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"位置坐标参数格式有误",new JSONObject());
//                }
                GeoResults<GeoLocation<String>> geoResults =  redisUtils.getPosition(Constants.REDIS_KEY_USER_POSITION_LIST,lat,lon, Constants.RADIUS,0,Constants.LIMIT);
                Iterator iter  = geoResults.getContent().iterator();
                String nearUserIds = "";
                while (iter.hasNext())
                {
                    GeoResult<GeoLocation<String>> geoResult = (GeoResult<GeoLocation<String>>) iter.next();
                    if(geoResult!=null){
                        GeoLocation<String> GeoLocation= geoResult.getContent();
                        Distance distance = geoResult.getDistance();
                        if(GeoLocation==null||distance==null){
                            continue;
                        }
                        String userIdString = GeoLocation.getName();
                        if(CommonUtils.checkFull(userIdString)||userIdString.equals(CommonUtils.getMyId()+"")){
                            continue;
                        }
                        nearUserIds +=userIdString+",";
                        distanceMap.put(userIdString,distance);
                    }
                }
                pageBean = homeBlogService.findBlogListByFirend(CommonUtils.getMyId(),nearUserIds.split(","),1,1,page,count);
                break;
            case 7://7查询今日现场首页
                pageBean = homeBlogService.findBlogListByTags(new String[]{"40"},1,CommonUtils.getMyId(),page,count);
                break;
            case 8://8查询今日现场同城
                if(cityId<0){
                    return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"cityId参数有误",new JSONObject());
                }
                //验证参数
//                if(lon<0||lat<0||!String.valueOf(lon).matches(str) || !String.valueOf(lat).matches(str)){
//                    return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"位置坐标参数格式有误",new JSONObject());
//                }
                pageBean = homeBlogService.findBlogListByCityId(userId,cityId,2,page,count);
                break;
            case 9://9查询今日现场关注
                //获取我关注的人的列表
                String[] followArray1 = null;
                String followUserIds1 = followInfoUtils.getFollowInfo(CommonUtils.getMyId());
                //将自己加入查询列表中 关注暂时不查自己的
//                followUserIds = followUserIds+","+CommonUtils.getMyId();
                if(!CommonUtils.checkFull(followUserIds1)){
                    followArray1 = followUserIds1.split(",");
                }
                if(followArray1==null||followArray1.length<=0){//无关注列表存在 直接返回
                    pageBean = new PageBean<HomeBlog>();
                    pageBean.setList(new ArrayList<>());
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",pageBean);
                }
                pageBean = homeBlogService.findBlogListByFirend(CommonUtils.getMyId(),followArray1,2,0,page,count);
                break;
            case 10://10查询娱乐圈首页
                pageBean = homeBlogService.findBlogListByTags(new String[]{"39"},1,CommonUtils.getMyId(),page,count);
                break;
            case 11://11查询娱乐圈关注
                //获取我关注的人的列表
                String[] followArray2 = null;
                String followUserIds2 = followInfoUtils.getFollowInfo(CommonUtils.getMyId());
                //将自己加入查询列表中 关注暂时不查自己的
//                followUserIds = followUserIds+","+CommonUtils.getMyId();
                if(!CommonUtils.checkFull(followUserIds2)){
                    followArray2 = followUserIds2.split(",");
                }
                if(followArray2==null||followArray2.length<=0){//无关注列表存在 直接返回
                    pageBean = new PageBean<HomeBlog>();
                    pageBean.setList(new ArrayList<>());
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",pageBean);
                }
                pageBean = homeBlogService.findBlogListByFirend(CommonUtils.getMyId(),followArray2,3,0,page,count);
                break;
            case 12://今日现场查询指定用户
                if(userId<0){
                    return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
                }
                //判断是不是查自己的信息
                int type1 = 0;
                if(userId!=CommonUtils.getMyId()){
                    type1 = 1;
                }
                pageBean = homeBlogService.findBlogListByUserId(userId,type1,2,page,count);
                break;
            case 13://娱乐圈查询指定用户
                if(userId<0){
                    return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
                }
                //判断是不是查自己的信息
                int type2 = 0;
                if(userId!=CommonUtils.getMyId()){
                    type2 = 1;
                }
                pageBean = homeBlogService.findBlogListByUserId(userId,type2,3,page,count);
                break;
            case 14://查询待稿费审核视频列表
                String[] tagArray = null;
                if(!CommonUtils.checkFull(tags)){
                    tagArray = tags.split(",");
                }
                pageBean = homeBlogService.findBlogListByTags2(tagArray,1,userId,page,count);
                break;
            case 15://查询已审核稿费视频列表
                String[] tagArray2 = null;
                if(!CommonUtils.checkFull(tags)){
                    tagArray2 = tags.split(",");
                }
                pageBean = homeBlogService.findBlogListByTags2(tagArray2,0,userId,page,count);
                break;
            case 16://16查询医生圈首页
                pageBean = homeBlogService.findBlogListByTags(new String[]{"41"},1,CommonUtils.getMyId(),page,count);
                break;
            case 17://17查询医生圈关注
                //获取我关注的人的列表
                String[] followArray3 = null;
                String followUserIds3 = followInfoUtils.getFollowInfo(CommonUtils.getMyId());
                //将自己加入查询列表中 关注暂时不查自己的
//                followUserIds = followUserIds+","+CommonUtils.getMyId();
                if(!CommonUtils.checkFull(followUserIds3)){
                    followArray3 = followUserIds3.split(",");
                }
                if(followArray3==null||followArray3.length<=0){//无关注列表存在 直接返回
                    pageBean = new PageBean<HomeBlog>();
                    pageBean.setList(new ArrayList<>());
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",pageBean);
                }
                pageBean = homeBlogService.findBlogListByFirend(CommonUtils.getMyId(),followArray3,4,0,page,count);
                break;
            case 18://18查询医生圈指定用户
                if(userId<0){
                    return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
                }
                //判断是不是查自己的信息
                int type3 = 0;
                if(userId!=CommonUtils.getMyId()){
                    type3 = 1;
                }
                pageBean = homeBlogService.findBlogListByUserId(userId,type3,4,page,count);
                break;
            case 19://19查询律师圈首页
                pageBean = homeBlogService.findBlogListByTags(new String[]{"42"},1,CommonUtils.getMyId(),page,count);
                break;
            case 20://20查询律师圈关注
                //获取我关注的人的列表
                String[] followArray4 = null;
                String followUserIds4 = followInfoUtils.getFollowInfo(CommonUtils.getMyId());
                //将自己加入查询列表中 关注暂时不查自己的
//                followUserIds = followUserIds+","+CommonUtils.getMyId();
                if(!CommonUtils.checkFull(followUserIds4)){
                    followArray4 = followUserIds4.split(",");
                }
                if(followArray4==null||followArray4.length<=0){//无关注列表存在 直接返回
                    pageBean = new PageBean<HomeBlog>();
                    pageBean.setList(new ArrayList<>());
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",pageBean);
                }
                pageBean = homeBlogService.findBlogListByFirend(CommonUtils.getMyId(),followArray4,5,0,page,count);
                break;
            case 21://21查询律师圈指定用户
                if(userId<0){
                    return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
                }
                //判断是不是查自己的信息
                int type4 = 0;
                if(userId!=CommonUtils.getMyId()){
                    type4 = 1;
                }
                pageBean = homeBlogService.findBlogListByUserId(userId,type4,5,page,count);
                break;
        }
        if(pageBean==null){
            pageBean = new PageBean<HomeBlog>();
            pageBean.setList(new ArrayList<>());
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",pageBean);
        }
        List<HomeBlog> list  = pageBean.getList();
        for(int i=0;i<list.size();i++){
            HomeBlog homeBlog = list.get(i);
            if(homeBlog==null){
                continue;
            }
            //设置用户信息
            UserInfo userInfo = userInfoUtils.getUserInfo(homeBlog.getUserId());
            if(userInfo!=null){
                homeBlog.setUserName(userInfo.getName());
                homeBlog.setUserHead(userInfo.getHead());
                homeBlog.setProTypeId(userInfo.getProType());
                homeBlog.setHouseNumber(userInfo.getHouseNumber());
            }
            //添加位置信息
            if(searchType==1){
                homeBlog.setDistance(CommonUtils.getShortestDistance(lon,lat,homeBlog.getLongitude(),homeBlog.getLatitude()));
            }
            if(searchType==6){
                Distance distance = distanceMap.get(homeBlog.getUserId()+"");
                homeBlog.setDistance(new BigDecimal(distance.getValue()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            //设置是否喜欢过状态
            boolean isMember = redisUtils.isMember(Constants.EBLOG_LIKE_LIST+homeBlog.getId(),CommonUtils.getMyId());
            if(isMember){
                homeBlog.setIsLike(1);
            }else{
                homeBlog.setIsLike(0);
            }
            //设置是否为 已付稿费作品
            if(homeBlog.getUserId()>=13870&&homeBlog.getUserId()<=53870&&homeBlog.getRemunerationStatus()==0&&searchType!=14&&searchType!=15){
                if(homeBlog.getUserId()%2==0){
                    Random random = new Random();
                    double moneyNew = 10;
                    int remunerationStatus = random.nextInt(4)+1;
                    if(remunerationStatus==1){
                        moneyNew = 10;
                    }else if(remunerationStatus==2){//20 50 100元
                        double rs2 = random.nextInt(3) ;
                        if(rs2==2){
                            moneyNew = 20;
                        }else if(rs2==1){
                            moneyNew = 50;
                        }else{
                            moneyNew = 10;
                        }
                    }else if(remunerationStatus==3){
                        moneyNew = 10;
                    }else if(remunerationStatus==4){
                        moneyNew = 20;
                    }
                    homeBlog.setRemunerationStatus(remunerationStatus);
                    homeBlog.setRemunerationMoney(moneyNew);
                    homeBlogService.updateGradeBlog(homeBlog);
                }
            }
        }
        if(list.size()<count&&searchType==6){//家门口补充假数据
            Random random = new Random();
            String userIds = "";
            for(int i=0;i<100;i++){
                long newUserId = random.nextInt(40000) + 13870;
                if(i==0){
                    userIds = newUserId+"";
                }else{
                    userIds += ","+newUserId;
                }
            }
            PageBean<HomeBlog> newPageBean = homeBlogService.findBlogListByFirend(CommonUtils.getMyId(),userIds.split(","),1,0,1,100);
            List<HomeBlog> newList  = newPageBean.getList();
            for (int i = 0; i < newList.size(); i++) {
                HomeBlog homeBlog = newList.get(i);
                if(homeBlog==null){
                    continue;
                }
                //设置用户信息
                UserInfo userInfo = userInfoUtils.getUserInfo(homeBlog.getUserId());
                if(userInfo!=null){
                    homeBlog.setUserName(userInfo.getName());
                    homeBlog.setUserHead(userInfo.getHead());
                    homeBlog.setProTypeId(userInfo.getProType());
                    homeBlog.setHouseNumber(userInfo.getHouseNumber());
                }
                //添加位置信息
                int radius = random.nextInt(10000)+500;
                homeBlog.setDistance(radius);
                //设置是否喜欢过状态
                boolean isMember = redisUtils.isMember(Constants.EBLOG_LIKE_LIST+homeBlog.getId(),CommonUtils.getMyId());
                if(isMember){
                    homeBlog.setIsLike(1);
                }else{
                    homeBlog.setIsLike(0);
                }
                //设置是否为 已付稿费作品
                if(homeBlog.getUserId()>=13870&&homeBlog.getUserId()<=53870&&homeBlog.getRemunerationStatus()==0&&searchType!=14&&searchType!=15){
                    if(homeBlog.getUserId()%2==0){
                        Random random2 = new Random();
                        double moneyNew = 10;
                        int remunerationStatus = random2.nextInt(4)+1;
                        if(remunerationStatus==1){
                            moneyNew = 10;
                        }else if(remunerationStatus==2){//20 50 100元
                            double rs2 = random2.nextInt(3) ;
                            if(rs2==2){
                                moneyNew = 20;
                            }else if(rs2==1){
                                moneyNew = 50;
                            }else{
                                moneyNew = 10;
                            }
                        }else if(remunerationStatus==3){
                            moneyNew = 10;
                        }else if(remunerationStatus==4){
                            moneyNew = 20;
                        }
                        homeBlog.setRemunerationStatus(remunerationStatus);
                        homeBlog.setRemunerationMoney(moneyNew);
                        homeBlogService.updateGradeBlog(homeBlog);
                    }
                }
                list.add(homeBlog);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 检测当前登录用户与被检测用户之间的好友关系和关注关系
     * @param userId     被检测的用户ID
     * @return
     */
    @Override
    public ReturnData checkFirendAndFollowStatus(@PathVariable long userId) {
        //验证参数
        if(userId<0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
        }
        //判断自己是否与该用户是好友关系
        int isFriend = 0;//是否为好友 0不是好友  1是好友
        List list = null;
        list = userRelationShipUtils.getFirendList(CommonUtils.getMyId());
        if(list!=null){//缓存一定存在好友关系 否则该账号异常
            boolean forFlag = false;
            for(int i=0;i<list.size();i++){
                if(forFlag){
                    break;
                }
                Map map = (Map) list.get(i);
                if(map!=null&&map.size()>0){
                    List userList = (List) map.get("userList");
                    if(userList==null||userList.size()<=0){
                        continue;
                    }
                    for(int j=0;j <userList.size();j++){
                        UserRelationShip userRelationShip = (UserRelationShip) userList.get(j);
                        if(userRelationShip != null&&userRelationShip.getFriendId()==userId){
                            isFriend = 1;
                            forFlag = true;
                            break;
                        }
                    }
                }
            }
        }
        //查询关注关系
        int isFollow = 0;//是否已关注 0未关注 1已关注
        String[] followArray = null;
        String followUserIds = followInfoUtils.getFollowInfo(CommonUtils.getMyId());
        if(!CommonUtils.checkFull(followUserIds)){
            followArray = followUserIds.split(",");
        }
        if(followArray!=null){
            for (int i = 0; i < followArray.length; i++) {
                if(userId==Long.parseLong(followArray[i])){
                    isFollow = 1;
                    break;
                }
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("isFriend",isFriend);
        map.put("isFollow",isFollow);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    public static boolean ckcometnP(String str){

        if(!CommonUtils.checkFull(str)){

            String regex = "<p>(.*?)</p>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);

            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                matcher.appendReplacement(sb, "");
            }
            matcher.appendTail(sb);

            if(CommonUtils.checkFull(sb.toString())){
                return true;
            }
            return false;
        }
        return true;
    }

}
