package com.codervai.campusdeal.screen.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentHomeBinding;
import com.codervai.campusdeal.databinding.FragmentOnBoardingBinding;
import com.codervai.campusdeal.util.Constants;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding mVB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mVB = FragmentHomeBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
    }
}