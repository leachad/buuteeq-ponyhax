package NetworkAndPower;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

/**
 * Created by leachad on 5/29/2015.
 * Static class used to obtain pertinent information regarding the
 * state of the Battery Levels and the Users network connectivity.
 *
 */
public class DeviceMonitor {

    public static BatteryLevel getBatteryLevel(Context theAppContext) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = theAppContext.registerReceiver(null, filter);
        int level = 0;
        int scale = 0;
        if (batteryStatus != null) {
            level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }
        Log.w("Device BattScale", Float.toString(level / (float) scale));
        return getGeneralLevel(level / (float) scale);
    }

    private static BatteryLevel getGeneralLevel(float thePercentage) {
        BatteryLevel current = null;
        if (thePercentage >= 0 && thePercentage < .20) {
            current = BatteryLevel.LOW;
        } else if (thePercentage >= .20 && thePercentage < .40) {
            current = BatteryLevel.MID_LOW;
        } else if (thePercentage >= .40 && thePercentage < .60) {
            current = BatteryLevel.MID;
        } else if (thePercentage >= .60 && thePercentage < .80) {
            current = BatteryLevel.MID_HIGH;
        } else if (thePercentage >= .80 && thePercentage < .100) {
            current = BatteryLevel.FULL;
        }
        return  current;
    }












    public enum BatteryLevel {
        LOW, MID_LOW, MID, MID_HIGH, FULL;
    }
}
