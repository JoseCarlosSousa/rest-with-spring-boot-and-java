package pt.seixal.carlos.mapper;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import java.util.ArrayList;
import java.util.List;

public class ObjectMapper {

    private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    public static <O, D> D parseObject(O origen, Class<D> destination) {
        return mapper.map(origen, destination);
    }

    public static <O, D> List<D> parseListObjects(List<O> origen, Class<D> destination) {

        List<D> destinationList = new ArrayList<>();
        for(Object o : origen) {
            destinationList.add(mapper.map(o, destination));
        }

        return destinationList;
    }
}
