package com.enterprise.restraunt.texasHamburger.location;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LocationService {

    LocationRepository locationRepository;
    Logger logger = LoggerFactory.getLogger(LocationService.class);

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public void saveLocation(Location l){
        locationRepository.save(l);
    }

    public List<Location> getLocations(){
        return locationRepository.findAll();
    }

    /**
     * Add user to database
     * @param location
     * @return Location
     */
    public Location createLocation(Location location){
        logger.info("Inside location service method createLocation");
        logger.info("Creating location information.");
        UUID uuid = UUID.randomUUID();

        location.setId(uuid);
        location.setPassword(BCrypt.hashpw(location.getPassword(), BCrypt.gensalt()));

        logger.info("Saving location information to database.");

        saveLocation(location);

        return location;
    }

    public boolean checkIfLocationExists(String username){
        logger.info("Inside location service method checkIfLocationExists");
        if(username == null){
            logger.warn("Username cannot be null.");
            return false;
        }
        logger.info("Getting all locations from database.");
        List<Location> locations = getLocations();
        logger.info("Going through all locations to find the location.");
        for(Location l:locations){

            if(l.getUsername().equals(username)){
                logger.info("Location is present in database.");
                return true;
            }
        }
        logger.warn("Location is not present in database.");
        return false;
    }

    public Map<String, String> locationResponseBody(Location location){
        logger.info("Inside user service method userResponseBody");
        logger.info("Creating user response body.");
        Map<String, String> locationDetails = new HashMap<>();

        locationDetails.put("id", location.getId().toString());
        locationDetails.put("address", location.getAddress());
        locationDetails.put("name", location.getName());
        locationDetails.put("emailId", location.getUsername());

        logger.info("Location response body successfully generated.");
        logger.info("Returning location response body.");
        return locationDetails;
    }

    public String[] getLocationCredentials(String locationHeader){
        logger.info("Inside location service method getLocationCredentials");
        logger.info("Decoding location Credentials from header");
        String[] locationHeaderSplit = locationHeader.split(" ");
        String decodedString;
        byte[] decodedBytes;
        decodedBytes = Base64.decodeBase64(locationHeaderSplit[1]);
        decodedString = new String(decodedBytes);
        String[] locationCredentials = decodedString.split(":");

        logger.info("Successfully decoded location credentials from header");
        logger.info("Returning location credentials decoded from header");

        return locationCredentials;
    }

    public Location getLocationByUsername(String username){
        logger.info("Inside location service method getLocationByUsername");
        logger.info("Getting location by username.");

        Location location = new Location();

        List<Location> locations = getLocations();

        logger.info("Finding location from locations present in the database.");
        for(Location l:locations){
            if(l.getUsername().equals(username)){
                location = l;
            }
        }
        logger.info("Returning location retrieved from database.");
        return location;
    }

    public Map<Integer, Map<String, String>> getAllLocationsResponseBody(){
        logger.info("Inside service method getAllLocationsResponseBody");
        List<Location> locations = getLocations();

        logger.info("Generating allLocationsResponseBody");
        int i=1;
        Map<Integer, Map<String, String>> allLocationsResponseBody = new HashMap<>();
        for(Location l: locations){
            allLocationsResponseBody.put(i++, locationResponseBody(l));
        }

        logger.info("Returning allLocationsResponseBody");
        return allLocationsResponseBody;
    }

    public ResponseEntity<Object> authenticateHeader(HttpServletRequest request){
        logger.info("Inside user service method authenticateHeader");
        logger.info("Authenticating request header.");
        String[] location_credentials; // Array of Strings to store user credentials.
        logger.info("Getting Authorization part of request header.");
        String locationHeader = request.getHeader("Authorization");

        if(locationHeader.endsWith("Og==")) { // When No credentials are provided.
            logger.warn("No credentials were sent in request.");
            return new ResponseEntity<Object>("No credentials sent", HttpStatus.BAD_REQUEST);
        }
        else if (locationHeader!=null && locationHeader.startsWith("Basic")) { // When Header is correct
            logger.info("Header is correct. Sending it to retrieve credentials from it.");
            location_credentials = getLocationCredentials(locationHeader);
        }
        else { // When authentication type is correct.
            logger.warn("Header is not correct.");
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }

        Location location_from_database;
        if(!checkIfLocationExists(location_credentials[0])){ // When user does not exist in database.
            logger.info("Checking if location exists in database.");
            return new ResponseEntity<Object>("Location dont Exists",HttpStatus.BAD_REQUEST);
        } else { // When correct user existing in database is getting requested for update or get.
            logger.info("Get location by username from database.");
            location_from_database = getLocationByUsername(location_credentials[0]);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if (    !encoder.matches(location_credentials[1],
                    location_from_database.getPassword())) { // When password is not correct.
                logger.warn("Invalid password is entered for authentication.");
                return new ResponseEntity<Object>("Invalid Password", HttpStatus.BAD_REQUEST);
            } else { // When everything is correct.
                logger.warn("Location credentials authenticated successfully.");
                return new ResponseEntity<Object>(location_from_database.getUsername(), HttpStatus.OK);
            }
        }
    }

    public boolean deleteLocationByUsername(String username){
        logger.info("Inside deleteLocationByUsername service method");
        if(checkIfLocationExists(username)){

            logger.info("Getting all locations");
            List<Location> locations = getLocations();

            for(Location l : locations){
                if(l.getUsername().equals(username)){
                    UUID uuid = l.getId();
                    logger.info("Deleting location by id");
                    locationRepository.deleteById(uuid);
                }
            }

            return true;
        }

        logger.info("Cannot delete location");
        return false;
    }

    public UUID getLocationIdByLocationName(String location_name){
        List<Location> locations = getLocations();

        for(Location l:locations){
            if(l.getName().equals(location_name)){
                return l.getId();
            }
        }

        return null;
    }
    public Location getLocationByLocationName(String location_name){
        List<Location> locations = getLocations();

        for(Location l:locations){
            if(l.getName().equals(location_name)){
                return l;
            }
        }

        return null;
    }
    public Location getLocationByLocationId(UUID location_id){
        List<Location> locations = getLocations();

        for(Location l:locations){
            if(l.getId().equals(location_id)){
                return l;
            }
        }

        return null;
    }

}
