package astump.aslocationservice;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import java.io.File;

/**
 * Created by astump on 12/6/17.
 */

public class DeviceSensorServices extends Service implements SensorEventListener {

    final public static File sensorLogFile = new File(SNMPShit.snmpOutPath.toString(), "aslsSensors.log");
    long sensorCount = 0;

    private SensorManager mSensorManager;
    private Sensor mAmbientTemperature;

    public static final String BROADCAST_ACTION = "ASLS Sensor Poll";

    Intent intent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String unsupportedMessages = "ASLS Sensor Service Started!";
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAmbientTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mSensorManager.registerListener(this, mAmbientTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        if(mAmbientTemperature == null) { unsupportedMessages += "\nTYPE_AMBIENT_TEMPERTATURE not supported."; }
        Toast.makeText(getApplicationContext(), unsupportedMessages, Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorCount++;
        float ambientTemperature = 0.0f;
        float ambientTemperatureF = 0.0f;
        ambientTemperature = event.values[0];
        ambientTemperatureF = (ambientTemperature * 9 / 5) + 32;
        if(SharedMethods.isSetNotZero(Float.toString(ambientTemperature))) {
            String sensorUpdateString = "Sensor Update: " + sensorCount
                    + ", AmbientTemperatureF: " + ambientTemperatureF
                    + ", EndSensorData\n";
            SharedMethods.writeOutputToFile(sensorLogFile, sensorUpdateString, "Sensor Logfile");
            /* Toast.makeText(getApplicationContext(), sensorUpdateString, Toast.LENGTH_SHORT).show(); */
        } else {
            /* Toast.makeText(getApplicationContext(), "Sensor data missing from one or more sensors!", Toast.LENGTH_LONG).show(); */
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        /* Maybe do something? */
    }

}
