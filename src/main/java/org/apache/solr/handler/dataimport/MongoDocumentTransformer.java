package org.apache.solr.handler.dataimport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Translate MongoDB's Document to field value.
 *
 * Put the Data Transform class name on the file data-config.xml,
 * following the example below:
 *
 * <dataConfig>
 *  ...
 *  <dataSource ... />
 *  <document ...>
 *      <entity ...transformer="DocumentTransformer" >
 *          ...
 *      </entity>
 * </dataConfig>
 * 
 * @author Erlindo Silva
 */
public class MongoDocumentTransformer extends Transformer {

	private static final Logger logger = LoggerFactory.getLogger(MongoDBDataSource.class);
	
	private static final String HINT_DOCUMENT = "documentObject";
	
	private static final String HINT_ARRAY = "arrayObject";
	
	private static final String HINT_FROM_STRING_ENUM_TO_INTEGER_VALUE = "fromStringEnumToIntegerValueObject";
	private static final String HINT_FROM_STRING_ENUM_TO_INTEGER_VALUE_DATA = "fromStringEnumToIntegerValueObjectData";
	
	private static final String HINT_FIX_VALUE = "fixValueObject";
	private static final String HINT_FIX_VALUE_DATA = "fixValueObjectData";
	
	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		for (Map<String, String> fld : context.getAllEntityFields()) {
			String hint = context.replaceTokens(fld.get(HINT_DOCUMENT));
			if (hint != null && Boolean.parseBoolean(hint)) {
				String column = fld.get(DataImporter.COLUMN);
				documentTransformation(column, row);
			}
			
			hint = context.replaceTokens(fld.get(HINT_ARRAY));
			if (hint != null && Boolean.parseBoolean(hint)) {
				String column = fld.get(DataImporter.COLUMN);
				arrayTransformation(column, row);
			}
			
			hint = context.replaceTokens(fld.get(HINT_FROM_STRING_ENUM_TO_INTEGER_VALUE));
			if (hint != null && Boolean.parseBoolean(hint)) {
				String column = fld.get(DataImporter.COLUMN);
				String hintData = context.replaceTokens(fld.get(HINT_FROM_STRING_ENUM_TO_INTEGER_VALUE_DATA));
				fromStringToNumberTransformation(column, row, hintData);
			}
			
			//simplesmente pega o valor inserido no campo de dados e adiciona na coluna
			hint = context.replaceTokens(fld.get(HINT_FIX_VALUE));
			if (hint != null && Boolean.parseBoolean(hint)) {
				String column = fld.get(DataImporter.COLUMN);
				String hintData = context.replaceTokens(fld.get(HINT_FIX_VALUE_DATA));
				
				if(hintData.indexOf("{") > -1) //indica que é pra fazer um replace pelo valor de um campo da base
				{
					//extrai o valor do campo
					Pattern databaseFieldNamePattern = Pattern.compile("\\{(.+)\\}");
					Matcher databaseFiledNameMatcher = databaseFieldNamePattern.matcher(hintData);
					if(databaseFiledNameMatcher.find())
					{
						row.put(column, hintData.replace(databaseFiledNameMatcher.group(), row.get(databaseFiledNameMatcher.group(1)).toString()));
					}
				}
				else
				{
					row.put(column, hintData);
				}
			}
		}
		return row;
	}
	
	private void documentTransformation(String column, Map<String, Object> row)
	{
		try
		{
			Document iteractionDocument = null;
			Object valueTransformed = null;
			
			String[] identificadoresHierarquizados = column.split("\\.");
			for(int indiceNaHierarquiaDoIdentificador = 0; 
					indiceNaHierarquiaDoIdentificador < identificadoresHierarquizados.length; indiceNaHierarquiaDoIdentificador++)
			{
				if((indiceNaHierarquiaDoIdentificador + 1) == identificadoresHierarquizados.length) //se chegou na última posição, significa que está no documento correto no qual possui o campo que se quer encontrar.
				{
					valueTransformed = iteractionDocument.get(identificadoresHierarquizados[indiceNaHierarquiaDoIdentificador]);
				}
				else
				{					
					if(iteractionDocument == null)
						iteractionDocument = (Document)row.get(identificadoresHierarquizados[indiceNaHierarquiaDoIdentificador]);
					else
						iteractionDocument = (Document)iteractionDocument.get(identificadoresHierarquizados[indiceNaHierarquiaDoIdentificador]);
				}
			}
			
			if(valueTransformed != null)
			{
				row.put(column, valueTransformed);
			}
			else
			{
				logger.warn("There was no error but It wasn't possible to transform the column '{}'.", column);
			}
		}
		catch(Exception e)
		{
			logger.error("Occurred the following error when it was trying to transform the column '{}': {}", column, e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unlikely-arg-type")
	private void arrayTransformation(String column, Map<String, Object> row)
	{
		try
		{
			Document iteractionDocument = null;
			Object valueTransformed = null;
			
			String[] identificadoresHierarquizados = column.split("\\.");
			
			int positionArrayToSeek = Integer.parseInt(identificadoresHierarquizados[1]);
			
			@SuppressWarnings("unchecked")
			List<Document> allDocuments = (ArrayList<Document>)row.get(identificadoresHierarquizados[0]);
			
			if(!Objects.isNull(allDocuments) && !allDocuments.isEmpty())
			{
				iteractionDocument = (Document)allDocuments.get(positionArrayToSeek);
				valueTransformed = iteractionDocument.get(identificadoresHierarquizados[2]);
				row.put(column, valueTransformed);
			}
		}
		catch(Exception e)
		{
			logger.error("Occurred the following error when it was trying to transform the column '{}': {}", column, e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unlikely-arg-type")
	private void fromStringToNumberTransformation(String column, Map<String, Object> row, String hintData)
	{
		try
		{
			String iteractionDocument = (String)row.get(column);
			
			Map<String, Integer> dataGroupMap = mountMap(hintData);
			
			row.put(column, dataGroupMap.get(iteractionDocument));
		}
		catch(Exception e)
		{
			logger.error("Occurred the following error when it was trying to transform the column '{}': {}", column, e.getMessage(), e);
		}
	}
	
	private Map<String, Integer> mountMap(String hintData)
	{
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		String[] dataGroups = hintData.split(",");
		
		for(String dataGroup : dataGroups)
		{
			String[] internalDatas = dataGroup.split("=");
			result.put(internalDatas[0], Integer.parseInt(internalDatas[1]));
		}
		
		return result;
	}

}
