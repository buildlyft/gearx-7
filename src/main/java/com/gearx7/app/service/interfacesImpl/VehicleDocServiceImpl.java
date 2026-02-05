package com.gearx7.app.service.interfacesImpl;

import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.User;
import com.gearx7.app.domain.VehicleDocument;
import com.gearx7.app.repository.MachineRepository;
import com.gearx7.app.repository.UserRepository;
import com.gearx7.app.repository.VehicleDocumentRepository;
import com.gearx7.app.security.AuthoritiesConstants;
import com.gearx7.app.security.SecurityUtils;
import com.gearx7.app.service.dto.VehicleDocumentDTO;
import com.gearx7.app.service.dto.VehicleDocumentResponseDTO;
import com.gearx7.app.service.interfaces.VehicleDocService;
import com.gearx7.app.web.rest.errors.NotFoundAlertException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class VehicleDocServiceImpl implements VehicleDocService {

    private final Logger log = LoggerFactory.getLogger(VehicleDocServiceImpl.class);

    private final MachineRepository machineRepository;
    private final UserRepository userRepository;
    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final CloudinaryDocumentStorageServiceImpl fileStorageService;

    public VehicleDocServiceImpl(
        MachineRepository machineRepository,
        UserRepository userRepository,
        VehicleDocumentRepository vehicleDocumentRepository,
        CloudinaryDocumentStorageServiceImpl fileStorageService
    ) {
        this.machineRepository = machineRepository;
        this.userRepository = userRepository;
        this.vehicleDocumentRepository = vehicleDocumentRepository;
        this.fileStorageService = fileStorageService;
    }

    // ================= UPLOAD DOCUMENTS =================

    @Override
    public VehicleDocumentResponseDTO uploadDocuments(Long machineId, Long uploadedBy, MultipartFile[] files) {
        log.info(
            "SERVICE START | Upload documents | machineId={} uploadedBy={} fileCount={}",
            machineId,
            uploadedBy,
            files != null ? files.length : 0
        );

        validatePermission(machineId);

        Machine machine = getMachine(machineId);
        User uploader = resolveUploader(uploadedBy);

        List<VehicleDocumentDTO> uploadedDocs = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                log.warn("SERVICE SKIP | Empty file detected | machineId={}", machineId);
                continue;
            }

            log.debug(
                "SERVICE PROCESS FILE | name={} size={} contentType={}",
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType()
            );

            String fileUrl = fileStorageService.uploadMachineDocument(file, machineId);

            VehicleDocument doc = new VehicleDocument();
            doc.setMachine(machine);
            doc.setUploadedBy(uploader);
            doc.setDocType(extractDocType(file));
            doc.setFileName(file.getOriginalFilename());
            doc.setFileKey(fileUrl);
            doc.setContentType(file.getContentType());
            doc.setSize(file.getSize());
            doc.setUploadedAt(Instant.now());

            VehicleDocument saved = vehicleDocumentRepository.save(doc);
            uploadedDocs.add(toDTO(saved, false));
        }
        VehicleDocumentResponseDTO response = new VehicleDocumentResponseDTO();
        response.setMachineId(machineId);
        response.setDocuments(uploadedDocs);

        log.info("SERVICE SUCCESS | Bulk upload completed | machineId={} documentsSaved={}", machineId, uploadedDocs.size());
        return response;
    }

    // ================= GET MACHINE DOCUMENTS =================

    @Override
    @Transactional(readOnly = true)
    public VehicleDocumentResponseDTO getMachineDocuments(Long machineId) {
        log.info("SERVICE START | Fetch machine documents | machineId={}", machineId);

        machineRepository
            .findById(machineId)
            .orElseThrow(() -> {
                log.error("Machine not found | machineId={}", machineId);
                return new NotFoundAlertException("Machine not found with id " + machineId, "machine", "machineNotFound");
            });

        List<VehicleDocumentDTO> docs = vehicleDocumentRepository
            .findAllByMachineId(machineId)
            .stream()
            .map(doc -> toDTO(doc, false))
            .toList();

        log.info("SERVICE SUCCESS | Documents fetched | machineId={} count={}", machineId, docs.size());

        VehicleDocumentResponseDTO response = new VehicleDocumentResponseDTO();
        response.setMachineId(machineId);
        response.setDocuments(docs);

        return response;
    }

    // ================= GET ALL DOCUMENTS =================

    @Override
    @Transactional(readOnly = true)
    public List<VehicleDocumentResponseDTO> getAllDocuments() {
        log.info("SERVICE START | Fetch all documents");

        List<VehicleDocument> allDocs = vehicleDocumentRepository.findAll();

        Map<Long, List<VehicleDocumentDTO>> grouped = allDocs
            .stream()
            .collect(
                Collectors.groupingBy(doc -> doc.getMachine().getId(), Collectors.mapping(doc -> toDTO(doc, false), Collectors.toList()))
            );

        List<VehicleDocumentResponseDTO> response = new ArrayList<>();

        for (Map.Entry<Long, List<VehicleDocumentDTO>> entry : grouped.entrySet()) {
            VehicleDocumentResponseDTO dto = new VehicleDocumentResponseDTO();
            dto.setMachineId(entry.getKey());
            dto.setDocuments(entry.getValue());
            response.add(dto);
        }

        log.info("SERVICE SUCCESS | Grouped machine documents fetched | machines={}", response.size());

        return response;
    }

    // ================= HELPERS =================

    private void validatePermission(Long machineId) {
        if (!SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ADMIN, AuthoritiesConstants.PARTNER)) {
            log.warn(
                "SECURITY VIOLATION | Upload denied | machineId={} user={}",
                machineId,
                SecurityUtils.getCurrentUserLogin().orElse("UNKNOWN")
            );
            throw new AccessDeniedException("Only ADMIN and PARTNER can upload vehicle documents");
        }
    }

    private Machine getMachine(Long id) {
        return machineRepository
            .findById(id)
            .orElseThrow(() -> {
                log.error("DATA ERROR | Machine not found | machineId={}", id);
                return new NotFoundAlertException("Machine not found with id " + id, "machine", "machineNotFound");
            });
    }

    private User resolveUploader(Long uploadedBy) {
        if (uploadedBy != null) {
            return userRepository
                .findById(uploadedBy)
                .orElseThrow(() -> {
                    log.error("DATA ERROR | UploadedBy user not found | userId={}", uploadedBy);
                    return new NotFoundAlertException("User not found with id " + uploadedBy, "user", "userNotFound");
                });
        }

        return userRepository
            .findOneByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow())
            .orElseThrow(() -> {
                log.error("SECURITY ERROR | Current user not found");
                return new AccessDeniedException("Current user not found");
            });
    }

    private VehicleDocumentDTO toDTO(VehicleDocument doc, boolean includeMachineId) {
        VehicleDocumentDTO dto = new VehicleDocumentDTO();
        dto.setId(doc.getId());
        dto.setDocType(doc.getDocType());
        dto.setFileName(doc.getFileName());
        dto.setFileUrl(doc.getFileKey());
        dto.setContentType(doc.getContentType());
        dto.setSize(doc.getSize());
        dto.setUploadedAt(doc.getUploadedAt());
        dto.setUploadedBy(doc.getUploadedBy() != null ? doc.getUploadedBy().getLogin() : null);

        return dto;
    }

    private String extractDocType(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || name.isBlank()) {
            return "UNKNOWN";
        }
        return name.contains(".")
            ? name.substring(0, name.lastIndexOf('.')).toUpperCase().replaceAll("[^A-Z0-9_]", "_")
            : name.toUpperCase();
    }
}
