package astump.aslocationservice;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by astump on 12/6/17.
 */


public class SharedMethods {

    final static File sharedPath = new File(Environment.getExternalStorageDirectory() + File.separator + "ASTools");

    public static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    public static boolean isSet(String tStr) {
        if (tStr != null && !tStr.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSetNotZero(String tStr) {
        if (tStr != null && !tStr.isEmpty() && !tStr.equals("0.0") && !tStr.equals("0")) {
            return true;
        } else {
            return false;
        }
    }

    public static void makeDir(File newPath) {

        if(!newPath.exists()) {
            newPath.mkdirs();
            /* Toast.makeText(null, "Creating folder" + newPath.getPath(), Toast.LENGTH_SHORT).show(); */
        } else

        {
            deleteDir(newPath);
            newPath.mkdirs();
            /* Toast.makeText(null, "Cleaning folder" + newPath.getPath(), Toast.LENGTH_SHORT).show(); */
        }

    }

        public static void runProcess(String pString) {
            System.out.println(" --> Running [ "+pString+" ]");
            String s = null;
            String[] pArray = { "bash", "-c", pString };
            try {
                Process p = new ProcessBuilder(pArray).start();
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while ((s = stdInput.readLine()) != null) { System.out.println(s); }
                while ((s = stdError.readLine()) != null) { System.out.println(s); }
                p.destroy();
            }
            catch (IOException e) { e.printStackTrace(); }
            System.out.flush();
        }

        public static void runProcessSilently(String pString) {
            String s = null;
            String[] pArray = { "bash", "-c", pString };
            try {
                Process p = new ProcessBuilder(pArray).start();
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while ((s = stdInput.readLine()) != null) { System.out.println(s); }
                while ((s = stdError.readLine()) != null) { System.out.println(s); }
                p.destroy();
            }
            catch (IOException e) { e.printStackTrace(); }
            System.out.flush();
        }

        public static void runProcessOutFile(String pString, File outFile, boolean appendFlag) throws FileNotFoundException {
            System.out.println(" --> (Output following result to file: "+outFile.getPath()+")");
            String tmpVar = null;
            try { tmpVar = runProcessOutVar(pString); } catch (IOException ix) { ix.printStackTrace(); }
            varToFile(tmpVar, outFile, appendFlag);
        }

        public static String runProcessOutVar(String pString) throws java.io.IOException {
            String[] pArray = { "sh", "-c", pString };
            Process proc = new ProcessBuilder(pArray).start();
            InputStream is = proc.getInputStream();
            Scanner co = new Scanner(is).useDelimiter("\\A");
            String val = "";
            if (co.hasNext()) { val = co.next(); } else { val = ""; }
            return val;
        }

        public static void varToFile(String thisVar, File outFile, boolean appendFlag) throws FileNotFoundException {
            try ( PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFile, appendFlag))) ) {
                out.println(thisVar);
            } catch (IOException io) { io.printStackTrace(); }
        }

        public static void writeOutputToFile(File logFilePath, String stringToWrite, String logFileDesc) {
                    try {
                        FileOutputStream fOut = new FileOutputStream(new File(logFilePath.getAbsolutePath().toString()), true);
                        OutputStreamWriter osw = new OutputStreamWriter(fOut);
                        osw.write(stringToWrite);
                        osw.flush();
                        osw.close();
                        Log.i(logFileDesc, "File write attempted.");
                    } catch (FileNotFoundException fnf) {
                        fnf.printStackTrace();
                    } catch (IOException iox) {
                        iox.printStackTrace();
                    }
            }
}
