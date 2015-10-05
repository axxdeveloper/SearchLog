# SearchLog

## Build a jar file
<pre>
mvn clean package
</pre>
It will build a zip file in target folder, unzip that file and execute run.sh or run.bat


## Prepare log files
Put log files to the folder you want to execute jar file<br>
Ex.
<pre>
D:/searchlog-0.0.1-SNAPSHOT-jar-with-dependencies/logs/#{many log files}
D:/searchlog-0.0.1-SNAPSHOT-jar-with-dependencies/run.bat
</pre> 

## Execute run.sh or run.bat
## Use URL to search log file
<pre>
http://127.0.0.1:8080/search?match=Exception
</pre>