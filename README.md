# IBProject

## Style Checking
Run the bash script `./checkstyle/check.sh <files to check>`, or alternatively
import `checkstyle/google_checks.xml` into your own style-checker.

## Libraries
JTwitter is in the lib file. The link to its Javadoc is
http://www.winterwell.com/software/jtwitter/javadoc/ .

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

