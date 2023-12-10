package com.sky.mapper;


import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface shoppingCartMapper {

    //返回购物车数据
    List<ShoppingCart> list(ShoppingCart shoppingCart);


    //更新购物车数据
    void update(ShoppingCart cart);

    @Insert("insert into sky_take_out.shopping_cart (name,user_id,dish_id,setmeal_id,dish_flavor,amount,number,image,create_time)" +
            " values (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{amount},#{number},#{image},#{createTime})")
    void insert(ShoppingCart shoppingCart);


    @Delete("delete from sky_take_out.shopping_cart where user_id=#{userId}")
    void deletebyid(Long userId);
}
