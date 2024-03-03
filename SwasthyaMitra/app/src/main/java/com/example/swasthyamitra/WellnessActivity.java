package com.example.swasthyamitra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

public class WellnessActivity extends AppCompatActivity {

    private CardView Nutrition, Mental_Health, Fitness, Genral_Health, Sleep, Weight, Medical_News, Parenting, Womens_Health;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellness);

        // Initialize card views
        Nutrition = (CardView) findViewById(R.id.Nutrition_and_Diet);
        Mental_Health = (CardView) findViewById(R.id.Mental_Health_and_Mindfulness);
        Fitness = (CardView) findViewById(R.id.Fitness_and_Exercise);
        Genral_Health = (CardView) findViewById(R.id.General_Health_Information);
        Sleep = (CardView) findViewById(R.id.Sleep_and_Sleep_Disorders);
        Weight = (CardView) findViewById(R.id.Weight_Management);
        Medical_News = (CardView) findViewById(R.id.Medical_News_and_Updates);
        Parenting = (CardView) findViewById(R.id.Parenting_and_Child_Health);
        Womens_Health = (CardView) findViewById(R.id.Womens_Health);

        // Set click listeners for card views
        setOnClickListener(Nutrition, "https://www.niddk.nih.gov/health-information/diet-nutrition");
        setOnClickListener(Mental_Health, "https://www.who.int/news-room/fact-sheets/detail/mental-health-strengthening-our-response");
        setOnClickListener(Fitness, "https://www.health.harvard.edu/topics/exercise-and-fitness");
        setOnClickListener(Genral_Health, "https://www.webmd.com/");
        setOnClickListener(Sleep, "https://www.cdc.gov/sleep/index.html");
        setOnClickListener(Weight, "https://www.niddk.nih.gov/health-information/weight-management");
        setOnClickListener(Medical_News, "https://www.medicalnewstoday.com/");
        setOnClickListener(Parenting, "https://www.ncbi.nlm.nih.gov/pmc/articles/PMC1113192/");
        setOnClickListener(Womens_Health, "https://www.who.int/health-topics/women-s-health");
    }

    private void setOnClickListener(CardView cardView, String url) {
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                // Check for default browser availability
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // Check for specific browsers if default is unavailable
                    if (intent.resolveActivity(getPackageManager()
                    ) != null) {
                        intent.setPackage("com.android.chrome"); // Example for Chrome
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "No web browser found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
