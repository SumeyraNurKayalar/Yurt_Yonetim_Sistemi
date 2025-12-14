package com.yurt.design.state;

import com.yurt.model.Room;

public class AvailableState implements IRoomState {

    private final Room room;

    public AvailableState(Room room) {
        this.room = room;
    }

    @Override
    public void handlePlacement() {
        if (room.getCurrentOccupancy() < room.getCapacity()) {
            room.setCurrentOccupancy(room.getCurrentOccupancy() + 1);

            System.out.println("STATE LOG: Oda " + room.getRoomNumber() + " başarıyla yerleştirildi. Yeni doluluk: " + room.getCurrentOccupancy());

            if (room.getCurrentOccupancy() == room.getCapacity()) {
                System.out.println("STATE LOG: Oda doldu. AvailableState -> FullState geçişi.");
                room.setState(new FullState(room));
            }
        } else {
            System.err.println("STATE HATA: Oda dolu olmasına rağmen AvailableState'te kalmış.");
        }
    }

    @Override
    public void handleVacate() {

    }
}