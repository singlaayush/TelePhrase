package ai.ayushsingla.telephrase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class isUnlockedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, RecordingService.class));
    }
}
