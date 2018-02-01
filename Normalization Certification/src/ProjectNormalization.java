import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class ProjectNormalization {
	public static String columnNameLine;
	public static String tableName;
	public static HashMap<String, List<String>> tableColumnMap;
	public static HashMap<String, HashMap<String, List<String>>> tableColumnKeyMap;
	public static List<String> columnNameList;
	public static List<String> columnNameKList;
	public static List<String> columnNameNKList;
	public static StringBuffer decompositionContent = new StringBuffer("\n");
	public static String result_Table = "NF_TEMP_TEAM5";
	public static int columnFlag = 0;
	public static StringBuffer sqlDumpString = new StringBuffer("\n");
	public static String sqlDumpFile;
	public static String inputFileName;
	public static String OutputFile;

	
	// iniliization
	public void fileNameInilization(String inputFile,String outPutFile,String sqlDump)
	{
		inputFileName=inputFile;
		OutputFile=outPutFile;
		sqlDumpFile=sqlDump;
	}
	
	// get connection function
		public static Connection getConnection() {
			Connection con = null;
			try {
				Class.forName("com.vertica.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				System.out.println("Could not find the JDBC driver class.");
				e.printStackTrace();
			}
			Properties myProp = new Properties();
			myProp.put("user", "cosc6340");
			myProp.put("password", "**********");
			try {
				con = DriverManager.getConnection("jdbc:vertica://129.7.242.19:5433/cosc6340", myProp);
			} catch (SQLException e) {
				// Could not connect to database.
				System.out.println("Could not connect to database.");
				e.printStackTrace();
			}
			return con;
		}
	
	// read the contents of the File
	public HashMap<String, HashMap<String, List<String>>> readContentsOfFile() {
		tableColumnKeyMap = new HashMap<String, HashMap<String, List<String>>>();
		HashMap<String, List<String>> hm;
		try {
			Scanner input;
			File file = new File(inputFileName);
			input = new Scanner(file);
			while (input.hasNextLine()) {
				hm = new HashMap<String, List<String>>();
				columnNameKList = new ArrayList<String>();
				columnNameNKList = new ArrayList<String>();
				String line = input.nextLine();
				tableName = line.substring(0, line.indexOf("("));
				columnNameLine = line.substring(line.indexOf("(") + 1, line.length() - 1);
				String[] tokens = columnNameLine.split(",");
				for (int i = 0; i < tokens.length; i++) {
					if (tokens[i].contains("(")) {
						columnNameKList.add(tokens[i].toString().substring(0, tokens[i].indexOf("(")));

					} else {
						// System.out.println(tokens[i].toString());
						columnNameNKList.add(tokens[i].toString());
					}

				}
				hm.put("K", columnNameKList);
				hm.put("NK", columnNameNKList);
				tableColumnKeyMap.put(tableName, hm);
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableColumnKeyMap;
	}

	// check if table exists
	public int checkTableExists(String tableName) throws Exception {
		Statement st = null;
		Connection con = getConnection();
		int retVal = 0;
		try {
			st = con.createStatement();
			String strSmt = "SELECT count(table_name) as cnt FROM v_catalog.tables where table_name='" + tableName
					+ "'";
			ResultSet rs = st.executeQuery(strSmt);
			sqlDumpString.append(strSmt);
			sqlDumpString.append("\n");
			while (rs.next()) {
				retVal = rs.getInt(1);
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Could not create statement");
			e.printStackTrace();
		}

		return retVal;

	}

	// check if Column exists
	public int checkColumnExist(String tableName, List<String> columnNameList) {
		System.out.println(tableName + columnNameList);
		Statement st = null;
		Statement st1 = null;
		Connection conn = getConnection();
		int retVal = 0;
		try {
			st = conn.createStatement();
			for (String columnName : columnNameList) {
				String strSmt = "SELECT count(column_name) as cnt FROM v_catalog.columns where table_name='" + tableName
						+ "' and column_name ='" + columnName + "'";
				ResultSet rs1 = st.executeQuery(strSmt);
				sqlDumpString.append(strSmt);
				sqlDumpString.append("\n");
				while (rs1.next()) {
					if (rs1.getInt("cnt") == 1) {
						retVal = 1;
					} else {
						st1 = conn.createStatement();
						String tblNtExist = "INSERT INTO " + result_Table + " VALUES ('" + tableName + "', '"
								+ columnName + "', '', 'COLUMN DOES NOT EXIST')";
						st1.executeUpdate(tblNtExist);
						sqlDumpString.append(tblNtExist);
						sqlDumpString.append("\n");
						st1.close();
					}
				}
			}
			st.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}

	// certification of tables
	public void certification(HashMap<String, HashMap<String, List<String>>> tableColumnKeyMap) {
		Connection con = getConnection();
		HashMap<String, List<String>> hm = new HashMap<String, List<String>>();
		List<String> colList;
		Statement st = null;
		Statement st1 = null;
		List<String> colKList;
		List<String> colNKList;
		String sql1 = "", sql2 = "";
		int tableFlag = 0, nullCount = 0;
		String NF_Y_N = "", NF_KEY = "", NForm1 = "";
		String NF_1 = "", NF_2 = "";
		int NF1_Flag_1 = 0, NF1_Flag_2 = 0;
		String NF3_1 = "", NF3_2 = "";

		try {
			for (String tblName : tableColumnKeyMap.keySet()) {
				colList = new ArrayList<String>();
				hm = tableColumnKeyMap.get(tblName);
				colKList = new ArrayList<String>();
				colNKList = new ArrayList<String>();
				colKList = hm.get("K");
				colNKList = hm.get("NK");
				colList.addAll(colNKList);
				colList.addAll(colKList);
				if (colKList.size() > 1) {
					// Null Check
					sql1 = "SELECT COUNT(*) FROM ( SELECT " + colKList.get(0) + "," + colKList.get(1) + " FROM ("
							+ "SELECT * FROM " + tblName + " WHERE (" + colKList.get(0) + "," + colKList.get(1)
							+ ") IS NULL)T" + " GROUP BY " + colKList.get(0) + "," + colKList.get(1) + " ) TE";

					NF_1 = "SELECT COUNT(*) FROM " + tblName;
					NF_2 = "SELECT COUNT(*) FROM ( SELECT " + colKList.get(0) + "," + colKList.get(1)
							+ " FROM ( SELECT * FROM " + tblName + " WHERE (" + colKList.get(0) + "," + colKList.get(1)
							+ ") IS NOT NULL)T GROUP BY " + colKList.get(0) + "," + colKList.get(1)
							+ " HAVING COUNT(*) = 1) as TE";

					sql2 = "SELECT COUNT(*) FROM (SELECT " + colKList.get(0)
							+ " FROM " + tblName + " WHERE " + colKList.get(0)+" OR "+colKList.get(1)
							+ " IS NULL )TE) > 0";// THEN 'KEY1' ELSE 'KEY2' END AS NF_KEY";
				} else {
					// Null Check
					sql1 = "SELECT COUNT(*) FROM ( SELECT " + colKList.get(0) + " FROM (" + "SELECT * FROM " + tblName
							+ " WHERE " + colKList.get(0) + " IS NULL)T" + " GROUP BY " + colKList.get(0) + " ) TE";

					NF_1 = "SELECT COUNT(*) FROM " + tblName;
					NF_2 = "SELECT COUNT(*) FROM (SELECT " + colKList.get(0) + " FROM ( SELECT * FROM " + tblName
							+ " WHERE " + colKList.get(0) + " IS NOT NULL) AS T GROUP BY T." + colKList.get(0)
							+ " HAVING COUNT(*) = 1) as TE";

					sql2 = "SELECT COUNT(*) AS CNT FROM (SELECT " + colKList.get(0)
							+ " FROM " + tblName + " WHERE " + colKList.get(0)
							+ " IS NULL )TE) > 0 ";//THEN 'KEY1' ELSE 'KEY2' END AS NF_KEY";

				}
				// check if table exist
				tableFlag = checkTableExists(tblName);

				// update in the result table in DB
				if (tableFlag == 0) {
					st = con.createStatement();
					String tblNtExist = "INSERT INTO " + result_Table + " VALUES ('" + tblName
							+ "', '', '', 'TABLE DOES NOT EXIST')";
					st.executeQuery(tblNtExist);
					sqlDumpString.append(tblNtExist);
					sqlDumpString.append("\n");
					st.close();
				} else {
					// checking for columns existence
					columnFlag = checkColumnExist(tblName, colList);
					if (columnFlag != 0) {
						st1 = con.createStatement();
						ResultSet rs1 = st1.executeQuery(sql1);
						sqlDumpString.append(sql1);
						sqlDumpString.append("\n");
						while (rs1.next()) {
							nullCount = rs1.getInt(1);
						}
						if (nullCount > 0) {
							rs1 = st1.executeQuery(sql2);
							sqlDumpString.append(sql2);
							sqlDumpString.append("\n");
							while (rs1.next()) {
								int f=rs1.getInt(1);
								if(f>0)
									NF_KEY="KEY1";
								else
									NF_KEY="KEY2";
							}
							if (NF_KEY.equals("KEY1"))
								st1.execute("INSERT INTO  " + result_Table + "  VALUES ('" + tblName
										+ "', '1NF', 'N', '" + colKList.get(0) + " HAS NULL VALUES')");
							else
								st.execute("INSERT INTO  " + result_Table + "  VALUES ('" + tblName + "', '1NF', 'N', '"
										+ colKList.get(1) + " HAS NULL VALUES')");
						} else {
							if (colKList.size() > 1) {
								ResultSet rs_1 = st1.executeQuery(NF_1);
								sqlDumpString.append(NF_1);
								sqlDumpString.append("\n");
								while (rs_1.next()) {
									NF1_Flag_1 = rs_1.getInt(1);
								}
								ResultSet rs_2 = st1.executeQuery(NF_2);
								sqlDumpString.append(NF_2);
								sqlDumpString.append("\n");
								while (rs_2.next()) {
									NF1_Flag_2 = rs_2.getInt(1);
								}
								Statement st2 = null;
								st2 = con.createStatement();
								if (NF1_Flag_1 == NF1_Flag_2)
									st2.execute("INSERT INTO  " + result_Table + "  VALUES ('" + tblName
											+ "', '1NF', 'Y', '')");
								else
									st2.execute("INSERT INTO  " + result_Table + "  VALUES ('" + tblName
											+ "', '1NF', 'N', '" + colKList.get(0) + "," + colKList.get(1)
											+ " COMBINATION HAS DUPLICATE VALUES')");
							} else {
								// System.out.println("NF_1" + NF_1);
								ResultSet rs_1 = st1.executeQuery(NF_1);
								sqlDumpString.append(NF_1);
								sqlDumpString.append("\n");
								while (rs_1.next()) {
									NF1_Flag_1 = rs_1.getInt(1);
									// System.out.println("NF1_Flag_1 :" +
									// NF1_Flag_1);
								}
								ResultSet rs_2 = st1.executeQuery(NF_2);
								sqlDumpString.append(NF_2);
								sqlDumpString.append("\n");
								//System.out.println(NF_2);
								while (rs_2.next()) {
									NF1_Flag_2 = rs_2.getInt(1);
									// System.out.println("NF1_Flag_2 :" +
									// NF1_Flag_2);
								}
								Statement st2 = null;
								st2 = con.createStatement();
								if (NF1_Flag_1 == NF1_Flag_2)
									st2.execute("INSERT INTO  " + result_Table + "  VALUES ('" + tblName
											+ "', '1NF', 'Y', '')");
								else
									st2.execute("INSERT INTO  " + result_Table + "  VALUES ('" + tblName
											+ "', '1NF', 'N', '" + colKList.get(0) + " HAS DUPLICATE VALUES')");
							}

						}
						String selectqry="SELECT Y_N FROM  " + result_Table + "  WHERE TABLENAME = '" + tblName
								+ "' AND NORMALFORM = '1NF'";
						rs1 = st1.executeQuery(selectqry);
						sqlDumpString.append(selectqry);
						sqlDumpString.append("\n");
						while (rs1.next()) {
							NF_Y_N = rs1.getString("Y_N");
						}
						Statement st_1_2 = null;
						st_1_2 = con.createStatement();
						if (NF_Y_N.equals("Y")) {	
							String insertqry = "INSERT INTO  " + result_Table + "  VALUES ('" + tblName
									+ "', '2NF', '', '')";
							st_1_2.execute(insertqry);
							if (colKList.size() == 1){
							String updtqry="UPDATE  " + result_Table + "  SET Y_N = 'Y' WHERE TABLENAME = '"
									+ tblName + "' AND NORMALFORM = '2NF'";
								st_1_2.execute(updtqry);
							sqlDumpString.append(updtqry);
							sqlDumpString.append("\n");
							}else {
								List<String> ck_FD = new ArrayList<String>();
								List<String> nck_FD = new ArrayList<String>();
								for (int i = 0; i < colKList.size(); i++) {
									for (int j = 0; j < colNKList.size(); j++) {
										String NF2_1 = "SELECT COUNT(*) FROM (SELECT t." + colKList.get(i) + ",t."
												+ colNKList.get(j) + " FROM " + tblName + " AS t INNER JOIN ( SELECT "
												+ colKList.get(i) + " FROM " + tblName + " GROUP BY " + colKList.get(i)
												+ " HAVING COUNT( DISTINCT " + colNKList.get(j) + ") > 1 ) g ON t."
												+ colKList.get(i) + " = g." + colKList.get(i) + " WHERE t."
												+ colNKList.get(j) + " IS NOT NULL GROUP BY t." + colKList.get(i)
												+ ", t." + colNKList.get(j) + " ) X";
										// System.out.println("NF2:" + NF2_1);
										Statement stnf2 = null;
										stnf2 = con.createStatement();
										// stnf2.execute(NF2_1);
										ResultSet rsnf2 = stnf2.executeQuery(NF2_1);
										sqlDumpString.append(NF2_1);
										sqlDumpString.append("\n");
										int nf2_flag = 1;
										while (rsnf2.next()) {
											nf2_flag = rsnf2.getInt(1);
										}
										if (nf2_flag == 0) {
											ck_FD.add(colKList.get(i));
											nck_FD.add(colNKList.get(j));
											//String updatequr = "UPDATE  " + result_Table + "  SET REASON ='"
											//		+ colKList.get(i) + "->" + colNKList.get(j)
											//		+ "' , Y_N = 'N' WHERE TABLENAME ='" + tblName
											//		+ "' AND NORMALFORM = '2NF' ";
											//stnf2.executeUpdate(updatequr);
											//sqlDumpString.append(updatequr);
											//sqlDumpString.append("\n");
										}
										stnf2.close();
									}
								}
								if (ck_FD.size() > 0){
									Statement stnf21 = null;
									stnf21 = con.createStatement();
									String printStr = "";
									for (int i = 0; i<ck_FD.size();i++){
										printStr = printStr + ck_FD.get(i) + "->" + nck_FD.get(i)+ " ";
									}
									String updatequr = "UPDATE  " + result_Table + "  SET REASON ='"
											+ printStr
											+ "' , Y_N = 'N' WHERE TABLENAME ='" + tblName
											+ "' AND NORMALFORM = '2NF' ";
									stnf21.executeUpdate(updatequr);
									sqlDumpString.append(updatequr);
									sqlDumpString.append("\n");
									decompositionContent.append("2NF Decomposition");
									decompositionContent.append("\n");
									System.out.println("2NF Decomposition");
									// We have FD. Let's decompose
									//System.out.println(ck_FD);
									//System.out.println(nck_FD);
									List<String> Decomp_ck = new ArrayList<String>();
									List<String> Decomp_nck = new ArrayList<String>();
									List<Integer> allNCKeys = new ArrayList<Integer>(); // Will contain 1 if the non candidate key is not deleted. 0 if deleted. 
									for (int i=0;i<colNKList.size();i++){
										allNCKeys.add(1);
									}
									
									int sum;
									String Relation;
									int count = 0;
									int flag = 1;
									for (int i = 0; i<colKList.size(); i++){// For every candidate key
										for (int j = 0; j<colNKList.size(); j++){ // For every non candidate key
											sum = allNCKeys.stream().mapToInt(Integer::intValue).sum(); // If we haven't finished with all non-candidate keys
											if (sum==0){
												break;
											}
											if (allNCKeys.get(j) == 1){ // If we have not deleted the non-candidate key 
												for (int z = 0; z<ck_FD.size(); z++){ // Check if the FD between the key and the non-candidate key exists
													if (colKList.get(i) == ck_FD.get(z) & colNKList.get(j) == nck_FD.get(z)){
														Decomp_ck.add(ck_FD.get(z));
														Decomp_nck.add(nck_FD.get(z));
														count++;
														Relation = "R"+ count + "(" + ck_FD.get(z)+"," + nck_FD.get(z) +")";
														decompositionContent.append(Relation);
														decompositionContent.append("\n");
														System.out.println(Relation);
														allNCKeys.set(j, Integer.valueOf(0));
														break;
													}
												}
											}
										}
										sum = allNCKeys.stream().mapToInt(Integer::intValue).sum();
										if (sum==0){
											flag = 0;
											break;
										}
									}
									count++;
									if (flag==0){
										// PRINT ALL CANDIDATE KEYS
										String pr = "R" + count +"(";
										String conc = "(";
										for (int i=0;i<colKList.size();i++){
											if (i == colKList.size()-1){
												pr = pr + colKList.get(i)+")";
												conc = conc + colKList.get(i) +")";
											}else{
												conc = conc+ colKList.get(i)+",";
												pr = pr + colKList.get(i)+",";
											}
											
										}
										Decomp_ck.add(conc);
										Decomp_nck.add(" ");
										System.out.println(pr);
										decompositionContent.append(pr);
										decompositionContent.append("\n");
										//writeContent(content.toString());
										//System.out.println(Decomp_ck);
										//System.out.println(Decomp_nck);
									}else{
										sum = allNCKeys.stream().mapToInt(Integer::intValue).sum();
										if (sum>1){
											for (int i = 0; i<colKList.size(); i++){
												for (int j = 0; j<colNKList.size(); j++){
													// Printing for more than 1 candidate keys and more than one non-canidadate with a FD
												}
												
											}
										}else{
											// Print all candidate keys and whatever's left from the non-candidate
											String pr = "R" + count +"(";
											for (int i=0;i<colKList.size();i++){
												pr = pr + colKList.get(i)+",";
											}
											for (int i=0;i<allNCKeys.size();i++){
												sum = allNCKeys.stream().mapToInt(Integer::intValue).sum();
												if (allNCKeys.get(i)==1 & sum >1){
													pr = pr + colNKList.get(i)+",";
													allNCKeys.set(i, Integer.valueOf(0));
												}else if (allNCKeys.get(i)==1 & sum ==1){
													pr = pr + colNKList.get(i)+")";
													allNCKeys.set(i, Integer.valueOf(0));
												}
											}
											decompositionContent.append(pr);
											decompositionContent.append("\n");
											System.out.println(pr);					
										}
									}
									//Join Verification
									
									//String J = "SELECT DISTINCT " + Decomp_ck.get(0) + "," + Decomp_nck.get(0) + " INTO Join1 FROM " + tblName ;
									//String J1 = "SELECT * FROM " + "Join1";
									//String Join = "SELECT DISTINCT " + Decomp_ck.get(0) + "," + Decomp_nck.get(0) + " FROM " + tblName ;
									//Statement stnfJ = null;
									//stnfJ = con.createStatement();
									//stnfJ.execute("DROP TABLE IF EXISTS" +" Join1");
									// stnf2.execute(NF2_1);
									//stnfJ.execute(J);
									//ResultSet rsnf2 = stnfJ.executeQuery(J1);
									//while(rsnf2.next()){
										
									//}
									//sqlDumpString.append(J);
									//sqlDumpString.append("\n");
								}
							}
							String rsltqry = "SELECT Y_N FROM  " + result_Table + "  WHERE TABLENAME = '" + tblName
									+ "' AND NORMALFORM = '2NF'";
							rs1 = st1.executeQuery(rsltqry);
							sqlDumpString.append(rsltqry);
							sqlDumpString.append("\n");
							while (rs1.next()) {
								NForm1 = rs1.getString("Y_N");
							}
							if ((NForm1.equals("")) || NForm1.equals("Y")) {
								String updateqry="UPDATE  " + result_Table + "  SET Y_N = 'Y' WHERE TABLENAME = '" + tblName
										+ "' AND NORMALFORM = '2NF'";
								st1.execute(updateqry);
								sqlDumpString.append(updateqry);
								sqlDumpString.append("\n");						
								// checking for 3NF
								List<String> ck_FD_3NF = new ArrayList<String>();
								List<String> nck_FD_3NF = new ArrayList<String>();
								st1.execute("INSERT INTO  " + result_Table + "  VALUES ('" + tblName + "', '3NF', '', '')");
								for (int i = 0; i < (colNKList.size() - 1); i++) {
									for (int j = (i + 1); j < colNKList.size(); j++) {

										NF3_1 = "SELECT COUNT(*) FROM (SELECT t." + colNKList.get(i) + ",t."
												+ colNKList.get(j) + " FROM " + tblName + " AS t INNER JOIN ( SELECT "
												+ colNKList.get(i) + " FROM " + tblName + " GROUP BY "
												+ colNKList.get(i) + " HAVING COUNT( DISTINCT " + colNKList.get(j)
												+ ") > 1 ) g ON t." + colNKList.get(i) + " = g." + colNKList.get(i)
												+ " WHERE (t." + colNKList.get(i) + " , t." + colNKList.get(j)
												+ ") IS NOT NULL GROUP BY t." + colNKList.get(i) + ", t."
												+ colNKList.get(j) + " ) X ";

										Statement stnf3 = null;
										stnf3 = con.createStatement();
										ResultSet rsnf3 = stnf3.executeQuery(NF3_1);
										sqlDumpString.append(NF3_1);
										sqlDumpString.append("\n");	
										int nf3_flag = 1;
										while (rsnf3.next()) {
											nf3_flag = rsnf3.getInt(1);
										}
										if (nf3_flag == 0) {
											ck_FD_3NF.add(colNKList.get(i));
											nck_FD_3NF.add(colNKList.get(j)); // save can keys and non can keys that are func. dependent
											//String updatequr = "UPDATE " + result_Table + "  SET REASON = '"
											//		+ colNKList.get(i) + "->" + colNKList.get(j)
											//		+ "  ' , Y_N = 'N' WHERE TABLENAME='" + tblName
											//		+ "' AND NORMALFORM='3NF' ";
											//stnf3.executeUpdate(updatequr);
											//sqlDumpString.append(updatequr);
											//sqlDumpString.append("\n");	
										}
										stnf3.close();
										NF3_2 = "SELECT COUNT(*) FROM (SELECT t." + colNKList.get(j) + ",t."
												+ colNKList.get(i) + " FROM " + tblName + " AS t INNER JOIN ( SELECT "
												+ colNKList.get(j) + " FROM " + tblName + " GROUP BY "
												+ colNKList.get(j) + " HAVING COUNT( DISTINCT " + colNKList.get(i)
												+ ") > 1 ) g ON t." + colNKList.get(j) + " = g." + colNKList.get(j)
												+ " WHERE (t." + colNKList.get(j) + " , t." + colNKList.get(i)
												+ ") IS NOT NULL GROUP BY t." + colNKList.get(j) + ", t."
												+ colNKList.get(i) + " ) X ";

										Statement stnf3_1 = null;
										stnf3_1 = con.createStatement();
										ResultSet rsnf3_1 = stnf3_1.executeQuery(NF3_2);
										sqlDumpString.append(NF3_2);
										sqlDumpString.append("\n");	
										nf3_flag = 1;
										while (rsnf3_1.next()) {
											nf3_flag = rsnf3_1.getInt(1);
										}
										if (nf3_flag == 0) {
											//ck_FD_3NF.add(colNKList.get(j));
											//nck_FD_3NF.add(colNKList.get(i));
											//String updatequr = "UPDATE " + result_Table + "  SET REASON = '"
											//		+ colNKList.get(j) + "->" + colNKList.get(i)
											//		+ "', Y_N = 'N' WHERE TABLENAME='" + tblName
											//		+ "' AND NORMALFORM='3NF' ";
											//stnf3_1.executeUpdate(updatequr);
											//sqlDumpString.append(updatequr);
											//sqlDumpString.append("\n");	
											
										}
										stnf3_1.close();
									}
								}
								if (ck_FD_3NF.size() > 0){ // 3NF Decomposition
									Statement stnf31 = null;
									stnf31 = con.createStatement();
									String printStr = "";
									for (int i= 0; i<ck_FD_3NF.size();i++){
										printStr = printStr + ck_FD_3NF.get(i) + "->" + nck_FD_3NF.get(i) + " ";
									}
									
									String updatequr = "UPDATE " + result_Table + "  SET REASON = '"
											+ printStr
											+ "', Y_N = 'N' WHERE TABLENAME='" + tblName
											+ "' AND NORMALFORM='3NF' ";
									stnf31.executeUpdate(updatequr);
									sqlDumpString.append(updatequr);
									sqlDumpString.append("\n");	
									decompositionContent.append("3NF Decomposition\n");
		
									System.out.println("3NF Decomposition");
									
									List<String> Up = new ArrayList<String>();
									List<String> Down = new ArrayList<String>();
									int count = 0;
									String Relation;
									for (int i = 0; i<ck_FD_3NF.size();i++){
										if (Down.contains(ck_FD_3NF.get(i)) | Down.contains(nck_FD_3NF.get(i))) {
										    //ignore
										}else{
											if (Up.contains(ck_FD_3NF.get(i)) | Up.contains(nck_FD_3NF.get(i))) {
												count++;
												if (Up.contains(ck_FD_3NF.get(i))){
													Down.add(nck_FD_3NF.get(i));
													Relation = "R_"+ count + "(" + ck_FD_3NF.get(i)+"," + nck_FD_3NF.get(i) +")";
													System.out.println(Relation);
													decompositionContent.append(Relation);
													decompositionContent.append("\n");
												}else{
													Down.add(ck_FD_3NF.get(i));
													Relation = "R_"+ count + "(" + ck_FD_3NF.get(i)+"," + nck_FD_3NF.get(i) +")";
													System.out.println(Relation);
													decompositionContent.append(Relation);
													decompositionContent.append("\n");
						
												}
											}else{
												count++;
												Up.add(ck_FD_3NF.get(i));
												Down.add(nck_FD_3NF.get(i));
												Relation = "R_"+ count + "(" + ck_FD_3NF.get(i)+"," + nck_FD_3NF.get(i) +")";
												System.out.println(Relation);
												decompositionContent.append(Relation);
												decompositionContent.append("\n");
											}
										}
									}
									count++;
									String pr3 = "R_" + count + "(";
									for (int i=0;i<colKList.size();i++){
										pr3 = pr3 + colKList.get(i)+",";
									}
									for (int i = 0; i<Up.size();i++){
										if (i == Up.size()-1){
											pr3 = pr3 + Up.get(i) +")";
										}else{
											pr3 = pr3 + Up.get(i) +",";
										}
										
									}
									System.out.println(pr3);
									decompositionContent.append(pr3);
									decompositionContent.append("\n");
								}
								rs1 = st1.executeQuery("SELECT Y_N FROM  " + result_Table + "  WHERE TABLENAME = '"
										+ tblName + "' AND NORMALFORM = '3NF'");
								sqlDumpString.append("SELECT Y_N FROM  " + result_Table + "  WHERE TABLENAME = '"
										+ tblName + "' AND NORMALFORM = '3NF'");
								sqlDumpString.append("\n");	

								while (rs1.next()) {
									NForm1 = rs1.getString("Y_N");
								}

								if (NForm1.equals("")) {
									st1.execute("UPDATE  " + result_Table + "  SET Y_N = 'Y' WHERE TABLENAME = '"
											+ tblName + "' AND NORMALFORM = '3NF'");
									sqlDumpString.append("UPDATE  " + result_Table + "  SET Y_N = 'Y' WHERE TABLENAME = '"
											+ tblName + "' AND NORMALFORM = '3NF'");
									sqlDumpString.append("\n");	
								}
							}
						}
						st1.close();
					} // end of for loop
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	// Table initialize
	public void tablesInilialize() {
		Connection con = getConnection();
		Statement st = null;
		try {
			st = con.createStatement();
			st.execute("DROP TABLE IF EXISTS " + result_Table);
			st.execute("CREATE TABLE " + result_Table
					+ "(TableName varchar(20),NormalForm varchar(5),Y_N varchar(3),Reason varchar(200))");
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// Final Output table generator
	public void resultTableGenerator()
	{
		Connection con = getConnection();
		Statement st = null;
		StringBuffer content;
		try {
			st = con.createStatement();
			ResultSet rsout=st.executeQuery("SELECT * FROM "+result_Table);
			while(rsout.next())
			{
				content = new StringBuffer();
				content.append(rsout.getString(1));
				content.append(" \t ");
				content.append(rsout.getString(2));
				content.append(" \t ");
				content.append(rsout.getString(3));
				content.append(" \t ");
				content.append(rsout.getString(4));
				writeContent(content.toString());
			}
			st.close();
			writeContent(decompositionContent.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// write contents to a file 
	public void writeContent(String content){
		try {
			int f=0;
			File file = new File(OutputFile);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
				f=1;
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			if(f==1){
				bw.write("----------------------------");
				bw.write("\n");
				bw.write("TABLE \t FORM \t Y_N \t REASON");
				bw.write("\n");
				bw.write("----------------------------");
				bw.write("\n");
				bw.write(content);
				bw.write("\n");
				bw.close();
			}
			else
			{
			bw.write(content);
			bw.write("\n");
			bw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Sql dump
		public void resultSqlDump() {
			try {
				int f = 0;
				File file = new File(sqlDumpFile);
				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(sqlDumpString.toString());
				bw.write("\n");
				bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}