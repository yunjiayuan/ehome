package com.busi.dao;

import com.busi.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 招聘相关Dao
 * author：zhaojiajie
 * create time：2019-1-3 16:02:20
 */
@Mapper
@Repository
public interface WorkRecruitDao {

    /***
     * 新增简历
     * @param workEnterprise
     * @return
     */
    @Insert("insert into workEnterprise(userId,corporateName,industry,companySize,companyNature,companypProfile,addTime,state,jobProvince,jobCity,jobDistrict,imgUrl,downloads) " +
            "values (#{userId},#{corporateName},#{industry},#{companySize},#{companyNature},#{companypProfile},#{addTime},#{state},#{jobProvince},#{jobCity},#{jobDistrict},#{imgUrl},#{downloads})")
    @Options(useGeneratedKeys = true)
    int addEnterprise(WorkEnterprise workEnterprise);

    /***
     *  根据用户ID查询企业
     * @param userId
     * @return
     */
    @Select("select * from WorkEnterprise where userId=#{userId} and state=0")
    WorkEnterprise getEnterprise(@Param("userId") long userId);

    /***
     *  根据ID查询企业
     * @param id
     * @return
     */
    @Select("select * from WorkEnterprise where id=#{id} and state=0")
    WorkEnterprise getEnter(@Param("id") long id);

