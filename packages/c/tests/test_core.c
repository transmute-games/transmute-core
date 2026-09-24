#include "transmute.h"
#include <stdio.h>
#include <stdlib.h>

static int fails;

#define CHECK(cond) do { if (!(cond)) { fprintf(stderr, "FAIL %s:%d\n", __FILE__, __LINE__); fails++; } } while (0)

int main(void) {
    TmWorld *w = tm_world_create(8, 6, 16);
    tm_world_fill_border(w);
    CHECK(tm_world_is_solid(w, 0, 0));
    TmActor *a = tm_world_add_actor(w, 16, 16, 16, 16, tm_pixel(255, 0, 0, 255), "hero");
    CHECK(a != NULL);
    CHECK(tm_world_find_actor(w, "hero") == a);
    tm_world_set_tile(w, 2, 1, 1);
    CHECK(!tm_actor_try_move(a, 16, 0));

    TmBody2D body;
    tm_body_init(&body, 10, 0, 16, 16);
    TmSolid plat = {0, 100, 200, 20};
    for (int i = 0; i < 50; i++) tm_body_step(&body, &plat, 1);
    CHECK(body.on_ground);
    CHECK(body.y > 83.f && body.y < 85.f);

    TmContext *ctx = tm_context_create(32, 32);
    uint32_t bg = tm_pixel(10, 20, 30, 255);
    tm_context_fill_rect(ctx, 0, 0, 32, 32, bg);
    CHECK(tm_assert_pixel(ctx, 0, 0, bg) == 0);

    tm_context_destroy(ctx);
    tm_world_destroy(w);
    if (fails) {
        fprintf(stderr, "%d failures\n", fails);
        return 1;
    }
    printf("test_core ok\n");
    return 0;
}
