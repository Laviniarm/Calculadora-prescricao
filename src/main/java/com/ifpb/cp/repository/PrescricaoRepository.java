package com.ifpb.cp.repository;

import com.ifpb.cp.model.Prescricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescricaoRepository  extends JpaRepository<Prescricao, Long> {

    @Query("SELECT p FROM Prescricao p WHERE p.usuario.id = :usuarioId")
    List<Prescricao> listarPrescricaoPorUsuario(@Param("usuarioId") Long usuarioId);
}
