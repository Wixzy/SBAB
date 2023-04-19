import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BusLines {
    public static void main(String[] args) {
        // Set the API endpoint URL
        String endpointUrl = "https://api.sl.se/api2/LineData.json";

        // Set the API parameters
        String apiKey = "5da196d47f8f4e5facdb68d2e25b9eae";
        String model = "JourneyPatternPointOnLine";

        // Build the API request URL
        String requestUrl = String.format("%s?model=%s&key=%s", endpointUrl, model, apiKey);

        StringBuilder response = new StringBuilder();
        // Create a new HTTP connection and set the request method
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the API response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            connection.disconnect();
        } catch (IOException e) {
            System.out.println("An error occurred while calling the SL Stops and Lines 2 API: " + e.getMessage());
            e.printStackTrace();
            System.exit(99);
        }

        // Parse the API response JSON
        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray journeyPatternPointOnLineArray = null;
        try {
            journeyPatternPointOnLineArray = jsonResponse.getJSONArray("ResponseData");
        } catch (JSONException e) {
            System.out.printf("Too many API request has been made. Better luck next time, if you wait long enough...");
            System.exit(99);
        }

        // Count the occurrence of each LineNumber
        Map<String, Integer> lineNumberCountMap = new HashMap<>();
        for (int i = 0; i < journeyPatternPointOnLineArray.length(); i++) {
            JSONObject journeyPatternPointOnLine = journeyPatternPointOnLineArray.getJSONObject(i);
            String lineNumber = journeyPatternPointOnLine.getString("LineNumber");
            lineNumberCountMap.merge(lineNumber, 1, Integer::sum);
        }

        // Sort the LineNumbers by occurrence count in descending order
        List<Map.Entry<String, Integer>> sortedLineNumberCountList = new ArrayList<>(lineNumberCountMap.entrySet());
        sortedLineNumberCountList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Print the top ten LineNumbers by occurrence count
        int numTopLineNumbers = 10;
        System.out.printf("Top %d LineNumbers:\n", numTopLineNumbers);
        for (int i = 0; i < Math.min(sortedLineNumberCountList.size(), numTopLineNumbers); i++) {
            Map.Entry<String, Integer> entry = sortedLineNumberCountList.get(i);
            String lineNumber = entry.getKey();
            int count = entry.getValue();
            System.out.printf("%s: %d\n", lineNumber, count);
        }
        System.exit(0);
    }
}

