package astump.aslocationservice;

import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by astump on 12/16/17.
 */

public class ZipIntegration {

    public static void ZipFolder(File sourceFolder, File outputZipFile) {
        String zipFile = outputZipFile.toString();
        String sourceDir = sourceFolder.toString();

        try {
            byte[] buffer = new byte[1024];
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File dir = new File(sourceDir);
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                FileInputStream fis = new FileInputStream(files[i]);
                zos.putNextEntry(new ZipEntry(files[i].getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) { zos.write(buffer, 0, length); }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }

}
