package reisinger.htl;

import java.util.Stack;
import java.util.concurrent.Callable;

public class PasrerThread implements Callable<String> {

    private String newLine;

    public PasrerThread(String line) {
        this.newLine = line;
    }

    Stack<String> startTags = new Stack<>();
    Stack<String> endTags = new Stack<>();
    StringBuilder content = new StringBuilder();

    @Override
    public String call() throws Exception {
        StringBuilder returnString = new StringBuilder();
        StringBuilder tag = new StringBuilder();
        char[] line = newLine.toCharArray();
        for (int i = 0; i < line.length; i++) {
            if (line[i] == '<') {
                if (line[i+1] == 'H' && line[i+2] == 'R') {
                    content.append("Horizontal Line");
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
                            returnString.append(content);
                            content.setLength(0);
                        }

                    } else{
                        returnString.append("Fehler beim Tag ").append(startTags.peek()).append(" ");
                        startTags.pop();
                        endTags.pop();
                        content.setLength(0);
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
            returnString.append(content);
            content.setLength(0);
        }


        return returnString.toString();
    }
}
