package com.enterprise.restraunt.texasHamburger.menuItem;

import com.enterprise.restraunt.texasHamburger.location.Location;
import com.enterprise.restraunt.texasHamburger.location.LocationController;
import com.enterprise.restraunt.texasHamburger.location.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "v1/menuItem")
public class MenuItemController {

    LocationService locationService;
    MenuItemService menuItemService;

    Logger logger = LoggerFactory.getLogger(MenuItemController.class);

    @Autowired
    public MenuItemController(MenuItemService menuItemService, LocationService locationService) {
        this.locationService = locationService;
        this.menuItemService = menuItemService;
    }

    @PostMapping(path = "")
    public ResponseEntity<Object> addMenuItem(HttpServletRequest request, @RequestBody MenuItem menuItem){
        logger.info("Inside Controller addMenuItem");
        logger.info("Authenticating request header for username and password");

        ResponseEntity<Object> header_authentication_result = locationService.authenticateHeader(request);

        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
            logger.error("Location Credentials are incorrect.");
            return header_authentication_result;
        } else {
            logger.info("Authenticated header to be correct.");
            String username = header_authentication_result.getBody().toString();

            Map<String, String> response_body = menuItemService.addMenuItemToLocation(username, menuItem);

            logger.info("Returning response for addMenuItem controller.");

            return new ResponseEntity<Object>(response_body, HttpStatus.OK);
        }
    }

    @GetMapping()
    public ResponseEntity<Object> getAllMenuItems(@RequestParam("location") String location_name){
        logger.info("Inside Controller getAllMenuItems");

        UUID location_id = locationService.getLocationIdByLocationName(location_name);

        if(location_id == null){
            return new ResponseEntity<>("Location does not exists", HttpStatus.BAD_REQUEST);
        } else {
            Map<Integer, Map<String, String>> menu = menuItemService.getAllMenuItems(location_id);
            if(menu == null){
                return new ResponseEntity<>(
                        "Location does not contain any menu items",
                        HttpStatus.EXPECTATION_FAILED);
            } else {
                return new ResponseEntity<>(menu, HttpStatus.OK);
            }
        }
    }

    @DeleteMapping(path = "")
    public ResponseEntity<Object> deleteMenuItem(HttpServletRequest request, @RequestParam("id") String menuItemId_string){
        logger.info("Inside Controller deleteMenuItem");
        logger.info("Authenticating request header for username and password");

        UUID menuItemId = UUID.fromString(menuItemId_string);

        ResponseEntity<Object> header_authentication_result = locationService.authenticateHeader(request);

        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
            logger.error("Location Credentials are incorrect.");
            return header_authentication_result;
        } else {
            logger.info("Authenticated header to be correct.");
            String username = header_authentication_result.getBody().toString();

            if(!menuItemService.checkIfMenuItemExists(menuItemId)){
                return new ResponseEntity<>("Menu item does not exists", HttpStatus.BAD_REQUEST);
            } else if(!menuItemService.checkIfMenuItemExistsInLocation(menuItemId, username)){
                return new ResponseEntity<>(
                        "This menu item does not exists for this user",
                        HttpStatus.BAD_REQUEST
                );
            } else {

                menuItemService.deleteMenuItem(menuItemId);

                logger.info("Returning response for deleteMenuItem controller.");

                return new ResponseEntity<Object>("Menu item successfully deleted.", HttpStatus.OK);
            }
        }
    }

    @PutMapping()
    public ResponseEntity<Object> updateMenuItem(HttpServletRequest request, @RequestBody MenuItem menuItem){
        logger.info("Inside Controller deleteMenuItem");
        logger.info("Authenticating request header for username and password");

        logger.info("#$# => "+ menuItem.toString());
        ResponseEntity<Object> header_authentication_result = locationService.authenticateHeader(request);

        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
            logger.error("Location Credentials are incorrect.");
            return header_authentication_result;
        } else {
            logger.info("Authenticated header to be correct.");
            String username = header_authentication_result.getBody().toString();

            if(!menuItemService.checkIfMenuItemExists(menuItem.getId())){
                return new ResponseEntity<>("Menu item does not exists", HttpStatus.BAD_REQUEST);
            } else if(!menuItemService.checkIfMenuItemExistsInLocation(menuItem.getId(), username)){
                return new ResponseEntity<>(
                        "This menu item does not exists for this location",
                        HttpStatus.BAD_REQUEST
                );
            } else {

                menuItemService.updateMenuItem(menuItem);
                MenuItem mi = menuItemService.getMenuItemById(menuItem.getId());

                logger.info("Returning response for deleteMenuItem controller.");

                return new ResponseEntity<Object>(menuItemService.getMenuItemResponseBody(mi), HttpStatus.OK);
            }
        }
    }

}
