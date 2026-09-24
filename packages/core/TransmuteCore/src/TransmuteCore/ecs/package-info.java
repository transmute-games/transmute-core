/**
 * Legacy entity stack (inheritance OO despite the package name — not a true ECS).
 * <p>
 * <b>For new games prefer:</b>
 * <ul>
 *   <li>{@link TransmuteCore.world.World} + {@link TransmuteCore.world.World.Actor} (top-down)</li>
 *   <li>{@link TransmuteCore.physics.Body2D} (platformers)</li>
 * </ul>
 * Types here remain supported for existing projects.
 */
package TransmuteCore.ecs;
