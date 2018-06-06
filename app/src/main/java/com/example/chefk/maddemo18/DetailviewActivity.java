package com.example.chefk.maddemo18;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.chefk.maddemo18.databinding.ActivityDetailviewBinding;
import com.example.chefk.maddemo18.model.DataItem;
import com.example.chefk.maddemo18.model.IDataItemCRUDOperations;
import com.example.chefk.maddemo18.view.DetailviewActions;

public class DetailviewActivity extends AppCompatActivity implements DetailviewActions {

    private IDataItemCRUDOperations crudOperations;

    public static final String ARG_ITEM_ID = "itemId";

    private DataItem item;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityDetailviewBinding bindingMediator =  DataBindingUtil.setContentView(this, R.layout.activity_detailview);

        crudOperations = ((DataItemApplication)getApplication()).getCRUDOperations();

        long itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);
        if (itemId != -1) {
            this.item = crudOperations.readItem(itemId);
        }
        else {
            this.item = new DataItem();
        }

        bindingMediator.setItem(this.item);
        bindingMediator.setActions(this);
    }


    public void saveItem() {

        if (this.item.getId() == -1) {
            long itemId = crudOperations.createItem(this.item);
            this.item.setId(itemId);
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM_ID,this.item.getId());

        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
