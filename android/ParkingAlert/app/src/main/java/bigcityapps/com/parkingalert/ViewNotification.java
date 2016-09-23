package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by fasu on 22/09/2016.
 */
public class ViewNotification extends Activity {
    TextView detalii;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_notification);
        initComponents();
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            detalii.setText((String) b.get("detalii"));
        }
    }
    public void initComponents(){
        detalii=(TextView)findViewById(R.id.detalii_vizualizare_notificare);
    }
}
