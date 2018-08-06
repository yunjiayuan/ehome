package com.busi.utils;

import com.busi.entity.HouseNumber;
import com.busi.entity.PickNumber;
import com.busi.service.HouseNumberService;
import com.busi.service.PickNumberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * 加载数据到缓存 工具类
 * author：SunTianJie
 * create time：2018/7/3 14:52
 */
@Component
@Slf4j
public class LoadCacheUtils implements CommandLineRunner {

    @Autowired
    private HouseNumberService houseNumberService;

    @Autowired
    private PickNumberService pickNumberService;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void run(String... args) throws Exception {
        log.info("加载门牌号记录信息到redis中...");
        loadHouseNumberToRedis();
        log.info("加载门牌号记录信息到redis中完成");
        log.info("加载预选账号记录信息到redis中...");
        loadPickNumberToRedis();
        log.info("加载预选账号记录信息到redis中完成");
    }

    /***
     * 加载门牌号信息到缓存
     */
    public void loadHouseNumberToRedis(){
        List<HouseNumber> list = houseNumberService.find();
        if(list!=null&&list.size()>0){
            for(int i=0;i<list.size();i++){
                HouseNumber houseNumber = list.get(i);
                if(houseNumber!=null){
                    redisUtils.hset("houseNumberCount",houseNumber.getProKeyWord()+"",houseNumber.getNewNumber());
                }
            }
        }
    }

    /**
     * 加载用户靓号和预选预售账号信息到缓存
     */
    public void loadPickNumberToRedis(){
        List<PickNumber> list = pickNumberService.find();
        Map<String,Object> map = null;
        if(list!=null&&list.size()>0){
            for(int i=0;i<list.size();i++){
                PickNumber pickNumber = list.get(i);
                if(pickNumber!=null){
                    String pickNumberValue = pickNumber.getProId()+"_"+pickNumber.getHouseNumber();
                    redisUtils.hset("pickNumberMap",pickNumberValue,"1");
//                    String pickNumberKey = "pickNumberMap_"+pickNumber.getProId()+"_"+pickNumber.getHouseNumber();
//                    map = CommonUtils.objectToMap(pickNumber);
//                    if(map!=null){
//                        redisUtils.hmset(pickNumberKey,map);
//                    }
                }
            }
        }
    }

}
