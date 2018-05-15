package com.example.chefk.maddemo18;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chefk.maddemo18.model.DataItem;
import com.example.chefk.maddemo18.model.IDataItemCRUDOperations;

public class OverviewActivity extends AppCompatActivity {

    private IDataItemCRUDOperations crudOperations;

    private ViewGroup listView;
    private FloatingActionButton createItemButton;

    private static final int CALL_EDIT_ITEM = 0;
    private static final int CALL_CREATE_ITEM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        this.crudOperations =  ((DataItemApplication)getApplication()).getCRUDOperations();

        listView = findViewById(R.id.listView);
        createItemButton = findViewById(R.id.createItem);

        for (DataItem item: crudOperations.readAllItems()) {
            addItemToList(item);
        }

        createItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailviewForCreate();
            }
        });

    }

    protected void addItemToList(final DataItem item) {

        ViewGroup listitemLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_overview_listitem, null);
        TextView itemNameText = listitemLayout.findViewById(R.id.itemName);
        itemNameText.setText(item.getName());

        listitemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetailviewForEdit(item);
            }
        });

        listView.addView(listitemLayout);
    }

    protected void updateItemInList(DataItem item) {
        Snackbar.make(findViewById(R.id.contentView),"received itemName from detailview: " + item.getName(),Snackbar.LENGTH_INDEFINITE).show();
    }

    protected void showDetailviewForEdit(DataItem item) {
        Intent callDetailviewIntent = new Intent(this,DetailviewActivity.class);
        callDetailviewIntent.putExtra(DetailviewActivity.ARG_ITEM_ID,item);
        startActivityForResult(callDetailviewIntent,CALL_EDIT_ITEM);
    }

    protected void showDetailviewForCreate() {
        Intent callDetailViewForCreateIntent = new Intent(this,DetailviewActivity.class);
        startActivityForResult(callDetailViewForCreateIntent,CALL_CREATE_ITEM);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CALL_EDIT_ITEM || requestCode == CALL_CREATE_ITEM) {
            if (resultCode == RESULT_OK) {
                long itemId = data.getLongExtra(DetailviewActivity.ARG_ITEM_ID, -1);
                DataItem item = (DataItem)data.getSerializableExtra(DetailviewActivity.ARG_ITEM_ID);
                if (requestCode == CALL_EDIT_ITEM) {
                    updateItemInList(item);
                }
                else {
                    addItemToList(item);
                }
            }
            else {
                Toast.makeText(OverviewActivity.this, "no itemName received from detailview", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
