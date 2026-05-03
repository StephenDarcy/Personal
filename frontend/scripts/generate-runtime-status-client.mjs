import {
  generateRuntimeStatusClient,
  runtimeStatusClientOutputDirectory,
} from "./runtime-status-client-generator.mjs";

await generateRuntimeStatusClient();

console.log(`Generated runtime status client at ${runtimeStatusClientOutputDirectory}`);
