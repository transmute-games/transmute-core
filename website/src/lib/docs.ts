export const docs = [
  {
    slug: "",
    file: "GETTING_STARTED.md",
    title: "Getting started",
    group: "Learn",
  },
  {
    slug: "architecture",
    file: "ARCHITECTURE.md",
    title: "Architecture",
    group: "Learn",
  },
  { slug: "examples", file: "EXAMPLES.md", title: "Examples", group: "Learn" },
  {
    slug: "testing",
    file: "TESTING.md",
    title: "Headless testing",
    group: "Learn",
  },
  {
    slug: "api",
    file: "api/README.md",
    title: "API overview",
    group: "Engine API",
  },
  {
    slug: "api/game",
    file: "api/game.md",
    title: "Game lifecycle",
    group: "Engine API",
  },
  {
    slug: "api/assets",
    file: "api/assets.md",
    title: "Assets and manifests",
    group: "Engine API",
  },
  {
    slug: "api/world",
    file: "api/world.md",
    title: "Worlds and physics",
    group: "Engine API",
  },
  {
    slug: "api/testing",
    file: "api/testing.md",
    title: "Headless harness",
    group: "Engine API",
  },
  ...[
    "01-hello-world",
    "02-sprites-and-animation",
    "03-input-and-movement",
    "04-collision-detection",
    "05-state-management",
    "06-audio-system",
    "07-level-design",
  ].map((name) => ({
    slug: `tutorials/${name}`,
    file: `tutorials/${name}.md`,
    title: name.slice(3).replaceAll("-", " "),
    group: "Tutorials",
  })),
  {
    slug: "authoring",
    file: "AUTHORING.md",
    title: "Authoring games",
    group: "Resources",
  },
  {
    slug: "cookbook",
    file: "COOKBOOK.md",
    title: "Cookbook",
    group: "Resources",
  },
  {
    slug: "contributing",
    file: "CONTRIBUTING.md",
    title: "Contributing",
    group: "Resources",
  },
  {
    slug: "website",
    file: "WEBSITE.md",
    title: "Website development",
    group: "Resources",
  },
  {
    slug: "cli",
    file: "CLI_DISTRIBUTION.md",
    title: "CLI distribution",
    group: "Resources",
  },
];
export const repo = "https://github.com/transmute-games/transmute-core";
export const docUrl = (slug: string) => `/docs${slug ? `/${slug}` : ""}`;
export const headingId = (text: string) =>
  text
    .toLowerCase()
    .replace(/[^\p{L}\p{N}\s-]/gu, "")
    .replace(/\s/g, "-");
