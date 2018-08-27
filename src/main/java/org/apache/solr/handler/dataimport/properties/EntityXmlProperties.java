package org.apache.solr.handler.dataimport.properties;

/**
 * <dataConfig>
 *   <dataSource ... />
 *     <document name="myCollection">
 *      <entity
 *         processor="MongoDBEntityProcessor"
 *         collection="myCollection"
 *         fullDumpQuery="Find Query { } OR Aggregation Query [ ]"
 *         findDeltaQuery="Find Query { } OR Aggregation Query [ ]"
 *         deltaDumpQuery="Find Query { } OR Aggregation Query [ ]"
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
    MONGO_FULL_DUMP_QUERY("fullDumpQuery"),
    MONGO_FIND_DELTA_QUERY("findDeltaQuery"),
    MONGO_DELTA_DUMP_QUERY("deltaDumpQuery");

    EntityXmlProperties(String propertieName) {
        this.propertieName = propertieName;
    }

    private String propertieName;

    public String getPropertieName() {
        return propertieName;
    }

}
