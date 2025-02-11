package com.javaprogramming.quiz.interfaces.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.javaprogramming.quiz.model.Category;
import com.javaprogramming.quiz.utilities.Helper;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Category category);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertCategories(List<Category> categories);

    @Query("SELECT * FROM "+ Helper.CATEGORY_TABLE_NAME)
    LiveData<List<Category>> getAllCategories();

    @Query("UPDATE category SET levelsCompleted_C = levelsCompleted_C + 1 WHERE categoryID_C = :categoryId")
    void updateLevelCompleted(int categoryId);

}
