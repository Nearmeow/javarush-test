package com.space.service;

import com.space.controller.ShipOrder;
import com.space.dto.AddOrUpdateShipDtoRequest;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        Double rating = 80 * speed * k / (3019 - prodYear + 1);
        return Math.round(rating * 100) / 100D;
    }

    public Ship saveShip(Ship ship) {
        return shipRepository.save(ship);
    }

    public ResponseEntity<Ship> validateAndSaveShip(AddOrUpdateShipDtoRequest request) {

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

    public ResponseEntity<Ship> getShip(Long id) {
        if (id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Ship ship = shipRepository.findById(id).orElse(null);
        if (ship == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    public ResponseEntity<Ship> updateShip(Long id, AddOrUpdateShipDtoRequest request) {
        if (id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ResponseEntity<Ship> entityShip = getShip(id);
        Ship ship = entityShip.getBody();
        if (ship == null) {
            return entityShip;
        }

        ResponseEntity<Ship> badEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String newName = request.getName();
        if (newName != null) {
            if (request.checkString(newName)) {
                ship.setName(newName);
            } else return badEntity;
        }

        String newPlanet = request.getPlanet();
        if (newPlanet != null) {
            if (request.checkString(newPlanet)) {
                ship.setPlanet(newPlanet);
            } else return badEntity;
        }

        ShipType newType = request.getShipType();
        if (newType != null) {
            ship.setShipType(newType);
        }

        if (request.getProdDate() != null) {
            Date newDate = new Date(request.getProdDate());
            if (isDateValid(newDate)) {
                ship.setProdDate(newDate);
            } else return badEntity;
        }

        if (request.getUsed() != null) {
            ship.setUsed(request.getUsed());
        }

        Double newSpeed = request.getSpeed();
        if (newSpeed != null) {
            if (request.checkSpeed(newSpeed)) {
                ship.setSpeed(newSpeed);
            } else return badEntity;
        }

        Integer newCrewSize = request.getCrewSize();
        if (newCrewSize != null) {
            if (request.checkCrewSize(newCrewSize)) {
                ship.setCrewSize(newCrewSize);
            } else return badEntity;
        }

        double newRating = calculateRating(ship.getSpeed(), ship.getUsed(), ship.getProdDate());
        ship.setRating(newRating);

        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    private boolean isDateValid(Date prodDate) {
        final Date startProd = getDateForYear(2800);
        final Date endProd = getDateForYear(3019);
        return prodDate != null && prodDate.after(startProd) && prodDate.before(endProd);
    }

    private Date getDateForYear(int year) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

}
