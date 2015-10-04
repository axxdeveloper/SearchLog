# SearchLog

## Build a jar file
<pre>
mvn clean package
</pre>
It will build a jar "searchlog-0.0.1-SNAPSHOT-jar-with-dependencies.jar" file in target folder, copy that file.


## Prepare log files
Put log files to the folder you want to execute jar file<br>
Ex.
<pre>
D:/searchlog/logs/#{many log files}
D:/searchlog/searchlog-0.0.1-SNAPSHOT-jar-with-dependencies.jar
</pre> 

## Run searchlog
<pre>
java -jar target\searchlog-0.0.1-SNAPSHOT-jar-with-dependencies.jar
</pre>

## Use URL to search log file
<pre>
http://127.0.0.1:8080/search?match=Exception
</pre>