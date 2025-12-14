package com.yurt.model;

import com.yurt.design.state.IRoomState;
import com.yurt.design.state.AvailableState;

public class Room {
    private String roomId;
    private String roomNumber;
    private int capacity;
    private int currentOccupancy;
    private IRoomState currentState;

    public Room(String roomNumber, int capacity) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.currentOccupancy = 0;
        this.roomId = "R-" + roomNumber;
        this.currentState = new AvailableState(this);
    }

    private Room(RoomBuilder builder) {
        this.roomId = builder.roomId;
        this.roomNumber = builder.roomNumber;
        this.capacity = builder.capacity;
        this.currentOccupancy = builder.currentOccupancy;
        this.currentState = (builder.currentState != null) ? builder.currentState : new AvailableState(this);
    }

    public void setState(IRoomState newState) {
        this.currentState = newState;
    }

    public void addOccupant() {
        currentState.handlePlacement();
    }

    public String getRoomId() { return roomId; }
    public String getRoomNumber() { return roomNumber; }
    public int getCapacity() { return capacity; }
    public int getCurrentOccupancy() { return currentOccupancy; }
    public IRoomState getCurrentState() { return currentState; }

    public void setRoomId(String roomId) { this.roomId = roomId; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }
    public void setCurrentState(IRoomState currentState) {
        this.currentState = currentState;
    }

    @Override
    public String toString() {
        String stateName = (currentState != null) ? currentState.getClass().getSimpleName() : "NOT_INITIALIZED";
        return "Room {roomNumber=" + roomNumber +
                ", capacity=" + capacity +
                ", occupancy=" + currentOccupancy +
                ", state=" + stateName + "}";
    }

    public static class RoomBuilder {
        private String roomId;
        private String roomNumber;
        private int capacity;
        private int currentOccupancy = 0;
        private IRoomState currentState;

        public RoomBuilder() {}

        public RoomBuilder roomId(String roomId) {
            this.roomId = roomId;
            return this;
        }

        public RoomBuilder roomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
            this.roomId = "R-" + roomNumber;
            return this;
        }

        public RoomBuilder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public RoomBuilder currentOccupancy(int currentOccupancy) {
            this.currentOccupancy = currentOccupancy;
            return this;
        }

        public RoomBuilder currentState(IRoomState currentState) {
            this.currentState = currentState;
            return this;
        }

        public Room build() {
            if (this.roomNumber == null || this.capacity <= 0) {
                throw new IllegalStateException("Room Number ve Capacity zorunlu alanlardır.");
            }
            return new Room(this);
        }
    }
}