package com.busi.dao;

import com.busi.entity.ShopFloorShoppingCart;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 楼店购物车
 * @author: ZHaoJiaJie
 * @create: 2019-12-09 13:58
 */
@Mapper
@Repository
public interface ShopFloorShoppingCartDao {

    /***
     * 新增
     * @param homeShopGoods
     * @return
     */
    @Insert("insert into ShopFloorShoppingCart(userId,goodsId,goodsCoverUrl,goodsTitle,basicDescribe,specs,price,addTime,number) " +
            "values (#{userId},#{goodsId},#{goodsCoverUrl},#{goodsTitle},#{basicDescribe},#{specs},#{price},#{addTime},#{number})")
    @Options(useGeneratedKeys = true)
    int add(ShopFloorShoppingCart homeShopGoods);

    /***
     * 更新
     * @param homeShopGoods
     * @return
     */
    @Update("<script>" +
            "update ShopFloorShoppingCart set" +
            " number=#{number}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int update(ShopFloorShoppingCart homeShopGoods);

    /***
     * 批量删除商品
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from ShopFloorShoppingCart " +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int updateDels(@Param("ids") String[] ids);

    /***
     * 根据Id查询
     * @param userId
     */
    @Select("select * from ShopFloorShoppingCart where userId=#{userId} order by addTime desc")
    List<ShopFloorShoppingCart> findList(@Param("userId") long userId);

    /***
     * 根据Id查询
     * @param goodsId
     */
    @Select("select * from ShopFloorShoppingCart where userId=#{userId} and goodsId=#{goodsId}")
    ShopFloorShoppingCart findId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    /***
     * 统计用户购物车商品数量
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from ShopFloorShoppingCart" +
            " where 1=1 " +
            " and userId = #{userId}" +
            "</script>")
    int findNum(@Param("userId") long userId);
}
