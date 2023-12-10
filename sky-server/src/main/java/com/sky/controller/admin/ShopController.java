package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("admincontroller")
@Api("设置营业状态")
@Slf4j
@RequestMapping("/admin/shop")
public class ShopController {

    public static final String Key="SHOP_STATUS";

    @Autowired
    RedisTemplate redisTemplate;


    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setstatus(@PathVariable Integer status){
        log.info("设置店铺营业状态");
        redisTemplate.opsForValue().set(Key,status);
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getstatus(){
        log.info("获取店铺营业状态");
        Integer status = (Integer) redisTemplate.opsForValue().get(Key);
        return Result.success(status);
    }


}
