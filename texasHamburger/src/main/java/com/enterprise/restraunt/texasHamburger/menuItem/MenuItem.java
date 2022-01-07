package com.enterprise.restraunt.texasHamburger.menuItem;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity(name = "Menu")
@Table( name = "menu"//,
//        uniqueConstraints = {
//                @UniqueConstraint(
//                        name = "location_name_unique",
//                        columnNames = "name"
//                )
//        }
)
@Getter
@Setter
public class MenuItem {

    @Id
    private UUID id;

    @Column(
            name = "location_id",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private UUID location_id;

    @Column(
            name = "item_name",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String item_name;

    @Column(
            name = "price",
            nullable = false,
            columnDefinition = "NUMERIC"
    )
    private Double price;

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + this.getId() +
                ", location_id=" + this.getLocation_id() +
                ", item_name='" + this.getItem_name() + '\'' +
                ", price=" + this.getPrice() +
                '}';
    }
}
