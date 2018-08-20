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
                    EntityXmlProperties.MONGO_FIND_QUERY.getPropertieName()
                )
            ),
            context.replaceTokens(
                context.getEntityAttribute(
                    EntityXmlProperties.MONGO_FIND_DELTA_QUERY.getPropertieName()
                )
            ),
            context.replaceTokens(
                context.getEntityAttribute(
                    EntityXmlProperties.MONGO_AGGREGATION_QUERY.getPropertieName()
                )
            ),
            context.replaceTokens(
                context.getEntityAttribute(
                    EntityXmlProperties.MONGO_AGGREGATION_DELTA_QUERY.getPropertieName()
                )
            )
        ) : null;
    }

    private EntityDTO (
        String collection,
        String findQuery,
        String findDeltaQuery,
        String aggregationQuery,
        String aggregationDeltaQuery
    ) {
        if (collection == null) {
            throw new DataImportHandlerException(SEVERE, "collection is null");
        }
        this.collection = collection != null ? collection.trim() : "";
        this.findQuery = findQuery != null ? findQuery.trim() : "";
        this.findDeltaQuery = findDeltaQuery != null ? findDeltaQuery.trim() : "";
        this.aggregationQuery = aggregationQuery != null ? aggregationQuery.trim() : "";
        this.aggregationDeltaQuery = aggregationDeltaQuery != null ? aggregationDeltaQuery.trim() : "";
    }

    private Context context;

    private String collection;

    private String findQuery;

    private String findDeltaQuery;

    private String aggregationQuery;

    private String aggregationDeltaQuery;

    public String getCollection() {
        return collection;
    }

    public String getFindQuery() {
        return findQuery;
    }

    public String getFindDeltaQuery() {
        return findDeltaQuery;
    }

    public String getAggregationQuery() {
        return aggregationQuery;
    }

    public String getAggregationDeltaQuery() {
        return aggregationDeltaQuery;
    }
}
