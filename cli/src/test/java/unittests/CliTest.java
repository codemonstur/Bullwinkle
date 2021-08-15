package unittests;

import static app.Constants.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import app.Main;
import unittests.util.CachingPrintStream;
import org.junit.Before;
import org.junit.Test;

public class CliTest {

	private CachingPrintStream stdout;
	private CachingPrintStream stderr;

	@Before
	public void setup() {
		stdout = new CachingPrintStream();
		stderr = new CachingPrintStream();
	}

	@Test
	public void testCli1() {
		final String[] args = {};
		final int exitCode = Main.doMain(args, null, stdout, stderr);
		assertEquals(ERROR_ARGUMENTS, exitCode);
	}
	
	@Test
	public void testCliInvalidArgumentFormat() {
		final String[] args = {"-foo"};
		final int exitCode = Main.doMain(args, null, stdout, stderr);
		assertEquals(ERROR_ARGUMENTS, exitCode);
	}

	@Test
	public void testCliUnknownArgument() {
		final String[] args = {"--foo"};
		final int exitCode = Main.doMain(args, null, stdout, stderr);
		assertEquals(ERROR_ARGUMENTS, exitCode);
	}

	@Test
	public void testCli3() {
		final String[] args = {"-g", "src/test/resources/grammars/tests/0.bnf"};
		final var stdin = new ByteArrayInputStream("".getBytes());
		final int exitCode = Main.doMain(args, stdin, stdout, stderr);
		assertEquals(ERROR_PARSE, exitCode);
	}
	
	@Test
	public void testCliXml() {
		final String[] args = {"--format", "xml", "-g", "src/test/resources/grammars/tests/0.bnf"};
		final var stdin = new ByteArrayInputStream("SELECT foo FROM bar".getBytes());
		final int exitCode = Main.doMain(args, stdin, stdout, stderr);

		assertEquals(0, exitCode);
		final String output = stdout.toString();
		assertFalse(output.isEmpty());
		assertTrue(output.contains("<token>"));
	}
	
	@Test
	public void testCliTxt() {
		final String[] args = {"--format", "txt", "-g", "src/test/resources/grammars/tests/0.bnf"};
		final var stdin = new ByteArrayInputStream("SELECT foo FROM bar".getBytes());

		final int exitCode = Main.doMain(args, stdin, stdout, stderr);

		assertEquals(0, exitCode);
		final String output = stdout.toString();
		assertFalse(output.isEmpty());
		assertTrue(output.contains("SELECT"));
	}
	
	@Test
	public void testCliDot() {
		final String[] args = {"--format", "dot", "-g", "src/test/resources/grammars/tests/0.bnf"};
		final var stdin = new ByteArrayInputStream("SELECT foo FROM bar".getBytes());

		final int exitCode = Main.doMain(args, stdin, stdout, stderr);

		assertEquals(0, exitCode);
		final String output = stdout.toString();
		assertFalse(output.isEmpty());
		assertTrue(output.contains("digraph"));
	}
	
	@Test
	public void testCliFoo() {
		final String[] args = {"--format", "foo", "-g", "src/test/resources/grammars/tests/0.bnf"};
		final var stdin = new ByteArrayInputStream("SELECT foo FROM bar".getBytes());
		final int exitCode = Main.doMain(args, stdin, stdout, stderr);
		assertEquals(ERROR_UNKNOWN_FORMAT, exitCode);
	}
	
	@Test
	public void testCliFromFile() {
		final String[] args = {"--format", "dot", "-g", "src/test/resources/grammars/tests/0.bnf", "-i", "src/test/resources/TextToParse.txt"};
		final var stdin = new ByteArrayInputStream("SELECT foo FROM bar".getBytes());
		final int exitCode = Main.doMain(args, stdin, stdout, stderr);
		assertEquals(0, exitCode);
	}
	
	@Test
	public void testCliFromNonexistentFile() {
		final String[] args = {"--format", "dot", "-g", "src/test/resources/grammars/tests/0.bnf", "-i", "doesnotexist.txt"};
		final var stdin = new ByteArrayInputStream("SELECT foo FROM bar".getBytes());
		final int exitCode = Main.doMain(args, stdin, stdout, stderr);
		assertEquals(ERROR_IO, exitCode);
	}
	
	@Test
	public void testCli5() {
		final String[] args = {"-g", "nonexistentfile.bnf"};
		final var stdin = new ByteArrayInputStream("SELECT foo FROM bar".getBytes());
		final int exitCode = Main.doMain(args, stdin, stdout, stderr);
		assertEquals(ERROR_IO, exitCode);
	}
	
	@Test
	public void testCli6() {
		final String[] args = {"--format", "dot", "-g", "src/test/resources/grammars/tests/0.bnf"};
		final var stdin = new ByteArrayInputStream("SELECT".getBytes());
		final int exitCode = Main.doMain(args, stdin, stdout, stderr);
		assertEquals(ERROR_PARSE, exitCode);
	}
	
	@Test
	public void testInvalidGrammar() {
		final String[] args = {"-g", "src/test/resources/grammars/tests/invalid-1.bnf"};
		final var stdin = new ByteArrayInputStream("SELECT".getBytes());
		final int exitCode = Main.doMain(args, stdin, stdout, stderr);
		assertEquals(ERROR_GRAMMAR, exitCode);
	}
	
	@Test
	public void testCliVersion() {
		final String[] args = {"--version"};
		final int exitCode = Main.doMain(args, null, stdout, stderr);
		assertEquals(0, exitCode);
	}
	
	@Test
	public void testCliHelp() {
		final String[] args = {"--help", "--log-level", "0"};
		final int exitCode = Main.doMain(args, null, stdout, stderr);
		assertEquals(0, exitCode);
	}

}