    /***
     * 更新企业
     * @param workEnterprise
     * @return
     */
    @Update("<script>" +
            "update workEnterprise set" +
            " corporateName=#{corporateName}," +
            " industry=#{industry}," +
            " companySize=#{companySize}," +
            " companyNature=#{companyNature}," +
            " companypProfile=#{companypProfile}," +
            " jobProvince=#{jobProvince}," +
            " jobDistrict=#{jobDistrict}," +
            " jobCity=#{jobCity}," +
            " imgUrl=#{imgUrl}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateEnterprise(WorkEnterprise workEnterprise);

    /***
     * 招聘信息
     * @param id
     * @return
     */
    @Select("select * from WorkRecruit where id=#{id} and state=0")
    WorkRecruit findRecruit(@Param("id") long id);

    /***
     * 职位申请记录
     * @param userId
     * @return
     */
    @Select("select * from WorkApplyRecord where userId=#{userId} and resumeId=#{resumeId} and recruitId=#{recruitId} and state=0 ")
    WorkApplyRecord findApply(@Param("userId") long userId, @Param("resumeId") long resumeId, @Param("recruitId") long recruitId);

    /***
     * 职位申请记录
     * @param userId
     * @return
     */
    @Select("select * from WorkApplyRecord where userId=#{userId} and resumeId=#{resumeId} and companyId=#{companyId} and state=0 ")
    WorkApplyRecord findApply2(@Param("userId") long userId, @Param("resumeId") long resumeId, @Param("companyId") long companyId);


    /***
     * 新增职位申请记录
     * @param workApplyRecord
     * @return
     */
    @Insert("insert into workApplyRecord(userId,resumeId,recruitId,companyId,addTime,refreshTime,state,employmentStatus,enterpriseFeedback,dowtype) " +
            "values (#{userId},#{resumeId},#{recruitId},#{companyId},#{addTime},#{refreshTime},#{state},#{employmentStatus},#{enterpriseFeedback},#{dowtype})")
    @Options(useGeneratedKeys = true)
    int addApplyRecord(WorkApplyRecord workApplyRecord);

    /***
     * 更新招聘信息投递数
     * @param workRecruit
     * @return
     */
    @Update("<script>" +
            "update workRecruit set" +
            " deliveryNumber=#{deliveryNumber}" +
            " where id=#{id}" +
            "</script>")
    int updateDeliveryNumber(WorkRecruit workRecruit);

    /***
     * 查询职位申请记录
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from WorkApplyRecord" +
            " where state=0 and dowtype=0" +
            " and userId=#{userId}" +
            " order by refreshTime desc" +
            "</script>")
    List<WorkApplyRecord> findApplyList(@Param("userId") long userId);

    /***
     * 查询职位申请记录
     * @param recruitId     招聘ID
     * @param identity    身份区分：0求职者查 1企业查
     * @param queryType   查询方式：0按职位查 1查询近半年的（仅在identity=1时有效）
     * @param employmentStatus     录用状态:默认0不限 1面试 2录用  （仅在identity=1时有效）
     * @return
     */
    @Select("<script>" +
            "select * from WorkApplyRecord" +
            " where state=0 and dowtype=0" +
            "<if test=\"queryType == 0 and recruitId>0\">" +
            " and recruitId=#{recruitId}" +
            "</if>" +
            "<if test=\"queryType != 0 and recruitId &lt;= 0\">" +
            " and companyId=#{userId} and refreshTime >= date_sub(now(), interval 182 day)" +
            "</if>" +
            "<if test=\"employmentStatus > 0\">" +
            " and employmentStatus=#{employmentStatus}" +
            "</if>" +
            " order by refreshTime desc" +
            "</script>")
    List<WorkApplyRecord> findApplyList2(@Param("identity") int identity, @Param("queryType") int queryType, @Param("recruitId") long recruitId, @Param("employmentStatus") int employmentStatus);

    /***
     * 批量查询指定的招聘信息
     * @param id
     * @return
     */
    @Select("<script>" +
            "select * from WorkRecruit" +
            " where id in" +
            "<foreach collection='id' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<WorkRecruit> findRecruitList(@Param("id") String[] id);

    /***
     * 新增面试通知
     * @param workInterview
     * @return
     */
    @Insert("insert into workInterview(userId,companyId,notifiedUserId,resumeId,addTime,corporateName,positionName,address,contactPeople,contactsPhone,interviewTime,remarks,deleteState) " +
            "values (#{userId},#{companyId},#{notifiedUserId},#{resumeId},#{addTime},#{corporateName},#{positionName},#{address},#{contactPeople},#{contactsPhone},#{interviewTime},#{remarks},#{deleteState})")
    @Options(useGeneratedKeys = true)
    int addInterview(WorkInterview workInterview);

    /***
     * 更新面试通知
     * @param workInterview
     * @return
     */
    @Update("<script>" +
            "update workInterview set" +
            " contactPeople=#{contactPeople}," +
            " contactsPhone=#{contactsPhone}," +
            " interviewTime=#{interviewTime}," +
            " remarks=#{remarks}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateInterview(WorkInterview workInterview);

    /***
     * 更新面试通知删除状态
     * @param workInterview
     * @return
     */
    @Update("<script>" +
            "update workInterview set" +
            " deleteState=#{deleteState}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int delInterview(WorkInterview workInterview);

    /***
     * 根据ID查询面试通知
     * @param id
     * @return
     */
    @Select("select * from WorkInterview where id=#{id} and deleteState=0")
    WorkInterview findInterview(@Param("id") long id);

    /***
     * 查询面试通知列表
     * @param userId
     * @param identity    身份区分：0求职者查 1企业查
     * @return
     */
    @Select("<script>" +
            "select * from WorkInterview" +
            " where deleteState=0" +
            "<if test=\"identity == 0\">" +
            " and notifiedUserId=#{userId}" +
            "</if>" +
            "<if test=\"identity != 0\">" +
            " and userId=#{userId}" +
            "</if>" +
            " order by addTime desc" +
            "</script>")
    List<WorkInterview> findInterviewList(@Param("identity") int identity, @Param("userId") long userId);

    /***
     * 更新面试标签
     * @param workApplyRecord
     * @return
     */
    @Update("<script>" +
            "update workApplyRecord set" +
            " refreshTime=#{refreshTime}," +
            " employmentStatus=#{employmentStatus}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateInterviewLabel(WorkApplyRecord workApplyRecord);

    /***
     * 统计该用户招聘信息数量
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from WorkRecruit  where 1=1 " +
            " and userId = #{userId}" +
            " and state = 0" +
            "</script>")
    int findRecruitNum(@Param("userId") long userId);

    /***
     * 新增招聘信息
     * @param workRecruit
     * @return
     */
    @Insert("insert into workRecruit(userId,companyId,positionName,positionType1,positionType2,total,educational,addTime,refreshTime,state,recruitmentStatus,workingLife,startSalary,endSalary,address,jobProvince," +
            "jobCity,jobDistrict,browseAmount,deliveryNumber,welfare,requirements,contactsPhone,contactPeople,mailbox,corporateName) " +
            "values (#{userId},#{companyId},#{positionName},#{positionType1},#{positionType2},#{total},#{educational},#{addTime},#{refreshTime},#{state},#{recruitmentStatus},#{workingLife},#{startSalary},#{endSalary},#{address},#{jobProvince}" +
            ",#{jobCity},#{jobDistrict},#{browseAmount},#{deliveryNumber},#{welfare},#{requirements},#{contactsPhone},#{contactPeople},#{mailbox},#{corporateName})")
    @Options(useGeneratedKeys = true)
    int addRecruit(WorkRecruit workRecruit);

    /***
     * 更新招聘信息
     * @param workRecruit
     * @return
     */
    @Update("<script>" +
            "update workRecruit set" +
            " address=#{address}," +
            " contactPeople=#{contactPeople}," +
            " contactsPhone=#{contactsPhone}," +
            " educational=#{educational}," +
            " endSalary=#{endSalary}," +
            " jobCity=#{jobCity}," +
            " jobDistrict=#{jobDistrict}," +
            " jobProvince=#{jobProvince }," +
            " mailbox=#{mailbox}," +
            " total=#{total}," +
            " positionName=#{positionName}," +
            " positionType1=#{positionType1}," +
            " positionType2=#{positionType2}," +
            " requirements=#{requirements}," +
            " startSalary=#{startSalary}," +
            " welfare=#{welfare}," +
            " workingLife=#{workingLife}," +
            " userId=#{userId}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateRecruit(WorkRecruit workRecruit);

    /***
     * 更新招聘信息删除状态
     * @param workRecruit
     * @return
     */
    @Update("<script>" +
            "update workRecruit set" +
            " state=#{state}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int delRecruit(WorkRecruit workRecruit);

    /***
     * 查询招聘列表
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from WorkRecruit" +
            " where 1=1" +
            " and userId = #{userId} and state=0" +
            " order by refreshTime desc" +
            "</script>")
    List<WorkRecruit> findRecruitLists(@Param("userId") long userId);

    /***
     * 条件查询简历信息
     * @param userId  用户ID
     * @param positionName  职位名称
     * @return
     */
    @Select("<script>" +
            "select * from WorkResume" +
            " where 1=1" +
            " and userId != #{userId} and state=0" +
            "<if test=\"positionName != null and positionName != '' \">" +
            " and positionName LIKE #{positionName}" +
            "</if>" +
            "</script>")
    List<WorkResume> queryResumeList1(@Param("userId") long userId, @Param("positionName") String positionName);

    /***
     * 条件查询简历信息
     * @param jobProvince  工作区域:省 默认0
     * @param jobCity  工作区域:城市 默认0
     * @param jobDistrict  工作区域:地区或县 默认0
     * @param positionType1     一级分类:职位名称
     * @param positionType2     二级分类:职位类型
     * @param updateTime  更新时间  0不限  1一天以内  2三天以内  3七天以内
     * @param userId  用户
     * @param workingLife  工作年限
     * @param sex  性别
     * @param education  学历要求(仅在条件查询简历时可用,空时为不限)
     * @param photo  照片  仅在条件查询简历时有效  0不限  1有照片的
     * @param startSalary  薪资水平:起
     * @param endSalary  薪资水平:始
     * @return
     */
    @Select("<script>" +
            "select * from WorkResume" +
            " where 1=1" +
            " and userId != #{userId} and state=0" +
            " <if test=\"jobProvince>=0 and jobCity>=0 and jobDistrict>=0\">" +
            " and jobProvince=#{jobProvince} and jobCity=#{jobCity} and jobDistrict=#{jobDistrict}" +
            "</if>" +
            " <if test=\"jobProvince>=0 and jobCity>=0 and jobDistrict==-1\">" +
            " and jobProvince=#{jobProvince} and jobCity=#{jobCity}" +
            "</if>" +
            " <if test=\"jobProvince>=0 and jobCity==-1 and jobDistrict==-1\">" +
            " and jobProvince=#{jobProvince}" +
            "</if>" +
            " <if test=\"positionType1 >= 0 and positionType2>=0\">" +
            " and jobType1=#{positionType1} and jobType2=#{positionType2}" +
            "</if>" +
            " <if test=\"positionType1 >= 0\">" +
            " and jobType1=#{positionType1}" +
            "</if>" +
            " <if test=\"startSalary >= 0 and endSalary>=0\">" +
            " and startSalary >= #{startSalary} and endSalary &lt;= #{endSalary}" +
            "</if>" +
            " <if test=\"startSalary >= 0 and endSalary &lt; 0\">" +
            " and startSalary >= #{startSalary}" +
            "</if>" +
            " <if test=\"workingLife >= 0\">" +
            " and workExperience >= #{workingLife}" +
            "</if>" +
            " <if test=\"sex > 0\">" +
            " and sex >= #{sex}" +
            "</if>" +
            " <if test=\"photo > 0\">" +
            " and opusImgUrl != null" +
            "</if>" +
            " <if test=\"education != null and education != ''\">" +
            " and highestEducation in" +
            "<foreach collection='education' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</if>" +
            " <if test=\"updateTime > -1 and updateTime == 0\">" +
            " and refreshTime > date_sub(now(), interval 1 day)" +
            "</if>" +
            " <if test=\"updateTime > -1 and updateTime == 1\">" +
            " and refreshTime > date_sub(now(), interval 3 day)" +
            "</if>" +
            " <if test=\"updateTime > -1 and updateTime > 1\">" +
            " and refreshTime > date_sub(now(), interval 7 day)" +
            "</if>" +
            " ORDER BY refreshTime DESC" +
            "</script>")
    List<WorkResume> queryResumeList2(@Param("userId") long userId, @Param("jobProvince") int jobProvince, @Param("jobCity") int jobCity, @Param("jobDistrict") int jobDistrict, @Param("positionType1") int positionType1,
                                      @Param("positionType2") int positionType2, @Param("workingLife") int workingLife, @Param("sex") int sex, @Param("education") String[] education, @Param("photo") int photo, @Param("updateTime") int updateTime, @Param("startSalary") int startSalary, @Param("endSalary") int endSalary);

    /***
     * 刷新招聘信息
     * @param workRecruit
     * @return
     */
    @Update("<script>" +
            "update workRecruit set" +
            " refreshTime=#{refreshTime}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int refreshRecruit(WorkRecruit workRecruit);

    /***
     * 查询招聘管理
     * @param type  0.应聘简历 1.招聘信息
     * @return
     */
    @Select("<script>" +
            "<if test=\"type == 0 \">" +
            "select count(id) from WorkApplyRecord where companyId = #{userId} and dowtype=0" +
            "</if>" +
            "<if test=\"type == 1 \">" +
            "select count(id) from WorkRecruit where userId = #{userId}" +
            "</if>" +
            " and state=0" +
            "</script>")
    int findSupervise(@Param("userId") long userId, @Param("type") int type);

    /***
     * 查询应聘简历列表
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from WorkApplyRecord" +
            " where 1=1" +
            " and companyId=#{userId}" +
            " and state=0" +
            " order by addTime desc" +
            "</script>")
    List<WorkApplyRecord> findApplicationList(@Param("userId") long userId);
}
