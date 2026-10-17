#include "transmute.h"
#include <stdlib.h>
#include <string.h>

#define TM_EMPTY 0
#define TM_SOLID 1
#define TM_MAX_ACTORS 64

struct TmActor {
    int x, y, w, h;
    uint32_t color;
    char name[64];
    TmWorld *world;
};

struct TmWorld {
    int cols, rows, tile;
    int *tiles;
    uint32_t clear_color;
    uint32_t solid_color;
    TmActor actors[TM_MAX_ACTORS];
    int actor_count;
};

TmWorld *tm_world_create(int cols, int rows, int tile) {
    TmWorld *w = calloc(1, sizeof(TmWorld));
    if (!w) return NULL;
    w->cols = cols;
    w->rows = rows;
    w->tile = tile;
    w->tiles = calloc((size_t)cols * (size_t)rows, sizeof(int));
    w->clear_color = tm_pixel(20, 20, 30, 255);
    w->solid_color = tm_pixel(60, 60, 80, 255);
    if (!w->tiles) {
        free(w);
        return NULL;
    }
    return w;
}

void tm_world_destroy(TmWorld *w) {
    if (!w) return;
    free(w->tiles);
    free(w);
}

void tm_world_set_clear(TmWorld *w, uint32_t c) { w->clear_color = c; }
void tm_world_set_solid_color(TmWorld *w, uint32_t c) { w->solid_color = c; }

void tm_world_fill_border(TmWorld *w) {
    for (int x = 0; x < w->cols; x++) {
        tm_world_set_tile(w, x, 0, TM_SOLID);
        tm_world_set_tile(w, x, w->rows - 1, TM_SOLID);
    }
    for (int y = 0; y < w->rows; y++) {
        tm_world_set_tile(w, 0, y, TM_SOLID);
        tm_world_set_tile(w, w->cols - 1, y, TM_SOLID);
    }
}

void tm_world_set_tile(TmWorld *w, int tx, int ty, int tile) {
    if (tx < 0 || ty < 0 || tx >= w->cols || ty >= w->rows) return;
    w->tiles[tx + ty * w->cols] = tile;
}

int tm_world_is_solid(const TmWorld *w, int tx, int ty) {
    if (tx < 0 || ty < 0 || tx >= w->cols || ty >= w->rows) return 1;
    return w->tiles[tx + ty * w->cols] == TM_SOLID;
}

int tm_world_blocks(const TmWorld *w, int x, int y, int ww, int hh) {
    int min_tx = x / w->tile;
    int max_tx = (x + ww - 1) / w->tile;
    int min_ty = y / w->tile;
    int max_ty = (y + hh - 1) / w->tile;
    for (int tx = min_tx; tx <= max_tx; tx++)
        for (int ty = min_ty; ty <= max_ty; ty++)
            if (tm_world_is_solid(w, tx, ty)) return 1;
    return 0;
}

TmActor *tm_world_add_actor(TmWorld *w, int x, int y, int ww, int hh, uint32_t color, const char *name) {
    if (w->actor_count >= TM_MAX_ACTORS) return NULL;
    TmActor *a = &w->actors[w->actor_count++];
    a->x = x;
    a->y = y;
    a->w = ww;
    a->h = hh;
    a->color = color;
    a->world = w;
    if (name) {
        strncpy(a->name, name, sizeof(a->name) - 1);
        a->name[sizeof(a->name) - 1] = 0;
    } else {
        a->name[0] = 0;
    }
    return a;
}

TmActor *tm_world_find_actor(TmWorld *w, const char *name) {
    for (int i = 0; i < w->actor_count; i++)
        if (strcmp(w->actors[i].name, name) == 0) return &w->actors[i];
    return NULL;
}

int tm_actor_try_move(TmActor *a, int dx, int dy) {
    if (!a->world) {
        a->x += dx;
        a->y += dy;
        return 1;
    }
    int nx = a->x + dx, ny = a->y + dy;
    if (tm_world_blocks(a->world, nx, ny, a->w, a->h)) return 0;
    a->x = nx;
    a->y = ny;
    return 1;
}

void tm_world_render(TmWorld *w, TmContext *ctx) {
    tm_context_fill_rect(ctx, 0, 0, ctx->width, ctx->height, w->clear_color);
    for (int ty = 0; ty < w->rows; ty++)
        for (int tx = 0; tx < w->cols; tx++)
            if (tm_world_is_solid(w, tx, ty))
                tm_context_fill_rect(ctx, tx * w->tile, ty * w->tile, w->tile, w->tile, w->solid_color);
    for (int i = 0; i < w->actor_count; i++) {
        TmActor *a = &w->actors[i];
        tm_context_fill_rect(ctx, a->x, a->y, a->w, a->h, a->color);
    }
}

int tm_world_cols(const TmWorld *w) { return w->cols; }
int tm_world_rows(const TmWorld *w) { return w->rows; }
