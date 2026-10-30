/** tone: amber | red | green | teal */
export function KpiCard({
  icon,
  label,
  value,
  caption = 'Atualizado hoje',
  tone,
  percent,
  percentHint,
}) {
  return (
    <article className="kpi">
      <div className="kpi-top">
        <div className={`kpi-icon ${tone}`}>{icon}</div>
        <span>{label}</span>
      </div>
      <div className="kpi-body">
        <div>
          <strong>{value}</strong>
          <small>{caption}</small>
        </div>
        <div
          className={`donut ${tone}`}
          style={{ '--progress': `${percent * 3.6}deg` }}
          title={percentHint}
        >
          <b>{percent}%</b>
        </div>
      </div>
    </article>
  );
}
