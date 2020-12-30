package com.busi.dao;

import com.busi.entity.HomeAlbum;
import com.busi.entity.HomeAlbumPic;
import com.busi.entity.HomeAlbumPicWhole;
import com.busi.entity.HomeAlbumPwd;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 储藏室Dao
 * author：zhaojiajie
 * create time：2018-10-23 10:03:57
 */
@Mapper
@Repository
public interface HomeAlbumDao {

    /***
     * 新增相册
     * @param homeAlbum
     * @return
     */
    @Insert("insert into homeAlbum(userId,name,roomType,photoSize,createTime,shootTime,albumType,albumDescribe,albumPurview,albumSeat,albumState,imgCover) " +
            "values (#{userId},#{name},#{roomType},#{photoSize},#{createTime},#{shootTime},#{albumType},#{albumDescribe},#{albumPurview},#{albumSeat},#{albumState},#{imgCover})")
    @Options(useGeneratedKeys = true)
    int addAlbum(HomeAlbum homeAlbum);

    /***
     * 更新相册
     * @param homeAlbum
     * @return
     */
    @Update("<script>" +
            "update homeAlbum set" +
            "<if test=\"name != null and name != ''\">" +
            " name=#{name}," +
            "</if>" +
            "<if test=\"shootTime != null and shootTime != ''\">" +
            " shootTime=#{shootTime}," +
            "</if>" +
            "<if test=\"albumType != null and albumType != ''\">" +
            " albumType=#{albumType}," +
            "</if>" +
            "<if test=\"albumDescribe != null and albumDescribe != ''\">" +
            " albumDescribe=#{albumDescribe}," +
            "</if>" +
            "<if test=\"imgCover != null and imgCover != ''\">" +
            " imgCover=#{imgCover}," +
            "</if>" +
            " albumPurview=#{albumPurview}," +
            " albumSeat=#{albumSeat}," +
            " roomType=#{roomType}" +
            " where id=#{id}" +
            "</script>")
    int updateAlbum(HomeAlbum homeAlbum);

    /***
     * 更新相册删除状态
     * @param homeAlbum
     * @return
     */
    @Update("<script>" +
            "update homeAlbum set" +
            " albumState=#{albumState}" +
            " where id=#{id}" +
            "</script>")
    int delAlbum(HomeAlbum homeAlbum);

    /***
     * 更新相册封面
     * @param homeAlbum
     * @return
     */
    @Update("<script>" +
            "update homeAlbum set" +
            "<if test=\"imgCover != null and imgCover != ''\">" +
            " imgCover=#{imgCover}" +
            "</if>" +
            " where id=#{id}" +
            "</script>")
    int updateAlbumCover(HomeAlbum homeAlbum);

    /***
     * 更新相册图片总数
     * @param homeAlbum
     * @return
     */
    @Update("<script>" +
            "update homeAlbum set" +
            " photoSize=#{photoSize}" +
            " where id=#{id}" +
            "</script>")
    int updateAlbumNum(HomeAlbum homeAlbum);

    /***
     * 查询指定相册下的图片
     * @return
     */
    @Select("select * from  homeAlbumPic" +
            " where albumId=#{id}")
    List<HomeAlbumPic> updateByAlbumId(@Param("id") long id);

    /***
     * 新增图片
     * @param homeAlbumPic
     * @return
     */
    @Insert("insert into homeAlbumPic(userId,albumId,name,picDescribe,roomType,picState,imgUrl,time,newTime) " +
            "values (#{userId},#{albumId},#{name},#{picDescribe},#{roomType},#{picState},#{imgUrl},#{time},#{newTime})")
    @Options(useGeneratedKeys = true)
    int uploadPic(HomeAlbumPic homeAlbumPic);

    @Insert("insert into HomeAlbumPicWhole(userId,num,time,albumId) " +
            "values (#{userId},#{num},#{time},#{albumId})")
    @Options(useGeneratedKeys = true)
    int add(HomeAlbumPicWhole homeAlbumPic);

    /***
     * 更新图片
     * @param homeAlbumPic
     * @return
     */
    @Update("<script>" +
            "update homeAlbumPic set" +
            "<if test=\"name != null and name != ''\">" +
            " name=#{name}," +
            "</if>" +
            "<if test=\"picDescribe != null and picDescribe != ''\">" +
            " picDescribe=#{picDescribe}," +
            "</if>" +
            " userId=#{userId}" +
            " where id=#{id} and picState=0" +
            "</script>")
    int updatePic(HomeAlbumPic homeAlbumPic);

