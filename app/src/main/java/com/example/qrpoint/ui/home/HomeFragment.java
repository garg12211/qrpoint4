package com.example.qrpoint.ui.home;

import android.content.SharedPreferences;

import androidx.navigation.NavDirections;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.NavController;

import com.example.qrpoint.ui.login.LoginViewModel;
import com.google.android.material.navigation.NavigationView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.os.StrictMode;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.qrpoint.R;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import android.app.ProgressDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import android.view.Menu;
import android.view.MenuItem;

public class HomeFragment extends Fragment {
    private EditText _Amt;
    private TextView _TotalPoint;
    private Button _submit;
    //private FirebaseAuth firebaseAuth;
    private static final String url = "jdbc:mysql://27.102.205.37:3306/example_2";
    private static final String user = "root";
    private static final String pass = "boriberry882";
    private static final String sshuser = "root";
    private static final String sshpass = "boriberry882";
    private static final String host = "27.102.205.37";
    private int portNum = 22;
    private static final int REQUEST_SIGNUP = 0;
    private Context context;
    private NavigationView navigationView;
    private SharedPreferences appData;
    private NavController navController;
    private View view;
    private Menu menu=null;
    private HomeViewModel homeViewModel;
    private String g_totalpoint;
    private Session session;
    private TextView _userName;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        _Amt = root.findViewById(R.id.etAmt);

        _submit = root.findViewById(R.id.btnSubmit);
        context = container.getContext();
        appData = getActivity().getSharedPreferences("appData", context.MODE_PRIVATE);

        navigationView = getActivity().findViewById(R.id.nav_view);
        _TotalPoint = root.findViewById(R.id.TotalPoint);
        Float fTotalPoint = appData.getFloat("Point", 0.0f);
        _TotalPoint.setText(String.format("%.1f", fTotalPoint));

        //Navigation.findNavController(root).navigate(R.id.nav_login);
        /*
        _userName = navigationView.findViewById(R.id.userName);
        Log.d("userName", _userName.getText().toString());
        _TotalPoint = root.findViewById(R.id.TotalPoint);
        String _sUserName = _userName.getText().toString();

        if (!_sUserName.equals("Sign in") ) {
        } else {
            Float fTotalPoint = appData.getFloat("Point", 0.0f);
            _TotalPoint.setText(String.format("%.1f", fTotalPoint));
        }

        */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        _submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!validate()) {
                    onAddFailed();
                    return;
                }
               _submit.setEnabled(false);
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Adding point...");
                progressDialog.show();

                final boolean isSuccess = doInBackground(navigationView);

                //progressDialog.dismiss();
                //onLoginSuccess(view);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                if (isSuccess == true) {
                                    /*navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
                                    navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
                                    Navigation.findNavController(view).navigate(R.id.nav_home);*/
                                    onAddSuccess();
                                } else {
                                    onAddFailed();
                                }
                                //onLoginSuccess();
                                // onLoginFailed();
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }
        });  return root;
    }

    public boolean validate() {
        String s_amt = _Amt.getText().toString();
        float n_amt = Float.valueOf(s_amt);

        boolean valid = true;

        if (s_amt.isEmpty() || n_amt == 0) {
            _Amt.setError("input amount");
            valid = false;
        }

        return valid;
    }

    protected boolean doInBackground(NavigationView navigationView) {
        boolean ret_val = false;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(sshuser, host, portNum);
            session.setPassword(sshpass);
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.connect(60000);
            session.setPortForwardingL(3366, "localhost", 3306);

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3366/example_2", user, pass);
            System.out.println("Databaseection success");

            String result = "Database Connection Successful\n";
            Statement st = con.createStatement();

            String s_amt = _Amt.getText().toString();
            String s_totalpoint = _TotalPoint.getText().toString();
            String s_merchant_nm = "Test Bubble Store";
            String user_id = appData.getString("ID", null);

            float n_amt = Float.valueOf(s_amt);
            float n_point = n_amt/10;

            float n_totalpoint = Float.valueOf(s_totalpoint);
            n_totalpoint = n_totalpoint + n_point;
            String s_n_point = String.format ("%.1f", n_point);
            String s_n_totalpoint = String.format("%.1f", n_totalpoint);

            st.executeUpdate("INSERT INTO tbl_trns (user_id, pt, trns_dt, merchant_nm) VALUES('" + user_id + "'," + s_n_point + ",NOW(), '" + s_merchant_nm + "')");
            st.executeUpdate("UPDATE tbl_user SET pt = " + s_n_totalpoint + " WHERE user_id = '" + user_id + "'");
            g_totalpoint = s_n_totalpoint;
            //res = result;
            con.close();
            session.disconnect();
            ret_val = true;

        } catch (Exception e) {
            e.printStackTrace();
            session.disconnect();
            //res = e.toString();
            //con.close();
            //return false;
        }
        return ret_val;
    }

    public void onAddFailed() {
        Toast.makeText(context, "Add failed", Toast.LENGTH_LONG).show();

        _submit.setEnabled(true);
    }
    public void onAddSuccess() {
        _TotalPoint.setText(g_totalpoint);
        SharedPreferences.Editor editor = appData.edit();
        editor.putFloat("Point", Float.parseFloat(g_totalpoint));
        editor.apply();
        _submit.setEnabled(true);

    }
}