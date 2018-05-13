package com.example.chefk.maddemo18;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class OverviewActivity extends AppCompatActivity {

    //private TextView welcomeMessage;
    private ViewGroup listView;
    private FloatingActionButton createItemButton;
    private static final int CALL_EDIT_ITEM = 0;
    private static final int CALL_CREATE_ITEM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        listView = findViewById(R.id.listView);
        createItemButton = findViewById(R.id.createItem);

        for(int i=0;i<listView.getChildCount();i++) {
            TextView currentItem = (TextView)listView.getChildAt(i);
            currentItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence selectedItem = ((TextView)v).getText();
                    onListitemSelected(String.valueOf(selectedItem));
                }
            });
        }

        createItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewItem();
            }
        });

    }

    protected void onListitemSelected(String item) {
        //Toast.makeText(OverviewActivity.this, String.format(getApplicationContext().getResources().getString(R.string.popup_message),item), Toast.LENGTH_SHORT).show();
        //DetailviewActivity detailviewActivity = new DetailviewActivity(item);
        //detailviewActivity.onCreate();
        Intent callDetailviewIntent = new Intent(this,DetailviewActivity.class);
        callDetailviewIntent.putExtra(DetailviewActivity.ARG_ITEM_NAME,item);
        startActivityForResult(callDetailviewIntent,CALL_EDIT_ITEM);
    }

    protected void createNewItem() {
        Intent callDetailViewForCreateIntent = new Intent(this,DetailviewActivity.class);
        startActivityForResult(callDetailViewForCreateIntent,CALL_CREATE_ITEM);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CALL_EDIT_ITEM || requestCode == CALL_CREATE_ITEM) {
            if (resultCode == RESULT_OK) {
                String itemName = data.getStringExtra(DetailviewActivity.ARG_ITEM_NAME);
                if (requestCode == CALL_EDIT_ITEM) {
                    Snackbar.make(findViewById(R.id.contentView),"received itemName from detailview: " + itemName,Snackbar.LENGTH_INDEFINITE).show();
                }
                else {
                    Snackbar.make(findViewById(R.id.contentView),"received new itemName from detailview: " + itemName,Snackbar.LENGTH_INDEFINITE).show();
                }
            }
            else {
                Toast.makeText(OverviewActivity.this, "no itemName received from detailview", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
