import { format, parseISO } from 'date-fns';
import { ptBR } from 'date-fns/locale';

const ISO_DATE = 'yyyy-MM-dd';

const parseDate = (value) => {
  if (!value) return undefined;
  const parsed = parseISO(value);
  return Number.isNaN(parsed.getTime()) ? undefined : parsed;
};

const toIso = (date) => format(date, ISO_DATE);

const capitalize = (text) => (text ? text[0].toUpperCase() + text.slice(1) : text);

export function taskProgress(task) {
  if (task.status === 'Concluido' || task.status === 'Concluído') return 100;
  const checklist = task.checklist ?? [];
  if (checklist.length === 0) return task.done ? 100 : 0;
  const done = checklist.filter((item) => item.done).length;
  return Math.round((done / checklist.length) * 100);
}

export function toTimelineTask(task) {
  const startDate = parseDate(task.start);
  const endDate = parseDate(task.end);
  const normalizedEnd = startDate && endDate && endDate < startDate ? startDate : endDate || startDate;

  return {
    ...task,
    phaseId: task.stageId ?? task.stage ?? 'sem-fase',
    phaseName: task.stage || 'Sem fase',
    startDate,
    endDate: normalizedEnd,
    progress: taskProgress(task),
  };
}

export function overlapsDateSelection(task, selection) {
  if (!selection?.from) return true;
  const taskDates = toTimelineTask(task);
  if (!taskDates.startDate || !taskDates.endDate) return false;

  const from = parseDate(selection.from);
  const to = parseDate(selection.to) || from;
  if (!from || !to) return true;

  return taskDates.startDate <= to && taskDates.endDate >= from;
}

export function selectionAnchor(selection) {
  return parseDate(selection?.from) || new Date();
}

export function calendarMonthLabel(selection) {
  const anchor = selectionAnchor(selection);
  return capitalize(format(anchor, "LLLL 'de' yyyy", { locale: ptBR }));
}

export function buildGanttTasks(tasks) {
  const phases = new Map();

  tasks
    .map(toTimelineTask)
    .filter((task) => task.startDate && task.endDate)
    .forEach((task) => {
      const key = String(task.phaseId);
      if (!phases.has(key)) {
        phases.set(key, {
          id: `phase-${key}`,
          text: task.phaseName,
          type: 'summary',
          open: true,
          phaseName: task.phaseName,
          children: [],
        });
      }
      phases.get(key).children.push(task);
    });

  const result = [];
  phases.forEach((phase) => {
    const starts = phase.children.map((task) => task.startDate).sort((a, b) => a - b);
    const ends = phase.children.map((task) => task.endDate).sort((a, b) => a - b);
    const phaseProgress = phase.children.length
      ? Math.round(phase.children.reduce((sum, task) => sum + task.progress, 0) / phase.children.length)
      : 0;

    result.push({
      id: phase.id,
      text: phase.text,
      type: 'summary',
      open: true,
      start: starts[0],
      end: ends.at(-1),
      progress: phaseProgress,
    });

    phase.children.forEach((task) => {
      result.push({
        id: task.id,
        parent: phase.id,
        text: task.name,
        start: task.startDate,
        end: task.endDate,
        progress: task.progress,
        type: 'task',
        project: task.project,
        status: task.status,
      });
    });
  });

  return result;
}

export function rangeFromSelection(selection) {
  const from = parseDate(selection?.from);
  const to = parseDate(selection?.to) || from;
  if (!from || !to) return undefined;
  return { start: from, end: to };
}

export function taskEndsOnDate(task, day) {
  const timelineTask = toTimelineTask(task);
  if (!timelineTask.endDate) return false;
  return toIso(timelineTask.endDate) === toIso(day);
}
