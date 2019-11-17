package com.example.qrpoint.ui.transaction;

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

import android.widget.TableLayout;
import android.widget.TableRow;
import android.view.Gravity;
import android.graphics.Color;
import android.util.TypedValue;

public class TransactionFragment extends Fragment {

    private TransactionViewModel transactionViewModel;
    private TableLayout _tableTransactions;
    private Context context;
    private NavigationView navigationView;
    private SharedPreferences appData;
    private NavController navController;
    private View view;
    private Menu menu=null;
    private Session session;

    private static final String url = "jdbc:mysql://27.102.205.37:3306/example_2";
    private static final String user = "root";
    private static final String pass = "boriberry882";
    private static final String sshuser = "root";
    private static final String sshpass = "boriberry882";
    private static final String host = "27.102.205.37";
    private int portNum = 22;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        transactionViewModel =
                ViewModelProviders.of(this).get(TransactionViewModel.class);
        View root = inflater.inflate(R.layout.fragment_transaction, container, false);
        final TextView textView = root.findViewById(R.id.text_transaction);
        transactionViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        _tableTransactions = root.findViewById(R.id.tableTransactions);
        _tableTransactions.setStretchAllColumns(true);

        context = container.getContext();
        appData = getActivity().getSharedPreferences("appData", context.MODE_PRIVATE);

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Transactions...");
        progressDialog.show();

        final boolean isSuccess = doInBackground(navigationView);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (isSuccess == true) {
                                    /*navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
                                    navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
                                    Navigation.findNavController(view).navigate(R.id.nav_home);*/
                            //onAddSuccess();
                        } else {
                            //onAddFailed();
                        }
                        //onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);

        return root;
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

            String user_id = appData.getString("ID", null);

            ResultSet rs = st.executeQuery("SELECT user_id, pt, trns_dt FROM tbl_trns WHERE user_id='" + user_id + "' ORDER BY trns_dt DESC");

            //res = result;
            rs.last();
            int rows = rs.getRow();

            TextView textSpacer = null;
            _tableTransactions.removeAllViews();

            int leftRowMargin=15;
            int topRowMargin=10;
            int rightRowMargin=15;
            int bottomRowMargin = 0;

            int fontSize = 7;

            if (rows > 0) {
                rs.beforeFirst();
                int i = -1;
                do {
                    if (i > -1) {

                    } else {
                        textSpacer = new TextView(context);
                        textSpacer.setText("");

                    }
                    /*****************************/
                    /*User Id Column            */
                    final TextView tv = new TextView(context);
                    tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tv.setGravity(Gravity.LEFT);
                    tv.setPadding(5, 15, 0, 15);
                    if (i == -1) {
                        tv.setText("User ID");
                        tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_PT, fontSize);
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    } else {
                        tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                        tv.setText(rs.getString("user_id"));
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_PT, fontSize);
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }

                    /**************************
                     * Point Column
                     */
                    final TextView tv2 = new TextView(context);
                    tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tv2.setGravity(Gravity.LEFT);
                    tv2.setPadding(5, 15, 0, 15);
                    if (i == -1) {
                        tv2.setText("Point Earned");
                        tv2.setBackgroundColor(Color.parseColor("#f0f0f0"));
                        tv2.setTextSize(TypedValue.COMPLEX_UNIT_PT, fontSize);
                        tv2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    } else {
                        tv2.setBackgroundColor(Color.parseColor("#f8f8f8"));
                        tv2.setText(String.format ("%.1f", rs.getFloat("pt")));
                        tv2.setTextSize(TypedValue.COMPLEX_UNIT_PT, fontSize);
                        tv2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }

                    /**************************
                     * Date Column
                     */
                    final TextView tv3 = new TextView(context);
                    tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tv3.setGravity(Gravity.LEFT);
                    tv3.setPadding(5, 15, 0, 15);
                    if (i == -1) {
                        tv3.setText("Transaction Date");
                        tv3.setBackgroundColor(Color.parseColor("#f0f0f0"));
                        tv3.setTextSize(TypedValue.COMPLEX_UNIT_PT, fontSize);
                        tv3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    } else {
                        tv3.setBackgroundColor(Color.parseColor("#f8f8f8"));
                        tv3.setText(rs.getDate("trns_dt").toString());
                        tv3.setTextSize(TypedValue.COMPLEX_UNIT_PT, fontSize);
                        tv3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }

                    final TableRow tr = new TableRow(context);
                    tr.setId(i + 1);
                    TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
                    tr.setPadding(0,0,0,0);
                    tr.setLayoutParams(trParams);

                    tr.addView(tv);
                    tr.addView(tv2);
                    tr.addView(tv3);
                    _tableTransactions.addView(tr, trParams);

                    if (i > -1) {
                        final TableRow trSep = new TableRow(context);
                        TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT);
                        trParamsSep.setMargins(leftRowMargin, topRowMargin,rightRowMargin, bottomRowMargin);
                        trSep.setLayoutParams(trParamsSep);
                        TextView tvSep = new TextView(context);
                        TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT);
                        tvSepLay.span = 3;

                        tvSep.setLayoutParams(tvSepLay);
                        tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                        tvSep.setHeight(1);
                        trSep.addView(tvSep);
                        _tableTransactions.addView(trSep, trParamsSep);
                    }

                    i = i + 1;

                } while (rs.next());

            }
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

}