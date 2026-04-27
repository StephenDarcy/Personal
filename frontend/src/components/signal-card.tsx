import type { ReactNode } from "react";
import styles from "./signal-card.module.css";

type SignalCardProps = {
  children: ReactNode;
  label: string;
  tone?: "green" | "blue" | "rust";
};

export function SignalCard({ children, label, tone = "green" }: SignalCardProps) {
  return (
    <article className={`${styles.card} ${styles[tone]}`}>
      <p className={styles.label}>{label}</p>
      <div className={styles.body}>{children}</div>
    </article>
  );
}
