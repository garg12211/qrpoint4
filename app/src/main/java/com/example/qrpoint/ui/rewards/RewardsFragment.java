package com.example.qrpoint.ui.rewards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.qrpoint.R;
import com.example.qrpoint.ui.home.HomeFragment;

public class RewardsFragment extends Fragment {

    private RewardsViewModel rewardsViewModel;
    private EditText firstrewardbutton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rewardsViewModel =
                ViewModelProviders.of(this).get(RewardsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_rewards, container, false);


        firstrewardbutton = root.findViewById(R.id.buyfirstrewardbutton);

        firstrewardbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment().n_totalpoint = n_totalpoint + 200;

            }
        });

        return root;
    }





}