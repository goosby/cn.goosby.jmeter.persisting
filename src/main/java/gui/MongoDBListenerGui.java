package gui;

import listener.MongoDBListener;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.gui.AbstractListenerGui;
import org.apache.jorphan.gui.JLabeledTextField;
import utils.ExtraValues;

import javax.swing.*;
import java.awt.*;

/**
 *
 *@author goosby.liu
 *
 */
public class MongoDBListenerGui extends AbstractListenerGui implements Clearable {

	private static final long serialVersionUID = 2520871146574605319L;

    /**
     * mongodb 连接地址
     */
    private final JLabeledTextField mongodbhost = new JLabeledTextField("MONGO_HOST: ");

    /**
     * mongod port
     */
    private final JLabeledTextField mogodbPort = new JLabeledTextField("MONGO_PORT: ");

    /**
     * mongo 帐号
     */
    private final JLabeledTextField username = new JLabeledTextField("USERNAME: ");
    /**
     * mongo 密码
     */
    private final JLabeledTextField passwrod = new JLabeledTextField("PASSWORD: ");

    /**
     * DATA_BASE_NAME
     */
    private final JLabeledTextField dataBaseName = new JLabeledTextField("DATABASE_NAME: ");
	/**
	 * 测试用例名称对应mongodb中的集合名称
	 */
	private final JLabeledTextField testCaseName = new JLabeledTextField("TESTCASE_NAME: ");

	public MongoDBListenerGui(){
		init();
	}

    /**
     * 完成整个Gui界面的布局及事件处理
     */
	private void init() {

        setLayout(new BorderLayout());
        setBorder(makeBorder());

        add(makeTitlePanel(), BorderLayout.NORTH);
        JPanel mainPanel = new VerticalPanel();
        add(mainPanel, BorderLayout.CENTER);

        /**
         * 以下为mongodb配置参数输入区
         */
        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new GridLayout(3, 2,5,5));
        paramPanel.add(mongodbhost);
        paramPanel.add(mogodbPort);
        paramPanel.add(username);
        paramPanel.add(passwrod);
        paramPanel.add(dataBaseName);
        paramPanel.add(testCaseName);

        JPanel controlPanel = new VerticalPanel();
        controlPanel.add(paramPanel);
        controlPanel.setBorder(BorderFactory.createTitledBorder( "MongoDB 参数配置"));
        mainPanel.add(controlPanel);

        JPanel descriptionPanel = new VerticalPanel();
        descriptionPanel.setBorder(BorderFactory.createTitledBorder("配置说明"));
        mainPanel.add(descriptionPanel);


	}

    /**
     * 显示在jmeter组件中的名称
     * @return
     */
	@Override
	public String getStaticLabel() {
		return ExtraValues.VIEW_TAB;
	}

    /**
     * 把Sampler中的数据加载到界面中
     * @param element
     */
	@Override
    public void configure(TestElement element) {
        super.configure(element);
		mongodbhost.setText(element.getPropertyAsString(ExtraValues.MONGO_HOST));
        mogodbPort.setText(element.getPropertyAsString(ExtraValues.MONGO_PORT));
        testCaseName.setText(element.getPropertyAsString(ExtraValues.TEST_CASE_NAME));
        dataBaseName.setText(element.getPropertyAsString(ExtraValues.DATABASE_NAME));
        passwrod.setText(element.getPropertyAsString(ExtraValues.PASSWORD));
        username.setText(element.getPropertyAsString(ExtraValues.USERNAME));
    }
	
	@Override
	public TestElement createTestElement() {
		MongoDBListener mongoDBListener = new MongoDBListener();
		modifyTestElement(mongoDBListener);
		return mongoDBListener;
	}

	@Override
	public String getLabelResource() {
		return this.getClass().getSimpleName();
	}

    /**
     * 把界面的数据移到Sampler中
     * @param element
     */
	@Override
	public void modifyTestElement(TestElement element) {
		super.configureTestElement(element);
		element.setProperty(ExtraValues.MONGO_HOST,mongodbhost.getText());
		element.setProperty(ExtraValues.MONGO_PORT,mogodbPort.getText());
        element.setProperty(ExtraValues.USERNAME,username.getText());
        element.setProperty(ExtraValues.PASSWORD,passwrod.getText());
        element.setProperty(ExtraValues.DATABASE_NAME,dataBaseName.getText());
		element.setProperty(ExtraValues.TEST_CASE_NAME,testCaseName.getText());
	}

	@Override
	public void clearData() {
        this.mongodbhost.setText("");
        this.mogodbPort.setText("");
        this.testCaseName.setText("");
        this.dataBaseName.setText("");
        this.username.setText("");
        this.passwrod.setText("");
	}

    /**
     * 在reset新界面的时候调用，这里可以填入界面控件中需要显示的一些缺省的值。
     */
	@Override
    public void clearGui() {
        super.clearGui();
		this.mongodbhost.setText("");
        this.mogodbPort.setText("");
        this.testCaseName.setText("");
        this.dataBaseName.setText("");
        this.username.setText("");
        this.passwrod.setText("");
    }


    @Override
    protected Container makeTitlePanel() {
        return super.makeTitlePanel();
    }
}
