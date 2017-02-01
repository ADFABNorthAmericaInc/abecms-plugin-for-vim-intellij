import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.util.Arrays;

/**
 * Created by gg on 31/01/17.
 */
public class CompleteTag extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        Document document = editor.getDocument();
        CharSequence text = document.getCharsSequence();
        int currentPosition = editor.getCaretModel().getOffset();
        int startWord = getStartPositionWord(text, currentPosition);
        CharSequence word = text.subSequence(startWord, currentPosition);
        String tag = generateAbeTag(word.toString());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                document.replaceString(startWord, currentPosition, tag);
            }
        };
        WriteCommandAction.runWriteCommandAction(project, runnable);
    }

    private int getStartPositionWord(CharSequence text, int currentPosition) {
        //Find start and end word
        boolean done = false;
        int i = 1;
        int startWord = currentPosition;
        while(!done && currentPosition - i > 0) {
            char currentChar = text.charAt(currentPosition - i);
            if (Character.isLetter(currentChar) || Character.isDigit(currentChar) || currentChar == ':' || currentChar == '_') {
                startWord--;
                i++;
            } else {
                done = true;
            }
        }
        return startWord;
    }

    private String generateAbeTag(String word) {
        String type = extractType(word);
        String key = extractKey(word);
        String desc = "";
        String tab = "default";
        if (key != "") {
            desc = extractDesc(key);
            tab = extractTab(key);
        }
        return tag(type, key, desc, tab);
    }

    private String tag(String type, String key, String desc, String tab) {
        if (type == "import") {
            return "{{abe type='" + type + "' file='" + key + ".html'}}";
        } else {
            return "{{abe type='" + type + "' key='" + key + "' desc='" + desc + "' tab='" + tab + "'}}";
        }
    }

    private String extractType(String word) {
        String[] words = word.split(":");
        String abbr = words[0];
        switch (abbr) {
            case "t":
                return "text";
            case "i":
                return "image";
            case "I":
                return "import";
            case "f":
                return "file";
            case "tt":
                return "textarea";
            case "l":
                return "link";
            case "d":
                return "data";
            case "s":
                return "slug";
        }
        return "text";
    }

    private String extractKey(String word) {
        String[] words = word.split(":");
        if (words.length >= 2) {
            return words[1];
        } else {
            return "";
        }
    }

    private String extractDesc(String key) {
        String[] words = key.split("_");
        if (words.length > 1 && words[1].length() > 0) {
            String[] desc = Arrays.copyOfRange(words, 1, words.length);
            String description = String.join(" ", desc);
            return description.substring(0, 1).toUpperCase() + description.substring(1).toLowerCase();
        }
        return "";
    }

    private String extractTab(String key) {
        String[] words = key.split("_");
        if (words[0].length() > 0) {
            return words[0].substring(0, 1).toUpperCase() + words[0].substring(1).toLowerCase();
        }
        return "";
    }
}
