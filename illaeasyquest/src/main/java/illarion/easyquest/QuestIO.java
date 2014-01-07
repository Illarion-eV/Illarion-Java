package illarion.easyquest;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.io.mxObjectCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.view.mxGraph;
import illarion.easyquest.quest.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

/**
 * This is the input/output class for the quests. It handles loading and saving quest graphs. Also it handles
 * creating the LUA quest files.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class QuestIO {
    /**
     * The character sets that will be tried to load the file. One by one. The first one in the list is
     * {@link #CHARSET} as this one is the most likely one to be the one used.
     */
    private static final Collection<Charset> CHARSETS;

    /**
     * The char set that is used by default to load and save the data.
     */
    public static final Charset CHARSET;

    static {
        CHARSET = Charset.forName("ISO-8859-1");
        CHARSETS = new ArrayList<>();
        CHARSETS.add(CHARSET);
        CHARSETS.addAll(Charset.availableCharsets().values());

        mxCodecRegistry.register(new mxObjectCodec(new Handler()));
        mxCodecRegistry.addPackage(Handler.class.getPackage().getName());
        mxCodecRegistry.register(new mxObjectCodec(new Status()));
        mxCodecRegistry.addPackage(Status.class.getPackage().getName());
        mxCodecRegistry.register(new mxObjectCodec(new Trigger()));
        mxCodecRegistry.addPackage(Trigger.class.getPackage().getName());
        mxCodecRegistry.register(new mxObjectCodec(new Position()));
        mxCodecRegistry.addPackage(Position.class.getPackage().getName());
    }

    /**
     * Load the graph model of a quest from a file. This is the quest data as its handled internally by the easyQuest
     * editor.
     *
     * @param file the file that is used as data source
     * @return the graph model instance containing the data of the file
     * @throws IOException in case reading the model from the file fails for any reason
     */
    public static mxIGraphModel loadGraphModel(@Nonnull final Path file) throws IOException {
        if (!Files.isReadable(file)) {
            throw new IOException("Can't read the required file.");
        }
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            IOException firstException = null;
            Document document = null;
            for (Charset charset : CHARSETS) {
                try (Reader reader = Files.newBufferedReader(file, charset)) {
                    document = docBuilder.parse(new InputSource(reader));
                    break;
                } catch (IOException e) {
                    if (firstException == null) {
                        firstException = e;
                    }
                }
            }
            if (document == null) {
                throw firstException;
            }
            final mxCodec codec = new mxCodec(document);
            mxIGraphModel model = new mxGraphModel();
            codec.decode(document.getDocumentElement(), model);
            return model;
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException(e);
        }
    }

    /**
     * Save a graph model to the file system.
     *
     * @param model the model to store to the file system
     * @param target tje target in the file system that will receive the data
     * @throws IOException in case saving the file fails
     */
    public static void saveGraphModel(@Nonnull final mxIGraphModel model, @Nonnull final Path target)
            throws IOException {
        if (!Files.isWritable(target)) {
            throw new IOException("Can't write the required file.");
        }
        final mxCodec codec = new mxCodec();
        Node node = codec.encode(model);

        if (node == null) {
            throw new IOException("Model can't be encoded to XML.");
        }

        try (Writer writer = Files.newBufferedWriter(target, CHARSET)) {
            Transformer tf = TransformerFactory.newInstance().newTransformer();

            tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            tf.setOutputProperty(OutputKeys.ENCODING, CHARSET.name());

            tf.transform(new DOMSource(node), new StreamResult(writer));
            writer.flush();
        } catch (TransformerException e) {
            throw new IOException(e);
        }
    }

    /**
     * Export a quest to its lua files.
     *
     * @param model the quest model
     * @param rootDirectory the directory to store the root directory in
     * @throws IOException in case anything goes wrong
     */
    public static void exportQuest(@Nonnull final mxIGraphModel model, @Nonnull final Path rootDirectory)
            throws IOException {
        if (Files.isDirectory(rootDirectory)) {
            Files.walkFileTree(rootDirectory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc == null) {
                        Files.delete(dir);
                        return CONTINUE;
                    } else {
                        throw exc;
                    }
                }
            });
        }
        Files.createDirectories(rootDirectory);

        String questName = rootDirectory.getName(rootDirectory.getNameCount() - 1).toString();

        final mxICell root = (mxCell) model.getRoot();
        final Object idNode = root.getValue();
        int questID = -1;
        if (idNode != null) {
            try {
                questID = Integer.parseInt(idNode.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        if (questID == -1) {
            throw new IOException("Required quest ID is not set.");
        }

        Path questMainFile = rootDirectory.resolve("quest.txt");

        if (Files.exists(questMainFile)) {
            Files.delete(questMainFile);
        }

        mxGraph graph = new mxGraph(model);
        final Object[] edges = graph.getChildEdges(graph.getDefaultParent());

        for (int i = 0; i < edges.length; i++) {
            final mxCell edge = (mxCell) edges[i];
            final Trigger trigger = (Trigger) edge.getValue();
            final TriggerTemplate template = TriggerTemplates.getInstance().getTemplate(trigger.getType());

            final String scriptName = "trigger" + Integer.toString(i + 1);
            final mxICell source = edge.getSource();
            final mxICell target = edge.getTarget();
            final Status sourceState = (Status) source.getValue();
            final Status targetState = (Status) target.getValue();
            final String sourceId = sourceState.isStart() ? "0" : source.getId();
            final String targetId = targetState.isStart() ? "0" : target.getId();
            final Object[] parameters = trigger.getParameters();
            final Handler[] handlers = targetState.getHandlers();
            final Collection<String> handlerTypes = new HashSet<>();
            final Condition[] conditions = trigger.getConditions();

            final StringBuilder handlerCode = new StringBuilder();
            if (handlers != null) {
                for (final Handler handler : handlers) {
                    final String type = handler.getType();
                    final Object[] handlerParameters = handler.getParameters();
                    final HandlerTemplate handlerTemplate = HandlerTemplates.getInstance().getTemplate(type);
                    final int playerIndex = handlerTemplate.getPlayerIndex();

                    handlerTypes.add(type);

                    handlerCode.append("    handler.").append(type.toLowerCase()).append('.').append(type).append('(');
                    if (handlerParameters.length > 0) {
                        if (playerIndex == 0) {
                            handlerCode.append("PLAYER, ");
                        }
                        handlerCode.append(exportParameter(handlerParameters[0],
                                                           handlerTemplate.getParameter(0).getType()));

                        for (int j = 1; j < handlerParameters.length; ++j) {
                            if (playerIndex == j) {
                                handlerCode.append(", PLAYER");
                            }
                            handlerCode.append(", ").append(exportParameter(handlerParameters[j],
                                                                            handlerTemplate.getParameter(j).getType()));
                        }
                    }
                    handlerCode.append("):execute()\n");
                }
            }

            final StringBuilder conditionCode = new StringBuilder();
            if (conditions != null) {
                for (final Condition condition : conditions) {
                    final String type = condition.getType();
                    final Object[] conditionParameters = condition.getParameters();
                    final ConditionTemplate conditionTemplate = ConditionTemplates.getInstance().getTemplate(type);
                    String conditionString = conditionTemplate.getCondition();
                    if (conditionString != null) {
                        for (int j = 0; j < conditionParameters.length; ++j) {
                            final Object param = conditionParameters[j];
                            final String paramName = conditionTemplate.getParameter(j).getName();
                            final String paramType = conditionTemplate.getParameter(j).getType();
                            String operator = null;
                            String value = null;
                            if ("INTEGERRELATION".equals(paramType)) {
                                final IntegerRelation ir = (IntegerRelation) param;
                                value = String.valueOf(ir.getInteger());
                                operator = ir.getRelation().toLua();
                            }
                            if (operator != null) {
                                conditionString = conditionString.replaceAll("OPERATOR_" + j, operator)
                                        .replaceAll(paramName, value);
                            }
                        }
                    }
                    if (conditionCode.length() > 0) {
                        conditionCode.append("   and ");
                    }
                    conditionCode.append(conditionString).append('\n');
                }
            }
            if (conditionCode.length() == 0) {
                conditionCode.append("true\n");
            }

            try (BufferedWriter writer = Files.newBufferedWriter(rootDirectory.resolve(scriptName + ".lua"), CHARSET)) {
                for (final String type : handlerTypes) {
                    writer.write("require(\"handler.");
                    writer.write(type.toLowerCase());
                    writer.write("\"}");
                    writer.newLine();
                }
                String header = template.getHeader();
                if (header != null) {
                    writer.write(header);
                    writer.newLine();
                }

                writer.write("module(\"questsystem.");
                writer.write(questName);
                writer.write('.');
                writer.write(scriptName);
                writer.write("\", package.seeall)");
                writer.newLine();

                writer.write("local QUEST_NUMBER = ");
                writer.write(Integer.toString(questID));
                writer.newLine();

                writer.write("local PRECONDITION_QUESTSTATE = ");
                writer.write(sourceId);
                writer.newLine();

                writer.write("local POSTCONDITION_QUESTSTATE = ");
                writer.write(targetId);
                writer.newLine();

                int paramCount = template.size();
                if (parameters == null || paramCount != parameters.length) {
                    throw new IOException("Required parameters are not present.");
                }
                for (int j = 0; j < template.size(); ++j) {
                    writer.write("local ");
                    writer.write(template.getParameter(j).getName());
                    writer.write(" = ");
                    writer.write(exportParameter(parameters[j], template.getParameter(j).getType()));
                    writer.newLine();
                }

                String body = template.getBody();
                if (body != null) {
                    writer.write(body);
                    writer.newLine();
                }
                writer.write("function HANDLER(PLAYER)");
                writer.newLine();
                writer.write(handlerCode.toString());
                writer.write("end");
                writer.newLine();

                writer.write("function ADDITIONALCONDITIONS(PLAYER)");
                writer.newLine();
                writer.write("return ");
                writer.write(conditionCode.toString());
                writer.write("end");

                writer.flush();
            }

            try (BufferedWriter writer = Files
                    .newBufferedWriter(rootDirectory.resolve("quest.txt"), CHARSET, APPEND, CREATE)) {
                String cat = template.getCategory();
                TemplateParameter id = template.getId();
                String type = id == null ? null : id.getType();
                String entryPoint = template.getEntryPoint();

                if (cat == null || type == null || entryPoint == null) {
                    throw new IOException("Template appears to be incomplete.");
                }

                writer.write(template.getCategory());
                writer.write(',');
                writer.write(exportId(trigger.getObjectId(), template.getId().getType()));
                writer.write(',');
                writer.write(template.getEntryPoint());
                writer.write(',');
                writer.write(scriptName);
                writer.newLine();
                writer.flush();
            }
        }
    }

    private static String exportId(final Object parameter, final String type) {
        if ("POSITION".equals(type)) {
            final Position p = (Position) parameter;
            return p.getX() + "," + p.getY() + ',' + p.getZ();
        }
        if ("INTEGER".equals(type)) {
            if (parameter instanceof Long) {
                final Long n = (Long) parameter;
                return n.toString();
            }
            return parameter.toString();
        }
        return "TYPE NOT SUPPORTED";
    }

    private static String exportParameter(@Nonnull final Object parameter, @Nonnull final String type) {
        if ("TEXT".equals(type)) {
            final String s = (String) parameter;
            return '"' + s.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
        }
        if ("POSITION".equals(type)) {
            final Position p = (Position) parameter;
            return "position(" + p.getX() + ", " + p.getY() + ", " + p.getZ() + ')';
        }
        if ("INTEGER".equals(type)) {
            if (parameter instanceof Long) {
                final Long n = (Long) parameter;
                return n.toString();
            }
            return parameter.toString();
        }
        return "TYPE NOT SUPPORTED";
    }
}
