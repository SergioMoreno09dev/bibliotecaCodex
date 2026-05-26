package com.universidad.biblio.repository;

import com.universidad.biblio.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByReceptorIdOrderByFechaEnvioDesc(int receptorId);

    List<Mensaje> findByEmisorIdOrderByFechaEnvioDesc(int emisorId);

    Optional<Mensaje> findByIdAndReceptorId(Long id, int receptorId);

    long countByReceptorIdAndLeidoFalse(int receptorId);
}
