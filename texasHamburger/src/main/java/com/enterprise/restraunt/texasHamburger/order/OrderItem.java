package com.enterprise.restraunt.texasHamburger.order;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItem {

    UUID id;
    UUID menu_item_id;
    String menu_item_name;
    int quantity;

}
