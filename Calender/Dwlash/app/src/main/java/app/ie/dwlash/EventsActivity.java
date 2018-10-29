package app.ie.dwlash;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static app.ie.dwlash.MainActivity.com_Date;


public class EventsActivity extends AppCompatActivity {


    private static final String TAG = "EventsActivity";
    DatabaseHelper mDatabaseHelper;
    private String SelectedEvent;
    private Button button_Save, button_Delete, button_Create;
    private EditText text_Event;
    private ListView mListView;
    private TextView dateView;
    private int SelectedID;
    private String SelectedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_main);


        mListView = (ListView) findViewById(R.id.list_Event);
        text_Event = (EditText) findViewById(R.id.text_Event);
        button_Create = (Button) findViewById(R.id.button_Create);
        button_Save = (Button) findViewById(R.id.button_Save);
        button_Delete = (Button) findViewById(R.id.button_Delete);
        mDatabaseHelper = new DatabaseHelper(this);
        dateView = (TextView)findViewById(R.id.dateView);
        dateView.setText(com_Date);


        button_Save.setVisibility(View.INVISIBLE);

        button_Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEntry = text_Event.getText().toString();
                String newDate = dateView.getText().toString();
                if (text_Event.length() != 0) {
                    AddData(newEntry, newDate);
                    text_Event.setText("");

                } else {
                    toastMessage("You must put something in the text field!");
                }
                populateListView();
            }

        });

        button_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = text_Event.getText().toString();
                if(!item.equals("")){
                    mDatabaseHelper.updateData(item,SelectedID,SelectedEvent);
                }else{
                    toastMessage("You must enter a name");
                }
                populateListView();
                button_Create.setVisibility(View.VISIBLE);
                button_Save.setVisibility(View.INVISIBLE);
                text_Event.setText("");
            }
        });

        button_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseHelper.deleteData(SelectedID, SelectedEvent);
                text_Event.setText("");
                toastMessage("removed from database");
                populateListView();
                button_Create.setVisibility(View.VISIBLE);
                button_Save.setVisibility(View.INVISIBLE);
            }
        });

        populateListView();

        }






    public void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");

        //get the data and append to a list
        Cursor data = mDatabaseHelper.getItemData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData.add(data.getString(1));
        }
        //create the list adapter and set the adapter
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);



        //DELETE SELECTED
        //set an onItemClickListener to the ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               button_Create.setVisibility(View.INVISIBLE);
               button_Save.setVisibility(View.VISIBLE);

                String event = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "onItemClick: You Clicked on " + event);

                Cursor data = mDatabaseHelper.getItemID(event); //get the id associated with that event
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > -1){
                    Log.d(TAG, "onItemClick: The ID is: " + itemID);
                    SelectedID = itemID;
                    SelectedEvent = event;
                    text_Event.setText(event);
                }
                else{
                    toastMessage("No ID associated with that event");
                }
            }
        });
    }

    public void AddData(String newEntry, String newDate) {
        boolean insertData = mDatabaseHelper.AddData(newEntry, newDate);

        if (insertData) {
            toastMessage("Data Successfully Inserted!");
        } else {
            toastMessage("Something went wrong");
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
