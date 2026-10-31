import { createElement, useState } from 'react';
import { NavLink, Link } from 'react-router-dom';
import {
  BriefcaseBusiness,
  Building2,
  CalendarCheck2,
  ClipboardClock,
  ContactRound,
  Handshake,
  LayoutDashboard,
  LogOut,
  Menu,
  X,
} from 'lucide-react';
import { clearAuthSession } from '@/shared/api/auth';
import { Button } from '@/shared/ui';
import './AppShell.css';

const NAV_ITEMS = [
  { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/tarefa', label: 'Tarefas', icon: CalendarCheck2 },
  { to: '/registros', label: 'Registros', icon: ClipboardClock },
  { to: '/clientes', label: 'Clientes', icon: ContactRound },
  { to: '/fornecedores', label: 'Fornecedores', icon: Handshake },
  { to: '/projetos', label: 'Projetos', icon: Building2 },
  { to: '/colaborador', label: 'Colaboradores', icon: BriefcaseBusiness },
];

export function AppShell({ children }) {
  const [open, setOpen] = useState(false);
  const close = () => setOpen(false);

  return (
    <div className="app-layout">
      <Button
        variant="icon"
        className="mobile-menu"
        onClick={() => setOpen(true)}
        aria-label="Abrir menu"
      >
        <Menu />
      </Button>

      <aside className={open ? 'sidebar sidebar-open' : 'sidebar'}>
        <Button variant="icon" className="sidebar-close" onClick={close} aria-label="Fechar menu">
          <X />
        </Button>
        <div className="profile">
          <div>CF</div>
          <span>Camila Ferreira</span>
          <small>Perfil</small>
        </div>
        <nav aria-label="Menu principal">
          {NAV_ITEMS.map(({ to, label, icon: Icon }) => (
            <NavLink key={to} to={to} onClick={close} className={({ isActive }) => (isActive ? 'nav-active' : '')}>
              {createElement(Icon, { size: 20 })}
              <span>{label}</span>
            </NavLink>
          ))}
        </nav>
        <Link to="/login" className="logout" onClick={clearAuthSession}>
          <LogOut size={20} />
          <span>Sair</span>
        </Link>
      </aside>

      {open && <div className="sidebar-backdrop" onClick={close} />}
      <main>{children}</main>
    </div>
  );
}
