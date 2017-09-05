package com.hadoop.test.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelParser {
	private static final Log LOG=LogFactory.getLog(ExcelParser.class);
	private StringBuilder currentString=null;
	private long bytesRead=0;
	
	public String parseExcelData(InputStream is){
		try {
			HSSFWorkbook workbook=new HSSFWorkbook(is);
			HSSFSheet sheet=workbook.getSheetAt(0);
			Iterator<Row> rowIterator=sheet.iterator();
			currentString=new StringBuilder();
			while(rowIterator.hasNext()){
				Row row=rowIterator.next();
				Iterator<Cell> cellIterator=row.cellIterator();
				while(cellIterator.hasNext()){
					Cell cell=cellIterator.next();
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						bytesRead++;
						currentString.append(cell.getBooleanCellValue()+"\t");
						break;
					case Cell.CELL_TYPE_NUMERIC:
						bytesRead++;
						currentString.append(cell.getNumericCellValue()+"\t");
						break;
					case Cell.CELL_TYPE_STRING:
						bytesRead++;
						currentString.append(cell.getStringCellValue()+"\t");
						break;
					}
				}
				currentString.append("\n");
			}
			is.close();
		} catch (IOException e) {
			LOG.error("IO Exception: File not found"+e);
		}
		return currentString.toString();
	}
}
