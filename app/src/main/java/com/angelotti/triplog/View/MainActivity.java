package com.angelotti.triplog.View;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.angelotti.triplog.Adapters.TripListAdapter;
import com.angelotti.triplog.Model.Trip;
import com.angelotti.triplog.Model.Type;
import com.angelotti.triplog.R;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rvTrips;
    LinearLayout llEmptyList;
    public static ArrayList<Trip> tripList;
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

        adapter = new TripListAdapter(tripList, onItemClickListener, onItemLongClickListener, this);
        rvTrips.setAdapter(adapter);

        registerForContextMenu(rvTrips);
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            position = rvTrips.getChildAdapterPosition(view);
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra(getString(R.string.const_index), position);
            startActivity(intent);
        }
    };

    private View.OnLongClickListener onItemLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            position = rvTrips.getChildAdapterPosition(view);
            openContextMenu(view);
            return true;
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(tripList.get(position).getTitle());
        getMenuInflater().inflate(R.menu.menu_trip_select, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                editTrip();
                return true;
            case R.id.action_remove:
                removeTrip();
                return true;
        }
        return super.onContextItemSelected(item);
    }

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
        if(tripList == null)
            tripList = new ArrayList<>();

        checkListSize();

        /*//Mock
        tripList.add(new Trip("Cornélio Procópio", "Estudando aqui, fazer o que né?", new Date(112, 2, 3), null));
        tripList.add(new Trip("New York City", "Mano que cidade top, não tem lugar melhor que esse na terra", new Date(110, 8, 15), new Type("City", "#FF00FF")));
        tripList.add(new Trip("São Paulo", "Véi, só quero voltar a morar lá", new Date(93, 4, 21), new Type("City", "#FF00FF")));
        */
    }

    public void addNewTrip(View view){
        Intent intent = new Intent(getApplicationContext(), TripDataActivity.class);
        startActivityForResult(intent, getResources().getInteger(R.integer.int_add_trip));
    }

    public void editTrip(){
        Intent intent = new Intent(getApplicationContext(), TripDataActivity.class);
        intent.putExtra(getString(R.string.const_index), position);
        startActivityForResult(intent, getResources().getInteger(R.integer.int_add_trip));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == getResources().getInteger(R.integer.int_add_trip) || requestCode == getResources().getInteger(R.integer.int_edit_trip)) && resultCode == RESULT_OK){
            position = data.getIntExtra(getString(R.string.const_index), -1);
            if(position >= 0)
                adapter.notifyItemChanged(position);
            else
                Toast.makeText(this, getString(R.string.message_error), Toast.LENGTH_SHORT).show();
            checkListSize();
        }
    }

    public void removeTrip(){
        tripList.remove(position);
        adapter.notifyItemRemoved(position);
        checkListSize();
    }

    void checkListSize(){
        if(tripList.size() == 0){
            rvTrips.setVisibility(View.GONE);
            llEmptyList.setVisibility(View.VISIBLE);
        }
        else{
            rvTrips.setVisibility(View.VISIBLE);
            llEmptyList.setVisibility(View.GONE);
        }
    }
}

