package pt.seixal.carlos.util;

import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class PageableUtils {

	private PageableUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static Pageable getPageable(Map<String, String> params) {
		int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) : 0;
		int size = params.containsKey("size") ? Integer.parseInt(params.get("size")) : 12;

		Sort sort = Sort.unsorted();

		if (params.containsKey("direction") && params.containsKey("sort")) {
			String sortColumn = params.get("sort");

			switch (params.get("direction").toLowerCase()) {
			case "desc":
				sort = Sort.by(Direction.DESC, sortColumn);
				break;
			case "asc":
				sort = Sort.by(Direction.ASC, sortColumn);
				break;
			}
		}

		return PageRequest.of(page, size, sort);
	}
}
