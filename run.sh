gnome-terminal -x bash -c "cd ws-handlers; mvn install"
sleep 5
gnome-terminal -x bash -c "./gen_keys.sh UpaBroker UpaTransporter1 UpaTransporter2"
sleep 5
gnome-terminal -x bash -c "cd ca-ws ; mvn clean compile exec:java"
i="1"
while [ $i -le 2 ]
do
gnome-terminal -x bash -c "cd transporter-ws ; mvn clean compile -Dws.i=$i exec:java"
i=$[$i+1]
done
sleep 8;
gnome-terminal -x bash -c "cd broker-ws ; mvn clean compile -Dws.i=9 -Dws.name=UpaBrokerSec exec:java"
sleep 8;
gnome-terminal -x bash -c "cd broker-ws ; mvn clean compile exec:java"
