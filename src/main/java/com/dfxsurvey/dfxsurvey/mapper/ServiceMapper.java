package com.dfxsurvey.dfxsurvey.mapper;

import com.dfxsurvey.dfxsurvey.controllers.dto.SurveyDto;
import com.dfxsurvey.dfxsurvey.dao.entity.SurveyEntity;
import com.dfxsurvey.dfxsurvey.mapper.cfg.MapstructConfig;
import java.util.List;
import org.mapstruct.Mapper;


@Mapper(config = MapstructConfig.class)
public abstract class ServiceMapper {

    public abstract List<SurveyDto> toSurveyDtos(List<SurveyEntity> surveyEntities);

}
