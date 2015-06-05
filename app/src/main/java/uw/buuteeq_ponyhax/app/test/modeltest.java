package uw.buuteeq_ponyhax.app.test;

import junit.framework.TestCase;

import db.User;

/**
 * Created by eduard_prokhor on 6/4/15.
 */
public class modeltest extends TestCase {

    private User myUser;

    public void setUp(){
        myUser = new User();
    }

    public void testConstructor() {
        User user = new User();
        assertNotNull(user);
    }

    public void testSetEmail() {
        myUser.setEmail("yourmomworks@hooters.com");
        assertEquals("yourmomworks@hooters.com", myUser.getEmail());
    }

    public void testSetNullEmail() {
        try {
            myUser.setEmail(null);
            fail("Email can be set to null");
        } catch (IllegalArgumentException e){
        }
    }

    public void testSetPassword() {
        try {
            myUser.setPassword("1234");
            fail("Password is to short");
        } catch (IllegalArgumentException e ) {
        }
    }

    public void testGetPassword() {
        myUser.setPassword("123456");
        assertEquals("123456", myUser.getPassword());
    }

    public void testSetSecurityQuestion() {
        myUser.setSecurityQuestion("Whats your moms number?");
        assertEquals("Whats your moms number?", myUser.getSecurityQuestion());
    }

    public void testSetSecurityAnswer() {
        myUser.setSecurityAnswer("2539119111");
        assertEquals("2539119111", myUser.getSecurityAnswer());
    }
}
