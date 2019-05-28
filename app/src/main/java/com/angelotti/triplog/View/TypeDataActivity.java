package com.angelotti.triplog.View;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.angelotti.triplog.R;

public class TypeDataActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView colorIndicator;
    TextInputEditText tieType;
    TextInputEditText tieColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setAppTheme(this);
        setContentView(R.layout.activity_type_data);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.txt_type);
        //toolbar.setSubtitle(trip.getType().getName());

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        colorIndicator = findViewById(R.id.img_color_indicator);
        tieType = findViewById(R.id.tie_type);
        tieColor = findViewById(R.id.tie_color);

        tieColor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 6){
                    colorIndicator.setImageDrawable(getDrawable(R.drawable.check_decagram));
                    colorIndicator.setImageTintList(new ColorStateList(new int[][]{new int[] { android.R.attr.state_enabled}}, new int[] {Color.parseColor("#" + s)}));
                }
                else{
                    colorIndicator.setImageDrawable(getDrawable(R.drawable.alert));
                    colorIndicator.setImageTintList(new ColorStateList(new int[][]{new int[] { android.R.attr.state_enabled}}, new int[] {getColor(R.color.colorAlert)}));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_type_data_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.onBackPressed();
                finish();
                return true;
            case R.id.action_save:
                this.onBackPressed();
                finish();
                Toast.makeText(this, R.string.action_save, Toast.LENGTH_LONG).show();
                return true;
        }
        return true;
    }
}
