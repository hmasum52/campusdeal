package com.codervai.campusdeal.screen;

import android.animation.Animator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codervai.campusdeal.MainActivity;
import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentOnBoardingBinding;
import com.codervai.campusdeal.databinding.FragmentSplashBinding;
import com.codervai.campusdeal.model.User;
import com.codervai.campusdeal.util.StateData;
import com.codervai.campusdeal.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SplashFragment extends Fragment {

    private FragmentSplashBinding mVB;

    @Inject
    FirebaseAuth fAuth;

    private UserViewModel userVM;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userVM = new ViewModelProvider(this).get(UserViewModel.class);
    }

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
                    checkIfUserExists();
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


    public void checkIfUserExists(){
        userVM.fetchUserProfile()
                .observe(getViewLifecycleOwner(), new Observer<StateData<User>>() {
                    @Override
                    public void onChanged(StateData<User> userStateData) {
                        switch (userStateData.getStatus()){
                            case SUCCESS:
                                User user = userStateData.getData();
                                if(user !=null && user.checkIfProfileIsComplete()){
                                    MainActivity.navigateToStartDestination(getActivity(),
                                            R.id.action_splashFragment_to_homeFragment,
                                            R.id.homeFragment);
                                }else {
                                    MainActivity.navigateToStartDestination(getActivity(),
                                            R.id.action_splashFragment_to_completeProfileFragment,
                                            R.id.completeProfileFragment);
                                }
                                break;
                            case ERROR:
                                MainActivity.navigateToStartDestination(getActivity(),
                                        R.id.action_splashFragment_to_onBoardingFragment, R.id.onBoardingFragment);
                                break;
                        }
                    }
                });
    }
}