package com.busi.dao;

import com.busi.entity.SelfChannelVip;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 自频道会员DAO
 *
 * @author: ZHaoJiaJie
 * @create: 2019-03-22 13:57
 */
@Mapper
@Repository
public interface SelfChannelVipDao {

    /***
     * 新增
     * @param selfChannelVip
     * @return
     */
    @Insert("insert into SelfChannelVip(userId,startTime,expiretTime," +
            "memberShipStatus) " +
            "values (#{userId},#{startTime},#{expiretTime}," +
            "#{memberShipStatus})")
    @Options(useGeneratedKeys = true)
    int add(SelfChannelVip selfChannelVip);

    /***
     * 查询会员信息
     * @param userId
     * @return
     */
    @Select("select * from SelfChannelVip where userId = #{userId} ")
    SelfChannelVip findDetails(@Param("userId") long userId);
}
