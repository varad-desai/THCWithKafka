package com.enterprise.restraunt.texasHamburger.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@ToString
@Document(collection = "Order")
public class Order {

    @Id
    private UUID id;
    private UUID location_id;
    private List<OrderItem> order_items;
    private Double total_price;

}
