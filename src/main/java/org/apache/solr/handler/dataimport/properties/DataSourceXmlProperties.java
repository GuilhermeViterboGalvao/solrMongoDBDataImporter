package org.apache.solr.handler.dataimport.properties;

/**
 * <dataConfig>
 *   <dataSource
 *     name="MyMongoDataSource"
 *     type="MongoDBDataSource"
 *     database="my_mongo_data_base"
 *     host="localhost"
 *     port="27017"
 *     username="admin"
 *     password="admin123"/>
 *      <entity ...></entity>
 *   </document>
 * </dataConfig>
 */
public enum DataSourceXmlProperties {

    MONGO_HOST("host"),
    MONGO_PORT("port"),
    MONGO_USERNAME("username"),
    MONGO_PASSWORD("password"),
    MONGO_DATABASE("database"),
    DEFAULT_HOST("localhost"),
    DEFAULT_PORT("27017");

    DataSourceXmlProperties(String propertieName) {
        this.propertieName = propertieName;
    }

    private String propertieName;

    public String getPropertieName() {
        return propertieName;
    }
}
