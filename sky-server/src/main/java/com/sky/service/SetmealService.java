package com.sky.service;

import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void savesetmeal(SetmealVO setmealVO);
    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);

    PageResult pagequery(SetmealPageQueryDTO setmealPageQueryDTO);

    void StartorStop(Integer status, Long id);

    SetmealVO getbysetmealid(Integer id);

    void update(SetmealVO setmealVO);

    void delete(List<Long> ids);
}
