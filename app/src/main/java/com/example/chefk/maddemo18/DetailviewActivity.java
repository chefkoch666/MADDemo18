package com.example.chefk.maddemo18;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.chefk.maddemo18.databinding.ActivityDetailviewBinding;
import com.example.chefk.maddemo18.model.DataItem;
import com.example.chefk.maddemo18.model.IDataItemCRUDOperationsAsync;
import com.example.chefk.maddemo18.view.DetailviewActions;

import java.util.Calendar;

public class DetailviewActivity extends AppCompatActivity implements DetailviewActions, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private IDataItemCRUDOperationsAsync crudOperations;

    public static final String ARG_ITEM_ID = "itemId";

    private DataItem item;

    Button btnDatePicker, btnDeleteTodo;
    EditText txtDate;
    CheckBox itemDoneCheckbox, itemFavoriteCheckbox;
    private String pickedDate = ""; // is used fot Date/Time picker to set the text of EditText only once

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityDetailviewBinding bindingMediator =  DataBindingUtil.setContentView(this, R.layout.activity_detailview);

        // date / time picker (https://www.journaldev.com/9976/android-date-time-picker-dialog)
        btnDatePicker = findViewById(R.id.btn_date);
        btnDeleteTodo = findViewById(R.id.btn_delete);
        txtDate = findViewById(R.id.in_date);
        btnDatePicker.setOnClickListener(this);
        btnDeleteTodo.setOnClickListener(this);
        itemDoneCheckbox = findViewById(R.id.itemDoneCheckbox);
        itemFavoriteCheckbox = findViewById(R.id.itemFavoriteCheckBox);

        crudOperations = ((DataItemApplication)getApplication()).getCRUDOperations();

        long itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);
        if (itemId != -1) {
            crudOperations.readItem(itemId, new IDataItemCRUDOperationsAsync.ResultCallback<DataItem>() {
                @Override
                public void onresult(DataItem result) {
                    item = result;
                    doDatabinding(bindingMediator);
                    itemDoneCheckbox.setChecked(item.isDone()); // Zeile ggf wieder loeschen, funktioniert vielleicht nicht
                    itemFavoriteCheckbox.setChecked(item.isFavorite());
                }
            });
        }
        else {
            this.item = new DataItem();
        }

        bindingMediator.setItem(this.item);
        bindingMediator.setActions(this);

        itemFavoriteCheckbox.setOnCheckedChangeListener(this);

        itemDoneCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setDone(isChecked);
                crudOperations.updateItem(item.getId(), item, new IDataItemCRUDOperationsAsync.ResultCallback<Boolean>() {
                    @Override
                    public void onresult(Boolean result) {
                        Toast.makeText(DetailviewActivity.this,"Item with id " + item.getId() + " has been updates!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void doDatabinding(ActivityDetailviewBinding bindingMediator) {
        bindingMediator.setItem(item);
        bindingMediator.setActions(DetailviewActivity.this);
    }

    public void saveItem() {
        if (this.item.getId() == -1) {
            crudOperations.createItem(this.item, new IDataItemCRUDOperationsAsync.ResultCallback<Long>() {
                @Override
                public void onresult(Long result) {
                    item.setId(result);
                    returnToOverview();
                }
            });
        } else {
            crudOperations.updateItem(this.item.getId(), this.item, new IDataItemCRUDOperationsAsync.ResultCallback<Boolean>() {
                @Override
                public void onresult(Boolean result) {
                }
            });
            returnToOverview();
        }
    }

    public void deleteItem() {
            Log.i("DetailviewActivity", "Item with id : " + String.valueOf(this.item.getId()) + " has been deleted");
            crudOperations.deleteItem(this.item.getId(), new IDataItemCRUDOperationsAsync.ResultCallback<Boolean>() {
                @Override
                public void onresult(Boolean result) {
                    Toast.makeText(DetailviewActivity.this, "Item has been deleted", Toast.LENGTH_LONG).show();
                    returnToOverview();
                }
            });
    }

    public void returnToOverview() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM_ID,this.item.getId());

        setResult(RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onClick(View v) { // https://www.journaldev.com/9976/android-date-time-picker-dialog
        int mYear, mMonth, mDay, mHour, mMinute;
        if (v == btnDatePicker) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            pickedDate = pickedDate + " "  + hourOfDay + ":" + minute;
                            txtDate.setText(pickedDate);
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // is called first
                            pickedDate = (dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

        /*
        if (v == btnDeleteTodo) {
            final long itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);
            crudOperations.deleteItem(itemId, new IDataItemCRUDOperationsAsync.ResultCallback<Boolean>() {
                @Override
                public void onresult(Boolean result) {
                    Toast.makeText(DetailviewActivity.this, "Item with id : " + String.valueOf(itemId) + " has been deleted", Toast.LENGTH_LONG).show();
                }
            });
            Log.i("DetailviewActivity", "Item with id : " + String.valueOf(itemId) + " has been deleted");
            returnToOverview();
        }
        */
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == itemFavoriteCheckbox) {
            item.setFavorite(isChecked);
            crudOperations.updateItem(item.getId(), item, new IDataItemCRUDOperationsAsync.ResultCallback<Boolean>() {
                @Override
                public void onresult(Boolean result) {
                    Toast.makeText(DetailviewActivity.this, "Item with id " + item.getId() + " has changed FAVORITE status!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
