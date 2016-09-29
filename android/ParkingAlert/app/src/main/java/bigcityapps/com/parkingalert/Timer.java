package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by fasu on 15/09/2016.
 */
public class Timer  extends Activity implements View.OnClickListener{
    Context parentActivity;
    int min;
    ImageView image;
    RelativeLayout relative_ok;
    private ProgressBar progBar;
    private TextView text;
    private Handler mHandler = new Handler();
    private int mProgressStatus=0;
    TextView nr_car, time;
    RelativeLayout back;
    int timer;
    String ora;
    public static long getDateDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return diffInMillies;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bigcityapps.com.parkingalert.R.layout.timer);
        initcComponents();

        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null) {
            try {
                timer = Integer.parseInt((String) b.get("time"));
                ora = (String) b.get("ora");
                String [] split= ora.split("T");
                Log.w("meniuu","ora:"+ora);
                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
                SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
                Date date = null;
                try {
                    date = format1.parse(split[1]);
                    String a= format2.format(date);

                    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                    Date d = df.parse(a);
                    Date date2= new Date();
                    String actual_date=df.format(date2);
                    Log.w("meniuu","data_actuala:"+actual_date);
                    Date date_actual=df.parse(actual_date);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(d);
                    cal.add(Calendar.MINUTE, timer);
                    String newTime = df.format(cal.getTime());
                    Date date_plus=df.parse(newTime);


                 Log.w("meniuu","diff:"+ getDateDiff(date_plus,date_actual));
                    long diff=getDateDiff(date_plus,date_actual);
                    diff=diff/1000;
                    Log.w("meniuu","diff inainte:"+diff);
                    if(diff>0) {
                        Log.w("meniuu","diff in if:"+diff);
                        progBar.setMax((int)diff);
                        mProgressStatus=(int)diff;
                        Log.w("meniuu","start");
                        dosomething();
                    }
                    Log.w("meniuu","data ora:"+a);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.w("meniuu0","cahct");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

public void initcComponents(){
    back=(RelativeLayout)findViewById(R.id.back_timer);
    back.setOnClickListener(this);
    image=(ImageView)findViewById(R.id.image);
    progBar= (ProgressBar)findViewById(R.id.progressBar);
    text = (TextView)findViewById(R.id.textView1);
    nr_car = (TextView)findViewById(R.id.nr_masina);
    time = (TextView)findViewById(R.id.time);
}

    public void dosomething() {
        new Thread(new Runnable() {
            public void run() {
                while (mProgressStatus >0) {
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
                                    text.setText("0" + minutes + ":" + sec);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_timer:

                break;
        }
    }
}
