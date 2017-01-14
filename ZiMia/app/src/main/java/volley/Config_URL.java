/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package volley;

public class Config_URL {
    // Main Server
    private static final String base_URL = "http://zimia.ir/";
    // Get Details
    public static final String url_fastfood_detials = base_URL + "get_fastfood_details.php";
    public static final String url_market_detials = base_URL + "get_market_details.php";
    public static final String url_resturan_detials = base_URL + "get_resturan_details.php";
    public static final String url_item_detials = base_URL + "get_item_details.php";
    // Get ALL Products
    public static final String url_all_fastfood_foods = base_URL + "get_all_fastfood_foods.php";
    public static final String url_all_market_products = base_URL + "get_all_market_products.php";
    public static final String url_all_resturan_foods = base_URL + "get_all_resturan_foods.php";
    // User Management
    public static final String url_get_person_detials = base_URL + "users/include/Get_User_Detail.php";
    public static final String url_delete_person = base_URL + "users/include/Del_User_Detail.php";
    public static final String url_set_person_detials = base_URL + "users/include/Set_User_Detail.php";
    public static final String url_login = base_URL + "users/";
    public static final String url_register = base_URL + "users/";
}
