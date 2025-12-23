**MANDATORY TOOLS**
- Android Studio
- Microsoft's SQL Server Management Studio
- Microsoft's SQL Server Configuration Manager

**INSTALL INSTRUCTIONS**
- Download and install Microsoft's SQL Server Management Studio and SQL Server Configuration Manager.
- Open SQL Server Management Studio. In order to access to SQL database, you need a specific account.
- To create an account for testing, follow these steps:
  + In "Authentication", choose "Windows Authentication".
  + Tick "Trust Server Certificate".
  + Press "Connect".
  + Expand the Security folder in the Object Explorer window, then expand the Login folder, and double click on "sa".
  + In "General", make sure the username is "sa" and the password is "password", and choose SQL Authentication.
  + In "Status", grant the account to connect to database engine, and enable login.
  + Click OK.
- Now, disconnect the server and reconnect the server. It is time to connect to the database using the account that we created earlier:
  + In "Authentication", choose "SQL Server Authentication".
  + Input username "sa" and password "password".
  + Tick "Trust Server Certificate".
  + Press "Connect".
- Time to create the database.
  + Import the Mobile_app.sql query to the application.
  + Create a new database called Mobile_app by executing "CREATE Mobile_app".
  + After creating the database, execute the whole imported Mobile_app.sql query to create all necessary tables.
- **(OPTIONAL)** Let's add the sample data to the database:
  + Import the Mobile_app_sample_data.sql to the application.
  + Execute the whole imported Mobile_app_sample_data.sql query to add the sample data.
- Run the mobile program by connecting your device to Android Studio using USB cable, and press "Run".
