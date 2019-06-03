package com.angelotti.triplog.View;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.angelotti.triplog.Adapters.TripListAdapter;
import com.angelotti.triplog.Model.Trip;
import com.angelotti.triplog.Persistence.AppDatabase;
import com.angelotti.triplog.R;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    BottomAppBar appBar;
    Toolbar toolbar;
    RecyclerView rvTrips;
    LinearLayout llEmptyList;
    ArrayList<Trip> tripList;
    TripListAdapter adapter;
    FloatingActionButton fab;
    int position;

    final String THEME_PREFERENCE = "THEME_PREFERENCE";

    static boolean isDarkThemeEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences(THEME_PREFERENCE, this.MODE_PRIVATE);
        isDarkThemeEnabled = sharedPreferences.getBoolean(THEME_PREFERENCE, false);
        setAppTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appBar = findViewById(R.id.app_bar);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.app_slogan);

        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab_add_new);

        rvTrips = findViewById(R.id.rvTrips);
        llEmptyList = findViewById(R.id.ll_empty_list);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvTrips.setLayoutManager(layoutManager);

        loadTrips();
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            position = rvTrips.getChildAdapterPosition(view);
            Trip trip = tripList.get(position);
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra(getString(R.string.const_id), trip.getId());
            intent.putExtra(getString(R.string.const_title), trip.getTitle());
            startActivity(intent);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(currentActionMode != null)
            currentActionMode.finish();
    }

    private View.OnLongClickListener onItemLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            position = rvTrips.getChildAdapterPosition(view);

            if (currentActionMode != null)
                return false;
            startActionMode(actionModeCallback);
            view.setSelected(true);
            return true;
        }
    };

    private ActionMode currentActionMode;
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_trip_select, menu);
            currentActionMode = mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            fab.hide();
            toolbar.getMenu().clear();
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    editTrip();
                    break;
                case R.id.action_remove:
                    removeTripVerification();
                    break;
            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            currentActionMode = null;
            fab.show();
            toolbar.inflateMenu(R.menu.menu_main_activity);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater() .inflate(R.menu.menu_main_activity, menu);
        menu.findItem(R.id.action_swap_theme).setChecked(isDarkThemeEnabled);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_swap_theme:
                item.setChecked(!isDarkThemeEnabled);
                swapTheme();
                return true;
            case R.id.action_search:
                Toast.makeText(this, getString(R.string.message_not_implemented), Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_about:
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                return true;
        }
        return true;
    }

    private void swapTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences(THEME_PREFERENCE, this.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(THEME_PREFERENCE, !sharedPreferences.getBoolean(THEME_PREFERENCE, false)).commit();
        this.recreate();
    }

    public static void setAppTheme(Activity activity){
        if(MainActivity.isDarkThemeEnabled){
            activity.setTheme(R.style.AppTheme_Dark);
        }
        else{
            activity.setTheme(R.style.AppTheme);
        }
    }

    public void loadTrips(){
        tripList = new ArrayList<>();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
              AppDatabase database = AppDatabase.getDatabase(MainActivity.this);
              tripList.addAll(database.tripDAO().getAll());
              for(Trip trip : tripList){
                  trip.setType(database.typeDAO().getById(trip.getTypeId()));
              }
              MainActivity.this.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      checkListSize();
                  }
              });
            }
        });
    }

    public void addNewTrip(View view){
        Intent intent = new Intent(getApplicationContext(), TripDataActivity.class);
        startActivityForResult(intent, getResources().getInteger(R.integer.int_add_trip));
    }

    public void editTrip(){
        Intent intent = new Intent(getApplicationContext(), TripDataActivity.class);
        intent.putExtra(getString(R.string.const_id), tripList.get(position).getId());
        intent.putExtra(getString(R.string.const_title), tripList.get(position).getTitle());
        startActivityForResult(intent, getResources().getInteger(R.integer.int_add_trip));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == getResources().getInteger(R.integer.int_add_trip) || requestCode == getResources().getInteger(R.integer.int_edit_trip)) && resultCode == RESULT_OK){
            loadTrips();
        }
    }

    public void removeTripVerification(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_remove)
                .setMessage(R.string.message_remove_trip)
                .setPositiveButton(R.string.action_remove, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeTrip();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
        builder.create().show();
    }

    private void removeTrip() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getDatabase(MainActivity.this);
                database.tripDAO().delete(tripList.get(position));
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadTrips();
                    }
                });
            }
        });
    }

    void checkListSize(){
        if(tripList.size() == 0){
            rvTrips.setVisibility(View.GONE);
            llEmptyList.setVisibility(View.VISIBLE);
        }
        else{
            adapter = new TripListAdapter(tripList, onItemClickListener, onItemLongClickListener, this);
            rvTrips.swapAdapter(adapter, false);
            rvTrips.setVisibility(View.VISIBLE);
            llEmptyList.setVisibility(View.GONE);
        }
    }
}

