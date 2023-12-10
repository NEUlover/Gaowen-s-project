package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.shoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceimpl implements ShoppingCartService {
    @Autowired
    shoppingCartMapper shoppingCartMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;


    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //判断当前加入的菜品是否已经存在
        Long userId = BaseContext.getCurrentId();//通过令牌解析拿到当前用户id
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);//其实只会查出来一个
        //存在份数加一
        if(list!=null&&list.size()>0) {
            ShoppingCart Cart = list.get(0);
            Cart.setNumber(Cart.getNumber()+1);
            shoppingCartMapper.update(Cart);
        }
        else {//不存在加入购物车表
            //看是套餐还是菜品
            Long dishId = shoppingCart.getDishId();
            if(dishId!=null)
            {    Dish dish=dishMapper.getbyid(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice()); //口味已经拷贝了

            }
            else{
                Long setmealid=shoppingCart.getSetmealId();
                Integer setmealid1=setmealid.intValue();//转成int
                Setmeal setmeal=setmealMapper.selectsetmealbyid(setmealid1);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice()); //口味已经拷贝了

            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);

        }




    }
     /*查看购物车*/
    @Override
    public List<ShoppingCart> showshoppingcart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    @Override
    public void cleancart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(userId);
        shoppingCartMapper.deletebyid(userId);
    }
}
