package com.tec.domingostore.Main.Sale;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SalesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SalesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is sale fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}