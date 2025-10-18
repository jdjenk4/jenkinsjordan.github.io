/*
CS-499-10453-M01 Computer Science Capstone 2025
Southern New Hampshire University
Jordan Jenkins

Artifact Three

The purpose of this program is to showcase the enhancement of a mobile application in java. The enhancements that
were made improved the security of registration and storing of passwords into the local database via a salt and hash 
as well as parameterizing SQL queries to prevent SWL injection. Additional enhancements included cleaning up 
the UI/UX, implementing logic to keep unique usernames to prevent duplication and easier authentication lookups, 
and error catching.
*/

package com.example.androidappenhancement.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.Navigation;
import com.google.android.material.appbar.MaterialToolbar;
import com.example.androidappenhancement.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Set up toolbar as ActionBar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Connect NavController with toolbar and fragments
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController);
            System.out.println("NavController loaded: " + navController.getGraph().getStartDestination());
        } else {
            System.out.println("navHostFragment is null");
        }
    }

    // Handle "Up" navigation (back arrow)
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

}
