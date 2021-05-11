package com.github.calhounvp.tools;

import com.github.calhounvp.entities.BudgetPeriod;
import com.github.calhounvp.entities.SpendRequest;
import com.github.calhounvp.entities.User;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.regex.Pattern;

/***
 * This class is purely a tool to centralise everything to do with the terminal,
 * any printout or terminal input goes through this class to whatever class or
 * method that needs it.
 */
public class TerminalTool {
    //__________________________________Properties__________________________________
    private static final ResourceBundle bundle;
    private static Scanner keyboard;

    static {
        bundle = ResourceBundle.getBundle("TerminalResource", Locale.getDefault());
    }

    //____________________________________Methods___________________________________
    //*********************************Utility Method*******************************
    public static String startUpMessage() {
        //local variable
        String userName = "";

        //statements
        //start scanner
        keyboard = new Scanner(System.in);

        System.out.println(bundle.getString("startup"));
        userName = requestUserName();

        return userName;
    }

    public static void welcomeMessage(User user) {
        //local variable
        String pattern = bundle.getString("welcome");

        //statements
        System.out.println(MessageFormat.format(pattern,
                user.getUserName(),
                BudgetPeriod.getInstance().toString(),
                user.getAmountSpent(),
                user.getUserMonthlyLimit()));
    }

    public static void pendingRequest(User user, SpendRequest request) {
        //local variables
        String pendingPattern = bundle.getString("pending");
        String approvedPattern = bundle.getString("approved");

        //statements
        System.out.println(MessageFormat.format(pendingPattern,
                request.getRequestInfo(),
                "\n"));

        //adjust status if needed
        switch (requestPendingStatusCheck()) {
            case 1:
                request.setStatus(SpendRequest.Status.APPROVED);
                user.addToSpending(request);
                System.out.println(MessageFormat.format(approvedPattern,
                        String.format("%.2f", user.getAmountSpent()
                                .doubleValue())));
                break;
            case 2:
                request.setStatus(SpendRequest.Status.REJECTED);
                break;
        }
    }

    public static SpendRequest newRequest() {
        //local variables
        String budgetPattern = bundle.getString("newBudget");
        String reasonPattern = bundle.getString("newReason");
        BigDecimal requestBudget;
        String requestReason;

        //statements
        System.out.println(budgetPattern);
        requestBudget = requestCostInput();
        System.out.println(reasonPattern);
        requestReason = requestReasonInput();

        //close off scanner
        keyboard.close();

        return new SpendRequest(requestReason, requestBudget);
    }

    public static void approvedRequest(User user) {
        //local variables
        String approvedPattern = bundle.getString("newApproved");
        String amountSpent = String.format("%.2f", user.getAmountSpent().doubleValue());

        //statement
        System.out.println(MessageFormat.format(approvedPattern, amountSpent));
    }

    public static void confirmationNeededRequest() {
        System.out.println(bundle.getString("newPending"));
    }


    //********************************Internal Method*******************************
    private static int requestPendingStatusCheck() {
        //local variables
        int selection = -1;

        //statements
        while (selection < 0 || selection > 2) {
            try {
                selection = keyboard.nextInt();
                if (selection < 0 || selection > 2) {
                    throw new RuntimeException("invalid choice");
                }
            } catch (Exception ex) {
                System.out.println("Invalid input, please input a number between 0-2");
            }
        }
        return selection;
    }

    private static BigDecimal requestCostInput() {
        //local variables
        double input = 0.0;

        //statements
        while (input <= 0.0) {
            try {
                input = keyboard.nextDouble();
            } catch (Exception ex) {
                System.out.println("Invalid input, please enter a number");
            }
        }
        return BigDecimal.valueOf(input);
    }

    private static String requestReasonInput() {
        //local variables
        String reason = "";
        Scanner kbd = new Scanner(System.in);

        //statements
        while (reason.isBlank()) {
            try {
                reason = kbd.nextLine().strip();
                if (reason.length() == 0) {
                    throw new Exception("Empty input");
                }
            } catch (Exception ex) {
                System.out.println("Invalid or empty input, please input a reason");
            }
        }
        return reason;
    }

    private static String requestUserName() {
        //local variables
        Pattern pattern = Pattern.compile("[^a-zA-Z. ]");
        String userName = "";

        //statements
        //if username is empty or has a symbol other than a-z, '.' or ' ' it will ask for input
        while (userName.isEmpty() || pattern.matcher(userName).find()) {
            try {
                userName = keyboard.nextLine().strip();
                if (pattern.matcher(userName).find()) {
                    throw new Exception("Invalid character in username, try again");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        return userName;
    }


}
