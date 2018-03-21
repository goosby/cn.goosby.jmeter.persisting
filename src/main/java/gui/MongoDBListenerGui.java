package gui;

import listener.MongoDBListener;
import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.gui.AbstractListenerGui;
import utils.ExtraValues;

import javax.swing.*;
import java.awt.*;


public class MongoDBListenerGui extends AbstractListenerGui implements Clearable {

	private static final long serialVersionUID = 2520871146574605319L;


	private JTextField mongodbhost;//mongodb 连接地址
	private JTextField mogodbPort; //mongod port
	private JTextField testcase; //测试用例名称对应mongodb中的集合名称

	public MongoDBListenerGui(){
		super();
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		setBorder(makeBorder());
		Box box = Box.createVerticalBox();
		box.add(makeTitlePanel());
		box.add(createMongoHostsPanel());
		box.add(createMongoPortPanel());
		box.add(createTestCasePanel());
		add(box,BorderLayout.NORTH);
	}
	
	@Override
	public String getStaticLabel() {
		return ExtraValues.VIEW_TAB;
	}
	
	@Override
    public void configure(TestElement element) {
        super.configure(element);
		mongodbhost.setText(element.getPropertyAsString(ExtraValues.MONGO_HOST));
		mogodbPort.setText(element.getPropertyAsString(ExtraValues.MONGO_PORT));
		testcase.setText(element.getPropertyAsString(ExtraValues.TEST_CASE));
    }
	
	@Override
	public TestElement createTestElement() {
		MongoDBListener listener = new MongoDBListener();
		modifyTestElement(listener);
		return listener;
	}

	@Override
	public String getLabelResource() {

		return this.getClass().getSimpleName();
	}

	@Override
	public void modifyTestElement(TestElement element) {

		super.configureTestElement(element);
		element.setProperty(ExtraValues.MONGO_HOST,mongodbhost.getText());
		element.setProperty(ExtraValues.MONGO_PORT,mogodbPort.getText());
		element.setProperty(ExtraValues.TEST_CASE,testcase.getText());
	}

	@Override
	public void clearData() {

	}

	@Override
    public void clearGui() {

		super.clearGui();
		mongodbhost.setText("");
		mogodbPort.setText("");
		testcase.setText("");
    }


    private JPanel createMongoHostsPanel(){
		JLabel label = new JLabel("mongodb host: ");
		mongodbhost = new JTextField();
		mongodbhost.setName("mongodb_host");
		label.setLabelFor(mongodbhost);
		JPanel hostsPanel = new JPanel(new BorderLayout());
		hostsPanel.add(label, BorderLayout.WEST);
		hostsPanel.add(mongodbhost, BorderLayout.CENTER);
		return hostsPanel;
	}
	private JPanel createMongoPortPanel(){
		JLabel label = new JLabel("mongodb port: ");
		mogodbPort = new JTextField();
		mogodbPort.setName("Mongo_ports");
		label.setLabelFor(mogodbPort);
		JPanel testcasePanel = new JPanel(new BorderLayout());
		testcasePanel.add(label, BorderLayout.WEST);
		testcasePanel.add(mogodbPort, BorderLayout.CENTER);
		return testcasePanel;
	}
	private JPanel createTestCasePanel(){
		JLabel label = new JLabel("Test case: ");
		testcase = new JTextField();
		testcase.setName("test_case");
		label.setLabelFor(testcase);
		JPanel testcasePanel = new JPanel(new BorderLayout());
		testcasePanel.add(label, BorderLayout.WEST);
		testcasePanel.add(testcase, BorderLayout.CENTER);
		return testcasePanel;
	}
}
