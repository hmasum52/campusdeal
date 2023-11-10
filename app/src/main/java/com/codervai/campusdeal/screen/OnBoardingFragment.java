package com.codervai.campusdeal.screen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codervai.campusdeal.MainActivity;
import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentOnBoardingBinding;

public class OnBoardingFragment extends Fragment {

    private FragmentOnBoardingBinding mVB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mVB = FragmentOnBoardingBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mVB.getStartBtn.setOnClickListener(view1 -> {
           // Toast.makeText(getContext(), "Get start", Toast.LENGTH_SHORT).show();
            //NavHostFragment.findNavController(this)
            //        .navigate(R.id.action_onBoardingFragment_to_homeFragment);
            MainActivity.navigateToStartDestination(getActivity(),
                    R.id.action_onBoardingFragment_to_homeFragment, R.id.homeFragment);
        });
    }
}