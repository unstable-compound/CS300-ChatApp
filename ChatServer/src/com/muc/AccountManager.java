package com.muc;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeSet;

public class AccountManager implements Serializable {
    //private static final File file = new File("Accounts.txt");
    private FileOutputStream fileOutputStream;
    private FileInputStream fileInputStream;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private String fileName = "Accounts.txt";

    private ArrayList<Account> accounts = new ArrayList<>();

    private class Account implements Serializable {
        private String username;
        private String password;

        public Account() {
            username = null;
            password = null;
        }

        public Account(String loginName, String password) {
            this.username = loginName;
            this.password = password;
        }
        boolean isMatch(String user, String password)
        {
            if(this.username.equals(user) && this.password.equals(password))
                return true;
            else
                return false;
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

    private boolean isValidNewUsername(String login){ //helper for addAccount{
        boolean valid = true;
        int length = accounts.size();
        for (int i = 0; i < length; ++i) {
            if (login.equalsIgnoreCase(accounts.get(i).getUsername()))
                valid = false;
        }
        return valid;
    }

    public boolean displayAllUserNames(){// For testing purposes
        boolean retval = false;
        for(int i = 0; i < accounts.size(); ++i)
        {
            System.out.println("Name " + i+1 + ": "+ accounts.get(i).getUsername());
            retval = false;
        }
        return retval;
    }

    public boolean isValidLogin(String login, String password) {
        int length = accounts.size();
        boolean isValid = false;
        for (int i = 0; i < length; ++i) {
            if (accounts.get(i).isMatch(login, password)) {
                isValid = true;
                i = length;//exit loop
            }
        }
        return isValid;
    }


    public boolean addAccount(String loginName, String password) {
        //check that the account is valid
        boolean success;
        if (success = isValidNewUsername(loginName)) {
            accounts.add(new Account(loginName, password));
        }
        return success;
    }


    public boolean writeToFile() {
        boolean success = false;

        int length = accounts.size();
        try {
            fileOutputStream = new FileOutputStream(new File(fileName));
            //
            //FileWriter fileWriter = new FileWriter(fileName);
            //
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            // for (int i = 0; i < length; ++i) {


            objectOutputStream.writeObject(accounts);
            success = true;
            fileOutputStream.close();
            objectOutputStream.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            success = false;
        } catch (IOException e) {
            System.out.println("Error initializing stream");
            success = false;
        }
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean readFromFile() {
        boolean success = false;
        //Account to_add;
        File file;
        file = new File(fileName);
        if(file.exists() && file.canRead()) {
            try {
                fileInputStream = new FileInputStream(fileName);
                objectInputStream = new ObjectInputStream(fileInputStream);
                // while ((to_add = (Account) objectInputStream.readObject()) != null) {
                //    accounts.add(to_add);
                //    success = true;
                // }

                // accounts = new ArrayList<>();
                accounts.addAll((ArrayList<Account>) objectInputStream.readObject());


                success = true;


                fileInputStream.close();
                objectInputStream.close();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();

            }
        }

       /* try {
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
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
    public static void main(String[] args) {
        AccountManager manager = new AccountManager();
         manager.addAccount("culewis", "CurtisL");
         manager.writeToFile();

        AccountManager manager1 = new AccountManager();
        boolean success = manager1.readFromFile();


        success = success;

    }
}
