package models;

import enums.UserRole;

public class Staff extends User {
    private String staffId;
    private String position;

    public Staff(String userId, String username, String password, String staffId, String position) {
        super(userId, username, password, "Staff Member", "staff@university.edu", String.valueOf(UserRole.STAFF));
        this.staffId = staffId;
        this.position = position;
    }

    public Staff(String userId, String username, String password, String name, String email, String staffId, String position) {
        super(userId, username, password, name, email, String.valueOf(UserRole.STAFF));
        this.staffId = staffId;
        this.position = position;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}