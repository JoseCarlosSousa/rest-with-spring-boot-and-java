package pt.seixal.carlos.file.exporter.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.data.dto.v1.UserDTO;
import pt.seixal.carlos.file.exporter.contract.FileExporter;

@Component
public class CsvExporter implements FileExporter {

	@Override
	public Resource exportPeople(List<PersonDTO> people) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

		CSVFormat csvFormat = CSVFormat.Builder.create()
				.setHeader("ID", "First Name", "Last Name", "Address", "Gender", "Enabled")
				.setSkipHeaderRecord(false)
				.build();

		try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
			for (PersonDTO person : people) {
				csvPrinter.printRecord(
						person.getId(),
						person.getFirstName(),
						person.getLastName(),
						person.getAddress(),
						person.getGender(),
						person.getEnabled());
			}
		}
		return new ByteArrayResource(outputStream.toByteArray());
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
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

		CSVFormat csvFormat = CSVFormat.Builder.create()
				.setHeader("ID", "Author", "Title", "Price", "Launch Date")
				.setSkipHeaderRecord(false)
				.build();

		try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
			for (BookDTO book : books) {
				csvPrinter.printRecord(
						book.getId(),
						book.getAuthor(),
						book.getTitle(),
						book.getPrice(),
						book.getLaunchDate());
			}
		}
		return new ByteArrayResource(outputStream.toByteArray());
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
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

		CSVFormat csvFormat = CSVFormat.Builder.create()
				.setHeader("ID",
						"Username",
						"Full Name",
						"Account Non Expired",
						"Account Non Locked",
						"Credentials Non Expired",
						"Enabled")
				.setSkipHeaderRecord(false)
				.build();

		try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
			for (UserDTO user : users) {
				csvPrinter.printRecord(
						user.getId(),
						user.getUserName(),
						user.getFullName(),
						user.isAccountNonExpired(),
						user.isAccountNonLocked(),
						user.isCredentialsNonExpired(),
						user.isEnabled());
			}
		}
		return new ByteArrayResource(outputStream.toByteArray());
	}

	@Override
	public Resource exportUser(UserDTO user) throws Exception {
		if (user == null) {
			return new ByteArrayResource(new byte[0]);
		}
		return exportUsers(List.of(user));
	}

}