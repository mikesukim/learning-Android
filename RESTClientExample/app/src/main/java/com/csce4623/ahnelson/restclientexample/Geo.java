package com.csce4623.ahnelson.restclientexample;

import java.io.Serializable;

public class Geo implements Serializable {

        private double lat;
        private double lng;

        public double getLat(){
            return lat;
        }
        public double getLng(){
            return lng;
        }
        public void setLat(double lat){
            this.lat = lat;
        }
        public void setLng(double lng){
            this.lng = lng;
        }
}


