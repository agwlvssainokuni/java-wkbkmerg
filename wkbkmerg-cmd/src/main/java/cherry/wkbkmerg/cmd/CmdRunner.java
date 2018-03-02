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

package cherry.wkbkmerg.cmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

import cherry.wkbkmerg.processor.IOConsumer;
import cherry.wkbkmerg.processor.WkbkConsumerFactory;

@Component
public class CmdRunner implements ApplicationRunner, ExitCodeGenerator {

	private static final String ARG_CREATE = "create";

	private static final String ARG_TARGET = "target";

	@Autowired
	private WkbkConsumerFactory wkbkConsumerFactory;

	private Integer exitCode = null;

	@Override
	public void run(ApplicationArguments args) throws IOException, InvalidFormatException {

		boolean create = args.containsOption(ARG_CREATE);

		File target = new File("target.xlsx");
		if (args.containsOption(ARG_TARGET)) {
			String fname = args.getOptionValues(ARG_TARGET).get(0);
			if (StringUtils.isNotBlank(fname)) {
				target = new File(fname);
			}
		}

		try {
			if (create || !target.exists()) {
				try (Workbook dbook = target.getName().endsWith(".xls") ? new HSSFWorkbook() : new XSSFWorkbook()) {
					merge(args, dbook);
					write(dbook, target);
				}
			} else {
				try (InputStream in = new FileInputStream(target); Workbook dbook = WorkbookFactory.create(in)) {
					merge(args, dbook);
					write(dbook, target);
				}
			}
		} catch (IOException | InvalidFormatException | UncheckedIOException | IllegalArgumentException ex) {
			setExitCode(-1);
			throw ex;
		}
		setExitCode(0);
	}

	private void merge(ApplicationArguments args, Workbook dbook) {
		args.getNonOptionArgs().stream().map(File::new).forEach((IOConsumer<File>) file -> {
			try (InputStream in = new FileInputStream(file)) {
				wkbkConsumerFactory.create(file.getName(), in).accept(dbook);
			}
		});
	}

	private void write(Workbook dbook, File target) throws IOException {
		try (OutputStream out = new FileOutputStream(target)) {
			dbook.write(out);
		}
	}

	@Override
	public int getExitCode() {
		while (true) {
			if (exitCode != null) {
				return exitCode.intValue();
			}
			try {
				wait();
			} catch (InterruptedException ex) {
				// NOTHING TO DO
			}
		}
	}

	private synchronized void setExitCode(int code) {
		exitCode = code;
		notifyAll();
	}

}
