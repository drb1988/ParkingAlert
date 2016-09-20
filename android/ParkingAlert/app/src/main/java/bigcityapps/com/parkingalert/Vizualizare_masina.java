package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * Created by fasu on 19/09/2016.
 */
public class Vizualizare_masina extends Activity implements View.OnClickListener{
    Context ctx;
    EditText tv_numele_masina, tv_nr, tv_producator, tv_model, tv_an_producti;
    RelativeLayout inapoi, salvare;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bigcityapps.com.parkingalert.R.layout.vizualizare_masina);
        initcomponents();
        ctx = this;
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            tv_numele_masina.setText((String) b.get("nume"));
            tv_nr.setText((String) b.get("nr"));
            tv_producator.setText((String) b.get("producator"));
            tv_model.setText((String) b.get("model"));
            tv_an_producti.setText((String) b.get("an"));
        }
    }
    public void initcomponents() {
        tv_numele_masina = (EditText) findViewById(bigcityapps.com.parkingalert.R.id.et_numele_masina);
        tv_nr = (EditText) findViewById(bigcityapps.com.parkingalert.R.id.et_nr);
        tv_producator = (EditText) findViewById(bigcityapps.com.parkingalert.R.id.et_producator);
        tv_model = (EditText) findViewById(bigcityapps.com.parkingalert.R.id.et_model);
        tv_an_producti = (EditText) findViewById(bigcityapps.com.parkingalert.R.id.et_an_productie);
        inapoi=(RelativeLayout)findViewById(bigcityapps.com.parkingalert.R.id.inapoi_vizualizare_masina);
        salvare=(RelativeLayout)findViewById(bigcityapps.com.parkingalert.R.id.gata_vizualizare_masina);
        inapoi.setOnClickListener(this);
        salvare.setOnClickListener(this);
    }

    public void onClick(View view) {
    switch (view.getId()){
        case bigcityapps.com.parkingalert.R.id.inapoi_vizualizare_masina:
            finish();
//    Intent inapoi= new Intent(Vizualizare_masina.this, MainActivity.class);
//        startActivity(inapoi);
        break;

        case bigcityapps.com.parkingalert.R.id.gata_vizualizare_masina:
            finish();
//        Intent salvare= new Intent(Vizualizare_masina.this, MainActivity.class);
//        startActivity(salvare);
        break;
}
    }
}
