import type { ReactNode } from "react";
import styles from "./section.module.css";

type SectionProps = {
  children: ReactNode;
  eyebrow?: string;
  id?: string;
  title: string;
};

export function Section({ children, eyebrow, id, title }: SectionProps) {
  const titleId = id ? `${id}-title` : undefined;

  return (
    <section className={styles.section} id={id} aria-labelledby={titleId}>
      <div className={styles.header}>
        {eyebrow ? <p className={styles.eyebrow}>{eyebrow}</p> : null}
        <h2 id={titleId}>{title}</h2>
      </div>
      {children}
    </section>
  );
}
