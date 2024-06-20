package Models;

public class Employee implements User {
    private int id;
    private String name;
    private String role;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public void setRole(String role) {
        this.role = role;
    }

    public void viewRecommendationMenu() {
        // Implementation
    }

    public void selectNextDayFoodItems() {
        // Implementation
    }

    public void viewFinalizedMenu() {
        // Implementation
    }

    public void giveFeedbackOnFoodItem() {
        // Implementation
    }
}