package com.company.vitrents.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.company.vitrents.R;
import com.company.vitrents.databinding.ActivityMainBinding;
import com.company.vitrents.fragments.ChatsFragment;
import com.company.vitrents.fragments.FavoritesFragment;
import com.company.vitrents.fragments.HomeFragment;
import com.company.vitrents.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseAuth=firebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()==null){
            startLoginActivity();
        }




        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId= item.getItemId();

                if(itemId== R.id.item_home){

                    showHomeFragment();

                }
                else if(itemId==R.id.item_chats){

                    showChatFragment();

                }
                else if(itemId==R.id.item_favorite){

                    showFavoritesFragment();

                }
                else if(itemId==R.id.item_profile){

                    showProfileFragment();

                }


                return true;
            }
        });

    }

    private void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void showHomeFragment(){
        binding.toolbarTitleTv.setText("Home");

        FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        HomeFragment homeFragment=new HomeFragment();

        fragmentTransaction.replace(binding.fragmentsFl.getId(),homeFragment,"HomeFragment");
        fragmentTransaction.commit();

    }

    private void showChatFragment(){
        binding.toolbarTitleTv.setText("Chats");

        FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        ChatsFragment chatFragment=new ChatsFragment();

        fragmentTransaction.replace(binding.fragmentsFl.getId(),chatFragment,"ChatFragment");
        fragmentTransaction.commit();

    }

    private void showFavoritesFragment(){
        binding.toolbarTitleTv.setText("Favorites");

        FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        FavoritesFragment favoritesFragment=new FavoritesFragment();

        fragmentTransaction.replace(binding.fragmentsFl.getId(),favoritesFragment,"FavoriteFragment");
        fragmentTransaction.commit();

    }

    private void showProfileFragment(){
        binding.toolbarTitleTv.setText("Profile");

        FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        ProfileFragment profileFragment=new ProfileFragment();

        fragmentTransaction.replace(binding.fragmentsFl.getId(),profileFragment,"ProfileFragment");
        fragmentTransaction.commit();

    }
}