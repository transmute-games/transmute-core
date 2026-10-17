#include "transmute.h"
#include <stdio.h>
#include <stdlib.h>

int main(void) {
    TmContext *ctx = tm_context_create(320, 180);
    uint32_t clear = tm_pixel(32, 32, 64, 255);
    tm_context_fill_rect(ctx, 0, 0, 320, 180, clear);
    tm_context_fill_rect(ctx, 20, 20, 40, 40, tm_pixel(220, 180, 60, 255));
    if (tm_assert_pixel(ctx, 0, 0, clear) != 0) {
        fprintf(stderr, "pixel assert failed\n");
        return 1;
    }
    printf("hello headless ok hash=0x%x\n", tm_context_hash(ctx));
    tm_context_destroy(ctx);
    return 0;
}
