package com.codervai.campusdeal.screen;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codervai.campusdeal.MainActivity;
import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentOnBoardingBinding;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OnBoardingFragment extends Fragment {

    private FragmentOnBoardingBinding mVB;

    @Inject
    FirebaseAuth fAuth;

    // https://developer.android.com/training/basics/intents/result
    // https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#configuration
    ActivityResultLauncher<Intent> signInIntentLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    //IdpResponse response = result.getIdpResponse();
                    if(result.getResultCode() == RESULT_OK){
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Log.d("After Sing in: ", user.getDisplayName());
                    }else{
                        //response.getError().getErrorCode();
                    }
                }
            }
    );


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mVB = FragmentOnBoardingBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.fragment_login)
                .setGoogleButtonId(R.id.google_sign_in_btn)
                .setEmailButtonId(R.id.email_sing_in_btn)
                .build();

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.Theme_CampusDeal)
                .setAvailableProviders(providers)
                .setAuthMethodPickerLayout(customLayout)
                .build();


        mVB.getStartBtn.setOnClickListener(view1 -> {
            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                // https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#sign-out
                AuthUI.getInstance()
                        .signOut(requireContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Signed out", Toast.LENGTH_SHORT).show();
                            }
                        });
                return;
            }
            signInIntentLauncher.launch(signInIntent);
        });
    }
}