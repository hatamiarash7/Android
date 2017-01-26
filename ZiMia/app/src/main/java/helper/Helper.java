/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package helper;

public class Helper {
    private String Email_Address;

    public Helper(String Address) {
        this.Email_Address = Address;
    }

    public boolean isValidEmail() {
        return Email_Address.endsWith("@gmail.com") || Email_Address.endsWith("@yahoo.com") || Email_Address.endsWith("@live.com")
                || Email_Address.endsWith("@outlook.com");
    }
}
