package com.github.calhounvp.services;

import com.github.calhounvp.entities.BudgetPeriod;
import com.github.calhounvp.entities.SpendRequest;
import com.github.calhounvp.entities.User;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/***
 * The filemanager handles all the file I/O and nio aspects,
 * from making the paths, readers and writers to clearing the files
 * for new input, it's all handled in here.
 */
public class FileManager {
    //__________________________________Properties__________________________________
    private final boolean startupUsersFileExists;
    private final boolean startupUserRecordExists;
    private final Path usersFilePath;
    private final Path userRecordPath;

    //_________________________________Constructors_________________________________
    public FileManager (User user) {
        //local variables
        this.usersFilePath = makeFilePath("users.json");
        this.startupUsersFileExists = Files.exists(usersFilePath);
        this.userRecordPath = makeUserRecordPath(user);
        this.startupUserRecordExists = Files.exists(userRecordPath);
    }

    //____________________________________Methods___________________________________
    //************************************getters***********************************
    public boolean isStartupUsersFileExists() {
        return startupUsersFileExists;
    }

    public boolean isStartupUserRecordExists() {
        return startupUserRecordExists;
    }

    //*********************************Utility Method*******************************
    public FileReader getUserFileReader () {
        //local variable
        FileReader reader = null;

        //statements
        try {
            reader = new FileReader(usersFilePath.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return reader;
    }

    public FileReader getUserRecordReader () {
        //local variable
        FileReader reader = null;

        //statements
        try {
            reader = new FileReader(userRecordPath.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return reader;
    }

    public void writeUserFile (ArrayList<User> users) {
        //local variables
        JsonService jsonService = new JsonService();
        String usersJsonString = jsonService.getUsersJsonString(users);

        //statements
        //clearing existing file or making the file
        this.prepareFile(startupUsersFileExists, usersFilePath);

        //writing the actual users away
        try (FileWriter writer = new FileWriter(usersFilePath.toString())){
            writer.write(usersJsonString);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeUserRecordFile (ArrayList<SpendRequest> userSpendRequests) {
        //local variables
        JsonService jsonService = new JsonService();
        String requestsJsonString = jsonService.getRequestJsonString(userSpendRequests);

        //statements
        //clearing existing file or making the file
        this.prepareFile(startupUserRecordExists, userRecordPath);

        //writing the actual records away
        try (FileWriter writer = new FileWriter(userRecordPath.toString())){
            writer.write(requestsJsonString);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //********************************Internal Method*******************************
    private Path makeFilePath(String pathExtension) {
        return Path.of(FileSystemView.getFileSystemView().getDefaultDirectory().getPath(),
                "BudgetManager", pathExtension);
    }

    private Path makeUserRecordPath(User user) {
        //local variables
        String budgetPeriod = BudgetPeriod.getInstance().toString();
        String fileName = user.getUserName() + budgetPeriod + ".json";
        String pathExtension = Path.of(budgetPeriod,fileName).toString();

        //statement
        return makeFilePath(pathExtension);
    }

    private void clearFileContent (Path filePath) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(filePath);
            writer.write("");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createParentDirectory (Path path) {
        try {
            path.toFile().getParentFile().mkdirs();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createFile (Path path) {
        try {
           path.toFile().createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareFile (boolean fileExists, Path path) {
        //check for existence of the file on the path
        if (fileExists) {
            //if the file exists clear the content
            this.clearFileContent(path);
        }
        //if the file doesn't exist check for existence parent directory
        else {
            //if parent directory doesn't exist create it
            if (!path.toFile().getParentFile().exists()) {
                this.createParentDirectory(path);
            }
            //when the directories exist file can be made
            this.createFile(path);
        }
    }

}
