package com.codervai.campusdeal.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.codervai.campusdeal.model.DealRequest;
import com.codervai.campusdeal.model.Product;
import com.codervai.campusdeal.model.User;
import com.codervai.campusdeal.util.Constants;
import com.codervai.campusdeal.util.StateLiveData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DealViewModel extends ViewModel {
    FirebaseFirestore db;

    @Inject
    public DealViewModel(FirebaseFirestore db) {this.db = db;}

    public StateLiveData<Boolean> addDealRequest(User buyer, Product product){
        DealRequest dealRequest = new DealRequest(
                product.getId(),
                product.getTitle(),
                buyer.getUid(),
                buyer.getName(),
                product.getSellerId(),
                product.getSellerName(),
                new Date()
        );
        StateLiveData<Boolean> addDealRequestLiveData = new StateLiveData<>();

        db.collection(Constants.DEAL_REQUEST_COLLECTION)
                .document(product.getId())
                .set(dealRequest)
                .addOnSuccessListener(unused -> {
                    addDealRequestLiveData.postSuccess(true);
                })
                .addOnFailureListener(addDealRequestLiveData::postError);

        return  addDealRequestLiveData;
    }
}
