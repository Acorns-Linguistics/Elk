cd src\helpData
rmdir /s/q JavaHelpSearch
java -cp ..\..\jhall.jar com.sun.java.help.search.Indexer helpFiles\*.htm*
cd ..\..
