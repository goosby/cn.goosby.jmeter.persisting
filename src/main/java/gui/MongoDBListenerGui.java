package gui;

import listener.MongoDBListener;
import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.gui.AbstractListenerGui;

import javax.swing.*;
import java.awt.*;


public class MongoDBListenerGui extends AbstractListenerGui implements Clearable {

	private static final long serialVersionUID = 2520871146574605319L;
	private static final String TAB = "Persisting Sample to MongoDB";
	
	public MongoDBListenerGui(){
		init();
	}
	
	private void init() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		setBorder(makeBorder());
		add(mainPanel);
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

	}

	@Override
	public void clearData() {

		super.clearGui();
	}
	@Override
    public void clearGui() {

		super.clearGui();
    }
}
