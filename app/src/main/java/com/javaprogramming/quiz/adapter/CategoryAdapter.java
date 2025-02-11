package com.javaprogramming.quiz.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.javaprogramming.quiz.R;
import com.javaprogramming.quiz.activity.QuizPreStartActivity;
import com.javaprogramming.quiz.database.SaveData;
import com.javaprogramming.quiz.model.Category;
import com.javaprogramming.quiz.model.CategoryQuestionCount;
import com.javaprogramming.quiz.utilities.Helper;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ExampleViewHolder> {
    private final Context mContext;
    private List<Category> categories;
    private List<CategoryQuestionCount> categoryCounts;

    public CategoryAdapter(Context context, List<Category> exampleList, List<CategoryQuestionCount> categoryCounts) {
        mContext = context;
        categories = exampleList;
        this.categoryCounts = categoryCounts;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_category_btn, parent, false);
        return new ExampleViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Category currentItem = categories.get(position);

        // Calculate the index and ensure it's not less than 0
        int index = Math.max(currentItem.getCategoryID_C() - 1, 0);

        // Access the category counts map safely
        CategoryQuestionCount current = categoryCounts.get(index);

        int requiredCompletedLevel = 5;


        if (currentItem.getCategoryID_C() == 1){
            holder.btn_recycler.setEnabled(true);
            holder.layer_lock.setVisibility(GONE);
            holder.txtMsg.setVisibility(GONE);
            holder.layer_unlock.setVisibility(VISIBLE);
            holder.btn_recycler.setBackgroundResource(R.drawable.primary_15px);
        }else{

            Category previousItem = categories.get(position -1);

            CategoryQuestionCount previousCategory = categoryCounts.get(index - 1);

            if (calculateFullSets(previousCategory.getTotalQuestions_CQC()) <= requiredCompletedLevel){
                requiredCompletedLevel = 2;
            }

            if (previousItem.getLevelsCompleted_C() >= requiredCompletedLevel){
                holder.btn_recycler.setEnabled(true);
                holder.layer_lock.setVisibility(GONE);
                holder.txtMsg.setVisibility(GONE);
                holder.layer_unlock.setVisibility(VISIBLE);
                holder.btn_recycler.setBackgroundResource(R.drawable.primary_15px);
            }else{
                holder.btn_recycler.setEnabled(false);
                holder.txtMsg.setVisibility(VISIBLE);
                holder.txtMsg.setText(mContext.getString(R.string.complete_previous_category)+" 5" );
                holder.layer_lock.setVisibility(VISIBLE);
                holder.layer_unlock.setVisibility(GONE);
                holder.btn_recycler.setBackgroundResource(R.drawable.primary_transparent_50_15px);
            }
        }

        holder.txtLevel.setText(currentItem.getLevelsCompleted_C() +"/"+ calculateFullSets(current.getTotalQuestions_CQC()));

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.appCompatTextView.getLayoutParams();

        if (holder.txtMsg.getVisibility() == View.GONE) {
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            params.bottomToTop = ConstraintLayout.LayoutParams.UNSET; // Remove previous constraint
        } else {
            params.topToTop = ConstraintLayout.LayoutParams.UNSET; // Remove top constraint
            params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET; // Remove bottom constraint
            params.bottomToTop = R.id.txt_msg;
        }

        holder.appCompatTextView.setLayoutParams(params);
        holder.appCompatTextView.requestLayout(); // Ensure the layout updates immediately



        String categoryTitle = currentItem.getCategoryTitle_C();
        holder.appCompatTextView.setText(categoryTitle);
        holder.btn_recycler.setOnClickListener(v -> {

            // Check if same level or set is clicked again at the end of the sets,
            // then it should return last time's same question
            SaveData.saveIsSetsNew(currentItem.getLevelsCompleted_C() != calculateFullSets(current.getTotalQuestions_CQC()), mContext);

            SaveData.saveClickedCategoryTitle(currentItem.getCategoryTitle_C(), mContext);
            SaveData.saveCategoryID(currentItem.getCategoryID_C(),mContext);
            SaveData.saveClickedSetsNumber(currentItem.getLevelsCompleted_C(),mContext);
            Bundle bundle = new Bundle();
            bundle.putSerializable("category", currentItem);
            Helper.navigateToActivity(mContext, QuizPreStartActivity.class, bundle, false);

        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setCategories(List<Category> categories, List<CategoryQuestionCount> categoryCounts) {
        this.categories = categories;
        this.categoryCounts = categoryCounts;
        notifyDataSetChanged();  // Notify the adapter that the data has changed and it needs to update
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout btn_recycler;
        private AppCompatTextView appCompatTextView;
        private LinearLayout layer_unlock, layer_lock;
        private TextView txtMsg, txtLevel;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            btn_recycler = itemView.findViewById(R.id.btn_recycler);
            appCompatTextView = itemView.findViewById(R.id.appCompatTextView);
            layer_unlock = itemView.findViewById(R.id.layer_unlock);
            layer_lock = itemView.findViewById(R.id.layer_lock);
            txtMsg = itemView.findViewById(R.id.txt_msg);
            txtLevel = itemView.findViewById(R.id.txtLevel);
        }
    }

    public int calculateFullSets(int totalQuestions) {
        return totalQuestions / 10;
    }
}