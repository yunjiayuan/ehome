package com.busi.dao;

import com.busi.entity.RentAhouse;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 租房买房
 * @author: ZHaoJiaJie
 * @create: 2020-04-20 21:50:43
 */
@Mapper
@Repository
public interface RentAhouseDao {
    /***
     * 新增房源
     * @param kitchenBooked
     * @return
     */
    @Insert("insert into RentAhouse(userId,roomState,title,describe,picture,videoUrl,videoCover,villageName,province,city,district,lat,lon,houseNumber,houseCompany,unitNumber,unitCompany,roomNumber,residence,livingRoom,toilet,housingArea,roomType,orientation,renovation,rentalType,bedroomType,houseType,expectedPrice,paymentMethod,lookHomeTime,realName,elevator,propertyFee,heatingCost,floor,totalFloor,addTime,refreshTime)" +
            "values (#{userId},#{roomState},#{title},#{describe},#{picture},#{videoUrl},#{videoCover},#{villageName},#{province},#{city},#{district},#{lat},#{lon},#{houseNumber},#{houseCompany},#{unitNumber},#{unitCompany},#{roomNumber},#{residence},#{livingRoom},#{toilet},#{housingArea},#{roomType},#{orientation},#{renovation},#{rentalType},#{bedroomType},#{houseType},#{expectedPrice},#{paymentMethod},#{lookHomeTime},#{realName},#{elevator},#{propertyFee},#{heatingCost},#{floor},#{totalFloor},#{addTime},#{refreshTime})")
    @Options(useGeneratedKeys = true)
    int addCommunity(RentAhouse kitchenBooked);

