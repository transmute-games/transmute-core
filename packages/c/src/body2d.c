#include "transmute.h"

void tm_body_init(TmBody2D *b, float x, float y, float w, float h) {
    b->x = x;
    b->y = y;
    b->w = w;
    b->h = h;
    b->vx = 0;
    b->vy = 0;
    b->on_ground = 0;
    b->gravity = 0.5f;
    b->jump_strength = -10.f;
}

void tm_body_jump(TmBody2D *b) {
    if (b->on_ground) {
        b->vy = b->jump_strength;
        b->on_ground = 0;
    }
}

static int aabb(const TmBody2D *b, const TmSolid *s) {
    return b->x < s->x + s->w && b->x + b->w > s->x && b->y < s->y + s->h && b->y + b->h > s->y;
}

void tm_body_step(TmBody2D *b, const TmSolid *solids, int count) {
    b->vy += b->gravity;
    b->x += b->vx;
    for (int i = 0; i < count; i++) {
        if (aabb(b, &solids[i])) {
            if (b->vx > 0) b->x = solids[i].x - b->w;
            else if (b->vx < 0) b->x = solids[i].x + solids[i].w;
            b->vx = 0;
        }
    }
    b->y += b->vy;
    b->on_ground = 0;
    for (int i = 0; i < count; i++) {
        if (aabb(b, &solids[i])) {
            if (b->vy > 0) {
                b->y = solids[i].y - b->h;
                b->vy = 0;
                b->on_ground = 1;
            } else if (b->vy < 0) {
                b->y = solids[i].y + solids[i].h;
                b->vy = 0;
            }
        }
    }
}
