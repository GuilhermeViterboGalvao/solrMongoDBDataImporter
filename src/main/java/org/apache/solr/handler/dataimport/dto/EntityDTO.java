package org.apache.solr.handler.dataimport.dto;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImportHandlerException;
import org.apache.solr.handler.dataimport.properties.EntityXmlProperties;

import static org.apache.solr.handler.dataimport.DataImportHandlerException.SEVERE;

/**
 * @author Guilherme Viterbo Galvão
 */
public final class EntityDTO {

    public static final EntityDTO getInstance(Context context) {
        return context != null ? new EntityDTO(
            context.getEntityAttribute(
                EntityXmlProperties.MONGO_COLLECTION.getPropertieName()
            ),
            context.replaceTokens(
                context.getEntityAttribute(
                    EntityXmlProperties.MONGO_FULL_DUMP_QUERY.getPropertieName()
                )
            ),
            context.replaceTokens(
                context.getEntityAttribute(
                    EntityXmlProperties.MONGO_FIND_DELTA_QUERY.getPropertieName()
                )
            ),
            context.replaceTokens(
                context.getEntityAttribute(
                    EntityXmlProperties.MONGO_DELTA_DUMP_QUERY.getPropertieName()
                )
            )
        ) : null;
    }

    private EntityDTO (
        String collection,
        String fullDumpQuery,
        String findDeltaQuery,
        String deltaDumpQuery
    ) {
        if (collection == null) {
            throw new DataImportHandlerException(SEVERE, "collection is null");
        }
        this.collection = collection != null ? collection.trim() : "";
        this.fullDumpQuery = fullDumpQuery != null ? fullDumpQuery.trim(): "";
        this.findDeltaQuery = findDeltaQuery!= null ? findDeltaQuery.trim() : "";
        this.deltaDumpQuery = deltaDumpQuery != null ? deltaDumpQuery.trim() : "";
    }

    private String collection;

    private String fullDumpQuery;

    private String findDeltaQuery;

    private String deltaDumpQuery;

    public String getCollection() {
        return collection;
    }

    public String getFullDumpQuery() {
        return fullDumpQuery;
    }

    public String getFindDeltaQuery() {
        return findDeltaQuery;
    }

    public String getDeltaDumpQuery() {
        return deltaDumpQuery;
    }
}
