@echo off
net stop "Apache Tomcat"
call ant redeploy_tomcat_alf
net start "Apache Tomcat"
pause
