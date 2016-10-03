package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sistem1 on 01/10/2016.
 */
public class TimerSender extends Activity implements View.OnClickListener {
    RelativeLayout back;
    int timer;
    boolean run;
    String ora,nr_carString;
    private Handler mHandler = new Handler();
    private ProgressBar progBar;
    private int mProgressStatus=0;
    TextView text;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_sender);
        initComponents();
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null) {
            try {
                timer = Integer.parseInt((String) b.get("time"));
                ora = (String) b.get("hour");
                nr_carString = (String) b.get("nr_car");
                Log.w("meniuu","hour:"+ora);
                try {
                    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                    Date d = df.parse(ora);
                    Date date2= new Date();
                    String actual_date=df.format(date2);
                    Log.w("meniuu","data_actuala:"+actual_date);
                    Date date_actual=df.parse(actual_date);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(d);
                    cal.add(Calendar.MINUTE, timer);
                    String newTime = df.format(cal.getTime());
                    Date date_plus=df.parse(newTime);


                    Log.w("meniuu","diff:"+ getDateDiff(date_actual,date_plus));
                    long diff=getDateDiff(date_plus,date_actual);
                    diff=diff/1000;
                    Log.w("meniuu","diff inainte:"+diff);
                    if(diff>0) {
                        Log.w("meniuu","diff in if:"+diff);
                        progBar.setMax(timer*60);
                        mProgressStatus=(int)diff;
                        Log.w("meniuu","start");
                        dosomething();
                    }else {
                        Intent harta = new Intent(TimerSender.this, Harta.class);
                        harta.putExtra("hour", ora);
                        harta.putExtra("nr_car", nr_carString);
                        harta.putExtra("time", timer);
                        startActivity(harta);
                        finish();
                    }
                    Log.w("meniuu","data hour:"+ora);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.w("meniuu0","cahct");
                }
            }catch (Exception e){
                e.printStackTrace();
                Log.w("meniuu","catch");
            }
        }
    }
    public void dosomething() {
        new Thread(new Runnable() {
            public void run() {

                while (mProgressStatus > 0) {
                    Log.w("meniuu","run:"+run);
                    if(run=false)
                        mProgressStatus=0;
                    mProgressStatus -= 1;
                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            progBar.setProgress(mProgressStatus);
                            int minutes=(mProgressStatus%3600)/60;
                            int sec=mProgressStatus%60;
                            if(minutes<10) {
                                if (sec < 10)
                                    text.setText("0" + minutes + ":0" + sec);
                                else
                                    text.setText("0" + minutes + ":" + sec);
                            } else {
                                if(sec<10)
                                    text.setText(minutes + ":0" + sec);
                                else
                                    text.setText(minutes + ":" + sec);
                            }
                            Log.w("meniuu","sec:"+sec+" min:"+minutes);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public static long getDateDiff(Date date1, Date date2) {
        Log.w("meniuu","date2:"+date2+" date1:"+date1);

        long diffInMillies = date1.getTime() - date2.getTime();
        return diffInMillies;
    }
    public void initComponents(){
        back=(RelativeLayout)findViewById(R.id.back_timer_sender);
        back.setOnClickListener(this);
        progBar= (ProgressBar)findViewById(R.id.progressBar);
        text = (TextView)findViewById(R.id.textView1);
    }

    /**
     * onclick method
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_timer_sender:
                finish();
                break;
        }
    }
}
