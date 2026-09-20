package pt.seixal.carlos.file.exporter.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.data.dto.v1.UserDTO;
import pt.seixal.carlos.file.exporter.contract.FileExporter;

@Component
public class XlsxExporter implements FileExporter {

	@Override
	public Resource exportPeople(List<PersonDTO> people) throws Exception {

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("People");
			Row headerRow = sheet.createRow(0);
			String[] headers = { "ID", "First Name", "Last Name", "Address", "Gender", "Enabled" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(createHeaderCellStyle(workbook));
			}

			int rowIndex = 1;
			for (PersonDTO person : people) {
				Row row = sheet.createRow(rowIndex++);
				row.createCell(0).setCellValue(person.getId());
				row.createCell(1).setCellValue(person.getFirstName());
				row.createCell(2).setCellValue(person.getLastName());
				row.createCell(3).setCellValue(person.getAddress());
				row.createCell(4).setCellValue(person.getGender());
				row.createCell(5).setCellValue(person.getEnabled() != null && person.getEnabled() ? "Yes" : "No");
			}

			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);

			return new ByteArrayResource(outputStream.toByteArray());
		}
	}

	private CellStyle createHeaderCellStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);

		return style;
	}

	@Override
	public Resource exportPerson(PersonDTO person) throws Exception {
		if (person == null) {
			return new ByteArrayResource(new byte[0]);
		}
		return exportPeople(List.of(person));
	}

	@Override
	public Resource exportBooks(List<BookDTO> books) throws Exception {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Books");
			Row headerRow = sheet.createRow(0);
			String[] headers = { "ID", "Author", "Title", "Price", "Launch Date" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(createHeaderCellStyle(workbook));
			}

			int rowIndex = 1;
			for (BookDTO book : books) {
				Row row = sheet.createRow(rowIndex++);
				row.createCell(0).setCellValue(book.getId());
				row.createCell(1).setCellValue(book.getAuthor());
				row.createCell(2).setCellValue(book.getTitle());
				row.createCell(3).setCellValue(book.getPrice() != null ? book.getPrice().doubleValue() : 0.0);
				row.createCell(4).setCellValue(book.getLaunchDate() != null ? book.getLaunchDate().toString() : "");
			}

			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);

			return new ByteArrayResource(outputStream.toByteArray());
		}
	}

	@Override
	public Resource exportBook(BookDTO book) throws Exception {
		if (book == null) {
			return new ByteArrayResource(new byte[0]);
		}
		return exportBooks(List.of(book));
	}

	@Override
	public Resource exportUsers(List<UserDTO> users) throws Exception {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Users");
			Row headerRow = sheet.createRow(0);
			String[] headers = { "ID", "Username", "Full Name", "Enabled" };

			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(createHeaderCellStyle(workbook));
			}

			int rowIndex = 1;
			for (UserDTO user : users) {
				Row row = sheet.createRow(rowIndex++);
				row.createCell(0).setCellValue(user.getId());
				row.createCell(1).setCellValue(user.getUserName());
				row.createCell(2).setCellValue(user.getFullName());
				row.createCell(3).setCellValue(user.isEnabled() ? "Yes" : "No");
			}

			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayResource(outputStream.toByteArray());
		}
	}

	@Override
	public Resource exportUser(UserDTO user) throws Exception {
		if (user == null) {
			return new ByteArrayResource(new byte[0]);
		}
		return exportUsers(List.of(user));
	}

}
