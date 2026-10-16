# CLI distribution

The engine and project generator have separate release histories. Engine tags use `vX.Y.Z`; CLI tags use `cli-vX.Y.Z`.

## Automatic CLI releases

Every successful push CI run on `master` can create the next CLI patch tag. Auto-release first checks that the tested commit is still the branch head. It then dispatches `release-cli.yml` explicitly.

The release workflow checks out the tag, runs CLI tests, builds the fat JAR with `-PreleaseVersion`, and verifies that the executable reports that version. It uploads:

- `transmute-cli.jar`
- `transmute` and `transmute.bat`
- `LICENSE`
- `SHA256SUMS`

The JAR requires Java 17. Keep the wrapper beside the JAR in a directory on PATH. Download artifacts from [GitHub releases](https://github.com/transmute-games/transmute-core/releases).

## Retry a release

Rerun the failed auto-release run, or dispatch an existing stable tag:

```bash
gh workflow run release-cli.yml --ref master -f tag=cli-v1.0.2
```

Use the tag that actually needs recovery. The workflow validates the `cli-vX.Y.Z` format, requires the tag to exist, and replaces assets when retried. It never moves an existing tag.

## Engine releases

Engine releases remain deliberate. Update the root `build.gradle` version, test it, and tag the tested commit with `vX.Y.Z`. JitPack builds tagged engine artifacts on demand. Check the [JitPack build](https://jitpack.io/#transmute-games/transmute-core) before updating the CLI's `ENGINE_VERSION` in `CliConstants.java`.

For development, use:

```bash
./gradlew :transmute-core:publishToMavenLocal
```

See [contributing](CONTRIBUTING.md) for the complete CI and release flow.
