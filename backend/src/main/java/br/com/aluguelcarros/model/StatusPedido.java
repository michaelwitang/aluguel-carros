package br.com.aluguelcarros.model;

/** Ciclo de vida: PENDENTE -> (APROVADO | REPROVADO) -> CONTRATADO; CANCELADO pelo cliente antes do contrato. */
public enum StatusPedido { PENDENTE, APROVADO, REPROVADO, CANCELADO, CONTRATADO }
