CREATE DATABASE alfresco_dev DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_bin;
grant all on alfresco.* to 'alfresco'@'localhost' identified by 'alfresco' with grant option;
grant all on alfresco.* to 'alfresco'@'localhost.localdomain' identified by 'alfresco' with grant option;
grant all on alfresco_dev.* to 'alfresco'@'localhost' identified by 'alfresco' with grant option;
grant all on alfresco_dev.* to 'alfresco'@'localhost.localdomain' identified by 'alfresco' with grant option;
