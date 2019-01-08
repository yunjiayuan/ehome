package com.busi.dao;

import com.busi.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 简历相关Dao
 * author：zhaojiajie
 * create time：2018-12-24 13:09:32
 */
@Mapper
@Repository
public interface WorkResumeDao {

    /***
     * 新增简历
     * @param workResume
     * @return
     */
    @Insert("insert into workResume(userId,educationId,experienceId,name,sex,birthDay,positionName,addTime,refreshTime,state,myJob,highestEducation,workExperience,startSalary,endSalary,jobProvince," +
            "jobCity,jobDistrict,integrity,downloads,browseAmount,delivery,openType,jobType,jobType1,jobType2,highlights,selfEvaluation,headImgUrl,opusImgUrl,defaultResume) " +
            "values (#{userId},#{educationId},#{experienceId},#{name},#{sex},#{birthDay},#{positionName},#{addTime},#{refreshTime},#{state},#{myJob},#{highestEducation},#{workExperience},#{startSalary},#{endSalary},#{jobProvince}" +
            ",#{jobCity},#{jobDistrict},#{integrity},#{downloads},#{browseAmount},#{delivery},#{openType},#{jobType},#{jobType1},#{jobType2},#{highlights},#{selfEvaluation},#{headImgUrl},#{opusImgUrl},#{defaultResume})")
    @Options(useGeneratedKeys = true)
    int addResume(WorkResume workResume);

    /***
     * 统计该用户简历数量
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from workResume" +
            " where 1=1 " +
            " and userId = #{userId}" +
            " and state = 0" +
            "</script>")
    int findNum(@Param("userId") long userId);

    /***
     * 根据ID查询简历
     * @param id
     * @return
     */
    @Select("select * from workResume where id=#{id} and state=0")
    WorkResume findById(@Param("id") long id);

