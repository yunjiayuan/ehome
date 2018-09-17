package com.busi.dao;

import com.busi.entity.UserAccountSecurity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 用户账户安全DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface UserAccountSecurityDao {

    /***
     * 新增
     * @param userAccountSecurity
     * @return
     */
    @Insert("insert into UserAccountSecurity(userId,idCard,realName,phone,email,securityQuestion,otherPlatformType,otherPlatformAccount,deviceLock) " +
            "values (#{userId},#{idCard},#{realName},#{phone},#{email},#{securityQuestion},#{otherPlatformType},#{otherPlatformAccount},#{deviceLock})")
    @Options(useGeneratedKeys = true)
    int add(UserAccountSecurity userAccountSecurity);

    /***
     * 更新
     * @param userAccountSecurity
     * @return
     */
    @Update("<script>" +
            "update userAccountSecurity set"+
            "<if test=\"idCard != '' or idCard != null\">"+
            " idCard=#{idCard}," +
            "</if>" +
            "<if test=\"realName != '' or realName != null\">"+
            " realName=#{realName}," +
            "</if>" +
            "<if test=\"phone != '' or phone != null\">"+
            " phone=#{phone}," +
            "</if>" +
            "<if test=\"email != '' or email != null\">"+
            " email=#{email}," +
            "</if>" +
            "<if test=\"securityQuestion != '' or securityQuestion != null\">"+
            " securityQuestion=#{securityQuestion}," +
            "</if>" +
            "<if test=\"otherPlatformAccount != '' or otherPlatformAccount != null\">"+
            " otherPlatformAccount=#{otherPlatformAccount}," +
            "</if>" +
            "<if test=\"deviceLock != '' or deviceLock != null\">"+
            " deviceLock=#{deviceLock}," +
            "</if>" +
            "<if test=\"otherPlatformType != 0\">"+
            " otherPlatformType=#{otherPlatformType}," +
            "</if>" +
            " userId=#{userId}" +
            " where userId=#{userId}"+
            "</script>")
    int update(UserAccountSecurity userAccountSecurity);

    /***
     * 查询
     * @param userId
     * @return
     */
    @Select(("select * from userAccountSecurity where userId=#{userId}"))
    UserAccountSecurity findUserAccountSecurity(@Param("userId") long userId);

}
