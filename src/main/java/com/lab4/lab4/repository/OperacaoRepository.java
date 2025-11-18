package com.lab4.lab4.repository;

import com.lab4.lab4.entity.Operacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperacaoRepository extends JpaRepository<Operacao, Long> {
    // Métodos customizados podem ser adicionados aqui
}
