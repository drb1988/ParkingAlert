package bigcityapps.com.parkingalert;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by fasu on 15/09/2016.
 */
public class Timer  extends Dialog implements View.OnClickListener{
    Context parentActivity;
    int min;
    RelativeLayout relative_ok;
    private ProgressBar progBar;
    private TextView text;
    private Handler mHandler = new Handler();
    private int mProgressStatus=0;

    public Timer(Context context, int minute) {
        super(context);
        this.parentActivity=context;
        this.min=minute;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bigcityapps.com.parkingalert.R.layout.timer);
        initcComponents();
        progBar.setMax(3*60);
        dosomething(3);
    }

public void initcComponents(){
    progBar= (ProgressBar)findViewById(bigcityapps.com.parkingalert.R.id.progressBar);
    text = (TextView)findViewById(bigcityapps.com.parkingalert.R.id.textView1);
    relative_ok = (RelativeLayout) findViewById(bigcityapps.com.parkingalert.R.id.relative_ok);
    relative_ok.setOnClickListener(this);

}

    public void dosomething(final int min) {

        new Thread(new Runnable() {
            public void run() {
                while (mProgressStatus < min*60) {
                    mProgressStatus += 1;
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


    public void onClick(View view) {
        switch (view.getId()){
            case bigcityapps.com.parkingalert.R.id.relative_ok:
                dismiss();
                break;
        }
    }
}
