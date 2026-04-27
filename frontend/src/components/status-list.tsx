import styles from "./status-list.module.css";

type StatusItem = {
  label: string;
  value: string;
};

type StatusListProps = {
  items: StatusItem[];
};

export function StatusList({ items }: StatusListProps) {
  return (
    <dl className={styles.list}>
      {items.map((item) => (
        <div className={styles.item} key={item.label}>
          <dt>{item.label}</dt>
          <dd>{item.value}</dd>
        </div>
      ))}
    </dl>
  );
}
