package com.ourteams.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class User implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	private ArrayList<Team> teams;
    private String name;
    private String email;
    private String password;
    private String department;
    
    
    private ArrayList<String> notifications;
    private ArrayList<Chat> chats = new ArrayList<Chat>();

    public User() {
        this.teams = new ArrayList<Team>();
        this.notifications = new ArrayList<String>();

    }
    
    public User(String name, String email, String password, String department)  {
        this.name = name;
        this.email = email;
        this.password = password;
        this.department = department;
        this.teams = new ArrayList<Team>();
        this.notifications = new ArrayList<String>();
    }
    

    
    public ArrayList<Team> getTeams() {
		return teams;
	}

	public void setTeams(ArrayList<Team> teams) {
		this.teams = teams;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
	public ArrayList<Chat> getChats() {
		return chats;
	}

	public void setChats(ArrayList<Chat> chats) {
		this.chats = chats;
	}

	public boolean equals(Object o) {
		if(o instanceof User) {
			User user = (User)o;
			if(this.email.equals(user.getEmail())) {
				return true;
			}
		}
		return false;
	}


	public void join_team(Team team){
		team.addParticipant(this);
    	this.teams.add(team);
    	Team.updateTeamsData(team);
    	this.AddNotification("You have joined the team: " + team.getName() + "\n" + this.getcurrentTime());
    };
    
	public void createTeam(Team newteam) {
		newteam.addParticipant(this);
		this.teams.add(newteam);
		Team.AddTeamtoRecord(newteam);
		this.AddNotification("You created the team: " + newteam.getName() + "\n" + this.getcurrentTime());
		
	}
	
	public void deleteAccount() {	
		User.deleteUser(this);
	}

	public void leaveTeam(Team team) {
		this.teams.remove(team);
		User.updateUserData(this);
		team.removeParticipant(this);
		Team.updateTeamsData(team);
		this.AddNotification("You left the team: " + team.getName()+ "\n" + this.getcurrentTime());
		
	}

	public void postMessage(Team team, String text) {
		team.addPost(text);
		Team.updateTeamsData(team);
	}
	
	public void updatecredentials(String mail, String password) {
		User.updateUserDetails(this.email, mail, password);
		this.setEmail(mail);
		this.setPassword(password);
	}
	
	
	public ArrayList<String> getNotifications() {
		return notifications;
	}
	
	public void AddNotification(String text) {
		this.notifications.add(text);
		
	}
	
	
	@Override
	public String toString() {
		return "User [teams=" + teams + ", name=" + name + ", email=" + email + ", password=" + password
				+ ", department=" + department + ", notifications=" + notifications + ", chats=" + chats + "]";
	}

	//we will use this method to get our notification time
	public String getcurrentTime() {
    	   DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
    	   LocalDateTime now = LocalDateTime.now();  
    	   return dtf.format(now);
	}

	public int startaconversation(User user) {
		boolean found = false;
		for(int i = 0; i < this.chats.size(); i++) {
			if(user.getEmail().equals(this.chats.get(i).getContact().getEmail())){ 
				return i;		
			}
		}
		
		if(!found) {
			ArrayList<String> messagesofsender = new ArrayList<String>();
			
			messagesofsender.add(this.getName() + " has started a  new conversation.");
			Chat senderchat = new Chat(user, messagesofsender);
			this.chats.add(0, senderchat);
			
			ArrayList<String> messagesofreciever = new ArrayList<String>();
			messagesofreciever.add(this.getName() + " has started a  new conversation.");
			
			Chat receiverchat = new Chat(this, messagesofreciever);
			user.chats.add(0, receiverchat);
			
			User.updateUserData(user);
		
		}
		return 0;
		
	}

	public void sendMessage(int contactindex, String message) {
		
		this.getChats().get(contactindex).addMessage(message);
		User contact = User.findUser(this.getChats().get(contactindex).getContact().getEmail());
		for(int i = 0; i < contact.getChats().size(); i++) {
			if(contact.getChats().get(i).getContact().equals(this)) {
				contact.getChats().get(i).addMessage(message);
				break;
			}
		}

		User.updateUserData(contact);
	}
    
     
     //=======================================================================================
     public void updatecredentials(String name, String password,String depart,String semes) {
    	 //Updating data in the file
        User.updateUserDetails(this.email,name,password,depart,semes);
        this.setName(name);
        this.setPassword(password);
        this.setDepartment(depart);
        if (this instanceof Student ) {
            ((Student) this).setSemester(semes);
        }
    }
     
     //=========================================================================================
     public static void updateUserDetails(String currentmail,String name, String password,String depart,String semes) {

        ArrayList<User> users = readAllDataFromFile();
        Scanner input = new Scanner(System.in);
        for (User currentUser : users) {
            if (currentUser.getEmail().equalsIgnoreCase(currentmail)) {
                currentUser.setName(name);
                currentUser.setDepartment(depart);
                currentUser.setPassword(password);
                if (currentUser instanceof Student)
                    ((Student) currentUser).setSemester(semes);

                break;
            }
        }
        input.close();
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("Users Record"));
            output.writeObject(users);
            output.close();
        } catch (IOException e) {
            System.out.println("error");
        }
    }
     
    //========================================================================================
  
     public static User findUser(String mail){
        ArrayList<User> users = User.readAllDataFromFile();
        for (User currentuser:users){
            if (currentuser.getEmail().equalsIgnoreCase(mail))
                return currentuser;
        }
        return null;
    }
     //====================================================================================

    public static void updateUserDetails(String currentmail,String mail,String paassword) {

        ArrayList<User> users = readAllDataFromFile();
        Scanner input = new Scanner(System.in);
        for (User currentUser : users) {
            if (currentUser.getEmail().equalsIgnoreCase(currentmail)) {
                currentUser.setEmail(mail);
                currentUser.setPassword(paassword);
                System.out.println(currentUser.getEmail());
                break;
            }
        }
        input.close();
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("Users Record"));
            output.writeObject(users);
            output.close();
        } catch (IOException e) {
            System.out.println("error");
        }
    }

     //================================================================================
    @SuppressWarnings("unchecked")
	public static ArrayList<User> readAllDataFromFile(){
        ArrayList<User> list=new ArrayList<User>();
        try {
        	File file = new File("Users Record");
			if(!file.isFile()) {
				ObjectOutputStream o2=new ObjectOutputStream(new FileOutputStream("Users Record"));
				o2.writeObject(list);
				o2.close();
			}
            ObjectInputStream o1=new ObjectInputStream(new FileInputStream("Users Record"));
            list = (ArrayList<User>)o1.readObject();
            o1.close();

        }
        catch (Exception e){
        	System.out.println("Error inputing stream");
            e.printStackTrace();
        }
        return list;

    }
     //================================================================================
    public static void updateUserData(User user){
    	ArrayList<User> users = readAllDataFromFile();
        for (User currentUser : users
        ) {
            if (currentUser.getEmail().equalsIgnoreCase(user.getEmail())) {
                users.remove(currentUser);
                users.add(user);
                break;
            }
        }
        try {
            ObjectOutputStream output=new ObjectOutputStream(new FileOutputStream("Users Record"));
            output.writeObject(users);
            output.close();
      
        }
        catch (IOException e){
            System.out.println("error");
            e.printStackTrace();
        }
        

    }
