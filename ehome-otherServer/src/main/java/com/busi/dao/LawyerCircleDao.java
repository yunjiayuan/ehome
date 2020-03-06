package com.busi.dao;

import com.busi.entity.LawyerCircle;
import com.busi.entity.LawyerCircleRecord;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 律师圈相关Dao
 * author：zhaojiajie
 * create time：2020-03-03 19:06:06
 */
@Mapper
@Repository
public interface LawyerCircleDao {
    /***
     * 新增
     * @param lawyerCircle
     * @return
     */
    @Insert("insert into LawyerCircle(userId,businessStatus,auditType,title,lvshiName,lawFirm,cityId,addTime,imgUrl,headCover,content,jobStatus,province,city,district," +
            "videoUrl,videoCoverUrl,beGoodAt,lvshiNumber,longitude,latitude,lvshiType,age,sex)" +
            "values (#{userId},#{businessStatus},#{auditType},#{title},#{lvshiName},#{lawFirm},#{cityId},#{addTime},#{imgUrl},#{headCover},#{content},#{jobStatus},#{province},#{city},#{district}" +
            ",#{videoUrl},#{videoCoverUrl},#{beGoodAt},#{lvshiNumber},#{longitude},#{latitude},#{lvshiType},#{age},#{sex})")
    @Options(useGeneratedKeys = true)
    int add(LawyerCircle lawyerCircle);

