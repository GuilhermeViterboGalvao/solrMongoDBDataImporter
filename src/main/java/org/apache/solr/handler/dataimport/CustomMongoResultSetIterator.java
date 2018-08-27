package org.apache.solr.handler.dataimport;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.apache.solr.handler.dataimport.DataImportHandlerException.SEVERE;
import static org.apache.solr.handler.dataimport.DataImportHandlerException.wrapAndThrow;

/**
 * Custom "Iterator" for MongoCursor.
 *
 * @author Guilherme Viterbo Galvão
 */
public final class CustomMongoResultSetIterator {

    private Iterator<Map<String, Object>> resultSet;

    private MongoCursor mongoCursor;

    public CustomMongoResultSetIterator(MongoCursor mongoCursor) {

        this.mongoCursor = mongoCursor;

        resultSet = new Iterator<Map<String, Object>>() {
            public boolean hasNext() { return hasnext(); }

            public Map<String, Object> next() { return getNext(); }

            public void remove() { }
        };
    }

    public Iterator<Map<String, Object>> getIterator() {
        return resultSet;
    }

    private Map<String, Object> getNext() {
        Document mongoObject = (Document)mongoCursor.next();

        Set<String> keys = mongoObject.keySet();
        Map<String, Object> result = new HashMap<String, Object>(keys.size());

        for (String key : keys) {
            Object value = mongoObject.get(key);
            result.put(key, value);
        }

        return result;
    }

    private boolean hasnext() {
        if (mongoCursor == null) {
            return false;
        }
        try {
            if (mongoCursor.hasNext()) {
                return true;
            } else {
                close();
                return false;
            }
        } catch (MongoException e) {
            close();
            wrapAndThrow(SEVERE, e);
            return false;
        }
    }

    private void close() {
        try {
            if (mongoCursor != null) {
                mongoCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoCursor = null;
        }
    }
}
