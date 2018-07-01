package com.example.chefk.maddemo18;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chefk.maddemo18.model.DataItem;
import com.example.chefk.maddemo18.model.IDataItemCRUDOperationsAsync;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


//public class OverviewActivity extends FragmentActivity implements NoticeDialogFragment.NoticeDialogListener{
public class OverviewActivity extends AppCompatActivity {

    private IDataItemCRUDOperationsAsync crudOperations;

    private List<DataItem> itemsList = new ArrayList<>();
    private ArrayAdapter<DataItem> listViewAdapter;
    private ViewGroup listView;
    private FloatingActionButton createItemButton;

    private ProgressBar progress;

    private static final int CALL_EDIT_ITEM = 0;
    private static final int CALL_CREATE_ITEM = 1;

    public enum SortMode {
        SORT_BY_ID, SORT_BY_NAME, SORT_BY_FAVORITE, SORT_BY_DATE, SORT_BY_DONE
    }

    private SortMode activeSortMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        this.crudOperations =  ((DataItemApplication)getApplication()).getCRUDOperations();

        listView = findViewById(R.id.listView);
        createItemButton = findViewById(R.id.createItem);
        progress = findViewById(R.id.progressBar);

        this.activeSortMode = SortMode.SORT_BY_DONE; //this.activeSortMode = SortMode.SORT_BY_ID;

        listViewAdapter = new ArrayAdapter<DataItem>(this, R.layout.activity_overview_listitem, itemsList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View existingView, @NonNull ViewGroup parent) {

                // obtain the data
                final DataItem item = this.getItem(position);

                ListitemViewHolder viewHolder;

                // obtain the view
                View itemView = existingView;
                if (itemView == null) {
                    Log.i("OverviewActivity", "create new view for position " + position);
                    itemView = getLayoutInflater().inflate(R.layout.activity_overview_listitem, null);
                    viewHolder = new ListitemViewHolder();
                    viewHolder.itemName = itemView.findViewById(R.id.itemName);
                    viewHolder.itemId = itemView.findViewById(R.id.itemId);
                    viewHolder.itemDone = itemView.findViewById(R.id.itemDone);
                    viewHolder.itemFavorite = itemView.findViewById(R.id.itemOverviewFavoriteCheckBox);
                    viewHolder.itemDelete = itemView.findViewById(R.id.itemDeleteButton);
                    viewHolder.itemExpiry = itemView.findViewById(R.id.itemExpiryTV);
                    itemView.setTag(viewHolder);
                } else {
                    Log.i("OverviewActivity", "recycling existing view for position " + position);
                    viewHolder = (ListitemViewHolder)itemView.getTag();
                }

                // bind the data to the view
                viewHolder.itemName.setText(item.getName());
                viewHolder.itemId.setText(String.valueOf(item.getId()));
                String shortExpiryDateAsString = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date(item.getExpiry()));

                if (item.getExpiry() == 0 || item.getExpiry() == 1) { // set design dependant on expiry status
                    Log.i("Overview-Expiry", "0 or 1 : Value of itemId : " + String.valueOf(item.getId()) + " expiry value is " + String.valueOf(item.getExpiry()));
                    viewHolder.itemExpiry.setText(R.string.expire_none);
                } else {
                    if ((item.getExpiry() > 1) && (item.getExpiry() < new Date().getTime())) { // task is overdue -> emphasize and set text
                        Log.i("Overview-Expiry", ">1 und overdue Value of itemId : " + String.valueOf(item.getId()) + " expiry value is " + String.valueOf(item.getExpiry()));
                        viewHolder.itemExpiry.setTextColor(Color.RED);
                        viewHolder.itemExpiry.setText(shortExpiryDateAsString);
                    } else { // task not expired, set normal text
                        Log.i("Overview-Expiry", "task not expired Value of itemId : " + String.valueOf(item.getId()) + " expiry value is " + String.valueOf(item.getExpiry()));
                        viewHolder.itemExpiry.setText(shortExpiryDateAsString);
                    }
                }

