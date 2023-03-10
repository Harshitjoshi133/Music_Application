package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inspector.StaticInspectionCompanionProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    Button btnplay,btnnext,btnprev,btnff,btnfr;
    TextView txtsname,txtsstart,txtstop;
    SeekBar songbar;
    String sname;
    ImageView imageView;
    Thread updateseekbar;
    public static final String extra_name="Song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mysongs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        btnplay=findViewById(R.id.playbtn);
        btnprev=findViewById(R.id.prevbtn);
        btnnext=findViewById(R.id.nextbtn);
        btnff=findViewById(R.id.ffbtn);
        btnfr=findViewById(R.id.fpbtn);
        txtsname=findViewById(R.id.txtsn);
        txtsstart=findViewById(R.id.txtstart);
        txtstop=findViewById(R.id.txtstop);
        songbar=findViewById(R.id.seekbar);
        imageView=findViewById(R.id.songimage);
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent i=getIntent();
        Bundle bundle=i.getExtras();
        mysongs=(ArrayList) bundle.getParcelableArrayList("songs");
        String songname=i.getStringExtra("songname");
        position=bundle.getInt("pos",0);
        Uri uri=Uri.parse(mysongs.get(position).toString());
        sname=mysongs.get(position).getName();
        txtsname.setText(sname);
        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
       updateseekbar=new Thread(){
           @Override
           public void run(){                                                                           //function to update seekbar
               int totalduration=mediaPlayer.getDuration();
               int currentposition=0;
               while(currentposition<totalduration){
                   try{
                       sleep(500);
                       currentposition=mediaPlayer.getCurrentPosition();
                       songbar.setProgress(currentposition);
                   }
                   catch (Exception e){
                       e.printStackTrace();
                   }
               }
           }
       };
       songbar.setMax(mediaPlayer.getDuration());
       updateseekbar.start();
       songbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {                             //when user customizes seek bar
           @Override
           public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
           }
       });
       String endtime=createtime(mediaPlayer.getDuration());
       txtstop.setText(endtime);
       final Handler handler=new Handler();
       final int delay=1000;
       handler.postDelayed(new Runnable() {
           @Override
           public void run() {
               String currenttime=createtime(mediaPlayer.getCurrentPosition());
               txtsstart.setText(currenttime);
               handler.postDelayed(this,delay);
           }
       },delay);

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override                                                                                   //handle changing of icon while playing and pausing
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                {
                    btnplay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else{
                    btnplay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();


                }
            }
        });
        btnff.setOnClickListener(new View.OnClickListener() {                                            //function for fastforward button
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });
        btnfr.setOnClickListener(new View.OnClickListener() {                                            //function for rewind button
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });
        btnnext.setOnClickListener(new View.OnClickListener() {                                            //function for next button
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position+1)%mysongs.size());
                Uri u= Uri.parse(mysongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mysongs.get(position).getName();
                txtsname.setText(sname);
                mediaPlayer.start();
                animate(imageView);
            }
        });
        btnprev.setOnClickListener(new View.OnClickListener() {                                              //function for previous button
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position-1)<0)?(mysongs.size()-1):(position-1);                                    //in case of first song
                Uri u= Uri.parse(mysongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mysongs.get(position).getName();
                txtsname.setText(sname);
                mediaPlayer.start();
                animate(imageView);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {                   //function when music is finished
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {btnnext.performClick();
            }
        });
    }
    public void animate(View view){                                                                              //function to rotate image while changing song
        ObjectAnimator animator= ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public String createtime(int duration){                              //function to convert milisecond to min:sec
        String time="";
        int min=duration/1000/60;
        int sec=duration/1000%60;
        time+=min+":";
        if(sec<10)
        {
            time+="0";
        }
        time+=sec;
        return  time;
    }

}