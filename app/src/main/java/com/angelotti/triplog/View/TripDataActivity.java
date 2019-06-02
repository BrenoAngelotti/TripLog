package com.angelotti.triplog.View;

import android.support.v7.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import com.angelotti.triplog.Model.*;
import com.angelotti.triplog.Persistence.AppDatabase;
import com.angelotti.triplog.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.os.AsyncTask;

public class TripDataActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextInputEditText tieTitle;
    TextInputEditText tieDescription;
    TextInputEditText tieDate;
    DatePickerDialog tripDateDialog;
    FloatingActionButton fabSave;

    static Date tripDate;

    int id;
    boolean editing;
    Trip trip;
    ArrayList<Type> types;

    boolean checkDiscard = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setAppTheme(this);
        setContentView(R.layout.activity_trip_data);

        tieDescription = findViewById(R.id.tie_description);
        tieDate = findViewById(R.id.tie_date);
        tieTitle = findViewById(R.id.tie_name);
        fabSave = findViewById(R.id.fab_save);

        fabSave.setOnClickListener(saveClickListener);
        id = getIntent().getIntExtra(getString(R.string.const_id), -1);
        String title = getIntent().getStringExtra(getString(R.string.const_title));
        editing = id >= 0;

        toolbar = findViewById(R.id.toolbar);
        if(editing){
            loadTrip();
            toolbar.setTitle(R.string.txt_edit_trip);
            toolbar.setSubtitle(title);
        }
        else{
            trip = new Trip();
            toolbar.setTitle(getString(R.string.txt_add_trip));
        }

        loadTypes();

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        tieDate.setOnClickListener(onDateClickListener);

        Calendar now = Calendar.getInstance();
        tripDateDialog = new DatePickerDialog(this, datePickerListener, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
    }

    private void loadTypes() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getDatabase(TripDataActivity.this);
                types = new ArrayList<>(database.typeDAO().getAll());
                TripDataActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //carregar spinner
                    }
                });
            }
        });
    }

    View.OnClickListener saveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            save();
        }
    };

    private void setEditingFields() {
        tieTitle.setText(trip.getTitle());
        tieDescription.setText(trip.getDescription());
        tripDate = trip.getDate();
        tieDate.setText(new SimpleDateFormat(getString(R.string.format_date)).format(tripDate));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return true;
    }

    void save(){
        if(!saveTrip())
            return;
        finish();
    }

    void loadTrip(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getDatabase(TripDataActivity.this);
                trip = database.tripDAO().getById(id);
                trip.setType(database.typeDAO().getById(trip.getTypeId()));
                TripDataActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setEditingFields();
                    }
                });
            }
        });
    }

    boolean saveTrip(){
        if(!testFields())
            return false;
        final String title = tieTitle.getText().toString();
        final String description = tieDescription.getText().toString();
        final Date date = tripDate;
        final Type type = types.get(0);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getDatabase(TripDataActivity.this);

                if (!editing) {
                    trip = new Trip(title, description, tripDate, type);
                    int newId = (int) database.tripDAO().insert(trip);
                    trip.setId(newId);

                } else {
                    trip.setId(id);
                    trip.setTitle(title);
                    trip.setDescription(description);
                    trip.setDate(date);
                    trip.setType(type);
                    database.tripDAO().update(trip);
                }
            }
        });
        setResult(RESULT_OK, getIntent());
        return true;
    }

    boolean testFields(){
        String name = tieTitle.getText().toString();
        String description = tieDescription.getText().toString();
        String date = tieDate.getText().toString();
        boolean passing = true;

        if(name.isEmpty()){
            tieTitle.setError(getString(R.string.message_obligatory_field));
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
            View focus = getCurrentFocus();
            if(focus != null)
                focus.clearFocus();
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
