package com.ourteams.window;

import java.awt.Color;

import javax.swing.JPanel;

import com.ourteams.backend.Assignment;
import com.ourteams.backend.Student;
import com.ourteams.backend.User;


public class AssignmentPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel currentPanel;
	private User user;
	
	public AssignmentPanel(User user) {
		this.user = user;
		initialize();
	}

	private void initialize() {
		this.setLayout(null);
		this.setBounds(75, 42, 1149, 687);
		this.currentPanel = new ViewAssignmentsPanel(this);
		this.setBackground(new Color(33, 32, 32));
		
		
		this.add(currentPanel);
		

	}
	
	public void openAssignment(Assignment assignment) {
		this.remove(currentPanel);
		if (user instanceof Student){
            this.currentPanel = new StudentAssignmentPanel(user, assignment);
         
        }
        else{
            this.currentPanel = new FacultyAssignmentPanel(this, assignment);
        }

		this.add(currentPanel);
        this.repaint();
        this.revalidate();	
	}
	
	public void refreshViewAssignment() {
		this.remove(currentPanel);
		currentPanel = new ViewAssignmentsPanel(this);
		this.add(currentPanel);
		repaint();
		revalidate();
	}
	

	public User getUser() {
		return user;
	}
	
	
	
	

	
	
	
	
	
	
	
	
	
}
