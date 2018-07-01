package com.example.chefk.maddemo18;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.chefk.maddemo18.databinding.ActivityDetailviewBinding;
import com.example.chefk.maddemo18.model.DataItem;
import com.example.chefk.maddemo18.model.IDataItemCRUDOperationsAsync;
import com.example.chefk.maddemo18.view.DetailviewActions;

import java.util.Calendar;

public class DetailviewActivity extends AppCompatActivity implements DetailviewActions, /*View.OnClickListener,*/ CompoundButton.OnCheckedChangeListener {

    private IDataItemCRUDOperationsAsync crudOperations;

    public static final String ARG_ITEM_ID = "itemId";
    private static final int CALL_PICK_CONTACT = 2;
    private static final int REQUEST_PERMISSIONS = 4;

    private DataItem item;

    Button btnDeleteTodo;
    EditText txtDate;
    CheckBox itemDoneCheckbox, itemFavoriteCheckbox;
    private String pickedDate = ""; // is used fot Date/Time picker to set the text of EditText only once

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityDetailviewBinding bindingMediator =  DataBindingUtil.setContentView(this, R.layout.activity_detailview);

        // date / time picker (https://www.journaldev.com/9976/android-date-time-picker-dialog)
        btnDeleteTodo = findViewById(R.id.btn_delete);
        txtDate = findViewById(R.id.in_expiry);
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

    public void selectDate() {
        int mYear, mMonth, mDay, mHour, mMinute;
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

    public void pickContacts() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(pickContactIntent,CALL_PICK_CONTACT);
    }

    private void addContactToDataItem(Uri contactUri) {
        Log.i("Dview", "picked contact : " + contactUri);
        Cursor cursor = getContentResolver().query(contactUri,null,null,null,null);
        if (cursor.moveToFirst()) {
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.i("Dview", "got name: " + contactName);
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Log.i("Dview", "got id: " + contactId);

            int hasReadContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
            if (hasReadContactsPermission == PackageManager.PERMISSION_GRANTED) {
                Log.i("Dview", "Access to contacts granted");
            } else {
                Log.i("Dview", "Access to contacts denied!");
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},REQUEST_PERMISSIONS);
                return;
            }

            // access phone numbers
            // "number=?"
            Cursor contactsPhoneNumberCursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                    new String[]{contactId},
                    null);
            while (contactsPhoneNumberCursor.moveToNext()) {
                String currentPhoneNumber = contactsPhoneNumberCursor.getString(
                        contactsPhoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.i("Dview", "got phoneNumber: " + currentPhoneNumber);
                int currentPhoneNumberType = contactsPhoneNumberCursor.getInt(contactsPhoneNumberCursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DATA2));
                if (currentPhoneNumberType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                    Log.i("Dview", "phoneNumber is a mobile number! " + currentPhoneNumber);

                }
            }

            // access email address
            Cursor emailCursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
                    new String[]{contactId},
                    null);
            while (emailCursor.moveToNext()) {
                String currentEmailAddress = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                Log.i("Dview", "email: " + currentEmailAddress);
            }
        }
    }

    public void returnToOverview() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM_ID,this.item.getId());
        setResult(RESULT_OK,returnIntent);
        finish();
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == itemFavoriteCheckbox) {
            item.setFavorite(isChecked);
            crudOperations.updateItem(item.getId(), item, new IDataItemCRUDOperationsAsync.ResultCallback<Boolean>() {
                @Override
                public void onresult(Boolean result) {
                    Toast.makeText(DetailviewActivity.this,"Item with id " + item.getId() + " has changed FAVORITE status!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailview_optionsmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        pickContacts();
        //int menuItemId = menuItem.getItemId();
        // switch or if
        return true;
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if ((requestCode == CALL_PICK_CONTACT) && (resultCode == RESULT_OK)) {
            addContactToDataItem(data.getData());
        }
    }
}