package com.yurt.model;

public class Student extends User {
    private String currentRoomNumber;
    private int roomId;

    private Student(StudentBuilder builder) {
        super(builder);
        this.currentRoomNumber = builder.currentRoomNumber;
        this.roomId = builder.roomId;
    }

    public Student(long userId, String firstName, String lastName, String tcNumber,
                   String email, String phone, String address,
                   String currentRoomNumber, int roomId) {

        setUserId(userId);
        setFirstName(firstName);
        setLastName(lastName);
        setTcNumber(tcNumber);
        setEmail(email);
        setPhone(phone);
        setAddress(address);
        setRole("Ogrenci");

        this.currentRoomNumber = currentRoomNumber;
        this.roomId = roomId;
    }

    public Student() {
        setRole("Ogrenci");
    }

    public String getCurrentRoomNumber() { return currentRoomNumber; }
    public int getRoomId() { return roomId; }
    public void setCurrentRoomNumber(String currentRoomNumber) { this.currentRoomNumber = currentRoomNumber; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public static class StudentBuilder extends UserBuilder<Student, StudentBuilder> {
        private String currentRoomNumber = "Yok";
        private int roomId = 0;

        public StudentBuilder() {
            role("Ogrenci");
        }

        @Override
        protected StudentBuilder self() {
            return this;
        }

        @Override
        public Student build() {
            return new Student(this);
        }

        public StudentBuilder currentRoomNumber(String currentRoomNumber) {
            this.currentRoomNumber = currentRoomNumber;
            return self();
        }

        public StudentBuilder roomId(int roomId) {
            this.roomId = roomId;
            return self();
        }
    }
}