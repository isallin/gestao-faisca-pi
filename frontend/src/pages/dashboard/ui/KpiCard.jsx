/** tone: amber | red | green */
export function KpiCard({ icon, label, value, tone, percent }) {
  return (
    <article className="kpi">
      <div className={`kpi-icon ${tone}`}>{icon}</div>
      <div>
        <small>{label}</small>
        <strong>{value}</strong>
        <span>Atualizado hoje</span>
      </div>
      <div className={`donut ${tone}`} style={{ '--progress': `${percent * 3.6}deg` }}>
        <b>{percent}%</b>
      </div>
    </article>
  );
}
