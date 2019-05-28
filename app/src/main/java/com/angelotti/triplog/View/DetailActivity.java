package com.angelotti.triplog.View;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.angelotti.triplog.Model.Trip;
import com.angelotti.triplog.R;

import java.text.SimpleDateFormat;

public class DetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txvDescription;
    TextView txvDate;

    int index;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setAppTheme(this);
        setContentView(R.layout.activity_detail);

        index = getIntent().getIntExtra(getString(R.string.const_index), 0);

        txvDescription = findViewById(R.id.txv_trip_description);
        txvDate = findViewById(R.id.txv_trip_date);
        loadTrip();

        txvDate.setText(new SimpleDateFormat(getString(R.string.format_date)).format(trip.getDate()));
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(trip.getTitle());
        if(trip.getType() != null)
            toolbar.setSubtitle(trip.getType().getName());

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        txvDescription.setText( trip.getDescription());
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

    public void loadTrip(){
        trip = MainActivity.tripList.get(index);
        //trip = new Trip("New York City", "Mano que cidade top da porra, vai se fude não tem lugar melhor que esse na terra", new Date(110, 8, 15), new Type("City", "#ff00ff"));
    }
}
