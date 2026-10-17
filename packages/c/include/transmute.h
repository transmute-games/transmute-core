#ifndef TRANSMUTE_H
#define TRANSMUTE_H

#include <stddef.h>
#include <stdint.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

static inline uint32_t tm_pixel(int r, int g, int b, int a) {
    return ((uint32_t)(a & 255) << 24) | ((uint32_t)(r & 255) << 16) |
           ((uint32_t)(g & 255) << 8) | (uint32_t)(b & 255);
}

typedef struct {
    int width, height;
    uint32_t *pixels;
} TmContext;

TmContext *tm_context_create(int w, int h);
void tm_context_destroy(TmContext *ctx);
void tm_context_fill_rect(TmContext *ctx, int x, int y, int w, int h, uint32_t argb);
uint32_t tm_context_get_pixel(const TmContext *ctx, int x, int y);
uint32_t tm_context_hash(const TmContext *ctx);

typedef struct TmWorld TmWorld;
typedef struct TmActor TmActor;

TmWorld *tm_world_create(int cols, int rows, int tile);
void tm_world_destroy(TmWorld *w);
void tm_world_set_clear(TmWorld *w, uint32_t c);
void tm_world_set_solid_color(TmWorld *w, uint32_t c);
void tm_world_fill_border(TmWorld *w);
void tm_world_set_tile(TmWorld *w, int tx, int ty, int tile);
int tm_world_is_solid(const TmWorld *w, int tx, int ty);
int tm_world_blocks(const TmWorld *w, int x, int y, int ww, int hh);
TmActor *tm_world_add_actor(TmWorld *w, int x, int y, int ww, int hh, uint32_t color, const char *name);
TmActor *tm_world_find_actor(TmWorld *w, const char *name);
int tm_actor_try_move(TmActor *a, int dx, int dy);
void tm_world_render(TmWorld *w, TmContext *ctx);
int tm_world_cols(const TmWorld *w);
int tm_world_rows(const TmWorld *w);

typedef struct {
    float x, y, w, h;
    float vx, vy;
    int on_ground;
    float gravity;
    float jump_strength;
} TmBody2D;

typedef struct {
    float x, y, w, h;
} TmSolid;

void tm_body_init(TmBody2D *b, float x, float y, float w, float h);
void tm_body_jump(TmBody2D *b);
void tm_body_step(TmBody2D *b, const TmSolid *solids, int count);

typedef struct TmGameSpec TmGameSpec;
TmGameSpec *tm_gamespec_load(const char *path);
void tm_gamespec_destroy(TmGameSpec *s);
const char *tm_gamespec_title(const TmGameSpec *s);
uint32_t tm_gamespec_clear(const TmGameSpec *s);
int tm_gamespec_width(const TmGameSpec *s);
int tm_gamespec_height(const TmGameSpec *s);
TmWorld *tm_gamespec_create_world(TmGameSpec *s);

int tm_assert_pixel(const TmContext *ctx, int x, int y, uint32_t expected);

#ifdef __cplusplus
}
#endif

#endif
