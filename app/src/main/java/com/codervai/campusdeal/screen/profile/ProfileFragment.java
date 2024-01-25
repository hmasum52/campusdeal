package com.codervai.campusdeal.screen.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codervai.campusdeal.MainActivity;
import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentProfileBinding;
import com.codervai.campusdeal.model.User;
import com.codervai.campusdeal.util.ProfileMenuItem;
import com.codervai.campusdeal.util.ItemClickListener;
import com.codervai.campusdeal.util.StateData;
import com.codervai.campusdeal.viewmodel.UserViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding mVB;

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
        mVB = FragmentProfileBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        userVM.fetchUserProfile().observe(getViewLifecycleOwner(), new Observer<StateData<User>>() {
            @Override
            public void onChanged(StateData<User> userStateData) {
                if(userStateData.getStatus() == StateData.DataStatus.SUCCESS){
                    User user = userStateData.getData();
                    if(user!=null){
                        Log.d("TAG", "onViewCreated: user data found");
                        // set the name to username text view
                        mVB.usernameTv.setText(user.getName());
                        // set the email to email text view
                        mVB.emailTv.setText(user.getEmail());
                        // check if user has profile image
                        if(user.getProfileImageUrl() != null) {
                            // load the image using glide
                            Glide.with(mVB.getRoot())
                                    .load(user.getProfileImageUrl())
                                    .placeholder(R.drawable.avatar_bg)
                                    .into(mVB.profileImage);
                        }else{
                            // set name initial to nameInitialTV
                            if(!user.getName().isEmpty())
                                mVB.nameInitialsText.setText(user.getName().substring(0,1));
                        }
                    }else{
                        Toast.makeText(getContext(), "Network error! User not found.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "Network error!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set up profile menu adapter
        setUpProfileMenu();

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

    private void setUpProfileMenu() {
        ProfileMenuItemAdapter adapter = new ProfileMenuItemAdapter();
        mVB.optionListRv.setAdapter(adapter);

        adapter.setOnProfileMenuItemClickListener(new ItemClickListener<ProfileMenuItem>() {
            @Override
            public void onItemClick(ProfileMenuItem item) {
                String msg = "Unknown!";
                switch (item.getTitle()){
                    case "My Products":
                        msg = "My Products";
                        //Toast.makeText(getContext(), msg,Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(ProfileFragment.this)
                                .navigate(R.id.action_profileFragment_to_myProductsFragment);
                        break;
                    case "My Wishlist":
                        NavHostFragment.findNavController(ProfileFragment.this)
                                .navigate(R.id.action_profileFragment_to_myWishlistFragment);
                        break;
                    case "Edit Profile":
                        NavHostFragment.findNavController(ProfileFragment.this)
                                .navigate(R.id.action_profileFragment_to_editProfileFragment);
                        break;

                }

            }
        });
    }
}