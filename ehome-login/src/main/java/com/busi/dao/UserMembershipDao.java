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
    @Update("<script>" +
            "update UserMembership set"+
            "<if test=\"regularExpireTime != null\">"+
            " regularExpireTime=#{regularExpireTime}," +
            "</if>" +
            "<if test=\"regularStopTime != null\">"+
            " regularStopTime=#{regularStopTime}," +
            "</if>" +
            "<if test=\"vipExpireTime != null\">"+
            " vipExpireTime=#{vipExpireTime}," +
            "</if>" +
            "<if test=\"vipStopTime != null\">"+
            " vipStopTime=#{vipStopTime}," +
            "</if>" +
            "<if test=\"membershipTime != null\">"+
            " membershipTime=#{membershipTime}," +
            "</if>" +
            "<if test=\"initiatorMembershipTime != null\">"+
            " initiatorMembershipTime=#{initiatorMembershipTime}," +
            "</if>" +
            "membershipLevel=#{membershipLevel}," +
            "initiatorMembershipLevel=#{initiatorMembershipLevel}," +
            "vipMembershipLevel=#{vipMembershipLevel}," +
            "regularMembershipLevel=#{regularMembershipLevel}," +
            "memberShipStatus=#{memberShipStatus}," +
            "memberShipLevelStatus=#{memberShipLevelStatus}" +
            " where userId=#{userId}"+
            "</script>")
    int update(UserMembership userMembership);

    /***
     * 查询
     * @param userId
     * @return
     */
    @Select(("select * from UserMembership where userId=#{userId}"))
    UserMembership findUserMembership(@Param("userId") long userId);

}
