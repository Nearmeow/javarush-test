package com.space.dto;

import com.space.model.ShipType;

public class AddShipDtoRequest {

    private String name;
    private String planet;
    private ShipType shipType;
    private Long prodDate;
    private Boolean isUsed;
    private Double speed;
    private Integer crewSize;

    public AddShipDtoRequest() {
    }

    public AddShipDtoRequest(String name, String planet, ShipType shipType,
                             Long prodDate, Boolean isUsed, Double speed, Integer crewSize) {
        this.name = name;
        this.planet = planet;
        this.shipType = shipType;
        this.prodDate = prodDate;
        this.isUsed = isUsed;
        this.speed = speed;
        this.crewSize = crewSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanet() {
        return planet;
    }

    public void setPlanet(String planet) {
        this.planet = planet;
    }

    public ShipType getShipType() {
        return shipType;
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    public Long getProdDate() {
        return prodDate;
    }

    public void setProdDate(Long prodDate) {
        this.prodDate = prodDate;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getCrewSize() {
        return crewSize;
    }

    public void setCrewSize(Integer crewSize) {
        this.crewSize = crewSize;
    }

    public boolean validate() {
        return checkString(name) && checkString(planet) &&
                shipType != null && speed != null && prodDate > 0 && crewSize != null &&
                crewSize >= 1 && crewSize <= 9999 && speed >= 0.01 && speed <= 0.99;
    }



    private boolean checkString(String string) {
        return string.length() < 51 && !string.equals("");
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
