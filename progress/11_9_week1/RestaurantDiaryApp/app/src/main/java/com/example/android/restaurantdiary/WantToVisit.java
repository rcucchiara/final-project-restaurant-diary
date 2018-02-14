package com.example.android.restaurantdiary;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WantToVisit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_want_to_visit);

        FloatingActionButton fab = findViewById(R.id.fab_want_to_visit);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WantToVisit.this, ActivityForm.class);
                startActivity(intent);
            }
        });
    }
}

// This is a test to makes sure I can push to the master branch - Nate
// yo jake here test stuff