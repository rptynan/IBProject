# IBProject

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
2. Create a user called **ibproject** and give it a password that you will
   remember.
3. Create a database called **ibprojectdb**, no need to create tables, the
   Database module will do that if it detects they don't exist.
4. Type the password in when you run the code (there'll be a prompt).
5. Profit.


## Using the Knowledge Graph API:

1. Include the following KG jars:
   lib/kgsearch/libs/google-api-client-1.21.0.jar
   lib/kgsearch/libs/google-http-client-1.21.0.jar
   lib/kgsearch/libs/google-http-client-gson-1.21.0.jar
   lib/kgsearch/libs/google-oauth-client-1.21.0.jar
   lib/kgsearch/libs/gson-2.1.jar
   lib/kgsearch/libs/jsr305-1.3.9.jar
   lib/kgsearch/google-api-services-kgsearch-v1-rev3-1.21.0.jar

2. Include the following JSon jars:
   lib/json/json-simple.jar
   lib/json/json-path-0.8.0.jar
   lib/json/commons-lang-2.6.jar

3. That should be it ;) 
