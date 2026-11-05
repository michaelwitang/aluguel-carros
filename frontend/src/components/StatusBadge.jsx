const LABEL = { PENDENTE: "Pendente", APROVADO: "Aprovado", REPROVADO: "Reprovado", CANCELADO: "Cancelado", CONTRATADO: "Contratado" };
export default function StatusBadge({ status }) {
  return <span className={`badge ${status.toLowerCase()}`}>{LABEL[status]}</span>;
}
