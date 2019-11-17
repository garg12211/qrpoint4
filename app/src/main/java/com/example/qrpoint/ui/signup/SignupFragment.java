package com.example.qrpoint.ui.signup;

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

public class SignupFragment extends Fragment {
    private EditText _email;
    private EditText _usernm;
    private EditText _password;
    private EditText _cpassword;

    private TextView _info;
    private Button _signup;
    private int counter = 5;
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

    private SignupViewModel signupViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        signupViewModel =
                ViewModelProviders.of(this).get(SignupViewModel.class);
        View root = inflater.inflate(R.layout.fragment_signup, container, false);
        final TextView textView = root.findViewById(R.id.text_signup);
        signupViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        _email = root.findViewById(R.id.sEmail);
        _usernm = root.findViewById(R.id.sName);
        _password = root.findViewById(R.id.sPassword);
        _cpassword = root.findViewById(R.id.scPassword);
        _info = root.findViewById(R.id.sInfo);
        _signup = root.findViewById(R.id.btnSignup);
        context = container.getContext();
        appData = getActivity().getSharedPreferences("appData", context.MODE_PRIVATE);
        navigationView = getActivity().findViewById(R.id.nav_view);
        _info.setText("");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        _signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.d("Useremail", _email.getText().toString());
                if (!validate()) {
                    onSignupFailed();
                    return;
                }


                _signup.setEnabled(false);
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Signing up...");
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
                                    onSignupSuccess();
                                } else {
                                    onSignupFailed();
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
        String s_usesrnm = _usernm.getText().toString();
        String s_password = _password.getText().toString();
        String s_cpassword = _cpassword.getText().toString();
        boolean valid = true;

        if (s_email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(s_email).matches()) {
            _email.setError("enter a valid email address");
            //Log.d("_info", _email.getText().toString());
            valid = false;
        } else {
            _email.setError(null);
        }
        if (s_usesrnm.isEmpty() || s_usesrnm.length() < 3) {
            _usernm.setError("at least 3 characters");
            valid = false;
        } else {
            _usernm.setError(null);
        }
        if (s_password.isEmpty() || s_password.length() < 4 || s_password.length() > 15) {
            _password.setError("between 4 and 15 alphanumeric characters");
            valid = false;
        } else {
            _password.setError(null);
        }
        if (!s_password.equals(s_cpassword)) {
            _cpassword.setError("password mismatch");
            valid = false;
        } else {
            _cpassword.setError(null);
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
            String user_nm = _usernm.getText().toString();
            String pw = _password.getText().toString();
            ResultSet rs = st.executeQuery("SELECT user_id, user_nm FROM tbl_user WHERE user_id='" + user_id + "'");

            rs.last();
            if (rs.getRow() > 0) {
                _info.setText("email already exists");
                ret_val = false;
            } else {
                st.executeUpdate("INSERT INTO tbl_user (user_id, user_nm, pw) VALUES('" + user_id + "','" + user_nm + "', '" + pw + "')");
                Log.d("record", "zero");
                final TextView _userName = navigationView.findViewById(R.id.userName);
                _userName.setText(user_nm);
                final TextView _userID = navigationView.findViewById(R.id.userID);
                _userID.setText(user_id);
                ret_val = true;
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

    public void onSignupFailed() {
        Toast.makeText(context, "Signup failed", Toast.LENGTH_LONG).show();

        _signup.setEnabled(true);
    }
    public void onSignupSuccess() {
        _signup.setEnabled(true);
        SharedPreferences.Editor editor = appData.edit();
        //editor.putBoolean("SAVE_LOGIN_DATA", checkBox.isChecked());
        editor.putString("ID", _email.getText().toString().trim());
        editor.putString("PWD", _password.getText().toString().trim());
        //navController.navigate(R.id.nav_home);

        //Navigation.findNavController(view).navigate(R.id.nav_home);

    }
}