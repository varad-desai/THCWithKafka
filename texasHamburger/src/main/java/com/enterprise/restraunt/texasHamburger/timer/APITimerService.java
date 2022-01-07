package com.enterprise.restraunt.texasHamburger.timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class APITimerService {

    APITimerRepository apiTimerRepository;

    @Autowired
    public APITimerService(APITimerRepository apiTimerRepository){
        this.apiTimerRepository = apiTimerRepository;
    }

    public void addTime(String controller_name, long time_in_ms){
        UUID id = UUID.randomUUID();
        APITimer apiTimer = new APITimer();
        apiTimer.setId(id);
        apiTimer.setName(controller_name);
        apiTimer.setTime_in_ms(time_in_ms);
        apiTimerRepository.save(apiTimer);
    }


}
