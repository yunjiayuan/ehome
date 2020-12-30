package com.busi.dao;

import com.busi.entity.UserInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface UserInfoDao {
    /***
     * 新增用户
     * @param userInfo
     * @return
     */
    @Insert("insert into userInfo(phone,name,head,password,im_password,sex,proType,houseNumber,idCard,birthday,country,province,city,district,time,accountStatus,otherPlatformKey,otherPlatformType,accessRights,isGoodNumber,user_ce) values (#{phone},#{name},#{head},#{password},#{im_password},#{sex},#{proType},#{houseNumber},#{idCard},#{birthday},#{country},#{province},#{city},#{district},#{time},#{accountStatus},#{otherPlatformKey},#{otherPlatformType},#{accessRights},#{isGoodNumber},#{user_ce})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int add(UserInfo userInfo);

    /***
     * 根据userId查询用户信息
     * @param userId
     */
    @Select("select * from userInfo where userId = #{userId}")
    UserInfo findUserById(@Param("userId") long userId);

    /***
     * 根据手机号查询用户信息
     * @param phone
     */
    @Select("select * from userInfo where phone = #{phone}")
    UserInfo findUserByPhone(@Param("phone") String phone);

    /***
     * 根据第三方平台账号查询用户信息
     * @param otherPlatformType
     * @param otherPlatformKey
     * @return
     */
    @Select("select * from userInfo where otherPlatformType = #{otherPlatformType} and otherPlatformKey = #{otherPlatformKey}")
    UserInfo findUserByOtherPlat(@Param("otherPlatformType") int otherPlatformType, @Param("otherPlatformKey") String otherPlatformKey);

    /***
     * 根据门牌号 查找用户信息
     * @param proType
     * @param houseNumber
     * @return
     */
    @Select("select * from userInfo where proType = #{proType} and houseNumber = #{houseNumber} and accountStatus < 2 ")
    UserInfo findUserByHouseNumber(@Param("proType") int proType, @Param("houseNumber") String houseNumber);

    /***
     * 完善用户资料
     * @param userInfo
     * @return
     */
    @Update(("update userInfo set name=#{name},head=#{head},password=#{password},im_password=#{im_password},sex=#{sex},proType=#{proType},houseNumber=#{houseNumber},idCard=#{idCard},birthday=#{birthday},country=#{country},province=#{province},city=#{city},district=#{district},accountStatus=#{accountStatus},accessRights=#{accessRights} where userId=#{userId}"))
    int perfectUserInfo(UserInfo userInfo);

    /***
     * 根据用户ID删除用户
     * @param userInfo
     * @return
     */
    @Delete(("delete from userInfo where userId=#{userId}"))
    int delete(UserInfo userInfo);


    /***
     * 分页条件查询userInfo  注意 请使用>=  不要使用 <=
     */
//    @Select("select * from demo where name like '%孙%' order by name limit 0,5")
    @Select("<script>" +
            "select * from userInfo" +
            " where 1=1" +
            "<if test=\"name != null and name != ''\">" +
            " and name like CONCAT('%',#{name},'%')" +
            "</if>" +
            "<if test=\"beginAge > 0 \">" +
            " and TIMESTAMPDIFF(YEAR,birthday,CURDATE()) >= #{beginAge}" +
            "</if>" +
            "<if test=\"endAge >= beginAge and endAge > 0 \">" +
            " and #{endAge} >= TIMESTAMPDIFF(YEAR,birthday,CURDATE())" +
            "</if>" +
            "<if test=\"sex != 0 \">" +
            " and sex = #{sex}" +
            "</if>" +
            "<if test=\"province != -1 \">" +
            " and province = #{province}" +
            "</if>" +
            "<if test=\"city != -1 \">" +
            " and city = #{city}" +
            "</if>" +
            "<if test=\"district != -1 \">" +
            " and district = #{district}" +
            "</if>" +
            "<if test=\"studyrank > 0 \">" +
            " and studyrank = #{studyrank}" +
            "</if>" +
            "<if test=\"maritalstatus > 0 \">" +
            " and maritalstatus = #{maritalstatus}" +
            "</if>" +
            "<if test=\"talkToSomeoneStatus != -1 \">" +
                " and talkToSomeoneStatus = #{talkToSomeoneStatus}" +
            "</if>" +
            "<if test=\"chatnteractionStatus != -1 \">" +
                " and chatnteractionStatus = #{chatnteractionStatus}" +
            "</if>" +
            " and accountStatus = 0" +
            " order by time desc" +
            "</script>")
//    @SelectProvider(type=UserInfoService.class,method="getFindListSql")
    List<UserInfo> findList(@Param("name") String name, @Param("beginAge") int beginAge, @Param("endAge") int endAge,
                            @Param("sex") int sex, @Param("province") int province, @Param("city") int city,
                            @Param("district") int district, @Param("studyrank") int studyrank,
                            @Param("maritalstatus") int maritalstatus,@Param("talkToSomeoneStatus") int talkToSomeoneStatus,
                            @Param("chatnteractionStatus") int chatnteractionStatus);

    /***
     * 更新用户信息
     * @param userInfo
     * @return
     */
//    @Update(("update userInfo set name=#{name} where userId=#{userId}"))
    @Update("<script>" +
            "update userInfo set" +
            "<if test=\"name != null and name != ''\">" +
            " name=#{name}," +
            "</if>" +
            "<if test=\"sex == 1 or sex == 2\">" +
            " sex=#{sex}," +
            "</if>" +
            "<if test=\"birthday != null\">" +
            " birthday=#{birthday}," +
            "</if>" +
            "<if test=\"studyRank != 0\">" +
            " studyRank=#{studyRank}," +
            "</if>" +
            "<if test=\"job != 0\">" +
            " job=#{job}," +
            "</if>" +
            "<if test=\"maritalStatus != 0\">" +
            " maritalStatus=#{maritalStatus}," +
            "</if>" +
            "<if test=\"nation != 0\">" +
            " nation=#{nation}," +
            "</if>" +
            "<if test=\"gxqm != null and gxqm != ''\">" +
            " gxqm=#{gxqm}," +
            "</if>" +
            "<if test=\"sentiment != null and sentiment != ''\">" +
            " sentiment=#{sentiment}," +
            "</if>" +
            "<if test=\"company != null and company != ''\">" +
            " company=#{company}," +
            "</if>" +
            "<if test=\"position != null and position != ''\">" +
            " position=#{position}," +
            "</if>" +
            " country=#{country}," +
            " birthPlace_province=#{birthPlace_province}," +
            " birthPlace_city=#{birthPlace_city}," +
            " birthPlace_district=#{birthPlace_district}" +
            " where userId=#{userId}" +
            "</script>")
    int update(UserInfo userInfo);

    /**
     * 搬家更新
     *
     * @param userInfo
     * @return
     */
    @Update("<script>" +
            "update userInfo set" +
            " proType=#{proType}," +
            " houseNumber=#{houseNumber}," +
            " password=#{password}," +
            " accountStatus=#{accountStatus}" +
            " where userId=#{userId}" +
            "</script>")
    int updateByHouseMoving(UserInfo userInfo);

    /**
     * 修改头像  需求把涂鸦图像置空
     *
     * @param userInfo
     * @return
     */
    @Update("<script>" +
            "update userInfo set" +
            " head=#{head}," +
            " graffitiHead=\"\"" +
            " where userId=#{userId}" +
            "</script>")
    int updateUserHead(UserInfo userInfo);

    /**
     * 修改用户密码
     *
     * @param userInfo
     * @return
     */
    @Update("<script>" +
            "update userInfo set" +
            " password=#{newPassword}" +
            " where userId=#{userId}" +
            "</script>")
    int changePassWord(UserInfo userInfo);

    /**
     * 修改涂鸦头像
     *
     * @param userInfo
     * @return
     */
    @Update("<script>" +
            "update userInfo set" +
            " graffitiHead=#{graffitiHead}" +
            " where userId=#{userId}" +
            "</script>")
    int updateUserGraffitiHead(UserInfo userInfo);

    /**
     * 修改访问权限
     *
     * @param userInfo
     * @return
     */
    @Update("<script>" +
            "update userInfo set" +
            " accessRights=#{accessRights}" +
            " where userId=#{userId}" +
            "</script>")
    int updateUserAccessRights(UserInfo userInfo);

    /**
     * 修改新用户系统欢迎消息状态接口
     *
     * @param userInfo
     * @return
     */
    @Update("<script>" +
            "update userInfo set" +
            " welcomeInfoStatus=#{welcomeInfoStatus}" +
            " where userId=#{userId}" +
            "</script>")
    int updateWelcomeInfoStatus(UserInfo userInfo);

    /**
     * 修改新用户系统欢迎消息状态
     *
     * @param userInfo
     * @return
     */
    @Update("<script>" +
            "update userInfo set" +
            " isNewUser=#{isNewUser}" +
            " where userId=#{userId}" +
            "</script>")
    int updateIsNewUser(UserInfo userInfo);

    /**
     * 修改用户V认证状态
     *
     * @param userInfo
     * @return
     */
    @Update("<script>" +
            "update userInfo set" +
            " user_ce=#{user_ce}" +
            " where userId=#{userId}" +
            "</script>")
    int updateUserCe(UserInfo userInfo);

    /**
     * 用户找人倾诉状态
     *
     * @param userInfo
     * @return
     */
    @Update("<script>" +
            "update userInfo set" +
            " talkToSomeoneStatus=#{talkToSomeoneStatus}" +
            " where userId=#{userId}" +
            "</script>")
    int updateTalkToSomeoneStatus(UserInfo userInfo);
    /**
     * 更新用户聊天互动状态
     *
     * @param userInfo
     * @return
     */
    @Update("<script>" +
            "update userInfo set" +
            " chatnteractionStatus=#{chatnteractionStatus}" +
            " where userId=#{userId}" +
            "</script>")
    int updateChatnteractionStatus(UserInfo userInfo);

    /**
     * 修改生活圈首次视频发布状态
     *
     * @param userInfo
     * @return
     */
    @Update("<script>" +
            "update userInfo set" +
            " homeBlogStatus=#{homeBlogStatus}" +
            " where userId=#{userId}" +
            "</script>")
    int updateHomeBlogStatus(UserInfo userInfo);

    /**
     * 修改用户账号状态 正常 停用
     *
     * @param userInfo
     * @return
     */
    @Update("<script>" +
            "update userInfo set" +
            " accountStatus=#{accountStatus}" +
            " where userId=#{userId}" +
            "</script>")
    int updateAccountStatus(UserInfo userInfo);

    /***
     * 更新用户手机绑定状态
     * @param userInfo
     * @return
     */
    @Update("<script>" +
            "update userInfo set" +
            " phone=#{phone}" +
            " where userId=#{userId}" +
            "</script>")
    int updateBindPhone(UserInfo userInfo);

    /***
     * 更新用户第三方平台账号绑定状态
     * @param userInfo
     * @return
     */
    @Update("<script>" +
            "update userInfo set" +
            " otherPlatformKey=#{otherPlatformKey}," +
            " otherPlatformType=#{otherPlatformType}" +
            " where userId=#{userId}" +
            "</script>")
    int updateBindOther(UserInfo userInfo);

    /***
     * 条件查找用户信息
     * @return
     */
    @Select("select * from userInfo where (userId > 10000 and 13870 > userId) or (userId > 53870 ) ")
    List<UserInfo> findCondition();

    /**
     * 更新用户代言人身份标识
     *
     * @param userInfo
     * @return
     */
    @Update("<script>" +
            "update userInfo set" +
            " isSpokesman=#{isSpokesman}," +
            " spokesmanName=#{spokesmanName}" +
            " where userId=#{userId}" +
            "</script>")
    int updateSpokesmanStatus(UserInfo userInfo);
}
