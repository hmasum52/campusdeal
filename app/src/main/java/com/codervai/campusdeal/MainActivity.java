package com.codervai.campusdeal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.codervai.campusdeal.databinding.ActivityMainBinding;

import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {

    private ActivityMainBinding mVB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVB = ActivityMainBinding.inflate(super.getLayoutInflater());
        setContentView(mVB.getRoot());

        // set up bottom navigation bar with navhost fragments nav controller.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(mVB.bottomNavigation, navController);

        navController.addOnDestinationChangedListener(this);
    }

    @Override
    public void onDestinationChanged(@NonNull NavController navController,
                                     @NonNull NavDestination navDestination,
                                     @Nullable Bundle bundle) {
        Map<String, NavArgument> args = navDestination.getArguments();

        boolean showBottomNav = args.containsKey("showBottomNav")
                && Objects.equals(Objects.requireNonNull(args.get("showBottomNav")).getDefaultValue(), true);

        if(showBottomNav){
            mVB.bottomNavigation.setVisibility(View.VISIBLE);
        }else {
            mVB.bottomNavigation.setVisibility(View.GONE);
        }
    }

    public static void navigateToStartDestination(Activity activity, Integer transition, Integer startFragmentId){
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        navController.navigate(transition);

        // set start destination
        NavInflater inflater = navController.getNavInflater();
        NavGraph navGraph = inflater.inflate(R.navigation.nav_graph);
        navGraph.setStartDestination(startFragmentId);
        navController.setGraph(navGraph);
    }
}