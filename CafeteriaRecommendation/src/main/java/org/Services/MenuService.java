package org.Services;

import org.Database.IMenuManagementDatabase;
import org.Database.IFeedbackDatabase;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class MenuService {
    private final IMenuManagementDatabase menuDatabase;
    private final IFeedbackDatabase feedbackDatabase;

    public MenuService(IMenuManagementDatabase menuDatabase, IFeedbackDatabase feedbackDatabase) {
        this.menuDatabase = menuDatabase;
        this.feedbackDatabase = feedbackDatabase;
    }

    public void processMenuCreation(PrintWriter outputWriter, String[] menuItemData) {
        try {
            if (menuItemData.length < 5) {
                outputWriter.println("Invalid input for creating menu item.");
                return;
            }

            String itemName = menuItemData[1];
            double itemPrice = Double.parseDouble(menuItemData[2]);
            int mealTypeId = Integer.parseInt(menuItemData[3]);
            int itemAvailability = Integer.parseInt(menuItemData[4]);

            menuDatabase.createMenuItem(itemName, itemPrice, mealTypeId, itemAvailability);
            outputWriter.println("Menu item created successfully.");
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while creating menu item: " + runtimeException.getMessage());
        }
    }


    public void processMenuItemUpdate(PrintWriter outputWriter, String[] menuItemData) {
        try {
            if (menuItemData.length < 5) {
                outputWriter.println("Invalid input for updating menu item.");
                return;
            }

            int menuItemId = Integer.parseInt(menuItemData[1]);
            String updatedName = menuItemData[2];
            double updatedPrice = Double.parseDouble(menuItemData[3]);
            int updatedAvailability = Integer.parseInt(menuItemData[4]);

            menuDatabase.updateMenuItem(menuItemId, updatedName, updatedPrice, updatedAvailability);
            outputWriter.println("Menu item updated successfully.");
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while updating menu item: " + runtimeException.getMessage());
        }
    }


    public void processMenuItemDeletion(PrintWriter outputWriter, String[] menuItemData) {
        try {
            if (menuItemData.length < 2) {
                outputWriter.println("Invalid input for deleting menu item.");
                return;
            }

            int menuItemId = Integer.parseInt(menuItemData[1]);
            boolean isDeleted = menuDatabase.deleteMenuItem(menuItemId);
            if (isDeleted) {
                outputWriter.println("Menu item deleted successfully.");
            } else {
                outputWriter.println("Failed to delete menu item.");
            }
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while deleting menu item: " + runtimeException.getMessage());
        }
    }


    public void viewMenu(PrintWriter outputWriter) {
        try {
            List<String> menuItems = menuDatabase.getMenuItems();
            printMenu(outputWriter, menuItems, "----- Cafeteria Menu -----");
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while viewing menu: " + runtimeException.getMessage());
        }
    }


    public void generateRecommendationMenu(PrintWriter outputWriter) {
        try {
            List<String> recommendedMenuItems = menuDatabase.generateRecommendedMenu();
            if (recommendedMenuItems.isEmpty()) {
                outputWriter.println("No menu items available.");
            } else if (Objects.equals(recommendedMenuItems.get(0), "Recommendation Menu already generated.")) {
                outputWriter.println("Recommendation Menu already generated.");
            } else {
                outputWriter.println("----- Recommended Cafeteria Menu -----");
                outputWriter.println(String.format("%-15s %-20s %-10s %-20s %-15s %-15s", "Id", "Item", "Price", "Meal Type", "Avg Rating", "Avg Comment"));
                outputWriter.println("-----------------------------------------------------------------");
                for (String menuItem : recommendedMenuItems) {
                    String[] menuItemData = menuItem.split("\\^");
                    if (menuItemData.length >= 6) {
                        outputWriter.println(String.format("%-15s %-20s %-10s %-20s %-15s %-15s",
                                menuItemData[0], menuItemData[1], menuItemData[2], menuItemData[3], menuItemData[4], menuItemData[5]));
                    } else {
                        outputWriter.println("Invalid format for menu item: " + menuItem);
                    }
                }
                outputWriter.println("-----------------------------------------------------------------");
            }
            outputWriter.println("END");
        } catch (SQLException sqlException) {
            outputWriter.println("Database error while generating recommendation menu: " + sqlException.getMessage());
        } catch (IOException ioException) {
            outputWriter.println("I/O error while generating recommendation menu: " + ioException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while generating recommendation menu: " + runtimeException.getMessage());
        }
    }



    public void viewDiscardMenuItems(PrintWriter outputWriter) {
        try {
            List<String> discardMenuItems = menuDatabase.generateDiscardMenuItems();
            if (discardMenuItems.isEmpty()) {
                outputWriter.println("No menu items available.");
            } else {
                outputWriter.println("----- Discard Cafeteria Menu Items -----");
                outputWriter.println(String.format("%-15s %-20s %-10s %-20s %-15s", "Id", "Name", "Price", "Availability", "Meal Type"));
                outputWriter.println("-----------------------------------------------------------------");
                for (String menuItem : discardMenuItems) {
                    String[] menuItemData = menuItem.split("\\^");
                    if (menuItemData.length >= 5) {
                        outputWriter.println(String.format("%-15s %-20s %-10s %-20s %-15s",
                                menuItemData[0], menuItemData[1], menuItemData[2], menuItemData[3], menuItemData[4]));
                    } else {
                        outputWriter.println("Invalid format for menu item: " + menuItem);
                    }
                }
                outputWriter.println("-----------------------------------------------------------------");
            }
            outputWriter.println("END");
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while viewing discard menu items: " + runtimeException.getMessage());
        }
    }


    public void sendImprovementQuestionsForDiscardFoodItems(PrintWriter outputWriter) {
        boolean isSendSuccess = menuDatabase.updateStatusOfDiscardItem();
        if (isSendSuccess) {
            outputWriter.println("Successfully sent the Improvement Questions to Employees");
        } else {
            outputWriter.println("There was an issue while sending the Improvement Questions");
        }
    }


    public void getDiscardFoodItemIds(PrintWriter outputWriter) throws SQLException {
        String discardFoodItemIds = menuDatabase.getDiscardFoodItemIds();
        outputWriter.println(discardFoodItemIds);
    }

    public void rolloutRecommendationMenu(PrintWriter outputWriter) {
        try {
            int rolloutStatus = menuDatabase.rolloutRecommendation();
            outputWriter.println(rolloutStatus == 1 ? "Roll out successful" : "Roll out encountered issues");
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while rolling out recommendation menu: " + runtimeException.getMessage());
        }
    }


    public void generateFinalizedMenu(PrintWriter outputWriter, String[] menuItemData) {
        try {
            if (menuItemData.length < 4) {
                outputWriter.println("Invalid input for generating finalized menu.");
                return;
            }

            int breakfastMenuItemId = Integer.parseInt(menuItemData[1]);
            int lunchMenuItemId = Integer.parseInt(menuItemData[2]);
            int dinnerMenuItemId = Integer.parseInt(menuItemData[3]);

            List<String> finalizedMenu = menuDatabase.getFinalizedMenu(breakfastMenuItemId, lunchMenuItemId, dinnerMenuItemId);
            printMenu(outputWriter, finalizedMenu, "----- Finalized Cafeteria Menu -----");
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());
        } catch (SQLException sqlException) {
            outputWriter.println("Database error while generating finalized menu: " + sqlException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while generating finalized menu: " + runtimeException.getMessage());
        }
    }


    public void rolloutFinalizedMenu(PrintWriter outputWriter) {
        try {
            int rolloutStatus = menuDatabase.rolloutFinalizedMenusStatusUpdate();
            outputWriter.println(rolloutStatus == 1 ? "Roll out successful" : "Roll out encountered issues");
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while rolling out finalized menu: " + runtimeException.getMessage());
        }
    }


    public void viewRecommendationMenu(PrintWriter outputWriter, String[] menuItemData) {
        try {
            int userId = Integer.parseInt(menuItemData[1]);
            List<String> recommendedMenu = menuDatabase.getRecommendedMenu(userId);
            if (recommendedMenu.isEmpty()) {
                outputWriter.println("No menu items available.");
            } else {
                outputWriter.println("----- Recommended Cafeteria Menu -----");
                outputWriter.println(String.format("%-15s %-20s %-10s %-20s %-15s %-15s", "Id", "Item", "Price", "Meal Type", "Avg Rating", "Avg Comment"));
                outputWriter.println("-----------------------------------------------------------------");
                for (String menuItem : recommendedMenu) {
                    outputWriter.println(menuItem);
                }
                outputWriter.println("-----------------------------------------------------------------");
            }
            outputWriter.println("END");
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());
        } catch (SQLException sqlException) {
            outputWriter.println("Database error while viewing recommendation menu: " + sqlException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while viewing recommendation menu: " + runtimeException.getMessage());
        }
    }


    public void selectNextDayFoodItems(PrintWriter outputWriter, String[] menuItemData) {
        try {

            int breakfastMenuItemId = Integer.parseInt(menuItemData[1]);
            int lunchMenuItemId = Integer.parseInt(menuItemData[2]);
            int dinnerMenuItemId = Integer.parseInt(menuItemData[3]);
            int userId = Integer.parseInt(menuItemData[4]);

            List<Integer> ids = new ArrayList<>(Arrays.asList(breakfastMenuItemId, lunchMenuItemId, dinnerMenuItemId, userId));
            int isSelected = feedbackDatabase.insertSelectedFoodItemsInDB(ids);
            outputWriter.println(isSelected == 1 ? "Selected food items successfully" : "Selected food items have some issues");
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while selecting next day food items: " + runtimeException.getMessage());
        }
    }


    public void viewSelectedFoodItemsEmployees(PrintWriter outputWriter) {
        try {
            List<String> selectedFoodItemsByEmployees = feedbackDatabase.getSelectedFoodItemsEmployees();
            printMenu(outputWriter, selectedFoodItemsByEmployees, "----- Selected Food Items By Employees -----");
        } catch (SQLException sqlException) {
            outputWriter.println("Database error while viewing selected food items by employees: " + sqlException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while viewing selected food items by employees: " + runtimeException.getMessage());
        }
    }


    public void viewFinalizedMenu(PrintWriter outputWriter) {
        try {
            List<String> finalizedMenu = menuDatabase.getFinalizedMenu();
            printMenu(outputWriter, finalizedMenu, "----- Finalized Menu -----");
        } catch (SQLException sqlException) {
            outputWriter.println("Database error while viewing finalized menu: " + sqlException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while viewing finalized menu: " + runtimeException.getMessage());
        }
    }


    private void printMenu(PrintWriter outputWriter, List<String> menuItems, String header) {
        try {
            if (menuItems.isEmpty()) {
                outputWriter.println("No menu items available.");
            } else {
                outputWriter.println(header);
                outputWriter.println(String.format("%-15s %-20s %-10s %-15s %-15s", "Id", "Item", "Price", "Meal ID", "IsAvailable"));
                outputWriter.println("--------------------------------------------");
                for (String menuItem : menuItems) {
                    outputWriter.println(menuItem);
                }
                outputWriter.println("--------------------------------------------");
            }
            outputWriter.println("END");
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while printing menu: " + runtimeException.getMessage());
        }
    }



    public void fillEmployeeImprovementAnswers(BufferedReader inputReader, PrintWriter outputWriter, String[] employeeItemData) {
        try {
            int foodItemId = Integer.parseInt(employeeItemData[1]);
            int userId = Integer.parseInt(employeeItemData[2]);
            String answer1 = employeeItemData[3];
            String answer2 = employeeItemData[4];
            String answer3 = employeeItemData[5];

            menuDatabase.getImprovementQuestionsandAnswers();

        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());

        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while inserting Improvement Questions and Answers: " + runtimeException.getMessage());
            // Handle or log the exception as per your project's requirements
        }
    }


    public void viewImprovementQuestionAndAnswers(BufferedReader inputReader, PrintWriter outputWriter) {
        try {
            List<String> improvementQuestionAnswers = menuDatabase.getImprovementQuestionsandAnswers();
            if (improvementQuestionAnswers.isEmpty()) {
                outputWriter.println("No improvement questions and answers available.");
            } else {
                outputWriter.println("----- Improvement Questions and Answers -----");
                outputWriter.println(String.format("%-50s %-50s %-50s %-50s %-50s", "FoodItemId", "UserId", "What didn't you like about Food?", "How would you like Food?", "Share your mom's recipe for Food?"));
                outputWriter.println("-----------------------------------------------------------------");
                for (String questionAnswer : improvementQuestionAnswers) {
                    outputWriter.println(questionAnswer);
                }
                outputWriter.println("-----------------------------------------------------------------");
            }
            outputWriter.println("END");
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while viewing Improvement Questions and Answers: " + runtimeException.getMessage());
        }
    }



    public void makeEmployeeProfile(BufferedReader inputReader, PrintWriter outputWriter, String[] employeeItemData) {
        try {
            menuDatabase.makeEmployeeProfile(inputReader, outputWriter, employeeItemData);
            outputWriter.println("Employee Profile added successfully");

        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while making Employee Profile: " + runtimeException.getMessage());
        }
    }

}
