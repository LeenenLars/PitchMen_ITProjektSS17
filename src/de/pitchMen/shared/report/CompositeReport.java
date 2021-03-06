package de.pitchMen.shared.report;

import java.io.Serializable;
import java.util.ArrayList;
/**
	 * Superklasse der Klassen ApplicationsRelatedJobPostingsOfUser, ProjectInterweavingsWithParticipationsAndApplications, FanInJobPostingsOfUser, FanOutApplicationsOfUser.
	 * Zusammensetzung von mehreren Teil-Reports (SubReports Attribut).
	 * Implementiert das Interface Serializable.
	 * Subklasse von Report.
	 * 
	 * @author
	 */
	public abstract class CompositeReport extends Report implements Serializable{

	    /**
	     * 
	     */
	    private static final long serialVersionUID = 1L;

	    /**
	     * 
	     */
	    private ArrayList<Report> subReports = new ArrayList<Report>();

	    /**
	     * @param report 
	     * @return
	     */
	    public void addSubReport(Report report) {
	        subReports.add(report);
	   
	    }

	    /**
	     * @param report 
	     * @return
	     */
	    public void removeSubReport(Report report) {
	       subReports.remove(report);
	        
	    }
	    
	    public int getNumSubReports() {
	        return subReports.size();
	      }
	    
	    public Report getSubReportAt(int i) {
	        return subReports.get(i);
	      }

	}
