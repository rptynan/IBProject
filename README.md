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
