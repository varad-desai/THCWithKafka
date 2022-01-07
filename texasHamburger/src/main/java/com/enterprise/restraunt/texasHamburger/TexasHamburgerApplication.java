package com.enterprise.restraunt.texasHamburger;

import com.enterprise.restraunt.texasHamburger.order.OrderRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = OrderRepository.class)
public class TexasHamburgerApplication {

	public static void main(String[] args) {

		SpringApplication.run(TexasHamburgerApplication.class, args);

		System.out.println("Hello World" );
	}

}
