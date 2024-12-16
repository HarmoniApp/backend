package org.harmoniapp.services;

import java.util.List;

/**
 * Generic CRUD service interface for managing entities.
 *
 * @param <T> the type of the entity
 */
public interface CrudService<T> {

    /**
     * Retrieves an entity by its ID.
     *
     * @param id the ID of the entity
     * @return the entity with the specified ID
     */
    T getById(long id);

    /**
     * Retrieves all entities.
     *
     * @return a list of all entities
     */
    List<T> getAll();

    /**
     * Creates a new entity.
     *
     * @param dto the entity to create
     * @return the created entity
     */
    T create(T dto);

    /**
     * Updates an existing entity.
     *
     * @param id              the ID of the entity to update
     * @param contractTypeDto the updated entity
     * @return the updated entity
     */
    T updateById(long id, T contractTypeDto);

    /**
     * Deletes an entity by its ID.
     *
     * @param id the ID of the entity to delete
     */
    void deleteById(long id);
}
