package com.guvno.robotic;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class OttoActions extends Fragment {


    private Button mDoTheDanceBtn;

    public OttoActions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        View view =  inflater.inflate(R.layout.fragment_otto_actions, container, false);

        mDoTheDanceBtn = (Button)view.findViewById(R.id.doTheDance);


        mDoTheDanceBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                doTheDance(v);
            }
        });

        return view;
    }


    private void doTheDance(View view){
        BluetoothSettingsRepository.getInstance().send("0");
        toast("Make that nigga dance!");
    }

    private void toast(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
