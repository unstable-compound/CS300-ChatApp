package com.muc;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeSet;

public class AccountManager {
    private FileOutputStream fileOutputStream;
    private FileInputStream fileInputStream;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private String fileName = "Accounts.txt";
    private ArrayList<Account> accounts = new ArrayList<>();

    private class Account {
        private String username = null;
        private String password = null;

        public Account(String loginName, String password) {
            this.username = loginName;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    private boolean isValid(String login) {
        boolean valid = true;
        int length = accounts.size();
        for (int i = 0; i < length; ++i) {
            if (login.equalsIgnoreCase(accounts.get(i).getUsername()))
                valid = false;
        }
        return valid;
    }

    public boolean addAccount(String loginName, String password) {
        //check that the account is valid
        boolean success;
        if (success = isValid(loginName)) {
            accounts.add(new Account(loginName, password));
        }
        return success;
    }


    public boolean writeToFile() {
        boolean success = false;

        int length = accounts.size();
        try {
            fileOutputStream = new FileOutputStream(fileName);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            for (int i = 0; i < length; ++i) {
                objectOutputStream.writeObject(accounts.get(i));

            }
            success = true;

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            success = false;
        } catch (IOException e) {
            System.out.println("Error initializing stream");
            success = false;
        }
        return success;
    }




     /*   f = new FileOutputStream(new File("myObjects.txt"));
    ObjectOutputStream o = new ObjectOutputStream(f);

    // Write objects to file
			o.writeObject(p1);
			o.writeObject(p2);

			o.close();
			f.close();
			*/
     public static void main(String []args){

     }
}
