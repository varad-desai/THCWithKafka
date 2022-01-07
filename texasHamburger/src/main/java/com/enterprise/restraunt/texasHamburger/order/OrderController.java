package com.enterprise.restraunt.texasHamburger.order;


import com.enterprise.restraunt.texasHamburger.location.Location;
import com.enterprise.restraunt.texasHamburger.location.LocationService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping(path = "v1/order")
public class OrderController {

    OrderService orderService;
    LocationService locationService;

    Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    public OrderController(OrderService orderService, LocationService locationService) {
        this.orderService = orderService;
        this.locationService = locationService;
    }

    @PostMapping(path = "")
    public ResponseEntity<Object> addOrder(HttpServletRequest request,
                                           @RequestBody String order_list_json_string,
                                           @RequestParam("location") String location_name)
            throws ParseException {
        logger.info("Inside Controller addOrder");
        logger.info("Authenticating request header for username and password");

        JSONParser jsonParser = new JSONParser();
        Gson gson = new GsonBuilder().create();

        // System.out.println(order_list_json_string);
        JSONObject jsonObject = (JSONObject) jsonParser.parse(order_list_json_string);

        JSONArray jsonArray = (JSONArray)jsonObject.get("OrderItem");

        Iterator<JSONObject> iterator = jsonArray.iterator();
        List<OrderItem> orderList = new ArrayList<>();
        while(iterator.hasNext()) {
            OrderItem orderItem = gson.fromJson(iterator.next().toString(), OrderItem.class);
            // System.out.println(orderItem);
            orderList.add(orderItem);

        }

        Location l = locationService.getLocationByLocationName(location_name);

        if (l.equals(null)) {
            logger.warn("Incorrect location is requested.");
            return new ResponseEntity<>("Location does not exists", HttpStatus.BAD_REQUEST);
        } else if (!orderService.checkIfOrderItemsArePresentInMenu(orderList, l.getId()).isEmpty()) {
            logger.warn("Incorrect order items are ordered");
            return new ResponseEntity<>("Order item does not exists", HttpStatus.BAD_REQUEST);
        } else {
            logger.info("Everything is correct. Placing Order.");
            Order order = orderService.createOrder(orderList, l.getId());
            return new ResponseEntity<Object>(orderService.getOrderResponseBody(order), HttpStatus.OK);
        }
    }

    @GetMapping(path = "")
    public ResponseEntity<Object> getAllOrders(HttpServletRequest request){
        logger.info("Inside Controller getAllOrders");
        logger.info("Authenticating request header for username and password");

        ResponseEntity<Object> header_authentication_result = locationService.authenticateHeader(request);

        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
            logger.error("Location Credentials are incorrect.");
            return header_authentication_result;
        } else {
            logger.info("Authenticated header to be correct.");
            String username = header_authentication_result.getBody().toString();

            UUID location_id = locationService.getLocationByUsername(username).getId();

            Map<Integer, Map<Object, Object>> response_body = orderService.getAllOrdersResponseBodyByLocation(location_id);

            logger.info("Returning response for getAllOrders controller.");

            return new ResponseEntity<Object>(response_body, HttpStatus.OK);
        }
    }

    @DeleteMapping(path = "")
    public ResponseEntity<Object> deleteOrder(HttpServletRequest request,
                                              @RequestParam("orderId") UUID orderId){
        logger.info("Inside Controller deleteOrder");
        logger.info("Authenticating request header for username and password");

        ResponseEntity<Object> header_authentication_result = locationService.authenticateHeader(request);

        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
            logger.error("Location Credentials are incorrect.");
            return header_authentication_result;
        } else {
            if(!orderService.checkIfOrderExists(orderId)){
                logger.error("Order does not exists");
                return new ResponseEntity<>("Order does not exists", HttpStatus.BAD_REQUEST);
            } else {
            logger.info("Authenticated header is be correct. And Order exists");

            orderService.deleteOrderById(orderId);

            logger.info("Order deleted successfully.");

            return new ResponseEntity<Object>("Order deleted successfully.", HttpStatus.OK);
            }
        }
    }


}