    /***
     * 新增密码
     * @param homeAlbumPwd
     * @return
     */
    @Insert("insert into homeAlbumPwd(password,status) " +
            "values (#{password},#{status})")
    @Options(useGeneratedKeys = true)
    int addPwd(HomeAlbumPwd homeAlbumPwd);

    /***
     * 更新密码
     * @param homeAlbumPwd
     * @return
     */
    @Update("<script>" +
            "update homeAlbumPwd set" +
            "<if test=\"password != null and password != ''\">" +
            " password=#{password}," +
            "</if>" +
            "<if test=\"status != null and status != ''\">" +
            " status=#{status}," +
            "</if>" +
            " id=#{id}" +
            " where id=#{id}" +
            "</script>")
    int updatePwd(HomeAlbumPwd homeAlbumPwd);

    /***
     * 更新相册密码ID
     * @param homeAlbum
     * @return
     */
    @Update("<script>" +
            "update homeAlbum set" +
            " albumPurview=#{albumPurview}" +
            " where id=#{id}" +
            "</script>")
    int updatePwdId(HomeAlbum homeAlbum);

    @Update("<script>" +
            "update HomeAlbumPicWhole set" +
            " num=#{num}" +
            " where id=#{id}" +
            "</script>")
    int update(HomeAlbumPicWhole homeAlbum);

    /***
     * 统计该用户相册数量
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from homeAlbum" +
            " where userId=#{userId} and roomType=#{roomType} and albumState=0" +
            "</script>")
    int findNum(@Param("userId") long userId, @Param("roomType") int roomType);

    /***
     * 统计该用户  图片-童年，图片-青年，图片-中年，图片-老年,荣誉，六类的图片总数
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from homeAlbumPic" +
            "<![CDATA[ where userId=#{userId} and picState=0 and roomType >= 3 AND roomType <= 8 ]]>" +
            "</script>")
    int countPic(@Param("userId") long userId);

    /***
     * 根据ID查询用户相册
     * @param id
     */
    @Select("select * from homeAlbum where id = #{id} and albumState=0")
    HomeAlbum findById(@Param("id") long id);

    @Select("select * from HomeAlbumPicWhole where userId = #{id} and time=#{time} and albumId=#{albumId}")
    HomeAlbumPicWhole findWhole(@Param("id") long id, @Param("time") int time, @Param("albumId") long albumId);

    /***
     * 根据ID查询用户图片
     * @param id
     */
    @Select("select * from homeAlbumPic where id = #{id} and picState=0")
    HomeAlbumPic findAlbumInfo(@Param("id") long id);

    /***
     * 根据ID查询用户密码
     * @param id
     */
    @Select("select * from HomeAlbumPwd where id = #{id}")
    HomeAlbumPwd findByPwdId(@Param("id") long id);

    /***
     * 删除密码
     * @param id
     * @return
     */
    @Delete("<script>" +
            "delete from HomeAlbumPwd" +
            " where id =#{id}" +
            "</script>")
    int delPwd(@Param("id") long id);

    /***
     * 分页查询相册列表
     * @param userId 用户ID
     * @param roomType 房间类型 默认-1不限， 0花园,1客厅,2家店,3存储室-图片-童年,4存储室-图片-青年,5存储室-图片-中年,6存储室-图片-老年，7藏品室，8荣誉室
     * @param name 相册名
     * @return
     */
    @Select("<script>" +
            "select * from HomeAlbum" +
            " where 1=1" +
            "<if test=\"roomType > -1\">" +
            " and roomType=#{roomType}" +
            "</if>" +
            " and name LIKE CONCAT('%',#{name},'%')" +
            " and albumState=0" +
            " and userId=#{userId}" +
            " order by createTime desc" +
            "</script>")
    List<HomeAlbum> findPaging(@Param("userId") long userId, @Param("roomType") int roomType, @Param("name") String name);

    /***
     * 分页查询相册列表
     * @param userId 用户ID
     * @param roomType 房间类型 默认-1不限， 0花园,1客厅,2家店,3存储室-图片-童年,4存储室-图片-青年,5存储室-图片-中年,6存储室-图片-老年，7藏品室，8荣誉室
     * @return
     */
    @Select("<script>" +
            "select * from HomeAlbum" +
            " where 1=1" +
            "<if test=\"roomType > -1\">" +
            " and roomType=#{roomType}" +
            "</if>" +
            " and albumState=0" +
            " and userId=#{userId}" +
            " order by createTime desc" +
            "</script>")
    List<HomeAlbum> findPaging2(@Param("userId") long userId, @Param("roomType") int roomType);

