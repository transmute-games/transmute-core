export const docs = [
  {
    slug: "",
    file: "GETTING_STARTED.md",
    title: "Getting started",
    description: "Install the engine and build your first game.",
    group: "Learn",
  },
  {
    slug: "architecture",
    file: "ARCHITECTURE.md",
    title: "Architecture",
    description: "Understand the engine, managers, and game loop.",
    group: "Learn",
  },
  {
    slug: "examples",
    file: "EXAMPLES.md",
    title: "Examples",
    description: "Start with a working platformer, RPG, or hello world.",
    group: "Learn",
  },
  {
    slug: "testing",
    file: "TESTING.md",
    title: "Headless testing",
    description: "Verify gameplay without opening a window.",
    group: "Learn",
  },
  {
    slug: "api",
    file: "api/README.md",
    title: "API overview",
    description: "Explore the building blocks of the engine.",
    group: "Engine API",
  },
  {
    slug: "api/game",
    file: "api/game.md",
    title: "Game lifecycle",
    description: "Configure, initialize, update, and render your game.",
    group: "Engine API",
  },
  {
    slug: "api/assets",
    file: "api/assets.md",
    title: "Assets and manifests",
    description: "Load sprites, fonts, and audio with GameSpec.",
    group: "Engine API",
  },
  {
    slug: "api/world",
    file: "api/world.md",
    title: "Worlds and physics",
    description: "Build tile worlds, movement, and collision.",
    group: "Engine API",
  },
  {
    slug: "api/testing",
    file: "api/testing.md",
    title: "Headless harness",
    description: "Simulate input and assert frames and audio.",
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
    description: `A hands-on walkthrough of ${name.slice(3).replaceAll("-", " ")}.`,
  })),
  {
    slug: "authoring",
    file: "AUTHORING.md",
    title: "Authoring games",
    description: "Turn a prompt and assets into a verified game.",
    group: "Resources",
  },
  {
    slug: "cookbook",
    file: "COOKBOOK.md",
    title: "Cookbook",
    description: "Find practical recipes for common game features.",
    group: "Resources",
  },
  {
    slug: "contributing",
    file: "CONTRIBUTING.md",
    title: "Contributing",
    description: "Set up Flox and run the project checks.",
    group: "Resources",
  },
  {
    slug: "website",
    file: "WEBSITE.md",
    title: "Website development",
    description: "Develop, preview, and deploy this website.",
    group: "Resources",
  },
  {
    slug: "cli",
    file: "CLI_DISTRIBUTION.md",
    title: "CLI distribution",
    description: "Build and distribute the game scaffolding CLI.",
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
