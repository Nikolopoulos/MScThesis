/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Infrastructure;

/**
 *
 * @author billaros
 */
public class Communication {
    private Request request;
    private String answer;
    private int requestType;
    private int responseType;

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int type) {
        this.requestType = type;
    }

    public int getResponseType() {
        return responseType;
    }

    public void setResponseType(int response_type) {
        this.responseType = response_type;
    }
    
    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Communication() {
    }
    
    
    
    
}
