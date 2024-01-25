package com.codervai.campusdeal.screen.deals;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentMyDealsBinding;
import com.codervai.campusdeal.screen.common.TabLayoutViewPagerAdapter;
import com.codervai.campusdeal.screen.home.CategoryFragment;
import com.codervai.campusdeal.util.Constants;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyDealsFragment extends Fragment {

    FragmentMyDealsBinding mVB;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVB = FragmentMyDealsBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mVB.pager.setUserInputEnabled(false); // disable sweeping

        ArrayList<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i< Constants.DEALS_TAB_MENU_LIST.size(); i++){
            fragments.add(new DealListFragment());
        }

        mVB.pager.setAdapter(new TabLayoutViewPagerAdapter(this, fragments));

        new TabLayoutMediator(mVB.tabLayout, mVB.pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(Constants.DEALS_TAB_MENU_LIST.get(position));
            }
        }).attach();
    }
}