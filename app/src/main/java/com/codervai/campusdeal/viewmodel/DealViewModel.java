package com.codervai.campusdeal.viewmodel;

import android.util.Log;

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
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    public StateLiveData<DealRequest> getDealRequest(String productId){
        StateLiveData<DealRequest> dealRequestLiveData = new StateLiveData<>();

        db.collection(Constants.DEAL_REQUEST_COLLECTION)
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        dealRequestLiveData.postSuccess(documentSnapshot.toObject(DealRequest.class));
                    }else{
                        dealRequestLiveData.postError(new Exception("deal request not found"));
                    }
                })
                .addOnFailureListener(e -> {
                    dealRequestLiveData.postError(new Exception("deal request not found"));
                });

        return dealRequestLiveData;
    }

    public StateLiveData<Boolean> cancelDealRequest(String productId){
        StateLiveData<Boolean> cancelDealRequestLiveData = new StateLiveData<>();

        db.collection(Constants.DEAL_REQUEST_COLLECTION)
                .document(productId)
                .delete()
                .addOnSuccessListener(unused -> {
                    cancelDealRequestLiveData.postSuccess(true);
                })
                .addOnFailureListener(cancelDealRequestLiveData::postError);

        return cancelDealRequestLiveData;
    }

    public StateLiveData<List<DealRequest>> getDealRequests(String field, String value){
        StateLiveData<List<DealRequest>> dealRequestLiveData = new StateLiveData<>();

        db.collection(Constants.DEAL_REQUEST_COLLECTION)
                .whereEqualTo(field, value)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DealRequest> dealRequests = queryDocumentSnapshots.toObjects(DealRequest.class);
                    if(dealRequests.isEmpty()){
                        dealRequestLiveData.postError(new Exception("no deal request found"));
                    }else{
                        dealRequestLiveData.postSuccess(dealRequests);
                    }
                })
                .addOnFailureListener(e -> {
                    dealRequestLiveData.postError(new Exception("no deal request found"));
                });

        return dealRequestLiveData;
    }

    // make deal

    // decline deal
    public StateLiveData<Boolean> declineDealRequest(String productId){
        StateLiveData<Boolean> declineDealRequestLiveData = new StateLiveData<>();

        db.collection(Constants.DEAL_REQUEST_COLLECTION)
                .document(productId)
                .delete()
                .addOnSuccessListener(unused -> {
                    declineDealRequestLiveData.postSuccess(true);
                })
                .addOnFailureListener(declineDealRequestLiveData::postError);

        return declineDealRequestLiveData;
    }
}
