package com.gearx7.app.service.interfaces;

import com.gearx7.app.service.dto.MachineOperatorDetailsDTO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface MachineOperatorService {
    MachineOperatorDetailsDTO create(MachineOperatorDetailsDTO dto, List<MultipartFile> files, List<String> docTypes);

    MachineOperatorDetailsDTO reassign(Long machineId, MachineOperatorDetailsDTO dto, List<MultipartFile> files, List<String> docTypes);

    MachineOperatorDetailsDTO getByMachineId(Long machineId);
}
