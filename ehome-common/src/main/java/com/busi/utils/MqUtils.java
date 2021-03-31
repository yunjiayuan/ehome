package com.busi.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息平台工具方法
 * author：SunTianJie
 * create time：2018/8/22 17:27
 */
@Component
public class MqUtils {

    @Autowired
    MQProducer MQProducer;

    /***
     * 更新钱包余额和钱包明细
     * @param userId          用户ID
     * @param tradeType       交易类型 0充值 1提现,2转账转入,3转账转出,4红包转入,5红包转出,6 点子转入,7点子转出,8悬赏转入,9悬赏转出,10兑换转入,11兑换支出,12红包退款,
     *                                  13二手购买转出,14二手出售转入,15家厨房转出,16家厨房转入,17购买会员支出,18游戏支出，19游戏转入,20任务奖励转入,21系统奖励转入,
     *                                  22购买靓号支出,23小时工支出,24小时工转入,25订座点菜支出,26订座点菜转入,27楼店交易转入,28楼店交易支出,29楼店缴纳保证金支出,
     *                                  30医生圈资费支出,31律师圈资费支出,32医生圈资费转入,33律师圈资费转入,34旅游费用支出,35旅游费用转入,36转账退款,37酒店民宿支出，
     *                                  38酒店民宿转入，39药费支出，40药费转入，41砸蛋奖励转入,42元老级会员返现,，43隐形商家费用转入,44隐形商家费用支出，45找人倾诉费用转入,
     *                                  46找人倾诉费用支出，47需求汇租房费用转入,48需求汇租房费用支出，49需求汇购房费用转入,50需求汇购房费用支出
     * @param currencyType    交易支付类型 0钱(真实人民币),1家币,2家点
     * @param tradeMoney      交易金额
     */
    public void sendPurseMQ(long userId,int tradeType,int currencyType,double tradeMoney){
        //调用MQ同步用户登录信息
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", 7);//interfaceType  7:表示更新钱包余额和钱包明细
        JSONObject content = new JSONObject();
        content.put("userId",userId);//用户ID
        content.put("tradeType",tradeType);
        content.put("currencyType",currencyType);
        content.put("tradeMoney",tradeMoney);
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        MQProducer.sendMsg(activeMQQueue,sendMsg);
    }

