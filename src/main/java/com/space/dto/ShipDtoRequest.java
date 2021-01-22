package com.space.dto;

import com.space.controller.ShipOrder;
import com.space.model.ShipType;

public class ShipDtoRequest {

    private String name;
    private String planet;
    private ShipType shipType;
    private Long after;
    private Long before;
    private Boolean isUsed;
    private Double minSpeed;
    private Double maxSpeed;
    private Integer minCrewSize;
    private Integer maxCrewSize;
    private Double minRating;
    private Double maxRating;
    private ShipOrder order;
    private Integer pageNumber;
    private Integer pageSize;

    public ShipDtoRequest() {
    }

    public ShipDtoRequest(String name, String planet, ShipType shipType,
                          Long after, Long before, Boolean isUsed,
                          Double minSpeed, Double maxSpeed, Integer minCrewSize,
                          Integer maxCrewSize, Double minRating, Double maxRating,
                          ShipOrder order, Integer pageNumber, Integer pageSize) {
        this.name = name;
        this.planet = planet;
        this.shipType = shipType;
        this.after = after;
        this.before = before;
        this.isUsed = isUsed;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.minCrewSize = minCrewSize;
        this.maxCrewSize = maxCrewSize;
        this.minRating = minRating;
        this.maxRating = maxRating;
        this.order = order;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public String getName() {
        return name;
    }

    public String getPlanet() {
        return planet;
    }

    public ShipType getShipType() {
        return shipType;
    }

    public Long getAfter() {
        return after;
    }

    public Long getBefore() {
        return before;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public Double getMinSpeed() {
        return minSpeed;
    }

    public Double getMaxSpeed() {
        return maxSpeed;
    }

    public Integer getMinCrewSize() {
        return minCrewSize;
    }

    public Integer getMaxCrewSize() {
        return maxCrewSize;
    }

    public Double getMinRating() {
        return minRating;
    }

    public Double getMaxRating() {
        return maxRating;
    }

    public ShipOrder getOrder() {
        return order;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }
}
