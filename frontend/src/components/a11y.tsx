import type { AnchorHTMLAttributes, ReactNode } from "react";
import styles from "./a11y.module.css";

type SkipLinkProps = AnchorHTMLAttributes<HTMLAnchorElement> & {
  children: ReactNode;
  href: `#${string}`;
};

export function SkipLink({ children, href, ...props }: SkipLinkProps) {
  return (
    <a className={styles.skipLink} href={href} {...props}>
      {children}
    </a>
  );
}
