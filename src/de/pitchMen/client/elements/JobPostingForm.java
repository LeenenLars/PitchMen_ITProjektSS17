package de.pitchMen.client.elements;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.pitchMen.client.ClientsideSettings;
import de.pitchMen.shared.bo.Application;
import de.pitchMen.shared.bo.JobPosting;
import de.pitchMen.shared.bo.Marketplace;
import de.pitchMen.shared.bo.PartnerProfile;
import de.pitchMen.shared.bo.Person;
import de.pitchMen.shared.bo.Project;
import de.pitchMen.shared.bo.Trait;

/**
 * Klasse für die Bereitstellung eines Formulars
 * zum Anzeigen und Bearbeiten von Ausschreibungen.
 * 
 * @author Leon
 */

public class JobPostingForm extends Formular{
 
	private Project parentProject = null;
	private JobPosting selectedJobPosting = null; 
	private PartnerProfile partnerProfileOfJobPosting = null;
	private PitchMenTreeViewModel pitchMenTreeViewModel = null;
	private ArrayList<Trait> jobPostingTraits = null;
	private FlexTable applicationTable  = null;
	Label idLabel = new Label();
	Label titleLabel = new Label("Name des Projektes:");
	Label titleBox = new Label();
	Label descLabel = new Label("Beschreibung des Projektes:");
	Label descBox = new Label();
	Label statusLabel = new Label("aktueller Status: "); 
	Label statusBox = new Label(); 
	Label deadlineLabel = new Label("Deadline: "); 
	Label deadlineBox = new Label(); 
	HorizontalPanel buttonsPanel = null;
	
	public JobPostingForm(JobPosting jobPosting) {
		
		RootPanel.get("content").clear();
		RootPanel.get("content").add(new HTML("<div class='lds-dual-ring'><div></div></div>"));
		
		ClientsideSettings.getPitchMenAdmin().getJobPostingByID(jobPosting.getId(), new JobPostingCallback());
	}
	
	private class JobPostingCallback implements AsyncCallback<JobPosting> {

		@Override
		public void onFailure(Throwable caught) {
			ClientsideSettings.getLogger().severe("Empfangen der Ausschreibung fehlgeschlagen");
		}

