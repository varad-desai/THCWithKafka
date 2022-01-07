package com.enterprise.restraunt.texasHamburger.menuItem;

import com.enterprise.restraunt.texasHamburger.location.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MenuItemService {

    MenuItemRepository menuItemRepository;
    LocationService locationService;
    Logger logger = LoggerFactory.getLogger(MenuItemService.class);


    @Autowired
    public MenuItemService(MenuItemRepository menuItemRepository, LocationService locationService) {
        this.locationService = locationService;
        this.menuItemRepository = menuItemRepository;
    }

    public Map<String, String> addMenuItemToLocation(String username, MenuItem menuItem) {
        logger.info("Inside addMenuItemToLocation service method");
        UUID l_id = locationService.getLocationByUsername(username).getId();
        UUID mi_id = UUID.randomUUID();

        menuItem.setLocation_id(l_id);
        menuItem.setId(mi_id);

        logger.info("Saving Menu Item");
        menuItemRepository.save(menuItem);

        return getMenuItemResponseBody(menuItem);
    }

    public Map<String, String> getMenuItemResponseBody(MenuItem menuItem) {

        Map<String, String> result = new HashMap<>();

        result.put("id", menuItem.getId().toString());
        result.put("item_name", menuItem.getItem_name());
        result.put("price", menuItem.getPrice().toString());

        return result;
    }

    public boolean checkIfMenuItemExists(UUID mi_id) {
        List<MenuItem> mis = menuItemRepository.findAll();

        for (MenuItem mi : mis) {
            if (mi.getId().equals(mi_id)) {
                return true;
            }
        }

        return false;
    }

    public boolean checkIfMenuItemExistsInLocation(UUID menuItemId, String username) {
        List<MenuItem> mis = menuItemRepository.findAll();
        UUID location_id = locationService.getLocationByUsername(username).getId();

        for (MenuItem mi : mis) {
            if (mi.getId().equals(menuItemId) && mi.getLocation_id().equals(location_id)) {
                return true;
            }
        }

        return false;
    }

    public Map<Integer, Map<String, String>> getAllMenuItems(UUID location_id) {
        List<MenuItem> mis = menuItemRepository.findAll();
        Map<Integer, Map<String, String>> result = new HashMap<>();

        int i = 1;
        for (MenuItem mi : mis) {
            if (mi.getLocation_id().equals(location_id)) {
                result.put(i++, getMenuItemResponseBody(mi));
            }
        }

        return result;
    }

    public void deleteMenuItem(UUID menu_item_id) {
        menuItemRepository.deleteById(menu_item_id);
    }

    public MenuItem getMenuItemById(UUID menu_item_id) {
        List<MenuItem> mis = menuItemRepository.findAll();

        for (MenuItem mi : mis) {
            if (mi.getId().equals(menu_item_id)) {
                return mi;
            }
        }

        return null;
    }

    public void updateMenuItem(MenuItem menuItem) {
        logger.info("Inside service method updateMenuItem");
        MenuItem mi = getMenuItemById(menuItem.getId());

        mi.setPrice(menuItem.getPrice());

//        menuItemRepository.deleteById(menuItem.getId());

        menuItemRepository.save(mi);

        logger.info("updateMenuItem " + menuItem);
        logger.info("Exiting service method updateMenuItem");
    }

}
