package illarion.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;

/**
 * This class is used to create a copyright header that is supposed to be attached to the head of a file.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CopyrightHeader {
    /**
     * The width of a line. The width of the text is limited to this length.
     */
    private final int lineWidth;

    /**
     * The intro text for the comment block. This is written before the actual license header.
     */
    @Nullable
    private final String commentIntro;

    /**
     * The outro text for the comment block. This is written after the actual license header.
     */
    @Nullable
    private final String commentOutro;

    /**
     * The prefix of a comment line. This is written before the actual comment text.
     */
    @Nullable
    private final String commentLineStart;

    /**
     * The suffix of a comment line. This is written at the end of the line.
     */
    @Nullable
    private final String commentLineEnd;

    /**
     * The original text, as its read from the template file.
     */
    @Nullable
    private String licenseTextTemplate;

    public CopyrightHeader(
            int lineWidth,
            @Nullable String commentIntro,
            @Nullable String commentOutro,
            @Nullable String commentLineStart,
            @Nullable String commentLineEnd) {
        this.lineWidth = lineWidth;
        this.commentIntro = commentIntro;
        this.commentOutro = commentOutro;
        this.commentLineStart = commentLineStart;
        this.commentLineEnd = commentLineEnd;
    }

    @Nonnull
    private String getLicenseTextTemplate() throws IOException {
        if (licenseTextTemplate != null) {
            return licenseTextTemplate;
        }

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        @Nullable InputStream in = cl.getResourceAsStream("agpl_template.txt");
        if (in != null) {
            try {
                StringBuilder resultBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    resultBuilder.append(line.trim());
                    resultBuilder.append(' ');
                }
                int index;
                while ((index = resultBuilder.indexOf("{NL}")) != -1) {
                    resultBuilder.replace(index, index + 4, "\n");
                }

                licenseTextTemplate = resultBuilder.toString().replaceAll("[ ]*\n[ ]*", "\n");
            } finally {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
        return licenseTextTemplate;
    }

    public void writeTo(@Nonnull final OutputStream stream) throws IOException {
        writeTo(new OutputStreamWriter(stream));
    }

    public void writeTo(@Nonnull final Writer writer) throws IOException {
        final BufferedWriter bw = new BufferedWriter(writer);

        if (commentIntro != null) {
            bw.write(commentIntro);
            bw.write('\n');
        }

        int textWidth = lineWidth;
        if (commentLineStart != null) {
            textWidth -= commentLineStart.length();
        }
        if (commentLineEnd != null) {
            textWidth -= commentLineEnd.length();
        }

        String[] lines = getLicenseTextTemplate().split("\n");
        for (String line : lines) {
            while (true) {
                if (line.length() < textWidth) {
                    writeLine(bw, line, textWidth);
                    break;
                } else {
                    int lastSpaceIndex = line.lastIndexOf(' ', textWidth);
                    writeLine(bw, line.substring(0, lastSpaceIndex), textWidth);
                    line = line.substring(lastSpaceIndex + 1);
                }
            }
        }

        if (commentOutro != null) {
            bw.write(commentOutro);
            bw.write('\n');
        }
        bw.flush();
    }

    private void writeLine(
            @Nonnull final Writer writer, @Nonnull final String line, final int padding) throws IOException {
        if (commentLineStart != null) {
            writer.write(commentLineStart);
        }
        writer.write(line);
        if (commentLineEnd != null) {
            for (int i = line.length(); i < padding; i++) {
                writer.write(' ');
            }
            writer.write(commentLineEnd);
        }
        writer.write('\n');
    }

    /**
     * Check if the license text meets the specifications set in this class instance.
     *
     * @param text the license text
     * @return {@code true} if the text fits the license text
     */
    public boolean isLicenseText(@Nonnull final String text) {
        String[] textParts = text.split("\n\r|\n|\r");
        if (commentIntro != null && !textParts[0].equals(commentIntro)) {
            return false;
        }
        if (commentOutro != null && !textParts[textParts.length - 1].equals(commentOutro)) {
            return false;
        }

        int textStartIndex = commentIntro == null ? 0 : 1;
        int textEndIndex = commentOutro == null ? textParts.length : textParts.length - 1;

        StringBuilder licenseText = new StringBuilder();
        for (int i = textStartIndex; i < textEndIndex; i++) {
            String line = textParts[i];
            int lineStartIndex = 0;
            int lineEndIndex = line.length();
            if (commentLineStart != null) {
                if (!line.startsWith(commentLineStart)) {
                    return false;
                }
                lineStartIndex = commentLineStart.length();
            }
            if (commentLineEnd != null) {
                if (!line.endsWith(commentLineEnd)) {
                    return false;
                }
                lineEndIndex = line.length() - commentLineEnd.length();
            }
            if (lineStartIndex != lineEndIndex) {
                licenseText.append(line.substring(lineStartIndex, lineEndIndex).trim());
            }
            licenseText.append(' ');
        }
        licenseText.setLength(licenseText.length() - 1);

        try {
            String licenseTemplate = getLicenseTextTemplate().replace('\n', ' ');
            return licenseTemplate.equals(licenseText.toString());
        } catch (IOException e) {
            return false;
        }
    }
}
