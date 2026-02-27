package org.vaadin.addons.dramafinder.tests.testuis;

/**
 * Mutable test-data model for Grid Pro views.
 * Uses a plain class (not a record) because Grid Pro edit columns require setters.
 */
public class GridProPerson {

    private String firstName;
    private String lastName;
    private boolean active;
    private String department;

    public GridProPerson(String firstName, String lastName, boolean active, String department) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
        this.department = department;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
