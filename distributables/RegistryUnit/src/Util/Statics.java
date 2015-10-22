/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

/**
 *
 * @author billaros
 */
public class Statics {

    public static int REGISTER_REQUEST = 1;
    public static int DELETE_REQUEST = 2;
    public static int UPDATE_REQUEST = 3;

    public static int SERVICES_REQUEST = 4;
    public static int AGGREGATORS_REQUEST = 5;
    public static int DESCRIBE_REQUEST = 6;

    public static int OK_RESPONSE = 200;

    public static int BAD_REQUEST_ERROR = 400;
    public static int PAYMENT_REQUIRED_ERROR = 402;
    public static int NOT_PERMITTED_ERROR = 403;
    public static int NOT_FOUND_ERROR = 404;
    public static int METHOD_NOT_ALLOWED_ERROR = 405;
    public static int GONE_ERROR = 410;
    public static int IM_A_TEAPOT_ERROR = 418;
    public static int TOO_MANY_REQUESTS_ERROR = 429;

    public static int INTERNAL_SERVER_ERROR = 500;
    public static int NOT_IMPLEMENTED_ERROR = 501;
    public static int SERVICE_UNAVAILABLE_ERROR = 501;

}