		@Override
		public void onSuccess(JobPosting result) {
			
			selectedJobPosting = result;
			
			ClientsideSettings.getPitchMenAdmin().getPartnerProfilesByJobPostingId(selectedJobPosting.getId(), new AsyncCallback<PartnerProfile>() {

				@Override
				public void onFailure(Throwable caught) {
					ClientsideSettings.getLogger().severe("Empfangen des Partner-Profils fehlgeschlagen");
				}

				@Override
				public void onSuccess(PartnerProfile result) {
					
					partnerProfileOfJobPosting = result;
					
					ClientsideSettings.getPitchMenAdmin().getTraitsByPartnerProfileId(partnerProfileOfJobPosting.getId(), new AsyncCallback<ArrayList<Trait>>() {

						@Override
						public void onFailure(Throwable caught) {
							ClientsideSettings.getLogger().severe("Empfangen der Traits fehlgeschlagen");
						}

						@Override
						public void onSuccess(ArrayList<Trait> result) {
							
							jobPostingTraits = result;
							
							ClientsideSettings.getPitchMenAdmin().getProjectByID(selectedJobPosting.getProjectId(), new AsyncCallback<Project>() {

								@Override
								public void onFailure(Throwable caught) {
									ClientsideSettings.getLogger().severe("Fehler beim Empfangen der Daten");
								}

								@Override
								public void onSuccess(Project result) {
									
									parentProject = result;
									
									RootPanel.get("content").clear();
									
									HorizontalPanel topPanel = new HorizontalPanel();
									
									topPanel.add(new HTML("<h2>Ausschreibung: <em>" + selectedJobPosting.getTitle() + "</em></h2>"));
									
									if (hasPermission(parentProject)) {
										RootPanel.get("content").add(new HTML("<div class='info'><p><span class='fa fa-info-circle'></span> Sie sind Besitzer dieser Ausschreibung. "
												+ "Sie können sich daher nicht bewerben. Nur Sie sehen die unten aufgeführten Bewerbungen.</p></div>"));
									
										/* ---------- Neue Ausschreibung-Button, ClickHandler hinzufügen und dem HorizontalPanel hinzufügen */
										Button addJobPostingBtn = new Button("Neue Ausschreibung in diesem Projekt hinzufügen");
										addJobPostingBtn.addClickHandler(new addJobPostingClickHandler());
										topPanel.add(addJobPostingBtn);
										
										/* ---------- Ausschreibung löschen, ClickHandler hinzufügen und dem HorizontalPanel hinzufügen */
										Button deleteJobPostingBtn = new Button("Ausschreibung löschen");
										deleteJobPostingBtn.setStyleName("delete");
										deleteJobPostingBtn.addClickHandler(new deleteJobPostingClickHandler());
										topPanel.add(deleteJobPostingBtn);

										/* ---------- Ausschreibung bearbeiten, ClickHandler hinzufügen und dem HorizontalPanel hinzufügen */
										Button updateJobPostingBtn = new Button("Ausschreibung bearbeiten");
										updateJobPostingBtn.addClickHandler(new updateJobPostingClickHandler());
										topPanel.add(updateJobPostingBtn);
									
									} else {
										/* ---------- Bewerben-Button, ClickHandler hinzufügen und dem HorizontalPanel hinzufügen */
										Button applicateButton = new Button ("Hier Bewerben"); 
										applicateButton.addClickHandler(new applicateClickHandler());
										topPanel.add(applicateButton);
									}
									
									RootPanel.get("content").add(topPanel);
									
									RootPanel.get("content").add(new HTML("<h3>Übergeordnetes Projekt</h3><p>" + parentProject.getTitle() + "</p>"));
									
									RootPanel.get("content").add(new HTML("<h3>Ausschreibungstext</h3><p> " + selectedJobPosting.getText() + "</p>"));
									
									RootPanel.get("content").add(new HTML("<h3>Angehängtes Partnerprofil</h3>"));
									
									FlexTable traitTable = new FlexTable();
									traitTable.setStyleName("traits");
									
									for(final Trait trait : jobPostingTraits) {
										int rowCount = traitTable.getRowCount();
										traitTable.setWidget(rowCount, 0, new HTML("<p><strong>" + trait.getName() + "</strong></p>"));
										traitTable.setWidget(rowCount, 1, new HTML("<p>" + trait.getValue() + "</p>"));
									}
									
									RootPanel.get("content").add(traitTable);
									
									if(hasPermission(parentProject)) {
										RootPanel.get("content").add(new HTML("<h3>Bewerbungen </h3>"));
										ClientsideSettings.getPitchMenAdmin().getApplicationsByJobPostingId(selectedJobPosting.getId(), new AsyncCallback<ArrayList<Application>>() {

											@Override
											public void onFailure(Throwable caught) {
												ClientsideSettings.getLogger().severe("Konnte Ausschreibungen nicht empfangen");
											}

											@Override
											public void onSuccess(ArrayList<Application> applications) {
												
												applicationTable = new FlexTable();
												
												for(final Application app : applications) {
													ClientsideSettings.getPitchMenAdmin().getPartnerProfileByID(app.getPartnerProfileId(), new AsyncCallback<PartnerProfile>() {

														@Override
														public void onFailure(Throwable caught) {
															ClientsideSettings.getLogger().severe("Konnte PartnerProfil nicht empfangen");
														}

														@Override
														public void onSuccess(PartnerProfile partnerProfile) {
															ClientsideSettings.getPitchMenAdmin().getPersonByID(partnerProfile.getPersonId(), new AsyncCallback<Person>() {

																@Override
																public void onFailure(Throwable caught) {
																	ClientsideSettings.getLogger().severe("Konnte Person nicht empfangen");
																}

																@Override
																public void onSuccess(Person person) {
																	applicationTable.setStyleName("traits");
																	
																	int rowCount = applicationTable.getRowCount();
																	applicationTable.setWidget(rowCount, 0, new HTML("<p><strong>" + person.getFirstName() + " " + person.getName() + "</strong></p>"));
																	applicationTable.setWidget(rowCount, 1, new HTML("<p>" + app.getText().substring(0, 200) + " [...]</p>"));
																	applicationTable.setWidget(rowCount, 2, new HTML("<p>" + app.getStatus() + "</p>"));
																	applicationTable.setWidget(rowCount, 3, new Button("Details"));
																
																	RootPanel.get("content").add(applicationTable);
																
																}
																
															});
														}
														
													});
												}
											}
											
										});
									}
								}
								
							});
						}
						
					});
				}
				
			});
		}	
	}