//    ==============================================================================
    public static boolean isValidEmail(String email){
        //alphabet se start ho ro @rate ayeee.
        //ali123@asASA.com;
        int i=email.indexOf("@");
        int idx = email.indexOf(".com");
        if (email.length()==0)
            return false;
        if (!Character.isLetter(email.charAt(0)))
            return false;
        else if (i==-1)
            return false;
        else if (!Character.isLetter(email.charAt(i+1)))
            return false;
        else if (!(email.length()-idx==4))
            return false;
        else
            return true;

        //
    }
//===================================================================================
    public static boolean hasEmailAlreadyBeenUser(String email) {
        ArrayList<User> users = readAllDataFromFile();
        for (User currentUser : users
        ) {
            if (currentUser.getEmail().equalsIgnoreCase(email)) {
                return true;
            }

        }
        return false;
    }
//   =========================================================================================
    public static void AddUser(User u){
        ArrayList<User> users=readAllDataFromFile();
        if (!isValidEmail(u.getEmail())) {
            System.out.println("invalid email");
            return;
        }
        if (hasEmailAlreadyBeenUser(u.getEmail())) {
            System.out.println("email already registered");
            return;
        }
        users.add(u);
        try {
            ObjectOutputStream output=new ObjectOutputStream(new FileOutputStream("Users Record"));
            output.writeObject(users);
            output.close();
        }
        catch (IOException e){
            System.out.println("error");
            e.printStackTrace();
        }

    }
//    ==============================================================================================
    public static User findUser(String mail,String passwrd ){
        ArrayList<User> users = readAllDataFromFile();
        for (User currentuser:users){
            if (currentuser.getEmail().equalsIgnoreCase(mail) && currentuser.getPassword().equals(passwrd))
                return currentuser;
        }
        return null;
    }
//    ============================================================================================


    public static boolean findEmail(User u){
        ArrayList<User> users=readAllDataFromFile();
        for (User currentUser:users ) {
            if (currentUser.getEmail().equalsIgnoreCase(u.getEmail()))
                return true;
        }
        return false;
    }
//    =====================================deleting user==============================================
    public static void deleteUser(User u){
        ArrayList<User> users=readAllDataFromFile();
        for (User currentUser:users ) {
            if (currentUser.getEmail().equalsIgnoreCase(u.getEmail())) {
                users.remove(currentUser);
                break;
            }

        }
//        updating file data ==============================================
        try {
            ObjectOutputStream output=new ObjectOutputStream(new FileOutputStream("Users Record"));
            output.writeObject(users);
            output.close();
        }
        catch (IOException e){
            System.out.println("error");
        }
    }
    
    public static void updateUserDetails(User u){
        ArrayList<User> users=readAllDataFromFile();
        Scanner input=new Scanner(System.in);
        for (User currentUser:users) {
            if (currentUser.getEmail().equalsIgnoreCase(u.getEmail())){

                System.out.println("enter new password");
                currentUser.setPassword(input.nextLine());
                System.out.println("enter new email");
                currentUser.setEmail(input.nextLine());

                break;
            }
        }
        input.close();
        try {
            ObjectOutputStream output=new ObjectOutputStream(new FileOutputStream("Users Record"));
            output.writeObject(users);
            output.close();
        }
        catch (IOException e){
            System.out.println("error");
        }

    }

	public static boolean searchUser(String text) {
		ArrayList<User> users=readAllDataFromFile();
		for (User currentUser:users) {
            if(currentUser.email.equals(text)) {
            	
            }

        }
		return false;
	}



}
