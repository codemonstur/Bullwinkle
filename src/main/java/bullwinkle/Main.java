package bullwinkle;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import bullwinkle.error.*;
import bullwinkle.output.*;
import bullwinkle.parsing.BnfParser;
import jcli.CliHelp;
import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import jcli.annotations.CliPositional;
import jcli.errors.InvalidCommandLine;
import jcli.errors.UnknownCommandLineArgument;

import static bullwinkle.Constants.ERROR_ARGUMENTS;
import static bullwinkle.Constants.ERROR_IO;
import static bullwinkle.util.Functions.isNullOrEmpty;
import static java.nio.charset.StandardCharsets.UTF_8;
import static jcli.CliParserBuilder.newCliParser;

public enum Main {;

	@SuppressWarnings("ALL")
	@CliCommand(name = "Bullwinkle 1, an LL(k) parser")
	private static class Arguments {
		@CliOption(name = 'l', longName = "log-level", defaultValue = "1", description = "Verbose logging with level x")
		private int logLevel;
		@CliOption(name = 'v', longName = "version", description = "Show version number")
		private boolean version;
		@CliOption(name = 'h', longName = "help", isHelp = true, description = "Display command line usage")
		private boolean help;
		@CliOption(name = 'g', longName = "grammar", description = "The grammar file to parse")
		private String grammar;
		@CliOption(name = 'i', longName = "input-file", description = "The input file to parse using the grammar")
		private String inputFile;
		@CliOption(name = 'f', longName = "format", defaultValue = "xml", description = "Output parse tree in format x (dot, xml, txt)")
		private String format;
	}

	public static void main(final String... args) throws InvalidCommandLine {
		System.exit(doMain(args, System.in, System.out, System.err));
	}

	public static int doMain(final String[] args, final InputStream stdin, final PrintStream stdout, final PrintStream stderr) {
		try {
			final Arguments arguments = newCliParser(Arguments::new).parse(args);
			if (arguments == null) {
				stderr.println("Error parsing command-line arguments");
				return ERROR_ARGUMENTS;
			}

			if (arguments.version) {
				stderr.println("Bullwinkle " + Main.class.getPackage().getImplementationVersion() + ", an LL(k) parser\n");
				stderr.println("(C) 2014-2018 Sylvain Hallé et al., Université du Québec à Chicoutimi");
				stderr.println("This program comes with ABSOLUTELY NO WARRANTY.");
				stderr.println("This is a free software, and you are welcome to redistribute it");
				stderr.println("under certain conditions. See the file LICENSE-2.0 for details.\n");
				return 0;
			}
			if (arguments.help) {
				stderr.println(CliHelp.getHelp(Arguments.class));
				return 0;
			}

			if (arguments.logLevel > 0)
				stderr.println("Bullwinkle " + Main.class.getPackage().getImplementationVersion() + ", an LL(k) parser");
			if (isNullOrEmpty(arguments.grammar)) {
				stderr.println("[ERROR] no grammar file specified");
				return ERROR_ARGUMENTS;
			}
			try (final var grammarInput = new FileInputStream(arguments.grammar)) {
				final var parser = new BnfParser(grammarInput);
				final var dataToParse = readDataToParse(arguments.inputFile, stdin);
				final var node = parser.parse(dataToParse);
				if (node == null) throw new ParseException("Failure to parse input");

				final var outputGenerator = findOutputFormat(arguments.format);
				node.prefixAccept(outputGenerator);
				stdout.print(outputGenerator.toOutputString());

				return 0;
			}
		} catch (Exception e) {
			stderr.println("[ERROR]: " + e.getMessage());
			if (e instanceof HasCliExitCode) {
				return ((HasCliExitCode) e).getExitCode();
			}
			if (e instanceof IOException) {
				return ERROR_IO;
			}
			if (e instanceof InvalidCommandLine) {
				return ERROR_ARGUMENTS;
			}
			return Integer.MAX_VALUE;
		}
	}

	private static OutputFormatVisitor findOutputFormat(final String selectedFormat) throws UnknownOutputFormat {
		if (selectedFormat.equalsIgnoreCase("xml")) return new Xml();
		if (selectedFormat.equalsIgnoreCase("dot")) return new Graphviz();
		if (selectedFormat.equalsIgnoreCase("txt")) return new IndentedPlainText();
		throw new UnknownOutputFormat(selectedFormat);
	}

	private static String readDataToParse(final String inputFileName, final InputStream inputStream) throws IOException {
		try (final var in = inputFileName != null ? new FileInputStream(inputFileName) : inputStream) {
			return new String(in.readAllBytes(), UTF_8);
		}
	}

}
