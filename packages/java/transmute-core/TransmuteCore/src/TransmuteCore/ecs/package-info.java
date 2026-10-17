/**
 * Legacy entity stack (inheritance OO despite the package name — not a true ECS).
 * <p>
 * <b>For new games prefer:</b>
 * <ul>
 *   <li>{@link TransmuteCore.world.World} + {@link TransmuteCore.world.World.Actor} (top-down)</li>
 *   <li>{@link TransmuteCore.physics.Body2D} (platformers)</li>
 * </ul>
 * {@link TransmuteCore.ecs.Object} / {@link TransmuteCore.ecs.types.Mob} are {@code @Deprecated}.
 * {@link TransmuteCore.ecs.ObjectManager} remains the authoring seam installed by
 * {@code Manager.bootstrapDefaults()} and is not deprecated.
 * The package is not renamed (binary compatibility).
 */
package TransmuteCore.ecs;
