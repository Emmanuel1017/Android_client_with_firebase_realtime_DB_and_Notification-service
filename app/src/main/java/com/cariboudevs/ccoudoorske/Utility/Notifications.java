package com.cariboudevs.ccoudoorske.Utility;

import static com.cariboudevs.ccoudoorske.Utility.helpers.ResourcesHelper.getStringResourceByKey;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.cariboudevs.ccoudoorske.Utility.helpers.BitmapHelper;

public class Notifications {
    public NotificationCompat.Builder getNotificationBuilder() {
        return builder;
    }



    public enum NotifyImportance { MIN, LOW, HIGH, MAX }

    public interface ChannelData {
        String
            ID = "notify_channel_id",
            NAME = "notify_channel_name",
            DESCRIPTION = "notify_channel_description";
    }

    private final Context context;
    private final NotificationCompat.Builder builder;

    private String channelId;
    private String channelName;
    private String channelDescription;

    private String title = "Notifications",
            content = "Notification test";

    private int id, smallIcon, oreoImportance, importance, color = -1;
    private Object largeIcon, picture = null;

    private Intent action = null;
    private long[] vibrationPattern = new long[]{0, 250, 250, 250};

    private boolean
            autoCancel = false,
            vibration = true,
            circle = false;

    private Notifications(Context _context){
        this.context = _context;
        ApplicationInfo applicationInfo = this.context.getApplicationInfo();

        /*
        * Default values:
        * */
        this.id = (int) System.currentTimeMillis();
        this.largeIcon = applicationInfo.icon;
        this.smallIcon = applicationInfo.icon;
        this.setDefaultPriority();

        try{
            this.channelId = getStringResourceByKey(context, ChannelData.ID);
        } catch (Resources.NotFoundException e){
            this.channelId = "NotifyAndroid";
        }

        try {
            this.channelName = getStringResourceByKey(context, ChannelData.NAME);
        } catch (Resources.NotFoundException e) {
            this.channelName = "NotifyAndroidChannel";
        }

        try{
            this.channelDescription = getStringResourceByKey(context, ChannelData.DESCRIPTION);
        } catch (Resources.NotFoundException e) {
            this.channelDescription = "Default notification android channel";
        }

        builder = new NotificationCompat.Builder(context, channelId);
    }

    public static Notifications build(@NonNull Context context){ return new Notifications(context); }

    public void show(){
        if (context == null) return;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager == null) return;