    /***
     * 更新
     * @param lawyerCircle
     * @return
     */
    @Update("<script>" +
            "update LawyerCircle set" +
            " age=#{age}," +
            " sex=#{sex}," +
            " longitude=#{longitude}," +
            " latitude=#{latitude}," +
            " cityId=#{cityId}," +
            " city=#{city}," +
            " lvshiType=#{lvshiType}," +
            " title=#{title}," +
            " lvshiName=#{lvshiName}," +
            " imgUrl=#{imgUrl}," +
            " content=#{content}," +
            " lawFirm=#{lawFirm}," +
            " videoUrl=#{videoUrl}," +
            " province=#{province}," +
            " district=#{district}," +
            " jobStatus=#{jobStatus}," +
            " headCover=#{headCover}," +
            " videoCoverUrl=#{videoCoverUrl}," +
            " beGoodAt=#{beGoodAt}," +
            " lvshiNumber=#{lvshiNumber}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int update(LawyerCircle lawyerCircle);

    /***
     * 更新删除状态
     * @param lawyerCircle
     * @return
     */
    @Update("<script>" +
            "update LawyerCircle set" +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDel(LawyerCircle lawyerCircle);

    /***
     * 更新帮助人数
     * @param lawyerCircle
     * @return
     */
    @Update("<script>" +
            "update LawyerCircle set" +
            " helpNumber=#{helpNumber}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateNumber(LawyerCircle lawyerCircle);

    /***
     * 更新营业状态
     * @param lawyerCircle
     * @return
     */
    @Update("<script>" +
            "update LawyerCircle set" +
            " businessStatus=#{businessStatus}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateBusiness(LawyerCircle lawyerCircle);

    /***
     * 根据userId查询
     * @param userId
     * @return
     */
    @Select("select * from LawyerCircle where userId=#{userId}")
    LawyerCircle findByUserId(@Param("userId") long userId);

    /***
     * 查询列表
     * @param search    模糊搜索（可以是：律所、律师类型、律师名字）
     * @param province     省
     * @param city      市
     * @param district    区
     * @return
     */
    @Select("<script>" +
            "select * from LawyerCircle" +
            " where businessStatus=0 and deleteType = 0 and auditType=1 " +
            " and userId != #{userId}" +
            " and (lvshiName LIKE CONCAT('%',#{search},'%')" +
            " or lawFirm LIKE CONCAT('%',#{search},'%')" +
            " or lvshiType LIKE CONCAT('%',#{search},'%'))" +
            "<if test=\"district >= 0\">" +
            " and district = #{district}" +
            "</if>" +
            "<if test=\"city >= 0\">" +
            " and city = #{city}" +
            "</if>" +
            "<if test=\"province >= 0\">" +
            " and province = #{province}" +
            "</if>" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            " order by helpNumber desc" +
            "</script>")
    List<LawyerCircle> findList2(@Param("watchVideos") int watchVideos, @Param("userId") long userId, @Param("search") String search, @Param("province") int province, @Param("city") int city, @Param("district") int district);

    /***
     * 查询列表
     * @param province     省
     * @param city      市
     * @param district    区
     * @return
     */
    @Select("<script>" +
            "select * from LawyerCircle" +
            " where businessStatus=0 and deleteType = 0 and auditType=1 " +
            " and userId != #{userId}" +
            "<if test=\"district >= 0\">" +
            " and district = #{district}" +
            "</if>" +
            "<if test=\"city >= 0\">" +
            " and city = #{city}" +
            "</if>" +
            "<if test=\"province >= 0\">" +
            " and province = #{province}" +
            "</if>" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            " order by helpNumber desc" +
            "</script>")
    List<LawyerCircle> findList3(@Param("watchVideos") int watchVideos, @Param("userId") long userId, @Param("province") int province, @Param("city") int city, @Param("district") int district);

    /***
     * 查询列表
     * @param userId
     * @param department    律师类型
     * @return
     */
    @Select("<script>" +
            "select * from LawyerCircle" +
            " where businessStatus=0 and deleteType = 0 and auditType=1 " +
            " and userId != #{userId} and lvshiType=#{department}" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            "<if test=\"district >= 0\">" +
            " and district = #{district}" +
            "</if>" +
            "<if test=\"city >= 0\">" +
            " and city = #{city}" +
            "</if>" +
            "<if test=\"province >= 0\">" +
            " and province = #{province}" +
            "</if>" +
            "</script>")
    List<LawyerCircle> findList(@Param("watchVideos") int watchVideos, @Param("userId") long userId, @Param("department") int department, @Param("province") int province, @Param("city") int city, @Param("district") int district);

    /***
     * 查询列表
     * @param userId
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from LawyerCircle" +
            " where businessStatus=0 and deleteType = 0 and auditType=1 " +
            " and userId != #{userId} and cityId = #{cityId}" +
            "</script>")
    List<LawyerCircle> findList4(@Param("cityId") int cityId, @Param("userId") long userId);

    /***
     * 按用户查询列表
     * @param users
     * @return
     */
    @Select("<script>" +
            "select * from LawyerCircle" +
            " where deleteType = 0 and auditType=1 " +
            " and userId in" +
            "<foreach collection='users' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<LawyerCircle> findUsersList(@Param("users") String[] users);

    /***
     * 新增
     * @param lawyerCircle
     * @return
     */
    @Insert("insert into LawyerCircleRecord(userId,lvshiId,prescribed,content,addTime,refreshTime)" +
            "values (#{userId},#{lvshiId},#{prescribed},#{content},#{addTime},#{refreshTime})")
    @Options(useGeneratedKeys = true)
    int addRecord(LawyerCircleRecord lawyerCircle);

    /***
     * 更新
     * @param lawyerCircle
     * @return
     */
    @Update("<script>" +
            "update LawyerCircleRecord set" +
            " state=#{state}," +
            " refreshTime=#{refreshTime}," +
            " prescribed=#{prescribed}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateRecord(LawyerCircleRecord lawyerCircle);

    /***
     * 更新删除状态
     * @param userId
     * @return
     */
    @Update("<script>" +
            "update LawyerCircleRecord set" +
            " deleteType=1" +
            " where id = #{id}" +
            " and (userId=#{userId} or lvshiId=#{userId})" +
            "</script>")
    int delRecord(@Param("id") long id, @Param("userId") long userId);

    /***
     * 查询列表
     * @param haveDoctor  有无建议：0全部 1没有
     * @param identity   身份区分：0用户查 1医师查
     * @return
     */
    @Select("<script>" +
            "select * from LawyerCircleRecord" +
            " where deleteType = 0 " +
            "<if test=\"identity == 0\">" +
            " and userId = #{userId}" +
            "</if>" +
            "<if test=\"identity == 1\">" +
            " and lvshiId = #{userId}" +
            "</if>" +
            "<if test=\"haveDoctor == 1\">" +
            " and prescribed is null" +
            "</if>" +
            " order by refreshTime desc" +
            "</script>")
    List<LawyerCircleRecord> findRecordList(@Param("userId") long userId, @Param("haveDoctor") int haveDoctor, @Param("identity") int identity);
}
