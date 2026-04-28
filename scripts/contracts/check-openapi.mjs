import { readFile } from "node:fs/promises";

const contractPath = new URL("../../contracts/runtime-status.openapi.json", import.meta.url);
const contract = JSON.parse(await readFile(contractPath, "utf8"));

const failures = [];

function assert(condition, message) {
  if (!condition) {
    failures.push(message);
  }
}

function get(path) {
  return path.split(".").reduce((value, key) => value?.[key], contract);
}

function getByPointer(pointer) {
  return pointer
    .replace(/^#\//, "")
    .split("/")
    .map((part) => part.replace(/~1/g, "/").replace(/~0/g, "~"))
    .reduce((value, key) => value?.[key], contract);
}

function resolveRef(value) {
  if (!value?.$ref) {
    return value;
  }
  return getByPointer(value.$ref);
}

function hasRef(value, ref) {
  return value?.$ref === ref;
}

function responseFor(path, status) {
  return contract.paths?.[path]?.get?.responses?.[status];
}

function operationParameters(path) {
  const pathItem = contract.paths?.[path] ?? {};
  const operation = pathItem.get ?? {};
  return [...(pathItem.parameters ?? []), ...(operation.parameters ?? [])].map(resolveRef);
}

function assertRuntimeOperation(path, operationId) {
  const operation = contract.paths?.[path]?.get;
  assert(operation, `${path} must define a GET operation.`);
  assert(operation?.operationId === operationId, `${path} must use operationId ${operationId}.`);
  assert(!operation?.requestBody, `${path} must not accept a request body.`);
  assert(
    operation?.parameters?.some((parameter) =>
      hasRef(parameter, "#/components/parameters/CorrelationId"),
    ),
    `${path} must document the X-Correlation-ID request header.`,
  );
  assert(
    responseFor(path, "200")?.headers?.["X-Correlation-ID"],
    `${path} 200 response must document the X-Correlation-ID response header.`,
  );
  assert(
    hasRef(responseFor(path, "400"), "#/components/responses/BadRequest"),
    `${path} must reference the shared BadRequest error response.`,
  );
  assert(
    hasRef(responseFor(path, "406"), "#/components/responses/NotAcceptable"),
    `${path} must reference the shared NotAcceptable error response.`,
  );
  assert(
    hasRef(responseFor(path, "500"), "#/components/responses/InternalServerError"),
    `${path} must reference the shared InternalServerError response.`,
  );
}

function assertRequiredFields(schemaName, requiredFields) {
  const required = get(`components.schemas.${schemaName}.required`) ?? [];
  for (const field of requiredFields) {
    assert(required.includes(field), `${schemaName} must require ${field}.`);
  }
}

function assertExampleSafe(value, label) {
  const serialized = JSON.stringify(value);
  const blockedPatterns = [
    [/AKIA[0-9A-Z]{16}/, "AWS access key"],
    [/gh[pousr]_[A-Za-z0-9_]{36,255}/, "GitHub token"],
    [/sk-[A-Za-z0-9_-]{32,}/, "API key token"],
    [/[A-Z0-9._%+-]+@(?!example\.(com|org|net)\b)[A-Z0-9.-]+\.[A-Z]{2,}/i, "non-example email"],
    [/\b(branch|username|machine|hostname|account id|token|secret)\b/i, "unsafe operational term"],
  ];

  for (const [pattern, name] of blockedPatterns) {
    assert(!pattern.test(serialized), `${label} contains ${name}.`);
  }
}

assert(contract.openapi === "3.1.0", "Contract must use OpenAPI 3.1.0.");
assertRuntimeOperation("/api/v1/health", "getRuntimeHealth");
assertRuntimeOperation("/api/v1/version", "getRuntimeVersion");
assert(
  hasRef(responseFor("/api/v1/health", "503"), "#/components/responses/ServiceUnavailable"),
  "/api/v1/health must reference the shared ServiceUnavailable response.",
);
assert(
  !operationParameters("/api/v1/health").some((parameter) => parameter?.in === "query"),
  "/api/v1/health must not define query parameters.",
);
assert(
  !operationParameters("/api/v1/version").some((parameter) => parameter?.in === "query"),
  "/api/v1/version must not define query parameters.",
);

assertRequiredFields("HealthResponse", ["status", "serviceName", "checkedAt"]);
assertRequiredFields("VersionResponse", ["serviceName", "version", "buildTimestamp", "commitSha"]);
assertRequiredFields("ErrorResponse", ["type", "title", "status", "detail", "instance", "correlationId"]);
assertRequiredFields("FieldError", ["field", "message"]);

const versionPattern = new RegExp(get("components.schemas.VersionResponse.properties.version.pattern"));
assert(versionPattern.test("1.2.3-rc.1+build.5"), "VersionResponse.version must allow prerelease plus build metadata.");
assert(!versionPattern.test("1.2.3-"), "VersionResponse.version must reject incomplete prerelease metadata.");

assert(
  get("components.schemas.ErrorResponse.properties.errors.items.$ref") ===
    "#/components/schemas/FieldError",
  "ErrorResponse.errors must reference FieldError.",
);

for (const [name, example] of Object.entries(contract.components?.examples ?? {})) {
  assertExampleSafe(example.value, `Example ${name}`);
}

if (failures.length > 0) {
  console.error("OpenAPI contract check failed:");
  for (const failure of failures) {
    console.error(`- ${failure}`);
  }
  process.exit(1);
}

console.log("OpenAPI contract check passed.");
