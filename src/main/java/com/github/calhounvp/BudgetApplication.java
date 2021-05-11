package com.github.calhounvp;


import com.github.calhounvp.services.UserManager;
import com.github.calhounvp.tools.TerminalTool;

/***
 * The BudgetApplication program implements an application that
 * allows the user to make spending requests with their userName.
 * It will check for past spending in the running month to see
 * if the user has budget left, even if the budget is all spent,
 * a spend request can be entered but the automatic mail from the user
 * needs to be confirmed by the superior outside of the program.
 *
 * @author CalhounVP
 * @version 1.0
 * @since 07 may 2021
 */
public class BudgetApplication {
    public static void main(String[] args) {
        //local variables
        UserManager manager;
        String userName;

        //statements
        //At startup ask for a username and use this to make the UserManager
        userName = TerminalTool.startUpMessage();
        manager = new UserManager(userName);

        //will welcome the user to the application and give info around current period budget
        TerminalTool.welcomeMessage(manager.getCurrentUser());

        //checking if there were pending requests from previous uses for the current period
        manager.checkingOffPendingRequests();

        //letting the user make a new request and checking off the request automatic status
        manager.processingNewRequest();

        //writing away files before shutting down application
        manager.documentationFinalisation();

    }
}
