package com.enterprise.restraunt.texasHamburger.order;

import com.enterprise.restraunt.texasHamburger.location.LocationService;
import com.enterprise.restraunt.texasHamburger.menuItem.MenuItem;
import com.enterprise.restraunt.texasHamburger.menuItem.MenuItemRepository;
import com.enterprise.restraunt.texasHamburger.menuItem.MenuItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class    OrderService {

    @Autowired
    OrderRepository orderRepository;
    MenuItemRepository menuItemRepository;
    LocationService locationService;
    Logger logger = LoggerFactory.getLogger(MenuItemService.class);


    @Autowired
    public OrderService(OrderRepository orderRepository,
                        MenuItemRepository menuItemRepository,
                        LocationService locationService) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.locationService = locationService;
    }

    public Order createOrder(List<OrderItem> orderItemList, UUID location_id) {
        // TO DO
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setLocation_id(location_id);
        order.setOrder_items(orderItemList);
        order.setTotal_price(getTotalPriceOfOrder(orderItemList, location_id));
        orderRepository.save(order);
        return order;
    }

    public List<OrderItem> checkIfOrderItemsArePresentInMenu(List<OrderItem> orderItemList, UUID location_id) {
        // TO DO
        List<MenuItem> mis = menuItemRepository.findAll();
        List<OrderItem> orderItemsNotPresent = new ArrayList<>();
        boolean result = true;
        for (OrderItem oi : orderItemList) {
            int i = 0;
            for (MenuItem mi : mis) {
                if (mi.getLocation_id().equals(location_id) && oi.getMenu_item_name().equals(mi.getItem_name())) {

                    oi.setMenu_item_id(mi.getId());
                    i = 1;
                }
            }
            if (i != 1) {
                orderItemsNotPresent.add(oi);
            }
        }
        return orderItemsNotPresent;
    }

    public double getTotalPriceOfOrder(List<OrderItem> orderItemList, UUID location_id) {
        // TO DO
        List<MenuItem> mis = menuItemRepository.findAll();
        double total_price = 0.0;
        for (OrderItem oi : orderItemList) {
            for (MenuItem mi : mis) {
                if (mi.getLocation_id().equals(location_id) && oi.getMenu_item_name().equals(mi.getItem_name())) {
                    total_price = total_price + (mi.getPrice() * oi.getQuantity());
                }
            }
        }
        return total_price;
    }

    public Map<Object, Object> getOrderResponseBody(Order order) {
        // TO DO
        Map<Object, Object> orderResponseBody = new HashMap<>();
        orderResponseBody.put("id", order.getId());
        orderResponseBody.put("location", locationService.getLocationByLocationId(order.getLocation_id()).getName());
        orderResponseBody.put("order_list", order.getOrder_items());
        orderResponseBody.put("total_price", order.getTotal_price());
        return orderResponseBody;
    }

    public Map<Integer, Map<Object, Object>> getAllOrdersResponseBodyByLocation(UUID location_id) {
        List<Order> orderList = orderRepository.findAll();
        Map<Integer, Map<Object, Object>> result = new HashMap<>();

        int i = 1;
        for (Order o : orderList) {
            if (o.getLocation_id().equals(location_id)) {
                result.put(i++, getOrderResponseBody(o));
            }
        }

        return result;
    }

    public List<Order> getAllOrdersByLocation(UUID location_id) {
        List<Order> orderList = orderRepository.findAll();
        List<Order> result = new ArrayList<>();
        for (Order o : orderList) {
            if (o.getLocation_id().equals(location_id)) {
                result.add(o);
            }
        }
        return result;
    }


    public boolean checkIfOrderExists(UUID orderId){
        List<Order> orderList = orderRepository.findAll();
        for (Order o : orderList) {
            if (o.getId().equals(orderId)) {
                return true;
            }
        }
        return false;
    }

    public void deleteOrderById(UUID orderId){
        orderRepository.deleteById(orderId);
    }

}
