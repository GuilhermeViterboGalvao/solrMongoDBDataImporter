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

	private MongoDatabase mongoDatabase;

	private MongoCollection collection;

    private EntityDTO entityDTO;

	private MongoCursor cursor;

	@Override
	public void init(Context context, Properties initProps) {
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
	    logger.info("Execution getData(String query)...");
	    return null;
	}

	public Iterator<Map<String, Object>> getDataFromFindQuery(String findQuery) {
        long start = System.currentTimeMillis();
        logger.info(String.format("Executing find query: %s", findQuery));
        cursor = collection.find(BsonDocument.parse(findQuery)).iterator();
        logger.info("Total time to get data from Mongo in millis: {}", ( System.currentTimeMillis() - start ) );
        CustomMongoResultSetIterator resultSet = new CustomMongoResultSetIterator(cursor);
        return resultSet.getIterator();
    }

    public Iterator<Map<String, Object>> getDataFromAggregationQuery(String aggregationQuery) {
        long start = System.currentTimeMillis();
        logger.info(String.format("Executing aggregation query: %s", aggregationQuery));
        cursor = collection.aggregate(Arrays.asList(BsonArray.parse(aggregationQuery).toArray())).iterator();
        logger.info("Total time to get data from Mongo in millis: {}", ( System.currentTimeMillis() - start ) );
        CustomMongoResultSetIterator resultSet = new CustomMongoResultSetIterator(cursor);
        return resultSet.getIterator();
    }

	@Override
	public void close() {
		if (cursor != null) {
			cursor.close();
		}
	}
}