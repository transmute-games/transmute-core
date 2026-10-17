import { test, expect } from "@playwright/test";
import AxeBuilder from "@axe-core/playwright";
import { docs, docUrl } from "../website/src/lib/docs";

test("primary flow, search recovery, and theme", async ({ page }) => {
  await page.goto("/");
  await page.getByRole("link", { name: "Build your first game" }).click();
  await expect(
    page.getByRole("heading", { name: "Getting started", exact: true }),
  ).toBeVisible();
  await page.getByRole("link", { name: "API reference", exact: true }).click();
  await expect(
    page.getByRole("heading", { name: "Transmute Core API" }),
  ).toBeVisible();
  await page.getByRole("button", { name: "Toggle theme" }).click();
  await expect(page.locator(".docs-shell")).toHaveAttribute(
    "data-theme",
    "light",
  );
  await page.getByRole("button", { name: "Search documentation" }).click();
  await page
    .getByRole("textbox", { name: "Search documentation" })
    .fill("not-a-real-guide");
  await expect(page.getByRole("dialog").getByRole("status")).toHaveText(
    "0 results",
  );
  await page.getByRole("button", { name: "Clear search" }).click();
  await page
    .getByRole("textbox", { name: "Search documentation" })
    .fill("worlds");
  await page
    .getByRole("dialog")
    .getByRole("link", { name: /Worlds and physics/ })
    .click();
  await expect(
    page.getByRole("heading", { name: "Worlds and physics", exact: true }),
  ).toBeVisible();
  await page.goBack();
  await expect(
    page.getByRole("heading", { name: "Transmute Core API" }),
  ).toBeVisible();
});

test("mobile menu traps focus, closes with Escape, and navigates", async ({
  page,
}) => {
  await page.setViewportSize({ width: 390, height: 844 });
  await page.goto("/");
  const menu = page.getByRole("button", { name: "Menu", exact: true });
  await menu.click();
  await expect(
    page.getByRole("dialog", { name: "What’s next?" }),
  ).toBeVisible();
  await page.keyboard.press("Escape");
  await expect(menu).toBeFocused();
  await menu.click();
  await page
    .getByRole("dialog", { name: "What’s next?" })
    .getByRole("link", { name: /Getting started/ })
    .click();
  await expect(
    page.getByRole("heading", { name: "Getting started", exact: true }),
  ).toBeVisible();
  await page.getByText("Browse documentation", { exact: true }).click();
  await page
    .getByRole("navigation", { name: "Documentation sections" })
    .getByRole("link", { name: "API overview" })
    .click();
  await expect(
    page.getByRole("heading", { name: "Transmute Core API" }),
  ).toBeVisible();
});

for (const route of ["/", "/docs", "/docs/api", "/docs/api/assets", "/brand"]) {
  test(`${route} has accessible, responsive content`, async ({ page }) => {
    await page.goto(route);
    for (const width of [320, 390, 768, 1440]) {
      await page.setViewportSize({ width, height: 900 });
      expect(
        await page.evaluate(
          () => document.documentElement.scrollWidth <= window.innerWidth,
        ),
      ).toBe(true);
    }
    const results = await new AxeBuilder({ page })
      .withTags(["wcag2a", "wcag2aa", "wcag21aa"])
      .analyze();
    expect(results.violations).toEqual([]);
  });
}

test("all documentation, internal links, assets, and missing routes resolve", async ({
  page,
  request,
}) => {
  const urls = new Set<string>();
  for (const route of ["/", "/brand", ...docs.map((doc) => docUrl(doc.slug))]) {
    await page.goto(route);
    expect(await page.locator("h1").count()).toBe(1);
    for (const url of await page
      .locator("a[href], img[src]")
      .evaluateAll((elements) =>
        elements.map(
          (element) =>
            element.getAttribute("href") || element.getAttribute("src") || "",
        ),
      )) {
      if (url.startsWith("/") && !url.startsWith("//"))
        urls.add(url.split("#")[0]);
    }
  }
  for (const url of urls)
    expect((await request.get(url)).status(), url).toBe(200);
  expect((await request.get("/not-a-page")).status()).toBe(404);
});

test("brand swatches copy with feedback and downloads resolve", async ({
  page,
  context,
}) => {
  await context.grantPermissions(["clipboard-read", "clipboard-write"]);
  await page.goto("/brand");
  await page.getByRole("button", { name: "Copy Sage #526447" }).click();
  await expect(page.getByRole("status")).toHaveText("Copied Sage: #526447");
  expect(await page.evaluate(() => navigator.clipboard.readText())).toBe(
    "#526447",
  );
  await page.evaluate(() => {
    Object.defineProperty(navigator.clipboard, "writeText", {
      value: () => Promise.reject(new Error("Clipboard unavailable")),
    });
  });
  await page.getByRole("button", { name: "Copy Paper #f7f7f2" }).click();
  await expect(page.getByRole("status")).toContainText(
    "Select and copy this value: #f7f7f2",
  );
  for (const link of await page.locator("a[download]").all()) {
    const response = await page.request.get((await link.getAttribute("href"))!);
    expect(response.ok()).toBeTruthy();
    expect((await response.body()).byteLength).toBeGreaterThan(100);
  }
});

for (const width of [390, 1440]) {
  test(`search filters, keyboard navigation, and accessibility at ${width}px`, async ({
    page,
  }) => {
    await page.setViewportSize({ width, height: 900 });
    await page.goto("/docs");
    const trigger = page.getByRole("button", { name: "Search documentation" });
    await trigger.click();
    const dialog = page.getByRole("dialog", { name: "Find your next step." });
    const input = dialog.getByRole("textbox", { name: "Search documentation" });
    await expect(input).toBeFocused();
    await dialog.getByRole("button", { name: "API", exact: true }).click();
    await expect(dialog.getByRole("link")).toHaveCount(5);
    await expect(
      dialog.getByRole("button", { name: "API", exact: true }),
    ).toHaveAttribute("aria-pressed", "true");
    await input.fill("collision");
    await expect(dialog.getByRole("link")).toHaveCount(1);
    await input.press("ArrowDown");
    await expect(
      dialog.getByRole("link", { name: /Worlds and physics/ }),
    ).toBeFocused();
    await page.keyboard.press("ArrowUp");
    await expect(input).toBeFocused();
    expect(
      (
        await new AxeBuilder({ page })
          .withTags(["wcag2a", "wcag2aa", "wcag21aa"])
          .analyze()
      ).violations,
    ).toEqual([]);
    expect(
      await dialog.evaluate((el) => el.scrollWidth <= el.clientWidth),
    ).toBe(true);
    await page.keyboard.press("Escape");
    await expect(trigger).toBeFocused();
    await trigger.click();
    await expect(input).toHaveValue("");
    await expect(dialog.getByRole("link")).toHaveCount(docs.length);
    await input.fill("Worlds and physics");
    await input.press("ArrowDown");
    await page.keyboard.press("Enter");
    await expect(page).toHaveURL(/\/docs\/api\/world/);
    await expect(dialog).not.toBeVisible();
  });
}