    /***
     * 提现同步到微信、支付宝、银行卡
     * @param userId     用户ID
     * @param id         订单ID
     * @param type       同步类型 0微信 1支付宝 2银行卡
     * @param openId     微信或支付宝的身份标识
     * @param tradeMoney 金额
     */
    public void sendCashOutMQ(long userId,String id,int type,String openId,double tradeMoney){
        //调用MQ同步用户登录信息
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", 17);//interfaceType  17:表示提现
        JSONObject content = new JSONObject();
        content.put("userId",userId);//用户ID
        content.put("id",id);//订单ID
        content.put("type",type);
        content.put("openId",openId);
        content.put("tradeMoney",tradeMoney);
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        MQProducer.sendMsg(activeMQQueue,sendMsg);
    }
    /***
     * 删除图片 调用MQ同步删除
     * @param userId       图片主人ID
     * @param delImageUrls 将要删除的图片地址组合，逗号分隔
     */
    public void sendDeleteImageMQ(long userId,String delImageUrls){

        //调用MQ同步
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "5");//interfaceType 5删除图片
        JSONObject content = new JSONObject();
        content.put("delImageUrls",delImageUrls);
        content.put("userId",userId);
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        MQProducer.sendMsg(activeMQQueue,sendMsg);
    }

    /***
     * 新增任务 任务系统同步
     * @param userId   当前用户ID
     * @param taskType 任务类型 0、一次性任务   1 、每日任务
     * @param sortTask 任务ID
     */
    public void sendTaskMQ(long userId, int taskType, long sortTask){

        //调用MQ同步
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "6");//interfaceType 5同步任务系统
        JSONObject content = new JSONObject();
        content.put("userId",userId);
        content.put("sortTask",sortTask);
        content.put("taskType",taskType);
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        MQProducer.sendMsg(activeMQQueue,sendMsg);
    }

    /***
     * 新增浏览记录 系统同步
     * @param userId   当前用户ID
     * @param infoId   公告ID
     * @param title 标题
     * @param afficheType 公告类型
     */
    public void sendLookMQ(long userId,long infoId,String title ,int afficheType){

        //调用MQ同步浏览记录到浏览记录表
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "8");//interfaceType 8同步浏览记录
        JSONObject content = new JSONObject();
        content.put("myId",userId);
        content.put("infoId",infoId);
        content.put("title",title);
        content.put("afficheType",afficheType);
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        MQProducer.sendMsg(activeMQQueue,sendMsg);
    }

    /***
     * 发送短信
     * @param phone     将要发送短信的手机号
     * @param phoneCode 短信验证码
     * @param phoneType 短信类型 0注册验证码  1找回支付密码验证码 2安全中心绑定手机验证码 3安全中心解绑手机验证码
     *                            4手机短信找回登录密码验证码  5手机短信修改密码验证码 6短信邀请新用户注册 7开通家电个人信息验证
     *                            8邀请好友参加抗击疫情评选活动投票
     */
    public void sendPhoneMessage(String phone,String phoneCode,int phoneType){

        //调用MQ同步浏览记录到浏览记录表
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "0");//interfaceType  0 表示发送手机短信
        JSONObject content = new JSONObject();
        content.put("phone",phone);
        content.put("phoneCode",phoneCode);
        content.put("phoneType",phoneType);
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        MQProducer.sendMsg(activeMQQueue,sendMsg);
    }

    /***
     * 发送邮件
     * @param userId    用户ID
     * @param email     将要发送邮件的邮箱地址
     * @param emailType 邮件类型 0绑定密保邮箱的验证邮件,1解绑密保邮箱的验证邮件,2修改密码的验证邮件,3找回密码的验证邮件
     * @param code      邮件验证码
     * @param userName  用户名
     */
    public void sendEmailMessage(long userId,String email,int emailType,String code,String userName){

        //调用MQ同步浏览记录到浏览记录表
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "1");//interfaceType  0 表示发送邮件
        JSONObject content = new JSONObject();
        content.put("userId",userId);
        content.put("email",email);
        content.put("emailType",emailType);
        content.put("code",code);
        content.put("userName",userName);
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        MQProducer.sendMsg(activeMQQueue,sendMsg);
    }

    /***
     * 新增足迹 任务系统同步
     * @param userId       用户ID
     * @param title        标题
     * @param imgUrl       图片路径
     * @param videoUrl     视频路径     1个
     * @param audioUrl     音频路径     1个
     * @param infoId       信息id 公告ID和分类ID(用,分隔，格式：123,4):1婚恋交友,2二手手机,3寻人,4寻物,5失物招领,6其他（注：后续添加）
     * @param footmarkType //足迹类型 0.默认全部 1.发布公告 2.发布家博 3.图片上传 4.音频上传 5.视频上传  6记事
     */
    public void sendFootmarkMQ(long userId,String title,String imgUrl,String videoUrl,String audioUrl,String infoId,int footmarkType){

        //调用MQ同步
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "9");//interfaceType 9同步足迹系统
        JSONObject content = new JSONObject();
        content.put("userId",userId);
        content.put("title",title);
        content.put("imgUrl",imgUrl);
        content.put("videoUrl",videoUrl);
        content.put("audioUrl",audioUrl);
        content.put("infoId",infoId);
        content.put("footmarkType",footmarkType);
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        MQProducer.sendMsg(activeMQQueue,sendMsg);
    }

    /***
     * 手机号或第三方平台新用户注册时同步安全中心
     * @param userId
     * @param phone                密保手机(绑定手机，由于手机登录为同一个手机号)
     * @param otherPlatformType    是否绑定第三方平台账号，0：未绑定, 1：绑定QQ账号，2：绑定微信账号，3：绑定新浪微博账号
     * @param otherPlatformAccount 第三方平台账号名称
     * @param otherPlatformKey     第三方平台账号key
     */
    public void sendUserAccountSecurityMQ(long userId,String phone,int otherPlatformType,String otherPlatformAccount,String otherPlatformKey){

        //调用MQ同步
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "10");//interfaceType 10同步安全中心
        JSONObject content = new JSONObject();
        content.put("userId",userId);
        content.put("phone",phone);
        content.put("otherPlatformType",otherPlatformType);
        content.put("otherPlatformAccount",otherPlatformAccount);
        content.put("otherPlatformKey",otherPlatformKey);
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        MQProducer.sendMsg(activeMQQueue,sendMsg);
    }

    /***
     * 更新粉丝数
     * @param userId
     * @param followCounts 正数为新增粉丝的粉丝数 负数为减少的粉丝数
     */
    public void updateFollowCounts(long userId,int followCounts){

        //调用MQ同步
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "11");//interfaceType 11更新粉丝数
        JSONObject content = new JSONObject();
        content.put("userId",userId);
        content.put("followCounts",followCounts);
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        MQProducer.sendMsg(activeMQQueue,sendMsg);
    }

    /***
     * 更新生活圈评论数、点赞数、浏览量、转发量
     * @param userId   生活圈主人ID
     * @param blogId   生活圈主键ID
     * @param type     0点赞 1评论 2转发 3浏览
     * @param count    变化的具体数值  可为正负数 正数代表增加  负数代表减少
     */
    public void updateBlogCounts(long userId,long blogId,int type,int count){

        //调用MQ同步
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "12");//interfaceType 12更新生活圈评论数、点赞数、浏览量、转发量
        JSONObject content = new JSONObject();
        content.put("userId",userId);
        content.put("blogId",blogId);
        content.put("count",count);
        content.put("type",type);
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        MQProducer.sendMsg(activeMQQueue,sendMsg);
    }

    /***
     * 新增消息系统同步
     * @param userId       发出消息用户
     * @param replayId        被回复用户ID
     * @param masterId        博主用户ID
     * @param blog              博文ID
     * @param origBlogId      原始博文ID（只用于转发）
     * @param commentId     评论ID
     * @param contents     消息内容
     * @param newsType       消息类型 0评论 1回复 2赞 3转发
     */
    public void addMessage(long userId,long replayId,long masterId,long blog,long origBlogId,long commentId,String contents,int newsType){

        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "13");//interfaceType 9同步消息系统
        JSONObject content = new JSONObject();
        content.put("userId",userId);
        content.put("replayId",replayId);
        content.put("masterId",masterId);
        content.put("blog",blog);
        content.put("origBlogId",origBlogId);
        content.put("commentId",commentId);
        content.put("content",contents);
        content.put("newsType",newsType);
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        MQProducer.sendMsg(activeMQQueue,sendMsg);
    }

    /***
     * 更新生活圈 评论回复数
     * @param commentId  将要修改的评论ID
     * @param count      变化的具体数值  可为正负数 正数代表增加  负数代表减少
     */
    public void updateCommentCounts(long commentId,int count){

        //调用MQ同步
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "14");//interfaceType 14更新生活圈 评论回复数
        JSONObject content = new JSONObject();
        content.put("commentId",commentId);
        content.put("count",count);
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        MQProducer.sendMsg(activeMQQueue,sendMsg);
    }

    /***
     * 新增用户奖励记录
     * @param userId          用户ID
     * @param rewardType      奖励类型 0红包雨奖励 1新人注册奖励 2分享码邀请别人注册奖励 3生活圈首次发布视频奖励 4生活圈10赞奖励 5生活圈100赞奖励 6生活圈10000赞奖励 7是一级稿费作品 8是二级稿费作品
     *                                  9是三级稿费作品 10是四级稿费作品  11邀请商家入驻奖励（订座） 12邀请商家入驻奖励（旅游） 13邀请商家入驻奖励（酒店民宿） 14邀请商家入驻奖励（药店）
     * @param rewardMoneyType 奖励金额类型 0表示人民币  1表示其他（预留）
     * @param rewardMoney     奖励的具体金额
     * @param infoId          生活圈主键ID 默认传0
     */
    public void addRewardLog(long userId,int rewardType,int rewardMoneyType,double rewardMoney,long infoId){
        //调用MQ同步
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "15");//interfaceType 15新增用户奖励记录
        JSONObject content = new JSONObject();
        content.put("userId",userId);
        content.put("rewardType",rewardType);
        content.put("rewardMoneyType",rewardMoneyType);
        content.put("rewardMoney",rewardMoney);
        content.put("infoId",infoId);
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        MQProducer.sendMsg(activeMQQueue,sendMsg);
    }

    /***
     * 更新奖励总金额
     * @param userId           用户ID
     * @param rewardTotalMoney 奖励的总金额
     */
//    public void updateTotalRewardMoney(long userId,double rewardTotalMoney){
//        //调用MQ同步
//        JSONObject root = new JSONObject();
//        JSONObject header = new JSONObject();
//        header.put("interfaceType", "16");//interfaceType 16更新用户奖励总金额记录
//        JSONObject content = new JSONObject();
//        content.put("userId",userId);
//        content.put("rewardTotalMoney",rewardTotalMoney);
//        root.put("header", header);
//        root.put("content", content);
//        String sendMsg = root.toJSONString();
//        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
//        MQProducer.sendMsg(activeMQQueue,sendMsg);
//    }

}
