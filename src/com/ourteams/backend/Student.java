package com.ourteams.backend;

import java.io.File;

public class Student extends User{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private String semester;
	public Student() {
		
	};
	
	public Student(String name, String email, String password, String department, String semester) {
        super(name, email, password, department);
        this.semester = semester;
    }
	
   public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}


  //-----------------------------------------------------------------------------------------------------------------------
   public void submitAssignment(File file, Assignment assignment) {
		AssignmentData data = new AssignmentData(this, file);
		boolean found = false;
		for(AssignmentData ad: assignment.getAssignmentData()) {
			if(ad.getStudent().getEmail().equals(data.getStudent().getEmail())) {
				ad.setStudent(this);
				ad.setFile(file);
				found = true;
				break;
			}
		}
		if(!found) {
			assignment.getAssignmentData().add(data);
		}
		Team.updateTeamsData(assignment.getTeam());
    }

@Override
public String toString() {
	return super.toString() + "semester=" + semester;
}
   
   


}
