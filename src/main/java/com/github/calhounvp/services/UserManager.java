package com.github.calhounvp.services;

import com.github.calhounvp.entities.SpendRequest;
import com.github.calhounvp.entities.User;
import com.github.calhounvp.tools.TerminalTool;

import java.util.ArrayList;

/***
 * The UserManager is where it all is brought together,
 * it keeps track of all the users that had been on log
 * and does the communicating with the main mostly on it's own.
 */
public class UserManager {
    //__________________________________Properties__________________________________
    private ArrayList<User> users;
    private final User currentUser;
    private final FileManager fileManager;
    private final JsonService jsonService;

    //_________________________________Constructors_________________________________
    public UserManager(String userName) {
        //local variables
        this.currentUser = new User(userName, null);
        this.fileManager = new FileManager(currentUser);
        this.jsonService = new JsonService();

        //statements
        //will check if there was already a list of users and then set the list
        this.setUsers();

        //will check if there was already a record for this period then set the list
        this.setCurrentUserRequests();
    }

    //____________________________________Methods___________________________________
    //************************************getters***********************************
    public User getCurrentUser() {
        return currentUser;
    }

    //*********************************Utility Method*******************************
    public void checkingOffPendingRequests() {
        //local variable
        ArrayList<SpendRequest> pendingRequests = this.getPendingRequests();

        //statements
        //if there was a record for the user at startup it will pull up the requests
        if (fileManager.isStartupUserRecordExists()) {
            //does the check for every pendingRequest if there are any
            pendingRequests.forEach(request -> TerminalTool.pendingRequest(currentUser, request));
        }

    }

    public void processingNewRequest () {
        //local variable
        SpendRequest newRequest;

        //statements
        newRequest = TerminalTool.newRequest();
        this.currentUser.addSpendRequest(newRequest);
    }

    public void documentationFinalisation () {
        this.fileManager.writeUserFile(users);
        this.fileManager.writeUserRecordFile(currentUser.getCurrentRequests());
    }

    //********************************Internal Method*******************************
    private void setUsers () {
        if (fileManager.isStartupUsersFileExists()) {
            this.users = jsonService.getUserList(fileManager.getUserFileReader());
            this.addNewUserOnly();
        }
        else {
            this.users = new ArrayList<>();
            users.add(currentUser);
        }
    }

    private void addNewUserOnly() {
        if (!users.contains(currentUser)) {
            this.users.add(currentUser);
        }
    }

    private void setCurrentUserRequests() {
        if (fileManager.isStartupUserRecordExists()) {
            currentUser.setCurrentRequests(jsonService.getSpendRequestList(
                                                fileManager.getUserRecordReader()));
        }
    }

    private ArrayList<SpendRequest> getPendingRequests() {
        //local variable
        ArrayList<SpendRequest> pendingRequests = new ArrayList<>();

        //statements
        this.currentUser.getCurrentRequests()
                        .stream()
                        .filter(request -> request.getStatus().equals(SpendRequest.Status.PENDING))
                        .forEach(pendingRequests::add);

        return pendingRequests;
    }

}
