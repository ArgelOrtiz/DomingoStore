package com.tec.domingostore.Main.Products;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProductsViewModal extends ViewModel {

    private MutableLiveData<String> mText;

    public ProductsViewModal() {
        mText = new MutableLiveData<>();
        mText.setValue("This is product fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setText(String s){
        mText.setValue(s);
    }
}
