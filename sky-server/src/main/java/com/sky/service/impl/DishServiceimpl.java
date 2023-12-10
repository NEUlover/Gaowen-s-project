package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceimpl implements DishService {

    @Autowired
    DishMapper dishMapper;

    @Autowired
    DishFlavorMapper dishFlavorMapper;

    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void savewithflavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.savewithflavor(dish);//插入数据

        //返回insert生成的主键值
        //TODO 这个名字也要一样?
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();//口味表
        if(flavors!=null&&flavors.size()>0){
            //给口味表赋值
            flavors.forEach(dishflavor->
                   dishflavor.setDishId(dishId) );
            dishFlavorMapper.insertBatch(flavors);
        }
        

    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        int page = dishPageQueryDTO.getPage();
        int pageSize = dishPageQueryDTO.getPageSize();
        PageHelper.startPage(page,pageSize);
        Page<DishVO> page1=dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page1.getTotal(),page1.getResult());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品能否删除

        //起售中不能删除
        for (Long id : ids) {
            Dish dish=dishMapper.getbyid(id);
            if(dish.getStatus()== StatusConstant.ENABLE)
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);

            //是否和套餐关联了
            List<Long>setmealids=setmealDishMapper.getsetmealidbydishids(ids);
           if(setmealids!=null&&setmealids.size()>0)
           {
               throw  new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
           }

           //删除菜表中的数据
            dishMapper.deleteBatch(ids); //直接删除数组更有效率

            //删除菜品口味的数据
            dishFlavorMapper.deletebydishids(ids);

            //sql:delete from ?? where id in (1,2,3,4)
        }
    }

    @Override
    public DishVO getbyidwithflavors(Long id) {
        //查询菜品
        Dish dish = dishMapper.getbyid(id);
        //查询口味
        List<DishFlavor> flavors=dishFlavorMapper.getbydishid(id);
        //封装返回
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(flavors);

        return dishVO;
    }

    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        DishFlavor dishFlavor1=new DishFlavor();
        if(flavors!=null&&flavors.size()>0)
         dishFlavor1 = flavors.get(0);
        BeanUtils.copyProperties(dishDTO,dish);
        //修改菜品表
        dishMapper.update(dish);
        Long dishId = dish.getId();
        //修改口味表
        if(flavors!=null&&flavors.size()>0)
        dishFlavorMapper.update(dishFlavor1);

    }

    /*
    * 根据分类id查询菜品*/
    @Override
    public void StartorStop(Integer status, Long id) {
        Dish dish=new Dish();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.update(dish);
    }

    @Override
    public List<Dish> getbyCategoryid(Long categoryId) {
        List<Dish>list=dishMapper.selectbycategoryid(categoryId);
        return list;
    }


    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        //查询起售中的菜品
        List<Dish> dishList = dishMapper.selectbycategoryid2(dish.getCategoryId());

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getbydishid(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

}
