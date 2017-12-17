/* 
SNMP Walk -> Database
Version 4
Last updated: 2017-12-03

**** Was for build 12. Does not integrate newer features. Bit it shows you where to start ***

*/

package jASUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

import org.json.*;

import jASUtils.MyDBConnector;
import jASUtils.StumpJunk;



public class Walk2DBv4 {
	
	public static void main (String[] args) {
		
		final double tA = 0.97;
		final int multFact = 100;
		final String ramPath = "/dev/shm";
		final String keyPath = "/home/astump/.ssh";
		final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		final Date date = new Date();
		final String thisTimestamp = dateFormat.format(date);
		final String thisNode = args[0];
		PrintStream console = System.out;
		
		if (thisNode.equals("Desktop")) {	
			
			final File cam1File = new File(ramPath+"/GetCamsJ/OffCam1T.txt");
			final File cam2File = new File(ramPath+"/GetCamsJ/OffCam2T.txt");
			final File cam3File = new File(ramPath+"/GetCamsJ/OffCam3T.txt");
			final File cam4File = new File(ramPath+"/GetCamsJ/OffCam4T.txt");
			final File dbSizeFile = new File(ramPath+"/dbsizes.txt");
			final File duMySQLFile = new File(ramPath+"/duMySQL.txt");
			final File ns5File = new File(ramPath+"/ns5out.txt");
			final File sensorFile = new File(ramPath+"/sensors.txt");
			final File theWalkFile = new File(ramPath+"/snmpwalk.txt");
			final File upsFile = new File(ramPath+"/upsstats.cgi");
			
			final String aPayload = "/home/astump/aPayload.zip";
			final String ePayload = "/home/astump/ePayload.tar.gz";
			final File aPayloadFile = new File(aPayload);
			final File ePayloadFile = new File(ePayload);
			
			/* 
			CONVERT TO JAVA IF NEEDED LATER! ProcessTrace=$(ps -aux)
			dbCoreCursor.execute("INSERT IGNORE INTO net_snmp.Main_ProcTrace (WalkTime, ProcDump) VALUES ('"+thisTimestamp+"', '"+processTrace+"');")
			*/
			 
			Thread s1t1 = new Thread(new Runnable() { public void run() { try { StumpJunk.runProcessOutFile("sensors", sensorFile, false); } catch (FileNotFoundException fe) { fe.printStackTrace(); } }});
			Thread s1t2 = new Thread(new Runnable() { public void run() { try { StumpJunk.runProcessOutFile("snmpwalk localhost .", theWalkFile, false); } catch (FileNotFoundException fe) { fe.printStackTrace(); } }});
			Thread s1t3 = new Thread(new Runnable() { public void run() { try { StumpJunk.runProcessOutFile("netstat -W | grep \"astump-Desktop\" | grep \"ESTAB\"", ns5File, false); } catch (FileNotFoundException fe) { fe.printStackTrace(); } }});
			Thread s1t4 = new Thread(new Runnable() { public void run() { StumpJunk.jsoupOutFile("http://127.0.0.1/cgi-bin/apcupsd/upsstats.cgi", upsFile); }});		
			Thread s1t5 = new Thread(new Runnable() { public void run() { try { StumpJunk.runProcessOutFile("du /var/lib/mysql", duMySQLFile, false); } catch (FileNotFoundException fe) { fe.printStackTrace(); } }});
			Thread thListA[] = { s1t1, s1t2, s1t3, s1t4, s1t5 };
			for (Thread thread : thListA) { thread.start(); try { thread.join(); } catch (InterruptedException nx) { nx.printStackTrace(); } }
			/* for (int i = 0; i < thListA.length; i++) { try { thListA[i].join(); } catch (InterruptedException nx) { nx.printStackTrace(); } } */

			try { StumpJunk.runProcessOutFile("snmpwalk -m MYSQL-SERVER-MIB localhost enterprises.20267", theWalkFile, true); } catch (FileNotFoundException fe) { fe.printStackTrace(); }

			String dbSizeQuery = "SELECT table_schema 'Database', ROUND(SUM(data_length + index_length)/1024/1024, 1) 'DBSizeMB', GREATEST(SUM(TABLE_ROWS), SUM(AUTO_INCREMENT)) AS DBRows FROM information_schema.tables GROUP BY table_schema;";
			try (
				Connection conn = MyDBConnector.getMyConnection();
				Statement stmt = conn.createStatement();
				ResultSet resultSet = stmt.executeQuery(dbSizeQuery);
			) {
				System.setOut(new PrintStream(new FileOutputStream(dbSizeFile, false)));				
				while (resultSet.next()) {
					System.out.println(resultSet.getString("Database")+","+resultSet.getString("DBSizeMB")+","+resultSet.getString("DBRows"));
				}
			}
			catch (FileNotFoundException fnf) { fnf.printStackTrace(); }
			catch (SQLException sqlex) { sqlex.printStackTrace(); }
			catch (Exception e) { e.printStackTrace(); }
			System.setOut(console);		

			StumpJunk.sedFileReplace(ramPath+"/sensors.txt", " +", ",");
			StumpJunk.sedFileReplace(ramPath+"/ns5out.txt", " +", ",");
			StumpJunk.sedFileReplace(ramPath+"/dbsizes.txt", "null", "0");
			StumpJunk.sedFileReplace(ramPath+"/duMySQL.txt", "\\t", ",");
			
			int numUsers = 0;
			int cpuLoad1 = 0;
			int cpuLoad2 = 0;
			int cpuLoad3 = 0;
			int cpuLoad4 = 0;
			int cpuLoad5 = 0;
			int cpuLoad6 = 0;
			int cpuLoad7 = 0;
			int cpuLoad8 = 0;
			double loadIndex1 = 0.0;
			double loadIndex5 = 0.0;
			double loadIndex15 = 0.0;
			int processes = 0;
			long octetsIn = 0;
			long octetsOut = 0;
			long kMemPhys = 0;
			long kMemVirt = 0;
			long kMemBuff = 0;
			long kMemCached = 0;
			long kMemShared = 0;
			long kSwap = 0;
			long k4Root = 0;
			long kMemPhysU = 0;
			long kMemVirtU = 0;
			long kMemBuffU = 0;
			long kMemCachedU = 0;
			long kMemSharedU = 0;
			long kSwapU = 0;
			long k4RootU = 0;
			long iNodeSDA1 = 0;
			long iNodeSHM = 0;
			long diskIOSysRead = 0;
			long diskIOSysWrite = 0;
			long mySQLUpdate = 0;
			long mySQLInsert = 0;
			long mySQLSelect = 0;
			long mySQLReplace = 0;
			long mySQLDelete = 0;
			long logApache2GET = 0;
			long logTomcatGET = 0;
			
			Scanner walkFileScanner = null; try {		
				walkFileScanner = new Scanner(theWalkFile);
				while(walkFileScanner.hasNext()) {				
					String line = walkFileScanner.nextLine();
					if(line.contains("hrSystemNumUsers.0 =")) { Pattern p = Pattern.compile("Gauge32: (.*)"); Matcher m = p.matcher(line); if (m.find()) { numUsers = Integer.parseInt(m.group(1)); }}
					if(line.contains("hrProcessorLoad.196608 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { cpuLoad1 = Integer.parseInt(m.group(1)); }}
					if(line.contains("hrProcessorLoad.196609 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { cpuLoad2 = Integer.parseInt(m.group(1)); } }					
					if(line.contains("hrProcessorLoad.196610 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { cpuLoad3 = Integer.parseInt(m.group(1)); } }					
					if(line.contains("hrProcessorLoad.196611 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { cpuLoad4 = Integer.parseInt(m.group(1)); } }					
					if(line.contains("hrProcessorLoad.196612 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { cpuLoad5 = Integer.parseInt(m.group(1)); } }					
					if(line.contains("hrProcessorLoad.196613 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { cpuLoad6 = Integer.parseInt(m.group(1)); } }					
					if(line.contains("hrProcessorLoad.196614 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { cpuLoad7 = Integer.parseInt(m.group(1)); } }					
					if(line.contains("hrProcessorLoad.196615 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { cpuLoad8 = Integer.parseInt(m.group(1)); } }
					if(line.contains("laLoad.1 =")) { Pattern p = Pattern.compile("STRING: (.*)"); Matcher m = p.matcher(line); if (m.find()) { loadIndex1 = Double.parseDouble(m.group(1)); } }
					if(line.contains("laLoad.2 =")) { Pattern p = Pattern.compile("STRING: (.*)"); Matcher m = p.matcher(line); if (m.find()) { loadIndex5 = Double.parseDouble(m.group(1)); } }
					if(line.contains("laLoad.3 =")) { Pattern p = Pattern.compile("STRING: (.*)"); Matcher m = p.matcher(line); if (m.find()) { loadIndex15 = Double.parseDouble(m.group(1)); } }
					if(line.contains("hrSystemProcesses.0 =")) { Pattern p = Pattern.compile("Gauge32: (.*)"); Matcher m = p.matcher(line); if (m.find()) { processes = Integer.parseInt(m.group(1)); } }
					if(line.contains("ifHCInOctets.2 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { octetsIn = Long.parseLong(m.group(1)); } }
					if(line.contains("ifHCOutOctets.2 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { octetsOut = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.1 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { kMemPhys = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.3 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { kMemVirt = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.6 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { kMemBuff = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.7 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { kMemCached = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.8 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { kMemShared = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.10 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { kSwap = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.31 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { k4Root = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.1 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { kMemPhysU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.3 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { kMemVirtU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.6 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { kMemBuffU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.7 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { kMemCachedU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.8 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { kMemSharedU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.10 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { kSwapU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.31 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { k4RootU = Long.parseLong(m.group(1)); } }
					if(line.contains("dskPercentNode.3 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { iNodeSDA1 = Long.parseLong(m.group(1)); } }
					if(line.contains("dskPercentNode.6 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { iNodeSHM = Long.parseLong(m.group(1)); } }
					if(line.contains("ssIORawReceived.0 =")) { Pattern p = Pattern.compile("Counter32: (.*)"); Matcher m = p.matcher(line); if (m.find()) { diskIOSysRead = Long.parseLong(m.group(1)); } }
					if(line.contains("ssIORawSent.0 =")) { Pattern p = Pattern.compile("Counter32: (.*)"); Matcher m = p.matcher(line); if (m.find()) { diskIOSysWrite = Long.parseLong(m.group(1)); } }
					if(line.contains("myComUpdate.0 =")) { Pattern p = Pattern.compile("Counter32: (.*)"); Matcher m = p.matcher(line); if (m.find()) { mySQLUpdate = Long.parseLong(m.group(1)); } }
					if(line.contains("myComInsert.0 =")) { Pattern p = Pattern.compile("Counter32: (.*)"); Matcher m = p.matcher(line); if (m.find()) { mySQLInsert = Long.parseLong(m.group(1)); } }
					if(line.contains("myComSelect.0 =")) { Pattern p = Pattern.compile("Counter32: (.*)"); Matcher m = p.matcher(line); if (m.find()) { mySQLSelect = Long.parseLong(m.group(1)); } }
					if(line.contains("myComReplace.0 =")) { Pattern p = Pattern.compile("Counter32: (.*)"); Matcher m = p.matcher(line); if (m.find()) { mySQLReplace = Long.parseLong(m.group(1)); } }
					if(line.contains("logMatchCurrentCounter.1 =")) { Pattern p = Pattern.compile("Counter32: (.*)"); Matcher m = p.matcher(line); if (m.find()) { logApache2GET = Long.parseLong(m.group(1)); } }
					if(line.contains("logMatchCurrentCounter.5 =")) { Pattern p = Pattern.compile("Counter32: (.*)"); Matcher m = p.matcher(line); if (m.find()) { logTomcatGET = Long.parseLong(m.group(1)); } }
				}			
			} catch (FileNotFoundException e) { e.printStackTrace(); }
			
			int tempCPU = 0;
			int tempCase = 0;
			int tempCore1 = 0;
			int tempCore2 = 0;
			int tempCore3 = 0;
			int tempCore4 = 0;
			int fan1 = 0;
			int fan2 = 0;
			int fan3 = 0;
			double voltCPU = 0.0;
			double voltCore1 = 0.0;
			double voltCore2 = 0.0;
			double voltCore3 = 0.0;
			double voltCore4 = 0.0;
			double voltPlus33 = 0.0;
			double voltPlus5 = 0.0;
			double voltPlus12 = 0.0;
			double voltBatt = 0.0;
			
			Scanner sensorFileScanner = null; try {		
				sensorFileScanner = new Scanner(sensorFile);
				while(sensorFileScanner.hasNext()) {				
					String line = sensorFileScanner.nextLine();
					if(line.contains("Package,id,0:,")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[3].replaceAll("\\D+", ""); Double dThisVal = Double.parseDouble(strThisVal); tempCPU = (int) (dThisVal * tA * multFact); }
					if(line.contains("SYSTIN:,")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[1].replaceAll("\\D+", ""); Double dThisVal = Double.parseDouble(strThisVal); tempCase = (int) (dThisVal * tA * multFact);  }
					if(line.contains("Core,0:,")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2].replaceAll("\\D+", ""); Double dThisVal = Double.parseDouble(strThisVal); tempCore1 = (int) (dThisVal * tA * multFact); }
					if(line.contains("Core,1:,")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2].replaceAll("\\D+", ""); Double dThisVal = Double.parseDouble(strThisVal); tempCore2 = (int) (dThisVal * tA * multFact); }
					if(line.contains("Core,2:,")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2].replaceAll("\\D+", ""); Double dThisVal = Double.parseDouble(strThisVal); tempCore3 = (int) (dThisVal * tA * multFact); }
					if(line.contains("Core,3:,")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2].replaceAll("\\D+", ""); Double dThisVal = Double.parseDouble(strThisVal); tempCore4 = (int) (dThisVal * tA * multFact); }
					if(line.contains("Fan,1:,")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2].replaceAll("\\D+", ""); fan1 = Integer.parseInt(strThisVal); }
					if(line.contains("Fan,2:,")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2].replaceAll("\\D+", ""); fan2 = Integer.parseInt(strThisVal); }
					if(line.contains("CPU,fan:,")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2].replaceAll("\\D+", ""); fan3 = Integer.parseInt(strThisVal); }
					if(line.contains("CPU,Vcc:,")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2].replaceAll("\\D+", ""); Double dThisVal = Double.parseDouble(strThisVal); voltCPU = (dThisVal / multFact); }
					if(line.contains("core,0:,+")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2].replaceAll("\\D+", ""); Double dThisVal = Double.parseDouble(strThisVal); voltCore1 = (dThisVal / multFact); }
					if(line.contains("core,1:,+")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2].replaceAll("\\D+", ""); Double dThisVal = Double.parseDouble(strThisVal); voltCore2 = (dThisVal / multFact); }
					if(line.contains("core,2:,+")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2].replaceAll("\\D+", ""); Double dThisVal = Double.parseDouble(strThisVal); voltCore3 = (dThisVal / multFact); }
					if(line.contains("core,3:,+")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2].replaceAll("\\D+", ""); Double dThisVal = Double.parseDouble(strThisVal); voltCore4 = (dThisVal / multFact); }
					if(line.contains("+3.3V:,+")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[1].replaceAll("\\D+", ""); Double dThisVal = Double.parseDouble(strThisVal); voltPlus33 = (dThisVal / multFact); }
					if(line.contains("+5V:,+")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[1].replaceAll("\\D+", ""); Double dThisVal = Double.parseDouble(strThisVal); voltPlus5 = (dThisVal / multFact); }
					if(line.contains("+12V:,+")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[1].replaceAll("\\D+", ""); Double dThisVal = Double.parseDouble(strThisVal); voltPlus12 = (dThisVal / multFact); }
					if(line.contains("Vbat:,+")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[1].replaceAll("\\D+", ""); Double dThisVal = Double.parseDouble(strThisVal); voltBatt = (dThisVal / multFact); }				
				}					
			}
			catch (FileNotFoundException e) { e.printStackTrace(); }
			catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }
	
			Scanner cam1Scanner = null; int logSecCam1Down = 0;
			try { cam1Scanner = new Scanner(cam1File); while(cam1Scanner.hasNext()) { logSecCam1Down = Integer.parseInt(cam1Scanner.nextLine()); } } catch (FileNotFoundException e) { e.printStackTrace(); }

			Scanner cam2Scanner = null; int logSecCam2Down = 0;
			try { cam2Scanner = new Scanner(cam2File); while(cam2Scanner.hasNext()) { logSecCam2Down = Integer.parseInt(cam2Scanner.nextLine()); } } catch (FileNotFoundException e) { e.printStackTrace(); }

			Scanner cam3Scanner = null; int logSecCam3Down = 0;
			try { cam3Scanner = new Scanner(cam3File); while(cam3Scanner.hasNext()) { logSecCam3Down = Integer.parseInt(cam3Scanner.nextLine()); } } catch (FileNotFoundException e) { e.printStackTrace(); }

			Scanner cam4Scanner = null; int logSecCam4Down = 0;
			try { cam4Scanner = new Scanner(cam4File); while(cam4Scanner.hasNext()) { logSecCam4Down = Integer.parseInt(cam4Scanner.nextLine()); } } catch (FileNotFoundException e) { e.printStackTrace(); }

			long mySQLRowsCore = 0;
			long mySQLRowsFeeds = 0;
			long mySQLRowsNetSNMP = 0;
			long mySQLRowsWebCal = 0;
			long mySQLRowsWxObs = 0;
			double mySQLSizeCore = 0.0;
			double mySQLSizeFeeds = 0.0;
			double mySQLSizeNetSNMP = 0.0;
			double mySQLSizeWebCal = 0.0;
			double mySQLSizeWxObs = 0.0;

			Scanner dbSizeScanner = null; try {			
				dbSizeScanner = new Scanner(dbSizeFile);
				while(dbSizeScanner.hasNext()) {			
					String line = dbSizeScanner.nextLine();
					if(line.contains("Core,")) {
						String[] lineTmp = line.split(",");
						String strThisVal = lineTmp[1]; String strThisVa2 = lineTmp[2];
						mySQLRowsCore = Long.parseLong(strThisVa2); mySQLSizeCore = Double.parseDouble(strThisVal);
					}	
					if(line.contains("Feeds,")) {
						String[] lineTmp = line.split(",");
						String strThisVal = lineTmp[1]; String strThisVa2 = lineTmp[2];
						mySQLRowsCore = Long.parseLong(strThisVa2); mySQLSizeFeeds = Double.parseDouble(strThisVal);
					}	
					if(line.contains("net_snmp,")) {
						String[] lineTmp = line.split(",");
						String strThisVal = lineTmp[1]; String strThisVa2 = lineTmp[2];
						mySQLRowsNetSNMP = Long.parseLong(strThisVa2); mySQLSizeNetSNMP = Double.parseDouble(strThisVal);
					}	
					if(line.contains("WebCal,")) {
						String[] lineTmp = line.split(",");
						String strThisVal = lineTmp[1]; String strThisVa2 = lineTmp[2];
						mySQLRowsWebCal = Long.parseLong(strThisVa2); mySQLSizeWebCal = Double.parseDouble(strThisVal);
					}	
					if(line.contains("WxObs,")) {
						String[] lineTmp = line.split(",");
						String strThisVal = lineTmp[1]; String strThisVa2 = lineTmp[2];
						mySQLRowsWxObs = Long.parseLong(strThisVa2); mySQLSizeWxObs = Double.parseDouble(strThisVal);
					}	
				}
			}
			catch (FileNotFoundException e) { e.printStackTrace(); }
			catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }

			long duMySQLCore = 0;
			long duMySQLFeeds = 0;
			long duMySQLNetSNMP = 0;
			long duMySQLTotal = 0;
			long duMySQLWebCal = 0;
			long duMySQLWxObs = 0;

			Scanner duMySQLScanner = null; try {			
				duMySQLScanner = new Scanner(duMySQLFile);
				while(duMySQLScanner.hasNext()) {			
					String line = duMySQLScanner.nextLine();
					if(line.contains(",/var/lib/mysql/Core")) { String[] lineTmp = line.split(","); duMySQLCore = Long.parseLong(lineTmp[0]); }	
					if(line.contains(",/var/lib/mysql/Feeds")) { String[] lineTmp = line.split(","); duMySQLFeeds = Long.parseLong(lineTmp[0]); }	
					if(line.contains(",/var/lib/mysql/net_snmp")) { String[] lineTmp = line.split(","); duMySQLNetSNMP = Long.parseLong(lineTmp[0]); }	
					if(line.contains(",/var/lib/mysql/WxObs")) { String[] lineTmp = line.split(","); duMySQLWxObs = Long.parseLong(lineTmp[0]); }	
					if(line.contains(",/var/lib/mysql/WebCal")) { String[] lineTmp = line.split(","); duMySQLWebCal = Long.parseLong(lineTmp[0]); }	
					if(line.contains(",/var/lib/mysql")) { String[] lineTmp = line.split(","); duMySQLTotal = Long.parseLong(lineTmp[0]); }
				}
			}
			catch (FileNotFoundException e) { e.printStackTrace(); }
			catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }

			String ns5ActiveSSHClient = "";
			int ns5Active = 0;
			String ns5ActiveCon = "";
			int ns5ActiveSSH = 0;
			
			Scanner ns5Scanner = null; try {			
				ns5Scanner = new Scanner(ns5File);
				while(ns5Scanner.hasNext()) {				
					String line = ns5Scanner.nextLine();
					ns5Active++;
					String[] lineTmpA = line.split(","); String strThisValA = lineTmpA[4]; ns5ActiveCon = ns5ActiveCon + " " + strThisValA;
					if(line.contains("x")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[4]; ns5ActiveSSHClient = ns5ActiveSSHClient + " " + strThisVal; ns5ActiveSSH++; }
				}
			}
			catch (FileNotFoundException e) { e.printStackTrace(); }
			catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }

			double upsLoad = 0.0;
			double upsTimeLeft = 0.0;

			Scanner upsScanner = null; try {			
				upsScanner = new Scanner(upsFile);
				while(upsScanner.hasNext()) {				
					String line = upsScanner.nextLine();
					if(line.contains("upsload")) { Pattern p = Pattern.compile("value=(.*)\" alt"); Matcher m = p.matcher(line); if (m.find()) { upsLoad = Double.parseDouble(m.group(1)); } }
					if(line.contains("runtime")) { Pattern p = Pattern.compile("value=(.*)&amp"); Matcher m = p.matcher(line); if (m.find()) { upsTimeLeft = Double.parseDouble(m.group(1)); } }
				}
			} catch (FileNotFoundException e) { e.printStackTrace(); }
		
			int tomcatWars = 0;
			int tomcatDeploy = 0;
			int crashLogs = 0;

			try {
				tomcatWars = Integer.parseInt(StumpJunk.runProcessOutVar("ls /var/lib/tomcat8/webapps/*.war | wc -l").replaceAll("\\D+", ""));
				tomcatDeploy = Integer.parseInt(StumpJunk.runProcessOutVar("ls /var/lib/tomcat8/webapps/ -p | grep \"/\" | wc -l").replaceAll("\\D+", ""));
				crashLogs = Integer.parseInt(StumpJunk.runProcessOutVar("ls /var/crash/ | wc -l").replaceAll("\\D+", ""));
			} catch (IOException ix) { ix.printStackTrace(); }

			String thisSQLQuery = "INSERT IGNORE INTO net_snmp.Main ("
				+ "WalkTime, NumUsers,"
				+ " CPULoad1, CPULoad2, CPULoad3, CPULoad4, CPULoad5, CPULoad6, CPULoad7, CPULoad8,"
				+ " LoadIndex1, LoadIndex5, LoadIndex15, Processes,"
				+ " OctetsIn, OctetsOut, LogApache2GET, TomcatGET,"
				+ " INodeSDA1, INodeSHM, DiskIOSysRead, DiskIOSysWrite,"
				+ " KMemPhys, KMemVirt, KMemBuff, KMemCached, KMemShared, KSwap, K4Root,"
				+ " KMemPhysU, KMemVirtU, KMemBuffU, KMemCachedU, KMemSharedU, KSwapU, K4RootU,"
				+ " MySQLUpdate, MySQLInsert, MySQLSelect, MySQLReplace, MySQLDelete,"
				+ " TempCPU, TempCase, TempCore1, TempCore2, TempCore3, TempCore4,"
				+ " Fan1, Fan2, Fan3, VoltCPU, VoltCore1, VoltCore2, VoltCore3, VoltCore4,"
				+ " VoltPlus33, VoltPlus5, VoltPlus12, VoltBatt,"
				+ " LogSecCam1Down, LogSecCam2Down, LogSecCam3Down, LogSecCam4Down,"
				+ " MySQLRowsCore, MySQLRowsNetSNMP, MySQLRowsWebCal, MySQLRowsWxObs, MySQLRowsFeeds,"
				+ " MySQLSizeCore, MySQLSizeNetSNMP, MySQLSizeWebCal, MySQLSizeWxObs, MySQLSizeFeeds,"
				+ " NS5Active, NS5ActiveSSH, SSHClientIP, NS5ActiveCon,"
				+ " UPSLoad, UPSTimeLeft, TomcatWARs, TomcatDeploy, CrashLogs,"
				+ " duMySQLCore, duMySQLNetSNMP, duMySQLWebCal, duMySQLWxObs, duMySQLFeeds, duMySQLTotal"
				+ ") VALUES ("
				+ "'"+thisTimestamp+"',"+numUsers+","
				+ cpuLoad1+","+cpuLoad2+","+cpuLoad3+","+cpuLoad4+","+cpuLoad5+","+cpuLoad6+","+cpuLoad7+","+cpuLoad8+","
				+ loadIndex1+","+loadIndex5+","+loadIndex15+","+processes+","
				+ octetsIn+","+octetsOut+","+logApache2GET+","+logTomcatGET+","
				+ iNodeSDA1+","+iNodeSHM+","+diskIOSysRead+","+diskIOSysWrite+","
				+ kMemPhys+","+kMemVirt+","+kMemBuff+","+kMemCached+","+kMemShared+","+kSwap+","+k4Root+","
				+ kMemPhysU+","+kMemVirtU+","+kMemBuffU+","+kMemCachedU+","+kMemSharedU+","+kSwapU+","+k4RootU+","
				+ mySQLUpdate+","+mySQLInsert+","+mySQLSelect+","+mySQLReplace+","+mySQLDelete+","
				+ tempCPU+","+tempCase+","+tempCore1+","+tempCore2+","+tempCore3+","+tempCore4+","
				+ fan1+","+fan2+","+fan3+","+voltCPU+","+voltCore1+","+voltCore2+","+voltCore3+","+voltCore4+","
				+ voltPlus33+","+voltPlus5+","+voltPlus12+","+voltBatt+","
				+ logSecCam1Down+","+logSecCam2Down+","+logSecCam3Down+","+logSecCam4Down+","
				+ mySQLRowsCore+","+mySQLRowsNetSNMP+","+mySQLRowsWebCal+","+mySQLRowsWxObs+","+mySQLRowsFeeds+","
				+ mySQLSizeCore+","+mySQLSizeNetSNMP+","+mySQLSizeWebCal+","+mySQLSizeWxObs+","+mySQLSizeFeeds+","
				+ ns5Active+","+ns5ActiveSSH+",'"+ns5ActiveSSHClient+"','"+ns5ActiveCon+"',"
				+ upsLoad+","+upsTimeLeft+","+tomcatWars+","+tomcatDeploy+","+crashLogs+","
				+ duMySQLCore+","+duMySQLNetSNMP+","+duMySQLWebCal+","+duMySQLWxObs+","+duMySQLFeeds+","+duMySQLTotal
				+ ");";
		
			System.out.println(thisSQLQuery);
			try ( Connection conn = MyDBConnector.getMyConnection(); Statement stmt = conn.createStatement();) { stmt.executeUpdate(thisSQLQuery); }
			catch (Exception e) { e.printStackTrace(); }

			if(aPayloadFile.exists()) { 
			
				String uzPayload = ramPath+"/aPayload";
				File uzPayloadF = new File(uzPayload);
				String uzPayloadFull = uzPayload+"/storage/sdcard0/aPLoad";

				StumpJunk.unzipFile(aPayload, uzPayload);
				StumpJunk.sedFileReplace(uzPayloadFull+"/NetStatE.txt", " +", ",");
				StumpJunk.sedFileReplace(uzPayloadFull+"/IFStats.txt", " +", ",");
				StumpJunk.sedFileReplace(uzPayloadFull+"/DSCPU.txt", " +", ",");
				StumpJunk.sedFileReplace(uzPayloadFull+"/DSBattery.txt", " +", ",");
				StumpJunk.sedFileReplace(uzPayloadFull+"/DSConn.txt", " +", ",");
				StumpJunk.sedFileReplace(uzPayloadFull+"/DSGeo.txt", " +", ",");
				StumpJunk.sedFileReplace(uzPayloadFull+"/VMStat.txt", " +", ",");

				File aNetStatFile = new File(uzPayloadFull+"/NetStatE.txt");
				File aIfStatsFile = new File(uzPayloadFull+"/IFStats.txt");
				File aVMStatFile = new File(uzPayloadFull+"/VMStat.txt");
				File aDSCPUFile = new File(uzPayloadFull+"/DSCPU.txt");
				File aDSBatteryFile = new File(uzPayloadFull+"/DSBattery.txt");
				File aDSConnFile = new File(uzPayloadFull+"/DSConn.txt");
				File aDSGeoFile = new File(uzPayloadFull+"/DSGeo.txt");
				File aASLSLogFile = new File(uzPayloadFull+"/asls.log");
				
				int aActiveCon = 0;
				
				Scanner aNetStatScanner = null; try {		
					aNetStatScanner = new Scanner(aNetStatFile);
					while(aNetStatScanner.hasNext()) {				
						String line = aNetStatScanner.nextLine();
						if(line.contains("ESTAB")) { aActiveCon++; }						
					}
				} catch (FileNotFoundException e) { e.printStackTrace(); }
			
				long aRmnet0Rx = 0;
				long aRmnet0Tx = 0;
				long aWlan0Rx = 0;
				long aWlan0Tx = 0;

				Scanner aIfStatsScanner = null; try {			
					aIfStatsScanner = new Scanner(aIfStatsFile);
					while(aIfStatsScanner.hasNext()) {		
						String line = aIfStatsScanner.nextLine();
						if(line.contains("rmnet0:")) {
							String[] lineTmp = line.split(",");
							String strThisVal = lineTmp[1]; aRmnet0Rx = Long.parseLong(strThisVal);
							String strThisVa2 = lineTmp[9]; aRmnet0Tx = Long.parseLong(strThisVa2);
						}
						if(line.contains("wlan0:")) {
							String[] lineTmp = line.split(",");
							String strThisVal = lineTmp[2]; aWlan0Rx = Long.parseLong(strThisVal);
							String strThisVa2 = lineTmp[10]; aWlan0Tx = Long.parseLong(strThisVa2);
						}
					}
				}
				catch (FileNotFoundException e) { e.printStackTrace(); }
				catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }

				int aMemoryBuffers = 0;
				int aMemoryFree = 0;
				int aMemoryShared = 0;
				int aMemoryTotal = 0;
				int aMemoryUse = 0;
				
				Scanner aVMStatScanner = null; try {			
					aVMStatScanner = new Scanner(aVMStatFile);
					while(aVMStatScanner.hasNext()) {		
						String line = aVMStatScanner.nextLine();
						if(line.contains("Mem:")) {
							String[] lineTmp = line.split(",");
							String strThisVal = lineTmp[5]; aMemoryBuffers = Integer.parseInt(strThisVal);
							String strThisVa2 = lineTmp[3]; aMemoryFree = Integer.parseInt(strThisVa2);
							String strThisVa3 = lineTmp[4]; aMemoryShared = Integer.parseInt(strThisVa3);
							String strThisVa4 = lineTmp[1]; aMemoryTotal = Integer.parseInt(strThisVa4);
							String strThisVa5 = lineTmp[2]; aMemoryUse = Integer.parseInt(strThisVa5);
						}
					}
				}
				catch (FileNotFoundException e) { e.printStackTrace(); }
				catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }

				double aLoadNow = 0.0;
				double aLoad5 = 0.0;
				double aLoad15 = 0.0;
				double aCPUUse = 0.0;
				
				Scanner aDSCPUScanner = null; try {		
					aDSCPUScanner = new Scanner(aDSCPUFile);
					while(aDSCPUScanner.hasNext()) {				
						String line = aDSCPUScanner.nextLine();					
						if(line.contains("Load:")) {
							String[] lineTmp = line.split(",");
							String strThisVal = lineTmp[1]; aLoadNow = Double.parseDouble(strThisVal);
							String strThisVa2 = lineTmp[3]; aLoad5 = Double.parseDouble(strThisVa2);
							String strThisVa3 = lineTmp[5]; aLoad15 = Double.parseDouble(strThisVa3);
						}
						if(line.contains("TOTAL:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2].replaceAll("\\D+", ""); aCPUUse = Double.parseDouble(strThisVal); }
					}
				}
				catch (FileNotFoundException e) { e.printStackTrace(); }
				catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }
				
				int aBattLevel = 0;
				int aBattVolt = 0;
				int aBattCurrent = 0;
				int aBattTemp = 0;
				String aBattPowered = null;
				String aBattPoweredU = null;
				int aBattHealth = 0;
			
				Scanner aDSBatteryScanner = null; try {			
					aDSBatteryScanner = new Scanner(aDSBatteryFile);
					while(aDSBatteryScanner.hasNext()) {				
						String line = aDSBatteryScanner.nextLine();
						if(line.contains(",level:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2]; aBattLevel = Integer.parseInt(strThisVal); }
						if(line.contains(",voltage:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2]; aBattVolt = Integer.parseInt(strThisVal); }
						if(line.contains(",current,now:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[3]; aBattCurrent = Integer.parseInt(strThisVal); }
						if(line.contains(",temperature:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2]; aBattTemp = Integer.parseInt(strThisVal); }
						if(line.contains(",AC,powered:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[3]; aBattPowered = strThisVal; }
						if(line.contains(",USB,powered:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[3]; aBattPoweredU = strThisVal; }
						if(line.contains(",health:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2]; aBattHealth = Integer.parseInt(strThisVal); }
					}
				}
				catch (FileNotFoundException e) { e.printStackTrace(); }
				catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }

				int aSigStrGSM = 0;
				int aSigStrCDMA = 0;
				int aSigStrEVDO = 0;
				int aSigStrLTE = 0;
				String aSigStrMode = null;
				int aCellIdentMCC = 0;
				int aCellIdentMNC = 0;
				int aCellIdentPCI = 0;

				Scanner aDSConnScanner = null; try {			
					aDSConnScanner = new Scanner(aDSConnFile);
					while(aDSConnScanner.hasNext()) {				
						String line = aDSConnScanner.nextLine();
						if(line.contains(",mSignalStrength=")) {
							String[] lineTmp = line.split(",");
							String strThisVal = lineTmp[2]; aSigStrGSM = Integer.parseInt(strThisVal);
							String strThisVa2 = lineTmp[4]; aSigStrCDMA = Integer.parseInt(strThisVa2);
							String strThisVa3 = lineTmp[7]; aSigStrEVDO = Integer.parseInt(strThisVa3);
							String strThisVa4 = lineTmp[10]; aSigStrLTE = Integer.parseInt(strThisVa4);
							String strThisVa5 = lineTmp[15]; aSigStrMode = strThisVa5;
						}
						if(line.contains(",mCellInfo=")) {
							String[] lineTmp = line.split(",");
							String strThisVal = lineTmp[5].replaceAll("\\D+", ""); aCellIdentMCC = Integer.parseInt(strThisVal);
							String strThisVa2 = lineTmp[6].replaceAll("\\D+", ""); aCellIdentMNC = Integer.parseInt(strThisVa2);
							String strThisVa3 = lineTmp[8].replaceAll("\\D+", ""); aCellIdentPCI = Integer.parseInt(strThisVa3);
						}
					}
				}
				catch (FileNotFoundException e) { e.printStackTrace(); }
				catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }

				String aLocationLon = null;
				String aLocationLat = null;

				Scanner aDSGeoScanner = null; try {			
					aDSGeoScanner = new Scanner(aDSGeoFile);
					while(aDSGeoScanner.hasNext()) {				
						String line = aDSGeoScanner.nextLine();
						if(line.contains(",passive:,Location")) {
							String[] lineTmp = line.split(",");
							String strThisVal = lineTmp[4]; aLocationLon = strThisVal;
							String strThisVa2 = lineTmp[3]; aLocationLat = strThisVa2;
						}
					}
				}
				catch (FileNotFoundException e) { e.printStackTrace(); }
				catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }

				String aRapidUpdateNumber = "";
				String aRapidTime = "";
				String aRapidLocationLat = "";
				String aRapidLocationLon = "";
				String aRapidSource = "";
				String rapidString = "";

				Scanner aASLSLogFileScanner = null; try {			
					aASLSLogFileScanner = new Scanner(aASLSLogFile);
					while(aASLSLogFileScanner.hasNext()) {				
						String line = aASLSLogFileScanner.nextLine();
						if(line.contains("EndLine")) {

							String[] lineTmp = line.split(",");
							String strThisVal = lineTmp[0].replaceAll("Update: ", ""); aRapidUpdateNumber = strThisVal;
							String strThisVa2 = lineTmp[1].replaceAll(" Time: ", ""); aRapidTime = strThisVa2;
							String strThisVa3 = lineTmp[2].replaceAll(" Latitude: ", ""); aRapidLocationLat = strThisVa3;
							String strThisVa4 = lineTmp[3].replaceAll(" Longitude:", ""); aRapidLocationLon = strThisVa4;
							String strThisVa5 = lineTmp[4].replaceAll(" Source:", ""); aRapidSource = strThisVa5;

							JSONObject jRapidObj = new JSONObject();
							JSONObject jRapidData = new JSONObject();
							if (StumpJunk.isSet(aRapidUpdateNumber)) { jRapidObj.put(aRapidUpdateNumber, jRapidData);
								if (StumpJunk.isSet(aRapidTime)) { jRapidData.put("Time", aRapidTime); }
								if (StumpJunk.isSet(aRapidLocationLat)) { jRapidData.put("Latitude", aRapidLocationLat); }
								if (StumpJunk.isSet(aRapidLocationLon)) { jRapidData.put("Longitude", aRapidLocationLon); }
								if (StumpJunk.isSet(aRapidSource)) { jRapidData.put("Source", aRapidSource); }						
								String thisJSONstring = jRapidObj.toString().substring(1);
								thisJSONstring = thisJSONstring.substring(0, thisJSONstring.length()-1)+",";
								rapidString += thisJSONstring;
							}
						}
					}
				}
				catch (FileNotFoundException e) { e.printStackTrace(); }
				catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }

				String aLocation = "["+aLocationLon+","+aLocationLat+"]";
				rapidString = ("{"+rapidString+"}").replace("\n","").replace(",}", "}");

				String aSQLQuery = "INSERT IGNORE INTO net_snmp.Note3 ("
					+ "WalkTime, WalkTimeMatcher, ActiveConn,"
					+ " rmnet0Rx, rmnet0Tx, wlan0Rx, wlan0Tx,"
					+ " MemoryBuffers, MemoryFree, MemoryShared, MemoryTotal, MemoryUse,"
					+ " LoadNow, Load5, Load15, CPUUse,"
					+ " BattLevel, BattVolt, BattCurrent, BattTemp, BattPowered, BattPoweredU, BattHealth,"
					+ " SigStrGSM, SigStrCDMA, SigStrEVDO, SigStrLTE, SigStrMode,"
					+ " CellIdentMCC, CellIdentMNC, CellIdentPCI,"
					+ " Location, RapidLocation"
					+ ") VALUES ("
					+ "'"+thisTimestamp+"',(SELECT MAX(WalkTime) FROM net_snmp.Main),"+aActiveCon+","
					+ aRmnet0Rx+","+aRmnet0Tx+","+aWlan0Rx+","+aWlan0Tx+","
					+ aMemoryBuffers+","+aMemoryFree+","+aMemoryShared+","+aMemoryTotal+","+aMemoryUse+","
					+ aLoadNow+","+aLoad5+","+aLoad15+","+aCPUUse+","
					+ aBattLevel+","+aBattVolt+","+aBattCurrent+","+aBattTemp+",'"+aBattPowered+"','"+aBattPoweredU+"',"+aBattHealth+","
					+ aSigStrGSM+","+aSigStrCDMA+","+aSigStrEVDO+","+aSigStrLTE+",'"+aSigStrMode+"',"
					+ aCellIdentMCC+","+aCellIdentMNC+","+aCellIdentPCI+","
					+ "'"+aLocation+"','"+rapidString+"'"
					+ ");";
				
				System.out.println(aSQLQuery);
				try ( Connection conn = MyDBConnector.getMyConnection(); Statement stmt = conn.createStatement();) { stmt.executeUpdate(aSQLQuery); }
				catch (SQLException se) { se.printStackTrace(); }
				catch (Exception e) { e.printStackTrace(); }

				aPayloadFile.delete();
				StumpJunk.deleteDir(uzPayloadF);
				
			}

			if(ePayloadFile.exists()) { 
				
				String uzEPayload = ramPath+"/ePayload";
				File uzEPayloadF = new File(uzEPayload);
				String uzEPayloadFull = uzEPayload+"/sdcard/ePLoad";

				StumpJunk.unTarGz(ePayload, uzEPayload);
				StumpJunk.sedFileReplace(uzEPayloadFull+"/NetStatE.txt", " +", ",");
				StumpJunk.sedFileReplace(uzEPayloadFull+"/IFStats.txt", " +", ",");
				StumpJunk.sedFileReplace(uzEPayloadFull+"/DSCPU.txt", " +", ",");
				StumpJunk.sedFileReplace(uzEPayloadFull+"/DSBattery.txt", " +", ",");
				StumpJunk.sedFileReplace(uzEPayloadFull+"/DSConn.txt", " +", ",");
				StumpJunk.sedFileReplace(uzEPayloadFull+"/DSGeo.txt", " +", ",");
				StumpJunk.sedFileReplace(uzEPayloadFull+"/VMStat.txt", " +", ",");

				File eNetStatFile = new File(uzEPayloadFull+"/NetStatE.txt");
				File eIfStatsFile = new File(uzEPayloadFull+"/IFStats.txt");
				File eVMStatFile = new File(uzEPayloadFull+"/VMStat.txt");
				File eDSCPUFile = new File(uzEPayloadFull+"/DSCPU.txt");
				File eDSBatteryFile = new File(uzEPayloadFull+"/DSBattery.txt");
				File eDSConnFile = new File(uzEPayloadFull+"/DSConn.txt");
				File eDSGeoFile = new File(uzEPayloadFull+"/DSGeo.txt");
				
				int eActiveCon = 0;
				
				Scanner eNetStatScanner = null; try {		
					eNetStatScanner = new Scanner(eNetStatFile);
					while(eNetStatScanner.hasNext()) {				
						String line = eNetStatScanner.nextLine();
						if(line.contains("ESTAB,")) { eActiveCon++; }
					}
				} catch (FileNotFoundException e) { e.printStackTrace(); }

				long eRmnet0Rx = 0;
				long eRmnet0Tx = 0;
				long eWlan0Rx = 0;
				long eWlan0Tx = 0;

				Scanner eIfStatsScanner = null; try {			
					eIfStatsScanner = new Scanner(eIfStatsFile);
					while(eIfStatsScanner.hasNext()) {		
						String line = eIfStatsScanner.nextLine();
						if(line.contains("rmnet_usb0:")) {
							String[] lineTmp = line.split(",");
							String strThisVal = lineTmp[1]; eRmnet0Rx = Long.parseLong(strThisVal);
							String strThisVa2 = lineTmp[9]; eRmnet0Tx = Long.parseLong(strThisVa2);
						}
						if(line.contains("wlan0:")) {
							String[] lineTmp = line.split(",");
							String strThisVal = lineTmp[2]; eWlan0Rx = Long.parseLong(strThisVal);
							String strThisVa2 = lineTmp[10]; eWlan0Tx = Long.parseLong(strThisVa2);
						}
					}
				}
				catch (FileNotFoundException e) { e.printStackTrace(); }
				catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }

				int eMemoryBuffers = 0;
				int eMemoryFree = 0;
				int eMemoryShared = 0;
				int eMemoryTotal = 0;
				int eMemoryUse = 0;
				
				Scanner eVMStatScanner = null; try {			
					eVMStatScanner = new Scanner(eVMStatFile);
					while(eVMStatScanner.hasNext()) {		
						String line = eVMStatScanner.nextLine();
						if(line.contains("Mem:")) {
							String[] lineTmp = line.split(",");
							String strThisVal = lineTmp[5]; eMemoryBuffers = Integer.parseInt(strThisVal);
							String strThisVa2 = lineTmp[3]; eMemoryFree = Integer.parseInt(strThisVa2);
							String strThisVa3 = lineTmp[4]; eMemoryShared = Integer.parseInt(strThisVa3);
							String strThisVa4 = lineTmp[1]; eMemoryTotal = Integer.parseInt(strThisVa4);
							String strThisVa5 = lineTmp[2]; eMemoryUse = Integer.parseInt(strThisVa5);
						}
					}
				}
				catch (FileNotFoundException e) { e.printStackTrace(); }
				catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }

				double eLoadNow = 0.0;
				double eLoad5 = 0.0;
				double eLoad15 = 0.0;
				double eCPUUse = 0.0;
				
				Scanner eDSCPUScanner = null; try {		
					eDSCPUScanner = new Scanner(eDSCPUFile);
					while(eDSCPUScanner.hasNext()) {				
						String line = eDSCPUScanner.nextLine();					
						if(line.contains("Load:")) {
							String[] lineTmp = line.split(",");
							String strThisVal = lineTmp[1]; eLoadNow = Double.parseDouble(strThisVal);
							String strThisVa2 = lineTmp[3]; eLoad5 = Double.parseDouble(strThisVa2);
							String strThisVa3 = lineTmp[5]; eLoad15 = Double.parseDouble(strThisVa3);
						}
						if(line.contains("TOTAL:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2].replaceAll("\\D+", ""); eCPUUse = Double.parseDouble(strThisVal); }
					}
				}
				catch (FileNotFoundException e) { e.printStackTrace(); }
				catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }
				
				int eBattLevel = 0;
				int eBattVolt = 0;
				int eBattCurrent = 0;
				int eBattTemp = 0;
				String eBattPowered = null;
				String eBattPoweredU = null;
				int eBattHealth = 0;
			
				Scanner eDSBatteryScanner = null; try {			
					eDSBatteryScanner = new Scanner(eDSBatteryFile);
					while(eDSBatteryScanner.hasNext()) {				
						String line = eDSBatteryScanner.nextLine();
						if(line.contains(",level:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2]; eBattLevel = Integer.parseInt(strThisVal); }
						if(line.contains(",voltage:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2]; eBattVolt = Integer.parseInt(strThisVal); }
						if(line.contains(",current,now:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[3]; eBattCurrent = Integer.parseInt(strThisVal); }
						if(line.contains(",temperature:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2]; eBattTemp = Integer.parseInt(strThisVal); }
						if(line.contains(",AC,powered:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[3]; eBattPowered = strThisVal; }
						if(line.contains(",USB,powered:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[3]; eBattPoweredU = strThisVal; }
						if(line.contains(",health:")) { String[] lineTmp = line.split(","); String strThisVal = lineTmp[2]; eBattHealth = Integer.parseInt(strThisVal); }
					}
				}
				catch (FileNotFoundException e) { e.printStackTrace(); }
				catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }

				int eSigStrGSM = 0;
				int eSigStrCDMA = 0;
				int eSigStrEVDO = 0;
				int eSigStrLTE = 0;
				String eSigStrMode = null;
				int eCellIdentMCC = 0;
				int eCellIdentMNC = 0;
				int eCellIdentPCI = 0;

				Scanner eDSConnScanner = null; try {			
					eDSConnScanner = new Scanner(eDSConnFile);
					while(eDSConnScanner.hasNext()) {				
						String line = eDSConnScanner.nextLine();
						if(line.contains(",mSignalStrength=")) {
							String[] lineTmp = line.split(",");
							String strThisVal = lineTmp[2]; eSigStrGSM = Integer.parseInt(strThisVal);
							String strThisVa2 = lineTmp[4]; eSigStrCDMA = Integer.parseInt(strThisVa2);
							String strThisVa3 = lineTmp[7]; eSigStrEVDO = Integer.parseInt(strThisVa3);
							String strThisVa4 = lineTmp[10]; eSigStrLTE = Integer.parseInt(strThisVa4);
							String strThisVa5 = lineTmp[15]; eSigStrMode = strThisVa5;
						}
						if(line.contains(",mCellInfo=")) {
							String[] lineTmp = line.split(",");
							String strThisVal = lineTmp[5].replaceAll("\\D+", ""); eCellIdentMCC = Integer.parseInt(strThisVal);
							String strThisVa2 = lineTmp[6].replaceAll("\\D+", ""); eCellIdentMNC = Integer.parseInt(strThisVa2);
							String strThisVa3 = lineTmp[8].replaceAll("\\D+", ""); eCellIdentPCI = Integer.parseInt(strThisVa3);
						}
					}
				}
				catch (FileNotFoundException e) { e.printStackTrace(); }
				catch (ArrayIndexOutOfBoundsException aix) { aix.printStackTrace(); }

				String eLocationLon = null;
				String eLocationLat = null;

				Scanner eDSGeoScanner = null; try {			
					eDSGeoScanner = new Scanner(eDSGeoFile);
					while(eDSGeoScanner.hasNext()) {				
						String line = eDSGeoScanner.nextLine();
						if(line.contains(",passive:,Location")) {
							String[] lineTmp = line.split(",");
							String strThisVal = lineTmp[4]; eLocationLon = strThisVal;
							String strThisVa2 = lineTmp[3]; eLocationLat = strThisVa2;
						}
					}
				} catch (FileNotFoundException e) { e.printStackTrace(); }

				String eLocation = "["+eLocationLon+","+eLocationLat+"]";

				String eSQLQuery = "INSERT IGNORE INTO net_snmp.EmS4 ("
					+ "WalkTime, WalkTimeMatcher, ActiveConn,"
					+ " rmnet0Rx, rmnet0Tx, wlan0Rx, wlan0Tx,"
					+ " MemoryBuffers, MemoryFree, MemoryShared, MemoryTotal, MemoryUse,"
					+ " LoadNow, Load5, Load15, CPUUse,"
					+ " BattLevel, BattVolt, BattCurrent, BattTemp, BattPowered, BattPoweredU, BattHealth,"
					+ " SigStrGSM, SigStrCDMA, SigStrEVDO, SigStrLTE, SigStrMode,"
					+ " CellIdentMCC, CellIdentMNC, CellIdentPCI, Location"
					+ ") VALUES ("
					+ "'"+thisTimestamp+"',(SELECT MAX(WalkTime) FROM net_snmp.Main),"+eActiveCon+","
					+ eRmnet0Rx+","+eRmnet0Tx+","+eWlan0Rx+","+eWlan0Tx+","
					+ eMemoryBuffers+","+eMemoryFree+","+eMemoryShared+","+eMemoryTotal+","+eMemoryUse+","
					+ eLoadNow+","+eLoad5+","+eLoad15+","+eCPUUse+","
					+ eBattLevel+","+eBattVolt+","+eBattCurrent+","+eBattTemp+",'"+eBattPowered+"','"+eBattPoweredU+"',"+eBattHealth+","
					+ eSigStrGSM+","+eSigStrCDMA+","+eSigStrEVDO+","+eSigStrLTE+",'"+eSigStrMode+"',"
					+ eCellIdentMCC+","+eCellIdentMNC+","+eCellIdentPCI+",'"+eLocation+"'"
					+ ");";

				System.out.println(eSQLQuery);
				try ( Connection conn = MyDBConnector.getMyConnection(); Statement stmt = conn.createStatement();) { stmt.executeUpdate(eSQLQuery); }
				catch (SQLException se) { se.printStackTrace(); }
				catch (Exception e) { e.printStackTrace(); }

				ePayloadFile.delete();
				StumpJunk.deleteDir(uzEPayloadF);
					
			}
	
		}

		if (thisNode.equals("Router")) {

			final String routerUser = /* INSERT */;
			final String routerIP = /* INSERT */;
			final String snmpRtrPass = /* INSERT */;
			final File rtrWalkFile = new File(ramPath+"/snmpwalkR.txt");
			try { StumpJunk.runProcessOutFile("snmpwalk -v3 -l authPriv -u "+routerUser+" -a MD5 -A "+snmpRtrPass+" -x DES -X "+snmpRtrPass+" "+routerIP+" .", rtrWalkFile, false); } catch (FileNotFoundException fe) { fe.printStackTrace(); }

			int rCPULoad1 = 0;
			int rCPULoad2 = 0;
			long rEth0Rx = 0;
			long rEth0Tx = 0;
			long rEth1Rx = 0;
			long rEth1Tx = 0;
			long rEth2Rx = 0;
			long rEth2Tx = 0;
			long rEth3Rx = 0;
			long rEth3Tx = 0;
			long rVlan1Rx = 0;
			long rVlan1Tx = 0;
			long rVlan2Rx = 0;
			long rVlan2Tx = 0;
			long rBr0Rx = 0;
			long rBr0Tx = 0;
			int rKMemPhys = 0;
			int rKMemVirt = 0;
			int rKMemBuff = 0;
			int rKMemCached = 0;
			int rKMemShared = 0;
			int rKSwap = 0;
			int rK4Root = 0;
			int rKMemPhysU = 0;
			int rKMemVirtU = 0;
			int rKMemBuffU = 0;
			int rKMemCachedU = 0;
			int rKMemSharedU = 0;
			int rKSwapU = 0;
			int rK4RootU = 0;

			Scanner rWalkFileScanner = null; try {		
				rWalkFileScanner = new Scanner(rtrWalkFile);
				while(rWalkFileScanner.hasNext()) {				
					String line = rWalkFileScanner.nextLine();
					if(line.contains("hrProcessorLoad.196608 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rCPULoad1 = Integer.parseInt(m.group(1)); } }
					if(line.contains("hrProcessorLoad.196609 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rCPULoad2 = Integer.parseInt(m.group(1)); } }
					if(line.contains("ifHCInOctets.4 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rEth0Rx = Long.parseLong(m.group(1)); } }
					if(line.contains("ifHCOutOctets.4 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rEth0Tx = Long.parseLong(m.group(1)); } }	
					if(line.contains("ifHCInOctets.11 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rEth1Rx = Long.parseLong(m.group(1)); } }
					if(line.contains("ifHCOutOctets.11 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rEth1Tx = Long.parseLong(m.group(1)); } }	
					if(line.contains("ifHCInOctets.12 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rEth2Rx = Long.parseLong(m.group(1)); } }
					if(line.contains("ifHCOutOctets.12 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rEth2Tx = Long.parseLong(m.group(1)); } }	
					if(line.contains("ifHCInOctets.13 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rEth3Rx = Long.parseLong(m.group(1)); } }
					if(line.contains("ifHCOutOctets.13 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rEth3Tx = Long.parseLong(m.group(1)); } }	
					if(line.contains("ifHCInOctets.8 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rVlan1Rx = Long.parseLong(m.group(1)); } }
					if(line.contains("ifHCOutOctets.8 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rVlan1Tx = Long.parseLong(m.group(1)); } }	
					if(line.contains("ifHCInOctets.9 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rVlan2Rx = Long.parseLong(m.group(1)); } }
					if(line.contains("ifHCOutOctets.9 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rVlan2Tx = Long.parseLong(m.group(1)); } }
					if(line.contains("ifHCInOctets.14 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rBr0Rx = Long.parseLong(m.group(1)); } }
					if(line.contains("ifHCOutOctets.14 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rBr0Tx = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.1 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rKMemPhys = Integer.parseInt(m.group(1)); } }
					if(line.contains("hrStorageSize.3 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rKMemVirt = Integer.parseInt(m.group(1)); } }
					if(line.contains("hrStorageSize.6 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rKMemBuff = Integer.parseInt(m.group(1)); } }
					if(line.contains("hrStorageSize.7 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rKMemCached = Integer.parseInt(m.group(1)); } }
					if(line.contains("hrStorageSize.8 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rKMemShared = Integer.parseInt(m.group(1)); } }
					if(line.contains("hrStorageSize.10 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rKSwap = Integer.parseInt(m.group(1)); } }
					if(line.contains("hrStorageSize.31 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rK4Root = Integer.parseInt(m.group(1)); } }
					if(line.contains("hrStorageUsed.1 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rKMemPhysU = Integer.parseInt(m.group(1)); } }
					if(line.contains("hrStorageUsed.3 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rKMemVirtU = Integer.parseInt(m.group(1)); } }
					if(line.contains("hrStorageUsed.6 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rKMemBuffU = Integer.parseInt(m.group(1)); } }
					if(line.contains("hrStorageUsed.7 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rKMemCachedU = Integer.parseInt(m.group(1)); } }
					if(line.contains("hrStorageUsed.8 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rKMemSharedU = Integer.parseInt(m.group(1)); } }
					if(line.contains("hrStorageUsed.10 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rKSwapU = Integer.parseInt(m.group(1)); } }
					if(line.contains("hrStorageUsed.31 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { rK4RootU = Integer.parseInt(m.group(1)); } }
				}
			} catch (FileNotFoundException e) { e.printStackTrace(); }
	
			String rSQLQuery = "INSERT IGNORE INTO net_snmp.Asus3200 ("
				+ "WalkTime, WalkTimeMatcher, CPULoad1, CPULoad2,"
				+ " eth0Rx, eth0Tx, eth1Rx, eth1Tx, eth2Rx, eth2Tx, eth3Rx, eth3Tx,"
				+ " vlan1Rx, vlan1Tx, vlan2Rx, vlan2Tx, br0Rx, br0Tx,"
				+ " KMemPhys, KMemVirt, KMemBuff, KMemCached, KMemShared, KSwap, K4Root,"
				+ " KMemPhysU, KMemVirtU, KMemBuffU, KMemCachedU, KMemSharedU, KSwapU, K4RootU"
				+ ") VALUES ("
				+ "'"+thisTimestamp+"', (SELECT MAX(WalkTime) FROM net_snmp.Main), "+rCPULoad1+","+rCPULoad2+","
				+ rEth0Rx+","+rEth0Tx+","+rEth1Rx+","+rEth1Tx+","+rEth2Rx+","+rEth2Tx+","+rEth3Rx+","+rEth3Tx+","
				+ rVlan1Rx+","+rVlan1Tx+","+rVlan2Rx+","+rVlan2Tx+","+rBr0Rx+","+rBr0Tx+","
				+ rKMemPhys+","+rKMemVirt+","+rKMemBuff+","+rKMemCached+","+rKMemShared+","+rKSwap+","+rK4Root+","
				+ rKMemPhysU+","+rKMemVirtU+","+rKMemBuffU+","+rKMemCachedU+","+rKMemSharedU+","+rKSwapU+","+rK4RootU
				+ ");";
				
			System.out.println(rSQLQuery);
			try ( Connection conn = MyDBConnector.getMyConnection(); Statement stmt = conn.createStatement();) { stmt.executeUpdate(rSQLQuery); }
			catch (SQLException se) { se.printStackTrace(); }
			catch (Exception e) { e.printStackTrace(); }
		}

		if (thisNode.equals("Pi")) {

			final String snmpPiPass = /* PUT IT IN! */
			final File piWalkFile = new File(ramPath+"/snmpwalkPi.txt");
			final File pioSerialMonFile = new File(ramPath+"/pioSerialMon.log");
			try { StumpJunk.runProcessOutFile("snmpwalk -v3 -l authPriv -u xxxxxx -a MD5 -A "+snmpPiPass+" -x DES -X "+snmpPiPass+" x.x.x.x .", piWalkFile, false); } catch (FileNotFoundException fe) { fe.printStackTrace(); }
			StumpJunk.runProcess("(printf \"lcd "+ramPath+"\nget /dev/shm/pioSerialMon.log\" | sudo -u xxxxxx sftp -i "+keyPath+"/Desktop2Pi -P xx pi@x.x.x.x)");
	
			int piNumUsers = 0;
			int piCPULoad1 = 0;
			int piCPULoad2 = 0;
			int piCPULoad3 = 0;
			int piCPULoad4 = 0;
			double piLoadIndex1 = 0;
			double piLoadIndex5 = 0;
			double piLoadIndex15 = 0;
			int piProcesses = 0;
			long piEthIn = 0;
			long piEthOut = 0;
			long piWiFiIn = 0;
			long piWiFiOut = 0;
			long piKMemPhys = 0;
			long piKMemVirt = 0;
			long piKMemBuff = 0;
			long piKMemCached = 0;
			long piKMemShared = 0;
			long piKSwap = 0;
			long piK4Root = 0;
			long piKMemPhysU = 0;
			long piKMemVirtU = 0;
			long piKMemBuffU = 0;
			long piKMemCachedU = 0;
			long piKMemSharedU = 0;
			long piKSwapU = 0;
			long piK4RootU = 0;
			
			Scanner piWalkFileScanner = null; try {		
				piWalkFileScanner = new Scanner(piWalkFile);
				while(piWalkFileScanner.hasNext()) {				
					String line = piWalkFileScanner.nextLine();
					if(line.contains("hrSystemNumUsers.0 =")) { Pattern p = Pattern.compile("Gauge32: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piNumUsers = Integer.parseInt(m.group(1)); }}
					if(line.contains("hrProcessorLoad.196608 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piCPULoad1 = Integer.parseInt(m.group(1)); }}
					if(line.contains("hrProcessorLoad.196609 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piCPULoad2 = Integer.parseInt(m.group(1)); } }					
					if(line.contains("hrProcessorLoad.196610 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piCPULoad3 = Integer.parseInt(m.group(1)); } }					
					if(line.contains("hrProcessorLoad.196611 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piCPULoad4 = Integer.parseInt(m.group(1)); } }					
					if(line.contains("laLoad.1 =")) { Pattern p = Pattern.compile("STRING: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piLoadIndex1 = Double.parseDouble(m.group(1)); } }
					if(line.contains("laLoad.2 =")) { Pattern p = Pattern.compile("STRING: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piLoadIndex5 = Double.parseDouble(m.group(1)); } }
					if(line.contains("laLoad.3 =")) { Pattern p = Pattern.compile("STRING: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piLoadIndex15 = Double.parseDouble(m.group(1)); } }
					if(line.contains("hrSystemProcesses.0 =")) { Pattern p = Pattern.compile("Gauge32: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piProcesses = Integer.parseInt(m.group(1)); } }
					if(line.contains("ifHCInOctets.2 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piEthIn = Long.parseLong(m.group(1)); } }
					if(line.contains("ifHCOutOctets.2 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piEthOut = Long.parseLong(m.group(1)); } }
					if(line.contains("ifHCInOctets.3 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piWiFiIn = Long.parseLong(m.group(1)); } }
					if(line.contains("ifHCOutOctets.3 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piWiFiOut = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.1 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemPhys = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.3 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemVirt = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.6 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemBuff = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.7 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemCached = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.8 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemShared = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.10 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKSwap = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.31 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piK4Root = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.1 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemPhysU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.3 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemVirtU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.6 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemBuffU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.7 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemCachedU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.8 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemSharedU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.10 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKSwapU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.31 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piK4RootU = Long.parseLong(m.group(1)); } }
	
				}
			} catch (FileNotFoundException e) { e.printStackTrace(); }

			StumpJunk.sedFileReplace(pioSerialMonFile.getPath(), "\\n\\n", "\n");

			double piExtTemp = 0.0;
			int piExtAmbLight = 0;
			int piExtNoise = 0;
			long piRunSec = 0;
			String piExtIRLEDs = "";

			try {
				FileInputStream inSerialMon = new FileInputStream(pioSerialMonFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(inSerialMon));
				String serialLine = null, tmpLine = null;
				while ((tmpLine = br.readLine()) != null) { serialLine = tmpLine; }
				String line = serialLine;
				if(line.contains("IRLeds:")) {
					System.out.println(line);
					String[] lineTmp = line.split(",");
					piExtTemp = Double.parseDouble(lineTmp[0].replace("TempF: ", ""));
					piExtAmbLight = Integer.parseInt(lineTmp[1].replace(" AmbLight: ", ""));
					piExtIRLEDs = lineTmp[2].replace(" IRLeds: ", "");
					piExtNoise = Integer.parseInt(lineTmp[3].replace(" Noise: ", ""));
					piRunSec = Long.parseLong(lineTmp[4].replace(" Seconds: ", ""));
				}
				inSerialMon.close();
			}
			catch (FileNotFoundException fnf) { fnf.printStackTrace(); }
			catch (IOException ix) { ix.printStackTrace(); }

			String piSQLQuery = "INSERT IGNORE INTO net_snmp.RaspberryPi ("
				+ "WalkTime, WalkTimeMatcher, NumUsers, Processes,"
				+ " CPULoad, CPULoad2, CPULoad3, CPULoad4,"
				+ " LoadIndex1, LoadIndex5, LoadIndex15,"
				+ " OctetsIn, OctetsOut, WiFiOctetsIn, WiFiOctetsOut,"
				+ " KMemPhys, KMemVirt, KMemBuff, KMemCached, KMemShared, KSwap, K4Root,"
				+ " KMemPhysU, KMemVirtU, KMemBuffU, KMemCachedU, KMemSharedU, KSwapU, K4RootU,"
				+ " ExtTemp, ExtAmbLight, ExtIRLEDs, ExtNoise, UptimeSec"
				+ ") VALUES ("
				+ "'"+thisTimestamp+"', (SELECT MAX(WalkTime) FROM net_snmp.Main), "+piNumUsers+","+piProcesses+","
				+ piCPULoad1+","+piCPULoad2+","+piCPULoad3+","+piCPULoad4+","
				+ piLoadIndex1+","+piLoadIndex5+","+piLoadIndex15+","
				+ piEthIn+","+piEthOut+","+piWiFiIn+","+piWiFiOut+","
				+ piKMemPhys+","+piKMemVirt+","+piKMemBuff+","+piKMemCached+","+piKMemShared+","+piKSwap+","+piK4Root+","
				+ piKMemPhysU+","+piKMemVirtU+","+piKMemBuffU+","+piKMemCachedU+","+piKMemSharedU+","+piKSwapU+","+piK4RootU+","
				+ piExtTemp+","+piExtAmbLight+",'"+piExtIRLEDs+"',"+piExtNoise+","+piRunSec
				+ ");";		
	
			System.out.println(piSQLQuery);
			try ( Connection conn = MyDBConnector.getMyConnection(); Statement stmt = conn.createStatement();) { stmt.executeUpdate(piSQLQuery); }
			catch (SQLException se) { se.printStackTrace(); }
			catch (Exception e) { e.printStackTrace(); }

		}

		if (thisNode.equals("Pi2")) {

			final String snmpPiPass = /* PUT IT IN */
			final File pi2WalkFile = new File(ramPath+"/snmpwalkPi2.txt");
			try { StumpJunk.runProcessOutFile("snmpwalk -v3 -l authPriv -u xxxxx -a MD5 -A "+snmpPiPass+" -x DES -X "+snmpPiPass+" x.x.x.x .", pi2WalkFile, false); } catch (FileNotFoundException fe) { fe.printStackTrace(); }
			final File pioSerialMonFile2 = new File(ramPath+"/pioSerialMon2.log");
			StumpJunk.runProcess("(printf \"lcd "+ramPath+"\nget /dev/shm/pioSerialMon2.log\" | sudo -u xxxxx sftp -i "+keyPath+"/forPi2 -P xxxxx pi@x.x.x.x)");

			int piNumUsers = 0;
			int piCPULoad1 = 0;
			int piCPULoad2 = 0;
			int piCPULoad3 = 0;
			int piCPULoad4 = 0;
			double piLoadIndex1 = 0;
			double piLoadIndex5 = 0;
			double piLoadIndex15 = 0;
			int piProcesses = 0;
			long piEthIn = 0;
			long piEthOut = 0;
			long piWiFiIn = 0;
			long piWiFiOut = 0;
			long piKMemPhys = 0;
			long piKMemVirt = 0;
			long piKMemBuff = 0;
			long piKMemCached = 0;
			long piKMemShared = 0;
			long piKSwap = 0;
			long piK4Root = 0;
			long piKMemPhysU = 0;
			long piKMemVirtU = 0;
			long piKMemBuffU = 0;
			long piKMemCachedU = 0;
			long piKMemSharedU = 0;
			long piKSwapU = 0;
			long piK4RootU = 0;
			
			Scanner pi2WalkFileScanner = null; try {		
				pi2WalkFileScanner = new Scanner(pi2WalkFile);
				while(pi2WalkFileScanner.hasNext()) {				
					String line = pi2WalkFileScanner.nextLine();
					if(line.contains("hrSystemNumUsers.0 =")) { Pattern p = Pattern.compile("Gauge32: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piNumUsers = Integer.parseInt(m.group(1)); }}
					if(line.contains("hrProcessorLoad.196608 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piCPULoad1 = Integer.parseInt(m.group(1)); }}
					if(line.contains("hrProcessorLoad.196609 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piCPULoad2 = Integer.parseInt(m.group(1)); } }					
					if(line.contains("hrProcessorLoad.196610 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piCPULoad3 = Integer.parseInt(m.group(1)); } }					
					if(line.contains("hrProcessorLoad.196611 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piCPULoad4 = Integer.parseInt(m.group(1)); } }					
					if(line.contains("laLoad.1 =")) { Pattern p = Pattern.compile("STRING: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piLoadIndex1 = Double.parseDouble(m.group(1)); } }
					if(line.contains("laLoad.2 =")) { Pattern p = Pattern.compile("STRING: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piLoadIndex5 = Double.parseDouble(m.group(1)); } }
					if(line.contains("laLoad.3 =")) { Pattern p = Pattern.compile("STRING: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piLoadIndex15 = Double.parseDouble(m.group(1)); } }
					if(line.contains("hrSystemProcesses.0 =")) { Pattern p = Pattern.compile("Gauge32: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piProcesses = Integer.parseInt(m.group(1)); } }
					if(line.contains("ifHCInOctets.2 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piEthIn = Long.parseLong(m.group(1)); } }
					if(line.contains("ifHCOutOctets.2 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piEthOut = Long.parseLong(m.group(1)); } }
					if(line.contains("ifHCInOctets.3 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piWiFiIn = Long.parseLong(m.group(1)); } }
					if(line.contains("ifHCOutOctets.3 =")) { Pattern p = Pattern.compile("Counter64: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piWiFiOut = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.1 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemPhys = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.3 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemVirt = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.6 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemBuff = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.7 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemCached = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.8 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemShared = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.10 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKSwap = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageSize.31 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piK4Root = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.1 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemPhysU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.3 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemVirtU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.6 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemBuffU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.7 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemCachedU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.8 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKMemSharedU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.10 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piKSwapU = Long.parseLong(m.group(1)); } }
					if(line.contains("hrStorageUsed.31 =")) { Pattern p = Pattern.compile("INTEGER: (.*)"); Matcher m = p.matcher(line); if (m.find()) { piK4RootU = Long.parseLong(m.group(1)); } }
	
				}
			} catch (FileNotFoundException e) { e.printStackTrace(); }

			StumpJunk.sedFileReplace(pioSerialMonFile2.getPath(), "\\n\\n", "\n");

			double piExtTemp = 0.0;
			long piRunSec = 0;
			double piGPSLat = 0.0;
			double piGPSLon = 0.0;
			int piGPSAge = 0;
			int piGPSCourse = 0;
			int piGPSAlti = 0;
			double piGPSSpeed = 0.0;
			long piGPSChars = 0;
			long piGPSFails = 0;
			int piHallAState = 0;
			int piHallBState = 0;
			float piGPSEstDist = 0.0f;
			int piLightLevel = 0;
			int piSwitchState = 0;

			try {
				FileInputStream inSerialMon = new FileInputStream(pioSerialMonFile2);
				BufferedReader br = new BufferedReader(new InputStreamReader(inSerialMon));
				String serialLine = null, tmpLine = null;
				while ((tmpLine = br.readLine()) != null) { serialLine = tmpLine; }
				String line = serialLine;
				if(line.contains("TempF:")) {
					System.out.println(line);
					String[] lineTmp = line.split(",");
					piRunSec = Long.parseLong(lineTmp[0].replace("Seconds: ", ""));
					piExtTemp = Double.parseDouble(lineTmp[1].replace(" TempF: ", ""));
					piGPSLat = Double.parseDouble(lineTmp[2].replace(" Lat: ", ""));
					piGPSLon = Double.parseDouble(lineTmp[3].replace(" Lon: ", ""));
					piGPSAge = Integer.parseInt(lineTmp[4].replace(" FixAgeMS: ", ""));
					piGPSAlti = Integer.parseInt(lineTmp[7].replace(" AltitCM: ", ""));
					piGPSCourse = Integer.parseInt(lineTmp[8].replace(" Course: ", ""));
					piGPSSpeed = Double.parseDouble(lineTmp[9].replace(" SpeedMPH: ", ""));
					piGPSChars = Long.parseLong(lineTmp[10].replace(" GPSDataUseChar: ", ""));
					piGPSFails = Long.parseLong(lineTmp[11].replace(" GPSCheckSumFail: ", ""));
					piHallAState = Integer.parseInt(lineTmp[12].replace(" HallAState: ",""));
					piHallBState = Integer.parseInt(lineTmp[13].replace(" HallBState: ",""));
					piGPSEstDist = Float.parseFloat(lineTmp[14].replace(" EstDistMI: ", ""));
					piLightLevel = Integer.parseInt(lineTmp[15].replace(" LightLevel: ", ""));
					piSwitchState = Integer.parseInt(lineTmp[16].replace(" SwitchState: ", ""));
				}
				inSerialMon.close();
			}
			catch (FileNotFoundException fnf) { fnf.printStackTrace(); }
			catch (IOException ix) { ix.printStackTrace(); }

			String pi2SQLQuery = "INSERT IGNORE INTO net_snmp.RaspberryPi2 ("
				+ "WalkTime, WalkTimeMatcher, NumUsers, Processes,"
				+ " CPULoad, CPULoad2, CPULoad3, CPULoad4,"
				+ " LoadIndex1, LoadIndex5, LoadIndex15,"
				+ " OctetsIn, OctetsOut, WiFiOctetsIn, WiFiOctetsOut,"
				+ " KMemPhys, KMemVirt, KMemBuff, KMemCached, KMemShared, KSwap, K4Root,"
				+ " KMemPhysU, KMemVirtU, KMemBuffU, KMemCachedU, KMemSharedU, KSwapU, K4RootU,"
				+ " UptimeSec, ExtTemp, GPSCoords, GPSAgeMS, GPSCourse, GPSAltiCM, GPSSpeedMPH,"
				+ " GPSDataChars, GPSChecksumFails, HallAState, HallBState, GPSEstDist,"
				+ " LightLevel, SwitchState"
				+ ") VALUES ("
				+ "'"+thisTimestamp+"', (SELECT MAX(WalkTime) FROM net_snmp.Main), "+piNumUsers+","+piProcesses+","
				+ piCPULoad1+","+piCPULoad2+","+piCPULoad3+","+piCPULoad4+","
				+ piLoadIndex1+","+piLoadIndex5+","+piLoadIndex15+","
				+ piEthIn+","+piEthOut+","+piWiFiIn+","+piWiFiOut+","
				+ piKMemPhys+","+piKMemVirt+","+piKMemBuff+","+piKMemCached+","+piKMemShared+","+piKSwap+","+piK4Root+","
				+ piKMemPhysU+","+piKMemVirtU+","+piKMemBuffU+","+piKMemCachedU+","+piKMemSharedU+","+piKSwapU+","+piK4RootU+","
				+ piRunSec+","+piExtTemp+",'["+piGPSLon+","+piGPSLat+"]',"+piGPSAge+","+piGPSCourse+","+piGPSAlti+","+piGPSSpeed+","
				+ piGPSChars+","+piGPSFails+","+piHallAState+","+piHallBState+",'"+piGPSEstDist+"',"
				+ piLightLevel+","+piSwitchState
				+ ");";		
	
			System.out.println(pi2SQLQuery);
			try ( Connection conn = MyDBConnector.getMyConnection(); Statement stmt = conn.createStatement();) { stmt.executeUpdate(pi2SQLQuery); }
			catch (SQLException se) { se.printStackTrace(); }
			catch (Exception e) { e.printStackTrace(); }

		}
	}
		
}
