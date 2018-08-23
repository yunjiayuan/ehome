package com.busi.dao;

import com.busi.entity.UserBankCardInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

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
    @Insert("insert into purse (userId,bankCard,bankPhone,bankName,bankCardNo,time) values (#{userId},#{bankCard},#{bankPhone},#{bankName},#{bankCardNo},#{time})")
    @Options(useGeneratedKeys = true)
    int addUserBankCardInfo(UserBankCardInfo userBankCardInfo);

    /***
     * 根据userId查询
     * @param userId
     */
    @Select("select * from userBankCardInfo where userId = #{userId}")
    UserBankCardInfo findUserBankCardInfo(@Param("userId") long userId);

}
