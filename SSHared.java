package astump.aslocationservice;

import android.os.Environment;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by astump on 12/16/17.
 */

public class SSHared {

        private static final String hostIP = "YOUR IP ADDY";
        private static final String sshUser = "YOUR USER";
        private static final String sshUserHome = "/home/USER";
        private static final int hostPort = PORT;
        private static final File hostKey = new File(Environment.getExternalStorageDirectory(), "YOUR SSH KEY");

        public static void sftpUpload(File fileToUpload) {

            Session session = null;
            Channel channel = null;
            ChannelSftp channelSftp = null;

            try {
                JSch jsch = new JSch();
                jsch.addIdentity(hostKey.toString());
                session = jsch.getSession(sshUser, hostIP, hostPort);
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();
                channel = session.openChannel("sftp");
                channel.connect();
                channelSftp = (ChannelSftp) channel;
                channelSftp.cd(sshUserHome);
                channelSftp.put(new FileInputStream(fileToUpload), fileToUpload.getName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }


    }

}
