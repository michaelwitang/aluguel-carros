# Histórias de Usuário

| ID | História | Critérios de aceitação |
|----|----------|------------------------|
| US01 | Como **cliente**, quero me cadastrar informando RG, CPF, nome, endereço, profissão e até 3 rendimentos, para poder usar o sistema. | CPF/RG únicos; no máximo 3 rendimentos; senha mínima de 6 caracteres. |
| US02 | Como **agente** (empresa ou banco), quero me cadastrar com CNPJ e tipo, para avaliar pedidos. | CNPJ único; tipo EMPRESA ou BANCO. |
| US03 | Como **usuário**, quero fazer login com usuário e senha, para acessar o sistema. | Sem login nenhuma funcionalidade é acessível; senha armazenada com BCrypt; sessão via JWT. |
| US04 | Como **cliente**, quero consultar, atualizar e excluir meu cadastro. | Só o próprio cliente altera/exclui; exclusão bloqueada se houver pedidos. |
| US05 | Como **cliente**, quero introduzir um pedido de aluguel escolhendo um automóvel e o período. | Pedido criado com status PENDENTE; data final posterior à inicial. |
| US06 | Como **cliente**, quero consultar meus pedidos e seu status. | Lista apenas os pedidos do próprio cliente, com status e parecer. |
| US07 | Como **cliente**, quero modificar um pedido pendente. | Apenas pedidos PENDENTES. |
| US08 | Como **cliente**, quero cancelar um pedido ainda não contratado. | Status PENDENTE ou APROVADO → CANCELADO. |
| US09 | Como **agente**, quero avaliar financeiramente um pedido (com acesso à renda do cliente). | PENDENTE → APROVADO/REPROVADO com parecer obrigatório. |
| US10 | Como **agente**, quero modificar um pedido ainda não contratado. | Bloqueado para CONTRATADO/CANCELADO. |
| US11 | Como **agente**, quero executar o contrato de um pedido aprovado, definindo o proprietário do automóvel (cliente, empresa ou banco). | Apenas pedidos APROVADOS; o automóvel passa a ter o proprietário escolhido. |
| US12 | Como **banco**, quero associar um contrato de crédito ao aluguel. | Exigido quando o pedido solicita crédito; só agentes BANCO concedem. |
| US13 | Como **agente**, quero manter o cadastro de automóveis (matrícula, ano, marca, modelo, placa). | Placa e matrícula únicas; exclusão bloqueada se houver pedidos. |
