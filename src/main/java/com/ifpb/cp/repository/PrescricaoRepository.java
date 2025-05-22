package com.ifpb.cp.repository;

import com.ifpb.cp.model.Prescricao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescricaoRepository  extends JpaRepository<Prescricao, Long> {
}
