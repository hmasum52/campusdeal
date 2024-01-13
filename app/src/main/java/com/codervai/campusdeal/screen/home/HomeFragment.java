package com.codervai.campusdeal.screen.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentHomeBinding;
import com.codervai.campusdeal.databinding.FragmentOnBoardingBinding;
import com.codervai.campusdeal.util.Constants;
import com.codervai.campusdeal.viewmodel.UserViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private FragmentHomeBinding mVB;

    private UserViewModel userVM;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userVM = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mVB = FragmentHomeBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mVB.pager.setUserInputEnabled(false); // disable sweeping
        loadUserData();
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i< Constants.CATEGORY_LIST.size(); i++){
            Fragment f = new CategoryFragment();

            // set args
            Bundle b = new Bundle();
            b.putString("name", Constants.CATEGORY_LIST.get(i));
            f.setArguments(b);
            fragments.add(f);
        }

        mVB.pager.setAdapter(new CategoryViewPagerAdapter(this, fragments));

        new TabLayoutMediator(mVB.tabLayout, mVB.pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(Constants.CATEGORY_LIST.get(position));
            }
        }).attach();

        mVB.addProductFab.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_addProductFragment);
        });
    }

    private void loadUserData() {
        userVM.getUser();
    }
}