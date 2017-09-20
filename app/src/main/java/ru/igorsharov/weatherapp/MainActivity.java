package ru.igorsharov.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View fragmentContainer = findViewById(R.id.frame);
        if (fragmentContainer != null) {
            OneActivity detailFragment = new OneActivity();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, detailFragment);
            transaction.commit();
        }
    }
}

