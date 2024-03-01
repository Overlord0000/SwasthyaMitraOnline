package com.example.swasthyamitra;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class EmergencyProcedureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_procedure);

        // Find views by their IDs
        CardView cprCardView = findViewById(R.id.CPR);
        CardView chokingReliefCardView = findViewById(R.id.ChokingRelief);
        CardView controlOfBleedingCardView = findViewById(R.id.Control_of_Bleeding);
        CardView treatmentForShockCardView = findViewById(R.id.TreatmentforShock);
        CardView basicBurnCareCardView = findViewById(R.id.Basic_Burn_Care);
        CardView seizureManagementCardView = findViewById(R.id.SeizureManagement);
        CardView strokeRecognitionCardView = findViewById(R.id.StrokeRecognition);
        CardView fractureManagementCardView = findViewById(R.id.FractureManagement);
        CardView heatstrokeOrHypothermiaCardView = findViewById(R.id.Heatstroke_or_Hypothermia);

        // Set click listeners
        cprCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToWebsite("https://www.redcross.org/take-a-class/cpr");
            }
        });

        chokingReliefCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToWebsite("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/choking.html");
            }
        });

        controlOfBleedingCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToWebsite("https://www.redcross.org/take-a-class/bleeding-control");
            }
        });

        treatmentForShockCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToWebsite("https://www.mayoclinic.org/first-aid/first-aid-shock/basics/ART-20056620");
            }
        });

        basicBurnCareCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToWebsite("https://www.mayoclinic.org/first-aid/first-aid-burns/basics/ART-20056649");
            }
        });

        seizureManagementCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToWebsite("https://www.epilepsy.com/learn/seizure-first-aid-and-safety");
            }
        });

        strokeRecognitionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToWebsite("https://www.stroke.org/en/about-stroke/stroke-symptoms");
            }
        });

        fractureManagementCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToWebsite("https://orthoinfo.aaos.org/en/diseases--conditions/fractures-broken-bones/");
            }
        });

        heatstrokeOrHypothermiaCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToWebsite("https://www.mayoclinic.org/first-aid/first-aid-heatstroke/basics/ART-20056655");
            }
        });
    }

    private void redirectToWebsite(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
