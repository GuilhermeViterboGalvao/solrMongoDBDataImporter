package org.apache.solr.handler.dataimport.properties;

/**
 * <dataConfig>
 *   <dataSource ... />
 *     <document name="myCollection">
 *      <entity
 *         processor="MongoDBEntityProcessor"
 *         collection="myCollection"
 *         findQuery="{ ... YOUR FIND QUERY HERE ... }"
 *         findDeltaQuery="{ ... YOUR FIND DELTA QUERY HERE ... }"
 *         aggregationQuery="{ ... YOUR AGGREGATION QUERY HERE ... }"
 *         aggregationDeltaQuery="{ ... YOUR AGGREGATION DELTA QUERY HERE ... }"
 *         datasource="MyMongoDataSource"
 *         transformer="ObjectIdToLongTransformer"
 *         name="myEntityOfMyCollection">
 *     </entity>
 *   </document>
 * </dataConfig>
 */
public enum EntityXmlProperties {

    MONGO_COLLECTION("collection"),
    MONGO_FIND_QUERY("findQuery"),
    MONGO_FIND_DELTA_QUERY("findDeltaQuery"),
    MONGO_AGGREGATION_QUERY("aggregationQuery"),
    MONGO_AGGREGATION_DELTA_QUERY("aggregationDeltaQuery");

    EntityXmlProperties(String propertieName) {
        this.propertieName = propertieName;
    }

    private String propertieName;

    public String getPropertieName() {
        return propertieName;
    }

}
