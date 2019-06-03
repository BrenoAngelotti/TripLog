package com.angelotti.triplog.View;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.angelotti.triplog.Model.Trip;
import com.angelotti.triplog.Persistence.AppDatabase;
import com.angelotti.triplog.R;

import java.text.SimpleDateFormat;

public class DetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txvDescription;
    TextView txvDate;
    TextView txvType;

    int id;
    String title;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setAppTheme(this);
        setContentView(R.layout.activity_detail);

        id = getIntent().getIntExtra(getString(R.string.const_id), 0);
        title = getIntent().getStringExtra(getString(R.string.const_title));

        txvDescription = findViewById(R.id.txv_trip_description);
        txvDate = findViewById(R.id.txv_trip_date);
        txvType = findViewById(R.id.txv_type_name);

        loadTrip();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.onBackPressed();
                finish();
                return true;
        }
        return true;
    }

    void loadTrip(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getDatabase(DetailActivity.this);
                trip = database.tripDAO().getById(id);
                trip.setType(database.typeDAO().getById(trip.getTypeId()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txvDate.setText(new SimpleDateFormat(getString(R.string.format_date)).format(trip.getDate()));
                        txvDescription.setText( trip.getDescription());
                        txvType.setText(trip.getType().getName());
                        txvType.setTextColor(Color.parseColor(trip.getType().getColor()));
                    }
                });
            }
        });
    }
}
