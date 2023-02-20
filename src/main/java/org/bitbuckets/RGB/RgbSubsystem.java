package org.bitbuckets.RGB;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.CANdleFaults;
import com.ctre.phoenix.led.LarsonAnimation;
import com.ctre.phoenix.led.RainbowAnimation;
import org.bitbuckets.lib.debug.IDebuggable;

public class RgbSubsystem {

    final CANdle candle;

    RgbFSM state = RgbFSM.A;

    final RgbInput rgbInput;


    final IDebuggable debuggable;

    public RgbSubsystem(CANdle candle, RgbInput rgbInput, IDebuggable debuggable) {
        this.candle = candle;
        this.rgbInput = rgbInput;
        this.debuggable = debuggable;
    }


    public void robotPeriodic() {

        switch (state) {
            case DISABLED:
                ;
            case A:
                if (rgbInput.buttonB()) {
                    state = RgbFSM.B;
                    break;
                }
                RainbowAnimation rainbowAnim = new RainbowAnimation(1, 1, 68);
                candle.animate(rainbowAnim);
                ErrorCode error = candle.getLastError(); // gets the last error generated by the CANdle
                CANdleFaults faults = new CANdleFaults();
                ErrorCode faultsError = candle.getFaults(faults); // fills faults with the current CANdle faults; returns the last error generated
                break;
            case B:
                if (rgbInput.buttonA()) {
                    state = RgbFSM.A;
                    break;
                }
                LarsonAnimation larsonAnimation = new LarsonAnimation(255, 1, 1, 1, 1, 68, LarsonAnimation.BounceMode.Center, 2);
                candle.animate(larsonAnimation);
                break;
        }

    }


}
