package org.apache.solr.handler.dataimport;

import static org.apache.solr.handler.dataimport.DataImportHandlerException.SEVERE;
import static org.apache.solr.handler.dataimport.DataImportHandlerException.wrapAndThrow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.solr.common.SolrException;
import org.apache.solr.handler.dataimport.dto.EntityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entity Process for MongoDB.
 *
 * Put the Entity Processor class name on the file data-config.xml,
 * following the example below:
 *
 * <dataConfig>
 *  ...
 *  <dataSource ... />
 *  <document ...>
 *      <entity ...processor="MongoDBEntityProcessor" >
 *          ...
 *      </entity>
 * </dataConfig>
 *
 * @author Guilherme Viterbo Galvão
 */
public class MongoDBEntityProcessor extends EntityProcessorBase {

	private static final Logger logger = LoggerFactory.getLogger(MongoDBEntityProcessor.class);

	private MongoDBDataSource mongoDBDataSource;

    private EntityDTO entityDTO;

	@Override
	public void init(Context context) {
        super.init(context);
	    entityDTO = EntityDTO.getInstance(context);
		mongoDBDataSource = (MongoDBDataSource)context.getDataSource();
	}

	@Override
	public Map<String, Object> nextRow() {
        if (context.currentProcess().equals(Context.FULL_DUMP)) {
            initRowIterator(entityDTO.getFullDumpQuery());
        } else if (context.currentProcess().equals(Context.DELTA_DUMP)) {
            initRowIterator(entityDTO.getDeltaDumpQuery());
        }
        return next();
	}

    @Override
    public Map<String, Object> nextModifiedRowKey() {
        if (context.currentProcess().equals(Context.FIND_DELTA)) {
            initRowIterator(entityDTO.getFindDeltaQuery());
        }
        return next();
    }

    private Map<String, Object> next() {
	    logger.info("Current context process: {}", context.currentProcess());
        Map<String, Object> data = getNext();
        logger.info("Current data: {}", data);
        return data;
    }

    private void initRowIterator(String query) {
        this.query = query;
        if (rowIterator == null) {
            synchronized (this) {
                if (rowIterator == null) {
                    try {
                        DataImporter.QUERY_COUNT.get().incrementAndGet();
                        rowIterator = mongoDBDataSource.getData(query);
                    } catch (DataImportHandlerException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new DataImportHandlerException(SEVERE, e);
                    }
                }
            }
        }
    }
}
