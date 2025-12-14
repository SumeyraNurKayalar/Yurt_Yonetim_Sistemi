package com.yurt.model;

public class Staff extends User {
    private String department;

    private Staff(StaffBuilder builder) {
        super(builder);
        this.department = builder.department;
    }

    public Staff(long userId, String firstName, String lastName, String tcNumber,
                 String email, String phone, String address, String department, String role) {

        setUserId(userId);
        setFirstName(firstName);
        setLastName(lastName);
        setTcNumber(tcNumber);
        setEmail(email);
        setPhone(phone);
        setAddress(address);
        setRole(role);

        this.department = department;
    }

    public Staff() {}

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public static class StaffBuilder extends UserBuilder<Staff, StaffBuilder> {
        private String department;

        public StaffBuilder() {
        }

        @Override
        protected StaffBuilder self() {
            return this;
        }

        @Override
        public Staff build() {
            return new Staff(this);
        }

        public StaffBuilder department(String department) {
            this.department = department;
            return self();
        }
    }
}