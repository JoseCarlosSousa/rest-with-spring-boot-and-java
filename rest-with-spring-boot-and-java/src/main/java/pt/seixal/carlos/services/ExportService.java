package pt.seixal.carlos.services;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pt.seixal.carlos.mapper.DozertMapper;
import pt.seixal.carlos.util.PageableUtils;

@Service
public class ExportService {

	@FunctionalInterface
	public interface CheckedExporter<D> {
		Resource export(D data) throws Exception;
	}

	public <T, D> Resource exportPage(
			Map<String, String> params,
			String acceptHeader,
			Function<Pageable, Page<T>> findAllMethod,
			Class<D> dtoClass,
			CheckedExporter<List<D>> exporterMethod) {

		Pageable pageable = PageableUtils.getPageable(params);
		List<D> dtos = findAllMethod.apply(pageable)
				.map(entity -> DozertMapper.parseObject(entity, dtoClass))
				.getContent();

		try {
			return exporterMethod.export(dtos);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error exporting file: " + e.getMessage(), e);
		}
	}

	public <D> Resource exportSingle(
			D dto,
			CheckedExporter<D> exporterMethod) {
		try {
			return exporterMethod.export(dto);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error exporting file: " + e.getMessage(), e);
		}
	}
}
