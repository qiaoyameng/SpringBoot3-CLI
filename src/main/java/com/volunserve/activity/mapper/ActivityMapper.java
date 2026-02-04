package com.volunserve.activity.mapper;

import com.volunserve.activity.dto.ActivityCreateDTO;
import com.volunserve.activity.dto.ActivityDTO;
import com.volunserve.activity.dto.ActivityUpdateDTO;
import com.volunserve.activity.entity.Activity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActivityMapper {

    Activity toEntity(ActivityCreateDTO dto);

    ActivityDTO toDTO(Activity entity);

    void updateEntity(ActivityUpdateDTO dto, @MappingTarget Activity entity);
}
