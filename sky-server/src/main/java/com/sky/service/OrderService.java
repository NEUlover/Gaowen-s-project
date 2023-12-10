package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.*;

import java.time.LocalDate;

public interface OrderService {
    /*
    * 用户下单
    * */
    OrderSubmitVO submitorder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    PageResult pagequery(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO selectbyorderid(Long id);

    void usercancelorder(Long id);

    void repetition(Long id);

    void confirm(Long id);

    void rejectionorder(OrdersRejectionDTO ordersRejectionDTO);

    void admincancelorder(OrdersCancelDTO ordersCancelDTO);

    void delivery(Long id);

    void complete(Long id);

    OrderStatisticsVO statistic();

    void reminderorder(Long id);


}
