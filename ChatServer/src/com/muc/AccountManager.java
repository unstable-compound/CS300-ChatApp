package com.muc;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeSet;

public class AccountManager implements Serializable{
    private static FileOutputStream fileOutputStream;
    private static FileInputStream fileInputStream;
    private static ObjectInputStream objectInputStream;
    private static ObjectOutputStream objectOutputStream;
    private String fileName = "Accounts.txt";
    private ArrayList<Account> accounts = new ArrayList<>();

    private class Account implements Serializable {
        private String username = new String();
        private String password = new String();
        public Account(){
            username = null;
            password = null;
        }

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
            fileOutputStream = new FileOutputStream(new File(fileName));
            //
            //FileWriter fileWriter = new FileWriter(fileName);
            //
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
           // for (int i = 0; i < length; ++i) {


                objectOutputStream.writeObject(accounts);




                //}
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
        return success;
    }
    public boolean readFromFile() {
        boolean success = false;
        Account to_add;
        try {
            fileInputStream = new FileInputStream(fileName);
            objectInputStream = new ObjectInputStream(fileInputStream);
           // while ((to_add = (Account) objectInputStream.readObject()) != null) {
            //    accounts.add(to_add);
            //    success = true;
           // }

           // accounts = new ArrayList<>();
            accounts.addAll((ArrayList<Account>)objectInputStream.readObject());


            success = true;


            fileInputStream.close();
            objectInputStream.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();

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
         AccountManager manager = new AccountManager();
         manager.addAccount("culewis", "CurtisL");
         manager.writeToFile();

         AccountManager manager1 = new AccountManager();
         boolean success = manager1.readFromFile();


         success = success;

     }
}
