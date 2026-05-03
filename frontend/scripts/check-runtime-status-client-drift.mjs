import { mkdtemp, readdir, readFile, rm } from "node:fs/promises";
import { tmpdir } from "node:os";
import { join, relative, sep } from "node:path";

import {
  generateRuntimeStatusClient,
  runtimeStatusClientOutputDirectory,
} from "./runtime-status-client-generator.mjs";

async function listFiles(directory) {
  const entries = await readdir(directory, { withFileTypes: true });
  const files = [];

  for (const entry of entries) {
    const entryPath = join(directory, entry.name);

    if (entry.isDirectory()) {
      files.push(...(await listFiles(entryPath)));
    }

    if (entry.isFile()) {
      files.push(entryPath);
    }
  }

  return files.sort((left, right) => left.localeCompare(right));
}

function relativePath(fromDirectory, filePath) {
  return relative(fromDirectory, filePath).split(sep).join("/");
}

async function readFileMap(directory) {
  const files = await listFiles(directory);
  const fileMap = new Map();

  for (const filePath of files) {
    fileMap.set(relativePath(directory, filePath), await readFile(filePath));
  }

  return fileMap;
}

function compareFileMaps(committedFiles, regeneratedFiles) {
  const differences = [];
  const fileNames = new Set([...committedFiles.keys(), ...regeneratedFiles.keys()]);

  for (const fileName of [...fileNames].sort((left, right) => left.localeCompare(right))) {
    const committed = committedFiles.get(fileName);
    const regenerated = regeneratedFiles.get(fileName);

    if (!committed) {
      differences.push(`- ${fileName} exists only in regenerated output.`);
      continue;
    }

    if (!regenerated) {
      differences.push(`- ${fileName} exists only in committed output.`);
      continue;
    }

    if (!committed.equals(regenerated)) {
      differences.push(`- ${fileName} differs from regenerated output.`);
    }
  }

  return differences;
}

const temporaryRoot = await mkdtemp(join(tmpdir(), "runtime-status-client-"));
const temporaryOutputDirectory = join(temporaryRoot, "runtime-status");

try {
  await generateRuntimeStatusClient(temporaryOutputDirectory);

  const committedFiles = await readFileMap(runtimeStatusClientOutputDirectory);
  const regeneratedFiles = await readFileMap(temporaryOutputDirectory);
  const differences = compareFileMaps(committedFiles, regeneratedFiles);

  if (differences.length > 0) {
    console.error("Generated runtime status client is stale.");
    console.error("Run `npm --prefix frontend run contracts:generate` from the repository root.");
    console.error(differences.join("\n"));
    process.exit(1);
  }

  console.log("Generated runtime status client is up to date.");
} finally {
  await rm(temporaryRoot, { recursive: true, force: true });
}
