package com.busi.dao;

import com.busi.entity.PurseChangingLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

/**
 * 钱包交易明细相关DAO
 * author：SunTianJie
 * create time：2018-8-16 11:38:27
 */
@Mapper
@Repository
public interface PurseChangingLogDao {

    /***
     * 新增
     * @param purseChangingLog
     * @return
     */
    @Insert("insert into PurseChangingLog (userId,tradeType,currencyType,tradeMoney,time) values (#{userId},#{tradeType},#{currencyType},#{tradeMoney},#{time})")
    @Options(useGeneratedKeys = true)
    int addPurseChangingLog(PurseChangingLog purseChangingLog);

    /***
     * 根据userId查询交易明细列表
     * (date) 表示到date之间的毫秒数
     * @param userId      被查询的用户ID
     * @param currencyType 交易支付类型 -1所有 0钱(真实人民币),1家币,2家点
     * @param tradeType    交易类型-1所有 0充值 1提现,2转账转入,3转账转出,4红包转入,5红包转出,6 点子转入,7点子转出,8悬赏转入,9悬赏转出,10兑换转入,11兑换支出,12红包退款,13二手购买转出,14二手出售转入,15家厨房转出,16家厨房转入,17购买会员支出
     * @param beginTime    查询的日期起始时间
     * @param endTime      查询的日期结束时间
     * @return
     */
    @Select("<script>" +
            "select * from PurseChangingLog" +
            " where 1=1" +
            "<if test=\"tradeType != -1 \">"+
            " and tradeType = #{tradeType}" +
            "</if>" +
            "<if test=\"currencyType != -1 \">"+
            " and currencyType = #{currencyType}" +
            "</if>" +
            "<if test=\" beginTime != null  \">"+
            " and UNIX_TIMESTAMP(time) >= UNIX_TIMESTAMP(#{beginTime})" +
            "</if>" +
            "<if test=\" endTime != null \">"+
            " and UNIX_TIMESTAMP(#{endTime})+86400000 >= UNIX_TIMESTAMP(time)" +
            "</if>" +
            "<if test=\" userId != -1 \">"+
            " and userId = #{userId}"+
            "</if>" +

            " order by time desc" +
            "</script>")
    List<PurseChangingLog> findList(@Param("userId") long userId,@Param("tradeType") int tradeType,
                                    @Param("currencyType") int currencyType,@Param("beginTime") Date beginTime,
                                    @Param("endTime") Date endTime);

}
