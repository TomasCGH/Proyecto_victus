package co.edu.uco.backendvictus.application.usecase.administrador;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.uco.backendvictus.application.dto.administrador.AdministradorResponse;
import co.edu.uco.backendvictus.application.mapper.AdministradorApplicationMapper;
import co.edu.uco.backendvictus.domain.port.AdministradorRepository;

@Service
public class ListAdministradorUseCase {

    private final AdministradorRepository administradorRepository;
    private final AdministradorApplicationMapper mapper;

    public ListAdministradorUseCase(final AdministradorRepository administradorRepository,
            final AdministradorApplicationMapper mapper) {
        this.administradorRepository = administradorRepository;
        this.mapper = mapper;
    }

    public List<AdministradorResponse> execute() {
        return mapper.toResponseList(administradorRepository.findAll());
    }
}
