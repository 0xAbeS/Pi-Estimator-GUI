package piEstimator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PiEstimator implements ActionListener {
	//Where Threads are stored
	private ArrayList<Estimate> threads = new ArrayList<Estimate>();
	//GUI Elements
	private static JFrame frame;
	private JPanel panel;
	private JButton button;
	private static JLabel label;
	//Store PI and whether the estimator is currently running
	private volatile double pi;
	private static boolean running;
	
	// Initializes GUI upon creation of object
	public PiEstimator() {
		initialize();
	}
	/*
	 Sets up GUI
	 Creates and starts threads
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("ActionListener Demo");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(800, 500);
		frame.setLayout(new BorderLayout(10, 10));
		frame.setLocationRelativeTo(null);

		panel = new JPanel();
		label = new JLabel("PI: ");
		button = new JButton("Start");

		button.addActionListener(this);

	// Adds Start/Pause button and PI label
		label.setText("starting text");
		panel.add(label);
		panel.add(button);
		frame.add(panel, BorderLayout.CENTER);

		threads.add(new Estimate());

		threads.get(0).start();

	}
	
	 //Stores its own x and myPi values essential for calculating pi
	 
	class Estimate extends Thread {
		private long x;
		private double myPi;

		public Estimate() {
			x = 0;
			myPi = 0;
		}
		//Checks whether it is supposed to be running
		//if not it proceeds to wait until notified by button action listener
		//Else it keeps on calculating
		public void run() {
			while (true) {
				synchronized (this) {
					if (!running) {
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						/*
						 Algorithm:
						 	-Essentially uses Riemann sums to calculate pi
						 	-The leibniz rule states that if two functions f(x) and g(x) are differentiable n times individually, then their product f(x).g(x) is also differentiable n times.
						 	-Algorithm devides the circle to the top right 1/4 of the circle.
						 	-Algorithm  uses -1^x to alternate from subtracting and adding ratio of circle to square(pi)
						 	-It narrows this value and multiplies by 4 to get pi
						 	- Prints pi to the screen every 1 million cycles
						 	- This algorithm can be made faster by breaking up increments to a few multi-million values of x since it essentially is just narrowing down the value of pi from the positive to the negative
						 */
						myPi += (Math.pow(-1, x) * 4.0) / (2 * x + 1);
						x += 1;
						if (x % 1000000 == 0) {
							pi = myPi;
							label.setText("My pi estimate " + pi);
						}
					}
				}
			}
		}
	}
	
	// Shows the GUI
	// Needs to be called to make GUI visible
	public void show() {
		this.frame.setVisible(true);
	}
	
	//Listens to button and tells estimator to stop/start running
	@Override
	public void actionPerformed(ActionEvent e) {
// TODO Auto-generated method stub
		if (running) {
			running = false;
			button.setText("Start");
		} else {
			running = true;
			button.setText("Pause");
			synchronized (threads.get(0)) {
				threads.get(0).notify();
			}
		}

	}
}
