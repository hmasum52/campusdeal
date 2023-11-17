package com.codervai.campusdeal.viewmodel;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddProductViewModel extends ViewModel {
    // image uri list
    private final MutableLiveData<List<Uri>> imageUriList = new MutableLiveData<>();

    // category index
    private final MutableLiveData<Integer> selectedCategoryIndex = new MutableLiveData<>();

    @Inject
    public AddProductViewModel(){

    }

    public MutableLiveData<List<Uri>> getImageUriList() {
        return imageUriList;
    }

    public void addToImageUriList(List<Uri> imageUriList) {
        if (this.imageUriList.getValue() == null) {
            this.imageUriList.setValue(imageUriList);
            return;
        }
        List<Uri> currentImageUriList = this.imageUriList.getValue();
        currentImageUriList.addAll(imageUriList);
        this.imageUriList.setValue(currentImageUriList);
    }

    public MutableLiveData<Integer> getSelectedCategoryIndex() {
        return selectedCategoryIndex;
    }

    public void setCategoryIndex(int categoryIndex) {
        selectedCategoryIndex.setValue(categoryIndex);
    }
}
