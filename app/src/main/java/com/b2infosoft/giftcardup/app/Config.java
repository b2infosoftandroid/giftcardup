package com.b2infosoft.giftcardup.app;

/**
 * Created by rajesh on 7/24/2016.
 */
public class Config {
    private final String SERVER_IP = "192.168.0.55";
    public final String GIFT_CARD_IMAGE_FOLDER ="images/upload/";
    private final Boolean LIVE_SERVER = false;
    private static Config ourInstance = new Config();

    public static Config getInstance() {
        return ourInstance;
    }

    private Config() {

    }
    public String getGiftCardImageAddress(){
        if(LIVE_SERVER){
            return "";
        }else{
            return "http://".concat(SERVER_IP).concat("/giftcard2/").concat(GIFT_CARD_IMAGE_FOLDER);
        }
    }
    public String getServerAddress(){
        if(LIVE_SERVER){
            return "";
        }else{
            return "http://".concat(SERVER_IP).concat("/rajesh/giftcard_services/");
        }
    }

}



