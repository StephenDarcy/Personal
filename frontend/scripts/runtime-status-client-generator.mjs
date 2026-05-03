import { rm } from "node:fs/promises";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

import { createClient } from "@hey-api/openapi-ts";

const scriptDirectory = dirname(fileURLToPath(import.meta.url));
export const frontendRoot = resolve(scriptDirectory, "..");
export const repositoryRoot = resolve(frontendRoot, "..");

export const runtimeStatusContractPath = resolve(
  repositoryRoot,
  "contracts",
  "runtime-status.openapi.json",
);

export const runtimeStatusClientOutputDirectory = resolve(
  frontendRoot,
  "src",
  "api",
  "generated",
  "runtime-status",
);

export async function generateRuntimeStatusClient(outputDirectory = runtimeStatusClientOutputDirectory) {
  await rm(outputDirectory, { recursive: true, force: true });

  await createClient({
    input: runtimeStatusContractPath,
    output: outputDirectory,
  });
}
