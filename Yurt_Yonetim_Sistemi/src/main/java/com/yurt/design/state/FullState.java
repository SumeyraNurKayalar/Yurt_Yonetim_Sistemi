package com.yurt.design.state;

import com.yurt.model.Room;

public class FullState implements IRoomState {
    private final Room room;

    public FullState(Room room) {
        this.room = room;
    }

    @Override
    public void handlePlacement() {
        System.err.println("UYARI: Oda " + room.getRoomNumber() + " DOLU. Yerleştirme İPTAL edildi.");
    }

    @Override
    public void handleVacate() {
        room.setCurrentOccupancy(room.getCurrentOccupancy() - 1);
        System.out.println("LOG: Odadan bir kişi ayrıldı. Yeni doluluk: " + room.getCurrentOccupancy());

        room.setState(new AvailableState(room));
    }
}