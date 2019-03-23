package fr.univ_tours.li.mdjedaini.ideb.test;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import fr.univ_tours.li.mdjedaini.ideb.interestingness.User;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

public class userThread implements Runnable {

	User u;
	Session currentSession;
	Query currentQuery;

	public userThread(User u) {
		this.u = u;
	}

	@Override
	public void run() {
		try {
			System.out.println("Starting with user: " + u.getName());
			String username = u.getName();
			HashMap<Session, Character> us = u.getSessionLabels();
			for (Session s : us.keySet()) {
				currentSession = s;
				// set result file
				String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
						.format(new Date());
				String fileName = TestInterestingnessMT.pathToResult
						+ "RESULT-" + timestamp + "-"
						+ s.getMetadata("filename") + ".csv";
				FileWriter writer = new FileWriter(fileName);
				writer.write("session ;user;query position;cell hashcode;cell computation time;novelty;outlierness;relevance;surprise;query label;session label\n");

				Character sessionLabel = us.get(s);
				int sessionHashcode = s.hashCode();
				String sessionName = s.getMetadata("filename");
				int queryPos = 0;
				for (Query q : s.getQueryList()) {
					currentQuery = q;
					int queryHashcode = q.hashCode();
					// long startHistoryTime = System.currentTimeMillis();
					TestInterestingnessMT.computeHistory(u, queryPos);
					// System.out.println("history computed in: " +
					// (System.currentTimeMillis()-startHistoryTime));

					// u.getHistory().printMembers();
					int queryLabel = u.getCurrentQueryLabel();
					// System.out.println(q.getResult().getCellList().getCellCollection());
					for (EAB_Cell c : q.getResult().getCellList()
							.getCellCollection()) {

						long startCellTime = System.currentTimeMillis();
						int cellHashcode = c.hashCode();
						boolean novelty = c.binaryNovelty(u.getHistory());
						// System.out.println("novelty computed in: " +
						// (System.currentTimeMillis()-startCellTime));

						// long startOutlierTime = System.currentTimeMillis();
						double outlierness = c.outlierness(q.getResult()
								.getCellList());
						// System.out.println("outlier computed in: " +
						// (System.currentTimeMillis()-startOutlierTime));

						// long startRelevanceTime = System.currentTimeMillis();
						double relevance = c.simpleRelevance(u.getHistory());
						//double relevance = 0;
						
						
						// System.out.println("relevance computed in: " +
						// (System.currentTimeMillis()-startRelevanceTime));

						// long startSurpriseTime = System.currentTimeMillis();
						double surprise = c.surprise(u.getHistory()); 
						// System.out.println("surprise computed in: " +
						// (System.currentTimeMillis()-startSurpriseTime));

						long stopCellTime = System.currentTimeMillis();

						// put in file
						String current = "";
						current = current + sessionName + ";";
						current = current + username + ";";
						// current = current+ queryHashcode + ";";
						current = current + (queryPos + 1) + ";";
						current = current + cellHashcode + ";";
						current = current + (stopCellTime - startCellTime)
								+ ";";
						current = current + novelty + ";";
						current = current + outlierness + ";";
						current = current + relevance + ";";
						current = current + surprise + ";";
						current = current + queryLabel + ";";
						current = current + sessionLabel;

						current = current + "\n";
						writer.write(current);

						// Thread t=new Thread(new cellThread(c,u,q,
						// sessionName, username, queryPos, queryLabel,
						// sessionLabel));
						// t.start();

						// System.out.println(current);
						//System.gc();
					}
					queryPos++;
					// System.out.println("Flushing session: "+fileName);
					//System.out.println("User: " + u.getName());
					//System.out.println("Query: " + q.toString());
					// System.out.println("Memory consumption: " +
					// (Runtime.getRuntime().totalMemory()/1024)/1024 + " MB");

					writer.flush();

					// System.gc();
				}
				writer.close();
				System.out.println("Done with session: " + sessionName);
				// System.gc();
			}
			System.out.println("Done with user: " + u.getName());

		} catch (Exception e) {
			System.out.println("Problem for user: " + u.getName());
			System.out.println("in session: "
					+ currentSession.getMetadata("filename"));
			System.out.println("at query: " + currentQuery.toString());
			e.printStackTrace();

		}
	}

}
