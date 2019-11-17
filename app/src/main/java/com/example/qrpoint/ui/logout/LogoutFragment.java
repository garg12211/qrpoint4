package com.example.qrpoint.ui.logout;

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

public class LogoutFragment extends Fragment {

    private LogoutViewModel logoutViewModel;
    private Button _logout;
    private Context context;
    private NavigationView navigationView;
    private SharedPreferences appData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        logoutViewModel =
                ViewModelProviders.of(this).get(LogoutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_logout, container, false);
        final TextView textView = root.findViewById(R.id.text_share);
        logoutViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        _logout = root.findViewById(R.id.btnLogout);
        context = container.getContext();
        navigationView = getActivity().findViewById(R.id.nav_view);
        appData = getActivity().getSharedPreferences("appData", context.MODE_PRIVATE);

        _logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                _logout.setEnabled(false);
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Logging out...");
                progressDialog.show();


                //progressDialog.dismiss();
                //onLoginSuccess(view);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
                                navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
                                Navigation.findNavController(view).navigate(R.id.nav_login);

                                final TextView _userName = navigationView.findViewById(R.id.userName);
                                _userName.setText("Sign in");
                                final TextView _userID = navigationView.findViewById(R.id.userID);
                                _userID.setText("");

                                SharedPreferences.Editor editor = appData.edit();
                                //editor.putBoolean("SAVE_LOGIN_DATA", checkBox.isChecked());
                                editor.putString("ID", "");
                                editor.putString("PWD", "");
                                editor.apply();
                                // onLoginFailed();
                                progressDialog.dismiss();
                            }
                        }, 3000);




            }
        });
        return root;
    }
}