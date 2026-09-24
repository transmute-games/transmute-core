#include "transmute.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define TM_MAX_KEYS 128

struct TmGameSpec {
    char title[128];
    int width, height, scale;
    uint32_t clear;
    char keys[TM_MAX_KEYS][64];
    char vals[TM_MAX_KEYS][256];
    int count;
};

static const char *get(const TmGameSpec *s, const char *key, const char *def) {
    for (int i = 0; i < s->count; i++)
        if (strcmp(s->keys[i], key) == 0) return s->vals[i];
    return def;
}

TmGameSpec *tm_gamespec_load(const char *path) {
    FILE *f = fopen(path, "r");
    if (!f) return NULL;
    TmGameSpec *s = calloc(1, sizeof(TmGameSpec));
    if (!s) {
        fclose(f);
        return NULL;
    }
    strcpy(s->title, "Game");
    s->width = 320;
    s->height = 180;
    s->scale = 3;
    s->clear = tm_pixel(32, 32, 64, 255);
    char line[512];
    while (fgets(line, sizeof(line), f)) {
        char *p = line;
        while (*p == ' ' || *p == '\t') p++;
        if (*p == '#' || *p == '\n' || *p == '\0') continue;
        char *eq = strchr(p, '=');
        if (!eq) continue;
        *eq = 0;
        char *key = p;
        char *val = eq + 1;
        while (*val == ' ') val++;
        char *nl = strpbrk(val, "\r\n");
        if (nl) *nl = 0;
        char *ke = key + strlen(key) - 1;
        while (ke > key && (*ke == ' ' || *ke == '\t')) *ke-- = 0;
        if (s->count < TM_MAX_KEYS) {
            strncpy(s->keys[s->count], key, 63);
            strncpy(s->vals[s->count], val, 255);
            s->count++;
        }
    }
    fclose(f);
    strncpy(s->title, get(s, "title", "Game"), sizeof(s->title) - 1);
    s->width = atoi(get(s, "width", "320"));
    s->height = atoi(get(s, "height", "180"));
    s->scale = atoi(get(s, "scale", "3"));
    int r = atoi(get(s, "clear.r", "32"));
    int g = atoi(get(s, "clear.g", "32"));
    int b = atoi(get(s, "clear.b", "64"));
    int a = atoi(get(s, "clear.a", "255"));
    s->clear = tm_pixel(r, g, b, a);
    return s;
}

void tm_gamespec_destroy(TmGameSpec *s) { free(s); }
const char *tm_gamespec_title(const TmGameSpec *s) { return s->title; }
uint32_t tm_gamespec_clear(const TmGameSpec *s) { return s->clear; }
int tm_gamespec_width(const TmGameSpec *s) { return s->width; }
int tm_gamespec_height(const TmGameSpec *s) { return s->height; }

TmWorld *tm_gamespec_create_world(TmGameSpec *s) {
    const char *cols_s = get(s, "world.cols", NULL);
    const char *rows_s = get(s, "world.rows", NULL);
    if (!cols_s || !rows_s) return NULL;
    int cols = atoi(cols_s), rows = atoi(rows_s);
    int tile = atoi(get(s, "world.tile", "16"));
    TmWorld *w = tm_world_create(cols, rows, tile);
    tm_world_set_clear(w, s->clear);
    if (strcmp(get(s, "world.border", "false"), "true") == 0) tm_world_fill_border(w);
    const char *solids = get(s, "world.solid", "");
    if (solids[0]) {
        char buf[256];
        strncpy(buf, solids, sizeof(buf) - 1);
        char *save = NULL;
        for (char *tok = strtok_r(buf, ";", &save); tok; tok = strtok_r(NULL, ";", &save)) {
            int tx = 0, ty = 0;
            if (sscanf(tok, "%d,%d", &tx, &ty) == 2) tm_world_set_tile(w, tx, ty, 1);
        }
    }
    /* spawns: spawn.name=tx,ty */
    for (int i = 0; i < s->count; i++) {
        if (strncmp(s->keys[i], "spawn.", 6) != 0) continue;
        if (strchr(s->keys[i] + 6, '.')) continue; /* property key */
        const char *name = s->keys[i] + 6;
        int tx = 0, ty = 0;
        if (sscanf(s->vals[i], "%d,%d", &tx, &ty) != 2) continue;
        tm_world_add_actor(w, tx * tile, ty * tile, tile, tile, tm_pixel(100, 150, 255, 255), name);
    }
    return w;
}
