# Private stuff snipped out
# You must have a rooted phone.

if [ $1 = "P" ]; then
	ASLSLogFile="/storage/sdcard0/asls.log"
	Key=$KeyFile
	Port=$Port
	PLPath="/storage/sdcard0/aPLoad"
	zipPL="/storage/sdcard0/aPayload.zip"
	while [ 1 -eq 1 ]; do
		mkdir -p $PLPath
		cp $ASLSLogFile $PLPath
		echo "" > $ASLSLogFile
		netstat -W | grep ESTAB > $PLPath/NetStatE.txt
		echo "logcat -t 4096" | su > $PLPath/LogCat.txt
		free > $PLPath/VMStat.txt
		top -n 1 > $PLPath/Topless.txt
		cat /proc/net/dev > $PLPath/IFStats.txt
		echo "dumpsys cpuinfo" | su > $PLPath/DSCPU.txt
		echo "dumpsys telephony.registry" | su > $PLPath/DSConn.txt
		echo "dumpsys battery" | su > $PLPath/DSBattery.txt
		echo "dumpsys location" | su > $PLPath/DSGeo.txt
		zip -9rv $zipPL $PLPath/*
		rm -fr $PLPath
		echo "put $zipPL" | sftp -i $Key -P $Port astump@$Host
		rm $zipPL
		sleep 90;
	done
fi
