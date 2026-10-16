import type { MetadataRoute } from "next";
import { docs, docUrl } from "@/lib/docs";
export const dynamic = "force-static";
export default function sitemap(): MetadataRoute.Sitemap {
  const origin = process.env.SITE_URL;
  return origin
    ? ["/", "/brand", ...docs.map((doc) => docUrl(doc.slug))].map((route) => ({
        url: `${origin}${route}`,
      }))
    : [];
}
