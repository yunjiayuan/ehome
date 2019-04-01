package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.GoodNumber;
import com.busi.entity.ReturnData;
import com.busi.service.GoodNumberService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * 预售靓号相关业务接口(内部调用)
 * author：suntj
 * create time：2019-3-28 18:39:46
 */
@RestController
public class GoodNumberLController extends BaseController implements GoodNumberLocalController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    GoodNumberService goodNumberService;

    /***
     * 自动生成指定区间内的靓号
     * @param beginNumber 起始门牌号（包含）
     * @param endNumber   结束门牌号（包含）
     * @return
     */
    @Override
    public ReturnData addGoodNumber(@PathVariable long beginNumber,@PathVariable long endNumber) {
        for (int i = 0; i <34 ; i++) {//每个省简称下都要生成一部分
            for(long j=beginNumber;j<=endNumber;j++){
//                int count = 0;
                if(CommonUtils.isPretty(j)){//符合靓号规则 插入靓号预售表
                    //判断靓号是否已被占用
                    Object o = redisUtils.hget("pickNumberMap",i+"_"+j);
                    if(o!=null){
                        continue;//已存在
                    }
                    GoodNumber goodNumber = new GoodNumber();
                    goodNumber.setProId(i);
                    goodNumber.setHouse_number(j);
                    goodNumber.setTheme(0);//主题 0普通靓号 1顶级靓号 2爱情靓号 3生日靓号 4手机靓号
                    if(Pattern.compile(Constants.BRITHDAY).matcher(String.valueOf(j)).find()){
                        goodNumber.setTheme(3);//3生日靓号
                    }
                    if(Pattern.compile(Constants.PHONE).matcher(String.valueOf(j)).find()){
                        goodNumber.setTheme(4);//4手机靓号
                    }
                    if(Pattern.compile(Constants.LOVE).matcher(String.valueOf(j)).find()){
                        goodNumber.setTheme(2);//2爱情靓号
                    }
                    String numberDigit = j+"";
                    goodNumber.setNumberDigit(numberDigit.length());
                    //检测该账号符合几个规则
                    String labels ="";//最终符合的数字规则组合
                    double goodNumberPrice =0;//最终靓号的出售价格
                    TreeSet aaa = new TreeSet();//重复集合
                    TreeSet abc = new TreeSet();//连号集合
                    for(int k=0;k<Constants.PRETTY_NUMBER_ARRAY.length;k++) {
                        if(Pattern.compile(Constants.PRETTY_NUMBER_ARRAY[k]).matcher(String.valueOf(j)).find()){
                            //连号只保留最长的规则0-8 重复的号也只保留最长的规则9-15
                            if(k<=15){
                                if(k<=8){//重复集合
                                    aaa.add(k);
                                }else{//连号集合
                                    abc.add(k);
                                }
                            }else{
                                labels += "#"+k+"#,";
                                goodNumberPrice += Constants.PRETTY_NUMBER_PRICE_ARRAY[k];
//                                count++;
                            }
                        }
                    }
                    if(labels.length()>0){//去掉最后一个","
                        labels = labels.substring(0,labels.length()-1);
                        if(aaa.size()>0){
                            int a = Integer.parseInt(aaa.pollFirst().toString());
                            labels +=",#"+a+"#";
                            goodNumberPrice += Constants.PRETTY_NUMBER_PRICE_ARRAY[a];
//                            count++;
                        }
                        if(abc.size()>0){
                            int b = Integer.parseInt(abc.pollFirst().toString());
                            labels +=",#"+b+"#";
                            goodNumberPrice += Constants.PRETTY_NUMBER_PRICE_ARRAY[b];
//                            count++;
                        }
                    }else{
                        if(aaa.size()>0){
                            int a = Integer.parseInt(aaa.pollFirst().toString());
                            labels +="#"+a+"#";
                            goodNumberPrice += Constants.PRETTY_NUMBER_PRICE_ARRAY[a];
//                            count++;
                        }
                        if(abc.size()>0){
                            int b = Integer.parseInt(abc.pollFirst().toString());
                            if(labels.length()>0){
                                labels +=",#"+b+"#";
                            }else{
                                labels +="#"+b+"#";
                            }
                            goodNumberPrice += Constants.PRETTY_NUMBER_PRICE_ARRAY[b];
//                            count++;
                        }
                    }
//                    if(count>=3){//同时满足多个规则为顶级账号
//                        goodNumber.setTheme(1);//1顶级靓号
//                    }
                    if(goodNumberPrice>1000){//价格大于1000 为顶级靓号
                        goodNumber.setTheme(1);//1顶级靓号
                    }
                    goodNumber.setLabel(labels);
                    //设定账号价格 为符合所有规则的总和
                    goodNumber.setGoodNumberPrice(goodNumberPrice);
                    goodNumber.setStatus(0);
                    goodNumberService.add(goodNumber);
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新靓号状态(目前做成删除已购买的靓号)
     * @param goodNumber
     * @return
     */
    @Override
    public ReturnData updateGoodNumber( @RequestBody GoodNumber goodNumber) {
        GoodNumber gn = goodNumberService.findGoodNumberInfo(goodNumber.getProId(),goodNumber.getHouse_number());
        if(gn==null){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "更新靓号["+goodNumber.getProId()+"_"+goodNumber.getHouse_number()+"]状态失败,该靓号不存在", new JSONObject());
        }
        gn.setStatus(1);
        int count = goodNumberService.updateStatus(goodNumber);
        if(count<=0){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "更新靓号["+goodNumber.getProId()+"_"+goodNumber.getHouse_number()+"]状态失败", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
