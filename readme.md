# Solr MongoDB Data Importer

### Before to configure driver.

* [1 - Download and install Solr.](doc/installSolr.md)
* [2 - Download and install MongoDB.](doc/installMongoDB.md)
* [3 - Download and intall Java.](doc/installJava.md)
* [4 - Download and install Maven.](doc/installMaven.md)
* [5 - Download and install git.](doc/installGit.md)

### Download the other libs

* [1 - org.mongodb.Bson - 3.6.3](https://mvnrepository.com/artifact/org.mongodb/bson/3.6.3)
* [2 - org.mongodb.mongo-java-driver - 3.6.3](https://mvnrepository.com/artifact/org.mongodb/mongo-java-driver/3.6.3)
* [3 - org.apache.solr.solr-dataimporthandler](https://mvnrepository.com/artifact/org.apache.solr/solr-dataimporthandler/4.7.0)

### Cloning this projet
```bash
cd ~
git clone ...
```

### Compiling the source code with maven
```bash
mvn clean install
```

### Copy libs to your Solr index libs folder
Example:
```bash
cd /opt/solr-4.7.2/example/lib
cp ~/.m2/repository/org/mongodb/mongo-java-driver/3.6.3/mongo-java-driver-3.6.3-sources.jar .
cp ~/.m2/repository/org/mongodb/bson/3.6.3/bson-3.6.3.jar .
cp ~/.m2/repository/org/apache/solr/solr-dataimporthandler/4.7.0/solr-dataimporthandler-4.7.0.jar .
cp ~/SolrMongoDBDataImporter/target/solrMongoDBDataImporter-1.0.jar .
```

### How to use ?

* 1 - Change the file **solrconfig.xml**, and add the following content after **config** tag:
```xml
<requestHandler name="/dataimport" class="org.apache.solr.handler.dataimport.DataImportHandler">
    <lst name="defaults">
        <str name="config">data-config.xml</str>
    </lst>
</requestHandler>
```
* 2 - Add the new libs on file **solrconfig.xml**. Add after **config** tag:
```xml
  <lib path="../../lib/bson-3.6.3.jar" />
  <lib path="../../lib/mongo-java-driver-3.6.3.jar" />
  <lib path="../../lib/solr-dataimporthandler-4.7.0.jar" />  
  <lib path="../../lib/solrMongoDBDataImporter-1.0.jar" />
```
* 3 - Configure your **schema.xml**, for example:
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<schema name="hists" version="1.1">
    <types>
      <fieldType name="integer" class="solr.IntField"        omitNorms="true"/>
      <fieldType name="long"    class="solr.LongField"       omitNorms="true"/>
      <fieldType name="float"   class="solr.FloatField"      omitNorms="true"/>
      <fieldType name="double"  class="solr.DoubleField"     omitNorms="true"/>
      <fieldType name="string"  class="solr.StrField"        sortMissingLast="true" omitNorms="true"/>
      <fieldType name="boolean" class="solr.BoolField"       sortMissingLast="true" omitNorms="true"/>
      <fieldType name="sint"    class="solr.TrieIntField"    sortMissingLast="true" omitNorms="true"/>
      <fieldType name="slong"   class="solr.TrieLongField"   sortMissingLast="true" omitNorms="true"/>
      <fieldType name="sfloat"  class="solr.TrieFloatField"  sortMissingLast="true" omitNorms="true"/>
      <fieldType name="sdouble" class="solr.TrieDoubleField" sortMissingLast="true" omitNorms="true"/>
      <fieldType name="date"    class="solr.DateField"       sortMissingLast="true" omitNorms="true"/>
      <fieldType name="uuid"    class="solr.UUIDField" indexed="true" />
    
      <fieldType name="text_pt" class="solr.TextField" positionIncrementGap="100">
        <analyzer type="index">
          <tokenizer class="solr.WhitespaceTokenizerFactory"/>       
          <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt"/>
          <filter class="solr.WordDelimiterFilterFactory" generateWordParts="1" generateNumberParts="1" catenateWords="1" catenateNumbers="1" catenateAll="0" splitOnCaseChange="1"/>
          <filter class="solr.LowerCaseFilterFactory"/>
          <filter class="solr.SnowballPorterFilterFactory" language="Portuguese" />
          <filter class="solr.RemoveDuplicatesTokenFilterFactory"/>
          <charFilter class="solr.MappingCharFilterFactory" mapping="mapping-ISOLatin1Accent.txt"/>
        </analyzer>
        <analyzer type="query">
          <tokenizer class="solr.WhitespaceTokenizerFactory"/>
          <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt"/>
          <filter class="solr.WordDelimiterFilterFactory" generateWordParts="1" generateNumberParts="1" catenateWords="0" catenateNumbers="0" catenateAll="0" splitOnCaseChange="1"/>
          <filter class="solr.LowerCaseFilterFactory"/>
          <filter class="solr.SnowballPorterFilterFactory" language="Portuguese" />        
          <filter class="solr.RemoveDuplicatesTokenFilterFactory"/>
          <charFilter class="solr.MappingCharFilterFactory" mapping="mapping-ISOLatin1Accent.txt"/>
        </analyzer>
      </fieldType>
      <fieldType name="text_formated" class="solr.TextField" positionIncrementGap="100">
        <analyzer>
          <tokenizer class="solr.WhitespaceTokenizerFactory"/>
          <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt"/>
          <filter class="solr.LowerCaseFilterFactory"/>
          <charFilter class="solr.MappingCharFilterFactory" mapping="mapping-ISOLatin1Accent.txt"/>
        </analyzer>
      </fieldType>
      <fieldType name="text_cust" class="solr.TextField" positionIncrementGap="100">
        <analyzer>
          <tokenizer class="solr.StandardTokenizerFactory"/>
          <filter class="solr.LowerCaseFilterFactory"/>
          <charFilter class="solr.MappingCharFilterFactory" mapping="mapping-ISOLatin1Accent.txt"/>
        </analyzer>
      </fieldType>
    </types>

  <fields>

    <!-- Your mongodb document fields goes here... -->
    <field name="_id"        type="string"  indexed="true" stored="true"  required="true" />
    <field name="totalTime"  type="long"    indexed="true" stored="true"  required="true" />
    <field name="token"      type="string"  indexed="true" stored="true"  required="true" />
    <field name="statusCode" type="integer" indexed="true" stored="true"  required="true" />
    <field name="requestId"  type="string"  indexed="true" stored="true"  required="true" />
    <field name="date"       type="date"    indexed="true" stored="true"  required="true" />

    <field name="month"      type="integer" indexed="true" stored="true"  required="true" />
    <field name="day"        type="integer" indexed="true" stored="true"  required="true" />    
    <field name="hour"       type="integer" indexed="true" stored="true"  required="true" />

    <field name="text"       type="text_pt" indexed="true" stored="false" multiValued="true"/>
    <field name="_version_"  type="long"    indexed="true" stored="true"  multiValued="false" />

    <dynamicField name="*_i"        type="sint"           indexed="true"  stored="true" /> 
    <dynamicField name="*_s"        type="string"         indexed="true"  stored="true" /> 
    <dynamicField name="*_l"        type="slong"          indexed="true"  stored="true" /> 
    <dynamicField name="*_b"        type="boolean"        indexed="true"  stored="true" /> 
    <dynamicField name="*_f"        type="sfloat"         indexed="true"  stored="true" /> 
    <dynamicField name="*_d"        type="sdouble"        indexed="true"  stored="true" /> 
    <dynamicField name="*_dt"       type="date"           indexed="true"  stored="true" /> 
    <dynamicField name="*_cust"     type="text_cust"      indexed="true"  stored="true" />
    <dynamicField name="*_pt"       type="text_pt"        indexed="true"  stored="true" multiValued="true" />
    <dynamicField name="*_formated" type="text_formated"  indexed="true"  stored="true" multiValued="true" />
  </fields>

  <uniqueKey>_id</uniqueKey>
  <defaultSearchField>_id</defaultSearchField>
  <solrQueryParser defaultOperator="OR"/>
</schema>
```
* 4 - Now create the file **data-config.xml** in the same path of file **solrconfig.xml**:
```xml
<?xml version="1.0"?>
<dataConfig>
  <dataSource 
    name="MyMongoDBDataSourceName" 
    type="MongoDBDataSource" 
    database="myDatabase"
    host="localhost"
    port="27017"
    username="admin"
    password="admin123"/>
    <document name="hists">
      <!-- 
        if findQuery="" then it imports everything 
        or to import specific data, use query="{ Name: 'Guilherme' }"
      -->
     <entity  
        processor="MongoDBEntityProcessor"        
        collection="myCollection"
        aggregationQuery="[ { '$project' :{ totalTime: 1, token: 1, statusCode: 1, requestId: 1, date: 1, month: { $month: '$date' }, day: { $dayOfMonth: '$date' }, hour: { $hour: '$date' } } } ]"
        datasource="MyMongoDBDataSourceName-service_development"
        transformer="ObjectIdToLongTransformer" 
        name="myEntityOfCollectionXXX">
        <!-- 
            OR
            findQuery="{ totalTime: { $gte: 0 } }" for FULL IMPORT
            OR
            aggregationQuery="..." for FULL IMPORT
            OR
            findDeltaQuery="..." for DELTA IMPORT
            OR
            aggregationDeltaQuery="..." for DELTA IMPORT
        -->
        <field column="_id"        name="_id"        mongoField="_id"        />
        <field column="totalTime"  name="totalTime"  mongoField="totalTime"  />
        <field column="token"      name="token"      mongoField="token"      />
        <field column="statusCode" name="statusCode" mongoField="statusCode" />
        <field column="requestId"  name="requestId"  mongoField="requestId"  />
        <field column="date"       name="date"       mongoField="date"       />
        <field column="day"        name="day"        mongoField="day"        />
        <field column="month"      name="month"      mongoField="month"      />
        <field column="hour"       name="hour"       mongoField="hour"       />
    </entity>
  </document>
</dataConfig>

```
* 5 - Start your Solr and enjoy =).

### Driver options

#### DataSource tag properties
* **database**: Is the name of your MongoDB database.
* **host**: Is the your MongoDB host you want to connect (by default is localhost).
* **port**: The MongoDB port (by default is 27017). 
* **username**: Username you want to use for connect on MongoDB.
* **password**: The password of username you inform.

#### Entity tag properties
* **collection**: The name of collection you wanth extract the data.
* **findQuery**: MongoDB find query you want to execute on full import.
* **findDeltaQuery**: MongoDB find query you want to execute on delta import.
* **aggregationQuery**: MongoDB aggregation query of you want to execute on full import.
* **aggregationDeltaQuery**: MongoDB aggregation query of you want to execute on delta import.