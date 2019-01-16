package iac.cnr.it;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestLister {
	private static Logger logger = LogManager.getLogger("TestLister");

	private final static String OPT_INDEX = "index";
	private final static String OPT_OUTPUT = "output";

	private static void usage() {
		System.out.print("Usage:\n\t");

		StringBuilder sb = new StringBuilder();

		sb.append(TestSearcher.class.getCanonicalName());
		sb.append(" -").append(OPT_INDEX).append(" [CASE_PATH]");
		sb.append(" -").append(OPT_OUTPUT).append(" [OUTPUT_FILE]");
		System.out.println(sb.toString());
	}

	private static void write (String filename, String[] terms) throws IOException {
		BufferedWriter outputWriter = null;
		outputWriter = new BufferedWriter(new FileWriter(filename));
		for (int i = 0; i < terms.length; i++) {
			outputWriter.write(terms[i]);
			outputWriter.newLine();
		}
		outputWriter.flush();  
		outputWriter.close();  
	}

	public static void main(String[] args) {
		/** Command line parser and options */
		CommandLineParser parser = new PosixParser();

		Options options = new Options();
		options.addOption(OPT_INDEX, true, "Index path");
		options.addOption(OPT_OUTPUT, true, "Output file");

		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (org.apache.commons.cli.ParseException e) {
			logger.fatal("Error while parsing command line arguments: " + e.getMessage());
			System.exit(1);
		}

		/** Check for mandatory options */
		if (!cmd.hasOption(OPT_INDEX) || !cmd.hasOption(OPT_OUTPUT)) {
			usage();
			System.exit(0);
		}

		/** Read options */
		File casePath = new File(cmd.getOptionValue(OPT_INDEX));
		String filename = cmd.getOptionValue(OPT_OUTPUT);

		/** Check correctness of the path containing an ISODAC case */
		if (!casePath.exists() || !casePath.isDirectory()) {
			logger.fatal("The case directory \"" + casePath.getAbsolutePath() + "\" is not valid");
			System.exit(1);
		}

		/** Load all the directories containing an index */
		ArrayList<String> indexesDirs = new ArrayList<String>();
		for (File f : casePath.listFiles()) {
			logger.info("Analyzing: " + f);
			if (f.isDirectory())
				indexesDirs.add(f.getAbsolutePath());
		}
		logger.info(indexesDirs.size() + " directories found!");

		Lister lister = null;
		String[] terms = null;
		try {
			String[] array = indexesDirs.toArray(new String[indexesDirs.size()]);
			lister = new Lister(array);

			terms = lister.list();

			write(filename, terms);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (lister != null) {
				lister.close();
			}
		}
	}
}
