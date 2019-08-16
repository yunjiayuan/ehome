package com.busi.timerController;

import com.busi.entity.UserInfo;
import com.busi.iMUtils.IMTokenCacheBean;
import com.busi.iMUtils.IMUserUtils;
import com.busi.servive.UserInfoService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @program: ehome
 * @description: 向环信用户发送消息
 * @author: ZHaoJiaJie
 * @create: 2019-01-17 10:58
 */
@Slf4j
@Component
public class SendMessageToIMTimerController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoService userInfoService;

    private int count = 200;//给多少个用户发消息

    String[] message = {"你好", "您好", "打扰了,请问你是新注册的吗?", "交个朋友吗？", "要进群吗?", "认识一下吗?", "猜猜我是谁?", "在吗?", "可以加我好友吗", "你玩过砸蛋吗?", "互加砸蛋吧", "邀请你进群啊",
            "附近的人看到你的", "hello", "求聊天", "看你名字很熟悉", "管家推荐到我家的,算是缘分吧，聊聊吗", "要资源找我", "你好,在吗", "您是?", "不好意思,你是黄政吗?", "在忙什么?", "很高兴认识你", "How's everything?",
            "What's new?", "Nice to meet you", "Hi", "在干什么呢?", "寂寞吗?", "为什么不理我呢", "做个朋友吧", "附近的人看到你的，你在附近吗?",
            "在吗。",
            "你好、在吗？",
            "你好，咱们应该都算是云家园的新成员吧。呵呵。",
            "你好，这个APP注重串门，有艳遇？",
            "你好，今天串门了吗？",
            "你好，今天偷到一枚金蛋，奖品是89家点。你偷到过吗？",
            "你好，可以聊吗？",
            "你好，在哪个城市呢？",
            "你好，你觉得这个网行吗？人不多呀。",
            "你好，能帮我喂一次我家鹦鹉吗，等金蛋，谢谢。",
            "你好，我刚喂了你家鹦鹉，也帮我喂一次好吗。",
            "你好，感谢朋友们多喂我家鹦鹉。",
            "你好，谢谢来喂我家鹦鹉，金蛋奖品分你一半。",
            "你好，只请大家多喂我家鹦鹉。近期不能聊天，谢谢。",
            "你好，你说鹦鹉的奖品是真的吗？",
            "你好，红包分享你得到过返回的红包吗？好像都忘记填写分享码。",
            "你好，串门这个这个功能好玩，欢迎来多我家串门。",
            "你好，串门不错，希望大家都有艳遇。呵呵。",
            "你好，喂鹦鹉砸蛋赢奖品不错，今天砸到50块钱。你砸到过吗？",
            "你好，能聊聊吗？",
            "你好，这个网是不是又是个约炮神器呀。你觉得呢？呵呵。",
            "你好，这个网对生活而言应该是不错的，但要看好老公老婆。你说呢。哈哈",
            "你好，聊吗？算了，有事了。",
            "你好，打招呼总没人理，你说是不是都睡着了呀。",
            "好像又一个陌陌神器，好像比陌陌还厉害。是吧。呲牙",
            "你好，我到你家串门，你在客厅真的能看到我吗。谢谢。",
            "这个软件突出串门，你说是不是有什么暗示呀。呲牙",
            "家公告不错，有什么需求随时发布，并可以通过串门对接，就是目前信息量不大。不过我当前没什么要发布的，你呢？",
            "家门口的悬赏求助功能，钱好像什么时候都能提现，我不喜欢，你觉得呢？",
            "靠。家公告这个功能好像是针对中介的，我就是干中介的，以后真能取缔中介？你说有这个可能吗？",
            "串门串了一晚上也没碰到个能聊的，你呢？在吗？",
            "串门喽！大家赶快都串起来呀。打个招呼，祝你好运。",
            "大家都抢到过红包吗，抢到的话吱一声。谢谢。",
            "创始元老级会员知道什么意思吗，未来真能赚到钱？跪求答案。",
            "生活圈的概念应该比朋友圈的概念好，你觉得呢？都不理我呀。"};

    /**
     * Cron表达式的格式：秒 分 时 日 月 周 年(可选)。
     * <p>
     * “*”可用在所有字段中，表示对应时间域的每一个时刻，例如，*在分钟字段时，表示“每分钟”；
     * <p>
     * “?”字符：表示不确定的值 该字符只在日期和星期字段中使用，它通常指定为“无意义的值”，相当于点位符；
     * <p>
     * “,”字符：指定数个值 表达一个列表值，如在星期字段中使用“MON,WED,FRI”，则表示星期一，星期三和星期五；
     * <p>
     * “-”字符：指定一个值的范围 如在小时字段中使用“10-12”，则表示从10到12点，即10,11,12；
     * <p>
     * “/”字符：指定一个值的增加幅度。n/m表示从n开始，每次增加m
     * <p>
     * “L”字符：用在日表示一个月中的最后一天，用在周表示该月最后一个星期X
     * <p>
     * “W”字符：指定离给定日期最近的工作日(周一到周五)
     * <p>
     * “#”字符：表示该月第几个周X。6#3表示该月第3个周五
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 0 0/2 * * ?") // 每2小时执行一次
    public void sendMessageToIMTimer() throws Exception {
        try {
            log.info("开始向环信用户随机发送消息...");
            List list = userInfoService.findCondition();
            List<Object> sendList = new ArrayList();
            Map<String, UserInfo> map = new HashMap();
            Random random = new Random();
            if (list != null) {//给200个用户发信息
                if (list.size() > count) {//从当做随机200个用户
                    for (int i = 0; i < list.size(); i++) {
                        int c = random.nextInt(list.size());
                        UserInfo userInfo = (UserInfo) list.get(c);
                        if (userInfo != null) {
                            if (map.size() < count) {
                                map.put(userInfo.getUserId() + "", userInfo);
                            } else {
                                break;
                            }
                        }
                    }
                    for (String key : map.keySet()) {
                        sendList.add(map.get(key));
                    }
                } else {//全部发送信息
                    sendList = list;
                }
                for (int i = 0; i < sendList.size(); i++) {
                    int messageId = random.nextInt(message.length);//从预设消息中随机选取
                    long sendUserId = random.nextInt(40000) + 13870;//从机器人用户中随机选取
                    UserInfo receiveUsers = (UserInfo) sendList.get(i);
                    IMTokenCacheBean imTokenCacheBean = IMUserUtils.getToken();
                    Map<String, Object> sendUserMap = redisUtils.hmget(Constants.REDIS_KEY_USER + sendUserId);
//                    Map<String, Object> receiveUserMap = redisUtils.hmget(Constants.REDIS_KEY_USER + userInfo.getUserId());
                    UserInfo sendUser = (UserInfo) CommonUtils.mapToObject(sendUserMap, UserInfo.class);
                    if(sendUser==null){
                        sendUser = userInfoService.findUserInfo(sendUserId);
                    }
//                    UserInfo receiveUsers = (UserInfo) CommonUtils.mapToObject(receiveUserMap, UserInfo.class);
                    IMUserUtils.sendMessageToIMUser(message[messageId], sendUser, receiveUsers, imTokenCacheBean.getAccess_token(), 0);
                    Thread.sleep(1000);//等待1秒 避免环信并发压力太大 收不到消息
                }
            }
            log.info("向环信用户随机发送消息操作完成,本次共向[" + sendList.size() + "]个用户发送消息");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
