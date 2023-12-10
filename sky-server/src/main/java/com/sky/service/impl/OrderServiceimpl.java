package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceimpl implements OrderService {
    public static Long orderNumber; //订单号

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    shoppingCartMapper shoppingCartMapper;
    @Autowired
    AddressBookMapper addressBookMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    WeChatPayUtil weChatPayUtil;
    @Autowired
    WebSocketServer webSocketServer;



    /*
    * 用户下单
    *
    * */
    @Override
    public OrderSubmitVO submitorder(OrdersSubmitDTO ordersSubmitDTO) {
        //处理各种业务异常
        AddressBook addressbook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        //地址簿为空
        if(addressbook==null)
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
       //购物车为空
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);//购物车
        if(list==null||list.size()==0)
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        //插入订单
        Orders orders=new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setOrderTime(LocalDateTime.now());
        orders.setStatus(Orders.PENDING_PAYMENT);//状态等待付款
        orderNumber=System.currentTimeMillis(); //赋值给共享变量
        orders.setNumber(String.valueOf(orderNumber));//订单号
        orders.setPhone(addressbook.getPhone());
        orders.setConsignee(addressbook.getConsignee());
        orders.setUserId(userId);
        String adddress = addressbook.getProvinceName() + addressbook.getCityName() + addressbook.getDistrictName() + addressbook.getDetail();
        orders.setAddress(adddress);
        orderMapper.insert(orders);//插入订单表
        //插入订单明细
        //向订单明细表插入n条数据
        List<OrderDetail>list1=new ArrayList<>();
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail=new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            list1.add(orderDetail);
        }
       orderDetailMapper.insertbatch(list1);//批量插入订单明细

        //清空购物车
        shoppingCartMapper.deletebyid(userId);
        OrderSubmitVO orderSubmitVO=OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    /**

     * 订单支付

     *

     * @param ordersPaymentDTO

     * @return

     */

    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {


        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();

        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单

//    JSONObject jsonObject = weChatPayUtil.pay(

//        ordersPaymentDTO.getOrderNumber(), //商户订单号

//        new BigDecimal(0.01), //支付金额，单位 元

//        "苍穹外卖订单", //商品描述

//        user.getOpenid() //微信用户的openid

//    );

//

//    if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {

//  throw new OrderBusinessException("该订单已支付");

//    }

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("code", "ORDERPAID");

//

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);

        vo.setPackageStr(jsonObject.getString("package"));


        //为替代微信支付成功后的数据库订单状态更新，多定义一个方法进行修改

        Integer OrderPaidStatus = Orders.PAID; //支付状态，已支付

        Integer OrderStatus = Orders.TO_BE_CONFIRMED;  //订单状态，待接单



        //发现没有将支付时间 check_out属性赋值，所以在这里更新

        LocalDateTime check_out_time = LocalDateTime.now();


        Orders order = orderMapper.getByNumber(String.valueOf(orderNumber));//根据订单号拿到这个订单

        orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, order.getId());

        Map map=new HashMap();
        map.put("type",1);//1是来点提醒 2是催单
        map.put("orderId",order.getId());
        map.put("context","订单号:"+order.getNumber());
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);//发送给客户端
        return vo;

    }



    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }
     /*
     *
     * 分页搜索
     * */
    @Override
    public PageResult pagequery(OrdersPageQueryDTO ordersPageQueryDTO) {
        int page = ordersPageQueryDTO.getPage();
        int pageSize = ordersPageQueryDTO.getPageSize();
        PageHelper.startPage(page,pageSize);
        Page<Orders> page1=orderMapper.page(ordersPageQueryDTO);//分页查询
        List<Orders> orderslist = page1.getResult();
        List<OrderVO>list=new ArrayList<>();
        for (Orders orders : orderslist) {
            OrderVO orderVO=new OrderVO();
            List<OrderDetail> orderDetailList=orderDetailMapper.selectbyorderid(orders.getId());//根据订单id查询订单详情
            BeanUtils.copyProperties(orders,orderVO);
            orderVO.setOrderDetailList(orderDetailList);
            list.add(orderVO);

        }

        return new PageResult(page1.getTotal(),list);

    }

    /*
    *
    * 查询订单详情
    * */
    @Override
    public OrderVO selectbyorderid(Long orderid) {
        Orders orders=orderMapper.selectbyorderid(orderid);
        List<OrderDetail> list = orderDetailMapper.selectbyorderid(orderid);
        OrderVO orderVO=new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(list);
        return orderVO;
    }

    /*
    *
    *
    * 取消订单
    * */
    @Override
    public void usercancelorder(Long orderid) {
        Orders orders = orderMapper.selectbyorderid(orderid);
        if(orders==null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if(orders.getStatus()>2)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        //1状态别来这发癫


        //能过来就是 2状态
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orders.setStatus(Orders.REFUND);
        orderMapper.update(orders);
    }


    /*
    * 再来一单
    *
    * */
    @Override
    public void repetition(Long orderid) {
        List<OrderDetail> orderDetailList = orderDetailMapper.selectbyorderid(orderid);
        Long userId = BaseContext.getCurrentId();
        for (OrderDetail orderDetail : orderDetailList) {
            ShoppingCart shoppingCart=new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCart.setUserId(userId);
            //TODO 可以改成批量插入
            shoppingCartMapper.insert(shoppingCart);  //插入购物车数据
        }

    }



    /*
    * 接单
    * */
    @Override
    public void confirm(Long id) {
        Orders orders = orderMapper.selectbyorderid(id);
        orders.setStatus(Orders.CONFIRMED);//已经支付
        orderMapper.update(orders);
    }

    @Override
    public void rejectionorder(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = orderMapper.selectbyorderid(ordersRejectionDTO.getId());

        // 订单只有存在且状态为2（待接单）才可以拒单
        if (orders == null || !orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());//拒单原因
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void admincancelorder(OrdersCancelDTO ordersCancelDTO) {
        // 根据id查询订单
        Orders orders = orderMapper.selectbyorderid(ordersCancelDTO.getId());

        // 管理端取消订单需要退款，根据订单id更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void delivery(Long orderid) {
        Orders orders = orderMapper.selectbyorderid(orderid);
        if (orders == null || !orders.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    @Override
    public void complete(Long orderid) {
        Orders orders = orderMapper.selectbyorderid(orderid);
        if (orders == null || !orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.COMPLETED);
        orderMapper.update(orders);
    }

    @Override
    public OrderStatisticsVO statistic() {
        OrderStatisticsVO osv=new OrderStatisticsVO();
        osv.setConfirmed(0);
        osv.setToBeConfirmed(0);
        osv.setDeliveryInProgress(0);
        List<Orders>list=orderMapper.getallorders();
        for (Orders orders : list) {
            if(orders.getStatus().equals(Orders.TO_BE_CONFIRMED))//待接单
                osv.setToBeConfirmed(osv.getToBeConfirmed()+1);
            if(orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS))//派送中
                osv.setDeliveryInProgress(osv.getDeliveryInProgress()+1);
            if(orders.getStatus().equals(Orders.CONFIRMED))//已接单
                osv.setConfirmed(osv.getConfirmed()+1);
        }
        return osv;
    }

    @Override
    public void reminderorder(Long orderid) {
        Orders orders = orderMapper.selectbyorderid(orderid);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Map map=new HashMap();
        map.put("type",2);//1是来点提醒 2是催单
        map.put("orderId",orderid);
        map.put("content","订单号:"+orders.getNumber());
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);//发送给客户端
    }
}
