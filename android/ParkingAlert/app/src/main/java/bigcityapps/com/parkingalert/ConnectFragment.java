package bigcityapps.com.parkingalert;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import Util.Utils;

/**
 *
 * Created by fasu on 1 19/09/2016.
 */
public class ConnectFragment extends Fragment {
    RelativeLayout notificaProprietar;
    private CoordinatorLayout coordinatorLayout;
    public ConnectFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0){
            if (!Utils.isNetworkAvailable(getContext())) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Nu exista conexiune la internet!", Snackbar.LENGTH_LONG)
                        .setDuration(Snackbar.LENGTH_INDEFINITE)
                        .setAction("SETARI", new View.OnClickListener() {
                            public void onClick(View view) {
                                startActivityForResult(new Intent(android. provider.Settings.ACTION_SETTINGS), 0);
                            }
                        });
//
//            // Changing message text color
//            snackbar.setActionTextColor(Color.RED);
//
//            // Changing action button text color
//            View sbView = snackbar.getView();
//            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//            textView.setTextColor(Color.YELLOW);

                snackbar.show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(bigcityapps.com.parkingalert.R.layout.notifica_proprietar, container, false);
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);

        notificaProprietar=(RelativeLayout) rootView.findViewById(bigcityapps.com.parkingalert.R.id.relative_notifica_proprietarul);
        notificaProprietar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (Utils.isNetworkAvailable(getContext())) {
                    Intent intent = new Intent(getActivity(), Scan.class);
                    startActivity(intent);
                }else
                {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Nu exista conexiune la internet!", Snackbar.LENGTH_LONG)
                            .setDuration(Snackbar.LENGTH_INDEFINITE)
                            .setAction("SETARI", new View.OnClickListener() {
                                public void onClick(View view) {
                                    startActivityForResult(new Intent(android. provider.Settings.ACTION_SETTINGS), 0);
                                }
                            });
                    snackbar.show();
                }

            }
        });

        if (!Utils.isNetworkAvailable(rootView.getContext())) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Nu exista conexiune la internet!", Snackbar.LENGTH_LONG)
                    .setDuration(Snackbar.LENGTH_INDEFINITE)
                    .setAction("SETARI", new View.OnClickListener() {
                        public void onClick(View view) {
                            startActivityForResult(new Intent(android. provider.Settings.ACTION_SETTINGS), 0);
                        }
                    });
//
//            // Changing message text color
//            snackbar.setActionTextColor(Color.RED);
//
//            // Changing action button text color
//            View sbView = snackbar.getView();
//            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//            textView.setTextColor(Color.YELLOW);

            snackbar.show();
        }

        return rootView;
    }
}
