package gui;

import listener.MongoDBListener;
import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.gui.AbstractListenerGui;

import javax.swing.*;
import java.awt.*;


public class MongoDBListenerGui extends AbstractListenerGui implements Clearable {

	private static final long serialVersionUID = 2520871146574605319L;
	private static final String TAB = "Persisting Sampler to MongoDB";

	private JTextField mongodbhost;//mongodb 连接地址
	private JTextField testcase;

	public MongoDBListenerGui(){
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		setBorder(makeBorder());
		Box box = Box.createVerticalBox();
		box.add(makeTitlePanel());
		box.add(createMongoHostsPanel());
		box.add(createTestCasePanel());
		add(box,BorderLayout.NORTH);
	}
	
	@Override
	public String getStaticLabel() {
		return TAB;
	}
	
	@Override
    public void configure(TestElement element) {
        super.configure(element);

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
		element.setProperty("mongodb_host",mongodbhost.getText());
		element.setProperty("test_case",testcase.getText());
	}

	@Override
	public void clearData() {

		super.clearGui();

	}
	@Override
    public void clearGui() {

		super.clearGui();
		mongodbhost.setText("");
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
