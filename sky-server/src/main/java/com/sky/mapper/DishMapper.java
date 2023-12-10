package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.autofill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from sky_take_out.dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

     @autofill(value = OperationType.INSERT)
     void savewithflavor(Dish dish);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from sky_take_out.dish where id=#{id}")
    Dish getbyid(Long id);


    void deleteBatch(List<Long> ids);

    void update(Dish dish);

    @Select("select * from sky_take_out.dish where category_id=#{categoryId}")
    List<Dish> selectbycategoryid(Long categoryId);

    @Select("select * from sky_take_out.dish where category_id=#{categoryId} and status=1")
    List<Dish> selectbycategoryid2(Long categoryId);

    Integer countByMap(Map map);
}
