package com.example.chefk.maddemo18;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.InverseBindingAdapter;
import android.databinding.ObservableField;
import android.graphics.drawable.ColorDrawable;
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

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static java.security.AccessController.getContext;

public class DetailviewActivity extends AppCompatActivity implements DetailviewActions, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private IDataItemCRUDOperationsAsync crudOperations;

    public static final String ARG_ITEM_ID = "itemId";

    private DataItem item;

    Button btnDatePicker, btnTimePicker; // TODO: remove Time Picker
    EditText txtDate, txtTime; // TODO: remove Time Picker
    CheckBox itemDoneCheckbox, itemFavoriteCheckbox;
    private String pickedDate = ""; // is used fot Date/Time picker to set the text of EditText only once

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityDetailviewBinding bindingMediator =  DataBindingUtil.setContentView(this, R.layout.activity_detailview);

        // date / time picker (https://www.journaldev.com/9976/android-date-time-picker-dialog)
        btnDatePicker = findViewById(R.id.btn_date);
        btnTimePicker = findViewById(R.id.btn_time); // TODO: remove Time Picker
        txtDate = findViewById(R.id.in_date);
        txtTime = findViewById(R.id.in_time); // TODO: remove Time Picker
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this); // TODO: remove Time Picker
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

    @InverseBindingAdapter(attribute = "android:text")
    public static String captureStringValue(EditText view) {
        String value = "";
        try {
            value = view.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
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

        if (v == btnTimePicker) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.US);
            Log.i("DetailviewActivity", "Conversion of txtDate Text is : " + txtDate.getEditableText().toString());
        }
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
