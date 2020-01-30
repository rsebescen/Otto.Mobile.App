package com.guvno.robotic;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Date;

public class Notifications extends Fragment {
    private Button heyBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALL_LOG}, 1);
        createNotificationChannel();
        heyBtn = view.findViewById(R.id.notify);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification("");
            }
        };
        CallReceiver.callback = new CallReceiver.FuncInterface() {
            @Override
            public void notify(String number) {
                sendNotification(number);
            }
        };
        heyBtn.setOnClickListener(onClickListener);
        return view;
    }

    private class IntentClass extends Intent {

    }

    private void sendNotification(String input) {
        long id = (new Date().getTime() - 1580334050);
        Intent fullScreenIntent = new Intent(getContext(), IntentClass.class);
//        fullScreenIntent.putExtra("NOTIFICATION_ID", (int)id);
//        fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(getContext(), 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getContext(), "MOJ KANAL")
                .setSmallIcon(R.drawable.ic_android)
                .setContentTitle("OttoBot hoce nesto da ti kaze")
                .setContentText("R2D2 metnem ti kitu " + input)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                ;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
// notificationId is a unique int for each notification that you must define
        notificationManager.notify((int)id, notification.build());

    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MOJ aKANAL";
            String description = "nistaa jos";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("asdf KANAL", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
