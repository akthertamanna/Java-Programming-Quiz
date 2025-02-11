package com.javaprogramming.quiz.interfaces;

public interface DialogCallback {
    void onConfirm(String purpose);  // This will be called when the user clicks "Yes" or "Confirm"
    void onCancel(String purpose);   // This will be called when the user clicks "No" or "Cancel"
}
