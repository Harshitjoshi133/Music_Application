package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listview;   //listview access from xml
    String [] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview=findViewById(R.id.Listviewsong);                                     //calling the listview

        runtimepermission();                                                          //function to access the run time permission




    }
    public void runtimepermission(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                displaysongs();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

    }                                                  //function to ask for runtime permission from the device

    public ArrayList<File> findsong(File file)  {                                         // function to find the songs
        ArrayList<File> arrayList=new ArrayList<>();
        File[]files=file.listFiles();
        if (files != null) {
            for(File singlefile:files) {
                if(singlefile.isDirectory() && !singlefile.isHidden()) {
                    arrayList.addAll(findsong(singlefile));   // to check songs inside directory
                }
                else{
                    if(singlefile.getName().endsWith(".mp3")||singlefile.getName().endsWith(".wav")) {
                        arrayList.add(singlefile);                                            //to get files that ends with  the .mp3 and .wav and and it to arraylist
                    }
                }
            }
        }
        return arrayList;
    }                                     //function to find the song in system
    void displaysongs()                                                                   //function to display the songs
    {
        final ArrayList<File> mysongs=findsong(Environment.getExternalStorageDirectory());       //to access songs from windows
        items= new String[mysongs.size()];
        for(int i=0;i< mysongs.size();i++)
        {
            items[i]=mysongs.get(i).getName().replace(".mp3"," ").replace(".wav"," ");
        }
        //ArrayAdapter<String> myadapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,items);

        customadapter customadapter=new customadapter();
        listview.setAdapter(customadapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songname=(String)listview.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(),PlayerActivity.class).putExtra("songs",mysongs).putExtra("songname",songname).putExtra("pos",i));
            }
        });

    }
    class customadapter extends BaseAdapter                                                        //makng the basic design  more attaractive
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myview=getLayoutInflater().inflate(R.layout.list_view,null);
            TextView mytext=myview.findViewById(R.id.songname);
            mytext.setSelected(true);
            mytext.setText(items[i]);
            return myview;
        }
    }


}