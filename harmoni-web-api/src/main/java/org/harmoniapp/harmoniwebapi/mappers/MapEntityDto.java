package org.harmoniapp.harmoniwebapi.mappers;


public interface MapEntityDto<Entity, Dto> {
    Dto toDto(Entity entity);

    Entity toEntity(Dto dto);
}
