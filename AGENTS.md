# Repository Guidelines

## Project Structure & Module Organization

This Vaadin + Spring Boot app is a single Maven module. Server code lives under `src/main/java/com/example/vaadin`, grouped by domain packages (`model`, `services`, `session`, `views`). Shared configuration and resources belong in `src/main/resources`; keep environment overrides in `application.properties` or profile-specific copies. Frontend assets stay in `frontend`; anything under `frontend/generated` is managed by Vaadin and should not be edited manually. Migration notes and architectural context are documented in `README.md` and `SWING_TO_VAADIN_MAPPING.md`—refresh them when your change affects the guidance. Build products in `target/` should always be ignored.

## Build, Test, and Development Commands

All workflows run through Maven:

```bash
mvn clean install            # compile, run tests, package the jar
mvn spring-boot:run          # launch the dev server on http://localhost:8080
mvn clean package            # produce target/vaadin-migration-example-1.0-SNAPSHOT.jar
mvn vaadin:prepare-frontend  # regenerate frontend bundles after dependency changes
```

Use `mvn -Pproduction clean package` when verifying the production build profile, and add `-DskipTests=false` to ensure the default lifecycle executes tests.

## Coding Style & Naming Conventions

Write Java 17 with four-space indentation and K&R braces. Keep package names lowercase and continue the `com.example.vaadin` hierarchy. Views should extend Vaadin components, be annotated with `@Route`, and use the `*View` suffix; services keep verbs in method names and nouns in class names. Leverage Lombok only where it clarifies boilerplate; otherwise prefer explicit constructors. Avoid modifying generated frontend files—custom TypeScript modules belong beside `frontend/index.html`. Document non-obvious behavior with concise Javadoc.

## Testing Guidelines

Add unit or integration tests under `src/test/java` mirroring the production package path. Prefer JUnit Jupiter with Spring Boot’s `@SpringBootTest` or `@DataJpaTest` where relevant, and use Vaadin TestBench or component unit tests for UI flows. Name test classes `*Test` and individual methods `shouldDoThing_whenCondition`. Run `mvn test` locally before pushing, and aim to cover new service logic and critical UI actions with at least one automated check.

## Commit & Pull Request Guidelines

Use imperative, Conventional Commit–style messages (e.g., `feat: add pdf export toolbar`) and keep summaries under 72 characters. Each pull request should describe motivation, implementation notes, and any migration doc updates, plus screenshots or GIFs for visible UI changes. Link tracking issues, list manual verification steps (`mvn spring-boot:run`, scenario walkthroughs), and confirm that `README.md` or `SWING_TO_VAADIN_MAPPING.md` remains accurate.

# 依頼事項

ユーザーとのやりとりの際には日本語を使用してください。
