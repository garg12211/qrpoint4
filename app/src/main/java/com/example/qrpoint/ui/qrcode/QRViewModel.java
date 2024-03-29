package com.example.qrpoint.ui.qrcode;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QRViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public QRViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("QR Code");
    }

    public LiveData<String> getText() {
        return mText;
    }
}