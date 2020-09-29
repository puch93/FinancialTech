package kr.co.core.financial_tech.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.activity.PushAct;
import kr.co.core.financial_tech.server.netUtil.NetUrls;
import kr.co.core.financial_tech.util.AppPreference;
import kr.co.core.financial_tech.util.StringUtil;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private Context ctx;

    @Override
    public void onNewToken(String token) {
        Log.e(StringUtil.TAG, "refreshed token: " + token);
        AppPreference.setPrefString(getApplicationContext(), AppPreference.PREF_FCM, token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        ctx = getApplicationContext();
        Log.e(StringUtil.TAG, "remoteMessage.getData: " + remoteMessage.getData());

        JSONObject jo = new JSONObject(remoteMessage.getData());

        String filename = StringUtil.getStr(jo, "filename");
        String idx = StringUtil.getStr(jo, "idx");
        String msg = StringUtil.getStr(jo, "msg");
        String url = StringUtil.getStr(jo, "url");
        String send = StringUtil.getStr(jo, "send");
        String type = StringUtil.getStr(jo, "type");
        String title = StringUtil.getStr(jo, "title");
        String regdate = StringUtil.getStr(jo, "regdate");
        String senddate = StringUtil.getStr(jo, "senddate");

        sendDefaultNotification(title, msg, url, filename);
    }

    private void sendDefaultNotification(String title, String message, String url, String imageUrl) {
        //매니저 설정
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //채널설정
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "재테크야놀자", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("재테크야놀자 알림설정");

            notificationManager.createNotificationChannel(channel);
        }

        //인텐트 설정
        Intent intent = null;
        if(StringUtil.isNull(url)) {
            intent = new Intent(ctx, PushAct.class);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //노티 설정
        if(StringUtil.isNull(imageUrl)) {
            Notification notification = new NotificationCompat.Builder(ctx, "default")
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentIntent(pendingIntent)
                    .build();

            //푸시 날리기
            notificationManager.notify(0, notification);
        } else {
            Bitmap bitmap = getBitmapFromURL(NetUrls.DOMAIN_ORIGIN + imageUrl);
            Notification notification = new NotificationCompat.Builder(ctx, "default")
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.app_icon)
                    .setLargeIcon(bitmap)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)
                            .bigLargeIcon(null))
                    .setContentIntent(pendingIntent)
                    .build();
            //푸시 날리기
            notificationManager.notify(0, notification);
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        Log.i(StringUtil.TAG, "getBitmapFromURL: " + src);
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
            return null;
        }
    }
}
