/*
 * Copyright 2015 Enamul.
 *
 * Most of my softwares are open for educational purpose, but some are 
 * confidential. So, before using it openly drop me some lines at
 *
 *      enamsustcse@gmail.com
 *
 * I do not guarantee that the software would work properly. There could
 * remain bugs. If you found any of them, kindly report me.
 * If you need to use this or some part of it, use it at your own risk.
 * This software is not a professionally developed, so commercial use 
 * is not approved by default.
 */
package security;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Hashing is mainly used for matching password or relative things. It would
 * generate a hash value of the given string.
 * @since 1.0
 * @version 1.0
 * @author Enamul
 */
public class Hashing {
    /**
     * Gets a string and returns its hash value.
     * @param data original string.
     * @return hash value of the given string.
     */
    public static String sha512Hex(String data) {
        return DigestUtils.sha512Hex(data);
    }
}
