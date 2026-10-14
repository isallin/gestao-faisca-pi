/** tone: amber | red | green */
export function KpiCard({ icon, label, value, tone, percent }) {
  return (
    <article className="kpi">
      <div className={`kpi-icon ${tone}`}>{icon}</div>
      <div>
        <span>{label}</span>
        <strong>{value}</strong>
        <small>Atualizado hoje</small>
      </div>
      <div className={`donut ${tone}`} style={{ '--progress': `${percent * 3.6}deg` }}>
        <b>{percent}%</b>
      </div>
    </article>
  );
}
