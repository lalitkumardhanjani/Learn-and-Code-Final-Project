package org.Database;

import java.sql.SQLException;
import java.util.List;

public interface IFeedbackDatabase {
    int giveFoodFeedback(int foodItemId, int rating, String comment, int userId);
    List<String> getFoodFeedbackHistory() throws SQLException;
    int insertSelectedFoodItemsInDB(List<Integer> ids);

    List<String> getSelectedFoodItemsEmployees() throws SQLException;
}
