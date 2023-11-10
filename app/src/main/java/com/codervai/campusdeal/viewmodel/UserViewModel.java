package com.codervai.campusdeal.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.codervai.campusdeal.model.User;
import com.codervai.campusdeal.util.Constants;
import com.codervai.campusdeal.util.StateLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UserViewModel extends ViewModel {
    public static final String TAG = "UserViewModel";

    FirebaseFirestore db;

    @Inject
    public UserViewModel(FirebaseFirestore db){
        this.db = db;
    }


    public StateLiveData<Boolean> saveUserData(){
        StateLiveData<Boolean> saveUserDataLiveData = new StateLiveData<>();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser==null){
            saveUserDataLiveData.postError(new Exception("user not authenticated"));
            return saveUserDataLiveData;
        }
        // create a new user
        db.collection(Constants.USER_COLLECTION)
                .document(fUser.getUid())
                .set(new User(fUser))
                .addOnSuccessListener((aVoid) ->{
                    Log.d(TAG, "onSuccess: user saved successfully in firestore");
                    // success
                    saveUserDataLiveData.postSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "onFailure: user saved failed in firestore");
                    saveUserDataLiveData.postError(e);
                });
        return saveUserDataLiveData;
    }
}
