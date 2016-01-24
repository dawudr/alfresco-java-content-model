CREATE DATABASE alfresco_test DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_bin;

grant all on alfresco.* to 'alfresco'@'localhost' identified by 'alfresco' with grant option;
grant all on alfresco.* to 'alfresco'@'localhost.localdomain' identified by 'alfresco' with grant option;
grant all on alfresco_test.* to 'alfresco'@'localhost' identified by 'alfresco' with grant option;
grant all on alfresco_test.* to 'alfresco'@'localhost.localdomain' identified by 'alfresco' with grant option;
