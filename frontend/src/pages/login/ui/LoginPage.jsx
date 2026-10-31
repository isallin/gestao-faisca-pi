import { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Eye, EyeOff } from 'lucide-react';
import logo from '@/shared/assets/faisca-logo.png';
import { login } from '@/shared/api/auth';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { Button, Field, FormAlert } from '@/shared/ui';
import './LoginPage.css';

export function LoginPage() {
  usePageTitle('Entrar');
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    const form = new FormData(event.currentTarget);

    const email = String(form.get('email') ?? '').trim();
    const senha = String(form.get('senha') ?? '');

    if (!email || !senha) {
      return setError('Preencha e-mail e senha para continuar.');
    }

    setError('');
    setIsSubmitting(true);

    try {
      await login({ email, senha });
      navigate('/dashboard');
    } catch (requestError) {
      if (axios.isAxiosError(requestError) && requestError.response?.status === 401) {
        setError('E-mail ou senha inválidos.');
      } else {
        setError('Nao foi possivel entrar no momento. Verifique a conexao com o servidor.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main className="login-page">
      <section className="login-brand">
        <img src={logo} alt="Faísca Arquitetura" />
        {/* <p>
          Projetos organizados.
          <br />
          Ideias com espaço para crescer.
        </p> */}
      </section>

      <section className="login-form-panel">
        <form onSubmit={handleSubmit}>
          <small>Seu escritório em um só lugar</small>
          <h1>Bem-vindo</h1>
          <h2>Entre na sua conta</h2>
          <FormAlert>{error}</FormAlert>

          <Field label="E-mail" name="email" type="email" placeholder="voce@faisca.arq.br" autoFocus />

          <label className="field">
            <span>Senha</span>
            <div className="password-field">
              <input
                className="input"
                name="senha"
                type={showPassword ? 'text' : 'password'}
                placeholder="Digite sua senha"
              />
              <Button
                variant="icon"
                aria-label={showPassword ? 'Ocultar senha' : 'Mostrar senha'}
                onClick={() => setShowPassword((v) => !v)}
              >
                {showPassword ? <EyeOff /> : <Eye />}
              </Button>
            </div>
          </label>

          <button
            type="button"
            className="forgot"
            onClick={() => setError('Entre em contato com a administradora para redefinir sua senha.')}
          >
            Esqueceu a senha?
          </button>
          <Button variant="pill" type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Entrando...' : 'Entrar'}
          </Button>
          <p className="demo-note">Use o mesmo e-mail e senha cadastrados no backend.</p>
        </form>
      </section>
    </main>
  );
}
