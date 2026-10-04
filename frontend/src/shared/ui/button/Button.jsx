import { cn } from '@/shared/lib/cn';
import './Button.css';

/** variant: primary | secondary | ghost | danger | icon | pill */
export function Button({ variant = 'primary', className, type = 'button', ...props }) {
  return <button type={type} className={cn('btn', `btn-${variant}`, className)} {...props} />;
}
