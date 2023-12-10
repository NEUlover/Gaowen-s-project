package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void savewithflavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteBatch(List<Long> ids);

    DishVO getbyidwithflavors(Long id);

    void update(DishDTO dishDTO);

    void StartorStop(Integer status, Long id);

    List<Dish> getbyCategoryid(Long categoryId);

    List<DishVO> listWithFlavor(Dish dish);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */

}
