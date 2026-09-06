package pt.seixal.carlos.file.exporter.contract;

import java.util.List;

import org.springframework.core.io.Resource;

import pt.seixal.carlos.data.dto.v1.PersonDTO;

public interface FileExporter {

	 Resource exportFile(List<PersonDTO> people) throws Exception;
}
