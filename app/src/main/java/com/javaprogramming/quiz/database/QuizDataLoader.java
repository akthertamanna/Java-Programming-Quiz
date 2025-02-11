package com.javaprogramming.quiz.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.javaprogramming.quiz.model.Category;
import com.javaprogramming.quiz.model.Quiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizDataLoader {

    private static final String TAG = "QuizDataLoader";

    // Static map to store category quiz counts
    public static HashMap<Integer, Integer> categoryQuestionCountMap = new HashMap<>();

    // Static HashMap to store category names by ID globally
    private static HashMap<Integer, String> categoryNameMap = new HashMap<>();

    // Callback interface to pass categories and quizzes to the calling class (SplashClass or any other class)
    public interface QuizDataCallback {
        void onCategoriesLoaded(List<Category> categories, List<Quiz> quizzes);
    }


    // Load quiz data in the background (using ExecutorService) and return data via the callback
    public static void loadQuizData(Context context, QuizDataCallback callback) {
        // Create a single-threaded executor to perform the task in background
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Execute background task
        executorService.execute(() -> {
            // Load JSON data from assets
            String json = loadJSONFromAsset(context, "quizdata.json");

            if (json != null) {
                // Parse JSON data
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
                JsonObject resultObject = jsonObject.getAsJsonObject("result");

                // Parse categories and quizzes
                List<Category> categories = gson.fromJson(resultObject.getAsJsonArray("categories"), new TypeToken<List<Category>>(){}.getType());
                List<Quiz> quizzes = parseQuizzes(resultObject.getAsJsonArray("quizzes"));

                // Calculate the total number of questions for each category
                //calculateCategoryQuestionCounts(categories, quizzes);

                // Log data (for debugging purposes)
                Log.d(TAG, "Categories: " + categories);
                Log.d(TAG, "Quizzes: " + quizzes);

                // If the callback is provided, pass the data
                if (callback != null) {
                    callback.onCategoriesLoaded(categories, quizzes);
                }
            }
        });
    }

    // Method to load JSON from the assets folder
    private static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            // Open the JSON file
            InputStreamReader is = new InputStreamReader(context.getAssets().open(fileName));
            BufferedReader reader = new BufferedReader(is);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            json = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    // Method to parse quizzes with or without dynamic keys
    private static List<Quiz> parseQuizzes(JsonArray quizzesArray) {
        List<Quiz> quizzes = new ArrayList<>();

        for (JsonElement element : quizzesArray) {
            if (element.isJsonObject()) {
                JsonObject quizObject = element.getAsJsonObject();

                // Check if the quiz object contains a dynamic key (e.g., "1": {...})
                if (quizObject.entrySet().size() == 1 && quizObject.entrySet().iterator().next().getKey().matches("\\d+")) {
                    // Dynamic key present, use Map<String, Quiz> to store this quiz
                    String dynamicKey = quizObject.entrySet().iterator().next().getKey();
                    JsonObject quizDetails = quizObject.entrySet().iterator().next().getValue().getAsJsonObject();
                    Quiz quiz = new Gson().fromJson(quizDetails, Quiz.class);
                    quizzes.add(quiz);
                } else {
                    // No dynamic key, this is a regular Quiz object
                    Quiz quiz = new Gson().fromJson(quizObject, Quiz.class);
                    quizzes.add(quiz);
                }
            }
        }

        return quizzes;
    }

    private static HashMap<Integer, Integer> calculateCategoryQuestionCounts(List<Category> categories, List<Quiz> quizzes) {
        // Map to store the count of quizzes for each category

        // Clear the map before updating
        categoryQuestionCountMap.clear();

        categoryQuestionCountMap = new HashMap<>();

        // Iterate over the quizzes and update the count in the map
        for (Quiz quiz : quizzes) {
            // Increment the count for the category of this quiz
            int categoryID = quiz.getCategoryID_Q();
            categoryQuestionCountMap.put(categoryID, categoryQuestionCountMap.getOrDefault(categoryID, 0) + 1);
        }

//        // If you want to include only categories that have quizzes
//        HashMap<Integer, Integer> filteredCategoryCountMap = new HashMap<>();
//        for (Category category : categories) {
//            int categoryID = category.getCategoryID();
//            if (categoryQuestionCountMap.containsKey(categoryID)) {
//                filteredCategoryCountMap.put(categoryID, categoryQuestionCountMap.get(categoryID));
//            } else {
//                filteredCategoryCountMap.put(categoryID, 0); // In case there are categories with no quizzes
//            }
//        }

        return categoryQuestionCountMap;
    }




}
