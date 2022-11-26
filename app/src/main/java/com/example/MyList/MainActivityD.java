package com.example.MyList;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class MainActivityD extends AppCompatActivity {
    ArrayList<HashMap<String,String>> listData;
    ListView listView;
    SimpleAdapter simpleAdapter;
    TextView text;
    int iSelectedItem = -1;
    int iSelectedID = 0;
    int iMaxID = 0;
    private TextToSpeech tts;
    DBHelper dbHelper;
    SQLiteDatabase db;

    ActivityResultContract<Intent, ActivityResult> contract;
    ActivityResultCallback<ActivityResult> callback;
    ActivityResultLauncher<Intent> launcher;

    private void loadTable(){
        listData.clear();
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(DBContract.SQL_LOAD,null);
        while (cursor.moveToNext()){
            int nID = cursor.getInt(0);
            HashMap<String,String> hitem = new HashMap<>();

            hitem.put("schedule", cursor.getString(1));
            hitem.put("date", cursor.getString(2));
            hitem.put("place",cursor.getString(3));
            hitem.put("detail",cursor.getString(4));
            hitem.put("id",String.valueOf(nID));
            listData.add(hitem);
            iMaxID = Math.max(iMaxID,nID);
        }
         simpleAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        listView = findViewById(R.id.listView);
        listData = new ArrayList<>();
        simpleAdapter = new SimpleAdapter(this,listData,R.layout.simple_list_item_activated_3,new String[] {"schedule", "date", "place"},
                new int[] {R.id.text1,R.id.text2, R.id.text3});
        listView.setAdapter(simpleAdapter);

        dbHelper = new DBHelper(getApplicationContext());
        loadTable();
        
        contract = new ActivityResultContracts.StartActivityForResult();
        callback = new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intentR = result.getData();
                    int iItem = intentR.getIntExtra("item",-1);
                    int iID = intentR.getIntExtra("id",0);
                    HashMap<String,String> hitem = new HashMap<>();
                    hitem.put("schedule",intentR.getStringExtra("schedule"));
                    hitem.put("date",intentR.getStringExtra("date"));
                    hitem.put("place",intentR.getStringExtra("place"));
                    hitem.put("detail",intentR.getStringExtra("detail"));
                    ContentValues values = new ContentValues();
                    values.put("schedule",intentR.getStringExtra("schedule"));
                    values.put(DBContract.COL_WHEN,intentR.getStringExtra("date"));
                    values.put(DBContract.COL_WHERE, intentR.getStringExtra("place"));
                    values.put(DBContract.COL_DETAIL,intentR.getStringExtra("detail"));


                    if(iItem == -1){
                        iMaxID++;
                        hitem.put("id",String.valueOf(iMaxID));
                        values.put(DBContract.COL_ID, String.valueOf(iMaxID));
                        listData.add(hitem);
                        db = dbHelper.getWritableDatabase();
                        db.insert(DBContract.TABLE_NAME, null, values);
                    }else{
                        hitem.put("id",String.valueOf(iID));
                        listData.set(iItem,hitem);
                        db = dbHelper.getWritableDatabase();
                        db.update(DBContract.TABLE_NAME, values, "id = ?", new String[] {String.valueOf(iID)});
                    }simpleAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getApplicationContext(), "취소되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
        };
        launcher = registerForActivityResult(contract,callback);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                iSelectedItem = i;

                HashMap<String,String> hitem = (HashMap<String, String>) simpleAdapter.getItem(iSelectedItem);
                String sName = hitem.get("schedule");
                String sPlace = hitem.get("place");
                String sDetail = hitem.get("detail");
                String sDate = hitem.get("date");
                Toast.makeText(getApplicationContext(),"제목 : " + sName + "\n"+ "날짜 : " +sDate + "\n" + "장소 : " +sPlace + "\n" + "상세정보 : " + sDetail, Toast.LENGTH_LONG).show();

                iSelectedID =   Integer.parseInt(hitem.get("id"));
            }
        });

        Button btnInfo = findViewById(R.id.buttonInfo);
        btnInfo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(iSelectedItem== -1){
                    Toast.makeText(getApplicationContext(),"선택된 항목이 없습니다.",Toast.LENGTH_LONG).show();

                }
                else{
                    HashMap<String,String> hitem = (HashMap<String, String>) simpleAdapter.getItem(iSelectedItem);
                    String sName = hitem.get("schedule");
                    String sDetail = hitem.get("detail");
                    String sPlace = hitem.get("place");
                    String sDate = hitem.get("date");
                    tts.speak("제목\n"+sName+"\n날짜\n"+sDate+"\n장소\n"+sPlace+"\n내용\n"+sDetail,TextToSpeech.QUEUE_FLUSH, null, "lTTS");
                    Toast.makeText(getApplicationContext(),"음성으로 듣는중..",Toast.LENGTH_LONG).show();

                }
            }
        });

    } //oncreate

    @Override public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        super.onDestroy();
    }
    //삭제
    public void onClickDel(View view){
        int check, count = simpleAdapter.getCount();
        if (iSelectedItem == -1) {
            Toast.makeText(this, "선택된 항목이 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        if(count > 0){
            check = listView.getCheckedItemPosition();
            if(check>-1 && check < count){
                listData.remove(check);
                listView.clearChoices();
                simpleAdapter.notifyDataSetChanged();
                db.execSQL("DELETE FROM SCHEDULE_T WHERE ID="+ iSelectedID);
                Toast.makeText(this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void onClickAdd(View view){
        Intent intent = new Intent(this,EditActivityD.class);
        intent.putExtra("item",-1);
        intent.putExtra("id",0);
        launcher.launch(intent);
    }
    //수정
    public void onClickEdit(View view){

        if(iSelectedItem == -1){
            Toast.makeText(this,"선택된 항목이 없습니다",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this,EditActivityD.class);
        intent.putExtra("item",iSelectedItem);
        intent.putExtra("id",iSelectedID);
        HashMap<String,String> hitem = (HashMap<String, String>) simpleAdapter.getItem(iSelectedItem);
        intent.putExtra("schedule", hitem.get("schedule"));
        intent.putExtra("date", hitem.get("date"));
        intent.putExtra("place", hitem.get("place"));
        intent.putExtra("detail", hitem.get("detail"));

        launcher.launch(intent);
    }

}