package com.sky.controller.admin;


import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@Api("菜品接口")
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    DishService dishService;

    @Autowired
    RedisTemplate redisTemplate;

    /*
    * 新增菜品
    *  */
    @ApiOperation("新增菜品")
    @PostMapping
    public Result savewithflavor(@RequestBody DishDTO dishDTO){
        log.info("新增菜品");
        dishService.savewithflavor(dishDTO);
        //清除缓存
        String key="dish_"+dishDTO.getCategoryId();
        //新增菜品只需要删除对应的类的缓存
        cleancache(key);
        return Result.success();

    }
   @ApiOperation("菜品分页查询")
   @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult=dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);

   }
   @ApiOperation("删除菜品")
   @DeleteMapping
   public Result deletedish(@RequestParam List<Long> ids)//用来解析传来的参数
   {
       dishService.deleteBatch(ids);
       //批量删除的话一起删掉缓存
      cleancache("dish_*");
      //redisTemplate.delete()  这么删那么删除的是指定的
       return Result.success();
   }
   @ApiOperation("根据id查询菜品")
   @GetMapping("/{id}")
   public Result<DishVO> getByid(@PathVariable Long id){
        log.info("根据id查询菜品{}",id);
        DishVO dishVO=dishService.getbyidwithflavors(id);
        return Result.success(dishVO);
   }

   @PutMapping
   @ApiOperation("修改菜品")
   public Result update(@RequestBody DishDTO dishDTO){
        dishService.update(dishDTO);
        //如果修改分类会变复杂.所以全部删除
       cleancache("dish_*");
        return Result.success();
   }

    @PostMapping("/status/{status}")
    @ApiOperation("起售禁售菜品")
    public  Result StartorStop(@PathVariable Integer status,Long id){
        log.info("起售禁售",status,id);
        dishService.StartorStop(status,id);
        cleancache("dish_*");
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品") //拿到的是categoryId
    public  Result<List<Dish>> getbyCategoryid(Long categoryId)
    {
        List<Dish> list=dishService.getbyCategoryid(categoryId);
        return Result.success(list);
    }


    //清除缓存
    public void cleancache(String pattern){
        //封装成集合然后批量删除
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }


}
