import { useEffect, useState } from 'react';
import { CalendarDays, Link2, Unlink } from 'lucide-react';
import { getCalendarStatus, getConnectUrl, disconnectCalendar } from '@/shared/api/googleCalendar';

/**
 * Card de integração com o Google Calendar: mostra o status da conexão
 * e oferece os botões Conectar/Desconectar.
 */
export function GoogleCalendarCard() {
  const [connected, setConnected] = useState(false);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const google = params.get('google');
    if (google) window.history.replaceState({}, '', window.location.pathname);
    if (google === 'connected') setMessage('Conectado com sucesso!');
    if (google === 'error') setMessage('Não foi possível conectar. Tente novamente.');

    getCalendarStatus()
      .then((data) => setConnected(Boolean(data.connected)))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const handleConnect = async () => {
    setBusy(true);
    try {
      const url = await getConnectUrl();
      window.location.href = url;
    } catch {
      setMessage('Erro ao iniciar a conexão.');
      setBusy(false);
    }
  };

  const handleDisconnect = async () => {
    setBusy(true);
    try {
      await disconnectCalendar();
      setConnected(false);
      setMessage('Desconectado. Os eventos já criados no Google permanecem.');
    } catch {
      setMessage('Erro ao desconectar.');
    } finally {
      setBusy(false);
    }
  };

  return (
    <article className="dash-panel calendar-card">
      <header>
        <h2>
          <CalendarDays size={20} /> Google Calendar
        </h2>
        <span className={`calendar-status ${connected ? 'on' : ''}`}>
          {loading ? 'Verificando…' : connected ? 'Conectado' : 'Desconectado'}
        </span>
      </header>
      <p className="calendar-hint">
        Tarefas, projetos e registros de hora criados por você aparecem automaticamente no seu Google
        Calendar. Editar ou excluir no Faísca atualiza o evento; excluir no Faísca apaga o evento.
      </p>
      {message && <small className="calendar-message">{message}</small>}
      {connected ? (
        <button type="button" className="calendar-btn" onClick={handleDisconnect} disabled={busy}>
          <Unlink size={16} /> Desconectar
        </button>
      ) : (
        <button
          type="button"
          className="calendar-btn"
          onClick={handleConnect}
          disabled={busy || loading}
        >
          <Link2 size={16} /> Conectar Google Calendar
        </button>
      )}
    </article>
  );
}
