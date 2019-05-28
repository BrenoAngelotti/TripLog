package com.angelotti.triplog.View;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;
import com.angelotti.triplog.Model.Trip;
import com.angelotti.triplog.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TripDataActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextInputEditText tieName;
    TextInputEditText tieDescription;
    TextInputEditText tieDate;
    DatePickerDialog tripDateDialog;

    static Date tripDate;

    int index;
    boolean editing;
    Trip trip;

    boolean checkDiscard = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setAppTheme(this);
        setContentView(R.layout.activity_trip_data);

        tieDescription = findViewById(R.id.tie_description);
        tieDate = findViewById(R.id.tie_date);
        tieName = findViewById(R.id.tie_name);

        index = getIntent().getIntExtra(getString(R.string.const_index), -1);
        editing = index >= 0;

        toolbar = findViewById(R.id.toolbar);
        if(editing){
            trip = MainActivity.tripList.get(index);
            toolbar.setTitle(R.string.txt_edit_trip);
            toolbar.setSubtitle(trip.getTitle());
            setEditingFields();
        }
        else{
            trip = new Trip();
            toolbar.setTitle(getString(R.string.txt_add_trip));
        }

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        tieDate.setOnClickListener(onDateClickListener);

        Calendar now = Calendar.getInstance();
        tripDateDialog = new DatePickerDialog(this, datePickerListener, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
    }

    private void setEditingFields() {
        tieName.setText(trip.getTitle());
        tieDescription.setText(trip.getDescription());
        tripDate = trip.getDate();
        tieDate.setText(new SimpleDateFormat(getString(R.string.format_date)).format(tripDate));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_trip_data_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.onBackPressed();
                return true;
            case R.id.action_save:
                if(!saveTrip())
                    return true;
                finish();
                return true;
        }
        return true;
    }

    boolean saveTrip(){
        if(!testFields())
            return false;
        String name = tieName.getText().toString();
        String description = tieDescription.getText().toString();
        if(editing){
            trip.setTitle(name);
            trip.setDescription(description);
            trip.setDate(tripDate);
        }
        else{
            index = MainActivity.tripList.size();
            MainActivity.tripList.add(new Trip(name, description, tripDate, null));
        }
        getIntent().putExtra(getString(R.string.const_index), index);
        setResult(RESULT_OK, getIntent());
        return true;
    }

    boolean testFields(){
        String name = tieName.getText().toString();
        String description = tieDescription.getText().toString();
        String date = tieDate.getText().toString();
        boolean passing = true;

        if(name.isEmpty()){
            tieName.setError(getString(R.string.message_obligatory_field));
            passing = false;
        }
        if(description.isEmpty()){
            tieDescription.setError(getString(R.string.message_obligatory_field));
            passing = false;
        }
        if(date.isEmpty()){
            tieDate.setError(getString(R.string.message_obligatory_field));
            passing = false;
        }

        return passing;
    }

    private View.OnClickListener onDateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(editing){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(tripDate);
                tripDateDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            }
            tripDateDialog.show();
        }
    };

    @Override
    public void onBackPressed() {
        if(checkDiscard){
            showMessageDiscard();
            return;
        }
        super.onBackPressed();
    }

    void showMessageDiscard()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_discard)
                .setMessage(R.string.message_discard_changes)
                .setPositiveButton(R.string.action_discard, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkDiscard = false;
                        onBackPressed();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
        builder.create().show();
    }

    public void setDate(){
        tieDate.setError(null);
        tieDate.setText(new SimpleDateFormat(getString(R.string.format_date)).format(tripDate));
    }

    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        Calendar calendar = Calendar.getInstance();

        public void onDateSet(DatePicker view, int year, int month, int day) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            tripDate = calendar.getTime();
            setDate();
        }
    };
}
