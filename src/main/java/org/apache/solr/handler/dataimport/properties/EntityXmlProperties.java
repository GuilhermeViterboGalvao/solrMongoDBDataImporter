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
 *         aggregationDeltaImportQuery="{ ... YOUR AGGREGATION DELTA QUERY HERE FOR UNIQUE RESULT ... }"
 *         findDeltaImportQuery="{ ... YOUR FIND DELTA QUERY HERE FOR UNIQUE RESULT ... }"
 *         datasource="MyMongoDataSource"
 *         transformer="ObjectIdToLongTransformer"
 *         name="myEntityOfMyCollection">
 *     </entity>
 *   </document>
 * </dataConfig>
 *
 * @author Guilherme Viterbo Galvão
 */
public enum EntityXmlProperties {

    MONGO_COLLECTION("collection"),
    MONGO_FIND_QUERY("findQuery"),
    MONGO_FIND_DELTA_QUERY("findDeltaQuery"),
    MONGO_AGGREGATION_QUERY("aggregationQuery"),
    MONGO_AGGREGATION_DELTA_QUERY("aggregationDeltaQuery"),
    MONGO_AGGREGATION_DELTA_IMPORT_QUERY("aggregationDeltaImportQuery"),
    MONGO_FIND_DELTA_IMPORT_QUERY("findDeltaImportQuery");

    EntityXmlProperties(String propertieName) {
        this.propertieName = propertieName;
    }

    private String propertieName;

    public String getPropertieName() {
        return propertieName;
    }

}
