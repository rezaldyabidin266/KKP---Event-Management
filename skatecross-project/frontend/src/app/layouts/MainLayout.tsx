import type { ReactNode } from 'react';

type MainLayoutProps = {
  children: ReactNode;
};

export function MainLayout({ children }: MainLayoutProps) {
  return (
    <div className="app-shell">
      <header className="app-header">
        <strong>Skyline Skate Cross</strong>
        <span>KKP - Project Skeleton</span>
      </header>
      <main className="app-main">{children}</main>
    </div>
  );
}
