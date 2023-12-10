package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceimpl implements SetmealService {

    @Autowired
    SetmealMapper setmealMapper;

    @Autowired
    SetmealDishMapper setmealDishMapper;



    /*
    * 新增菜品
    * */
    @Override
    public void savesetmeal(SetmealVO setmealVO) {
        Setmeal setmeal=new Setmeal();
        SetmealDish setmealDish=new SetmealDish();
        BeanUtils.copyProperties(setmealVO,setmeal);
        List<SetmealDish> setmealDishes = setmealVO.getSetmealDishes();

        //插入套餐表
        setmealMapper.insert(setmeal);
        //从这里拿到套餐id
        Long id = setmeal.getId();
        //插入套餐-菜品表
        setmealDishes.forEach(setmealdish->setmealdish.setSetmealId(id));
        //这个套餐一次插入一个,但是对应多个菜品,所以全是一个套餐id
        setmealDishMapper.insert(setmealDishes);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

    @Override
    public PageResult pagequery(SetmealPageQueryDTO setmealPageQueryDTO) {
        int page = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();
        PageHelper.startPage(page,pageSize);
        Page<Setmeal> page1=setmealMapper.page(setmealPageQueryDTO);
        return new PageResult(page1.getTotal(),page1.getResult());
    }

    @Override
    public void StartorStop(Integer status, Long id) {
        Setmeal setmeal=new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }

    @Override
    public SetmealVO getbysetmealid(Integer setmealid) {
        //查套餐表
        Setmeal setmeal=setmealMapper.selectsetmealbyid(setmealid);

        //查套餐-菜品表
        List<SetmealDish>list=setmealDishMapper.selectdishbyid(setmealid);
        SetmealVO setmealVO=new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(list);
        return setmealVO;
    }

    @Override
    public void update(SetmealVO setmealVO) {
        Setmeal setmeal=new Setmeal();
        List<SetmealDish> setmealDishes = setmealVO.getSetmealDishes();
        BeanUtils.copyProperties(setmealVO,setmeal);
        //修改套餐表
        setmealMapper.update(setmeal);//返回给他主键值
        Long setmealId = setmeal.getId();
        log.info("拿到的主键值",setmealId);
        //修改套餐-菜品表
        //直接删除原本的,然后再插入
        List<Long>setmealIds=new ArrayList<>();
        setmealIds.add(setmealId);
        setmealDishMapper.deletebatch(setmealIds); //删掉对应的菜品
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        setmealDishMapper.insert(setmealDishes);//在把更新后的插入进来
    }


    /*删除套餐*/
    @Override
    public void delete(List<Long> ids) {
        //删除套餐
        setmealMapper.delete(ids);
        //删除菜品
        setmealDishMapper.deletebatch(ids);
    }
}
