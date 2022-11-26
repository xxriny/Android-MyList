package com.example.MyList;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;

public class MainActivity extends AppCompatActivity {
    ArrayList<HashMap<String,String>> listData;
    ListView listView;
    SimpleAdapter simpleAdapter;
    DBHelper dbHelper ;
    SQLiteDatabase db;
    int iSelectedItem = -1;
    ActivityResultContract<Intent, ActivityResult> contract;
    ActivityResultCallback<ActivityResult> callback;
    ActivityResultLauncher<Intent> launcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        listData = new ArrayList<>();
        simpleAdapter = new SimpleAdapter(this,listData,R.layout.simple_list_item_activated_3,new String[] {"schedule", "date", "place"},
                new int[] {R.id.text1,R.id.text2, R.id.text3});
        listView.setAdapter(simpleAdapter);

        contract = new ActivityResultContracts.StartActivityForResult();
        callback = new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intentR = result.getData();
                    int iItem = intentR.getIntExtra("item",-1);
                    HashMap<String,String> hitem = new HashMap<>();
                    hitem.put("schedule",intentR.getStringExtra("schedule"));
                    hitem.put("date",intentR.getStringExtra("date"));
                    hitem.put("place",intentR.getStringExtra("place"));
                    hitem.put("detail",intentR.getStringExtra("detail"));
                    if(iItem == -1){ //추가
                        listData.add(hitem);
                    }else{ //수정
                        listData.set(iItem,hitem);
                    }simpleAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getApplicationContext(), "취소되엇습니다", Toast.LENGTH_SHORT).show();
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
                String sDetail = hitem.get("detail");
                String sDate = hitem.get("date");
                Toast.makeText(getApplicationContext(),"제목 : " + sName + "\n" + "상세정보 : " + sDetail + "날짜 : " +sDate, Toast.LENGTH_LONG).show();
            }
        });
    } //oncreate


    public void onClickAdd(View view){
        Intent intent = new Intent(this,EditActivity.class);
        intent.putExtra("item",-1);
        launcher.launch(intent);
    }

    public void onClickEdit(View view){

        if(iSelectedItem == -1){
            Toast.makeText(this,"선택된 항목이 없습니다",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this,EditActivity.class);
        intent.putExtra("item",iSelectedItem); //현재선택된항목의인덱스값넘겨줘
        HashMap<String,String> hitem = (HashMap<String, String>) simpleAdapter.getItem(iSelectedItem);
        intent.putExtra("schedule", hitem.get("schedule"));
        intent.putExtra("date", hitem.get("date"));
        intent.putExtra("place", hitem.get("place"));
        intent.putExtra("detail", hitem.get("detail"));

        launcher.launch(intent);
    }


    public void onClickDel(View view) {
    }
}