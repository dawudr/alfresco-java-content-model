@echo off
c:
cd "C:\Program Files\OpenOffice.org 2.1\program\"
soffice "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless
pause