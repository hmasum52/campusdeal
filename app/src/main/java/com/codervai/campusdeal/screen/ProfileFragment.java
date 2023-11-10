package com.codervai.campusdeal.screen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codervai.campusdeal.MainActivity;
import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentOnBoardingBinding;
import com.codervai.campusdeal.databinding.FragmentProfileBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding mVB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mVB = FragmentProfileBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // when sign out button clicked
        mVB.signOutBtn.setOnClickListener(v -> {
            // https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#sign-out
            AuthUI.getInstance()
                    .signOut(requireContext())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Signed out", Toast.LENGTH_SHORT).show();
                            MainActivity.navigateToStartDestination(getActivity(), R.id.onBoardingFragment, R.id.onBoardingFragment);
                        }
                    });
        });
    }
}