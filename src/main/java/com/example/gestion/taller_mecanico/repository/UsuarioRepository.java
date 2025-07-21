package com.example.gestion.taller_mecanico.repository;

import com.example.gestion.taller_mecanico.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    Optional<Usuario> findByUsername(String username);
    
    @Query("SELECT u FROM Usuario u WHERE u.rol = com.example.gestion.taller_mecanico.utils.enums.Rol.TRABAJADOR AND NOT EXISTS (SELECT t FROM Trabajador t WHERE t.usuario.id = u.id)")
    List<Usuario> findTrabajadoresNoAsignados();
    
    @Query("SELECT u FROM Usuario u WHERE u.rol = com.example.gestion.taller_mecanico.utils.enums.Rol.CLIENTE AND NOT EXISTS (SELECT c FROM Cliente c WHERE c.usuario.id = u.id)")
    List<Usuario> findClientesNoAsignados();
}