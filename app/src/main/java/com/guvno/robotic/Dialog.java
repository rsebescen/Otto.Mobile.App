package com.guvno.robotic;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

//import org.springframework.http.converter.StringHttpMessageConverter;
//import org.springframework.web.client.RestTemplate;

public class Dialog extends Fragment {

    Button dialogButton;
    Button alertDialogButton;
    AlertDialog.Builder builder;

    public Dialog() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_dialog, container, false);

        builder = new AlertDialog.Builder(getContext());

        dialogButton = view.findViewById(R.id.shodDialog);
        alertDialogButton = view.findViewById(R.id.alertDialog);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        alertDialogButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CustomDialog d = new CustomDialog(getContext());
                d.show();
            }
        });

        return view;
    }

    private void showDialog() {
        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);

        builder.setCancelable(false)
                .setNegativeButton("Necu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Toast.makeText(getContext(), "you have chosen death",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle(R.string.dialog_title);
        alert.show();
    }
}
