package com.enterprise.restraunt.texasHamburger.location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.validator.routines.EmailValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(path = "v1/location")
public class LocationController {

    LocationService locationService;

    Logger logger = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<Object> createLocation(@RequestBody Location location){
        logger.info("Inside Controller createLocation");

        if (location.getAddress() == null ||
                location.getUsername() == null ||
                location.getPassword() == null ||
                location.getName() == null
        ) {
            logger.error("Controller createLocation: Address, Username or Password is null in Request body.");

            return new ResponseEntity<Object>("Address, Username and Password cannot be null",
                    HttpStatus.BAD_REQUEST);

        } else if (!EmailValidator.getInstance().isValid(location.getUsername())) {
            logger.error("Controller createLocation: User did not enter a valid email in Request body.");

            return new ResponseEntity<Object>("Username is not a valid Email", HttpStatus.BAD_REQUEST);

        } else if (locationService.checkIfLocationExists(location.getUsername())) {
            logger.error("This location already Exists.");
            return new ResponseEntity<Object>("Location Already Exists",HttpStatus.BAD_REQUEST);

        } else {
            logger.info("Location entered valid data in Request body.");
            Location response_location = locationService.createLocation(location);
            logger.info("Created this user: "+response_location.toString());

            logger.info("Creating Response body for user.");
            Map<String, String> locationDetails = locationService.locationResponseBody(response_location);

            logger.info("Returning response for createLocation controller.");
            return new ResponseEntity<Object>(locationDetails, HttpStatus.CREATED);
        }
    }

    @GetMapping(path = "self")
    public ResponseEntity<Object> getLocation(HttpServletRequest request){
        logger.info("Inside Controller getLocation");
        logger.info("Authenticating request header for username and password");

        ResponseEntity<Object> header_authentication_result = locationService.authenticateHeader(request);

        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){

            logger.error("Location Credentials are incorrect.");
            return header_authentication_result;
        } else {

            logger.info("Authenticated header to be correct.");
            String username = header_authentication_result.getBody().toString();

            logger.info("Getting location by username.");
            Location location = locationService.getLocationByUsername(username);

                logger.info("Returning response for getUser controller.");
                logger.info("Returning user information: " + location.toString());

                return new ResponseEntity<Object>(locationService.locationResponseBody(location), HttpStatus.OK);
        }
    }

    @GetMapping(path = "all")
    public ResponseEntity<Object> getAllLocations(HttpServletRequest request){
        logger.info("Inside Controller getAllLocations");
        logger.info("Authenticating request header for username and password");

        ResponseEntity<Object> header_authentication_result = locationService.authenticateHeader(request);

        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){

            logger.error("Location Credentials are incorrect.");
            return header_authentication_result;
        } else {
            logger.info("Returning response for getAllLocations controller.");

            return new ResponseEntity<Object>(locationService.getAllLocationsResponseBody(), HttpStatus.OK);
        }
    }

    @DeleteMapping(path = "self")
    public ResponseEntity<Object> deleteLocation(HttpServletRequest request){
        logger.info("Inside Controller deleteLocation");
        logger.info("Authenticating request header for username and password");

        ResponseEntity<Object> header_authentication_result = locationService.authenticateHeader(request);

        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){

            logger.error("Location Credentials are incorrect.");
            return header_authentication_result;
        } else {

            logger.info("Authenticated header to be correct.");
            String username = header_authentication_result.getBody().toString();

            return new ResponseEntity<Object>(
                    locationService.deleteLocationByUsername(username)?
                            "Deleted location successfully":"Cannot Delete location",
                    HttpStatus.OK);
        }
    }

}
