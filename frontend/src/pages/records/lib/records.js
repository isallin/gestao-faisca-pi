import { formatDate } from '@/entities/task';

/** "2026-08-04T09:00" → "04/08/2026 09:00" (registros antigos, sem horário, mostram só a data). */
export const formatDateTime = (dateTime) =>
  dateTime
    ? `${formatDate(dateTime.slice(0, 10))}${dateTime.length > 10 ? ` ${dateTime.slice(11, 16)}` : ''}`
    : '';

export const formatHours = (hours) => `${Number(hours || 0).toLocaleString('pt-BR')} h`;

/**
 * Junta os registros de tempo de todas as tarefas em uma única lista,
 * do mais recente para o mais antigo.
 */
export function collectRecords(tasks) {
  return tasks
    .flatMap((task) =>
      (task.timeLogs ?? []).map((log) => ({
        id: `${task.id}-${log.id}`,
        task: task.name,
        project: task.project,
        member: task.member,
        date: log.date,
        start: log.start ?? '',
        end: log.end ?? '',
        hours: log.hours,
        note: log.note ?? '',
      })),
    )
    .sort((a, b) => (b.start || b.date || '').localeCompare(a.start || a.date || ''));
}