    @Select("<script>" +
            "select * from HomeAlbum" +
            " where 1=1" +
            "<if test=\"roomType > -1\">" +
            " and roomType in(3,4,5,6,9)" +
            "</if>" +
            " and albumState=0" +
            " and userId=#{userId}" +
            " order by createTime desc" +
            "</script>")
    List<HomeAlbum> findPaging3(@Param("userId") long userId, @Param("roomType") int roomType);


    /***
     * 分页查询指定相册图片
     * @param userId  用户ID
     * @param albumId 相册ID
     * @param name  图片名
     * @return
     */
    @Select("<script>" +
            "select * from HomeAlbumPic" +
            " where 1=1" +
            " and name LIKE CONCAT('%',#{name},'%')" +
            " and picState=0" +
            " and userId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<HomeAlbumPic> findAlbumPic(@Param("userId") long userId, @Param("albumId") long albumId, @Param("name") String name);

    /***
     * 分页查询指定相册图片
     * @param userId  用户ID
     * @param albumId 相册ID
     * @return
     */
    @Select("<script>" +
            "select * from HomeAlbumPic" +
            " where 1=1" +
            " and albumId=#{albumId}" +
            " and picState=0" +
            " and userId=#{userId}" +
            " order by time desc" +
            "</script>")
    List<HomeAlbumPic> findAlbumPic2(@Param("userId") long userId, @Param("albumId") long albumId);

    /***
     * 分页查询图片
     * @param userId  用户ID
     * @param date  指定日期  0表示查所有   格式：20201212
     * @return
     */
    @Select("<script>" +
            "select * from HomeAlbumPic" +
            " where 1=1" +
            " and picState=0" +
            " and userId=#{userId}" +
            "<if test=\"albumId > 0\">" +
            " and albumId=#{albumId}" +
            "</if>" +
            "<if test=\"date > 0\">" +
            "<if test=\"type == 0\">" +
            " and newTime = #{date}" +
            "</if>" +
            "<if test=\"type == 1\">" +
            " and newTime >= #{date}" +
            "</if>" +
            " order by time asc" +
            "</if>" +
            "<if test=\"date == 0\">" +
            " order by time desc" +
            "</if>" +
            "</script>")
    List<HomeAlbumPic> findPicList(@Param("albumId") long albumId, @Param("userId") long userId, @Param("date") int date, @Param("type") int type);

    /***
     * 根据ID查询用户相册
     * @param ids
     * @return
     */
    @Select("<script>" +
            "select count(id),max(albumId) from homeAlbumPic" +
            " where 1=1" +
            " and albumId in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and picState=0 group by albumId" +
            "</script>")
    List<Object> findByIds(@Param("ids") String[] ids);

    /***
     * 根据ID查询用户相册
     * @param id
     * @return
     */
    @Select("<script>" +
            "select count(id) from homeAlbumPic" +
            " where albumId=#{id} and picState=0" +
            "</script>")
    long findByIds2(@Param("id") long id);

    /***
     * 统计用户各分类图片总数
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from HomeAlbumPic" +
            "<![CDATA[ where userId=#{userId} and picState=0 and roomType >= 3 AND roomType <= 8 ]]>" +
            "</script>")
    List<HomeAlbumPic> findPicNumber(@Param("userId") long userId);

    /***
     * 删除图片
     * @param albumId
     * @param userId
     * @return
     */
    @Update("<script>" +
            "update HomeAlbumPic set" +
            " picState=1" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and albumId=#{albumId}" +
            "</script>")
    int deletePic(@Param("userId") long userId, @Param("albumId") long albumId, @Param("ids") String[] ids);

    @Update("<script>" +
            "update HomeAlbumPic set" +
            " picState=1" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int delPic(@Param("userId") long userId, @Param("ids") String[] ids);

    @Select("<script>" +
            "select * from HomeAlbumPicWhole" +
            " where 1=1" +
            " and userId=#{userId}" +
            "<if test=\"albumId > 0\">" +
            " and albumId=#{albumId}" +
            "</if>" +
            " and time BETWEEN #{startTime} and #{endTime}" +
            "</script>")
    List<HomeAlbumPicWhole> findPicDate(@Param("userId") long userId, @Param("startTime") int startTime, @Param("endTime") int endTime, @Param("albumId") long albumId);

    /***
     * 更新图片记录
     * @param id
     * @return
     */
    @Delete("<script>" +
            "<if test=\"num > 0\">" +
            "update HomeAlbumPicWhole set" +
            " num=#{num}" +
            " where id =#{id}" +
            "</if>" +
            "<if test=\"num &lt;= 0\">" +
            "delete from HomeAlbumPicWhole" +
            " where id =#{id}" +
            "</if>" +
            "</script>")
    int upPicNum(@Param("num") long num, @Param("id") long id);
}
