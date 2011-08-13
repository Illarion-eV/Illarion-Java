package illarion.easyquest.gui;

import java.util.Hashtable;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;

import illarion.easyquest.Lang;

public class MainFrame extends JRibbonFrame
{

    private static MainFrame instance;

	public MainFrame()
	{
		super("easyQuest Editor");
		
		setApplicationIcon(Utils
            .getResizableIconFromResource("easyquest.png"));
		
		final RibbonTask graphTask =
            new RibbonTask(Lang.getMsg(getClass(), "ribbonTaskQuest"),
                new ClipboardBand(), new GraphBand());
        getRibbon().addTask(graphTask);

        getRibbon().setApplicationMenu(new MainMenu());

        final JCommandButton saveButton =
            new JCommandButton(Utils.getResizableIconFromResource("filesave.png"));
        saveButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
            getClass(), "saveButtonTooltipTitle"), Lang.getMsg(getClass(),
            "saveButtonTooltip")));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {

            }
        });
        getRibbon().addTaskbarComponent(saveButton);

        final JPanel rootPanel = new JPanel(new BorderLayout());

		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();

        mxStylesheet stylesheet = graph.getStylesheet();
        Hashtable<String, Object> nodeStyle = new Hashtable<String, Object>();
        nodeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        nodeStyle.put(mxConstants.STYLE_ROUNDED, true);
        nodeStyle.put(mxConstants.STYLE_OPACITY, 50);
        nodeStyle.put(mxConstants.STYLE_FILLCOLOR, "#EFEFFF");
        nodeStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#AFAFFF");
        nodeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        stylesheet.putCellStyle("NODE", nodeStyle);
        Hashtable<String, Object> edgeStyle = new Hashtable<String, Object>();
        edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ELBOW);
        edgeStyle.put(mxConstants.STYLE_STROKEWIDTH, 2.0);
        edgeStyle.put(mxConstants.STYLE_ROUNDED, true);
        stylesheet.putCellStyle("EDGE", edgeStyle);

		graph.getModel().beginUpdate();
		try
		{
			Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80,
					30, "NODE");
			Object v2 = graph.insertVertex(parent, null, "World!", 240, 150,
					80, 30, "NODE");
			graph.insertEdge(parent, null, "Edge", v1, v2, "EDGE");
		}
		finally
		{
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);

		final JScrollPane mainPanel = new JScrollPane(graphComponent);

        rootPanel.add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(rootPanel);
	}

	public static void main(String[] args)
	{
	    JRibbonFrame.setDefaultLookAndFeelDecorated(true);
	    
	    SwingUtilities.invokeLater(new Runnable() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
        		SubstanceLookAndFeel.setSkin(
        		    "org.pushingpixels.substance.api.skin.OfficeSilver2007Skin");	    
        		    
        	    instance = new MainFrame();
        		getInstance().setDefaultCloseOperation(JRibbonFrame.EXIT_ON_CLOSE);
        		getInstance().setSize(1204, 768);
        		getInstance().setVisible(true);
        	}
        });
	}

    protected static MainFrame getInstance() {
        return instance;
    }

    protected void closeWindow() {

        setVisible(false);
        dispose();

        System.exit(0);
    }

}
