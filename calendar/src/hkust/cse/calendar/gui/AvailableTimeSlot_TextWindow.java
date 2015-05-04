package hkust.cse.calendar.gui;


import hkust.cse.calendar.apptstorage.ApptController;
import hkust.cse.calendar.time.TimeController;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserController;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableModel;

public class AvailableTimeSlot_TextWindow extends JFrame{
	private static final long serialVersionUID = 1L;
    private CalGrid parent;
	private JScrollPane scrollPaneView;
	private Container contentPane;

	
    public AvailableTimeSlot_TextWindow(ArrayList<User> userList, ArrayList<Date> dateList) {
    	
    	for(int i=0; i<dateList.size();i++)
    	{
    		System.out.println(dateList.get(i).toString());
    	}
    	
		JPanel mainPanel = new JPanel();

		this.setAlwaysOnTop(true);

		//contentPane = getContentPane();
		//contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
    	setTitle("View Available TimeSlot");
    	//this.getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
    	setSize(500, 300);
        // create JTextField
    	

    	ArrayList<TimeSpan> slotList = new ArrayList<TimeSpan>();
    	
    	for(int i =0; i<dateList.size(); i++) {
    		
    		Date tempDate = dateList.get(i);
    		JLabel dateLabel= new JLabel("");
			dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    		dateLabel.setText( TimeController.getInstance().getYearFrom(tempDate)+"-"+TimeController.getInstance().getMonthFrom(tempDate)+"-"+TimeController.getInstance().getDateFrom(tempDate));
			
    		mainPanel.add(dateLabel);
			slotList = ApptController.getInstance().getSchedulableTimeSpan(userList, dateList.get(i));
    		

			for(int j=0; j<slotList.size();j++){
				JLabel timeslot = new JLabel("");
				timeslot.setAlignmentX(Component.CENTER_ALIGNMENT);
				timeslot.setText("("+slotList.get(j).OnlyTimetoString()+")");

				mainPanel.add(timeslot);
    		}
			JLabel bufferLabel1 = new JLabel("\n");
    		mainPanel.add(bufferLabel1);
    	}
    	
    	scrollPaneView = new JScrollPane(mainPanel);
		scrollPaneView.setBorder(new BevelBorder(BevelBorder.RAISED));
		add(scrollPaneView);
    }

}