import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectRun {

	public static void main(String[] args) throws Exception {
		ProjectNormalization obj = new ProjectNormalization();
		String inputFileName = "C://Users//Nikolaos//Documents//testschema.txt";
		String outputFileName = "C://Users//Nikolaos//Documents//NF_TEAM5.txt";
		String sqlDumpFile = "C://Users//Nikolaos//Documents//NF_TEAM5.sql";
		List<String> columnList = new ArrayList<String>();
		HashMap<String, List<String>> tableColumnMap = new HashMap<String, List<String>>();
		HashMap<String, HashMap<String, List<String>>> test = new HashMap<String, HashMap<String,List<String>>>();
		obj.fileNameInilization(inputFileName, outputFileName, sqlDumpFile);
		test = obj.readContentsOfFile();
		obj.tablesInilialize();
		obj.certification(test);
		obj.resultTableGenerator();
		obj.resultSqlDump();
	}
}
