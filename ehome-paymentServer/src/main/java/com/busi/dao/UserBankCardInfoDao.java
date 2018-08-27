package com.busi.dao;

import com.busi.entity.UserBankCardInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 银行卡相关DAO
 * author：SunTianJie
 * create time：2018-8-16 11:38:27
 */
@Mapper
@Repository
public interface UserBankCardInfoDao {

    /***
     * 新增
     * @param userBankCardInfo
     * @return
     */
    @Insert("insert into userBankCardInfo (userId,bankCard,bankPhone,bankName,bankCardNo,time) values (#{userId},#{bankCard},#{bankPhone},#{bankName},#{bankCardNo},#{time})")
    @Options(useGeneratedKeys = true)
    int addUserBankCardInfo(UserBankCardInfo userBankCardInfo);

    /***
     * 根据userId查询
     * @param userId
     */
    @Select("select * from userBankCardInfo where userId = #{userId}")
    UserBankCardInfo findUserBankCardInfo(@Param("userId") long userId);

    /***
     * 检测银行卡信息是否存在
     * @param bankCard
     */
    @Select("select * from userBankCardInfo where bankCard = #{bankCard}")
    List<UserBankCardInfo> findUserBankCardInfoByBankCard(@Param("bankCard") String bankCard);



}
