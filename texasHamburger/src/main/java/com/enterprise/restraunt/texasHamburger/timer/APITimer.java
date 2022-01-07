package com.enterprise.restraunt.texasHamburger.timer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.UUID;

@Getter
@Setter
@ToString
@Document(collection = "APITimer")
public class APITimer {

    @Id
    private UUID id;
    private String name;
    private long time_in_ms;
}