                viewHolder.itemDelete.setOnClickListener(null); // remove previous Listener
                viewHolder.itemDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("OverviewAct-DeleteBtn", "DeleteButton was clicked.");
                            deleteItemInList(item);
                    }
                });

                viewHolder.itemFavorite.setOnCheckedChangeListener(null); // remove previous Listener
                viewHolder.itemFavorite.setChecked(item.isFavorite());
                viewHolder.itemFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        item.setFavorite(isChecked);
                        crudOperations.updateItem(item.getId(), item, new IDataItemCRUDOperationsAsync.ResultCallback<Boolean>() {
                            @Override
                            public void onresult(Boolean result) {
                                Toast.makeText(OverviewActivity.this, "Item with id " + item.getId() + " has changed FAVORITE status!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                viewHolder.itemDone.setOnCheckedChangeListener(null); // remove previous Listener
                viewHolder.itemDone.setChecked(item.isDone());
                viewHolder.itemDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        item.setDone(isChecked);
                        crudOperations.updateItem(item.getId(), item, new IDataItemCRUDOperationsAsync.ResultCallback<Boolean>() {
                            @Override
                            public void onresult(Boolean result) {
                                Toast.makeText(OverviewActivity.this,"Item with id " + item.getId() + " updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

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
        this.sortItems();
        ((ListView)this.listView).setSelection(this.listViewAdapter.getPosition(item));
    }

    protected void updateItemInList(DataItem item) {
        Snackbar.make(findViewById(R.id.contentView),"received itemName from detailview: " + item.getName(),Snackbar.LENGTH_INDEFINITE).show();
        Log.d("Oview", item.getName() + "itemId: " + item.getId());
        this.listViewAdapter.clear(); // delete all items from list, then crudOperations.readAllItems (EXPENSIVE)
        crudOperations.readAllItems(new IDataItemCRUDOperationsAsync.ResultCallback<List<DataItem>>() {
            @Override
            public void onresult(List<DataItem> result) {
                listViewAdapter.addAll(result);
                progress.setVisibility(View.GONE);
            }
        });
    }

    protected void deleteItemInList(DataItem item) {
        Snackbar.make(findViewById(R.id.contentView),"received itemId is deleted",Snackbar.LENGTH_INDEFINITE).show();
        this.listViewAdapter.remove(item); // does nothing with "same" but different DataItem object
        crudOperations.deleteItem(item.getId(), new IDataItemCRUDOperationsAsync.ResultCallback<Boolean>() {
            @Override
            public void onresult(Boolean result) {

            }
        });
        this.listViewAdapter.clear(); // delete all items from list, then crudOperations.readAllItems (EXPENSIVE)
        crudOperations.readAllItems(new IDataItemCRUDOperationsAsync.ResultCallback<List<DataItem>>() {
            @Override
            public void onresult(List<DataItem> result) {
                listViewAdapter.addAll(result);
                progress.setVisibility(View.GONE);
            }
        });
        this.sortItems();
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
                           if (result == null) {
                               Log.i("OverviewActivity", "deleteItemInList gets called.");
                               //deleteItemInList(result); // has been deleted already via CRUD in Detailview
                           } else {
                               Log.i("OverviewActivity", "updateItemInList gets called.");
                               updateItemInList(result);
                           }
                       } else {
                           addItemToList(result);
                       }
                   }
               });
            } else {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int menuItemId = menuItem.getItemId();
        if (menuItemId == R.id.sortItemsByDate) {
            this.activeSortMode = SortMode.SORT_BY_DATE;
        }
        if (menuItemId == R.id.sortItemsByFavorite) {
            this.activeSortMode = SortMode.SORT_BY_FAVORITE;
        }
        if (menuItemId == R.id.sortItemsByDone) {
            this.activeSortMode = SortMode.SORT_BY_DONE;
        }

        this.sortItems();
        return true;
    }

    private void sortItems() {
        Log.i("OverviewActivity","sortItems(): " + this.itemsList);
        switch (this.activeSortMode) { // TODO test if it replaces the if-else below
            case SORT_BY_ID:
                listViewAdapter.sort(DataItem.SORT_BY_ID);
                break;
            case SORT_BY_NAME: listViewAdapter.sort(DataItem.SORT_BY_NAME); break;
            case SORT_BY_DONE: listViewAdapter.sort(DataItem.SORT_BY_DONE); break;
            case SORT_BY_DATE: listViewAdapter.sort(DataItem.SORT_BY_DATE); /* listViewAdapter.sort(DataItem.SORT_BY_DONE); */ break;
            case SORT_BY_FAVORITE: listViewAdapter.sort(DataItem.SORT_BY_FAVORITE); break;
            default: break;
        }
    }

    public class ListitemViewHolder {

        private DataItem item; // stretching the ViewHolder pattern

        public TextView itemName;
        public TextView itemId;
        public CheckBox itemDone;
        public CheckBox itemFavorite;
        public ImageButton itemDelete;
        public TextView itemExpiry;
    }
}
