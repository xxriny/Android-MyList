package com.example.MyList;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EditActivityD extends AppCompatActivity {
    EditText editTitle, editDate, editPlace, editDetail;
    int iItem = -1;
    int iID = 0;
    DatePickerDialog datePickerDialog;
    DBHelper dbHelper ;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        //달력
        Button datePickerbtn = findViewById(R.id.buttonCal);

        datePickerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int pYear = calendar.get(Calendar.YEAR);
                int pMonth = calendar.get(Calendar.MONTH);
                int pDate = calendar.get(Calendar.DATE);

                datePickerDialog = new DatePickerDialog(EditActivityD.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month+1;
                        String date = year +"/" + month +"/" + day;
                        editDate.setText(date);
                    }
                }, pYear, pMonth, pDate);
                datePickerDialog.show();

            }
        });
        editTitle = findViewById(R.id.editTextName);
        editDate = findViewById(R.id.editTextDate);
        editPlace = findViewById(R.id.editTextPlace);
        editDetail = findViewById(R.id.editTextDetail);

        dbHelper = new DBHelper(EditActivityD.this); 
        
        
        Intent intentR = getIntent();
        iItem = intentR.getIntExtra("item",-1);
        iID = intentR.getIntExtra("id",0);

        if(iItem != -1){
            editTitle.setText(intentR.getStringExtra("schedule"));
            editDate.setText(intentR.getStringExtra("date"));
            editPlace.setText(intentR.getStringExtra("place"));
            editDetail.setText(intentR.getStringExtra("detail"));
        }
        Button btnCancel = findViewById(R.id.buttonCancel);
        Button btnSave = findViewById(R.id.buttonSave);
        //취소버튼
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //저장버튼
        btnSave.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String sName = editTitle.getText().toString().trim();
                String sDate = editDate.getText().toString().trim();
                String sPlace = editPlace.getText().toString().trim();
                String sDetail = editDetail.getText().toString().trim();
                
                if(sName.isEmpty() || sDate.isEmpty() || sPlace.isEmpty() ){
                    Toast.makeText(getApplicationContext(),"입력을 해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

                db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery(DBContract.SQL_SELECT_ID, new String[] {sName,sDate});

                if(cursor.getCount() != 0 ){
                    cursor.moveToNext();
                    if(iItem == -1){
                        Toast.makeText(getApplicationContext(),"중복된 항목이 있습니다",Toast.LENGTH_LONG).show();
                        return;
                    } else if(iID != cursor.getInt(0)){
                        Toast.makeText(getApplicationContext(),"중복된 항목이 있습니다",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                
                Intent intent = new Intent();
                intent.putExtra("schedule",sName);
                intent.putExtra("date",sDate);
                intent.putExtra("place",sPlace);
                intent.putExtra("detail",sDetail);
                intent.putExtra("item",iItem);
                intent.putExtra("id",iID);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }//on.

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dbHelper != null)
            dbHelper.close();
    }
}