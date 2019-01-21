package bookstore;

import org.junit.Test;

import static org.junit.Assert.*;

public class AllFieldsEmptyExceptionTest {

    @Test
    public void test(){
        String message="All fields were missing/empty, the entry was not saved";
        Exception e=new AllFieldsEmptyException();
        assertEquals(message,e.getMessage());
        System.out.println("this test");
    }
}
