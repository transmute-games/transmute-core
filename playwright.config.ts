import { defineConfig } from "@playwright/test";
export default defineConfig({
  testDir: "./tests",
  testMatch: "**/*.spec.ts",
  fullyParallel: true,
  retries: process.env.CI ? 1 : 0,
  reporter: [["list"], ["html", { open: "never" }]],
  use: { baseURL: "http://localhost:3005", trace: "retain-on-failure" },
  webServer: {
    command: "bun scripts/serve.ts",
    port: 3005,
    reuseExistingServer: !process.env.CI,
  },
});
