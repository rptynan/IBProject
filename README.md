# IBProject

## Setting up Tomcat

1. Download and install Tomcat 8.0 for your OS https://tomcat.apache.org/download-80.cgi .
2. Copy the twikfeed.war file from the Tomcat Web App file in the repository to the webapps folder in your server.
3. Run the server. A twikfeed folder should have been generated in your webapps folder conataining META-INF and WEB-INF. 
4. Copy the index.html, jQuery.js and project.js files from the twikfeed folder in the repository to your twikfeed folder.
5. Run the Core (on port 90),the Database and the Tomcat server and then visit http://localhost:8080/twikfeed/ .

## Style Checking
Run the bash script `./checkstyle/check.sh <files to check>`, or alternatively
import `checkstyle/google_checks.xml` into your own style-checker.

## Libraries
JTwitter is in the lib file. The link to its Javadoc is
http://www.winterwell.com/software/jtwitter/javadoc/ . Bliki is used for
parsing Wiki-text and can be found at
https://code.google.com/archive/p/gwtwiki/source .  

## Generating Docs
```javadoc -d docs -classpath lib/JTwitter/jtwitter.jar -sourcepath
uk/ac/cam/quebec/*/*.java```  
Will generate them in the docs/ directory, from all of the source java files.
Don't commit the generated docs to the repo, generate them from the sources
instead.

## Building
```javac -classpath lib/JTwitter/jtwitter.jar uk/ac/cam/quebec/*/*.java```

## .gitignore
Right now the .gitignore file tells git to ignore all .class files and anything
in the docs/ directory.

## Setting up MySQL database
1. Install some kind of MySQL database, make sure it is running on the default
   port, **3306**.
2. Create a database called **ibprojectdb**, no need to create tables, the
   Database module will do that if it detects they don't exist.  
   `CREATE DATABASE ibprojectdb;`
3. Create a user called **ibproject** and give it a password that you will
   remember.  
   `CREATE USER 'ibproject'@'localhost' IDENTIFIED BY 'password_goes_here';`
4. Grant your user privileges to access the database you made.  
   `GRANT ALL PRIVILEGES ON ibprojectdb.* TO 'ibproject'@'localhost';`  
   `FLUSH PRIVILEGES;`
5. Either call setCredentials() in the Database class to provide your username,
   password and database location in code, or don't do this and let it prompt
   you for them when it runs.
5. Profit.


## Using the Knowledge Graph API:

1. Include the following KG jars:
   <p>lib/kgsearch/libs/google-api-client-1.21.0.jar
   <p>lib/kgsearch/libs/google-http-client-1.21.0.jar
   <p>lib/kgsearch/libs/google-http-client-gson-1.21.0.jar
   <p>lib/kgsearch/libs/google-oauth-client-1.21.0.jar
   <p>lib/kgsearch/libs/gson-2.1.jar
   <p>lib/kgsearch/libs/jsr305-1.3.9.jar
   <p>lib/kgsearch/google-api-services-kgsearch-v1-rev3-1.21.0.jar

2. Include the following JSon jars:
   <p>lib/json/json-simple.jar
   <p>lib/json/json-path-0.8.0.jar
   <p>lib/json/commons-lang-2.6.jar

3. That should be it ;) 

##Running


For a headless startup:
Run GroupProjectCore with 1 argument - the path to a completed config.xml file

For a basic debugging console:
Run CoreConsole with 1 argument - the path to a completed config.xml file

A default config.xml file can be found in this directory

There are some depreciated entry points, they should not be used:

Run GroupProjectCore with 5 agruements - the 4 Twitter login keys and the username IBProjectQuebec.

Run CoreConsole with 5 agruements - the 4 Twitter login keys and the username IBProjectQuebec.

##Using the Core Console

The core console is started using the procedure outlined above. The console will
load and initialise the configuration file and all the other static classes.

The console will allow you to give a number of commands to the server, allowing
you to experiment with various features and run certain tests. Some of the 
useful commands are outlined below.

1. Start - this will start the Core
2. Status - this will tell you the status of the various services running
3. Exit - this will close down the Core
4. Add trend: "trend name", "trend location", "trend priority" - this will add a trend to the queue with the specified location and priority
5. Repopulate trends - this will add cause the core to request new trends from twitter for all its set locations