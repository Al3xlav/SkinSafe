package com.skinsafe.skinsafe;

import static com.skinsafe.skinsafe.TrackDetailsActivity.loadAd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.skinsafe.skinsafe.Database.HistoryDatabaseClass;
import com.skinsafe.skinsafe.Database.HistoryModel;
import com.skinsafe.skinsafe.Database.TrackDatabaseClass;
import com.skinsafe.skinsafe.Database.TrackModel;
import com.skinsafe.skinsafe.MainMenu.MainActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    private final String SAVE_INSTANCE = "save_instance";

    private TextView mainDig;
    private TextView secondDig0;
    private TextView secondDig1;
    private TextView secondDig2;
    private TextView secondDig3;
    private TextView secondDig4;
    private boolean save;
    private int track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ImageView photoImage = findViewById(R.id.photoImage);
        mainDig = findViewById(R.id.mainDig);
        secondDig0 = findViewById(R.id.secondDig0);
        secondDig1 = findViewById(R.id.secondDig1);
        secondDig2 = findViewById(R.id.secondDig2);
        secondDig3 = findViewById(R.id.secondDig3);
        secondDig4 = findViewById(R.id.secondDig4);

        Intent intent = getIntent();
        track = intent.getIntExtra("track", -2);
        save = intent.getBooleanExtra("saveOrNot", true);
        if (savedInstanceState != null && !savedInstanceState.getBoolean(SAVE_INSTANCE, false))
            save = false;
        float[] output = intent.getFloatArrayExtra("output");
        Bitmap bitMap = (Bitmap) intent.getExtras().get("imageBitmap");
        photoImage.setImageBitmap(bitMap);

        loadAd(this);
        setDiagnoze(output, bitMap);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setDiagnoze(float[] output, Bitmap bitMap) {
        Map<Float, String> digMap = new HashMap<>();
        digMap.put(output[0], "Actinic Keratosis");
        digMap.put(output[1], "Basal Cell Carcinoma");
        digMap.put(output[2], "Melanoma");
        digMap.put(output[3], "Nevus");
        digMap.put(output[4], "Seborrheic Keratosis");
        digMap.put(output[5], "Squamous Cell Carcinoma");
        List<Float> list = new ArrayList<>();
        list.addAll(digMap.keySet());
        Collections.sort(list);
        DecimalFormat df = new DecimalFormat("#.##");
        for (int i = list.size() - 1; i >= 0; i--) {
            if (i == list.size() - 1) {
                mainDig.setText("Probably it's: " + digMap.get(list.get(i)) + "\nThe chance is: " + df.format(list.get(i) * 100) + "%");
            } else {
                switch (i) {
                    case 4:
                        secondDig0.setText(digMap.get(list.get(i)) + " with chance: " + df.format(list.get(i) * 100) + "%" + "\n");
                        break;
                    case 3:
                        secondDig1.setText(digMap.get(list.get(i)) + " with chance: " + df.format(list.get(i) * 100) + "%" + "\n");
                        break;
                    case 2:
                        secondDig2.setText(digMap.get(list.get(i)) + " with chance: " + df.format(list.get(i) * 100) + "%" + "\n");
                        break;
                    case 1:
                        secondDig3.setText(digMap.get(list.get(i)) + " with chance: " + df.format(list.get(i) * 100) + "%" + "\n");
                        break;
                    case 0:
                        secondDig4.setText(digMap.get(list.get(i)) + " with chance: " + df.format(list.get(i) * 100) + "%" + "\n");
                        break;
                }
            }
        }

        mainDig.setOnClickListener(new digOnClickListener(digMap.get(list.get(5))));
        secondDig0.setOnClickListener(new digOnClickListener(digMap.get(list.get(4))));
        secondDig1.setOnClickListener(new digOnClickListener(digMap.get(list.get(3))));
        secondDig2.setOnClickListener(new digOnClickListener(digMap.get(list.get(2))));
        secondDig3.setOnClickListener(new digOnClickListener(digMap.get(list.get(1))));
        secondDig4.setOnClickListener(new digOnClickListener(digMap.get(list.get(0))));

        if (save) {
            if (track == -2) {
                saveToHistoryDatabase(output, bitMap);
            } else {
                saveToTrackDatabase(output, bitMap);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void saveToHistoryDatabase(float[] output, Bitmap bitMap) {
        HistoryModel model = new HistoryModel();
        model.setImage(bitMap);
        String min = "", hour = "";
        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.MINUTE) < 10) min += "0";
        if (cal.get(Calendar.HOUR_OF_DAY) < 10) hour += "0";
        min += String.valueOf(cal.get(Calendar.MINUTE));
        hour += String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        model.setTime(hour + ":" + min + ", " + cal.get(Calendar.MONTH) + "/" + (cal.get(Calendar.DAY_OF_MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
        model.setResults(output);
        HistoryDatabaseClass.getDatabase(getApplicationContext()).getDao().insertData(model);
    }

    private void saveToTrackDatabase(float[] output, Bitmap bitMap) {
        TrackModel model = new TrackModel();
        model.setImage(bitMap);
        String min = "", hour = "";
        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.MINUTE) < 10) min += "0";
        if (cal.get(Calendar.HOUR_OF_DAY) < 10) hour += "0";
        min += String.valueOf(cal.get(Calendar.MINUTE));
        hour += String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        model.setTime(hour + ":" + min + ", " + cal.get(Calendar.MONTH) + "/" + (cal.get(Calendar.DAY_OF_MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
        model.setResults(output);
        if (track == -1) {
            model.setHead(true);
        } else {
            model.setHead(false);
        }
        model.setNext(-1);
        model.setName((String) getIntent().getExtras().get("name"));
        model.setPlace((String) getIntent().getExtras().get("place"));
        int newId = (int) TrackDatabaseClass.getDatabase(getApplicationContext()).getDao().insertData(model);
        TrackDatabaseClass.getDatabase(this).getDao().updateTale(newId, findTale(track));
    }

    private int findTale(int id) {
        if (id == -1) {
            return -1;
        }
        TrackModel model = TrackDatabaseClass.getDatabase(this).getDao().loadSingle(id);
        while (model.getNext() != -1) {
            model = TrackDatabaseClass.getDatabase(this).getDao().loadSingle(model.getNext());
        }
        return model.getKey();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        savedInstanceState.putBoolean(SAVE_INSTANCE, false);
        super.onRestoreInstanceState(savedInstanceState);
    }

    private class digOnClickListener implements View.OnClickListener {
        private final String name;

        public digOnClickListener(String name) {
            this.name = name;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ResultActivity.this, DetectionResultsActivity.class);
            intent.putExtra("type", name);
            startActivity(intent);
        }
    }
}