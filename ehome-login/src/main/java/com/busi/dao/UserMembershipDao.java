package com.busi.dao;

import com.busi.entity.UserMembership;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 会员DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface UserMembershipDao {

    /***
     * 新增
     * @param userMembership
     * @return
     */
    @Insert("insert into UserMembership(userId,membershipLevel,initiatorMembershipLevel," +
            "vipMembershipLevel,regularMembershipLevel,memberShipStatus,memberShipLevelStatus," +
            "regularExpireTime,regularStopTime,vipExpireTime,vipStopTime,membershipTime,initiatorMembershipTime) " +
            "values (#{userId},#{membershipLevel},#{initiatorMembershipLevel}," +
            "#{vipMembershipLevel},#{regularMembershipLevel},#{memberShipStatus}," +
            "#{memberShipLevelStatus},#{regularExpireTime},#{regularStopTime}," +
            "#{vipExpireTime},#{vipStopTime},#{membershipTime},#{initiatorMembershipTime})")
    @Options(useGeneratedKeys = true)
    int add(UserMembership userMembership);

    /***
     * 更新
     * @param userMembership
     * @return
     */
    @Update(("update UserMembership set" +
            "membershipLevel=#{membershipLevel},initiatorMembershipLevel=#{initiatorMembershipLevel}," +
            "vipMembershipLevel=#{vipMembershipLevel},regularMembershipLevel=#{regularMembershipLevel}," +
            "memberShipStatus=#{memberShipStatus},memberShipLevelStatus=#{memberShipLevelStatus}," +
            "regularExpireTime=#{regularExpireTime},regularStopTime=#{regularStopTime}," +
            "vipExpireTime=#{vipExpireTime},vipStopTime=#{vipStopTime},membershipTime=#{membershipTime},initiatorMembershipTime=#{initiatorMembershipTime" +
            " where userId=#{userId}"))
    int update(UserMembership userMembership);

    /***
     * 查询
     * @param userId
     * @return
     */
    @Select(("select * from UserMembership where userId=#{userId}"))
    UserMembership findUserMembership(@Param("userId") long userId);

}
