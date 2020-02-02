package com.guvno.robotic;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

public class CustomDialog {

    AlertDialog alert;

    public CustomDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);

        builder.setCancelable(false)
                .setNegativeButton("Necu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        alert = builder.create();
        //Setting the title manually
        alert.setTitle(R.string.dialog_title);
    }

    public void show(){
        alert.show();
    }
}
