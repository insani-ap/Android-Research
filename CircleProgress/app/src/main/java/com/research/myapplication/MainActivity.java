package com.research.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.research.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        AnimationSet show = new AnimationSet(true);
        AnimationSet hide = new AnimationSet(true);

        binding.appBarMain.fab.setOnClickListener(view -> {
            if (binding.appBarMain.fab1.getVisibility() == View.VISIBLE) {
                binding.appBarMain.fab.animate().rotationBy(135F);
                show.addAnimation(slideUpOrDownAnimation(View.VISIBLE, binding.appBarMain.fab1));
                show.addAnimation(fadeInOrOutAnimation(View.VISIBLE));
                binding.appBarMain.fab1.setVisibility(View.GONE);
                binding.appBarMain.fab1.startAnimation(show);
            } else {
                binding.appBarMain.fab.animate().rotationBy(-135F);
                hide.addAnimation(fadeInOrOutAnimation(View.GONE));
                hide.addAnimation(slideUpOrDownAnimation(View.GONE, binding.appBarMain.fab1));
                binding.appBarMain.fab1.setVisibility(View.VISIBLE);
                binding.appBarMain.fab1.startAnimation(hide);
            }
        });
        binding.logOut.setOnClickListener(v -> finish());

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private Animation fadeInOrOutAnimation(int visibility) {
        Animation animation;

        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            animation = new AlphaAnimation(0, 1);
        } else {
            animation = new AlphaAnimation(1, 0);
        }

        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(250);

        return animation;
    }

    private Animation slideUpOrDownAnimation(int visibility, View view) {
        Animation animation;

        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            animation = new TranslateAnimation(0,0,10.0F/100.0F * view.getHeight(), 0);
        } else {
            animation = new TranslateAnimation(0,0,0, 10.0F/100.0F * view.getHeight());
        }

        animation.setDuration(300);
        animation.setFillAfter(true);

        return animation;
    }
}