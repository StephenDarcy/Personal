import type { HTMLAttributes, ReactNode } from "react";
import styles from "./primitives.module.css";

type Tone = "default" | "panel";

type ContainerProps = HTMLAttributes<HTMLDivElement> & {
  children: ReactNode;
  as?: "div" | "section";
  tone?: Tone;
};

export function Container({
  as: Component = "div",
  children,
  className,
  tone = "default",
  ...props
}: ContainerProps) {
  return (
    <Component
      className={[styles.container, styles[tone], className].filter(Boolean).join(" ")}
      {...props}
    >
      {children}
    </Component>
  );
}

type ClusterProps = HTMLAttributes<HTMLDivElement> & {
  align?: "start" | "center";
  children: ReactNode;
  justify?: "start" | "end" | "between";
};

export function Cluster({
  align = "center",
  children,
  className,
  justify = "start",
  ...props
}: ClusterProps) {
  return (
    <div
      className={[styles.cluster, styles[`align-${align}`], styles[`justify-${justify}`], className]
        .filter(Boolean)
        .join(" ")}
      {...props}
    >
      {children}
    </div>
  );
}

type CardProps = HTMLAttributes<HTMLElement> & {
  children: ReactNode;
  heading?: string;
};

export function Card({ children, className, heading, ...props }: CardProps) {
  return (
    <article className={[styles.card, className].filter(Boolean).join(" ")} {...props}>
      {heading ? <h3>{heading}</h3> : null}
      {children}
    </article>
  );
}

type PillListProps = {
  items: string[];
};

export function PillList({ items }: PillListProps) {
  return (
    <ul className={styles.pillList}>
      {items.map((item) => (
        <li key={item}>{item}</li>
      ))}
    </ul>
  );
}
