package com.example.jdtractorcontrol.helperClasses;

public class unsignedInt {
    public int value;

    public unsignedInt(byte number){
        this.value =  number & 0xff;
    }

    @Override
    public String toString() {
        return "unsignedInt{" +
                "value=" + value +
                '}';
    }

    public boolean isEqualTo(int num){
        if(num == value){
            return true;
        }else {
            return false;
        }
    }
}
