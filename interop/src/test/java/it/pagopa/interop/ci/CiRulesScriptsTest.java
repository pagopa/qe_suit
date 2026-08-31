package it.pagopa.interop.ci;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CiRulesScriptsTest {

    private static final Path BRANCH_SCRIPT = Path.of(".github/scripts/validate-branch-naming.sh").toAbsolutePath().normalize();
    private static final Path COMMIT_SCRIPT = Path.of(".github/scripts/validate-commit-messages.sh").toAbsolutePath().normalize();

    @Test
    void branch_script_accepts_standard_and_long_lived_branches() throws Exception {
        assertScriptSuccess(BRANCH_SCRIPT, null, "feature/interop-login");
        assertScriptSuccess(BRANCH_SCRIPT, null, "bugfix/fix-token");
        assertScriptSuccess(BRANCH_SCRIPT, null, "hotfix/urgent-fix");
        assertScriptSuccess(BRANCH_SCRIPT, null, "chore/update-deps");
        assertScriptSuccess(BRANCH_SCRIPT, null, "main");
        assertScriptSuccess(BRANCH_SCRIPT, null, "dependabot/npm_and_yarn/lodash");
    }

    @Test
    void branch_script_rejects_non_conforming_branches() throws Exception {
        var result = runScript(BRANCH_SCRIPT, null, "topic/bad-branch");
        assertEquals(1, result.exitCode(), result.stderr());
        assertTrue(result.stderr().contains("Branch name non conforme"));
    }

    @Test
    void commit_script_accepts_conventional_messages(@TempDir Path tempDir) throws Exception {
        initGitRepo(tempDir);
        writeCommit(tempDir, "feat: [PROJ-123] initial commit", "first\n");
        writeCommit(tempDir, "fix(auth): [AUTH-88] improve token handling", "second\n");

        assertScriptSuccess(COMMIT_SCRIPT, tempDir, "HEAD~1..HEAD");
    }

    @Test
    void commit_script_rejects_non_conventional_messages(@TempDir Path tempDir) throws Exception {
        initGitRepo(tempDir);
        writeCommit(tempDir, "feat: [PROJ-123] initial commit", "first\n");
        writeCommit(tempDir, "bad message", "second\n");

        var result = runScript(COMMIT_SCRIPT, tempDir, "HEAD~1..HEAD");
        assertEquals(1, result.exitCode(), result.stderr());
        assertTrue(result.stderr().contains("Commit message non conforme"));
    }

    @Test
    void commit_script_rejects_messages_without_jira_ticket(@TempDir Path tempDir) throws Exception {
        initGitRepo(tempDir);
        writeCommit(tempDir, "feat: [PROJ-123] initial commit", "first\n");
        writeCommit(tempDir, "fix(auth): improve token handling", "second\n");

        var result = runScript(COMMIT_SCRIPT, tempDir, "HEAD~1..HEAD");
        assertEquals(1, result.exitCode(), result.stderr());
        assertTrue(result.stderr().contains("Commit message non conforme"));
    }

    @Test
    void commit_script_accepts_various_ticket_formats(@TempDir Path tempDir) throws Exception {
        initGitRepo(tempDir);
        writeCommit(tempDir, "feat: [PROJ-123] new feature", "first\n");
        writeCommit(tempDir, "fix(api): [API-456] fix endpoint", "second\n");
        writeCommit(tempDir, "chore(deps): [TECH-789] update dependencies", "third\n");

        assertScriptSuccess(COMMIT_SCRIPT, tempDir, "HEAD~2..HEAD");
    }

    private static void assertScriptSuccess(Path script, Path workingDir, String... args) throws Exception {
        var result = runScript(script, workingDir, args);
        assertEquals(0, result.exitCode(), result.stderr());
    }

    private static ScriptResult runScript(Path script, Path workingDir, String... args) throws Exception {
        ProcessBuilder builder = new ProcessBuilder();
        List<String> command = new java.util.ArrayList<>();
        command.add("bash");
        command.add(script.toString());
        command.addAll(List.of(args));
        builder.command(command);
        if (workingDir != null) {
            builder.directory(workingDir.toFile());
        }

        Process process = builder.start();
        String stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        return new ScriptResult(exitCode, stdout, stderr);
    }

    private static void initGitRepo(Path repoDir) throws Exception {
        git(repoDir, "init");
        git(repoDir, "config", "user.email", "ci@example.com");
        git(repoDir, "config", "user.name", "CI Bot");
    }

    private static void writeCommit(Path repoDir, String message, String content) throws Exception {
        String fileName = "file.txt";
        Files.writeString(repoDir.resolve(fileName), content, StandardCharsets.UTF_8);
        git(repoDir, "add", fileName);
        git(repoDir, "commit", "-m", message);
    }

    private static void git(Path repoDir, String... args) throws Exception {
        ProcessBuilder builder = new ProcessBuilder();
        List<String> command = new java.util.ArrayList<>();
        command.add("git");
        command.addAll(List.of(args));
        builder.command(command);
        builder.directory(repoDir.toFile());
        Process process = builder.start();
        String stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IllegalStateException("Git command failed: " + command + "\nSTDOUT: " + stdout + "\nSTDERR: " + stderr);
        }
    }

    private record ScriptResult(int exitCode, String stdout, String stderr) {
    }
}


