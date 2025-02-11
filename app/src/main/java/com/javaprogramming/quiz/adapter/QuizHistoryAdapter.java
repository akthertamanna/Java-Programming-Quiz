package com.javaprogramming.quiz.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.javaprogramming.quiz.R;
import com.javaprogramming.quiz.activity.FullHistoryActivity;
import com.javaprogramming.quiz.database.QuizDataHandler;
import com.javaprogramming.quiz.model.Category;
import com.javaprogramming.quiz.model.CategoryQuestionCount;
import com.javaprogramming.quiz.model.QuizHistoryWithCategory;
import com.javaprogramming.quiz.utilities.Helper;
import com.javaprogramming.quiz.utilities.TimeUtils;
import com.javaprogramming.quiz.model.QuizHistory;
import java.util.ArrayList;
import java.util.List;

public class QuizHistoryAdapter extends RecyclerView.Adapter<QuizHistoryAdapter.QuizHistoryViewHolder> {
    private final Context mContext;
    private List<QuizHistoryWithCategory> quizHistoryLists;

    public QuizHistoryAdapter(Context context, List<QuizHistoryWithCategory> quizHistoryLists) {
        mContext = context;
        this.quizHistoryLists = quizHistoryLists;

    }

    @NonNull
    @Override
    public QuizHistoryAdapter.QuizHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_quiz_history, parent, false);
        return new QuizHistoryViewHolder(v);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull QuizHistoryAdapter.QuizHistoryViewHolder holder, @SuppressLint("RecyclerView") int position) {

        QuizHistoryWithCategory quizHistoryWithCategory = quizHistoryLists.get(position);
        QuizHistory singleItem = quizHistoryWithCategory.getQuizHistory();

        holder.txtCategoryName.setText(quizHistoryWithCategory.getCategoryTitle());

        if (quizHistoryWithCategory.getCategoryTitle().isEmpty()){
            holder.txtCategoryName.setText(String.format("Category ID: %d", singleItem.getCategoryID_QH()));
        }

        holder.txtCoinRewardGet.setText(String.format("+%d", singleItem.getScore_QH()));
        holder.txtCorrectAnswer.setText(String.format("Correct answer: %d", singleItem.getCorrectAnswers_QH()));
        holder.txtWrongAnswer.setText(String.format("Wrong answer: %d", singleItem.getWrongAnswers_QH()));
        holder.txtSkippedQuestion.setText(String.format("Skipped question: %d", singleItem.getSkippedQuestions_QH()));
        holder.txtTimeTaken.setText(TimeUtils.convertMillisecondsToTime(singleItem.getTimeTaken_QH()));

        // Set alternating background color based on position (even or odd)
        if (position % 2 == 0) {
            // Set white background for even positions
            holder.linearLayoutQizHistoryItemID.setBackgroundColor(ContextCompat.getColor(mContext, R.color.PapayaWhip));
            holder.cardviewQuizHistoryItemID.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.PapayaWhip));
        } else {
            // Set grey background for odd positions
            holder.linearLayoutQizHistoryItemID.setBackgroundColor(ContextCompat.getColor(mContext, R.color.LavenderBlush));
            holder.cardviewQuizHistoryItemID.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.LavenderBlush));
        }

        holder.itemView.setOnClickListener(v -> {
            Bundle data = new Bundle();
            data.putSerializable("quizHistory", quizHistoryWithCategory);
            Helper.navigateToActivity(mContext, FullHistoryActivity.class, data, true);
        });


    }

    @Override
    public int getItemCount() {
        return quizHistoryLists.size();
    }

    public void setCategories(List<QuizHistoryWithCategory> quizHistoryList) {
        this.quizHistoryLists = quizHistoryList;
        notifyDataSetChanged();
    }

    public static class QuizHistoryViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardviewQuizHistoryItemID;
        private final LinearLayout linearLayoutQizHistoryItemID;
        private final TextView txtCategoryName;
        private final TextView txtCorrectAnswer;
        private final TextView txtWrongAnswer;
        private final TextView txtSkippedQuestion;
        private final TextView txtTimeTaken;
        private final TextView txtCoinRewardGet;

        public QuizHistoryViewHolder(View itemView) {
            super(itemView);
            linearLayoutQizHistoryItemID = itemView.findViewById(R.id.linear_layout_quiz_history_item_ID);
            cardviewQuizHistoryItemID = itemView.findViewById(R.id.cardview_quiz_history_item_ID);
            txtCategoryName = itemView.findViewById(R.id.txt_category_name);
            txtCorrectAnswer = itemView.findViewById(R.id.txt_correct_answer);
            txtWrongAnswer = itemView.findViewById(R.id.txt_wrong_answer);
            txtSkippedQuestion = itemView.findViewById(R.id.txt_skipped_question);
            txtTimeTaken = itemView.findViewById(R.id.txt_time_taken);
            txtCoinRewardGet = itemView.findViewById(R.id.txt_coin_reward_get);
        }
    }
}