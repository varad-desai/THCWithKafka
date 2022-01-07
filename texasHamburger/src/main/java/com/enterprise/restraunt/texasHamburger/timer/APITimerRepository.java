package com.enterprise.restraunt.texasHamburger.timer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface APITimerRepository  extends MongoRepository<APITimer, UUID> { }