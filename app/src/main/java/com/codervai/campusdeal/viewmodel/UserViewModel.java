package com.codervai.campusdeal.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.codervai.campusdeal.model.Campus;
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

    // user profile live data
    StateLiveData<User> userLiveData = new StateLiveData<>();


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

    // fetch user data
    public StateLiveData<User> fetchUserProfile() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser == null){
            Log.d(TAG, "fetchUserProfile: user is not logged in");
            userLiveData.postError(new Exception("User is not logged in"));
            return userLiveData;
        }

        db.collection(Constants.USER_COLLECTION)
                .document(fUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        Log.d(TAG, "fetchUserProfile: failed to fetch user profile");
                        userLiveData.postError(task.getException());
                        return;
                    }
                    // check if the user exists
                    if(!task.getResult().exists()){ // user does not exists
                        Log.d(TAG, "onComplete: user does not exists. Creating new user");
                        userLiveData.postError(new RuntimeException("User does not exists"));
                    }else {
                        // user exists: get user data
                        User user = task.getResult().toObject(User.class);
                        if(user == null){
                            userLiveData.postError(new Exception("User is null"));
                            return;
                        }
                        Log.d(TAG, "User already exists. User name is "+user.getName());

                        // success
                        userLiveData.postSuccess(user);
                    }
                });
        return userLiveData;
    }

    public StateLiveData<Boolean> saveProfileCompleteData(String phone, Campus campus){
        StateLiveData<Boolean> profileCompleteLiveData = new StateLiveData<>();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        // save phone number and campus to firebase
        db.collection(Constants.USER_COLLECTION)
                .document(fUser.getUid())
                .update("phone", phone, "campus", campus)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "saveProfileCompleteData: phone number and campus saved successfully");
                    profileCompleteLiveData.postSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "saveProfileCompleteData: failed to save phone number and campus");
                    profileCompleteLiveData.postError(e);
                });

        return profileCompleteLiveData;
    }

}
