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

    /** Private variables used to determine state of the battery level.*/
    private static final float EMPTY = (float)0.0;
    private static final float TWENTY_PERCENT = (float)0.20;
    private static final float FORTY_PERCENT = (float)0.20;
    private static final float SIXTY_PERCENT = (float)0.20;
    private static final float EIGHTY_PERCENT = (float)0.20;
    private static final float FULL = (float)0.20;

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
        if (thePercentage >= EMPTY && thePercentage < TWENTY_PERCENT) {
            current = BatteryLevel.LOW;
        } else if (thePercentage >= TWENTY_PERCENT && thePercentage < FORTY_PERCENT) {
            current = BatteryLevel.MID_LOW;
        } else if (thePercentage >= FORTY_PERCENT && thePercentage < SIXTY_PERCENT) {
            current = BatteryLevel.MID;
        } else if (thePercentage >= SIXTY_PERCENT && thePercentage < EIGHTY_PERCENT) {
            current = BatteryLevel.MID_HIGH;
        } else if (thePercentage >= EIGHTY_PERCENT && thePercentage < FULL) {
            current = BatteryLevel.FULL;
        }
        return  current;
    }












    public enum BatteryLevel {
        LOW, MID_LOW, MID, MID_HIGH, FULL;
    }
}
