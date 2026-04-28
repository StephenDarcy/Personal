import { readFile } from "node:fs/promises";
import { join } from "node:path";

const htmlPath = join(process.cwd(), "out", "index.html");

function fail(message) {
  throw new Error(`Accessibility check failed: ${message}`);
}

function collect(pattern, content) {
  return [...content.matchAll(pattern)];
}

function getAttribute(tag, name) {
  const match = tag.match(new RegExp(`\\s${name}=["']([^"']*)["']`, "i"));
  return match?.[1] ?? null;
}

function normalizeText(value) {
  return value.replace(/\s+/g, " ").trim();
}

function textOutsideTags(fragment) {
  let text = "";
  let inTag = false;

  for (const character of fragment) {
    if (character === "<") {
      inTag = true;
      continue;
    }

    if (inTag) {
      if (character === ">") {
        inTag = false;
      }
      continue;
    }

    text += character;
  }

  return normalizeText(text);
}

function childImageAltText(fragment) {
  const altValues = collect(/<img\b[^>]*>/gi, fragment)
    .map((match) => getAttribute(match[0], "alt"))
    .filter((alt) => alt && alt.trim().length > 0);

  return normalizeText(altValues.join(" "));
}

function linkAccessibleName(fragment) {
  return (
    getAttribute(fragment, "aria-label") ||
    textOutsideTags(fragment) ||
    childImageAltText(fragment) ||
    getAttribute(fragment, "title") ||
    ""
  );
}

const html = await readFile(htmlPath, "utf8").catch((error) => {
  if (error.code === "ENOENT") {
    fail("missing out/index.html; run npm run build before npm run accessibility");
  }
  throw error;
});

if (!/<html\b[^>]*\slang=["'][a-z-]+["']/i.test(html)) {
  fail("the root html element must declare a language");
}

if (!/<title>[^<]+<\/title>/i.test(html)) {
  fail("the page must include a non-empty title");
}

if (!/<main\b[^>]*\sid=["']main-content["']/i.test(html)) {
  fail("the page must expose a main-content landmark target");
}

if (!/<a\b[^>]*\shref=["']#main-content["'][^>]*>[\s\S]*?Skip to content[\s\S]*?<\/a>/i.test(html)) {
  fail("the page must include a skip link to #main-content");
}

const ids = collect(/\sid=["']([^"']+)["']/gi, html).map((match) => match[1]);
const duplicates = ids.filter((id, index) => ids.indexOf(id) !== index);
if (duplicates.length > 0) {
  fail(`duplicate id values found: ${[...new Set(duplicates)].join(", ")}`);
}

for (const match of collect(/\saria-labelledby=["']([^"']+)["']/gi, html)) {
  const references = match[1].split(/\s+/);
  for (const reference of references) {
    if (!ids.includes(reference)) {
      fail(`aria-labelledby references missing id: ${reference}`);
    }
  }
}

const headings = collect(/<h([1-6])\b[^>]*>([\s\S]*?)<\/h\1>/gi, html).map((match) => ({
  level: Number(match[1]),
  text: textOutsideTags(match[2]),
}));

if (headings.length === 0 || headings[0].level !== 1) {
  fail("heading hierarchy must start with h1");
}

if (headings.filter((heading) => heading.level === 1).length !== 1) {
  fail("the page must include exactly one h1");
}

for (let index = 1; index < headings.length; index += 1) {
  if (headings[index].level - headings[index - 1].level > 1) {
    fail(`heading level jumps from h${headings[index - 1].level} to h${headings[index].level}`);
  }
}

for (const match of collect(/<a\b[^>]*>[\s\S]*?<\/a>/gi, html)) {
  const tag = match[0];
  const accessibleName = linkAccessibleName(tag);
  if (!accessibleName) {
    fail(`link is missing accessible text: ${tag.slice(0, 80)}`);
  }

  if (getAttribute(tag, "target") === "_blank") {
    const rel = getAttribute(tag, "rel") ?? "";
    if (!/\bnoreferrer\b/.test(rel) && !/\bnoopener\b/.test(rel)) {
      fail(`external link target="_blank" must include noreferrer or noopener: ${tag.slice(0, 80)}`);
    }
  }
}

for (const match of collect(/<img\b[^>]*>/gi, html)) {
  if (getAttribute(match[0], "alt") === null) {
    fail(`image is missing an alt attribute: ${match[0].slice(0, 80)}`);
  }
}

console.log("Static accessibility checks passed.");
