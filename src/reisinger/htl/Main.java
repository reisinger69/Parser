package reisinger.htl;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {
	    Parser p = new Parser();
        long nano1 = System.nanoTime();
        p.parsParallel("Files/sample1.html");
        long nano2 = System.nanoTime();
        System.out.println((nano2-nano1)/10000);
        long nano3 = System.nanoTime();
        p.pars("Files/sample1.html");
        long nano4 = System.nanoTime();
        System.out.println((nano4-nano3)/10000);
    }
}
