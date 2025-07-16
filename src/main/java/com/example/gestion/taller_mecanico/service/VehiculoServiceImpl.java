package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.exceptions.ClienteNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.VehiculoNotFoundException;
import com.example.gestion.taller_mecanico.mapper.VehiculoMapper;
import com.example.gestion.taller_mecanico.model.dto.VehiculoClientRequest;
import com.example.gestion.taller_mecanico.model.dto.VehiculoRequest;
import com.example.gestion.taller_mecanico.model.dto.VehiculoResponse;
import com.example.gestion.taller_mecanico.model.entity.Cliente;
import com.example.gestion.taller_mecanico.model.entity.Usuario;
import com.example.gestion.taller_mecanico.model.entity.Vehiculo;
import com.example.gestion.taller_mecanico.repository.ClienteRepository;
import com.example.gestion.taller_mecanico.repository.VehiculoRepository;
import com.example.gestion.taller_mecanico.specification.VehiculoSpecification;
import com.example.gestion.taller_mecanico.utils.enums.VehiculoEstado;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.gestion.taller_mecanico.utils.enums.Rol;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoMapper vehiculoMapper;

    @Override
    public List<VehiculoResponse> findAll() {
        return vehiculoRepository.findAll().stream()
                .map(vehiculoMapper::toVehiculoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehiculoResponse findById(Long id) {
        return vehiculoRepository.findById(id)
                .map(vehiculoMapper::toVehiculoResponse)
                .orElseThrow(() -> new VehiculoNotFoundException("Vehiculo no encontrado con ID: " + id));
    }

    @Override
    public VehiculoResponse findByPlaca(String placa) {
        return vehiculoRepository.findByPlaca(placa)
                .map(vehiculoMapper::toVehiculoResponse)
                .orElseThrow(() -> new VehiculoNotFoundException("Vehiculo no encontrado con placa: " + placa));
    }

    @Override
    public VehiculoResponse save(VehiculoRequest vehiculoRequest) {
        Cliente cliente = clienteRepository.findById(vehiculoRequest.getClienteId())
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + vehiculoRequest.getClienteId()));

        Vehiculo vehiculo = Vehiculo.builder()
                .cliente(cliente)
                .placa(vehiculoRequest.getPlaca())
                .marca(vehiculoRequest.getMarca())
                .modelo(vehiculoRequest.getModelo())
                .anio(vehiculoRequest.getAnio())
                .motor(vehiculoRequest.getMotor())
                .tipoVehiculo(vehiculoRequest.getTipoVehiculo())
                .estado(VehiculoEstado.valueOf(vehiculoRequest.getEstado().toUpperCase()))
                .build();
        return vehiculoMapper.toVehiculoResponse(vehiculoRepository.save(vehiculo));
    }

    @Override
    public VehiculoResponse update(Long id, VehiculoRequest vehiculoRequest) {
        return vehiculoRepository.findById(id)
                .map(vehiculo -> {
                    Cliente cliente = clienteRepository.findById(vehiculoRequest.getClienteId())
                            .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + vehiculoRequest.getClienteId()));

                    vehiculo.setCliente(cliente);
                    vehiculo.setPlaca(vehiculoRequest.getPlaca());
                    vehiculo.setMarca(vehiculoRequest.getMarca());
                    vehiculo.setModelo(vehiculoRequest.getModelo());
                    vehiculo.setAnio(vehiculoRequest.getAnio());
                    vehiculo.setMotor(vehiculoRequest.getMotor());
                    vehiculo.setTipoVehiculo(vehiculoRequest.getTipoVehiculo());
                    vehiculo.setEstado(VehiculoEstado.valueOf(vehiculoRequest.getEstado().toUpperCase()));
                    return vehiculoMapper.toVehiculoResponse(vehiculoRepository.save(vehiculo));
                }).orElseThrow(() -> new VehiculoNotFoundException("Vehiculo no encontrado con ID: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!vehiculoRepository.existsById(id)) {
            throw new VehiculoNotFoundException("Vehiculo no encontrado con ID: " + id);
        }
        vehiculoRepository.deleteById(id);
    }

    @Override
    public VehiculoResponse saveClientVehiculo(VehiculoClientRequest vehiculoClientRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        if (usuario.getRol() != Rol.CLIENTE) {
            throw new SecurityException("Solo los clientes pueden registrar sus propios vehículos.");
        }

        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado para el usuario autenticado."));

        Vehiculo vehiculo = Vehiculo.builder()
                .cliente(cliente)
                .placa(vehiculoClientRequest.getPlaca())
                .marca(vehiculoClientRequest.getMarca())
                .modelo(vehiculoClientRequest.getModelo())
                .anio(vehiculoClientRequest.getAnio())
                .motor(vehiculoClientRequest.getMotor())
                .tipoVehiculo(vehiculoClientRequest.getTipoVehiculo())
                .estado(VehiculoEstado.valueOf(vehiculoClientRequest.getEstado().toUpperCase()))
                .build();
        return vehiculoMapper.toVehiculoResponse(vehiculoRepository.save(vehiculo));
    }

    @Override
    public Page<VehiculoResponse> findVehiculosByFilters(String search, Long clienteId, Long tallerAsignadoId, String estado, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, Pageable pageable) {
        Specification<Vehiculo> spec = VehiculoSpecification.filterVehiculos(search, clienteId, tallerAsignadoId, estado, fechaCreacionDesde, fechaCreacionHasta);
        return vehiculoRepository.findAll(spec, pageable).map(vehiculoMapper::toVehiculoResponse);
    }

    @Override
    public Page<VehiculoResponse> findMyVehiculosByFilters(String search, String estado, Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado para el usuario autenticado."));

        Specification<Vehiculo> spec = VehiculoSpecification.filterVehiculos(search, cliente.getId(), null, estado, null, null);
        return vehiculoRepository.findAll(spec, pageable).map(vehiculoMapper::toVehiculoResponse);
    }

    @Override
    public VehiculoResponse updateClientVehiculo(Long id, VehiculoClientRequest vehiculoClientRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        if (usuario.getRol() != Rol.CLIENTE) {
            throw new SecurityException("Solo los clientes pueden editar sus propios vehículos.");
        }

        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado para el usuario autenticado."));

        return vehiculoRepository.findById(id)
                .map(vehiculo -> {
                    if (!vehiculo.getCliente().getId().equals(cliente.getId())) {
                        throw new SecurityException("No tiene permiso para editar este vehículo.");
                    }
                    vehiculo.setPlaca(vehiculoClientRequest.getPlaca());
                    vehiculo.setMarca(vehiculoClientRequest.getMarca());
                    vehiculo.setModelo(vehiculoClientRequest.getModelo());
                    vehiculo.setAnio(vehiculoClientRequest.getAnio());
                    vehiculo.setMotor(vehiculoClientRequest.getMotor());
                    vehiculo.setTipoVehiculo(vehiculoClientRequest.getTipoVehiculo());
                    vehiculo.setEstado(VehiculoEstado.valueOf(vehiculoClientRequest.getEstado().toUpperCase()));
                    return vehiculoMapper.toVehiculoResponse(vehiculoRepository.save(vehiculo));
                }).orElseThrow(() -> new VehiculoNotFoundException("Vehiculo no encontrado con ID: " + id));
    }
}