package com.javaprogramming.quiz.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.javaprogramming.quiz.R;
import com.javaprogramming.quiz.model.Quiz;
import com.javaprogramming.quiz.model.QuizHistoryWithQuestion;

import java.util.List;

public class SingleQuizHistoryAdapter extends RecyclerView.Adapter<SingleQuizHistoryAdapter.SingleQuizHistoryViewHolder> {
    private final Context mContext;
    private List<QuizHistoryWithQuestion> quizHistoryWithQuestions;

    public SingleQuizHistoryAdapter(Context context, List<QuizHistoryWithQuestion> quizHistoryWithQuestions) {
        mContext = context;
        this.quizHistoryWithQuestions = quizHistoryWithQuestions;

    }

    @NonNull
    @Override
    public SingleQuizHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_single_quiz_history, parent, false);
        return new SingleQuizHistoryViewHolder(v);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull SingleQuizHistoryAdapter.SingleQuizHistoryViewHolder holder, @SuppressLint("RecyclerView") int position) {

        QuizHistoryWithQuestion singleItem = quizHistoryWithQuestions.get(position);
        Quiz quiz = singleItem.quiz;

        int correctAnswer = quiz.getAnswer_Q();
        int selectedAnswer = singleItem.quizHistorySingle.getSelectedAnswer_QHQ();


        holder.txtQuestion.setText((position + 1) + ". " + quiz.getQuestion_Q());
        holder.txtOptionA.setText("A. " + quiz.getOptionA_Q());
        holder.txtOptionB.setText("B. " + quiz.getOptionB_Q());
        holder.txtOptionC.setText("C. " + quiz.getOptionC_Q());
        holder.txtOptionD.setText("D. " + quiz.getOptionD_Q());

        if (selectedAnswer != 0) {
            if (correctAnswer == selectedAnswer) {
                // Highlight the correct option in **GREEN**
                getOptionTextView(holder, correctAnswer).setTextColor(Color.GREEN);
            } else {
                // Highlight the correct answer in **GREEN**
                getOptionTextView(holder, correctAnswer).setTextColor(Color.GREEN);

                // Highlight the wrong selected answer in **RED**
                getOptionTextView(holder, selectedAnswer).setTextColor(Color.RED);
            }
        }else{
            holder.txtQuestion.setText((position + 1) + ". " + quiz.getQuestion_Q() + " - Skipped");
            holder.txtQuestion.setTextColor(Color.RED);
            // Highlight the correct answer in **GREEN**
            getOptionTextView(holder, correctAnswer).setTextColor(Color.GREEN);
        }

    }

    private TextView getOptionTextView(SingleQuizHistoryAdapter.SingleQuizHistoryViewHolder holder, int answer) {
        return switch (answer) {
            case 1 -> holder.txtOptionA;
            case 2 -> holder.txtOptionB;
            case 3 -> holder.txtOptionC;
            case 4 -> holder.txtOptionD;
            default ->
                    new TextView(holder.itemView.getContext()); // Return dummy TextView to avoid crash
        };
    }

    @Override
    public int getItemCount() {
        return quizHistoryWithQuestions.size();
    }

    public void setSingleQuiz(List<QuizHistoryWithQuestion> quizHistoryWithQuestions) {
        this.quizHistoryWithQuestions = quizHistoryWithQuestions;
        notifyDataSetChanged();
    }

    public static class SingleQuizHistoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtQuestion;
        private final TextView txtOptionA;
        private final TextView txtOptionB;
        private final TextView txtOptionC;
        private final TextView txtOptionD;

        public SingleQuizHistoryViewHolder(View itemView) {
            super(itemView);

            txtQuestion = itemView.findViewById(R.id.txt_question);
            txtOptionA = itemView.findViewById(R.id.txt_option_a);
            txtOptionB = itemView.findViewById(R.id.txt_option_b);
            txtOptionC = itemView.findViewById(R.id.txt_option_c);
            txtOptionD = itemView.findViewById(R.id.txt_option_d);
        }
    }
}