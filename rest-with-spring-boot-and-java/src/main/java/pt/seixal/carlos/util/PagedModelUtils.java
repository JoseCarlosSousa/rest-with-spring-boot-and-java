package pt.seixal.carlos.util;

import java.util.Map;
import java.util.function.BiConsumer;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import pt.seixal.carlos.mapper.DozertMapper;

public class PagedModelUtils {

	private PagedModelUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * @param <T> Type of entity (example: Book, Person)
	 * @param <D> Type of DTO (example: BookDTO, PersonDTO)
	 */
	public static <T, D> PagedModel<EntityModel<D>> buildPageModel(
			Map<String, String> params,
			Page<T> pageData,
			Class<D> dtoClass,
			PagedResourcesAssembler<D> assembler,
			Link findAllLink,
			BiConsumer<D, Map<String, String>> addHateoasLinks) {

		Page<D> dtoPage = pageData.map(entity -> {
			D dto = DozertMapper.parseObject(entity, dtoClass);
			addHateoasLinks.accept(dto, params);
			return dto;
		});

		return assembler.toModel(dtoPage, findAllLink);
	}
}
