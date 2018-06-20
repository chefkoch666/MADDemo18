package com.example.chefk.maddemo18;

import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chefk.maddemo18.model.DataItem;
import com.example.chefk.maddemo18.model.IDataItemCRUDOperations;
import com.example.chefk.maddemo18.model.IDataItemCRUDOperationsAsync;

import org.w3c.dom.Text;

import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    private IDataItemCRUDOperationsAsync crudOperations;

    private ArrayAdapter<DataItem> listViewAdapter;
    private ViewGroup listView;
    private FloatingActionButton createItemButton;

    private ProgressBar progress;

    private static final int CALL_EDIT_ITEM = 0;
    private static final int CALL_CREATE_ITEM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        this.crudOperations =  ((DataItemApplication)getApplication()).getCRUDOperations();

        listView = findViewById(R.id.listView);
        createItemButton = findViewById(R.id.createItem);
        progress = findViewById(R.id.progressBar);

        listViewAdapter = new ArrayAdapter<DataItem>(this, R.layout.activity_overview_listitem){
            @NonNull
            @Override
            public View getView(int position, @Nullable View existingView, @NonNull ViewGroup parent) {

                // obtain the data
                DataItem item = this.getItem(position);

                // obtain the view
                View itemView = existingView;
                if (itemView == null) {
                    Log.i("OverviewActivity", "create new view for position " + position);
                    itemView = getLayoutInflater().inflate(R.layout.activity_overview_listitem, null);
                } else {
                    Log.i("OverviewActivity", "recycling existing view for position " + position);
                }

                // bind the data to the view
                TextView itemNameText = itemView.findViewById(R.id.itemName);
                itemNameText.setText(item.getName());
                TextView itemIdText = itemView.findViewById(R.id.itemId);
                itemIdText.setText(String.valueOf(item.getId()));

                return itemView;
            }
        };
        listViewAdapter.setNotifyOnChange(true);
        ((ListView)listView).setAdapter(listViewAdapter);

        progress.setVisibility(View.VISIBLE);
        crudOperations.readAllItems(new IDataItemCRUDOperationsAsync.ResultCallback<List<DataItem>>() {
            @Override
            public void onresult(List<DataItem> result) {
                listViewAdapter.addAll(result);
                progress.setVisibility(View.GONE);
            }
        });

//        new AsyncTask<Void,Void,List<DataItem>>() {
//
//            @Override
//            protected void onPreExecute() {
//                progress.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            protected List<DataItem> doInBackground(Void... voids) {
//                return crudOperations.readAllItems();
//            }
//
//            @Override
//            protected void onPostExecute(List<DataItem> items) {
//                listViewAdapter.addAll(items);
//                progress.setVisibility(View.GONE);
//            }
//        }.execute();

        ((ListView) listView).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                DataItem selectedItem = listViewAdapter.getItem(position);
                showDetailviewForEdit(selectedItem);
            }
        });

        createItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailviewForCreate();
            }
        });

    }

    protected void addItemToList(DataItem item) {
        this.listViewAdapter.add(item);
        ((ListView)this.listView).setSelection(this.listViewAdapter.getPosition(item));
    }

    protected void updateItemInList(DataItem item) {
        Snackbar.make(findViewById(R.id.contentView),"received itemName from detailview: " + item.getName(),Snackbar.LENGTH_INDEFINITE).show();
    }

    protected void showDetailviewForEdit(DataItem item) {
        Intent callDetailviewIntent = new Intent(this,DetailviewActivity.class);
        callDetailviewIntent.putExtra(DetailviewActivity.ARG_ITEM_ID,item.getId());
        startActivityForResult(callDetailviewIntent,CALL_EDIT_ITEM);
    }

    protected void showDetailviewForCreate() {
        Intent callDetailViewForCreateIntent = new Intent(this,DetailviewActivity.class);
        startActivityForResult(callDetailViewForCreateIntent,CALL_CREATE_ITEM);

    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (requestCode == CALL_EDIT_ITEM || requestCode == CALL_CREATE_ITEM) {
            if (resultCode == RESULT_OK) {
                long itemId = data.getLongExtra(DetailviewActivity.ARG_ITEM_ID, -1);

               crudOperations.readItem(itemId, new IDataItemCRUDOperationsAsync.ResultCallback<DataItem>() {
                   @Override
                   public void onresult(DataItem result) {
                       if (requestCode == CALL_EDIT_ITEM) {
                           updateItemInList(result);
                       }
                       else {
                           addItemToList(result);
                       }
                   }
               });
            }
            else {
                Toast.makeText(OverviewActivity.this, "no itemName received from detailview", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* sort items functionality */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overview_optionsmenu,menu);
        return true;
    }
}
