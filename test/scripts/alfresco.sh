#!/bin/sh
#
# Start/Stop the Alfresco server and associated processes (ie. soffice and Xvfb)
#

APPSERVER=/home/chatch/alfresco/tomcat6
OOFFICE_SCRIPT=/etc/init.d/soffice.sh
ALFRESCO_USER=chatch

#
# Functions
#
getTomcatPid() {
  for PID in `pidof java`
  do
    ps -l $PID | grep tomcat > /dev/null
    if [ $? -eq 0 ]; then
      return $PID
    fi
  done
  return 0
}

start() {
  echo -n "Starting OpenOffice ... "
  export DISPLAY=:5
  /usr/bin/Xvfb ${DISPLAY} -screen 0 800x600x16 &
  su - $ALFRESCO_USER -c "/usr/bin/soffice -nologo -invisible -display ${DISPLAY} -accept=\"socket,host=localhost,port=8100;urp;StarOffice.ServiceManager\"" > /dev/null &
  echo "Done"

  echo -n "Starting Alfresco ... "
  su - $ALFRESCO_USER -c "$APPSERVER"/bin/startup.sh > /dev/null 2>&1
  echo "Done"
}

stop() {
  echo "Stopping Alfresco ... "
  "$APPSERVER"/bin/shutdown.sh > /dev/null 2>&1
  sleep 5
  getTomcatPid 
  TOMCAT_PID=$?
  if [ "$TOMCAT_PID" != "0" ]; then
    echo "Killing Tomcat process $TOMCAT_PID"
    kill -9 $TOMCAT_PID
  fi

  # OpenOffice
  echo "Stopping OpenOffice ... "
  killall soffice > /dev/null 2>&1
  killall Xvfb > /dev/null 2>&1
}

#
# Script Start
#
if [ "$1" = "start" ]; then
  start
elif [ "$1" = "stop" ]; then
  stop
elif [ "$1" = "restart" ]; then
  stop
  sleep 5
  start
fi

