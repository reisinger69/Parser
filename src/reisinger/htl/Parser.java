package reisinger.htl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.*;

public class Parser {

    Stack<String> startTags = new Stack<>();
    Stack<String> endTags = new Stack<>();

    StringBuilder content = new StringBuilder();

    List<String> parsParallel(String file) throws IOException, InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();

        List<PasrerThread> threads = new ArrayList<>();
        BufferedReader bf = new BufferedReader(new FileReader(file));
        while(true) {
            String newLIne = bf.readLine();
            if (newLIne == null) {
                break;
            }
            PasrerThread t = new PasrerThread(newLIne);
            threads.add(t);
        }
        ArrayList<String> lines = new ArrayList<>();
        for (Future<String> f:
                es.invokeAll(threads)) {
            lines.add(f.get());
        }
        es.shutdown();

        return lines;
    }

    ArrayList<String> pars(String file) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader bf = new BufferedReader(new FileReader(file));
        while(true) {
            String newLIne = bf.readLine();
            if (newLIne == null) {
                break;
            }
            StringBuilder tag = new StringBuilder();
            char[] line = newLIne.toCharArray();
            for (int i = 0; i < line.length; i++) {
                if (line[i] == '<') {
                    if (line[i+1] == 'H' && line[i+2] == 'R') {
                        content.append("Horizontal Line ");
                        i += 2;
                    }
                    else if (line[i+1] == '/') {
                        i++;
                        for (int j = i + 1; line[j] != '>'; j++) {
                            tag.append(line[j]);
                            i++;
                        }
                        endTags.add(tag.toString());
                        tag.setLength(0);
                        if (startTags.peek().equals(endTags.peek())) {
                            startTags.pop();
                            endTags.pop();
                            if (content.length() > 0 && startTags.size() == 0) {
                                lines.add(content.toString());
                                content.setLength(0);
                            }

                        } else{
                            content.setLength(0);
                            content.append("Fehler beim Tag ").append(startTags.peek()).append(" ");
                            System.out.println(startTags.pop());
                            System.out.println(endTags.pop());
                        }
                    } else {
                        for (int j = i + 1; line[j] != '>'; j++) {
                            tag.append(line[j]);
                            i++;
                        }
                        startTags.add(tag.toString());
                        tag.setLength(0);
                    }
                } else if(startTags.size() > 0) {
                    if (line[i] != '>') content.append(line[i]);
                }
            }
            if (!content.isEmpty()) {
                lines.add(content.toString());
                content.setLength(0);
            }

        }
        return lines;
    }
}
