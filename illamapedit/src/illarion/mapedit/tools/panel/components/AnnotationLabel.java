package illarion.mapedit.tools.panel.components;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @author Fredrik K
 */
public class AnnotationLabel extends JLabel {
    public AnnotationLabel() {
        Border paddingBorder = BorderFactory.createEmptyBorder(1,10,1,10);
        Border border = BorderFactory.createLineBorder(Color.RED);
        setBorder(BorderFactory.createCompoundBorder(border,paddingBorder));
        setVisible(false);
    }

    public void setAnnotation(final String text) {
        if ((text != null) && !text.isEmpty()) {
            setText(text);
            setVisible(true);
        } else {
            setText("");
            setVisible(false);
        }
    }

    public String getAnnotation() {
        if (getText() == null) {
            return "";
        }
        return getText();
    }
}
