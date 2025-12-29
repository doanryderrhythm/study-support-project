**MANDATORY TOOLS**
- [Android Studio](https://developer.android.com/studio?hl=vi)
- [Microsoft's SQL Server Management Studio](https://learn.microsoft.com/vi-vn/ssms/install/install)
- [Microsoft's SQL Server Configuration Manager](https://learn.microsoft.com/en-us/sql/tools/configuration-manager/sql-server-configuration-manager?view=sql-server-ver17)

**INSTALL INSTRUCTIONS**
1. INSTALL APPLICATIONS
- Download and install Microsoft's SQL Server Management Studio and SQL Server Configuration Manager.
2. CONFIGURE SQL DATA
- Open SQL Server Management Studio. In order to access to SQL database, you need a specific account.
- To create an account for testing, follow these steps:
  + In "Authentication", choose "Windows Authentication".
  + Tick "Trust Server Certificate".
  + Press "Connect".
  + Expand the Security folder in the Object Explorer window, then expand the Login folder, and double click on `sa`.
  + In "General", make sure the username is `sa` and the password is `password`, and choose SQL Authentication.
  + In "Status", grant the account to connect to database engine, and enable login.
  + Click OK.
- Now, disconnect the server and reconnect the server. It is time to connect to the database using the account that we created earlier:
  + In "Authentication", choose "SQL Server Authentication".
  + Input username `sa` and password `password`.
  + Tick "Trust Server Certificate".
  + Press "Connect".
- Time to create the database.
  + Import the `Mobile_app.sql` query to the application.
  + Create a new database called Mobile_app by executing `CREATE Mobile_app`.
  + After creating the database, execute the whole imported `Mobile_app.sql` query to create all necessary tables.
- **(OPTIONAL)** Let's add the sample data to the database:
  + Import the `Mobile_app_sample_data.sql` to the application.
  + Execute the whole imported `Mobile_app_sample_data.sql` query to add the sample data.
3. MODIFY SERVICES
- Open SQL Server Configuration Manager.
- First and foremost, let's change the IP port.
  + Expand "SQL Server Network Configuration", and choose "Protocol for `[YOUR SERVER NAME]`".
  + If you want to know what `[YOUR SERVER NAME]` is, check the server name in Connect window in SQL Server Management Studio.
  + Double click on "TCP/IP".
  + In "Protocol" tab, make sure to choose "Yes" in "Enabled" from "General" section.
  + In "IP Addresses" tab, check the "IPAll" section and type `1433` in "TCP Port".
  + Press Apply, then press OK.
- Finally, time to restart the service.
  + Click "SQL Server Services".
  + Right click on "SQL Server (`[YOUR SERVER NAME]`)".
  + Press "Restart".
4. MODIFY DEPENDENCIES
- Locate the file `app/libs/jtds-1.3.1.jar`.
- In Android Studio, select "File", then click "Project Structure".
- Go to "Dependencies" section, then select "All Modules" in the Modules tab.
- Under "All Dependencies", click on the `+` sign, and select "JAR/AAR Dependency".
- Copy the file location of the .jar file located earlier. **IMPORTANT: The extension must be available at the end of the path!**
- Click "OK".
- Finish the setup by clicking "Apply", and "OK".
5. RUN THE PROGRAM
- Run the mobile program by use Android Studio's emulator, and press "Run". **ATTENTION: ONLY ANDROID STUDIO'S EMULATOR IS SUITABLE FOR THIS PROJECT.**
