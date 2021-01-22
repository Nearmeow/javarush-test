package com.space.controller;

import com.space.dto.AddOrUpdateShipDtoRequest;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShipController {

    public ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "/rest/ships")
    public List<Ship> getAllShips(@RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "planet", required = false) String planet,
                                  @RequestParam(value = "shipType", required = false) ShipType shipType,
                                  @RequestParam(value = "after", required = false) Long after,
                                  @RequestParam(value = "before", required = false) Long before,
                                  @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                  @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                  @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                  @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                  @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                  @RequestParam(value = "minRating", required = false) Double minRating,
                                  @RequestParam(value = "maxRating", required = false) Double maxRating,
                                  @RequestParam(value = "order", required = false) ShipOrder order,
                                  @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                  @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        List<Ship> allShips = shipService.getShips(name, planet, shipType,
                after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating);
        List<Ship> sortedShips = shipService.sortShipsByOrder(allShips, order);
        return shipService.getShipsOnPage(sortedShips, pageNumber, pageSize);
    }

    @GetMapping(value = "/rest/ships/count")
    public Integer getShipsCount(@RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "planet", required = false) String planet,
                                 @RequestParam(value = "shipType", required = false) ShipType shipType,
                                 @RequestParam(value = "after", required = false) Long after,
                                 @RequestParam(value = "before", required = false) Long before,
                                 @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                 @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                 @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                 @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                 @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                 @RequestParam(value = "minRating", required = false) Double minRating,
                                 @RequestParam(value = "maxRating", required = false) Double maxRating) {
        return shipService.getShips(name, planet, shipType,
                after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating).size();
    }

    @PostMapping(value = "/rest/ships")
    public ResponseEntity<Ship> addShip(@RequestBody AddOrUpdateShipDtoRequest request) {
        return shipService.validateAndSaveShip(request);
    }

    @GetMapping(value = "/rest/ships/{id}")
    public ResponseEntity<Ship> getShipById(@PathVariable(value = "id") Long shipId) {
        return shipService.getShip(shipId);
    }

    @PostMapping(value = "rest/ships/{id}")
    public ResponseEntity<Ship> updateShip(@PathVariable(value = "id") Long shipId,
                                            @RequestBody AddOrUpdateShipDtoRequest request) {
        return shipService.updateShip(shipId, request);
    }

    @DeleteMapping(value = "/rest/ships/{id}")
    public ResponseEntity<Ship> deleteShip(@PathVariable(value = "id") Long shipId) {
        return shipService.deleteShip(shipId);
    }

}
