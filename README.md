TO GENERATE THE HASH YOULL NEED TO EDIT YOU BUILD.XML TO RUN THE GenerateHash file THEN YOU RESET IT BACK TO RUN YOUR MAIN
YOULL HAVE TO CHANGE <property name="main.class" value="com.santediagnostics.Main"/> IN BUILD.XML TO <property name="main.class" value="com.santediagnostics.GenerateHash"/>
THEN SET IT BACK TO <property name="main.class" value="com.santediagnostics.Main"/> ONCE YOU HAVE GOTTEN YOUR HASH
THEN OPEN PGADMIN AND DO THIS QUERY, PROVIDED YOU HAVE CREATED YOUR DATABASE UPDATE users 
SET password_hash = '$2a$10$CbAicu1aJVL2E/KO4lvjy.RfwZ/zmBbuXdDQOm1TPvExrSZE8vaaq'  REPLACE WITH THE HASH YOU GOT
WHERE email = 'admin@santediagnostics.com';
Then run the project and log in with:

Email: admin@santediagnostics.com
Password: admin123
