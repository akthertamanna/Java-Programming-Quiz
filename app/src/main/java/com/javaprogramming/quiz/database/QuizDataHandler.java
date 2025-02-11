package com.javaprogramming.quiz.database;

import android.content.Context;
import com.javaprogramming.quiz.utilities.Helper;
import com.javaprogramming.quiz.interfaces.dao.QuizHistoryDao;
import com.javaprogramming.quiz.model.Category;
import com.javaprogramming.quiz.model.QuizHistory;
import com.javaprogramming.quiz.model.Quiz;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class QuizDataHandler {
    // Static cache to store quiz data once fetched
    private static ArrayList<Quiz> cachedQuiz = null;

    // Static cache to store category data once fetched
    private static ArrayList<Category> cachedCategory = null;

    // Static HashMap to store category names by ID globally
    private static HashMap<Integer, String> categoryNameMap = new HashMap<>();

    // Fetch Categories (this will only happen once)
    public static void fetchCategories(String categoryUrl, Context context) {
        // Avoid re-fetching if data is already cached
        if (cachedCategory == null) {
            cachedCategory = new ArrayList<>();
            try {
                // Get the JSON data from the helper method
                String jsonData = Helper.getJsonData(categoryUrl, context);
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONObject resultObject = jsonObject.getJSONObject("result");


                // Check if the "categories" key exists and handle both object and array cases
                if (resultObject.has("categories")) {
                    Object categories = resultObject.get("categories");


                    if (categories instanceof JSONObject) {
                        // If it's a JSONObject (like "1": {...}, "2": {...})
                        JSONObject categoriesObject = (JSONObject) categories;
                        Iterator<String> keys = categoriesObject.keys();
                        while (keys.hasNext()) {
                            String key = keys.next(); // Key is like "1", "2", etc.
                            JSONObject jsonCategory = categoriesObject.getJSONObject(key);


                            // Dynamically parse each field using opt* methods for safety
                            String categoryTitle = jsonCategory.optString("categoryTitle", "Unknown Category");
                            int categoryID = jsonCategory.optInt("categoryID", -1);
                            int levelCompleted = jsonCategory.optInt("levelCompleted", 0);


                            // Add CategoryList object to the cache
                            cachedCategory.add(new Category(
                                    categoryID,
                                    categoryTitle,
                                    levelCompleted
                            ));


                            // Add the category ID and name to the global HashMap
                            categoryNameMap.put(categoryID, categoryTitle);
                        }


                    } else if (categories instanceof JSONArray) {
                        // If it's a JSONArray (like [{...}, {...}])
                        JSONArray categoryArray = (JSONArray) categories;


                        // Loop through the array and parse each category
                        for (int i = 0; i < categoryArray.length(); i++) {
                            JSONObject jsonCategory = categoryArray.getJSONObject(i);


                            // Dynamically parse each field using opt* methods for safety
                            String categoryTitle = jsonCategory.optString("categoryTitle", "Unknown Category");
                            int categoryID = jsonCategory.optInt("categoryID", -1);
                            int levelCompleted = jsonCategory.optInt("levelCompleted", 0);


                            // Add CategoryList object to the cache
                            cachedCategory.add(new Category(
                                    categoryID,
                                    categoryTitle,
                                    levelCompleted
                            ));

                            // Add the category ID and name to the global HashMap
                            categoryNameMap.put(categoryID, categoryTitle);
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    // Fetch quizzes from the file (this will only happen once)
    public static void fetchAndCacheQuizzes(String quizUrl, Context context) {
        // Avoid re-fetching if data is already cached
        if (cachedQuiz != null) {
            return; // Data is already cached
        }

        cachedQuiz = new ArrayList<>();
        try {
            // Get the JSON data from the helper method
            String jsonData = Helper.getJsonData(quizUrl, context);
            JSONObject jsonObjectGetQuestions = new JSONObject(jsonData);
            JSONObject resultObject = jsonObjectGetQuestions.getJSONObject("result");

            // Check if the "quizzes" key exists and handle both object and array cases
            if (resultObject.has("quizzes")) {
                Object quizzes = resultObject.get("quizzes");

                if (quizzes instanceof JSONObject) {
                    // If it's a JSONObject (e.g., "393": {...}, "394": {...})
                    JSONObject quizzesObject = (JSONObject) quizzes;
                    Iterator<String> keys = quizzesObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next(); // Key is like "393"
                        JSONObject jsonObject = quizzesObject.getJSONObject(key);

                        // Dynamically parse the fields using opt* methods for safety
                        int questionID = jsonObject.optInt("questionID");
                        int categoryID = jsonObject.optInt("categoryID");
                        String question = jsonObject.optString("question");
                        String optionA = jsonObject.optString("optionA");
                        String optionB = jsonObject.optString("optionB");
                        String optionC = jsonObject.optString("optionC");
                        String optionD = jsonObject.optString("optionD");
                        String hintTxt = jsonObject.optString("hint");

                        byte answer = (byte) jsonObject.optInt("answer");

                        // Create a new QuizList object and add it to the cache
                        cachedQuiz.add(new Quiz(
                                questionID,
                                categoryID,
                                question,
                                optionA,
                                optionB,
                                optionC,
                                optionD,
                                answer,
                                hintTxt
                        ));
                    }

                } else if (quizzes instanceof JSONArray) {
                    // If it's a JSONArray (e.g., [{...}, {...}])
                    JSONArray quizzesArray = (JSONArray) quizzes;


                    // Loop through the array and parse each quiz
                    for (int i = 0; i < quizzesArray.length(); i++) {
                        JSONObject jsonObject = quizzesArray.getJSONObject(i);

                        // Dynamically parse the fields using opt* methods for safety
                        int questionID = jsonObject.optInt("questionID");
                        int categoryID = jsonObject.optInt("categoryID");
                        String question = jsonObject.optString("question");
                        String optionA = jsonObject.optString("optionA");
                        String optionB = jsonObject.optString("optionB");
                        String optionC = jsonObject.optString("optionC");
                        String optionD = jsonObject.optString("optionD");
                        String hintTxt = jsonObject.optString("hint");

                        byte answer = (byte) jsonObject.optInt("answer");

                        // Create a new QuizList object and add it to the cache
                        cachedQuiz.add(new Quiz(
                                questionID,
                                categoryID,
                                question,
                                optionA,
                                optionB,
                                optionC,
                                optionD,
                                answer,
                                hintTxt
                        ));
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static ArrayList<Quiz> getAllQuizzes() {
        // Return cached quiz list, ensure it's not null before returning
        if (!cachedQuiz.isEmpty()) {
            return cachedQuiz;
        }
        return new ArrayList<>(); // Return an empty list if cachedQuizList is null
    }

    public static ArrayList<Category> getAllCategories() {
        return cachedCategory;
    }

    // Get a category name by ID from the global static map
    public static String getCategoryNameByID(int categoryID) {
        return categoryNameMap.get(categoryID);
    }

//    public static ArrayList<QuizHistory> getAllQuizHistory(Context context) {
//        List<QuizHistory> quizHistoryList;
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//
//        // Initialize the DAO
//        QuizHistoryDao quizHistoryDao = appDatabase.quizHistoryDao();
//
//        // Fetch data from the DAO and convert it to an ArrayList
//        quizHistoryList = quizHistoryDao.getAllQuizHistory();
//        // Return as an ArrayList
//        return new ArrayList<>(quizHistoryList);
//    }

}