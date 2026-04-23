package com.gearx7.app.service.interfaces;

import com.gearx7.app.service.dto.MachineOperatorDetailsDTO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface MachineOperatorService {
    MachineOperatorDetailsDTO create(MachineOperatorDetailsDTO dto, MultipartFile operatorImage, MultipartFile license);

    MachineOperatorDetailsDTO reassign(Long machineId, MachineOperatorDetailsDTO dto, MultipartFile operatorImage, MultipartFile license);

    MachineOperatorDetailsDTO partialUpdate(Long operatorId, MachineOperatorDetailsDTO dto, MultipartFile photo, MultipartFile license);

    MachineOperatorDetailsDTO getByMachineId(Long machineId);

    List<MachineOperatorDetailsDTO> getAllActiveOperators();

    void delete(Long operatorId);

    List<MachineOperatorDetailsDTO> getAllOperators();
}
