package com.medcorp.nevo.validic.model;


import java.io.Serializable;

/**
 * Created by gaillysu on 16/3/8.
 */
public class ValidicUser{
        private String code;
        private String message;
        private ValidicUserBase user;

        public String getCode() {
                return code;
        }

        public void setCode(String code) {
                this.code = code;
        }

        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }

        public ValidicUserBase getUser() {
                return user;
        }

        public void setUser(ValidicUserBase user) {
                this.user = user;
        }
}
