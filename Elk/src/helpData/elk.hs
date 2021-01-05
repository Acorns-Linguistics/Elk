<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE helpset   
PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN"
         "http://java.sun.com/products/javahelp/helpset_1_0.dtd">

<helpset version="2.0">

<!-- Help Title -->

  <title>ELK - [E]xtended [L]inguistic [K]eyboards</title>

<!-- Map Section -->

  <maps>
     <homeID>overview</homeID>
     <mapref location="map.jhm" />
  </maps>

<!-- View Section -->

   <view mergetype="javax.help.SortMerge">
    <name>Index</name>
    <label>Index</label>
    <type>javax.help.IndexView</type>
    <data>Index.xml</data>
  </view>

  <view mergetype="javax.help.SortMerge">
    <name>Search</name>
    <label>Search</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">
      JavaHelpSearch
    </data>
  </view>

<!--  Presentation Section -->

  <presentation default="true" displayviewimages="false">
     <name>Main_Window</name>
     <size width="950" height="600" />
     <location x="0" y="0" />
     <title>ELK - [E]xtended [L]linguistic [K]eyboards</title>
     <image>elk</image>
     <toolbar>
        <helpaction image="back">javax.help.BackAction</helpaction>
        <helpaction>javax.help.SeparatorAction</helpaction>
        <helpaction image="forward">javax.help.ForwardAction</helpaction>
        <helpaction>javax.help.SeparatorAction</helpaction>
        <helpaction image="home">javax.help.HomeAction</helpaction>
        <helpaction>javax.help.SeparatorAction</helpaction>
        <helpaction image="print">javax.help.PrintAction</helpaction>
     </toolbar>
  </presentation>

  <impl>
     <helpsetregistry helpbrokerclass="javax.help.DefaultHelpBroker" />
     <viewerregistry viewertype="text/html" viewerclass="com.sun.java.help.impl.CustomKit" />
  </impl>
</helpset>
 
