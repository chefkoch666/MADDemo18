package com.example.chefk.maddemo18.model;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class binderDatePicker {
    @BindingAdapter(value = "realValueAttrChanged")
    public static void setListener(EditText editText, final InverseBindingListener listener) {
        if (listener != null) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    listener.onChange();
                }
            });
        }
    }

    @BindingAdapter("realValue")
    public static void setRealValue(EditText view, long value) {
        //if (!view.getEditableText().equals() ) {
            //String dateString = new SimpleDateFormat("dd/MM/yyyy").format(new Date(value));
            String dateString = new SimpleDateFormat("dd/MM/yyyy hh:mm").format(new Date(value));
            view.setText(dateString);
            //view.setText(String.valueOf(value));
        //}
    }

    @InverseBindingAdapter(attribute = "realValue")
    public static long getRealValue(EditText editText) {
        // TODO: make setText change only once after time has been picked
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.US);
        //SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        try {
            Date dateFromString = formatter.parse(editText.getEditableText().toString());
            Log.i("DetailviewActivity", "Conversion success (maybe)");
            return dateFromString.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }
}
