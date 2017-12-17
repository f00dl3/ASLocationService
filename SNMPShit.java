package astump.aslocationservice;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by astump on 12/12/17.
 */

public class SNMPShit extends IntentService {

    public SNMPShit() {
        super("SNMPShit");
    }

    final public static File snmpOutPath = new File(SharedMethods.sharedPath.toString(), "SNMP");
    final File outZip = new File(SharedMethods.sharedPath.toString(), "aPayload.zip");

    @Override
    public void onHandleIntent(Intent intent) {
        SharedMethods.makeDir(snmpOutPath);
        snmpAgentService();
    }

    @Override
    public void onDestroy() {

    }

    /*
    Old process Shell Script
		echo "put $zipPL" | sftp -i $Key -P $Port astump@$Host
		rm $zipPL
	*/

    public void snmpAgentService() {

        Toast.makeText(getApplicationContext(), "snmpAgentService thread go!", Toast.LENGTH_SHORT).show();

        long pollNumber = 0;
        final int secBetweenPollPeriod = 60;
        final int logCatToPull = (4096 * 2);
        final File netstatFile = new File(snmpOutPath, "NetStatE.txt");
        final File logCatFile = new File(snmpOutPath, "LogCat.txt");
        final File vmStatFile = new File(snmpOutPath, "VMStat.txt");
        final File toplessFile = new File(snmpOutPath, "Topless.txt");
        final File ifStatFile = new File(snmpOutPath, "IFStats.txt");
        final File dsCPUFile = new File(snmpOutPath, "DSCPU.txt");
        final File dsTelRegFile = new File(snmpOutPath, "DSConn.txt");
        final File dsBattFile = new File(snmpOutPath, "DSBattery.txt");
        final File dsGeoFile = new File(snmpOutPath, "DSGeo.txt");

        while(true) {

            pollNumber++;

            Toast.makeText(getApplicationContext(), "Running SNMP poll #" + pollNumber, Toast.LENGTH_SHORT).show();

            try {
                SharedMethods.runProcessOutFile("netstat -W | grep ESTAB", netstatFile, false);
                SharedMethods.runProcessOutFile("echo \"logcat -t " + logCatToPull + "\" | su", logCatFile, false);
                SharedMethods.runProcessOutFile("free", vmStatFile, false);
                SharedMethods.runProcessOutFile("top -n 1", toplessFile, false);
                SharedMethods.runProcessOutFile("cat /proc/net/dev", ifStatFile, false);
                SharedMethods.runProcessOutFile("echo \"dumpsys cpuinfo\" | su", dsCPUFile, false);
                SharedMethods.runProcessOutFile("echo \"dumpsys telephony.registry\" | su", dsTelRegFile, false);
                SharedMethods.runProcessOutFile("echo \"dumpsys battery\" | su", dsBattFile, false);
                SharedMethods.runProcessOutFile("echo \"dumpsys location\" | su", dsGeoFile, false);
            } catch (FileNotFoundException fnf) {
                fnf.printStackTrace();
            }

            if(outZip.exists()) { outZip.delete(); }
            ZipIntegration.ZipFolder(snmpOutPath, outZip);

            try {
                SharedMethods.runProcessOutFile("echo \"\"", DeviceSensorServices.sensorLogFile, false);
                SharedMethods.runProcessOutFile("echo \"\"", LocationService.locLogFile, false);
            } catch (FileNotFoundException fnf) {
                fnf.printStackTrace();
            }

            SSHared.sftpUpload(outZip);

            try { Thread.sleep(secBetweenPollPeriod*1000); } catch (InterruptedException ix) { ix.printStackTrace(); }

        }
    }

}
