package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.*;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.AbsenceDto;
import org.harmoniapp.harmoniwebapi.contracts.StatusDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Service class for managing absences.
 * Provides methods to retrieve absences information.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class AbsenceService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves all Absences by user ID.
     *
     * @param id the ID of the user whose absences are to be retrieved
     * @return a list of AbsenceDto corresponding to the user's absences
     */
    public List<AbsenceDto> getAbsenceByUserId(long id) {
        List<Absence> userAbsences = repositoryCollector.getAbsences().findByUserId(id);

        return userAbsences.stream()
                .map(AbsenceDto::fromEntity)
                .toList();
    }

    /**
     * Retrieves all Absences by status name.
     *
     * @param statusName the name of the status to filter absences by
     * @return a list of AbsenceDto representing the absences with the specified status
     */
    public List<AbsenceDto> getAbsenceByStatus(String statusName) {
        List<Absence> absencesWithStatus = repositoryCollector.getAbsences().findAbsenceByStatusName(statusName);

        return absencesWithStatus.stream()
                .map(AbsenceDto::fromEntity)
                .toList();
    }

    /**
     * Retrieves a list of all absences.
     *
     * @return a list of AbsenceDto containing the details of all absences
     * @throws RuntimeException if an error occurs while retrieving absences
     */
    public List<AbsenceDto> getAllAbsences() {
        try {
            var absence = repositoryCollector.getAbsences().findAll();
            return absence.stream()
                    .map(AbsenceDto::fromEntity)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new Absence.
     *
     * @param absenceDto the AbsenceDto containing the details of the absence to create
     * @return the created AbsenceDto
     * @throws IllegalArgumentException if the user or absence type ID provided does not exist
     * @throws RuntimeException if an error occurs during creation
     */
    public AbsenceDto createAbsence(AbsenceDto absenceDto) { //TODO: change subbmision and updated to date anh hour

        User user = repositoryCollector.getUsers()
                .findById(absenceDto.userId())
                .orElseThrow(IllegalArgumentException::new);

        AbsenceType absenceType = repositoryCollector.getAbsenceTypes()
                .findById(absenceDto.absenceTypeId())
                .orElseThrow(IllegalArgumentException::new);

        Status status = repositoryCollector.getStatuses()
                .findById(1L)    //Here status MUST be awaiting
                .orElseThrow(IllegalArgumentException::new);

        Absence absence = absenceDto.toEntity(user, absenceType, status);
        absence.setSubmission(LocalDate.now());
        absence.setUpdated(LocalDate.now());
        Absence savedAbsence = repositoryCollector.getAbsences().save(absence);
        return AbsenceDto.fromEntity(savedAbsence);
    }

    /**
     * Updates an existing Absence or creates a new one if it doesn't exist.
     * IMPORTANT it is updated by employee
     *
     * @param id the ID of the absence to update
     * @param absenceDto the AbsenceDto containing the updated details of the absence
     * @return the updated or newly created AbsenceDto
     * @throws IllegalArgumentException if the user or absence type ID provided does not exist
     * @throws RuntimeException if an error occurs during the update process
     */
    @Transactional
    public AbsenceDto updateAbsence(long id, AbsenceDto absenceDto) {
        try {
            if(absenceDto.status().getId() != 1L){ // Employee can only change absence if the status is awaiting
                throw new RuntimeException("An error occurred: You are not allowed to update the status of the absence");
            }

            Absence existingAbsence = repositoryCollector.getAbsences().findById(id)
                    .orElse(null);

            User user = repositoryCollector.getUsers().findById(absenceDto.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            AbsenceType absenceType = repositoryCollector.getAbsenceTypes().findById(absenceDto.absenceTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("AbsenceType not found"));

            Status status = repositoryCollector.getStatuses()
                    .findById(1L)    //Here status MUST be awaiting
                    .orElseThrow(IllegalArgumentException::new);

            if(existingAbsence == null) {
                Absence newAbsence = absenceDto.toEntity(user, absenceType, status);
                Absence savedAbsence = repositoryCollector.getAbsences().save(newAbsence);
                return AbsenceDto.fromEntity(savedAbsence);
            } else {
                existingAbsence.setStart(absenceDto.start());
                existingAbsence.setEnd(absenceDto.end());
                existingAbsence.setUser(user);
                existingAbsence.setAbsenceType(absenceType);
                existingAbsence.setStatus(status);
                existingAbsence.setSubmission(absenceDto.submission());
                existingAbsence.setUpdated(absenceDto.updated());
                Absence updatedAbsence = repositoryCollector.getAbsences().save(existingAbsence);
                return AbsenceDto.fromEntity(updatedAbsence);
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the status of an existing Absence.
     * IMPORTANT it is updated by employer
     *
     * @param id the ID of the absence to update
     * @param statusId the ID of the new status to set for the absence
     * @return the updated AbsenceDto
     * @throws IllegalArgumentException if the provided status ID does not exist
     * @throws RuntimeException if the absence does not exist or if an error occurs during the update process
     */
    @Transactional
    public AbsenceDto updateAbsenceStatus(long id, long statusId) {
        try {
            Absence existingAbsence = repositoryCollector.getAbsences().findById(id)
                    .orElseThrow(() -> new RuntimeException("You can only change status if absence exists"));

            Status status = repositoryCollector.getStatuses()
                    .findById(statusId)
                    .orElseThrow(IllegalArgumentException::new);

            existingAbsence.setStatus(status);
            existingAbsence.setUpdated(LocalDate.now());
            Absence updatedAbsence = repositoryCollector.getAbsences().save(existingAbsence);
            return AbsenceDto.fromEntity(updatedAbsence);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update absence status: " + e.getMessage(), e);
        }
    }
}
