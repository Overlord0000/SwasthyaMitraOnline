package com.example.swasthyamitra;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class WellnessActivity extends AppCompatActivity {

    CardView Nutrition, Mental_Health, Fitness, Genral_Health, Sleep, Weight, Medical_News, Parenting, Womens_Health;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellness);


        Nutrition = (CardView) findViewById(R.id.Nutrition_and_Diet);
        Mental_Health = (CardView) findViewById(R.id.Mental_Health_and_Mindfulness);
        Fitness = (CardView) findViewById(R.id.Fitness_and_Exercise);
        Genral_Health = (CardView) findViewById(R.id.General_Health_Information);
        Sleep = (CardView) findViewById(R.id.Sleep_and_Sleep_Disorders);
        Weight = (CardView) findViewById(R.id.Weight_Management);
        Medical_News = (CardView) findViewById(R.id.Medical_News_and_Updates);
        Parenting = (CardView) findViewById(R.id.Parenting_and_Child_Health);
        Womens_Health = (CardView) findViewById(R.id.Womens_Health);




        Nutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.niddk.nih.gov/health-information/diet-nutrition";


                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));


                if (intent.resolveActivity(getPackageManager()) != null) {

                    startActivity(intent);
                } else {

                    Toast.makeText(getApplicationContext(), "No web browser found", Toast.LENGTH_SHORT).show();
                }
            }
        });



        Mental_Health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.who.int/news-room/fact-sheets/detail/mental-health-strengthening-our-response";


                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));


                if (intent.resolveActivity(getPackageManager()) != null) {

                    startActivity(intent);
                } else {

                    Toast.makeText(getApplicationContext(), "No web browser found", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Fitness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.health.harvard.edu/topics/exercise-and-fitness";


                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));


                if (intent.resolveActivity(getPackageManager()) != null) {

                    startActivity(intent);
                } else {

                    Toast.makeText(getApplicationContext(), "No web browser found", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Genral_Health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.webmd.com/";


                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));


                if (intent.resolveActivity(getPackageManager()) != null) {

                    startActivity(intent);
                } else {

                    Toast.makeText(getApplicationContext(), "No web browser found", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.cdc.gov/sleep/index.html";


                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));


                if (intent.resolveActivity(getPackageManager()) != null) {

                    startActivity(intent);
                } else {

                    Toast.makeText(getApplicationContext(), "No web browser found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.niddk.nih.gov/health-information/weight-management";


                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));


                if (intent.resolveActivity(getPackageManager()) != null) {

                    startActivity(intent);
                } else {

                    Toast.makeText(getApplicationContext(), "No web browser found", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Medical_News.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.medicalnewstoday.com/";


                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));


                if (intent.resolveActivity(getPackageManager()) != null) {

                    startActivity(intent);
                } else {

                    Toast.makeText(getApplicationContext(), "No web browser found", Toast.LENGTH_SHORT).show();
                }
            }
        });



        Parenting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.ncbi.nlm.nih.gov/pmc/articles/PMC1113192/";


                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));


                if (intent.resolveActivity(getPackageManager()) != null) {

                    startActivity(intent);
                } else {

                    Toast.makeText(getApplicationContext(), "No web browser found", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Womens_Health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.who.int/health-topics/women-s-health";


                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));


                if (intent.resolveActivity(getPackageManager()) != null) {

                    startActivity(intent);
                } else {

                    Toast.makeText(getApplicationContext(), "No web browser found", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
}