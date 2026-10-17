#include "transmute.h"
#include <stdlib.h>
#include <string.h>

TmContext *tm_context_create(int w, int h) {
    TmContext *ctx = calloc(1, sizeof(TmContext));
    if (!ctx) return NULL;
    ctx->width = w;
    ctx->height = h;
    ctx->pixels = calloc((size_t)w * (size_t)h, sizeof(uint32_t));
    if (!ctx->pixels) {
        free(ctx);
        return NULL;
    }
    return ctx;
}

void tm_context_destroy(TmContext *ctx) {
    if (!ctx) return;
    free(ctx->pixels);
    free(ctx);
}

void tm_context_fill_rect(TmContext *ctx, int x, int y, int w, int h, uint32_t argb) {
    if (!ctx) return;
    for (int py = y; py < y + h; py++) {
        if (py < 0 || py >= ctx->height) continue;
        for (int px = x; px < x + w; px++) {
            if (px < 0 || px >= ctx->width) continue;
            ctx->pixels[py * ctx->width + px] = argb;
        }
    }
}

uint32_t tm_context_get_pixel(const TmContext *ctx, int x, int y) {
    return ctx->pixels[y * ctx->width + x];
}

uint32_t tm_context_hash(const TmContext *ctx) {
    uint32_t h = 0x811c9dc5u;
    size_t n = (size_t)ctx->width * (size_t)ctx->height;
    for (size_t i = 0; i < n; i++) {
        h ^= ctx->pixels[i];
        h *= 0x01000193u;
    }
    return h;
}

int tm_assert_pixel(const TmContext *ctx, int x, int y, uint32_t expected) {
    uint32_t actual = tm_context_get_pixel(ctx, x, y);
    return actual == expected ? 0 : -1;
}
