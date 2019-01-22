package com.busi.dao;

import com.busi.entity.UserMembership;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 会员DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface UserMembershipDao {

    /***
     * 更新
     * @param userMembership
     * @return
     */
    @Update("<script>" +
            "update UserMembership set" +
            "<if test=\"regularExpireTime != null\">" +
            " regularExpireTime=#{regularExpireTime}," +
            "</if>" +
            "memberShipStatus=#{memberShipStatus}," +
            "vipMembershipLevel=#{vipMembershipLevel}," +
            "regularMembershipLevel=#{regularMembershipLevel}" +
            " where userId=#{userId}" +
            "</script>")
    int update(UserMembership userMembership);

    /***
     * 更新
     * @param userMembership
     * @return
     */
    @Update("<script>" +
            "update UserMembership set" +
            "memberShipStatus=#{memberShipStatus}," +
            "regularMembershipLevel=#{regularMembershipLevel}" +
            " where userId=#{userId}" +
            "</script>")
    int update2(UserMembership userMembership);

    /***
     * 更新
     * @param userMembership
     * @return
     */
    @Update("<script>" +
            "update UserMembership set" +
            "memberShipLevelStatus=#{memberShipLevelStatus}," +
            " where userId=#{userId}" +
            "</script>")
    int update3(UserMembership userMembership);

    /***
     * 条件查询
     * @return
     */
    @Select(("select * from UserMembership where (memberShipStatus=1 or memberShipStatus=2)"))
    List<UserMembership> findMembershipList();

    /***
     * 条件查询
     * @return
     */
    @Select(("select * from UserMembership where membershipTime <= date_sub(now(), interval 1 year) and memberShipLevelStatus=0"))
    List<UserMembership> findMembershipList2();

}
