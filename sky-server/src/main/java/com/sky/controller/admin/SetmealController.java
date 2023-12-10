package com.sky.controller.admin;


import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api("套餐接口")
public class SetmealController {

    @Autowired
    SetmealService setmealService;

    @ApiOperation("新增套餐")
    @PostMapping
    //清除缓存数据
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealVO.categoryId")
    public Result savesetmeal(@RequestBody SetmealVO setmealVO){
        setmealService.savesetmeal(setmealVO);
        return Result.success();
    }
    @ApiOperation("菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult=setmealService.pagequery(setmealPageQueryDTO);
        return Result.success(pageResult);

    }
    @PostMapping("/status/{status}")
    @ApiOperation("起售禁售套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)//直接清理所有缓存数据
    public  Result StartorStop(@PathVariable Integer status,Long id){
        log.info("起售禁售",status,id);
        setmealService.StartorStop(status,id);
        return Result.success();
    }
    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)//直接清理所有缓存数据
    public Result update(@RequestBody SetmealVO setmealVO)
    {     setmealService.update(setmealVO);
         return Result.success();
    }
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public  Result getbysetmealid(@PathVariable Integer id){
        SetmealVO setmealVO=setmealService.getbysetmealid(id);
        return Result.success(setmealVO);
    }
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public  Result delete(@RequestParam List<Long> ids){ //不写这个param没人给你解析啊
        setmealService.delete(ids);
        return Result.success();
    }





}
