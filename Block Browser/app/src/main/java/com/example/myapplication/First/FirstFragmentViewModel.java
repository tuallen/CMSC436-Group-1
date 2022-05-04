package com.example.myapplication.First;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FirstFragmentViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public void FirstFragViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is recipes fragment");
    }
    public LiveData<String> getText() {
        return mText;
    }

}
