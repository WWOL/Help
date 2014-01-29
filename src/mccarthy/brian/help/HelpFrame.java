package mccarthy.brian.help;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

public class HelpFrame extends JFrame implements TreeSelectionListener {

    private JPanel contentPane;
    private JEditorPane editor;
    private String loc;
    private String name;
    private DefaultMutableTreeNode root;
    private static final long serialVersionUID = 1L;
    private JTree tree;

    /**
     * Set up the frame and make it visible
     * @param loc where to read help files from
     */
    public HelpFrame(String loc, String name) {
        this(loc, name, true);
    }

    /**
     * Create this help frame
     * @param loc where to read help files from
     * @param name name of program (shown as root of tree)
     * @param shown show the frame when created
     */
    public HelpFrame(String loc, String name, boolean shown) {
        this.loc = loc;
        this.name = name;
        this.contentPane = new JPanel();
        setContentPane(contentPane);
        
        editor = new JEditorPane("text/html", getDefaultText());
        editor.setPreferredSize(new Dimension(600, 600));
        editor.setLocation(200, 0);
        editor.setBorder(new LineBorder(Color.BLACK, 2));
        editor.setEditable(false);
        root = new DefaultMutableTreeNode(name);
        
        tree = new JTree(root);
        tree.setPreferredSize(new Dimension(200, 600));
        tree.setLocation(0, 0);
        tree.setBorder(new LineBorder(Color.BLACK, 2));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);
        
        System.out.println("Reading info from: " + loc);
        //long start = System.currentTimeMillis();
        long start = System.currentTimeMillis();
        scanForFiles(root, new File(loc + name + File.separator));
        //System.out.println("Done reading (" + (System.currentTimeMillis() - start) + "ms)");
        long fin = System.currentTimeMillis();
        long time = fin - start;
        System.out.println("Done reading (" + time + "ms)");
        contentPane.add(tree);
        contentPane.add(editor);
        setTitle("Help: " + this.name + " (" + loc + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(shown);
    }

    private String readFile(String file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Scan for files to add to the tree
     */
    private void scanForFiles(DefaultMutableTreeNode parent, File file) {
        if (parent == null) {
            parent = root;
        }
        File[] files = file.listFiles();
        if (files == null) {
            System.out.println("Empty folder at " + file.getPath());
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                DefaultMutableTreeNode dirNode = new DefaultMutableTreeNode(f.getName());
                addNode(parent, dirNode);
                scanForFiles(dirNode, f);
            } else {
                addNode(parent, f);
            }
        }
    }
    
    private void addNode(DefaultMutableTreeNode parent, File f) {
        String fileName = f.getName().toLowerCase();
        if (fileName.equals(name)) {
            return; // Don't add the program name to the tree
        }
        if (f.isFile() && ((!fileName.endsWith(".html") && !fileName.endsWith(".htm")) || fileName.startsWith("."))) {
            // Skip files that aren't HTML or file that are hidden
            System.out.println("Skipping! File isn't right type. (" + f + ")");
            return;
        }//*/
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(f.getName());
        addNode(parent, node);
    }
    
    private void addNode(DefaultMutableTreeNode parent, DefaultMutableTreeNode node) {
        parent.add(node);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        //DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        Object[] oArray = e.getPath().getPath();
        if (oArray.length > 1) {
            //oArray = Arrays.copyOfRange(oArray, 1, oArray.length - 0);
        }
        StringBuilder sb = new StringBuilder(loc);
        for (Object o : oArray) {
            //if (o.toString().equals(name)) {
            //    continue;
            //}
            //System.out.println("o: " + o);
            //if (!o.equals(name)) {
                sb.append(o).append(File.separator);
            //}
        }
        String path = sb.substring(0, sb.length() - 1); // Remove trailing File.seperator 
        if (!path.endsWith(".html") && !path.endsWith(".htm")) {
            // Use the index file if clicking on a folder, not file
            path += File.separator + "index.html";
        }
        String fileLines = readFile(path);
        if (fileLines == "") {
            fileLines = "<html><b>Sorry, no help for that topic!</b></html>";
        }
        //String fileLines = "<html><b>Sorry, no help for that topic!</b></html>";
        editor.setText(fileLines);
    }
    
    /**
     * Get default text for the JEditorPane
     * @return string of default text
     */
    public String getDefaultText() {
        File file = new File(loc + name, "index.html");
        if (file.exists()) {
            return readFile(file.getPath());
        }
        StringBuilder sb = new StringBuilder();
        //"<html><b>Welcome</b><br/>This is help for " + name + ". </html>"
        sb.append("<html><h1>Welcome</h1><br/>This is help for ");
        sb.append(name);
        sb.append(".<br/>");
        sb.append("Contact me <a href=\"http://github.com/WWOL\"> on GitHub<a/>.<br/>");
        return sb.toString();
    }

}