        builder.setAutoCancel(this.autoCancel)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content));

        /*
         * Set large icon
         * */
        Bitmap largeIconBitmap;
        if (largeIcon instanceof String) largeIconBitmap = BitmapHelper.getBitmapFromUrl(String.valueOf(largeIcon));
        else largeIconBitmap = BitmapHelper.getBitmapFromRes(this.context, (int) largeIcon);

        /*
         * Circular large icon for chat messages
         * */
        if (largeIconBitmap != null){
            if (this.circle)
                largeIconBitmap = BitmapHelper.toCircleBitmap(largeIconBitmap);
            builder.setLargeIcon(largeIconBitmap);
        }

        /*
         * Set notification color
         * */
        if(picture != null){
            Bitmap pictureBitmap;
            if (picture instanceof String) pictureBitmap = BitmapHelper.getBitmapFromUrl(String.valueOf(picture));
            else pictureBitmap = BitmapHelper.getBitmapFromRes(this.context, (int) picture);

            if (pictureBitmap != null){
                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle().bigPicture(pictureBitmap).setSummaryText(content);
                bigPictureStyle.bigLargeIcon(largeIconBitmap);
                builder.setStyle(bigPictureStyle);
            }
        }

        /*
        * Set notification color
        * */
        int realColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) realColor = color == -1 ? Color.BLACK : context.getResources().getColor(color, null);
        else realColor = color == -1 ? Color.BLACK : context.getResources().getColor(color);
        builder.setColor(realColor);

        /*
        * Oreo^ notification channels
        * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId, channelName, oreoImportance
            );
            notificationChannel.setDescription(this.channelDescription);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(realColor);
            notificationChannel.enableVibration(this.vibration);
            notificationChannel.setVibrationPattern(this.vibrationPattern);

            notificationManager.createNotificationChannel(notificationChannel);
        }else{
            builder.setPriority(this.importance);
        }

        /*
        * Set vibration pattern
        * */
        if (this.vibration) builder.setVibrate(this.vibrationPattern);
        else builder.setVibrate(new long[]{0});

        /*
        * Action triggered when user clicks noti
        * */
        if(this.action != null){
            PendingIntent pi = PendingIntent.getActivity(context, id, this.action, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(pi);
        }

        /*
        * Show built notification
        * */
        notificationManager.notify(id, builder.build());
    }

    public Notifications setTitle(@NonNull String title) {
        if (!title.isEmpty())
            this.title = title;
        return this;
    }

    public Notifications setContent(@NonNull String content) {
        if (!content.isEmpty())
            this.content = content;
        return this;
    }

    public Notifications setChannelId(@NonNull String channelId) {
        if (!channelId.isEmpty()){
            this.channelId = channelId;
            this.builder.setChannelId(channelId);
        }
        return this;
    }

    public Notifications setChannelName(@NonNull String channelName) {
        if (!channelName.isEmpty())
            this.channelName = channelName;
        return this;
    }

    public Notifications setChannelDescription(@NonNull String channelDescription) {
        if (!channelDescription.isEmpty())
            this.channelDescription = channelDescription;
        return this;
    }

    public Notifications setImportance(@NonNull NotifyImportance importance){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            switch (importance){
                case MIN:
                    this.importance = Notification.PRIORITY_LOW;
                    this.oreoImportance = NotificationManager.IMPORTANCE_MIN;
                    break;

                case LOW:
                    this.importance = Notification.PRIORITY_LOW;
                    this.oreoImportance = NotificationManager.IMPORTANCE_LOW;
                    break;

                case HIGH:
                    this.importance = Notification.PRIORITY_HIGH;
                    this.oreoImportance = NotificationManager.IMPORTANCE_HIGH;
                    break;

                case MAX:
                    this.importance = Notification.PRIORITY_MAX;
                    this.oreoImportance = NotificationManager.IMPORTANCE_MAX;
                    break;
            }
        }
        return this;
    }

    private void setDefaultPriority(){
        this.importance = Notification.PRIORITY_DEFAULT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            this.oreoImportance = NotificationManager.IMPORTANCE_DEFAULT;
    }

    public Notifications enableVibration(boolean vibration) {
        this.vibration = vibration;
        return this;
    }

    public Notifications setAutoCancel(boolean autoCancel) {
        this.autoCancel = autoCancel;
        return this;
    }

    public Notifications largeCircularIcon() {
        this.circle = true;
        return this;
    }

    public Notifications setVibrationPattern(long[] vibrationPattern) {
        this.vibrationPattern = vibrationPattern;
        return this;
    }

    public Notifications setColor(@ColorRes int color) {
        this.color = color;
        return this;
    }

    public Notifications setSmallIcon(@DrawableRes int smallIcon) {
        this.smallIcon = smallIcon;
        return this;
    }

    public Notifications setLargeIcon(@DrawableRes int largeIcon) {
        this.largeIcon = largeIcon;
        return this;
    }

    public Notifications setLargeIcon(@NonNull String largeIconUrl) {
        this.largeIcon = largeIconUrl;
        return this;
    }

    public Notifications setPicture(@DrawableRes int bigPicture) {
        this.picture = bigPicture;
        return this;
    }

    public Notifications setPicture(@NonNull String bigPictureUrl) {
        this.picture = bigPictureUrl;
        return this;
    }

    public Notifications setAction(@NonNull Intent action) {
        this.action = action;
        return this;
    }

    public Notifications setId(int id) {
        this.id = id;
        return this;
    }

    public int getId() {
        return id;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public static void cancel(@NonNull Context context, int id){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.cancel(id);
    }

    public static void cancelAll(@NonNull Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.cancelAll();
    }
}


