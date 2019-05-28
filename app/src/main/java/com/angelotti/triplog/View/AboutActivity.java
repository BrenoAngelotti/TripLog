package com.angelotti.triplog.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.angelotti.triplog.R;

public class AboutActivity extends AppCompatActivity {

    Button btnLinkedin;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setAppTheme(this);
        setContentView(R.layout.activity_about);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        btnLinkedin = findViewById(R.id.btn_linkedin);
    }

    public void openLinkedin(View view){
        Uri uri = Uri.parse(getString(R.string.url_linkedin));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
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
}
