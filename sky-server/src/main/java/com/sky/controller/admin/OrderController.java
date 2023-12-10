package com.sky.controller.admin;


import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminorder")
@RequestMapping("/admin/order")
@Slf4j
@Api("订单相关接口")
public class OrderController {
    @Autowired
    OrderService orderService;

    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索分页")
    public Result<PageResult> pagequery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult pageResult = orderService.pagequery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/details/{id}")
    @ApiOperation("查看订单详情")
    public Result<OrderVO> selectbyorderid(@PathVariable Long id) {
        OrderVO orderVO = orderService.selectbyorderid(id);
        return Result.success(orderVO);
    }
    /*
     *
     * 接单*/
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirmorder(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.confirm(ordersConfirmDTO.getId());
        return Result.success();
    }

    /*
    * 拒单
    * */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public  Result rejectionorder(@RequestBody OrdersRejectionDTO ordersRejectionDTO)
    {   orderService.rejectionorder(ordersRejectionDTO);
        return Result.success();
    }
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public  Result cancelorder(@RequestBody OrdersCancelDTO ordersCancelDTO)
    {
        orderService.admincancelorder(ordersCancelDTO);
        return Result.success();
    }
    /**
     * 派送订单
     *
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable("id") Long id) {
        orderService.delivery(id);
        return Result.success();
    }
    /**
     * 完成订单
     *
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("派送订单")
    public Result complete(@PathVariable("id") Long id) {
        orderService.complete(id);
        return Result.success();
    }

    /**
     * 统计订单
     *
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("派送订单")
    public Result<OrderStatisticsVO> statistic(){
        OrderStatisticsVO orderStatisticsVO=orderService.statistic();
        return Result.success(orderStatisticsVO);
    }
}
