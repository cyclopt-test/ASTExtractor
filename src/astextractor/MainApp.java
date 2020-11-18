package astextractor;

import helpers.ParseHelpers;

/**
 * Contains the main class of the application.
 * 
 * @author themis
 */
public class MainApp {

	/**
	 * Prints the help message of the command line interface.
	 */
	private static void printHelpMessage() {
		System.out.println("ASTExtractor: Abstract Syntax Tree Extractor for Java Source Code\n");
		System.out.println("Run as:\n java -jar ASTExtractor.jar -project=\"path/to/project\""
				+ " -properties=\"path/to/propertiesfile\" -repr=XML|JSON");
		System.out.println("Or as:\n java -jar ASTExtractor.jar -file=\"path/to/file\""
				+ " -properties=\"path/to/propertiesfile\" -repr=XML|JSON");
		System.out.println("where -properties allows setting the location of the properties file"
				+ " (default is no properties so all syntax tree nodes are returned)");
		System.out.println("and -repr allows selecting the representation of the tree (default is XML)");
	}

	/**
	 * Executes the application.
	 * 
	 * @param args arguments for executing in command line mode.
	 */
	public static void main(String args[]) {
		if (args.length > 0) {
			String[] arguments = ParseHelpers.parseArgs(args);
			String project = arguments[0];
			String file = arguments[1];
			String properties = arguments[2];
			String repr = "XML";
			if (project.length() > 0 ^ file.length() > 0) {
				if (arguments[3].length() > 0 && !(arguments[3].equals("JSON") || arguments[3].equals("XML")))
					printHelpMessage();
				else {
					ASTExtractorProperties.setProperties(properties);
					if (arguments[3].equals("JSON") || arguments[3].equals("XML"))
						repr = arguments[3];
					String result = "";
					if (project.length() > 0)
						result = ASTExtractor.parseFolder(project, repr);
					else if (file.length() > 0)
						result = ASTExtractor.parseFile(file, repr);
					System.out.println(result);
				}
			} else {
				printHelpMessage();
			}
		} else {
			printHelpMessage();
		}
	}
}

package astextractor;

import java.io.File;
import java.util.ArrayList;

import org.json.XML;

import astparser.JavaASTParser;
import helpers.FileSystemHelpers;
import helpers.XMLHelpers;

/**
 * Contains all functions for extracting Abstract Syntax Trees (ASTs) from java files.
 * 
 * @author themis
 */
public class ASTExtractor {

	/**
	 * Parses the contents of a java file and returns its AST.
	 * 
	 * @param fileContents the contents of a java file, given as a String.
	 * @return a String containing the AST of the java file in XML format.
	 */
	public static String parseString(String fileContents) {
		return parseString(fileContents, "XML");
	}

	/**
	 * Parses the contents of a java file and returns its AST.
	 * 
	 * @param fileContents the contents of a java file, given as a String.
	 * @param astFormat the format of the returned AST, either "XML" or "JSON".
	 * @return a String containing the AST of the java file in XML or JSON format.
	 */
	public static String parseString(String fileContents, String astFormat) {
		String result = JavaASTParser.parse(fileContents);
		if (astFormat.equals("JSON"))
			return XML.toJSONObject(result).toString(3);
		else
			return XMLHelpers.formatXML(result, 3);
	}

	/**
	 * Parses a java file and returns its AST.
	 * 
	 * @param filename the filename of the java file to be parsed.
	 * @return a String containing the AST of the java file in XML format.
	 */
	public static String parseFile(String filename) {
		return parseFile(filename, "XML");
	}

	/**
	 * Parses a java file and returns its AST.
	 * 
	 * @param filename the filename of the java file to be parsed.
	 * @param astFormat the format of the returned AST, either "XML" or "JSON".
	 * @return a String containing the AST of the java file in XML or JSON format.
	 */
	public static String parseFile(String filename, String astFormat) {
		String result = parseString(FileSystemHelpers.readFileToString(filename));
		if (astFormat.equals("JSON"))
			return XML.toJSONObject(result).toString(3);
		else
			return XMLHelpers.formatXML(result, 3);
	}

	/**
	 * Parses all the files of a folder and returns a unified AST.
	 * 
	 * @param folderName the path of the folder of which the files are parsed.
	 * @return an AST containing all the files of a folder in XML format.
	 */
	public static String parseFolder(String folderName) {
		return parseFolder(folderName, "XML");
	}

	/**
	 * Parses all the files of a folder and returns a unified AST.
	 * 
	 * @param folderName the path of the folder of which the files are parsed.
	 * @param astFormat the format of the returned AST, either "XML" or "JSON".
	 * @return an AST containing all the files of a folder in XML or JSON format.
	 */
	public static String parseFolder(String folderName, String astFormat) {
		String folderAbsolutePath = new File(folderName).getAbsolutePath();
		ArrayList<File> files = FileSystemHelpers.getJavaFilesOfFolderRecursively(folderName);
		StringBuilder results = new StringBuilder("<folder>\n");
		for (File file : files) {
			String fileAbsolutePath = file.getAbsolutePath();
			String filePath = FileSystemHelpers.getRelativePath(folderAbsolutePath, fileAbsolutePath);
			String result = parseFile(fileAbsolutePath);
			results.append("<file>\n<path>" + filePath + "</path>\n<ast>\n" + result + "</ast>\n</file>\n");
		}
		results.append("</folder>\n");
		if (astFormat.equals("JSON"))
			return XML.toJSONObject(results.toString()).toString(3);
		else
			return XMLHelpers.formatXML(results.toString(), 3);
	}
}