    /***
     * 更新房源
     * @param kitchenBooked
     * @return
     */
    @Update("<script>" +
            "update RentAhouse set" +
            " title = #{title}," +
            " describe=#{describe}," +
            " picture=#{picture}," +
            " videoUrl = #{videoUrl}," +
            " videoCover=#{videoCover}," +
            " villageName=#{villageName}," +
            " province = #{province}," +
            " city=#{city}," +
            " district=#{district}," +
            " lat = #{lat}," +
            " lon=#{lon}," +
            " houseNumber=#{houseNumber}," +
            " houseCompany = #{houseCompany}," +
            " unitNumber=#{unitNumber}," +
            " unitCompany=#{unitCompany}," +
            " roomNumber = #{roomNumber}," +
            " residence=#{residence}," +
            " livingRoom=#{livingRoom}," +
            " toilet = #{toilet}," +
            " housingArea=#{housingArea}," +
            " roomType=#{roomType}," +
            " orientation = #{orientation}," +
            " renovation=#{renovation}," +

            "<if test=\"roomState == 1\">" +
            " rentalType=#{rentalType}," +
            " bedroomType = #{bedroomType}," +
            " houseType=#{houseType}," +
            " paymentMethod = #{paymentMethod}," +
            "</if>" +

            " expectedPrice=#{expectedPrice}," +
            " lookHomeTime=#{lookHomeTime}," +
            " realName=#{realName}," +
            " elevator = #{elevator}," +
            " propertyFee=#{propertyFee}," +
            " heatingCost=#{heatingCost}," +
            " floor = #{floor}," +
            " totalFloor=#{totalFloor}," +
            " refreshTime=#{refreshTime}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateBooked(RentAhouse kitchenBooked);

    /***
     * 删除房源
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "update RentAhouse set" +
            " state=1" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId=#{userId}" +
            "</script>")
    int delDishes(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 根据Id查询房源
     * @param id
     * @return
     */
    @Select("select * from RentAhouse where id=#{id}")
    RentAhouse findByUserId(@Param("id") long id);

    /***
     * 条件查询房源
     * @param userId    用户ID
     * @param sellState  -1不限 leaseState=0时：0出售中  1已售出  leaseState=1时：0出租中  1已出租
     * @param leaseState  -1不限 0出售  1出租
     * @param sort  排序条件:0最新发布，1价格最低，2价格最高
     * @param nearby  附近 0不限  1附近
     * @param roomType     房型：0不限 1一室 2二室 3三室 4四室 5五室及以上
     * @param lon     经度
     * @param lat     纬度
     * @param province     省
     * @param city      市
     * @param district    区
     * @param minPrice  最小价格
     * @param maxPrice  最大价格
     * @param minArea  最小面积
     * @param maxArea  最大面积
     * @param orientation  朝向：-1不限 0南北、1东北、2东南、3西南、4西北、5东西、6南、7东、8西、9北
     * @param renovation   房屋装修：-1不限 0精装 1普装 2毛坯
     * @param floor   房屋楼层：-1不限 0低楼层 1中楼层 2高楼层
     * @param bedroomType   卧室类型：-1不限 0主卧 1次卧 2其他
     * @param houseType  房源类型: -1不限 0业主直租 1中介
     * @param paymentMethod  支付方式: -1不限  0押一付一 1押一付三 2季付 3半年付 4年付
     * @param openHome  看房时间
     * @param string    模糊搜索
     * @return
     */
    @Select("<script>" +
            "select * from RentAhouse where" +
            "<if test=\"userId > 0\">" +
            " userId = #{userId}" +
            "</if>" +
            " and state=0" +
            " and roomState = #{leaseState}" +
            " and sellState = #{sellState}" +
            " and roomType = #{roomType}" +
            " and orientation = #{orientation}" +
            " and renovation = #{renovation}" +
            " and floor = #{floor}" +
            " and bedroomType = #{bedroomType}" +
            " and houseType = #{houseType}" +
            " and paymentMethod = #{paymentMethod}" +
            " and openHome = #{openHome}" +
            "<if test=\"district >= 0\">" +
            " and district = #{district}" +
            "</if>" +
            "<if test=\"city >= 0\">" +
            " and city = #{city}" +
            "</if>" +
            "<if test=\"province >= 0\">" +
            " and province = #{province}" +
            "</if>" +
            "<if test=\"maxPrice > 0\">" +
            " <![CDATA[ " +
            " and expectedPrice >= #{minPrice} and expectedPrice <= #{maxPrice}" +
            "  ]]> " +
            "</if>" +
            "<if test=\"maxPrice &lt;= 0\">" +
            " and expectedPrice &lt;= #{minPrice}" +
            "</if>" +
            "<if test=\"maxArea > 0\">" +
            " <![CDATA[ " +
            " and housingArea >= #{minArea} and housingArea <= #{maxArea}" +
            "  ]]> " +
            "</if>" +
            "<if test=\"maxArea &lt;= 0\">" +
            " and housingArea &lt;= #{minArea}" +
            "</if>" +
            "<if test=\"string != null and string != '' \">" +
            " and (title LIKE CONCAT('%',#{string},'%')" +
            " or describe LIKE CONCAT('%',#{string},'%')" +
            " or realName LIKE CONCAT('%',#{string},'%')" +
            " or villageName LIKE CONCAT('%',#{string},'%'))" +
            "</if>" +
            "<if test=\"nearby == 1\">" +
            " and lat > #{lat}-1" +  //只对于经度和纬度大于或小于该用户1度(111公里)范围内的用户进行距离计算,同时对数据表中的经度和纬度两个列增加了索引来优化where语句执行时的速度.
            " and lat &lt; #{lat}+1 and lon > #{lon}-1" +
            " and lon &lt; #{lon}+1 order by ACOS(SIN((#{lat} * 3.1415) / 180 ) *SIN((lat * 3.1415) / 180 ) +COS((#{lat} * 3.1415) / 180 ) * COS((lat * 3.1415) / 180 ) *COS((#{lon}* 3.1415) / 180 - (lon * 3.1415) / 180 ) ) * 6380 asc" +
            "</if>" +
            "<if test=\"sort == 0\">" +
            " order by refreshTime desc" +
            "</if>" +
            "<if test=\"sort == 1\">" +
            " order by expectedPrice asc" +
            "</if>" +
            "<if test=\"sort == 2\">" +
            " order by expectedPrice desc" +
            "</if>" +
            "</script>")
    List<RentAhouse> findRentAhouseList(@Param("userId") long userId, @Param("sellState") int sellState, @Param("leaseState") int leaseState, @Param("sort") int sort,
                                        @Param("nearby") int nearby, @Param("roomType") int roomType, @Param("lon") double lon, @Param("lat") double lat, @Param("province") int province,
                                        @Param("city") int city, @Param("district") int district, @Param("minPrice") int minPrice, @Param("maxPrice") int maxPrice,
                                        @Param("minArea") double minArea, @Param("maxArea") double maxArea, @Param("orientation") int orientation, @Param("renovation") int renovation,
                                        @Param("floor") int floor, @Param("bedroomType") int bedroomType, @Param("houseType") int houseType, @Param("paymentMethod") int paymentMethod,
                                        @Param("openHome") int openHome, @Param("string") String string);

}
