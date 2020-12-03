package com.csce4623.ahnelson.restclientexample;

import java.io.Serializable;

public class Address implements Serializable {

        private Geo geo;

        public Geo getGeo(){
            return geo;
        }
        public void setGeo(Geo geo){
            this.geo = geo;
        }

}


