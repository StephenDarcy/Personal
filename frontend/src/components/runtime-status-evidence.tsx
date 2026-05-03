"use client";

import { useEffect, useState } from "react";
import {
  getRuntimeHealth,
  getRuntimeVersion,
  type HealthResponse,
  type VersionResponse,
} from "@/api/generated/runtime-status";
import styles from "./runtime-status-evidence.module.css";

type EvidencePhase = "checking" | "available" | "unavailable";

type RuntimeEvidence = {
  correlationState: string;
  health?: HealthResponse;
  message: string;
  phase: EvidencePhase;
  version?: VersionResponse;
};

const unavailableEvidence: RuntimeEvidence = {
  correlationState: "Not confirmed",
  message: "Runtime API status is not available from this page.",
  phase: "unavailable",
};

function createCorrelationId() {
  if (globalThis.crypto?.randomUUID) {
    return `web-${globalThis.crypto.randomUUID()}`;
  }

  return `web-${Date.now().toString(36)}`;
}

function configuredRuntimeApiBaseUrl() {
  const configured = process.env.NEXT_PUBLIC_RUNTIME_STATUS_API_BASE_URL?.trim();
  if (configured) {
    return configured;
  }

  if (globalThis.location?.hostname === "localhost") {
    return "http://localhost:8080";
  }

  return null;
}

function formatTimestamp(value: string | undefined) {
  if (!value) {
    return "Not available";
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return "Prepared timestamp";
  }

  return new Intl.DateTimeFormat("en", {
    dateStyle: "medium",
    timeStyle: "short",
    timeZone: "UTC",
  }).format(date);
}

function shortCommitSha(value: string | undefined) {
  return value ? value.slice(0, 12) : "Not available";
}

export function RuntimeStatusEvidence() {
  const [evidence, setEvidence] = useState<RuntimeEvidence>({
    correlationState: "Pending",
    message: "Checking runtime API status.",
    phase: "checking",
  });

  useEffect(() => {
    const abortController = new AbortController();
    const correlationId = createCorrelationId();

    async function loadRuntimeEvidence() {
      const baseUrl = configuredRuntimeApiBaseUrl();
      if (!baseUrl) {
        setEvidence({
          ...unavailableEvidence,
          message: "Runtime API status is not configured for this page.",
        });
        return;
      }

      const requestOptions = {
        baseUrl,
        headers: {
          "X-Correlation-ID": correlationId,
        },
        signal: abortController.signal,
      };

      const [healthResult, versionResult] = await Promise.all([
        getRuntimeHealth(requestOptions),
        getRuntimeVersion(requestOptions),
      ]);

      if (abortController.signal.aborted) {
        return;
      }

      const healthCorrelation = healthResult.response?.headers.get("X-Correlation-ID");
      const versionCorrelation = versionResult.response?.headers.get("X-Correlation-ID");
      const hasAcceptedCorrelation =
        healthCorrelation === correlationId || versionCorrelation === correlationId;

      if (healthResult.data || versionResult.data) {
        setEvidence({
          correlationState: hasAcceptedCorrelation ? "Confirmed" : "Response received",
          health: healthResult.data,
          message: "Runtime API responded after page hydration.",
          phase: "available",
          version: versionResult.data,
        });
        return;
      }

      setEvidence(unavailableEvidence);
    }

    void loadRuntimeEvidence().catch(() => {
      if (!abortController.signal.aborted) {
        setEvidence(unavailableEvidence);
      }
    });

    return () => abortController.abort();
  }, []);

  const items = [
    {
      label: "Readiness",
      value: evidence.health?.status === "ready" ? "Ready" : "Unavailable",
    },
    {
      label: "Service",
      value: evidence.health?.serviceName ?? evidence.version?.serviceName ?? "Not available",
    },
    {
      label: "Version",
      value: evidence.version?.version ?? "Not available",
    },
    {
      label: "Build",
      value: formatTimestamp(evidence.version?.buildTimestamp),
    },
    {
      label: "Commit",
      value: shortCommitSha(evidence.version?.commitSha),
    },
    {
      label: "Correlation",
      value: evidence.correlationState,
    },
  ];

  return (
    <div className={styles.panel} data-phase={evidence.phase} aria-live="polite">
      <div>
        <p className={styles.label}>Runtime API</p>
        <p className={styles.message}>{evidence.message}</p>
      </div>
      <dl className={styles.grid}>
        {items.map((item) => (
          <div className={styles.item} key={item.label}>
            <dt>{item.label}</dt>
            <dd>{item.value}</dd>
          </div>
        ))}
      </dl>
    </div>
  );
}
