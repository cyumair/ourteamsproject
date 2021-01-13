package com.ourteams.backend;

public class Faculty extends User{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Faculty(){
		
	}
	
	public Faculty(String name, String email, String password, String department) {
        super(name, email, password, department);
    }

    
    public void createNewAssignmnet(String title, String detail, double points, Date date, int teamindex) {
    	Assignment assign = new Assignment(title, date, detail, this.getTeams().get(teamindex), points);
    	this.getTeams().get(teamindex).addAssignment(assign);
    	Team.updateTeamsData(this.getTeams().get(teamindex));
    	for(User u: this.getTeams().get(teamindex).getParticipants()) {
    		u.AddNotification("Assignment has been mentioned: " + title + ", " + this.getTeams().get(teamindex).getName() +
    				", Due date: " + date.getFormattedDate() + "."+ "\n" + this.getcurrentTime());
    		User.updateUserData(u);
    	}
    }

	public void returnAssignment(AssignmentData data, Assignment assignment, double marks) {
		data.setObtainedPoints(marks);
		data.setMarkedby(this.getEmail());
		Team.updateTeamsData(assignment.getTeam());
		data.getStudent().AddNotification("Assignment returned" + assignment.getTitle() + 
				", Obtained "+ marks + " points."+ "\n" + this.getcurrentTime());
		User.updateUserData(data.getStudent());
	}

	public void deleteAssignment(Assignment assignment) {
		assignment.getTeam().getAssignments().remove(assignment);
		Team.updateTeamsData(assignment.getTeam());
		
	}

	public void updateAssignment(Assignment assignment,String newDay,String newMonth,String newYear,
								 String newTitle,String newDetail,String newMin,String newHours,double newpoints){
	

		assignment.setDetail(newDetail);
		assignment.setTitle(newTitle);
		assignment.setTotalPoints(newpoints);
		Date d1 = new Date(newDay, newMonth, newYear, newHours, newMin);
		assignment.setDueDate(d1);
		Team.updateTeamsData(assignment.getTeam());

	}

	@Override
	public String toString() {
		return super.toString();
	}
    

}
