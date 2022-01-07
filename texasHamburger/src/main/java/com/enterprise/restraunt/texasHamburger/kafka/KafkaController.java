package com.enterprise.restraunt.texasHamburger.kafka;

import com.enterprise.restraunt.texasHamburger.location.LocationService;
import com.enterprise.restraunt.texasHamburger.order.Order;
import com.enterprise.restraunt.texasHamburger.order.OrderController;
import com.enterprise.restraunt.texasHamburger.order.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "v1/kafka")
public class KafkaController {

    KafkaProducer kafkaProducer;
    LocationService locationService;
    OrderService orderService;
    KafkaConsumer kafkaConsumer;
    Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    public KafkaController(KafkaProducer kafkaProducer,
                           LocationService locationService,
                           OrderService orderService,
                           KafkaConsumer kafkaConsumer) {
        this.kafkaProducer = kafkaProducer;
        this.locationService = locationService;
        this.orderService = orderService;
        this.kafkaConsumer = kafkaConsumer;
    }

    @PostMapping(path="sendAllOrders")
    public ResponseEntity<Object> sendAllOrdersToKafkaTopic(HttpServletRequest request) throws Exception {
        logger.info("Inside Controller sendAllOrdersToKafkaTopic");
        logger.info("Authenticating request header for username and password");

        ResponseEntity<Object> header_authentication_result = locationService.authenticateHeader(request);

        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
            logger.error("Location Credentials are incorrect.");
            return header_authentication_result;
        } else {
            logger.info("Authenticated header to be correct.");
            String username = header_authentication_result.getBody().toString();

            UUID location_id = locationService.getLocationByUsername(username).getId();

            List<Order> orderList = orderService.getAllOrdersByLocation(location_id);
            kafkaProducer.runProducer(orderList);

            logger.info("Returning response for sendAllOrdersToKafkaTopic controller.");

            return new ResponseEntity<Object>("All orders sent to Kafka", HttpStatus.OK);
        }
    }

    @GetMapping(path="getAllOrders")
    public ResponseEntity<Object> getAllOrdersFromKafkaTopic(HttpServletRequest request) throws Exception {
        logger.info("Inside Controller getAllOrdersFromKafkaTopic");
        logger.info("Authenticating request header for username and password");

        ResponseEntity<Object> header_authentication_result = locationService.authenticateHeader(request);

        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
            logger.error("Location Credentials are incorrect.");
            return header_authentication_result;
        } else {
            logger.info("Authenticated header to be correct.");

            Map<Integer, String> responseBody = kafkaConsumer.runConsumer();

            logger.info("Returning response for sendAllOrdersToKafkaTopic controller.");

            return new ResponseEntity<Object>(responseBody, HttpStatus.OK);
        }
    }

}
