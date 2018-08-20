package org.apache.solr.handler.dataimport;

import static org.apache.solr.handler.dataimport.DataImportHandlerException.SEVERE;

import java.util.Map;

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

    private Context context;

	@Override
	public void init(Context context) {
        super.init(context);
        this.context = context;
	    entityDTO = EntityDTO.getInstance(context);
		mongoDBDataSource = (MongoDBDataSource)context.getDataSource();
	}

	@Override
	public Map<String, Object> nextRow() {
        createRowIterator();
		Map<String, Object> data = getNext();
		logger.debug("process: " + data);
		return data;
	}

    @Override
    public Map<String, Object> nextModifiedRowKey() {
        return nextRow();
    }

    @Override
    public Map<String, Object> nextDeletedRowKey() {
        return nextRow();
    }

    @Override
    public Map<String, Object> nextModifiedParentRowKey() {
        return nextRow();
    }

	private void createRowIterator() {
        if (rowIterator == null) {
            synchronized (this) {
                if (rowIterator == null) {
                    try {
                        DataImporter.QUERY_COUNT.get().incrementAndGet();
                        rowIterator = mongoDBDataSource.getData(entityDTO);
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