    /***
     * 更新简历删除状态
     * @param workResume
     * @return
     */
    @Update("<script>" +
            "update workResume set" +
            " state=#{state}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDel(WorkResume workResume);

    /***
     * 查询当前用户的所有简历
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from WorkResume" +
            " where 1=1" +
            " and userId = #{userId}" +
            " and state=0" +
            " order by refreshTime desc" +
            "</script>")
    List<WorkResume> findList(@Param("userId") long userId);

    /***
     * 更新简历默认状态
     * @param workResume
     * @return
     */
    @Update("<script>" +
            "update workResume set" +
            " defaultResume=#{defaultResume}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDefault(WorkResume workResume);

    /***
     * 根据ID查询工作经验
     * @param id
     * @return
     */
    @Select("select * from WorkExperience where userId=#{userId} and resumeId=#{id}")
    WorkExperience findWork(@Param("userId") long userId, @Param("id") long id);

    /***
     * 根据ID查询教育经历
     * @param id
     * @return
     */
    @Select("select * from WorkEducation where userId=#{userId} and resumeId=#{id}")
    WorkEducation findTeach(@Param("userId") long userId, @Param("id") long id);

    /***
     * 更新工作经验删除状态
     * @param workExperience
     * @return
     */
    @Update("<script>" +
            "update WorkExperience set" +
            " state=#{state}" +
            " where id=#{id}" +
            "</script>")
    int updateWork(WorkExperience workExperience);

    /***
     * 更新教育经历删除状态
     * @param workEducation
     * @return
     */
    @Update("<script>" +
            "update workEducation set" +
            " state=#{state}" +
            " where id=#{id}" +
            "</script>")
    int updateTeach(WorkEducation workEducation);

    /***
     * 更新简历
     * @param workResume
     * @return
     */
    @Update("<script>" +
            "update workResume set" +
            "<if test=\"perfectType == 0 \">" +
            " name=#{name}," +
            " sex=#{sex}," +
            " birthDay=#{birthDay}," +
            " jobType=#{jobType}," +
            " highestEducation=#{highestEducation}," +
            " workExperience=#{workExperience}," +
            " jobType1=#{jobType1}," +
            " jobType2=#{jobType2}," +
            " selfEvaluation=#{selfEvaluation}," +
            " headImgUrl=#{headImgUrl}," +
            "</if>" +
            "<if test=\"perfectType == 1 \">" +
            " endSalary=#{endSalary}," +
            " startSalary=#{startSalary}," +
            " jobCity=#{jobCity}," +
            " jobDistrict=#{jobDistrict}," +
            " jobProvince=#{jobProvince}," +
            " jobType1=#{jobType1}," +
            " jobType2=#{jobType2}," +
            "</if>" +
            "<if test=\"perfectType == 2 \">" +
            " highlights=#{highlights}," +
            "</if>" +
            "<if test=\"perfectType == 3 \">" +
            " positionName=#{positionName}," +
            "</if>" +
            " userId=#{userId}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateResume(WorkResume workResume);

    /***
     * 根据ID查询简历的完整度
     * @param id
     * @return
     */
    @Select("select * from WorkResumeIntegrity where userId=#{userId} and resumeId=#{id}")
    WorkResumeIntegrity findIntegrity(@Param("userId") long userId, @Param("id") long id);

    /***
     * 更新简历亮点的完整度
     * @param workResumeIntegrity
     * @return
     */
    @Update("<script>" +
            "update workResumeIntegrity set" +
            " highlights=#{highlights}" +
            " where id=#{id}" +
            "</script>")
    int updateIntegrity(WorkResumeIntegrity workResumeIntegrity);

    /***
     * 新增简历的完整度
     * @param workResumeIntegrity
     * @return
     */
    @Insert("insert into workResumeIntegrity(userId,resumeId,workEducation,workExperience,highlights,photo) " +
            "values (#{userId},#{resumeId},#{workEducation},#{workExperience},#{highlights},#{photo})")
    @Options(useGeneratedKeys = true)
    int addIntegrity(WorkResumeIntegrity workResumeIntegrity);

    /***
     * 查询简历下载记录
     * @param id
     * @return
     */
    @Select("select * from WorkDowRecord where userId=#{userId} and resumeId=#{id}")
    WorkDowRecord findDowRecord(@Param("userId") long userId, @Param("id") long id);

    /***
     * 根据投递简历ID企业注册者ID查询
     * @param id
     * @return
     */
    @Select("select * from WorkApplyRecord where companyId=#{userId} and resumeId=#{id}")
    WorkApplyRecord findApplyRecord(@Param("userId") long userId, @Param("id") long id);

    /***
     * 新增简历浏览记录
     * @param workBrowseRecord
     * @return
     */
    @Insert("insert into workBrowseRecord(userId,resumeId,addTime) " +
            "values (#{userId},#{resumeId},#{addTime})")
    @Options(useGeneratedKeys = true)
    int addBrowseRecord(WorkBrowseRecord workBrowseRecord);

    /***
     * 更新简历完整度和浏览量
     * @param workResume
     * @return
     */
    @Update("<script>" +
            "update workResume set" +
            " integrity=#{integrity}" +
            " browseAmount=#{browseAmount}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateBrowse(WorkResume workResume);

    /***
     * 条件查询招聘信息
     * @param userId  用户ID
     * @param positionName  职位名称
     * @return
     */
    @Select("<script>" +
            "select * from WorkRecruit" +
            " where 1=1" +
            " and userId != #{userId}" +
            "<if test=\"positionName != null and positionName != '' \">" +
            " and positionName LIKE #{positionName}" +
            "</if>" +
            "</script>")
    List<WorkRecruit> findRecruitList(@Param("userId") long userId, @Param("positionName") String positionName);

    /***
     * 条件查询招聘信息
     * @param userId  用户ID
     * @param jobProvince 求职区域：省
     * @param jobCity  求职区域：城市
     * @param jobDistrict  求职区域：地区或县
     * @param jobType1  一级求职类型
     * @param jobType2  二级求职类型
     * @param workExperience  工作经验
     * @param startSalary  期望薪资:开始
     * @param endSalary 期望薪资:结束
     * @param highestEducation 最高学历
     * @return
     */
    @Select("<script>" +
            "select * from WorkRecruit" +
            " where 1=1" +
            " and userId != #{userId}" +
            " <if test=\"jobProvince>=0 and jobCity>=0 and jobDistrict>=0\">" +
            " and jobProvince=#{jobProvince} and jobCity=#{jobCity} and jobDistrict=#{jobDistrict}" +
            "</if>" +
            " <if test=\"jobProvince>=0 and jobCity>=0 and jobDistrict==-1\">" +
            " and jobProvince=#{jobProvince} and jobCity=#{jobCity}" +
            "</if>" +
            " <if test=\"jobProvince>=0 and jobCity==-1 and jobDistrict==-1\">" +
            " and jobProvince=#{jobProvince}" +
            "</if>" +
            " <if test=\"jobType1 >= 0 and jobType2>=0\">" +
            " and positionType1=#{jobType1} and positionType2=#{jobType2}" +
            "</if>" +
            " <if test=\"jobType1 >= 0\">" +
            " and positionType1=#{jobType1}" +
            "</if>" +
            " <if test=\"startSalary >= 0 and endSalary>=0\">" +
            " and startSalary >= #{startSalary} and endSalary &lt;= #{endSalary}" +
            "</if>" +
            " <if test=\"startSalary >= 0 and endSalary &lt; 0\">" +
            " and startSalary >= #{startSalary}" +
            "</if>" +
            " <if test=\"workingLife >= 0\">" +
            " and workingLife >= #{workingLife}" +
            "</if>" +
            " <if test=\"highestEducation >= 0\">" +
            " and educational >= #{highestEducation}" +
            "</if>" +
            " ORDER BY refreshTime DESC" +
            "</script>")
    List<WorkRecruit> findRecruitList1(@Param("userId") long userId, @Param("jobProvince") int jobProvince, @Param("jobCity") int jobCity, @Param("jobDistrict") int jobDistrict, @Param("jobType1") int jobType1,
                                       @Param("jobType2") int jobType2, @Param("workExperience") int workExperience, @Param("startSalary") int startSalary, @Param("endSalary") int endSalary, @Param("highestEducation") int highestEducation);

    /***
     * 统计我的求职下各类信息数量
     * @param type  0.我的简历 1.面试邀请 2.职位申请记录 3.谁下载了我的简历
     * @return
     */
    @Select("<script>" +
            "<if test=\"type == 0 \">" +
            "select count(id) from WorkResume where state=0 and userId=#{userId}" +
            "</if>" +
            "<if test=\"type == 1 \">" +
            "select count(id) from WorkApplyRecord where state=0 and userId=#{userId} and employmentStatus=1" +
            "</if>" +
            "<if test=\"type == 2 \">" +
            "select count(id) from WorkApplyRecord where state=0 and userId=#{userId} and dowtype=0" +
            "</if>" +
            "<if test=\"type == 3 \">" +
            "select count(id) from WorkDowRecord where resumeUserId=#{userId}" +
            "</if>" +
            "</script>")
    int findJobWanted(@Param("userId") long userId, @Param("type") int type);

    /**
     * @Description: 更改公开度
     * @Param: id  简历ID
     * @Param: openType  公开程度：0公开  1对认证企业公开  2 只投递企业可见
     * @return:
     */
    @Update("<script>" +
            "update workResume set" +
            " openType=#{openType}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateOpenType(WorkResume workResume);

    /**
     * @Description: 刷新简历
     * @Param: id  简历ID
     * @return:
     */
    @Update("<script>" +
            "update workResume set" +
            " refreshTime=#{refreshTime}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateTime(WorkResume workResume);

    /***
     * 根据用户ID查询默认简历
     * @param userId
     * @return
     */
    @Select("select * from workResume where userId=#{userId} and defaultResume=1")
    WorkResume findDefault(@Param("userId") long userId);

    /***
     * 更新简历图片
     * @param workResume
     * @return
     */
    @Update("<script>" +
            "update workResume set" +
            " opusImgUrl=#{opusImgUrl}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateImg(WorkResume workResume);

    /***
     * 更新简历照片的完整度
     * @param workResumeIntegrity
     * @return
     */
    @Update("<script>" +
            "update workResumeIntegrity set" +
            " photo=#{photo}" +
            " where id=#{id}" +
            "</script>")
    int updateIntegrity1(WorkResumeIntegrity workResumeIntegrity);

    /***
     * 更新简历工作经验的完整度
     * @param workResumeIntegrity
     * @return
     */
    @Update("<script>" +
            "update workResumeIntegrity set" +
            " workExperience=#{workExperience}" +
            " where id=#{id}" +
            "</script>")
    int updateIntegrity2(WorkResumeIntegrity workResumeIntegrity);

    /***
     * 更新简历教育经历的完整度
     * @param workResumeIntegrity
     * @return
     */
    @Update("<script>" +
            "update workResumeIntegrity set" +
            " workEducation=#{workEducation}" +
            " where id=#{id}" +
            "</script>")
    int updateIntegrity3(WorkResumeIntegrity workResumeIntegrity);

    /***
     * 新增工作经验
     * @param workExperience
     * @return
     */
    @Insert("insert into workExperience(userId,resumeId,companyName,positionName,addTime,startTime,endTime,content,state) " +
            "values (#{userId},#{resumeId},#{companyName},#{positionName},#{addTime},#{startTime},#{endTime},#{content},#{state})")
    @Options(useGeneratedKeys = true)
    int addExperience(WorkExperience workExperience);

    /***
     * 更新简历工作经验ID
     * @param workResume
     * @return
     */
    @Update("<script>" +
            "update workResume set" +
            " experienceId=#{experienceId}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateExperience(WorkResume workResume);

    /***
     * 根据ID查询工作经历
     * @param id
     * @return
     */
    @Select("select * from WorkExperience where id=#{id} and state=0")
    WorkExperience findExperience(@Param("id") long id);

    /***
     * 更新工作经验删除状态
     * @param workExperience
     * @return
     */
    @Update("<script>" +
            "update workExperience set" +
            " state=#{state}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int delExperience(WorkExperience workExperience);

    /***
     * 更新工作经验
     * @param workExperience
     * @return
     */
    @Update("<script>" +
            "update workExperience set" +
            " companyName=#{companyName}," +
            " positionName=#{positionName}," +
            " startTime=#{startTime}," +
            " endTime=#{endTime}," +
            " content=#{content}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateExp(WorkExperience workExperience);

    /***
     * 查询工作经验列表
     * @param id  简历ID
     * @return
     */
    @Select("<script>" +
            "select * from WorkExperience" +
            " where 1=1" +
            " and state = 0" +
            " and resumeId = #{id}" +
            " order by addTime desc" +
            "</script>")
    List<WorkExperience> findExperienceList(@Param("id") long id);

    /***
     * 新增教育经历
     * @param workEducation
     * @return
     */
    @Insert("insert into workEducation(userId,resumeId,schoolName,proTypeId,majorName,addTime,graduationTime,state) " +
            "values (#{userId},#{resumeId},#{schoolName},#{proTypeId},#{majorName},#{addTime},#{graduationTime},#{state})")
    @Options(useGeneratedKeys = true)
    int addEducation(WorkEducation workEducation);

    /***
     * 更新教育经历经验ID
     * @param workResume
     * @return
     */
    @Update("<script>" +
            "update workResume set" +
            " educationId=#{educationId}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateEducationId(WorkResume workResume);

    /***
     * 根据ID查询教育经历
     * @param id
     * @return
     */
    @Select("select * from WorkEducation where id=#{id} and state=0")
    WorkEducation findEducation(@Param("id") long id);

    /***
     * 更新教育经历删除状态
     * @param workEducation
     * @return
     */
    @Update("<script>" +
            "update WorkEducation set" +
            " state=#{state}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int delEducation(WorkEducation workEducation);

    /***
     * 更新教育经历
     * @param workEducation
     * @return
     */
    @Update("<script>" +
            "update workEducation set" +
            " schoolName=#{schoolName}," +
            " proTypeId=#{proTypeId}," +
            " majorName=#{majorName}," +
            " graduationTime=#{graduationTime}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateEducation(WorkEducation workEducation);

    /***
     * 查询教育经历列表
     * @param id  简历ID
     * @return
     */
    @Select("<script>" +
            "select * from WorkEducation" +
            " where 1=1" +
            " and state = 0" +
            " and resumeId = #{id}" +
            " order by addTime desc" +
            "</script>")
    List<WorkEducation> findEducationList(@Param("id") long id);

    /***
     * 根据企业ID查询
     * @param id
     * @return
     */
    @Select("select * from WorkEnterprise where id=#{id} and state=0")
    WorkEnterprise findApply(@Param("id") long id);

    /***
     * 根据下载用户ID查询下载次数记录
     * @param userId
     * @return
     */
    @Select("select * from WorkDowLimit where userId=#{userId} and TO_DAYS(lastDowDate)=TO_DAYS(NOW())")
    WorkDowLimit findDowLimit(@Param("userId") long userId);

    /***
     * 新增下载次数记录
     * @param workDowLimit
     * @return
     */
    @Insert("insert into workDowLimit(userId,lastDowDate,dowResumeTimes) " +
            "values (#{userId},#{lastDowDate},#{dowResumeTimes})")
    @Options(useGeneratedKeys = true)
    int addDowLimit(WorkDowLimit workDowLimit);

    /***
     * 更新下载次数记录
     * @param workDowLimit
     * @return
     */
    @Update("<script>" +
            "update workDowLimit set" +
            "<if test=\"lastDowDate != null\">" +
            " lastDowDate=#{lastDowDate}," +
            "</if>" +
            " dowResumeTimes=#{dowResumeTimes}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDowLimit(WorkDowLimit workDowLimit);

    /***
     * 新增下载记录
     * @param workDowRecord
     * @return
     */
    @Insert("insert into workDowRecord(userId,companyId,resumeUserId,resumeId,addTime,name,sex,highestEducation,workExperience,highlights,jobType2,jobProvince,corporateName) " +
            "values (#{userId},#{companyId},#{resumeUserId},#{resumeId},#{addTime},#{name},#{sex},#{highestEducation},#{workExperience},#{highlights},#{jobType2},#{jobProvince},#{corporateName})")
    @Options(useGeneratedKeys = true)
    int addDow(WorkDowRecord workDowRecord);

    /***
     * 更新简历下载量
     * @param workResume
     * @return
     */
    @Update("<script>" +
            "update workResume set" +
            " downloads=#{downloads}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDownloads(WorkResume workResume);

    /***
     * 更新企业简历下载量
     * @param workEnterprise
     * @return
     */
    @Update("<script>" +
            "update workEnterprise set" +
            " downloads=#{downloads}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateEnDownloads(WorkEnterprise workEnterprise);

    /***
     * 新增职位记录
     * @param workApplyRecord
     * @return
     */
    @Insert("insert into workApplyRecord(userId,resumeId,recruitId,companyId,addTime,refreshTime,state,employmentStatus,enterpriseFeedback,dowtype) " +
            "values (#{userId},#{resumeId},#{recruitId},#{companyId},#{addTime},#{refreshTime},#{state},#{employmentStatus},#{enterpriseFeedback},#{dowtype})")
    @Options(useGeneratedKeys = true)
    int addApplyRecord(WorkApplyRecord workApplyRecord);

    /***
     * 查询下载记录列表
     * @param identity 身份区分：0求职者查 1企业查  简历ID
     * @param userId   用户ID
     * @return
     */
    @Select("<script>" +
            "select * from WorkDowRecord" +
            " where 1=1" +
            "<if test=\"identity == 0\">" +
            " and resumeUserId=#{userId}" +
            "</if>" +
            "<if test=\"identity != 0\">" +
            " and userId=#{userId}" +
            "</if>" +
            " order by addTime desc" +
            "</script>")
    List<WorkDowRecord> findDowList(@Param("identity") int identity, @Param("userId") long userId);

    /***
     * 统计用户简历投递数，下载量，浏览量
     * @param id  被下载简历ID
     * @param type  0 投递数 1下载量 2浏览量
     * @return
     */
    @Select("<script>" +
            "<if test=\"type == 0 \">" +
            "select count(id) from WorkApplyRecord where state=0" +
            "</if>" +
            "<if test=\"type == 1 \">" +
            "select count(id) from WorkDowRecord where 1 = 1 " +
            "</if>" +
            "<if test=\"type == 2 \">" +
            "select count(id) from WorkBrowseRecord where 1 = 1 " +
            "</if>" +
            " and resumeId=#{id} and addTime >= date_sub(now(), interval 14 day)" +
            "</script>")
    int countDownloader(@Param("id") long id, @Param("type") int type);

    /***
     * 更新简历主动投递数
     * @param workResume
     * @return
     */
    @Update("<script>" +
            "update workResume set" +
            " delivery=#{delivery}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDelivery(WorkResume workResume);

    /***
     * 批量查询指定的简历
     * @param id
     * @return
     */
    @Select("<script>" +
            "select * from WorkResume" +
            " where 1=1" +
            " and id in" +
            "<foreach collection='id' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<WorkResume> findResumeList(@Param("ids") String[] id);

    /***
     * 批量查询指定的下载记录
     * @param id
     * @return
     */
    @Select("<script>" +
            "select * from WorkDowRecord" +
            " where userId=#{userId}" +
            " and id in" +
            "<foreach collection='id' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<WorkDowRecord> findDowRecords(@Param("userId") long userId, @Param("ids") String[] id);

}
