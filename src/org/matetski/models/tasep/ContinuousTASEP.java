package org.matetski.models.tasep;

import java.awt.*;         // Defines basic classes for GUI programming.
import java.awt.event.*;   // Defines classes for working with events.
import java.util.Objects;

import javax.swing.*;      // Defines the Swing GUI classes.
import javax.swing.event.*;

public class ContinuousTASEP 
   extends JApplet implements ActionListener,ItemListener,Runnable,ChangeListener
   {
  
/**
	 * 
	 */
	private static final long serialVersionUID = 6772823969948046669L;
	
	//	Variables for the animator
	private Thread animator;
	   double delay;

//	 Variables for the specific program
	   boolean freeze; // Changed with StartStopButton
	   int N;
	   double alpha;
	   int[] x; // Position of the particles
	   boolean[] CanJump; // Put true if particle can jump
	   int FreePart; // The number of particles which can jump (including the right most one of course)
	   double t; // Time
	   int Speed;
	   int IC; // Type of initial condition 0=step, 1=half flat, 2=flat
	   int Vis; // Type of visualization 0=straight, 1=45 degree rotation
	   double Diameter;

//	Variables for the Buttons
	   Display1 TASEPDisplay;  // The display for the TASEP particles
	   JButton StartStopButton,DirTempoButton,MultilinesButton,NewPointsButton,SetParameters,Reset;
	   JTextField PointsText,AlphaText,DiameterText; 
	   JLabel PointsLabel,AlphaLabel,pLabel,DiameterLabel; 
	   JSlider SpeedSlider;
	   JLabel SpeedLabel;
	   JComboBox<String> ICMenu,VisMenu;
	   JPanel AnimationRegion,ControlRegion;

//	Animation
	   public void start() {
	    animator = new Thread(this);
	    animator.setPriority(5);
	    animator.start();
	    }

	    public void run() {
	    // Remember the starting time
	    long tm = System.currentTimeMillis();
	    while (Thread.currentThread() == animator) {
	        // Display the next frame of animation.
	      	ComputeAnimation();
	       	repaint();
	        try {
	        tm += delay;
	        Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
	        } catch (InterruptedException e) {break;}
	    }
	    }
	    
	   public void stop() {
	     animator = null;
	   }
	   
//	 The init 
	    public void init() {
	  // Leggi le opzioni dell'applet e definisce alcune condizioni iniziali  	
	      N = 800;
	      alpha = 1.0;
	      Speed = 100;
	      delay = 1000 / Speed;
	      x = new int[20002];
	      CanJump = new boolean[20002];
	      Diameter=1;
	      
	      InitFrames();
	      Inizializza();
	     }  // end init()

//	 Inizializza il testo
	  public void Inizializza() {
	  	if (IC==2) { 
		    for(int k=1;k<=N;k++){
		      x[k]=-2*k+1+N;
		   	  }
		  	}
	  	if (IC==1) { 
	  	x[1]=-1;
	    for(int k=2;k<=N;k++){
	      x[k]=-2*k+1; // This is for half-flat
//          x[k]=x[k-1]-1; while (Math.random()<0.5) {x[k]--;} // This is for half-stationary
	   	  }
	  	}
	  	if (IC==0) { 
	  	    for(int k=1;k<=N;k++){
	  	      x[k]=-k;
	  	   	  }
	  	  	}
	   	  t=0;
	   // Check which particles can jump
	    CanJump[1]=true;
	    FreePart=1;
	    for(int k=2;k<=N;k++){
	   	  if (x[k-1]-x[k]>=2) {CanJump[k]=true; FreePart++;} else {CanJump[k]=false;}
	    }
	  } // End inizializza il resto

//	 Compute the next configuration for the animation
	  public void ComputeAnimation() {
	  int Nmove,conta;
	  double Dt;
	  double TotT=0;
	  if (!freeze) {
	  	while (TotT<=1) {
	  	// Determine the time of next jump
	  	Dt=-Math.log(Math.random())/(alpha+(FreePart-1.0));
	   // Determine if it is the first particle jumping or not
	  	if (Math.random()<=alpha/(alpha+(FreePart-1.0))) 
	  	{ x[1]++; 
	  	  if (x[1]-x[2]==2) {CanJump[2]=true; FreePart++;}
	  	} else // choose randomly one of the other particle that can jump
	  	{
	  	 Nmove=1;
	  	 while (Nmove==1)
	  	 {
	  	   conta=(int)(Math.random()*(N-1))+2;
	  	   if (CanJump[conta]) {Nmove=conta;}
	  	 } 
//	  	 while (conta>0)
//	  	 {
//	  	   Nmove++;
//	  	   if (canJump[Nmove]==true) {conta--;}
//	  	 }
	  	 x[Nmove]++;
	  	 if (x[Nmove-1]-x[Nmove]==1) {CanJump[Nmove]=false; FreePart--;}
	  	 if ((Nmove<N)&&(x[Nmove]-x[Nmove+1]==2)) {CanJump[Nmove+1]=true; FreePart++;}
	  	}
  	  t=t+Dt;
  	  TotT=TotT+Dt;}
	  }
	  } // End ComputeAnimation

//	 Restart the simulation when some parameters like number of points or configuration or geometry changed 

	  public void ridisegna() {
	    TASEPDisplay.repaint();
	  }
	  
	  public void riparti() {
	    freeze=true;
	    StartStopButton.setText("Start");
	    t=0;
	    ridisegna();
	  }

	  public class Display1 extends JPanel {
	     /**
		 * 
		 */
		private static final long serialVersionUID = -6949357091749745233L;
		Display1() {
	        setBackground(Color.lightGray);
	     }
	  public void paintComponent(Graphics g){
	    int width=getSize().width;
	    int height=getSize().height;
	    super.paintComponent(g);
	    int Delta2;
	    double Dm;
	    double alphaBis;
	    
	    double shift=1.0; // 1=standard
	    
	    if (alpha<1) {alphaBis=alpha;} else {alphaBis=1;}
	    
	    Delta2=(int)(Diameter*t/2);
		// Draw the TASEP particles position in the (k,x_k+k) plot
	    g.setColor(Color.white);
	    g.drawString("Continuous time = "+(int)(t),width-300,50);
	    g.setColor(Color.blue);
	    for(int k=1;k<N;k++){
	      g.fillOval((int)(shift*width/2+Diameter*x[k]),height-(int)(Diameter)-10,(int)(Diameter)+1,(int)(Diameter)+1);
	    }
	    if (Vis==1) {
    	  if (IC==1) {
    		g.setColor(Color.black);
         	g.drawLine(width/2,height-(int)(2*Diameter)-20,width/2+height,height-(int)(2*Diameter)-20-height);
    	  	if (alpha>0.5) {
              // draw the flat part
      		    g.setColor(Color.black);
    		    g.drawLine(0,height-(int)(2*Diameter)-20-Delta2,width/2,height-(int)(2*Diameter)-20-Delta2);
    		 // Draw the parabola
    		    g.setColor(Color.red);
        	    for(int k=1+(int)((1-alphaBis)*(1-alphaBis)*t);k<t/4;k++){
    		      g.drawLine(width/2+(int)(Diameter*(t-2*Math.sqrt(t*k))),height+(int)(-2*Diameter-20-Diameter*(2*k+(t-2*Math.sqrt(t*k)))),width/2+(int)(Diameter*(t-2*Math.sqrt(t*(k-1)))),height+(int)(-2*Diameter-20-Diameter*(2*k-2+(t-2*Math.sqrt(t*(k-1))))));
    		    }
           	 // Draw the shock region
        	    g.setColor(Color.cyan);
        	    if (alpha<1) {
                g.drawLine(width/2+(int)(Diameter*(2*alpha-1)*t),height+(int)(-2*Diameter-20-Diameter*(1-2*alpha+2*alpha*alpha)*t),width/2+(int)(Diameter*(alpha*t)),height+(int)(-2*Diameter-20-Diameter*(alpha*t)));
        	    }        	  
    		} else {
              // draw the flat part
      		  g.setColor(Color.red);
    		  g.drawLine(0,height-(int)(2*Diameter)-20-Delta2,width/2-(int)(Diameter*(0.5-alpha)*t),height-(int)(2*Diameter)-20-Delta2);
          	 // Draw the shock region
        	  g.setColor(Color.cyan);
              g.drawLine(width/2+(int)(Diameter*((alpha-0.5))*t),height-(int)(2*Diameter)-20-Delta2,width/2+(int)(Diameter*(alpha*t)),height+(int)(-2*Diameter-20-Diameter*(alpha*t)));
    	  	}
	      }
    	  if (IC==2) {
   		    g.setColor(Color.red);
   		    g.drawLine(0,height-(int)(2*Diameter)-20-Delta2,width,height-(int)(2*Diameter)-20-Delta2);
	      }
    	  if (IC==0){
    	    g.setColor(Color.black);
    	    g.drawLine(width/2,height-(int)(2*Diameter)-20,width/2-height,height-(int)(2*Diameter)-20-height);
    	    g.drawLine(width/2,height-(int)(2*Diameter)-20,width/2+height,height-(int)(2*Diameter)-20-height);
// Draw the parabola
    	    g.setColor(Color.red);
    	    for(int k=1+(int)((1-alphaBis)*(1-alphaBis)*t);k<t;k++){
		      g.drawLine(width/2+(int)(Diameter*(t-2*Math.sqrt(t*k))),height+(int)(-2*Diameter-20-Diameter*(2*k+(t-2*Math.sqrt(t*k)))),width/2+(int)(Diameter*(t-2*Math.sqrt(t*(k-1)))),height+(int)(-2*Diameter-20-Diameter*(2*k-2+(t-2*Math.sqrt(t*(k-1))))));
		    }
// Draw the shock region
    	    g.setColor(Color.cyan);
    	    if (alpha<1) {
            g.drawLine(width/2+(int)(Diameter*(2*alpha-1)*t),height+(int)(-2*Diameter-20-Diameter*(1-2*alpha+2*alpha*alpha)*t),width/2+(int)(Diameter*(alpha*t)),height+(int)(-2*Diameter-20-Diameter*(alpha*t)));
    	    }
//
    	  }
    	  g.setColor(Color.blue);
    	  if (IC==2) {Dm=Diameter*N*1.0;} else {Dm=0;}
          g.drawLine(width/2+(int)(Diameter*x[1]+Diameter),height+(int)(-2*Diameter-20-Diameter*(2 +x[1])+Diameter+Dm),width/2+(int)(Diameter*x[1]+Diameter)+height,height+(int)(-2*Diameter-20-Diameter*(2 +x[1])+Diameter+Dm)-height);
          g.drawLine(width/2+(int)(Diameter*x[1]),height+(int)(-2*Diameter-20-Diameter*(2 +x[1])+Dm),width/2+(int)(Diameter*x[1]+Diameter),height+(int)(-2*Diameter-20-Diameter*(2 +x[1])+Diameter+Dm));
	      for(int k=2;k<N;k++){
		      g.drawLine(width/2+(int)(Diameter*x[k]),height+(int)(-2*Diameter-20-Diameter*(2*k+x[k])+Dm),width/2+(int)(Diameter*x[k]+Diameter),height+(int)(-2*Diameter-20-Diameter*(2*k+x[k])+Diameter+Dm));
		      g.drawLine(width/2+(int)(Diameter*x[k]+Diameter),height+(int)(-2*Diameter-20-Diameter*(2*k+x[k])+Diameter+Dm),width/2+(int)(Diameter*x[k-1]),height+(int)(-2*Diameter-20-Diameter*(2*k-2+x[k-1])+Dm));
		    }
	    }
	    if (Vis==0) {
		  if (IC==1) {
		  	if (alpha>0.5) {
            // draw the flat part
   		    g.setColor(Color.black);
		    g.drawLine((int)(shift*width/2+Diameter*t/4),height-(int)(2*Diameter+Diameter*t/4)-20,(int)(shift*width/2)-height,height-(int)(2*Diameter)-20-height-Delta2);
   		 // Draw the parabola
	  	       g.setColor(Color.red);
	  	       for(int k=1+(int)((1-alphaBis)*(1-alphaBis)*t);k<t/4;k++){
		         g.drawLine((int)(shift*width/2+Diameter*(t-2*Math.sqrt(t*(k-1))+k-1)),height+(int)(-2*Diameter-20-Diameter*(k-1)),(int)(shift*width/2+Diameter*(t-2*Math.sqrt(t*k)+k)),height+(int)(-2*Diameter-20-Diameter*k));
			    }
	           	 // Draw the shock region
       	    g.setColor(Color.cyan);
       	    if (alpha<1) {
		       g.drawLine((int)(shift*width/2+Diameter*t*alpha*alpha),height+(int)(-2*Diameter-20-Diameter*(1-alpha)*(1-alpha)*t),(int)(shift*width/2+Diameter*alpha*t),height+(int)(-2*Diameter-20));
       	    }        	  
	  	    }
		    else {
	        // draw the flat part
	   		g.setColor(Color.black);
			g.drawLine((int)(shift*width/2+Diameter*t*alpha/2),height+(int)(-2*Diameter-20-Diameter*(1-alpha)*t/2),(int)(shift*width/2)-height,height-(int)(2*Diameter)-20-height-Delta2);
		    g.setColor(Color.cyan);
		    g.drawLine((int)(shift*width/2+Diameter*t*alpha/2),height+(int)(-2*Diameter-20-Diameter*(1-alpha)*t/2),(int)(shift*width/2+Diameter*alpha*t),height+(int)(-2*Diameter-20));
//            g.drawLine(width/2+(int)(Diameter*((alpha-0.5))*t),height-(int)(2*Diameter)-20-Delta2,width/2+(int)(Diameter*(alpha*t)),height+(int)(-2*Diameter-20-Diameter*(alpha*t)));
		    }
		  }
		if (IC==0) {
// Draw the parabola
  	       g.setColor(Color.red);
  	       for(int k=1+(int)((1-alphaBis)*(1-alphaBis)*t);k<t;k++){
	         g.drawLine((int)(shift*width/2+Diameter*(t-2*Math.sqrt(t*(k-1))+k-1)),height+(int)(-2*Diameter-20-Diameter*(k-1)),(int)(shift*width/2+Diameter*(t-2*Math.sqrt(t*k)+k)),height+(int)(-2*Diameter-20-Diameter*k));
		    }
//Draw the shock region
  	    g.setColor(Color.cyan);
  	    if (alpha<1) {
	          g.drawLine((int)(shift*width/2+Diameter*alpha*alpha*t),height+(int)(-2*Diameter-20-Diameter*(1-alpha)*(1-alpha)*t),(int)(shift*width/2+Diameter*alpha*t),height+(int)(-2*Diameter-20));
  	    }
//
		}
		  if (IC==2) {
		  	Dm=Diameter*N/2.0;
		    g.setColor(Color.red);
		    g.drawLine(width/2-(int)(Dm),height+(int)(-2*Diameter-20-Delta2-Dm),width/2+Delta2,height-(int)(2*Diameter)-20);
		  }
		  g.setColor(Color.blue);
          if (IC==2) {Dm=Diameter*N/2.0;} else {Dm=0;}
          if (IC==2) {
            for(int k=N/2+1;k<N;k++){
	           g.drawLine(width/2+(int)(Diameter*(x[k-1]+k-1)-Dm),height+(int)(-Diameter*(k+1)-20+Dm),width/2+(int)(Diameter*(x[k]+k-1)+Diameter-Dm),height+(int)(-2*Diameter-20-Diameter*k+Diameter+Dm));
		       g.drawLine(width/2+(int)(Diameter*(x[k]+k)-Dm),height+(int)(-(2+k-1)*Diameter-20+Dm),width/2+(int)(Diameter*(x[k]+k-1)+Diameter-Dm),height+(int)(-2*Diameter-20-Diameter*k+Dm));
	        }
          }
          if (IC!=2) {
            g.drawLine((int)(shift*width/2+Diameter*x[1]+Diameter-Dm),height+(int)(-2*Diameter-20+Dm),(int)(shift*width/2+Diameter*x[1]+Diameter-Dm),height+(int)(-2*Diameter-20-Diameter+Dm));
            for(int k=2;k<N;k++){
 	           g.drawLine((int)(shift*width/2+Diameter*(x[k-1]+k-2)+Diameter-Dm),height+(int)(-2*Diameter-20-Diameter*(k-1)+Dm),(int)(shift*width/2+Diameter*(x[k]+k-1)+Diameter-Dm),height+(int)(-2*Diameter-20-Diameter*k+Diameter+Dm));
 		       g.drawLine((int)(shift*width/2+Diameter*(x[k]+k-1)+Diameter-Dm),height+(int)(-2*Diameter-20-Diameter*k+Diameter+Dm),(int)(shift*width/2+Diameter*(x[k]+k-1)+Diameter-Dm),height+(int)(-2*Diameter-20-Diameter*k+Dm));
 	        }         
          }
          
        }
	    g.setColor(Color.black);
	    g.drawLine(0,height-(int)(2*Diameter)-20,width,height-(int)(2*Diameter)-20);
	  }
	  } // end nested class Display1
	  
	   // Construct the frames
	 public void InitFrames() {
	   getContentPane().setLayout(null);
	   getContentPane().setBackground(Color.black); 
	   // Set the animation region
	   AnimationRegion = new JPanel();
	   AnimationRegion.setLayout(new GridLayout(1,0));      
	   AnimationRegion.setBackground(Color.lightGray);
	   TASEPDisplay = new Display1();
	   TASEPDisplay.setBorder( BorderFactory.createLineBorder(Color.gray,1));
	   AnimationRegion.add(TASEPDisplay);
	   AnimationRegion.setBounds(200,0,getSize().width-200,getSize().height);
	   getContentPane().add(AnimationRegion);
	   
	   // Set the control region
	   ControlRegion = new JPanel();
	   ControlRegion.setLayout(null);
	   ControlRegion.setBounds(0,0,200,getSize().height);
	   getContentPane().add(ControlRegion);  
	     
	   int NButton,HButton; // To organize the button positions, starts from 0
	   // Set the Start/Stop button, part of control region
	   StartStopButton = new JButton("Start");
	   NButton=0; HButton=1;
	   freeze=true;
	   StartStopButton.addActionListener(this);
	   StartStopButton.setBounds(5,5+NButton*40,190,35*HButton);
	   ControlRegion.add(StartStopButton);

	   ICMenu = new JComboBox<>();
	   NButton=1; HButton=1;
	   ICMenu.setBackground(Color.white);
	   ICMenu.addItem("Step IC");
	   ICMenu.addItem("Half Flat IC");
	   ICMenu.addItem("Flat IC");
	   ICMenu.addItemListener(this);
	   ICMenu.setBounds(5,5+NButton*40,190,35*HButton);
	   ControlRegion.add(ICMenu);
	   IC=0; // Starts with step IC    

	   VisMenu = new JComboBox<>();
	   NButton=2; HButton=1;
	   VisMenu.setBackground(Color.white);
	   VisMenu.addItem("Angle 45");
	   VisMenu.addItem("Angle 0");
	   VisMenu.addItemListener(this);
	   VisMenu.setBounds(5,5+NButton*40,190,35*HButton);
	   ControlRegion.add(VisMenu);
	   Vis=1; // Starts with 45 degrees    

	   JPanel PointsPanel = new JPanel();
	   PointsPanel.setLayout(new GridLayout(3,2));
	   NButton=3; HButton=3;
	   PointsPanel.setBounds(5,5+NButton*40,190,35*HButton);
	   ControlRegion.add(PointsPanel);
	   // Set the Labels and the TextFields
	   PointsLabel = new JLabel("Nb Particles");
	   PointsPanel.add(PointsLabel);
	   PointsText = new JTextField(""+N);
	   PointsPanel.add(PointsText);
	   
	   DiameterLabel = new JLabel("Particles Radius");
	   PointsPanel.add(DiameterLabel);
	   DiameterText = new JTextField(""+Diameter);
	   PointsPanel.add(DiameterText);
	   
	   AlphaLabel = new JLabel("Jump Rate 1");
	   PointsPanel.add(AlphaLabel);
	   AlphaText = new JTextField(""+alpha);
	   PointsPanel.add(AlphaText);

	   JPanel FixedPanel = new JPanel();
	   FixedPanel.setLayout(new BorderLayout());
	   NButton=6; HButton=1;
	   FixedPanel.setBounds(5,5+NButton*40,190,35*HButton);
	   ControlRegion.add(FixedPanel);

	   pLabel = new JLabel("Jump Rate 2,... set to 1");
	   FixedPanel.add(pLabel);
	   
	   // Set the SpeedPanel, composed of a Slider and a Label
	   JPanel SpeedPanel = new JPanel();
	   SpeedPanel.setLayout(new BorderLayout());
	   NButton=7; HButton=2;
	   SpeedPanel.setBounds(5,5+NButton*40,190,35*HButton);
	   ControlRegion.add(SpeedPanel);
	   // Set the Slider  
	   SpeedSlider = new JSlider(0,100,Speed);
	   SpeedSlider.addChangeListener(this);
	   SpeedSlider.setMajorTickSpacing(50);
	   SpeedSlider.setMinorTickSpacing(5);
	   SpeedSlider.setPaintTicks(true);
	   SpeedSlider.setPaintLabels(true);
	   SpeedPanel.add(SpeedSlider, BorderLayout.CENTER);
	   // Set the Label
	   SpeedLabel = new JLabel("Speed = "+SpeedSlider.getValue());
	   SpeedPanel.add(SpeedLabel, BorderLayout.WEST);
	   
//	 Inizialize set parameters
	   
	   NButton=9; HButton=1;
	   SetParameters = new JButton("Set the parameters");
	   SetParameters.addActionListener(this);
	   SetParameters.setBounds(5,5+NButton*40,190,35*HButton);
	   ControlRegion.add(SetParameters);  
	   
	   NButton=10; HButton=1;
	   Reset = new JButton("Reset");
	   Reset.addActionListener(this);
	   Reset.setBounds(5,5+NButton*40,190,35*HButton);
	   ControlRegion.add(Reset);  

	 } // End InitFrames

//	 Answer to button pressed
	   public void actionPerformed(ActionEvent evt) {

	      String command = evt.getActionCommand();

		   switch (command) {
			   case "Start":
				   freeze = false;
				   StartStopButton.setText("Stop");
				   break;
			   case "Stop":
				   freeze = true;
				   StartStopButton.setText("Start");
				   break;
			   case "Set the parameters":
				   StartStopButton.setText("Start");
				   ridisegna();
				   break;
			   case "Reset":
				   freeze = true;
				   StartStopButton.setText("Start");
				   Inizializza();
				   break;
		   }
	      try {
	        String xStr = PointsText.getText();
	        if ((int)(Double.parseDouble(xStr))!=N)
	          {N=(int)(Double.parseDouble(xStr));         
	          Inizializza();
	          } 
	     }
	     catch (NumberFormatException e) {
	        PointsText.setText(""+N);
	     }
	     try {
	        String xStr = AlphaText.getText();
	        alpha = Double.parseDouble(xStr);
	     }
	     catch (NumberFormatException e) {
	        AlphaText.setText(""+alpha);
	     }
	     try {
	        String xStr = DiameterText.getText();
	        Diameter = Double.parseDouble(xStr);
	     }
	     catch (NumberFormatException e) {
	        DiameterText.setText(""+Diameter);
	     }
	          
	   }  // end actionPerformed
	   
//	 Answer to Slider modified
	   public void stateChanged(ChangeEvent evt) {
	      if (evt.getSource() == SpeedSlider) {
	      if (SpeedSlider.getValue()<1) {SpeedSlider.setValue(1);}
	      Speed=SpeedSlider.getValue();
	      SpeedLabel.setText("Speed = "+Speed);}
	      delay=1000/Speed;
	   } // End stateChanged

	   public void itemStateChanged(ItemEvent evt) {
	    String ICName = (String)ICMenu.getSelectedItem();
	    String VisName = (String)VisMenu.getSelectedItem();
	    
	    if (Objects.equals(ICName, "Step IC"))
	    {
	      if (IC!=0) {
	        IC=0;
	        freeze=true;
	        StartStopButton.setText("Start");
	        Inizializza();
	      }
	    }
	    else if (Objects.equals(ICName, "Half Flat IC"))
	    {
	      if (IC!=1) {
	        IC=1;
	        freeze=true;
	        StartStopButton.setText("Start");
	        Inizializza();        
	      }
	    }
	    else if (Objects.equals(ICName, "Flat IC"))
	    {
	      if (IC!=2) {
	        IC=2;
	        freeze=true;
	        StartStopButton.setText("Start");
	        Inizializza();        
	      }
	    }
	    
	    if (Objects.equals(VisName, "Angle 0"))
	    { 
          if (Vis!=0) {Vis=0; repaint();}
	    }
	    else if (Objects.equals(VisName, "Angle 45"))
	    {
	      if (Vis!=1) {Vis=1; repaint();}
	    }
	    
	 }  // End itemStateChanged

}