/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package helper;

public class Helper {
    private String Email_Address;
    private String Phone_Number;

    public Helper(String TAG, String VALUE) {
        switch (TAG) {
            case "email":
                this.Email_Address = VALUE;
                break;
            case "phone":
                this.Phone_Number = VALUE;
                break;
        }
    }

    public boolean isValidEmail() {
        return Email_Address.endsWith("@gmail.com") || Email_Address.endsWith("@yahoo.com") || Email_Address.endsWith("@live.com")
                || Email_Address.endsWith("@outlook.com");
    }

    public boolean isValidPhone() {
        return Phone_Number.startsWith("09") && Phone_Number.length() == 11;
    }
}
