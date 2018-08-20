package org.apache.solr.handler.dataimport;

import java.util.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.solr.handler.dataimport.dto.DataSourceDTO;
import org.apache.solr.handler.dataimport.dto.EntityDTO;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.solr.handler.dataimport.DataImportHandlerException.SEVERE;

/**
 * MongoDB DataSource
 *
 * Put the Data Source class name on the file data-config.xml,
 * following the example below:
 *
 * <dataConfig>
 *   <dataSource ... type="MongoDBDataSource"/>
 *   ...
 * </dataConfig>
 *
 * @author Guilherme Viterbo Galvão
 */
public class MongoDBDataSource extends DataSource<Iterator<Map<String, Object>>> {

	private static final Logger logger = LoggerFactory.getLogger(MongoDBDataSource.class);

	private DataSourceDTO dataSourceDTO;

    private EntityDTO entityDTO;

	private MongoDatabase mongoDatabase;

	private MongoCollection collection;

	private MongoCursor cursor;

	private Context context;

	@Override
	public void init(Context context, Properties initProps) {
	    this.context = context;
	    dataSourceDTO = DataSourceDTO.getInstance(initProps);
	    entityDTO = EntityDTO.getInstance(context);
		try {
            mongoDatabase = dataSourceDTO.getMongoDatabase();
            collection = mongoDatabase.getCollection(entityDTO.getCollection());
			logger.info(
                String.format(
                    "mongodb [%s:%d]@%s inited",
                    dataSourceDTO.getHost(),
                    dataSourceDTO.getPort(),
                    dataSourceDTO.getDatabase()
                )
            );
		} catch (Exception e) {
		    e.printStackTrace();
			throw new DataImportHandlerException(SEVERE, "init mongodb failed");
		}
	}

	@Override
	public Iterator<Map<String, Object>> getData(String query) {
        if (Context.FIND_DELTA.equals(context.currentProcess())) {
            if (entityDTO.getFindDeltaQuery() != null && !entityDTO.getFindDeltaQuery().isEmpty()) {
                logger.info(
                        String.format(
                                "Executing delta find query: %s",
                                entityDTO.getFindDeltaQuery()
                        )
                );
                cursor = collection.find(
                        BsonDocument.parse(
                                entityDTO.getFindDeltaQuery()
                        )
                ).iterator();
            } else if (entityDTO.getAggregationDeltaQuery() != null && !entityDTO.getAggregationDeltaQuery().isEmpty()) {
                logger.info(
                        String.format(
                                "Executing delta aggregation query: %s",
                                entityDTO.getAggregationDeltaQuery()
                        )
                );
                cursor = collection.aggregate(
                        Arrays.asList(
                                BsonArray.parse(entityDTO.getAggregationDeltaQuery()).toArray()
                        )
                ).iterator();
            } else {
                logger.info("Executing delta findAll query: {}");
                cursor = collection.find().iterator();
            }
        } else {
            if (entityDTO.getFindQuery() != null && !entityDTO.getFindQuery().isEmpty()) {
                logger.info(
                        String.format(
                                "Executing find query: %s",
                                entityDTO.getFindQuery()
                        )
                );
                cursor = collection.find(
                        BsonDocument.parse(
                                entityDTO.getFindQuery()
                        )
                ).iterator();
            } else if (entityDTO.getAggregationQuery() != null && !entityDTO.getAggregationQuery().isEmpty()) {
                logger.info(
                        String.format(
                                "Executing aggregation query: %s",
                                entityDTO.getAggregationQuery()
                        )
                );
                cursor = collection.aggregate(
                        Arrays.asList(
                                BsonArray.parse(entityDTO.getAggregationQuery()).toArray()
                        )
                ).iterator();
            } else {
                logger.info("Executing findAll query: {}");
                cursor = collection.find().iterator();
            }
        }
        CustomMongoResultSetIterator resultSet = new CustomMongoResultSetIterator(cursor);
		return resultSet.getIterator();
	}

	public Iterator<Map<String, Object>> getData(EntityDTO entityDTO) {
	    this.entityDTO = entityDTO;
		return getData("");
	}

	@Override
	public void close() {
		if (cursor != null) {
			cursor.close();
		}
	}
}