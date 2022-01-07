package com.enterprise.restraunt.texasHamburger.location;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity(name = "Location")
@Table( name = "location",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "location_name_unique",
                        columnNames = "name"
                )
        }
)
@Getter
@Setter
public class Location {

    @Id
    private UUID id;

    @Column(
            name = "address",
            columnDefinition = "TEXT"
    )
    private String address;

    @Column(
            name = "name",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String name;

    @Column(
            name = "username",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String username;

    @Column(
            name = "password",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String password;

    @Override
    public String toString() {
        return "Location{" +
                "id=" + this.getId() +
                ", address='" + this.getAddress() + '\'' +
                ", username='" + this.getUsername() + '\'' +
                ", password='" + this.getPassword() + '\'' +
                '}';
    }
}
