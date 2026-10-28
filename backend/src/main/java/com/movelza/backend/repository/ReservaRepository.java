
package com.movelza.backend.repository;

import com.movelza.backend.model.Reserva;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    @Query("""
        SELECT COUNT(r)
        FROM Reserva r
        WHERE r.veiculo.id = :veiculoId
          AND r.status IN ('Pendente', 'Confirmada')
          AND r.dataRetirada < :devolucao
          AND r.dataDevolucao > :retirada
        """)
    long contarConflitos(
            @Param("veiculoId") String veiculoId,
            @Param("retirada") LocalDateTime retirada,
            @Param("devolucao") LocalDateTime devolucao
    );
}
