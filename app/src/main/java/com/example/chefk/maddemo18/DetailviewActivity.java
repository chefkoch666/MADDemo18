package com.example.chefk.maddemo18;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DetailviewActivity extends AppCompatActivity {

    public static final String ARG_ITEM_NAME = "itemName";

    private TextView itemNameText;
    private FloatingActionButton saveItemButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailview);
        itemNameText = findViewById(R.id.itemName);
        saveItemButton = findViewById(R.id.saveItem);

        String itemNameArg = getIntent().getStringExtra(ARG_ITEM_NAME);
        if (itemNameArg != null) {
            itemNameText.setText(itemNameArg);
        }

        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = itemNameText.getText().toString();
                saveItem(itemName);
            }
        });
    }

    protected void saveItem(String itemName) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM_NAME,itemName);
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
