package com.example.detector.asr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.example.detector.MainActivity;

/**
 * @author Paval Shlyk
 * @since 14/04/2024
 */
public class BroadCastReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
	this.context = context;

	String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

	if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
	    Toast.makeText(context, "Call Incoming", Toast.LENGTH_SHORT).show();

	    String incomingCallerNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

	    if (incomingCallerNumber != null) {

		Intent intent1 = new Intent(context, MainActivity.class);
		intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent1.putExtra("number", incomingCallerNumber);
		context.startActivity(intent1);

	    }
	}
    }
}
