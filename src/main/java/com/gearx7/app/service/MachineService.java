package com.gearx7.app.service;

import com.gearx7.app.domain.Category;
import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.Subcategory;
import com.gearx7.app.repository.CategoryRepository;
import com.gearx7.app.repository.MachineRepository;
import com.gearx7.app.repository.SubcategoryRepository;
import com.gearx7.app.service.dto.MachineDTO;
import com.gearx7.app.service.mapper.MachineMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gearx7.app.domain.Machine}.
 */
@Service
@Transactional
public class MachineService {

    private final Logger log = LoggerFactory.getLogger(MachineService.class);

    private final MachineRepository machineRepository;

    private final MachineMapper machineMapper;

    private final CategoryRepository categoryRepository;

    private final SubcategoryRepository subcategoryRepository;

    public MachineService(
        MachineRepository machineRepository,
        MachineMapper machineMapper,
        CategoryRepository categoryRepository,
        SubcategoryRepository subcategoryRepository
    ) {
        this.machineRepository = machineRepository;
        this.machineMapper = machineMapper;
        this.categoryRepository = categoryRepository;
        this.subcategoryRepository = subcategoryRepository;
    }

    /**
     * Save a machine.
     *
     * @param machineDTO the entity to save.
     * @return the persisted entity.
     */
    public MachineDTO save(MachineDTO machineDTO) {
        log.debug("Request to save Machine : {}", machineDTO);
        Machine machine = machineMapper.toEntity(machineDTO);

        // Set category if provided
        if (machineDTO.getCategoryId() != null) {
            Category category = categoryRepository
                .findById(machineDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + machineDTO.getCategoryId()));
            machine.setCategory(category);
        }

        // Set subcategory if provided
        if (machineDTO.getSubcategoryId() != null) {
            Subcategory subcategory = subcategoryRepository
                .findById(machineDTO.getSubcategoryId())
                .orElseThrow(() -> new RuntimeException("Subcategory not found with id: " + machineDTO.getSubcategoryId()));
            machine.setSubcategory(subcategory);
        }

        machine = machineRepository.save(machine);
        return machineMapper.toDto(machine);
    }

    /**
     * Update a machine.
     *
     * @param machineDTO the entity to save.
     * @return the persisted entity.
     */
    public MachineDTO update(MachineDTO machineDTO) {
        log.debug("Request to update Machine : {}", machineDTO);
        Machine machine = machineMapper.toEntity(machineDTO);

        // Set category if provided
        if (machineDTO.getCategoryId() != null) {
            Category category = categoryRepository
                .findById(machineDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + machineDTO.getCategoryId()));
            machine.setCategory(category);
        } else {
            machine.setCategory(null);
        }

        // Set subcategory if provided
        if (machineDTO.getSubcategoryId() != null) {
            Subcategory subcategory = subcategoryRepository
                .findById(machineDTO.getSubcategoryId())
                .orElseThrow(() -> new RuntimeException("Subcategory not found with id: " + machineDTO.getSubcategoryId()));
            machine.setSubcategory(subcategory);
        } else {
            machine.setSubcategory(null);
        }

        machine = machineRepository.save(machine);
        return machineMapper.toDto(machine);
    }

    /**
     * Partially update a machine.
     *
     * @param machineDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MachineDTO> partialUpdate(MachineDTO machineDTO) {
        log.debug("Request to partially update Machine : {}", machineDTO);

        return machineRepository
            .findById(machineDTO.getId())
            .map(existingMachine -> {
                machineMapper.partialUpdate(existingMachine, machineDTO);

                // Update category if provided
                if (machineDTO.getCategoryId() != null) {
                    Category category = categoryRepository
                        .findById(machineDTO.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found with id: " + machineDTO.getCategoryId()));
                    existingMachine.setCategory(category);
                } else if (machineDTO.getCategoryId() == null && machineDTO.getId() != null) {
                    // Allow clearing category by setting to null explicitly
                    existingMachine.setCategory(null);
                }

                // Update subcategory if provided
                if (machineDTO.getSubcategoryId() != null) {
                    Subcategory subcategory = subcategoryRepository
                        .findById(machineDTO.getSubcategoryId())
                        .orElseThrow(() -> new RuntimeException("Subcategory not found with id: " + machineDTO.getSubcategoryId()));
                    existingMachine.setSubcategory(subcategory);
                } else if (machineDTO.getSubcategoryId() == null && machineDTO.getId() != null) {
                    // Allow clearing subcategory by setting to null explicitly
                    existingMachine.setSubcategory(null);
                }

                return existingMachine;
            })
            .map(machineRepository::save)
            .map(machineMapper::toDto);
    }

    /**
     * Get all the machines.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MachineDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Machines");
        return machineRepository.findAll(pageable).map(machineMapper::toDto);
    }

    /**
     * Get all the machines with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MachineDTO> findAllWithEagerRelationships(Pageable pageable) {
        return machineRepository.findAllWithEagerRelationships(pageable).map(machineMapper::toDto);
    }

    /**
     * Get one machine by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MachineDTO> findOne(Long id) {
        log.debug("Request to get Machine : {}", id);
        return machineRepository.findOneWithEagerRelationships(id).map(machineMapper::toDto);
    }

    /**
     * Delete the machine by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Machine : {}", id);
        machineRepository.deleteById(id);
    }
}
