package com.angelotti.triplog.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.angelotti.triplog.Adapters.TypeListAdapter;
import com.angelotti.triplog.Model.Type;
import com.angelotti.triplog.Persistence.AppDatabase;
import com.angelotti.triplog.R;

import java.util.ArrayList;

public class TypesActivity extends AppCompatActivity {
    BottomAppBar appBar;
    Toolbar toolbar;
    RecyclerView rvTypes;
    ArrayList<Type> typeList;
    TypeListAdapter adapter;
    FloatingActionButton fab;
    LinearLayout llEmptyList;

    int position;

    boolean editedType = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setAppTheme(this);
        setContentView(R.layout.activity_types);

        appBar = findViewById(R.id.app_bar);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.txt_types);

        setSupportActionBar(toolbar);

        llEmptyList = findViewById(R.id.ll_empty_list);
        fab = findViewById(R.id.fab_add_new);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TypeDataActivity.class);
                startActivityForResult(intent, getResources().getInteger(R.integer.int_add_type));
            }
        });

        rvTypes = findViewById(R.id.rvTypes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTypes.setLayoutManager(layoutManager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        registerForContextMenu(rvTypes);

        loadTypes();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == getResources().getInteger(R.integer.int_edit_type) && resultCode == RESULT_OK){
            loadTypes();
            setResult(RESULT_OK);
        }
        if(requestCode == getResources().getInteger(R.integer.int_add_type) && resultCode == RESULT_OK){
            loadTypes();
        }
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

    @Override
    public void onBackPressed() {
        if(editedType){
            setResult(RESULT_OK, getIntent());
        }
        super.onBackPressed();
    }

    private void loadTypes() {
        typeList = new ArrayList<>();
        AppDatabase database = AppDatabase.getDatabase(TypesActivity.this);
        typeList.addAll(database.typeDAO().getAll());
        adapter = new TypeListAdapter(typeList, onItemClickListener, onItemLongClickListener, this);
        rvTypes.swapAdapter(adapter, false);
        if(typeList.size() > 0){
            llEmptyList.setVisibility(View.GONE);
            rvTypes.setVisibility(View.VISIBLE);
        }
        else{
            llEmptyList.setVisibility(View.VISIBLE);
            rvTypes.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            position = rvTypes.getChildAdapterPosition(view);
            edit();
        }
    };

    private View.OnLongClickListener onItemLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            position = rvTypes.getChildAdapterPosition(view);
            return false;
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);

        getMenuInflater().inflate(R.menu.menu_types_activity, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_remove:
                removeTypeVerification();
                return false;
            case R.id.action_edit:
                edit();
                return false;
        }
        return true;
    }

    public void removeTypeVerification(){
        Type type = typeList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_remove)
                .setMessage(type.getName())
                .setPositiveButton(R.string.action_remove, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeType();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
        builder.create().show();
    }

    private void removeType() {
        Type type = typeList.get(position);
        AppDatabase database = AppDatabase.getDatabase(TypesActivity.this);
        int trips = database.tripDAO().getByType(type.getId()).size();

        if(trips > 0){
            Toast.makeText(this, R.string.message_cant_remove_type, Toast.LENGTH_SHORT).show();
            return;
        }
        database.typeDAO().delete(type);
        loadTypes();
    }

    private void edit() {
        Intent intent = new Intent(getApplicationContext(), TypeDataActivity.class);
        intent.putExtra(getString(R.string.const_id), typeList.get(position).getId());
        intent.putExtra(getString(R.string.const_title), typeList.get(position).getName());
        startActivityForResult(intent, getResources().getInteger(R.integer.int_edit_type));
    }
}
