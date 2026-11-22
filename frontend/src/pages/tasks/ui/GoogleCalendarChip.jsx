import { useEffect, useState } from 'react';
import { CalendarDays, Link2, Unlink } from 'lucide-react';
import { getCalendarStatus, getConnectUrl, disconnectCalendar } from '@/shared/api/googleCalendar';

const HINT =
  'Tarefas, projetos e registros de hora criados por você aparecem automaticamente no seu ' +
  'Google Calendar. Editar ou excluir no Faísca atualiza o evento; excluir no Faísca apaga o evento.';

/**
 * Card compacto de integração com o Google Calendar, exibido ao lado do
 * título "Tarefas": mostra o status da conexão e o botão Conectar/Desconectar.
 */
export function GoogleCalendarChip() {
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
      setMessage('Desconectado.');
    } catch {
      setMessage('Erro ao desconectar.');
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="gcal-chip" title={HINT}>
      <CalendarDays size={16} />
      <span className="gcal-name">Google Calendar</span>
      <span className={`gcal-status ${connected ? 'on' : ''}`}>
        <i /> {loading ? 'Verificando…' : connected ? 'Conectado' : 'Desconectado'}
      </span>
      {message && <small className="gcal-message">{message}</small>}
      {connected ? (
        <button type="button" className="gcal-btn on" onClick={handleDisconnect} disabled={busy}>
          <Unlink size={12} /> Desconectar
        </button>
      ) : (
        <button type="button" className="gcal-btn off" onClick={handleConnect} disabled={busy || loading}>
          <Link2 size={12} /> Conectar
        </button>
      )}
    </div>
  );
}
