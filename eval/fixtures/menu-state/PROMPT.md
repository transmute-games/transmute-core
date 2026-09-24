# Agent eval: menu → play state

Use `Manager.bootstrapDefaults()` and `StateManager`. Read `state.initial` from
GameSpec (must be `menu`). Push a menu state; on ENTER push play. Headless test
presses ENTER and asserts play is on top (`peek().getName()` equals `play`).
