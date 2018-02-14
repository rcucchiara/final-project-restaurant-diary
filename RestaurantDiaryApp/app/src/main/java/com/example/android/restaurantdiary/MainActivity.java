package com.example.android.restaurantdiary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.graphics.drawable.AnimationDrawable;
import android.widget.LinearLayout;

/**
 * MainActivity is the activity called at startup of app.
 */

public class MainActivity extends AppCompatActivity {


    /**
     * onCreate method.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_menu_layout);
        AnimationDrawable anim = (AnimationDrawable) linearLayout.getBackground();
        anim.setEnterFadeDuration(2000);
        anim.setExitFadeDuration(4000);
        anim.start();


        Button toVisit = findViewById(R.id.button_main_menu_want_to);
        Button visited = findViewById(R.id.button_main_menu_have_been);
        Button search = findViewById(R.id.button_main_menu_search);

        toVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toVisitIntent = new Intent(MainActivity.this, ProspectiveRestaurantActivity.class);
                startActivity(toVisitIntent);
            }
        });

        visited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent visitedIntent = new Intent(MainActivity.this, VisitedRestaurantActivity.class);
                startActivity(visitedIntent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(MainActivity.this, SearchRestaurantActivity.class);
                startActivity(searchIntent);
            }
        });
    }

}
