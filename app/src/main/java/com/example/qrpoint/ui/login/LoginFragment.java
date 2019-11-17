package com.example.qrpoint.ui.login;
import android.content.SharedPreferences;

import androidx.navigation.NavDirections;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.NavController;
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

public class LoginFragment extends Fragment {
    private EditText _email;
    private EditText _password;
    private TextView _info;
    private Button _login;
    private int counter = 5;
    private TextView _userRegistration;
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
    private LoginViewModel loginViewModel;
    private SharedPreferences appData;
    private  NavController navController;
    private View view;
    private Menu menu=null;
    private Float totalPoint;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        loginViewModel =
                ViewModelProviders.of(this).get(LoginViewModel.class);
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        //view = inflater.inflate(R.id.drawer_layout, container, false);

        final TextView textView = root.findViewById(R.id.text_tools);
        loginViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        _email = root.findViewById(R.id.etEmail);
        _password = root.findViewById(R.id.etPassword);
        _info = root.findViewById(R.id.tvInfo);
        _login = root.findViewById(R.id.btnLogin);
        _userRegistration = root.findViewById(R.id.tvRegister);
        context = container.getContext();
        appData = getActivity().getSharedPreferences("appData", context.MODE_PRIVATE);

        navigationView = getActivity().findViewById(R.id.nav_view);


        //navigationView = getActivity().findViewById(R.id.mobile_navigation);
        //ZnavController = Navigation.findNavController(loginViewModel);
        //SharedPreferences appData = getActivity().getSharedPreferences("appData", Context.MODE_PRIVATE);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
       /*
        _login.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Log.d("Useremail", _email.getText().toString());
                validate();
            }
        });*/
        _userRegistration.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(final View view) {
                  Navigation.findNavController(view).navigate(R.id.nav_signup);
              }
        });
        _login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.d("Useremail", _email.getText().toString());
                if (!validate()) {
                    onLoginFailed();
                    return;
                }


                _login.setEnabled(false);
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();

                final boolean isSuccess = doInBackground(navigationView);

                //progressDialog.dismiss();
                //onLoginSuccess(view);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                if (isSuccess == true) {
                                    navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
                                    navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
                                    Navigation.findNavController(view).navigate(R.id.nav_home);
                                    onLoginSuccess();
                                } else {
                                    onLoginFailed();
                                }
                                //onLoginSuccess();
                                // onLoginFailed();
                                progressDialog.dismiss();
                            }
                        }, 3000);




            }
        });
        return root;
    }

    public boolean validate() {
        String s_email = _email.getText().toString();
        String s_password = _password.getText().toString();
        boolean valid = true;

        if (s_email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(s_email).matches()) {
            _email.setError("enter a valid email address");
            //Log.d("_info", _email.getText().toString());
            valid = false;
        } else {
            _email.setError(null);
        }

        if (s_password.isEmpty() || s_password.length() < 4 || s_password.length() > 15) {
            _password.setError("between 4 and 15 alphanumeric characters");
            valid = false;
        } else {
            _password.setError(null);
        }

        return valid;
    }

    protected boolean doInBackground(NavigationView navigationView) {
        boolean ret_val = false;
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(sshuser, host, portNum);
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

            String user_id = _email.getText().toString();
            String pw = _password.getText().toString();
            ResultSet rs = st.executeQuery("SELECT user_id, user_nm, pt FROM tbl_user WHERE user_id='" + user_id + "' AND pw='" + pw + "'");
            /*
            while (rs.next()) {
                result += rs.getString(1).toString() + "\n";
            }*/
            rs.last();
            if (rs.getRow() > 0) {
                String user_nm = rs.getString("user_nm");
                //String user_id = rs.getString("user_id");
                totalPoint = rs.getFloat("pt");
                final TextView _userName = navigationView.findViewById(R.id.userName);
                _userName.setText(user_nm);
                final TextView _userID = navigationView.findViewById(R.id.userID);
                _userID.setText(user_id);

                Log.d("user_nm", user_nm);
                ret_val = true;
            } else {
                Log.d("record", "zero");
            }
            //res = result;
            con.close();
            session.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            //res = e.toString();
            //con.close();
            //return false;
        }
        return ret_val;
    }

    public void onLoginFailed() {
        Toast.makeText(context, "Login failed", Toast.LENGTH_LONG).show();

        _login.setEnabled(true);
    }
    public void onLoginSuccess() {
        _login.setEnabled(true);
        SharedPreferences.Editor editor = appData.edit();
        //editor.putBoolean("SAVE_LOGIN_DATA", checkBox.isChecked());
        editor.putString("ID", _email.getText().toString().trim());
        editor.putString("PWD", _password.getText().toString().trim());
        editor.putFloat("Point", totalPoint);
        editor.apply();
        //navController.navigate(R.id.nav_home);

        //Navigation.findNavController(view).navigate(R.id.nav_home);

    }
}