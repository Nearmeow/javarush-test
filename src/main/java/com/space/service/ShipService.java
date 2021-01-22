package com.space.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.space.controller.ShipOrder;
import com.space.dto.AddShipDtoRequest;
import com.space.dto.ShipDtoRequest;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ShipService {

    private ShipRepository shipRepository;

    @Autowired
    public ShipService(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    public List<Ship> getShips(String name, String planet, ShipType shipType,
                               Long after, Long before, Boolean isUsed, Double minSpeed,
                               Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                               Double minRating, Double maxRating
    ) {

        Date afterDate = after == null ? null : new Date(after);
        Date beforeDate = before == null ? null : new Date(before);

        List<Ship> list = new ArrayList<>();
        //List<Ship> allShips = shipRepository.findAll();

        /*for (Ship ship : allShips) {
            if (name != null && !ship.getName().contains(name)) {
                break;
            }
            if (planet != null && !ship.getPlanet().contains(planet)) {
                break;
            }
            if (shipType != null && ship.getShipType() != shipType) {
                break;
            }
            if (afterDate != null && ship.getProdDate().before(afterDate)) {
                break;
            }
            if (beforeDate != null && ship.getProdDate().after(beforeDate)) {
                break;
            }
            if (isUsed != null && ship.getUsed().booleanValue() != isUsed.booleanValue()) {
                break;
            }
            if (minSpeed != null && ship.getSpeed().compareTo(minSpeed) < 0) {
                break;
            }
            if (maxSpeed != null && ship.getSpeed().compareTo(maxSpeed) < 0) {
                break;
            }
            if (minCrewSize != null && ship.getCrewSize().compareTo(minCrewSize) < 0) {
                break;
            }
            if (maxCrewSize != null && ship.getCrewSize().compareTo(maxCrewSize) > 0) {
                break;
            }
            if (minRating != null && ship.getRating().compareTo(minRating) < 0) {
                break;
            }
            if (maxRating != null && ship.getRating().compareTo(maxRating) > 0) {
                break;
            }
            list.add(ship);
        }*/

        shipRepository.findAll().forEach((ship) -> {
            if (name != null && !ship.getName().contains(name)) return;
            if (planet != null && !ship.getPlanet().contains(planet)) return;
            if (shipType != null && ship.getShipType() != shipType) return;
            if (afterDate != null && ship.getProdDate().before(afterDate)) return;
            if (beforeDate != null && ship.getProdDate().after(beforeDate)) return;
            if (isUsed != null && ship.getUsed().booleanValue() != isUsed.booleanValue()) return;
            if (minSpeed != null && ship.getSpeed().compareTo(minSpeed) < 0) return;
            if (maxSpeed != null && ship.getSpeed().compareTo(maxSpeed) > 0) return;
            if (minCrewSize != null && ship.getCrewSize().compareTo(minCrewSize) < 0) return;
            if (maxCrewSize != null && ship.getCrewSize().compareTo(maxCrewSize) > 0) return;
            if (minRating != null && ship.getRating().compareTo(minRating) < 0) return;
            if (maxRating != null && ship.getRating().compareTo(maxRating) > 0) return;

            list.add(ship);
        });

        return list;
    }

    public List<Ship> sortShipsByOrder(List<Ship> ships, ShipOrder order) {
        if (order != null) {
            ships.sort((ship1, ship2) -> {
                switch (order) {
                    case ID: return ship1.getId().compareTo(ship2.getId());
                    case SPEED: return ship1.getSpeed().compareTo(ship2.getSpeed());
                    case DATE: return ship1.getProdDate().compareTo(ship2.getProdDate());
                    case RATING: return ship1.getRating().compareTo(ship2.getRating());
                    default: return 0;
                }
            });
        }
        return ships;
    }

    public List<Ship> getShipsOnPage(List<Ship> ships, Integer pageNumber, Integer pageSize) {
        final Integer page = pageNumber == null ? 0 : pageNumber;
        final Integer size = pageSize == null ? 3 : pageSize;
        final int from = page * size;
        int to = from + size;
        if (to > ships.size()) to = ships.size();
        return ships.subList(from, to);
    }

    public Double calculateRating(Double speed, boolean isUsed, Date prodDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(prodDate);
        Integer prodYear = calendar.get(Calendar.YEAR);
        Double k = isUsed ? 0.5 : 1;
        return 80 * speed * k / (3019 - prodYear + 1);
    }

    public Ship saveShip(Ship ship) {
        return shipRepository.save(ship);
    }

    public ResponseEntity<Ship> validateAndSaveShip(Ship ship) {
        if (ship == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2800);
        final Date startProd = calendar.getTime();
        calendar.set(Calendar.YEAR, 3019);
        final Date endProd = calendar.getTime();

        ship.setSpeed(ship.getSpeed());

        boolean isValid = checkString(ship.getName()) && checkString(ship.getPlanet()) &&
                ship.getSpeed() >= 0.01 && ship.getSpeed() <= 0.99 &&
                ship.getCrewSize() >= 1 && ship.getCrewSize() <= 9999 &&
                ship.getProdDate().after(startProd) && ship.getProdDate().before(endProd) && ship.getSpeed() != null;

        if (!isValid) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (ship.getUsed() == null) {
            ship.setUsed(false);
        }
        double rating = calculateRating(ship.getSpeed(), ship.getUsed(), ship.getProdDate());
        ship.setRating(rating);
        Ship savedShip = saveShip(ship);
        return new ResponseEntity<>(savedShip, HttpStatus.OK);
    }

    public ResponseEntity<Ship> validateAndSaveShipReq(AddShipDtoRequest request) {

        try {
            if (!request.validate()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (request.getUsed() == null) {
            request.setUsed(false);
        }
        Date prodDate = new Date(request.getProdDate());
        Double rating = calculateRating(request.getSpeed(), request.getUsed(), prodDate);
        Ship ship = new Ship(null, request.getName(), request.getPlanet(),
                request.getShipType(), prodDate,
                request.getUsed(), request.getSpeed(), request.getCrewSize(), rating);
        Ship savedShip = saveShip(ship);

        return new ResponseEntity<>(savedShip, HttpStatus.OK);
    }

    private boolean checkString(String string) {
        return string.length() < 51 && !string.equals("");
    }




}
