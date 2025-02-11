package com.javaprogramming.quiz.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.javaprogramming.quiz.interfaces.dao.CategoryDao;
import com.javaprogramming.quiz.interfaces.dao.QuizDao;
import com.javaprogramming.quiz.interfaces.dao.QuizHistoryDao;
import com.javaprogramming.quiz.interfaces.dao.QuizHistoryQuestionDao;
import com.javaprogramming.quiz.interfaces.dao.UserDao;
import com.javaprogramming.quiz.model.Category;
import com.javaprogramming.quiz.model.Quiz;
import com.javaprogramming.quiz.model.QuizHistory;
import com.javaprogramming.quiz.model.QuizHistorySingle;
import com.javaprogramming.quiz.model.User;
import com.javaprogramming.quiz.utilities.Helper;

@Database(entities = {
        User.class,
        Category.class,
        Quiz.class,
        QuizHistory.class,
        QuizHistorySingle.class}, version = 1, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();

    public abstract CategoryDao categoryDao();
    public abstract QuizDao quizDao();

    public abstract QuizHistoryDao quizHistoryDao();
    public abstract QuizHistoryQuestionDao quizHistoryQuestionDao();

    public static AppDatabase getDatabase(final Context context) {

        if (INSTANCE == null) {
            synchronized (AppDatabase.class){
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, Helper.DATABASE_NAME)
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }

        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // No changes, just a version upgrade

        }
    };

}