package com.angelotti.triplog.View;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.angelotti.triplog.Model.Type;
import com.angelotti.triplog.Persistence.AppDatabase;
import com.angelotti.triplog.R;

public class TypeDataActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView colorIndicator;
    TextInputEditText tieType;
    TextInputEditText tieColor;
    FloatingActionButton fabSave;

    boolean validColor = false;
    boolean editing = false;

    boolean checkDiscard = true;

    Type type;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setAppTheme(this);
        setContentView(R.layout.activity_type_data);

        id = getIntent().getIntExtra(getString(R.string.const_id), -1);
        String subtitle = getIntent().getStringExtra(getString(R.string.const_title));

        toolbar = findViewById(R.id.toolbar);

        editing = id >= 0;
        if(editing){
            loadType();
            toolbar.setTitle(R.string.txt_edit_type);
            toolbar.setSubtitle(subtitle);
        }
        else{
            type = new Type();
            toolbar.setTitle(R.string.txt_type);
        }

        colorIndicator = findViewById(R.id.img_color_indicator);
        tieType = findViewById(R.id.tie_type);
        tieColor = findViewById(R.id.tie_color);
        fabSave = findViewById(R.id.fab_save);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        tieColor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 6){
                    colorIndicator.setImageDrawable(getDrawable(R.drawable.check_decagram));
                    colorIndicator.setImageTintList(new ColorStateList(new int[][]{new int[] { android.R.attr.state_enabled}}, new int[] {Color.parseColor("#" + s)}));
                    validColor = true;
                }
                else{
                    colorIndicator.setImageDrawable(getDrawable(R.drawable.alert));
                    colorIndicator.setImageTintList(new ColorStateList(new int[][]{new int[] { android.R.attr.state_enabled}}, new int[] {getColor(R.color.colorAlert)}));
                    validColor = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadType() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getDatabase(TypeDataActivity.this);
                type = database.typeDAO().getById(id);
                TypeDataActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadEditingFields();
                    }
                });
            }
        });
    }

    private void loadEditingFields() {
        tieType.setText(type.getName());
        tieColor.setText(type.getColor().substring(1));
    }

    private void save() {
        if(!saveType())
            return;
        finish();
    }

    private boolean saveType() {
        if(!testFields())
            return false;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getDatabase(TypeDataActivity.this);

                if (!editing) {
                    type = new Type(tieType.getText().toString(), "#" + tieColor.getText().toString());
                    database.typeDAO().insert(type);
                } else {
                    type.setName(tieType.getText().toString());
                    type.setColor("#" + tieColor.getText().toString());
                    database.typeDAO().update(type);
                }
            }
        });
        setResult(RESULT_OK, getIntent());
        return true;
    }

    private boolean testFields() {
        boolean passing = true;

        if(tieType.getText().toString().isEmpty()){
            tieType.setError(getString(R.string.message_obligatory_field));
            passing = false;
        }
        if(!validColor){
            tieColor.setError(getString(R.string.message_error));
            passing = false;
        }

        return passing;
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
                this.onBackPressed();
        }
        return true;
    }
}
