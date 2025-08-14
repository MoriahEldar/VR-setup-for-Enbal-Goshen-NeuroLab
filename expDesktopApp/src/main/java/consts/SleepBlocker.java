package consts;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class SleepBlocker {

    private interface Kernel32 extends Library {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

        int ES_CONTINUOUS = 0x80000000;
        int ES_DISPLAY_REQUIRED = 0x00000002;
        int ES_SYSTEM_REQUIRED = 0x00000001;

        int SetThreadExecutionState(int esFlags);
    }

    public static void preventSleep() {
        Kernel32.INSTANCE.SetThreadExecutionState(
            Kernel32.ES_CONTINUOUS | Kernel32.ES_DISPLAY_REQUIRED | Kernel32.ES_SYSTEM_REQUIRED
        );
    }

    public static void allowSleep() {
        // Clear the previous request so system can sleep again
        Kernel32.INSTANCE.SetThreadExecutionState(Kernel32.ES_CONTINUOUS);
    }
}
