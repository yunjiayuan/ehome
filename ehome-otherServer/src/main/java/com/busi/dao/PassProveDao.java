package com.busi.dao;

import com.busi.entity.PassProve;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PassProveDao {

    /***
     * 新增出入证、证明
     * @param communityEventReporting
     * @return
     */
    @Insert("insert into PassProve(communityId,communityHouseId,communityHouseUserId,userId,eventReportingType,villageName,houseNumber,houseCompany,unitNumber,unitCompany,roomNumber,roomState,idCard,realName,phone,review,time,leaseContract,communityProve,remarks,type) " +
            "values (#{communityId},#{communityHouseId},#{communityHouseUserId},#{userId},#{eventReportingType},#{villageName},#{houseNumber},#{houseCompany},#{unitNumber},#{unitCompany},#{roomNumber},#{roomState},#{idCard},#{realName},#{phone},#{review},#{time},#{leaseContract},#{communityProve},#{remarks},#{type})")
    @Options(useGeneratedKeys = true)
    int addPassProve(PassProve communityEventReporting);

    /***
     * 更新出入证、证明审核状态
     * @param communityEventReporting
     * @return
     */
    @Update("<script>" +
            "update PassProve set" +
            " review=#{review}," +
            " communityProve=#{communityProve}," +
            " message=#{message}" +
            " where id=#{id}" +
            "</script>")
    int toExaminePassProve(PassProve communityEventReporting);

    @Update("<script>" +
            "update PassProve set" +
            " houseNumber=#{houseNumber}," +
            " houseCompany=#{houseCompany}," +
            " unitNumber=#{unitNumber}," +
            " unitCompany=#{unitCompany}," +
            " roomNumber=#{roomNumber}," +
            " roomState=#{roomState}," +
//            " idCard=#{idCard}," +
//            " realName=#{realName}," +
            " phone=#{phone}," +
            " remarks=#{remarks}," +
            " message=#{message}," +
            " leaseContract=#{leaseContract}," +
            " communityProve=#{communityProve}," +
            " review=#{review}," +
            " villageName=#{villageName}" +
            " where id=#{id}" +
            "</script>")
    int changePassProve(PassProve communityEventReporting);

    /***
     * 查询出入证、证明列表
     * @param communityId  居委会ID
     * @return
     */
    @Select("<script>" +
            "select * from PassProve" +
            " where 1=1" +
            "<if test=\"userId > 0\">" +
            " and userId=#{userId}" +
            "</if>" +
            "<if test=\"review != -1\">" +
            " and review=#{review}" +
            "</if>" +
            "<if test=\"type != -1\">" +
            " and type=#{type}" +
            "</if>" +
            " and communityId = #{communityId}" +
            " ORDER BY time desc" +
            "</script>")
    List<PassProve> findPassProveList(@Param("communityId") long communityId, @Param("userId") long userId, @Param("type") int type, @Param("review") int review);

    /***
     * 根据ID查询出入证、证明
     * @param id
     * @return
     */
    @Select("select * from PassProve where id = #{id}")
    PassProve findPassProve(@Param("id") long id);

    @Select("<script>" +
            "select * from PassProve where communityId = #{communityId} and userId = #{userId} and type = #{type}" +
            "</script>")
    List<PassProve> findPassProve2(@Param("communityId") long communityId, @Param("userId") long userId, @Param("type") int type);

    @Select("select * from PassProve where communityHouseId = #{communityHouseId} and villageName = #{villageName} and idCard = #{idCard} and type = #{type}" +
            " and communityId=#{communityId} and houseNumber=#{houseNumber} and houseCompany=#{houseCompany} and unitNumber=#{unitNumber}" +
            " and unitCompany=#{unitCompany} and roomNumber=#{roomNumber}")
    PassProve find(@Param("communityHouseId") long communityHouseId, @Param("villageName") String villageName, @Param("idCard") String idCard, @Param("type") int type,
                   @Param("communityId") long communityId, @Param("houseNumber") String houseNumber, @Param("houseCompany") int houseCompany
            , @Param("unitNumber") String unitNumber, @Param("unitCompany") int unitCompany, @Param("roomNumber") String roomNumber);

    /***
     * 统计各类审核数量
     * @return
     */
    @Select("<script>" +
            "select * from PassProve" +
            " where type = #{type} and communityId=#{communityId}" +
            "</script>")
    List<PassProve> countAuditType(@Param("type") int type, @Param("communityId") long communityId);

}
