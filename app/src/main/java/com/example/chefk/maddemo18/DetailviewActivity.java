package com.example.chefk.maddemo18;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.chefk.maddemo18.model.DataItem;
import com.example.chefk.maddemo18.model.IDataItemCRUDOperations;

public class DetailviewActivity extends AppCompatActivity {

    private IDataItemCRUDOperations crudOperations;

    public static final String ARG_ITEM_ID = "itemId";

    private TextView itemNameText;
    private FloatingActionButton saveItemButton;

    private DataItem item;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailview);

        crudOperations = ((DataItemApplication)getApplication()).getCRUDOperations();

        itemNameText = findViewById(R.id.itemName);
        saveItemButton = findViewById(R.id.saveItem);

        long itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);
        if (itemId != -1) {
            this.item = crudOperations.readItem(itemId);
            itemNameText.setText(this.item.getName());
        }
        else {
            this.item = new DataItem();
        }

        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });
    }


    protected void saveItem() {

        if (this.item.getId() == -1) {
            long itemId = crudOperations.createItem(this.item);
            this.item.setId(itemId);
        }

        this.item.setName(this.itemNameText.getText().toString());

        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM_ID,this.item.getId());

        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
