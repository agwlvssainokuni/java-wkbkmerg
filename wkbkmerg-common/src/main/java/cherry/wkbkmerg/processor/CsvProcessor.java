/*
 * Copyright 2018 agwlvssainokuni
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cherry.wkbkmerg.processor;

import org.apache.commons.csv.CSVParser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CsvProcessor {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void merge(String fname, CSVParser scsv, Workbook dbook) {
		logger.info("ファイル: {}", fname);
		String sheetname = createSheetname(fname);
		logger.info("  シート: {}", sheetname);
		Sheet dsheet = dbook.createSheet(sheetname);
		scsv.forEach(srec -> {
			Row drow = dsheet.createRow((int) srec.getRecordNumber() - 1);
			int column = 0;
			for (String value : srec) {
				Cell dcell = drow.createCell(column, CellType.STRING);
				dcell.setCellValue(value);
				column += 1;
			}
		});
	}

	private String createSheetname(String fname) {
		int dotpos = fname.lastIndexOf(".");
		if (dotpos <= 0) {
			return fname;
		} else {
			return fname.substring(0, dotpos);
		}
	}

}
