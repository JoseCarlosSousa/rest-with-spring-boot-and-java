package pt.seixal.carlos.file.exporter.contract;

import java.util.List;

import org.springframework.core.io.Resource;

import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.data.dto.v1.UserDTO;

public interface FileExporter {

	Resource exportPeople(List<PersonDTO> people) throws Exception;

	Resource exportPerson(PersonDTO person) throws Exception;

	Resource exportBooks(List<BookDTO> books) throws Exception;

	Resource exportBook(BookDTO book) throws Exception;

	Resource exportUsers(List<UserDTO> users) throws Exception;

	Resource exportUser(UserDTO user) throws Exception;

}
