package pt.seixal.carlos.file.exporter.contract;

import java.util.List;

import org.springframework.core.io.Resource;

import pt.seixal.carlos.data.dto.v1.PersonDTO;

public interface FileExporter {

	 Resource exportPeople(List<PersonDTO> people) throws Exception;
	 Resource exportPerson(PersonDTO person) throws Exception;
}
