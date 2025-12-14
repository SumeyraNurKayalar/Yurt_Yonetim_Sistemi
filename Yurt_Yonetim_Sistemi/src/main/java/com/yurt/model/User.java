package com.yurt.model;

public abstract class User {
    private long userId;
    private String firstName;
    private String lastName;
    private String tcNumber;
    private String email;
    private String phone;
    private String address;
    private String role;
    private String passwordHash;

    public User() {}

    protected User(UserBuilder<?, ?> builder) {
        this.userId = builder.userId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.tcNumber = builder.tcNumber;
        this.email = builder.email;
        this.phone = builder.phone;
        this.address = builder.address;
        this.role = builder.role;
        this.passwordHash = builder.passwordHash;
    }

    public long getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getTcNumber() { return tcNumber; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getRole() { return role; }
    public String getPasswordHash() { return passwordHash; }

    public void setUserId(long userId) { this.userId = userId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setTcNumber(String tcNumber) { this.tcNumber = tcNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setRole(String role) { this.role = role; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public abstract static class UserBuilder<T extends User, B extends UserBuilder<T, B>> {
        private long userId;
        private String firstName;
        private String lastName;
        private String tcNumber;
        private String email;
        private String phone;
        private String address;
        private String role;
        private String passwordHash;

        protected abstract B self();
        public abstract T build();

        public B userId(long userId) {
            this.userId = userId;
            return self();
        }
        public B firstName(String firstName) {
            this.firstName = firstName;
            return self();
        }
        public B lastName(String lastName) {
            this.lastName = lastName;
            return self();
        }
        public B tcNumber(String tcNumber) {
            this.tcNumber = tcNumber;
            return self();
        }
        public B email(String email) {
            this.email = email;
            return self();
        }
        public B phone(String phone) {
            this.phone = phone;
            return self();
        }
        public B address(String address) {
            this.address = address;
            return self();
        }
        public B role(String role) {
            this.role = role;
            return self();
        }
        public B passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return self();
        }
    }
}