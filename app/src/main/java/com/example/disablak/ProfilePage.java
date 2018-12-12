package com.example.disablak.skysofttaskone;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ProfilePage extends AppCompatActivity {

    EditText userName;
    ImageView ava;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userName = findViewById(R.id.userName);
        ava = findViewById(R.id.avatar);

        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        userName.setText(settings.getString("name", "null"));
        String urlImg = settings.getString("imageURL", "https://image.flaticon.com/icons/png/512/23/23228.png");
        Picasso.get().load(urlImg).into(ava);
    }

    public void Done(View view){
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name", userName.getText().toString());
        editor.apply();
        finish();
    }
}
