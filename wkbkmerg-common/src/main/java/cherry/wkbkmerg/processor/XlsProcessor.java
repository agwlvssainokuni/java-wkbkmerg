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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class XlsProcessor {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void merge(String fname, Workbook sbook, Workbook dbook) {
		logger.info("ファイル: {}", fname);
		sbook.forEach(ssheet -> {
			logger.debug("  シート: {}", ssheet.getSheetName());
			Sheet dsheet = dbook.createSheet(ssheet.getSheetName());
			ssheet.forEach(srow -> {
				Row drow = dsheet.createRow(srow.getRowNum());
				srow.forEach(scell -> {
					Cell dcell = drow.createCell(scell.getColumnIndex(), scell.getCellTypeEnum());
					if (scell.getCellTypeEnum() == CellType.NUMERIC) {
						dcell.setCellValue(scell.getNumericCellValue());
					} else if (scell.getCellTypeEnum() == CellType.STRING) {
						dcell.setCellValue(scell.getStringCellValue());
					} else if (scell.getCellTypeEnum() == CellType.FORMULA) {
						dcell.setCellFormula(scell.getCellFormula());
					} else if (scell.getCellTypeEnum() == CellType.BLANK) {
						// 何もしない
					} else if (scell.getCellTypeEnum() == CellType.BOOLEAN) {
						dcell.setCellValue(scell.getBooleanCellValue());
					} else if (scell.getCellTypeEnum() == CellType.ERROR) {
						dcell.setCellErrorValue(scell.getErrorCellValue());
					} else {
						// 何もしない
					}
				});
			});
		});
	}

}
