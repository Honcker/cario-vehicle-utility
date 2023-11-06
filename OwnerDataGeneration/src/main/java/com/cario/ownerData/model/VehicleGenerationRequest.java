package com.cario.ownerData.model;

public class VehicleGenerationRequest {
    private String owner;
    private int vehicles;
    public String getOwner() {
        return owner;
    }
    private void setOwner(String owner) {
        this.owner = owner;
    }
    public int getVehicles() {
        return vehicles;
    }
    private void setVehicles(int vehicles) {
        this.vehicles = vehicles;
    }
}