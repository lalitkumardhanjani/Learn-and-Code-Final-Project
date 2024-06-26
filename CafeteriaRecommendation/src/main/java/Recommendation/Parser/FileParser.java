package Recommendation.Parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileParser {
    public static List<String> parseCSVFile(String filePath ) {

        List<String> strings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                for (String part : parts) {
                    strings.add(part.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings;
    }

    public static void main(String[] args) {
        String filePath = "C:\\Users\\lalit.k.dhanjani\\IdeaProjects\\CafeteriaRecommendation\\src\\main\\java\\Recommendation\\FeederWords\\Postive.txt";
        List<String> parsedStrings = parseCSVFile(filePath);
        System.out.println(parsedStrings);
    }
}

