package com.gearx7.app.service.interfaces;

import com.gearx7.app.domain.Category;
import com.gearx7.app.domain.Type;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface TypeService {
    Type createType(Type type, MultipartFile file);

    Type updateType(Long id, Type type, MultipartFile file);

    List<Type> getAllTypes();

    Type getTypeById(Long id);

    void deleteType(Long id);

    List<Category> getCategoriesByTypeId(Long typeId);
}