		// ---------- ClickHandler
	
	// ---------- addJobPostingClickHandler
		private class addJobPostingClickHandler implements ClickHandler {

			public void onClick(ClickEvent event) {

				AddJobPostingForm addJobPosting = new AddJobPostingForm(selectedJobPosting,pitchMenTreeViewModel,true);

			}
		}
	
		// ---------- deleteJobPostingClickHandler
		private class deleteJobPostingClickHandler implements ClickHandler {

			public void onClick(ClickEvent event) {

				// bei Click wird die delete() Methode aufgerufen

				if (Window.confirm("Sind Sie sich sicher, dass Sie das löschen wollen?")) {
					delete();
				}

			}
		}

		
		// ---------- updateJobPostingClickHandler
		private class updateJobPostingClickHandler implements ClickHandler {
			public void onClick(ClickEvent event) {

				// bei Click wird die update() Methode aufgerufen
				AddJobPostingForm updateJobPosting = new AddJobPostingForm(selectedJobPosting,pitchMenTreeViewModel,false);

			}
		} 
		private class applicateClickHandler implements ClickHandler {
			public void onClick(ClickEvent event) {

				ApplicationForm applicationForm = new ApplicationForm(selectedJobPosting);

			}
		} 
		
		

		public void delete() {
			
			super.getPitchMenAdmin().deleteJobPosting(selectedJobPosting, new DeleteJobPostingCallback(selectedJobPosting));


		}

		class DeleteJobPostingCallback implements AsyncCallback<Void> {

			JobPosting j = null;

			public DeleteJobPostingCallback(JobPosting j) {
				this.j = j;
			}

			public void onFailure(Throwable caught) {
				Window.alert("Das Löschen der Ausschreibung ist fehlgeschlagen!");

			}

			public void onSuccess(Void result) {
				if (j != null) {
					setSelectedJobPosting(null);
				
					pitchMenTreeViewModel.deleteJobPosting(selectedJobPosting, parentProject);
				}
			}
		}
		
		// ----------pitchMenTreeViewModelsetter
		public void setPitchMenTreeViewModel(PitchMenTreeViewModel pitchMenTreeViewModel) {
			this.pitchMenTreeViewModel = pitchMenTreeViewModel;
		}

		
		
		// ---------- selectedProject setter
				public void setSelectedJobPosting(JobPosting j) {
					if (j != null) {
						this.selectedJobPosting = j;
						titleBox.setText(selectedJobPosting.getTitle());
						descBox.setText(selectedJobPosting.getText()); 						
						statusBox.setText(selectedJobPosting.getStatus());
						deadlineBox.setText(selectedJobPosting.getStatus());
						idLabel.setText(Integer.toString(parentProject.getId()));
					} 
					else {
						idLabel.setText("");
						titleBox.setText("");
						descBox.setText("");
						statusBox.setText("");
						deadlineBox.setText("");
						
					}
				}
	
}
