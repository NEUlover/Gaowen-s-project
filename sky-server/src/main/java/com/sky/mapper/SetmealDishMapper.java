package com.sky.mapper;


import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    List<Long> getsetmealidbydishids(List<Long> ids);



    //插入套餐-菜品
    void insert(List<SetmealDish> setmealDishes);

    @Select("select * from sky_take_out.setmeal_dish where setmeal_id=#{setmealid}")
    List<SetmealDish> selectdishbyid(Integer setmealid);

      //批量删除
    void deletebatch(List<Long> setmealids);
}
