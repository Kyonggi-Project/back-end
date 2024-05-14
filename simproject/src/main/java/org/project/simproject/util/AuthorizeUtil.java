package org.project.simproject.util;

public class AuthorizeUtil {
    public static boolean authorizeByAuthor(String author, String userNickname){
        if(author.equals(userNickname)) return true;
        else return false;
    }
}
