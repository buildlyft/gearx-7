package com.gearx7.app.service.interfaces;

import com.gearx7.app.domain.Type;
import java.util.List;

public interface TypeService {
    Type createType(Type type);

    Type updateType(Long id, Type type);

    List<Type> getAllTypes();

    Type getTypeById(Long id);

    void deleteType(Long id);
}
