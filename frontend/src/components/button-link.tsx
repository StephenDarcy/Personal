import type { AnchorHTMLAttributes, ReactNode } from "react";
import styles from "./button-link.module.css";

type ButtonLinkVariant = "primary" | "secondary" | "quiet";

type ButtonLinkProps = AnchorHTMLAttributes<HTMLAnchorElement> & {
  children: ReactNode;
  variant?: ButtonLinkVariant;
};

export function ButtonLink({
  children,
  className,
  variant = "primary",
  ...props
}: ButtonLinkProps) {
  const classes = [styles.buttonLink, styles[variant], className]
    .filter(Boolean)
    .join(" ");

  return (
    <a className={classes} {...props}>
      {children}
    </a>
  );
}
