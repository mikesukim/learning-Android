package com.csce4623.ahnelson.restclientexample;

import java.io.Serializable;

public class User implements Serializable {

        private int id;
        private String name;
        private String username;
        private String email;
        private String website;
        private String phone;
        private Address address;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String name) {
            this.username = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getPhone() {
        return phone;
    }

        public void setPhone(String phone) {
        this.phone = phone;
    }

        public Address getAddress() {return address;}

        public void setAddress(Address address) {this.address = address;}



    }


