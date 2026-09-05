package pt.seixal.carlos.dto.wrappers.json;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import pt.seixal.carlos.dto.PersonDTO;


public class PersonEmbeddedDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("people")
	private List<PersonDTO> people;

	public PersonEmbeddedDTO() {
	}

	public List<PersonDTO> getPeople() {
		return people;
	}

	public void setPeople(List<PersonDTO> people) {
		this.people = people;
	}
	

}
