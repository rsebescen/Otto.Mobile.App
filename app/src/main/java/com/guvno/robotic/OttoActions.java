package com.guvno.robotic;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
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
    private Button mDance;
    AlertDialog.Builder builder;

    public OttoActions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        View view =  inflater.inflate(R.layout.fragment_otto_actions, container, false);

        mDoTheDanceBtn = view.findViewById(R.id.doTheDance);
        mDance = view.findViewById(R.id.Dance);

        mDoTheDanceBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                doTheDance();
            }
        });

        mDance.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dance();
            }
        });

        if(BluetoothSettingsRepository.getInstance().mBTSocket == null){
            mDoTheDanceBtn.setEnabled(false);
           // mDance.setEnabled(false);
        }

        return view;
    }


    private void doTheDance(){
        BluetoothSettingsRepository.getInstance().send("1");
        toast("Make that nigga dance!");
    }

    private void dance(){
        if (BluetoothSettingsRepository.getInstance().mBTSocket == null){
            showDialog();
        }
        else {
            BluetoothSettingsRepository.getInstance().send("2");
        }
    }

    private void toast(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showDialog(){
        builder = new AlertDialog.Builder(getContext());
        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage("Plavi zub iskljucen") .setTitle("nece bez plavog zuba");

        builder.setCancelable(false)
                .setNegativeButton("Plavi zub", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                     //   getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                          //      new BluetoothSettings()).commit();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle(R.string.dialog_title);
        alert.show();
    }

}
