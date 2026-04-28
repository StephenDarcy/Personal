import Image from "next/image";
import { ButtonLink } from "@/components/button-link";
import { Card, Cluster, Container, PillList } from "@/components/primitives";
import { Section } from "@/components/section";
import styles from "./page.module.css";

const profileLinks = [
  {
    label: "GitHub",
    href: "https://github.com/StephenDarcy",
    variant: "primary" as const,
  },
  {
    label: "LinkedIn",
    href: "https://ie.linkedin.com/in/stephen-darcy-0871141b8",
    variant: "secondary" as const,
  },
];

const stackGroups = [
  {
    title: "Interfaces",
    items: ["TypeScript", "Next.js", "React", "Accessible UI"],
  },
  {
    title: "Services",
    items: ["Java", "Spring Boot", "OpenAPI", "Validation"],
  },
  {
    title: "Delivery",
    items: ["Docker", "GitHub Actions", "Security hygiene", "Observability"],
  },
];

const principles = [
  "Factual public copy",
  "Small reviewable slices",
  "Explicit architecture",
  "Static-first delivery",
];

export default function Home() {
  return (
    <main className={styles.pageShell} id="main-content" tabIndex={-1}>
      <Container as="section" className={styles.hero} aria-labelledby="hero-title">
        <div className={styles.heroCopy}>
          <p className={styles.kicker}>Software engineer</p>
          <h1 id="hero-title">Stephen Darcy</h1>
          <p className={styles.lede}>
            I build production-shaped software across polished interfaces,
            reliable services, cloud-aware architecture, and secure delivery
            practices.
          </p>
          <Cluster className={styles.actions} aria-label="Professional links">
            {profileLinks.map((link) => (
              <ButtonLink
                href={link.href}
                key={link.label}
                target="_blank"
                rel="noreferrer"
                variant={link.variant}
              >
                {link.label}
              </ButtonLink>
            ))}
          </Cluster>
        </div>

        <div className={styles.profilePanel} aria-label="Profile summary">
          <div className={styles.portraitFrame}>
            <Image
              src="https://avatars.githubusercontent.com/u/55543547?v=4"
              alt="Stephen Darcy GitHub avatar"
              width={192}
              height={192}
              priority
            />
          </div>
          <div className={styles.panelCopy}>
            <p className={styles.panelLabel}>Public first slice</p>
            <p>
              A compact personal site focused on engineering range, public-safe
              stack signals, and clear professional routes.
            </p>
          </div>
          <Image
            src="/architecture-mark.svg"
            alt=""
            className={styles.architectureMark}
            width={720}
            height={540}
            priority
          />
        </div>
      </Container>

      <Section
        eyebrow="About"
        id="about"
        title="I care about the shape of the work, not just the screen it lands on."
      >
        <p className={styles.sectionText}>
          My public work centers on typed frontend systems, clear API
          boundaries, reviewable architecture decisions, and delivery practices
          that keep software understandable after the first release.
        </p>
      </Section>

      <Section eyebrow="Stack" id="stack" title="A small public map of the tools and habits I use.">
        <div className={styles.stackGrid}>
          {stackGroups.map((group) => (
            <Card heading={group.title} key={group.title}>
              <PillList items={group.items} />
            </Card>
          ))}
        </div>
      </Section>

      <Section eyebrow="Contact" id="contact" title="Find the public profile paths here.">
        <div className={styles.contactBand}>
          <p>
            For now, this site keeps contact intentionally simple: GitHub for
            code and repository work, LinkedIn for professional context.
          </p>
          <Cluster className={styles.contactLinks} justify="end">
            {profileLinks.map((link) => (
              <ButtonLink
                href={link.href}
                key={link.label}
                target="_blank"
                rel="noreferrer"
                variant={link.variant}
              >
                {link.label}
              </ButtonLink>
            ))}
          </Cluster>
        </div>
        <ul className={styles.principleList} aria-label="Public content principles">
          {principles.map((principle) => (
            <li key={principle}>{principle}</li>
          ))}
        </ul>
      </Section>
    </main>
  );
}
