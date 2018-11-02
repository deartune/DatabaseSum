package com.jmk.edu.databasesum;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{

    EditText editTextCountry, editTextCity, editTextPkId;
    Button buttonInsert, buttonRead, buttonUpdate, buttonDelete,buttonAddVisitedRecord,buttonSearch;
    TextView textView;
    String country;

    MyDBOpenHelper dbHelper;
    SQLiteDatabase mdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("*************   Main onCreate()");
        dbHelper = new MyDBOpenHelper(this, "jmk.db", null, 1);
        System.out.println("*************   Main onCreate()");
        mdb = dbHelper.getWritableDatabase();

        editTextCountry = findViewById(R.id.editTextCountry);
        editTextCity = findViewById(R.id.editTextCity);
        editTextPkId = findViewById(R.id.editTextPkId);

        buttonInsert = findViewById(R.id.buttonInsert);
        buttonInsert.setOnClickListener(this);

        buttonRead = findViewById(R.id.buttonRead);
        buttonRead.setOnClickListener(this);

        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(this);

        buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(this);

        buttonAddVisitedRecord = findViewById(R.id.buttonAddVisitedRecord);
        buttonAddVisitedRecord.setOnClickListener(this);

        buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(this);

        textView= findViewById(R.id.textView);

        editTextCountry.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)) {

                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });
        editTextCountry.setOnKeyListener(this);
        editTextCity.setOnKeyListener(this);
    }

    @Override
    public void onClick(View v) {

        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        switch(v.getId()) {
            case R.id.buttonInsert:
                InsertDB();
                break;
            case R.id.buttonRead:
                readDB();
                break;
            case R.id.buttonUpdate:
                UpdateDB();
                break;
            case R.id.buttonDelete:
                DeleteDB();
                break;

            case R.id.buttonAddVisitedRecord:
                String strPkId = editTextPkId.getText().toString();
                String query = "INSERT INTO jmk_country_visitedcount VALUES('" + strPkId + "')";
                mdb.execSQL(query);
                break;

            case R.id.buttonSearch:
                   String query1 = "SELECT pkid, country, capital, count(fkid) visitedTotal " +
                        "FROM jmk_country INNER JOIN jmk_country_visitedcount " +
                        "ON pkid = fkid AND pkid = '" + country + "' ";
                Cursor cursor = mdb.rawQuery(query1, null);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();

                    int visitedTotal = cursor.getInt(cursor.getColumnIndex("visitedTotal"));
                    textView.setText(String.valueOf(visitedTotal));}
        }

    }

    public void InsertDB(){

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String currentDateTimeString = format.format(new Date());


        String str = "INSERT INTO jmk_country VALUES( '"+currentDateTimeString+"', '" + editTextCountry.getText().toString() + "', '" + editTextCity.getText().toString() + " ');";
        mdb.execSQL(str);
    }



    public void readDB() {
        String query = "SELECT * FROM jmk_country ORDER BY pkid DESC";
        Cursor cursor = mdb.rawQuery(query, null);
        String str = "";

        while(cursor.moveToNext()) {


           String id = cursor.getString(0);
            String country = cursor.getString(cursor.getColumnIndex("country"));
            String city = cursor.getString(2);
            str += (id + ":" + country + "-" + city + "\n");
        }
        if(str.length()>0) {
            textView.setText(str);
        }
        else{
            Toast.makeText(getApplicationContext(), "Warning: Empty DB", Toast.LENGTH_SHORT).show();
            textView.setText("");
        }
    }

    public void UpdateDB(){
        String query = "UPDATE jmk_country SET city='"+editTextCity.getText().toString()+"' WHERE country='" + editTextCountry.getText().toString() +"'";
        mdb.execSQL(query);
    }

    public void DeleteDB(){
        String query = "DELETE FROM jmk_country WHERE country='"+editTextCountry.getText().toString()+"'";
        mdb.execSQL(query);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)) {

            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            return true;
        }
        return false;
    }
}