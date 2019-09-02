package com.nevs.car.tools.util;

public class InterfaceUtils {
    public interface CSRListeners{
        void connect();
    }
    public static CSRListeners csrListeners1;
    public static void setCsrListeners(CSRListeners csrListeners){
        csrListeners1=csrListeners;
    }
    public static void toSends(){
        if(csrListeners1!=null){
            csrListeners1.connect();
        }
    }
}