package astextractor;

import helpers.ParseHelpers;

/**
 * Contains the main class of the application.
 * 
 * @author themis
 */
public class MainApp {

	/**
	 * Prints the help message of the command line interface.
	 */
	private static void printHelpMessage() {
		System.out.println("ASTExtractor: Abstract Syntax Tree Extractor for Java Source Code\n");
		System.out.println("Run as:\n java -jar ASTExtractor.jar -project=\"path/to/project\""
				+ " -properties=\"path/to/propertiesfile\" -repr=XML|JSON");
		System.out.println("Or as:\n java -jar ASTExtractor.jar -file=\"path/to/file\""
				+ " -properties=\"path/to/propertiesfile\" -repr=XML|JSON");
		System.out.println("where -properties allows setting the location of the properties file"
				+ " (default is no properties so all syntax tree nodes are returned)");
		System.out.println("and -repr allows selecting the representation of the tree (default is XML)");
	}

	/**
	 * Executes the application.
	 * 
	 * @param args arguments for executing in command line mode.
	 */
	public static void main(String args[]) {
		if (args.length > 0) {
			String[] arguments = ParseHelpers.parseArgs(args);
			String project = arguments[0];
			String file = arguments[1];
			String properties = arguments[2];
			String repr = "XML";
			if (project.length() > 0 ^ file.length() > 0) {
				if (arguments[3].length() > 0 && !(arguments[3].equals("JSON") || arguments[3].equals("XML")))
					printHelpMessage();
				else {
					ASTExtractorProperties.setProperties(properties);
					if (arguments[3].equals("JSON") || arguments[3].equals("XML"))
						repr = arguments[3];
					String result = "";
					if (project.length() > 0)
						result = ASTExtractor.parseFolder(project, repr);
					else if (file.length() > 0)
						result = ASTExtractor.parseFile(file, repr);
					System.out.println(result);
				}
			} else {
				printHelpMessage();
			}
		} else {
			printHelpMessage();
		}
	}
}

package astextractor;

import java.io.File;
import java.util.ArrayList;

import org.json.XML;

import astparser.JavaASTParser;
import helpers.FileSystemHelpers;
import helpers.XMLHelpers;

/**
 * Contains all functions for extracting Abstract Syntax Trees (ASTs) from java files.
 * 
 * @author themis
 */
public class ASTExtractor {

	/**
	 * Parses the contents of a java file and returns its AST.
	 * 
	 * @param fileContents the contents of a java file, given as a String.
	 * @return a String containing the AST of the java file in XML format.
	 */
	public static String parseString(String fileContents) {
		return parseString(fileContents, "XML");
	}

	/**
	 * Parses the contents of a java file and returns its AST.
	 * 
	 * @param fileContents the contents of a java file, given as a String.
	 * @param astFormat the format of the returned AST, either "XML" or "JSON".
	 * @return a String containing the AST of the java file in XML or JSON format.
	 */
	public static String parseString(String fileContents, String astFormat) {
		String result = JavaASTParser.parse(fileContents);
		if (astFormat.equals("JSON"))
			return XML.toJSONObject(result).toString(3);
		else
			return XMLHelpers.formatXML(result, 3);
	}

	/**
	 * Parses a java file and returns its AST.
	 * 
	 * @param filename the filename of the java file to be parsed.
	 * @return a String containing the AST of the java file in XML format.
	 */
	public static String parseFile(String filename) {
		return parseFile(filename, "XML");
	}

	/**
	 * Parses a java file and returns its AST.
	 * 
	 * @param filename the filename of the java file to be parsed.
	 * @param astFormat the format of the returned AST, either "XML" or "JSON".
	 * @return a String containing the AST of the java file in XML or JSON format.
	 */
	public static String parseFile(String filename, String astFormat) {
		String result = parseString(FileSystemHelpers.readFileToString(filename));
		if (astFormat.equals("JSON"))
			return XML.toJSONObject(result).toString(3);
		else
			return XMLHelpers.formatXML(result, 3);
	}

	/**
	 * Parses all the files of a folder and returns a unified AST.
	 * 
	 * @param folderName the path of the folder of which the files are parsed.
	 * @return an AST containing all the files of a folder in XML format.
	 */
	public static String parseFolder(String folderName) {
		return parseFolder(folderName, "XML");
	}

	/**
	 * Parses all the files of a folder and returns a unified AST.
	 * 
	 * @param folderName the path of the folder of which the files are parsed.
	 * @param astFormat the format of the returned AST, either "XML" or "JSON".
	 * @return an AST containing all the files of a folder in XML or JSON format.
	 */
	public static String parseFolder(String folderName, String astFormat) {
		String folderAbsolutePath = new File(folderName).getAbsolutePath();
		ArrayList<File> files = FileSystemHelpers.getJavaFilesOfFolderRecursively(folderName);
		StringBuilder results = new StringBuilder("<folder>\n");
		for (File file : files) {
			String fileAbsolutePath = file.getAbsolutePath();
			String filePath = FileSystemHelpers.getRelativePath(folderAbsolutePath, fileAbsolutePath);
			String result = parseFile(fileAbsolutePath);
			results.append("<file>\n<path>" + filePath + "</path>\n<ast>\n" + result + "</ast>\n</file>\n");
		}
		results.append("</folder>\n");
		if (astFormat.equals("JSON"))
			return XML.toJSONObject(results.toString()).toString(3);
		else
			return XMLHelpers.formatXML(results.toString(), 3);
	}
}

