package com.codervai.campusdeal.screen;

import android.animation.Animator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codervai.campusdeal.MainActivity;
import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentOnBoardingBinding;
import com.codervai.campusdeal.databinding.FragmentSplashBinding;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SplashFragment extends Fragment {

    private FragmentSplashBinding mVB;

    @Inject
    FirebaseAuth fAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mVB = FragmentSplashBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mVB.cdLogoLottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                // check if user if authenticated
                if(fAuth.getCurrentUser()!=null){
                    // navigate to home fragment
                    MainActivity.navigateToStartDestination(getActivity(),
                            R.id.action_splashFragment_to_homeFragment,
                            R.id.homeFragment);
                }else{
                    // navigate to on boarding screen
                    MainActivity.navigateToStartDestination(getActivity(),
                            R.id.action_splashFragment_to_onBoardingFragment,
                            R.id.onBoardingFragment);
                }
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
    }
}