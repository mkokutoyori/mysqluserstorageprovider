# mysqluserstorageprovider
A simple project in which I developed a user storage provider to manage a user database stored in mysql using the keycloak administration console.
## Requirements
- you must have a keycloak server with a version 
higher than 11.0.2, preferably running on port 8180 . 
To start the server you must go to the **$keycloak_home$/bin** 
directory and type the command 
**"standalone.bat -Djboss.socket.binding.port-offset=100 -Dkeycloak.profile.feature.impersonation=disabled"** if you are on windows, 
if you are on linux the command is 
**"standalone.sh -Djboss.socket.binding.port-offset=100 -Dkeycloak.profile.feature.impersonation=disabled"**. 
The part of the command relating to impersonation 
allows you to disable this property.

- a mysql dbms with a database named **somedatabase** running 
on port **3306** and having a user named **root** whose password is **admin**;

## setup
once keycloak and mysql are started, you have to build the project and copy the generated 
jar in the **$keycloak_home$/standalone/deployment** directory or you can deploy it 
through the admin console which should run on port **10090**.

go to the keycloak admin console and in any realm in the 
**user federation** tab in addition to the ldap and kerberos 
provider a new provider should appear under the name "**mysql-provider**" 
select and click on save. Now if you go into your database normally you 
should see a new table named **userentity**. The **userentity.sql** 
file located at the root of the project allows you to insert users into this table, 
execute it and return to your realm to see the list of users, 
you should see that now you have several new users in your realm  from mysql.

