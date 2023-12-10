package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("usercontroller") //指定bean的名称不要和shopcontroller重复
@Api("获取营业状态")
@Slf4j
@RequestMapping("/user/shop")
public class ShopController {

    public static final String Key="SHOP_STATUS";
    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getstatus(){
        log.info("获取店铺营业状态");
        Integer status = (Integer) redisTemplate.opsForValue().get(Key);
        return Result.success(status);
    }
}
