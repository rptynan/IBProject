#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

java -jar $DIR/checkstyle-6.14.1-all.jar -c $DIR/google_checks.xml $*

