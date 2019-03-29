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
    public ReturnData findGoodNumberListByNumber(@PathVariable long beginNumber,@PathVariable long endNumber) {
        for (int i = 0; i <34 ; i++) {//每个省简称下都要生成一部分
            for(long j=beginNumber;j<=endNumber;j++){
                int count = 0;
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
                    if(Pattern.compile(Constants.LOVE).matcher(String.valueOf(j)).find()){
                        goodNumber.setTheme(2);//2爱情靓号
                        count++;
                    }
                    if(Pattern.compile(Constants.BRITHDAY).matcher(String.valueOf(j)).find()){
                        goodNumber.setTheme(3);//3生日靓号
                        count++;
                    }
                    if(Pattern.compile(Constants.PHONE).matcher(String.valueOf(j)).find()){
                        goodNumber.setTheme(4);//4手机靓号
                        count++;
                    }
                    if(count>=3){//同时满足 爱情靓号 生日靓号 手机靓号规则 则为顶级靓号
                        goodNumber.setTheme(1);//1顶级靓号
                    }
                    String numberDigit = j+"";
                    goodNumber.setNumberDigit(numberDigit.length());
                    //检测该账号符合几个规则
                    goodNumber.setLabel();
                    //设定账号价格 为符合所有规则的总和
                    goodNumber.setGoodNumberPrice();
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
