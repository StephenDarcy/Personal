import Image from "next/image";
import { ButtonLink } from "@/components/button-link";
import { Section } from "@/components/section";
import { SignalCard } from "@/components/signal-card";
import { StatusList } from "@/components/status-list";
import styles from "./page.module.css";

const deliverySignals = [
  { label: "ADR", value: "Decisions before drift" },
  { label: "CI", value: "Checks before merge" },
  { label: "API", value: "Contract-owned boundary" },
  { label: "Ops", value: "Evidence over folklore" },
];

const platformAreas = [
  {
    title: "Frontend craft",
    detail: "Static-export Next.js, typed interfaces, and a component language I can keep refining in public.",
    tone: "green" as const,
  },
  {
    title: "Backend API",
    detail: "Spring Boot boundaries, validation, OpenAPI, and observable behavior before feature sprawl.",
    tone: "blue" as const,
  },
  {
    title: "Delivery system",
    detail: "Review, least-privilege automation, scanning, and release evidence as ordinary practice.",
    tone: "rust" as const,
  },
];

const principles = [
  "Public-safe by default",
  "Small reviewable slices",
  "Architecture recorded early",
  "Static first when possible",
];

export default function Home() {
  return (
    <main className={styles.pageShell}>
      <section className={styles.hero} aria-labelledby="hero-title">
        <div className={styles.heroCopy}>
          <p className={styles.kicker}>Public engineering platform</p>
          <h1 id="hero-title">Stephen Darcy</h1>
          <p className={styles.lede}>
            My production-shaped showcase for secure software delivery,
            frontend craft, backend API design, and operational clarity.
          </p>
          <div className={styles.actions} aria-label="Primary project links">
            <ButtonLink href="#system">View system shape</ButtonLink>
            <ButtonLink href="#delivery" variant="secondary">
              Review delivery signals
            </ButtonLink>
          </div>
        </div>

        <div className={styles.visualPanel} aria-label="Architecture delivery map">
          <Image
            src="/architecture-mark.svg"
            alt=""
            className={styles.architectureMark}
            width={720}
            height={540}
            priority
          />
          <StatusList items={deliverySignals} />
        </div>
      </section>

      <Section
        eyebrow="System shape"
        id="system"
        title="Built as a real platform from the first slice."
      >
        <div className={styles.areaGrid}>
          {platformAreas.map((area) => (
            <SignalCard label={area.title} tone={area.tone} key={area.title}>
              <p>{area.detail}</p>
            </SignalCard>
          ))}
        </div>
      </Section>

      <Section
        eyebrow="Delivery posture"
        id="delivery"
        title="Small PRs, explicit decisions, public-safe defaults."
      >
        <ul className={styles.principleList} aria-label="Delivery principles">
          {principles.map((principle) => (
            <li key={principle}>{principle}</li>
          ))}
        </ul>
      </Section>
    </main>
  );
}
