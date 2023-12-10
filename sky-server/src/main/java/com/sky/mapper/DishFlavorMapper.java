package com.sky.mapper;


import com.sky.annotation.autofill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    //@autofill(value = OperationType.INSERT)
    //不需要填充公共字段啊
    void insertBatch(List<DishFlavor> flavors);

    void deletebydishids(List<Long> ids);


    @Select("select * from sky_take_out.dish_flavor where dish_id=#{dishid} ")
   List<DishFlavor> getbydishid(Long dishid);

    void update(DishFlavor dishFlavor);
}
