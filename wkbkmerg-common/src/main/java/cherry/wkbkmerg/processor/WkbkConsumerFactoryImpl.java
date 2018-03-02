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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WkbkConsumerFactoryImpl implements WkbkConsumerFactory {

	@Autowired
	private XlsProcessor xlsProcessor;

	@Autowired
	private CsvProcessor csvProcessor;

	@Value("${charset}")
	private Charset charset;

	@Override
	public IOConsumer<Workbook> create(String fname, InputStream in) {
		if (fname.endsWith(".xls")) {
			return createXls(fname, in);
		} else if (fname.endsWith(".xlsx")) {
			return createXls(fname, in);
		} else if (fname.endsWith(".csv")) {
			return createCsv(fname, in, CSVFormat.EXCEL);
		} else if (fname.endsWith(".tsv")) {
			return createCsv(fname, in, CSVFormat.TDF);
		} else {
			return createCsv(fname, in, CSVFormat.TDF);
		}
	}

	private IOConsumer<Workbook> createXls(String fname, InputStream in) {
		return dbook -> {
			try (Workbook sbook = WorkbookFactory.create(in)) {
				xlsProcessor.merge(fname, sbook, dbook);
			} catch (InvalidFormatException ex) {
				throw new IllegalArgumentException(ex);
			}
		};
	}

	private IOConsumer<Workbook> createCsv(String fname, InputStream in, CSVFormat csvformat) {
		return dbook -> {
			try (Reader r = new InputStreamReader(in, charset); CSVParser scsv = csvformat.parse(r)) {
				csvProcessor.merge(fname, scsv, dbook);
			}
		};
	}

}
