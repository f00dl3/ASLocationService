package astump.aslocationservice;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.icu.util.Calendar;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class CapPhoto extends Service
{
    private SurfaceHolder sHolder;
    private Camera mCamera;
    private Parameters parameters;
    File sd = new File(SharedMethods.sharedPath.toString(), "CamCaps");


    @Override
    public void onCreate()
    {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);}
        Thread myThread = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(getApplicationContext(), "ASLS CapPhoto service started!", Toast.LENGTH_SHORT).show();
        SharedMethods.makeDir(sd);

        try {
            if (Camera.getNumberOfCameras() >= 2) {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT); }
            if (Camera.getNumberOfCameras() < 2) {
                mCamera = Camera.open();
            }

            SurfaceView sv = new SurfaceView(getApplicationContext());

            try {
                mCamera.setPreviewDisplay(sv.getHolder());
                parameters = mCamera.getParameters();
                mCamera.setParameters(parameters);
                mCamera.startPreview();
                mCamera.takePicture(null, null, mCall);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(getApplicationContext(), "Camera capture callback failed.", Toast.LENGTH_SHORT).show();
            }

            sHolder = sv.getHolder();
            sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(), "Camera capture failed!", Toast.LENGTH_SHORT).show();
        }

        return START_NOT_STICKY;
    }

    Camera.PictureCallback mCall = new Camera.PictureCallback()
    {

        public void onPictureTaken(final byte[] data, Camera camera)
        {

            FileOutputStream outStream = null;
            try{

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String tar = (sdf.format(cal.getTime()));

                outStream = new FileOutputStream(sd+tar+".jpg");
                outStream.write(data);  outStream.close();

                String thaMessage = data.length + " byte written to:\n"+sd+tar+".jpg";
                Toast.makeText(getApplicationContext(), thaMessage, Toast.LENGTH_SHORT).show();
                camkapa(sHolder);


            } catch (FileNotFoundException e){
                Log.d("CAM", e.getMessage());
                Toast.makeText(getApplicationContext(), "Camera write FNF Exception!", Toast.LENGTH_SHORT).show();
            } catch (IOException e){
                Log.d("CAM", e.getMessage());
                Toast.makeText(getApplicationContext(), "Camera write IO Exception!", Toast.LENGTH_SHORT).show();
            }}
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void camkapa(SurfaceHolder sHolder) {

        if (null == mCamera)
            return;
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        Log.i("CAM", " closed");
    }

}
