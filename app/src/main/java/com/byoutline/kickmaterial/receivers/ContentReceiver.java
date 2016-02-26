package com.byoutline.kickmaterial.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcelable;

import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.activities.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.listeners.OnyxBeaconsListener;
import com.onyxbeacon.listeners.OnyxCouponsListener;
import com.onyxbeacon.listeners.OnyxPushListener;
import com.onyxbeacon.listeners.OnyxTagsListener;
import com.onyxbeacon.rest.model.Coupon;
import com.onyxbeacon.service.logs.Log;
import com.onyxbeacon.service.model.Tag;
import com.onyxbeacon.service.model.web.BluemixApp;
import com.onyxbeaconservice.Beacon;
import com.onyxbeaconservice.Eddystone;
import com.onyxbeaconservice.IBeacon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mihai on 4/2/2015.
 */
public class ContentReceiver extends BroadcastReceiver {

    private OnyxBeaconsListener mOnyxBeaconListener;
    private OnyxCouponsListener mOnyxCouponsListener;
    private OnyxTagsListener mOnyxTagsListener;
    private OnyxPushListener mOnyxPushListener;
    private static ContentReceiver sInstance;

    /* Coupons */
    private static String COUPONS_TAG = "coupons_tag";

    public ContentReceiver() {}

    public static ContentReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new ContentReceiver();
            return sInstance;
        } else {
            return sInstance;
        }
    }

    public void setOnyxBeaconsListener(OnyxBeaconsListener onyxBeaconListener) {
        mOnyxBeaconListener = onyxBeaconListener;
    }

    public void setOnyxCouponsListener(OnyxCouponsListener onyxCouponsListener) {
        mOnyxCouponsListener = onyxCouponsListener;
    }

    public void setOnyxTagsListener(OnyxTagsListener onyxTagsListener){
        mOnyxTagsListener = onyxTagsListener;
    }

    public void setOnyxPushListener(OnyxPushListener onyxPushListener) {
        mOnyxPushListener = onyxPushListener;
    }

    public void onReceive(Context context, Intent intent) {
        String payloadType = intent.getStringExtra(OnyxBeaconApplication.PAYLOAD_TYPE);

        switch (payloadType) {
            case OnyxBeaconApplication.TAG_TYPE:
                ArrayList<Tag> tagsList = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_TAGS);
                if (mOnyxTagsListener != null) {
                    mOnyxTagsListener.onTagsReceived(tagsList);
                } else {
                    // In background display notification
                }
                break;
            case OnyxBeaconApplication.BEACON_TYPE:
                Log.d("TestReceiver", "Beacon received");
                ArrayList<Beacon> beacons = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_BEACONS);
                for (Beacon beacon : beacons) {
                    if (beacon instanceof IBeacon) {
                        IBeacon iBeacon = (IBeacon) beacon;
                    } else if (beacon instanceof Eddystone) {
                        Eddystone eddystone = (Eddystone) beacon;
                    }
                }
                if (mOnyxBeaconListener != null) {
                    mOnyxBeaconListener.didRangeBeaconsInRegion(beacons);
                } else {
                    // In background display notification
                }
                break;
            case OnyxBeaconApplication.COUPON_TYPE:
                Log.d("TestReceiver", "Coupon received");
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Coupon coupon = intent.getParcelableExtra(OnyxBeaconApplication.EXTRA_COUPON);
                IBeacon beacon = intent.getParcelableExtra(OnyxBeaconApplication.EXTRA_BEACON);

                if (coupon != null) {
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra("EXTRA_COUPON", (Parcelable) coupon);
                    stackBuilder.addNextIntent(i);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    long[] vibratePattern = {500, 500, 500, 500};
                    Notification.Builder builder =
                            new Notification.Builder(context)
                                    .setContentTitle(coupon.message)
                                    .setSmallIcon(R.drawable.dance)
                                    .setAutoCancel(true)
                                    .setVibrate(vibratePattern)
                                    .setLights(Color.BLACK, 500, 500)
                                    .setSound(notificationSound);

                    builder.setContentIntent(resultPendingIntent);
                    notificationManager.notify(COUPONS_TAG, 1, builder.build());


                    if (mOnyxCouponsListener != null) {
                        mOnyxCouponsListener.onCouponReceived(coupon, beacon);
                    }
                }
                break;
            case OnyxBeaconApplication.PUSH_TYPE:
                BluemixApp bluemixApp = intent.getParcelableExtra(OnyxBeaconApplication.EXTRA_BLUEMIX);
                if (mOnyxPushListener != null) {
                    mOnyxPushListener.onBluemixCredentialsReceived(bluemixApp);
                }
                break;
            case OnyxBeaconApplication.COUPONS_DELIVERED_TYPE:
                ArrayList<Coupon> deliveredCoupons = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_COUPONS);
                break;
            case OnyxBeaconApplication.WEB_REQUEST_TYPE:
                String extraInfo = intent.getStringExtra(OnyxBeaconApplication.EXTRA_INFO);
                if (extraInfo.equals(OnyxBeaconApplication.REQUEST_UNAUTHORIZED)) {
                    // Pin based session expired
                }
                break;
        }


    }
}
