package co.edu.uco.backendvictus.application.usecase.ciudad;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadResponse;
import co.edu.uco.backendvictus.application.mapper.CiudadApplicationMapper;
import co.edu.uco.backendvictus.domain.port.CiudadRepository;

@Service
public class ListCiudadUseCase {

    private final CiudadRepository ciudadRepository;
    private final CiudadApplicationMapper mapper;

    public ListCiudadUseCase(final CiudadRepository ciudadRepository, final CiudadApplicationMapper mapper) {
        this.ciudadRepository = ciudadRepository;
        this.mapper = mapper;
    }

    public List<CiudadResponse> execute() {
        return mapper.toResponseList(ciudadRepository.findAll());
    }
}
