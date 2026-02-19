package com.gearx7.app.service.interfaces;

import com.gearx7.app.service.dto.MachineOperatorDetailsDTO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface MachineOperatorService {
    MachineOperatorDetailsDTO create(MachineOperatorDetailsDTO dto, MultipartFile file);

    MachineOperatorDetailsDTO reassign(Long machineId, MachineOperatorDetailsDTO dto, MultipartFile file);

    MachineOperatorDetailsDTO getByMachineId(Long machineId);

    List<MachineOperatorDetailsDTO> getAllActiveOperators();

    void delete(Long machineId);
}
